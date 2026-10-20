package com.example.rocketmissionsimulatormodule.util;

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

    private final ScheduledExecutorService executor =
            Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> simulationTask;

    private static final double OVERHEATING_ALTITUDE = 400.0;
    private MissionEvent activeEvent;
    private boolean overheatingTriggered = false;
    private volatile boolean eventPending = false;

    private static final long EVENT_RESPONSE_TIMEOUT_NANOS = TimeUnit.SECONDS.toNanos(10);
    private long eventDeadlineNanos;


    public MissionController(Mission mission, TelemetryPane telemetryPane, SimulationPane simulationPane, MissionPane missionPane) {
        this.mission = mission;
        this.telemetryPane = telemetryPane;
        this.simulationPane = simulationPane;
        this.missionPane = missionPane;
    }




    private void updateSimulation() {
        if (mission.getCurrentState() == Mission.MissionState.ASCENT) {
            mission.getRocket().updateFlight(TIME_STEP);

            double altitude = mission.getRocket().getAltitude();
            double velocity = mission.getRocket().getVelocity();

            checkMissionEvents(altitude);

            boolean orbitReached = false;

            if (!eventPending) {
                orbitReached = mission.checkOrbitReached();
            }

            if (orbitReached) {
                stopSimulation();
            }

            /** use this cause JavaFX is not designed to allow multiple threads to modify its GUI simultaneously
             * it submits the update to the JavaFX Application Thread
             * because JavaFX modifies the GUI solely though its designated thread
             *
             */
            Platform.runLater(() -> {
                telemetryPane.updateTelemetry(altitude, velocity);
                simulationPane.updateRocketPosition(altitude);
                missionPane.updateMissionStatus();

                checkEventTimeout();
            });
        }
    }




    public void startSimulation() {
        if (mission.getCurrentState() == Mission.MissionState.ASCENT
                && (simulationTask == null || simulationTask.isDone())) {

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
            missionPane.updateMissionStatus();

            simulationPane.showSatellite(() -> {
                mission.completeMission();
                missionPane.updateMissionStatus();
            });
        }
    }



    public void failMission() {
        //check is mission alr ended
        if (mission.getCurrentState() != Mission.MissionState.SUCCESS && mission.getCurrentState() != Mission.MissionState.FAILURE) {
            mission.failMission();
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

        System.out.println("Event: " + activeEvent);
        System.out.println("Selected response index: " + selectedIndex);

        if (selectedIndex == 0) {
            reduceEngineThrottle();
            simulationPane.showRecoveryNotification(false);
        } else if (selectedIndex == 1) {
            simulationPane.showRecoveryNotification(true);
        } else if (selectedIndex == 2) {
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
            }
        });

        recoveryDelay.play();
    }




}