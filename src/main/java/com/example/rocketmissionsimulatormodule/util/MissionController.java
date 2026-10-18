package com.example.rocketmissionsimulatormodule.util;

import com.example.rocketmissionsimulatormodule.view.SimulationPane;
import com.example.rocketmissionsimulatormodule.view.TelemetryPane;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;

public class MissionController {

    private static final double TIME_STEP = 0.05; //how often the simulation updates (every 0.05 seconds)

    private final Mission mission;
    private final TelemetryPane telemetryPane;
    private final SimulationPane simulationPane;

    private final ScheduledExecutorService executor =
            Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> simulationTask;

    public MissionController(Mission mission, TelemetryPane telemetryPane, SimulationPane simulationPane) {
        this.mission = mission;
        this.telemetryPane = telemetryPane;
        this.simulationPane = simulationPane;




    }



    private void updateSimulation() {
        if (mission.getCurrentState() == Mission.MissionState.ASCENT) {
            mission.getRocket().updateFlight(TIME_STEP);

            double altitude = mission.getRocket().getAltitude();
            double velocity = mission.getRocket().getVelocity();

            /** use this cause JavaFX is not designed to allow multiple threads to modify its GUI simultaneously
             * it submits the update to the JavaFX Application Thread
             * bacause JavaFX modifies the GUI solely though its designated thread
             *
             */
            Platform.runLater(() -> {
                if (mission.getCurrentState() == Mission.MissionState.ASCENT) {
                    telemetryPane.updateTelemetry(altitude, velocity);
                    simulationPane.updateRocketPosition(altitude);
                }
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
}