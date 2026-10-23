package com.example.rocketmissionsimulatormodule.util;

import com.example.rocketmissionsimulatormodule.view.MissionLogPane;
import com.example.rocketmissionsimulatormodule.view.MissionPane;
import com.example.rocketmissionsimulatormodule.view.SimulationPane;
import com.example.rocketmissionsimulatormodule.view.TelemetryPane;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class MissionController {

    private static final double TIME_STEP = 0.05; //how often the simulation updates (every 0.05 seconds)

    private final Mission mission;
    private final TelemetryPane telemetryPane;
    private final SimulationPane simulationPane;
    private final MissionPane missionPane;
    private final MissionLogPane missionLogPane;

    private final ScheduledExecutorService executor =
            Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> simulationTask;
    private boolean ignitionStarted;

    private static final double OVERHEATING_ALTITUDE = 400.0;
    private static final double MAX_Q_EVENT_ALTITUDE = 8_000.0;
    private static final double PROPELLANT_INSTABILITY_ALTITUDE = 50_000.0;
    private static final double ATTITUDE_CONTROL_ALTITUDE = 220_000.0;
    private MissionEvent activeEvent;
    private boolean overheatingTriggered = false;
    private boolean aerodynamicStressTriggered = false;
    private boolean propellantInstabilityTriggered = false;
    private boolean attitudeControlTriggered = false;
    private boolean propellantExhaustionLogged = false;
    private boolean engineCutoffLogged = false;
    private volatile boolean eventPending = false;

    private static final long EVENT_RESPONSE_TIMEOUT_NANOS = TimeUnit.SECONDS.toNanos(20);
    private long eventDeadlineNanos;


    public MissionController(Mission mission, TelemetryPane telemetryPane, SimulationPane simulationPane, MissionPane missionPane, MissionLogPane missionLogPane) {
        this.mission = mission;
        this.telemetryPane = telemetryPane;
        this.simulationPane = simulationPane;
        this.missionPane = missionPane;
        this.missionLogPane = missionLogPane;
    }




    private void updateSimulation() {
        if (mission.getCurrentState() == Mission.MissionState.ASCENT) {
            mission.getRocket().updateFlight(TIME_STEP);

            double altitude = mission.getRocket().getAltitude();
            double velocity = mission.getRocket().getVelocity();
            double fuelFraction = mission.getRocket().getFuelFraction();
            double throttle = mission.getRocket().getEffectiveThrottle();
            if (!propellantExhaustionLogged && mission.getRocket().isOutOfFuel()) {
                propellantExhaustionLogged = true;
                Platform.runLater(() -> missionLogPane.addEntry(
                        "ENGINE CUTOFF: Propellant exhausted. Coasting toward target altitude."));
            }

            if (!engineCutoffLogged && mission.getRocket().hasEngineCutoff()) {
                engineCutoffLogged = true;
                Platform.runLater(() -> missionLogPane.addEntry(
                        "ENGINE CUTOFF: Coasting toward the target apogee."));
            }

            // do not fail merely because the engines have shut down
            // check whether the highest reached altitude was sufficient
            boolean missedTarget = (mission.getRocket().hasCrashed()
                    || (mission.getRocket().isCoasting() && velocity <= 0.0))
                    && altitude < Mission.TARGET_ALTITUDE;

            checkMissionEvents(altitude);

            boolean orbitReached = false;

            if (!eventPending) {
                orbitReached = mission.checkOrbitReached();
            }

            if (orbitReached || missedTarget) {
                stopSimulation();
            }

            /** use this cause JavaFX is not designed to allow multiple threads to modify its GUI simultaneously
             * it submits the update to the JavaFX Application Thread
             * because JavaFX modifies the GUI solely though its designated thread
             *
             */
            final boolean reachedOrbit = orbitReached;
            final boolean failedAscent = missedTarget && !orbitReached;
            Platform.runLater(() -> {
                if (failedAscent) {
                    failMission("Rocket could not reach the 300 km target altitude.");
                    return;
                }
                if (reachedOrbit) {
                    missionLogPane.setAscending(false);
                    missionLogPane.addEntry("APOGEE: Target altitude reached (300 km). Vertical ascent complete.");
                    missionLogPane.addEntry("INSTRUCTION: Deploy the satellite to complete the mission.");
                }
                telemetryPane.updateTelemetry(altitude, velocity, fuelFraction, throttle);
                simulationPane.updateRocketPosition(altitude);
                simulationPane.updateEngineExhaust(throttle, altitude);
                missionPane.updateMissionStatus();

                checkEventTimeout();
            });
        }
    }




    public void startSimulation() {
        if (mission.getCurrentState() != Mission.MissionState.ASCENT
                || ignitionStarted || (simulationTask != null && !simulationTask.isDone())) {
            return;
        }

        ignitionStarted = true;
        missionLogPane.addEntry("IGNITION: Raptor-inspired engine startup initiated.");
        // only start the physics clock once the three-second visual ignition finishes
        // the callback executes on the JavaFX application thread
        simulationPane.playIgnition(() -> {
            if (mission.getCurrentState() != Mission.MissionState.ASCENT) {
                simulationPane.stopEngines();
                return;
            }
            missionLogPane.setAscending(true);
            missionLogPane.addEntry("LIFTOFF: Rocket launch successful.");
            missionLogPane.addEntry("STATUS: Automatic ascent guidance active. Target: 300 km.");
            missionLogPane.addEntry("INSTRUCTION: Monitor telemetry for flight updates.");
            simulationTask = executor.scheduleAtFixedRate(
                    this::updateSimulation, 0, 50, TimeUnit.MILLISECONDS);
        });
    }


    public void stopSimulation() {
        if (simulationTask != null) {
            simulationTask.cancel(false);
        }
    }

    public void shutdown() {
        stopSimulation();
        simulationPane.dispose();
        missionLogPane.dispose();
        executor.shutdownNow();
    }




    public void deploySatellite() {
        if (mission.getCurrentState() == Mission.MissionState.ORBIT) {
            mission.deploySatellite();
            missionLogPane.addEntry("DEPLOYMENT: Satellite deployment initiated.");
            missionPane.updateMissionStatus();

            simulationPane.showSatellite(() -> {
                mission.completeMission();
                missionLogPane.addEntry("SUCCESS: Satellite deployed. Mission complete!");
                missionPane.updateMissionStatus();
                simulationPane.showSuccessAnimation();
            });
        }
    }



    public void failMission() {
        failMission("Critical engine failure. Mission terminated.");
    }

    private void failMission(String reason) {
        //check if mission already ended
        if (mission.getCurrentState() != Mission.MissionState.SUCCESS && mission.getCurrentState() != Mission.MissionState.FAILURE) {
            mission.failMission();
            missionLogPane.setAscending(false);
            missionLogPane.hideEventPrompt();
            missionLogPane.addEntry("FAILURE: " + reason);
            stopSimulation();
            simulationPane.stopEngines();
            missionPane.updateMissionStatus();
            simulationPane.showFailureAnimation();
        }
    }


    private void checkMissionEvents(double altitude) {
        if (eventPending || mission.getCurrentState() != Mission.MissionState.ASCENT) {
            return;
        }

        MissionEvent nextEvent;
        if (!overheatingTriggered && altitude >= OVERHEATING_ALTITUDE) {
            overheatingTriggered = true;
            nextEvent = MissionEvent.ENGINE_OVERHEATING;
        } else if (!aerodynamicStressTriggered && altitude >= MAX_Q_EVENT_ALTITUDE) {
            aerodynamicStressTriggered = true;
            nextEvent = MissionEvent.AERODYNAMIC_STRESS;
        } else if (!propellantInstabilityTriggered
                && altitude >= PROPELLANT_INSTABILITY_ALTITUDE) {
            propellantInstabilityTriggered = true;
            nextEvent = MissionEvent.PROPELLANT_INSTABILITY;
        } else if (!attitudeControlTriggered
                && altitude >= ATTITUDE_CONTROL_ALTITUDE) {
            attitudeControlTriggered = true;
            nextEvent = MissionEvent.ATTITUDE_CONTROL_FAILURE;
        } else {
            return;
        }

        // set this on simulation thread to prevent duplicate event scheduling
        eventPending = true;
        Platform.runLater(() -> {
            if (mission.getCurrentState() != Mission.MissionState.ASCENT) {
                eventPending = false;
                return;
            }
            activeEvent = nextEvent;
            eventDeadlineNanos = System.nanoTime() + EVENT_RESPONSE_TIMEOUT_NANOS;
            missionPane.displayMissionEvent(activeEvent);

            if (nextEvent == MissionEvent.AERODYNAMIC_STRESS) {
                missionLogPane.addEntry(String.format(java.util.Locale.US,
                        "DIAGNOSTIC: Dynamic pressure %.1f kPa (recommended limit: 25 kPa).",
                        mission.getRocket().getDynamicPressure() / 1_000.0));
            }
            missionLogPane.showEventPrompt(
                    activeEvent, eventDeadlineNanos, EVENT_RESPONSE_TIMEOUT_NANOS);
        });
    }

    private void checkEventTimeout() {
        if (activeEvent != null && System.nanoTime() - eventDeadlineNanos >= 0) {
            handleEventResponse(-1);
        }
    }

    public void handleEventResponse(int selectedIndex) {
        if (activeEvent == null) {
            return;
        }

        // even a click that arrives just after the deadline counts as a timeout
        boolean timedOut = System.nanoTime() - eventDeadlineNanos >= 0;
        MissionEvent resolvedEvent = activeEvent;
        activeEvent = null;
        missionLogPane.hideEventPrompt();
        missionPane.clearMissionEvent();

        if (resolvedEvent == MissionEvent.ENGINE_OVERHEATING) {
            handleOverheatingResponse(timedOut ? -1 : selectedIndex);
        } else if (resolvedEvent == MissionEvent.AERODYNAMIC_STRESS) {
            handleAerodynamicStressResponse(timedOut ? -1 : selectedIndex);
        } else if (resolvedEvent == MissionEvent.PROPELLANT_INSTABILITY) {
            handlePropellantInstabilityResponse(timedOut ? -1 : selectedIndex);
        } else if (resolvedEvent == MissionEvent.ATTITUDE_CONTROL_FAILURE) {
            handleAttitudeControlResponse(timedOut ? -1 : selectedIndex);
        }
    }

    private void handleOverheatingResponse(int selectedIndex) {
        switch (selectedIndex) {
            case 0 -> {
                missionLogPane.addEntry("RECOVERY: Throttle reduced to cool the engine.");
                reduceEngineThrottle();
                simulationPane.showRecoveryNotification(false);
                eventPending = false;
            }
            case 1 -> {
                missionLogPane.addEntry("RECOVERY: Emergency cooling activated. Ascent continuing.");
                simulationPane.showRecoveryNotification(true);
                eventPending = false;
            }
            default -> {
                String explanation = selectedIndex == -1
                        ? "TIMEOUT: Engine overheating response window expired."
                        : "INCORRECT RESPONSE: Ignoring an overheating engine risks severe damage.";
                missionLogPane.addImmediateEntry(explanation);
                scheduleEngineFailure();
            }
        }
    }

    private void handleAerodynamicStressResponse(int selectedIndex) {
        if (selectedIndex == 1) {
            missionLogPane.addEntry("RECOVERY: Throttle reduced to limit aerodynamic stress.");
            reduceEngineThrottle();
            simulationPane.showMaxQRecoveryNotification();
            eventPending = false;
            return;
        }

        String explanation = switch (selectedIndex) {
            case 0 -> "INCORRECT RESPONSE: Increasing thrust worsens aerodynamic loading.";
            case 2 -> "INCORRECT RESPONSE: Attitude thrusters cannot relieve aerodynamic pressure.";
            default -> "TIMEOUT: No corrective response to dangerous aerodynamic loading.";
        };
        missionLogPane.addImmediateEntry(explanation);
        scheduleAerodynamicFailure();
    }

    /** Only activating the dedicated damping controller stabilizes the tank oscillations. */
    private void handlePropellantInstabilityResponse(int selectedIndex) {
        if (selectedIndex == 2) {
            missionLogPane.addEntry(
                    "RECOVERY: Propellant slosh-damping controller activated. Vehicle stable.");
            simulationPane.showPropellantRecoveryNotification();
            eventPending = false;
            return;
        }

        String explanation = switch (selectedIndex) {
            case 0 -> "INCORRECT RESPONSE: Tank pressure was nominal; extra "
                    + "pressurization does not stop the propellant oscillations.";
            case 1 -> "INCORRECT RESPONSE: Aggressive attitude corrections "
                    + "can amplify propellant sloshing and destabilize the vehicle.";
            default -> "TIMEOUT: Propellant oscillations were not corrected in time.";
        };
        missionLogPane.addImmediateEntry(explanation);
        schedulePropellantInstabilityFailure();
    }


    private void handleAttitudeControlResponse(int selectedIndex) {
        if (selectedIndex == 2) {
            missionLogPane.addEntry(
                    "RECOVERY: Primary RCS isolated; backup thrusters restored stable attitude.");
            simulationPane.showAttitudeRecoveryNotification();
            eventPending = false;
            return;
        }

        String explanation = switch (selectedIndex) {
            case 0 -> "INCORRECT RESPONSE: Guidance is functioning. A computer restart "
                    + "cannot restore pressure in the primary RCS.";
            case 1 -> "INCORRECT RESPONSE: Main-engine ignition cannot safely replace "
                    + "the failed reaction-control thrusters during coast.";
            default -> "TIMEOUT: Uncorrected attitude drift prevents safe satellite deployment.";
        };
        missionLogPane.addImmediateEntry(explanation);
        scheduleAttitudeControlFailure();
    }

    private void scheduleAttitudeControlFailure() {
        // show diagnosis before transitioning to mission failure
        PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
        delay.setOnFinished(event -> {
            if (mission.getCurrentState() == Mission.MissionState.ASCENT) {
                failMission("Attitude control lost; satellite deployment is unsafe.");
            }
        });
        delay.play();
    }

    private void schedulePropellantInstabilityFailure() {
        // give user time to read why the response was unsafe
        PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
        delay.setOnFinished(event -> {
            if (mission.getCurrentState() == Mission.MissionState.ASCENT) {
                failMission("Loss of vehicle stability due to propellant sloshing.");
            }
        });
        delay.play();
    }

    private void scheduleEngineFailure() {
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            if (mission.getCurrentState() == Mission.MissionState.ASCENT) {
                failMission("Critical engine failure following overheating.");
            }
        });
        delay.play();
    }

    private void scheduleAerodynamicFailure() {
        PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
        delay.setOnFinished(event -> {
            if (mission.getCurrentState() == Mission.MissionState.ASCENT) {
                failMission("Vehicle structural failure due to excessive aerodynamic stress.");
            }
        });
        delay.play();
    }


    private void reduceEngineThrottle() {
        mission.getRocket().reduceAcceleration();

        System.out.println("Engine throttle reduced!");

        PauseTransition recoveryDelay = new PauseTransition(Duration.seconds(10));

        recoveryDelay.setOnFinished(event -> {
            if (mission.getCurrentState() == Mission.MissionState.ASCENT) {
                mission.getRocket().restoreAcceleration();

                System.out.println("Engine throttle restored!");
                missionLogPane.addEntry("STATUS: Engine throttle restored.");
            }
        });

        recoveryDelay.play();
    }




}