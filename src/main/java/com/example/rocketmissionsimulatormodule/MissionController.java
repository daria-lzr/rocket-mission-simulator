package com.example.rocketmissionsimulatormodule;

import com.example.rocketmissionsimulatormodule.view.SimulationPane;
import com.example.rocketmissionsimulatormodule.view.TelemetryPane;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class MissionController {

    private static final double TIME_STEP = 0.05; //how often the simulation updates (every 0.05 seconds)

    private final Mission mission;
    private final Timeline simulationTimeline;
    private final TelemetryPane telemetryPane;
    private final SimulationPane simulationPane;

    public MissionController(Mission mission, TelemetryPane telemetryPane, SimulationPane simulationPane) {
        this.mission = mission;
        this.telemetryPane = telemetryPane;
        this.simulationPane = simulationPane;



        /**
         * Timeline - JavaFX animation utility that executes an action at scheduled intervals.
         * KeyFrame - Defines what happens at a particular time in the animation. Every 0.05 seconds, KeyFrame calls updateSimulation()
         * Timeline.INDEFINITE - Makes the timeline repeat until we explicitly stop it.
         */
        simulationTimeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(TIME_STEP),
                        event -> updateSimulation()
                )
        );

        simulationTimeline.setCycleCount(Timeline.INDEFINITE);
    }



    private void updateSimulation() {
        if (mission.getCurrentState() == Mission.MissionState.ASCENT) {
            mission.getRocket().updateFlight(TIME_STEP);

            double altitude = mission.getRocket().getAltitude();
            double velocity = mission.getRocket().getVelocity();

            telemetryPane.updateTelemetry(altitude, velocity);
            simulationPane.updateRocketPosition(altitude);

        }
    }




    public void startSimulation() {
        if (mission.getCurrentState() == Mission.MissionState.ASCENT) {
            simulationTimeline.play();
        }
    }

    public void stopSimulation() {
        simulationTimeline.stop();
    }
}