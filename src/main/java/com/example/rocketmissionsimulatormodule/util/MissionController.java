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

    private static final double OVERHEATING_ALTITUDE = 400.0;
    private MissionEvent activeEvent;
    private boolean overheatingTriggered = false;
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

            // Do not fail merely because the engines have shut down.
            // Check whether the highest reached altitude was sufficient.
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
                missionPane.updateMissionStatus();

                checkEventTimeout();
            });
        }
    }




    public void startSimulation() {
        if (mission.getCurrentState() == Mission.MissionState.ASCENT
                && (simulationTask == null || simulationTask.isDone())) {

            missionLogPane.setAscending(true);
            missionLogPane.addEntry("LIFTOFF: Rocket launch successful.");
            missionLogPane.addEntry("STATUS: Automatic ascent guidance active. Target: 300 km.");
            missionLogPane.addEntry("INSTRUCTION: Monitor telemetry for flight updates.");

            simulationTask = executor.scheduleAtFixedRate(
                    () -> updateSimulation(),
                    0,
                    50,
                    TimeUnit.MILLISECONDS
            );
        }
    }


    public void stopSimulation() {
        if (simulationTask != null) {
            simulationTask.cancel(false);
        }
    }

    public void shutdown() {
        stopSimulation();
        executor.shutdown();
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
        //check is mission alr ended
        if (mission.getCurrentState() != Mission.MissionState.SUCCESS && mission.getCurrentState() != Mission.MissionState.FAILURE) {
            mission.failMission();
            missionLogPane.setAscending(false);
            missionLogPane.hideEventPrompt();
            missionLogPane.addEntry("FAILURE: " + reason);
            stopSimulation();
            missionPane.updateMissionStatus();
            simulationPane.showFailureAnimation();
        }
    }

    private void checkMissionEvents(double altitude) {
        if (!overheatingTriggered
                && altitude >= OVERHEATING_ALTITUDE
                && mission.getCurrentState() == Mission.MissionState.ASCENT) {

            overheatingTriggered = true;
            eventPending = true;

            Platform.runLater(() -> {
                if (mission.getCurrentState() == Mission.MissionState.ASCENT) {
                    activeEvent = MissionEvent.ENGINE_OVERHEATING;
                    eventDeadlineNanos = System.nanoTime() + EVENT_RESPONSE_TIMEOUT_NANOS;
                    missionPane.displayMissionEvent(activeEvent);
                    missionLogPane.showEventPrompt(
                            activeEvent, eventDeadlineNanos, EVENT_RESPONSE_TIMEOUT_NANOS);
                }
            });
        }
    }

    private void checkEventTimeout() {
        if (activeEvent == null) {
            return;
        }

        if (System.nanoTime() - eventDeadlineNanos >= 0) {
            System.out.println("Event response timed out!");
            missionLogPane.addEntry("WARNING: Response timed out. Engine failure imminent.");

            handleEventResponse(2);
        }
    }


    public void handleEventResponse(int selectedIndex) {
        if (activeEvent == null) {
            return;
        }

        if (System.nanoTime() - eventDeadlineNanos >= 0) {
            selectedIndex = 2;
        }

        missionLogPane.hideEventPrompt();

        System.out.println("Event: " + activeEvent);
        System.out.println("Selected response index: " + selectedIndex);

        if (selectedIndex == 0) {
            missionLogPane.addEntry("RECOVERY: Throttle reduced to cool the engine.");
            reduceEngineThrottle();
            simulationPane.showRecoveryNotification(false);
        } else if (selectedIndex == 1) {
            missionLogPane.addEntry("RECOVERY: Emergency cooling activated. Ascent continuing.");
            simulationPane.showRecoveryNotification(true);
        } else if (selectedIndex == 2) {
            if (System.nanoTime() - eventDeadlineNanos < 0) {
                missionLogPane.addEntry("WARNING: Overheating warning ignored. Engine failure imminent.");
            }
            scheduleEngineFailure();
        }

        activeEvent = null;
        missionPane.clearMissionEvent();

        if (selectedIndex != 2) {
            eventPending = false;
        }
    }


    private void scheduleEngineFailure() {
        PauseTransition failureDelay = new PauseTransition(Duration.seconds(3));

        failureDelay.setOnFinished(event -> {
            if (mission.getCurrentState() == Mission.MissionState.ASCENT) {
                System.out.println("Critical engine failure!");
                failMission();
            }
        });

        failureDelay.play();
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