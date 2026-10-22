package com.example.rocketmissionsimulatormodule;

import com.example.rocketmissionsimulatormodule.util.Mission;
import com.example.rocketmissionsimulatormodule.util.MissionController;
import com.example.rocketmissionsimulatormodule.view.FlightControlsPane;
import com.example.rocketmissionsimulatormodule.view.MissionLogPane;
import com.example.rocketmissionsimulatormodule.view.MissionPane;
import com.example.rocketmissionsimulatormodule.view.SimulationPane;
import com.example.rocketmissionsimulatormodule.view.TelemetryPane;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RocketMissionSimulatorApplication extends Application
{
    @Override
    public void start(Stage stage)
    {
        Mission mission = new Mission();
        MissionPane missionPane = new MissionPane(mission);
        TelemetryPane telemetryPane = new TelemetryPane();
        SimulationPane simulationPane = new SimulationPane();
        FlightControlsPane flightControlsPane = new FlightControlsPane();
        MissionLogPane missionLogPane = new MissionLogPane();

        // The controller needs the mission log because it records events and status changes.
        MissionController missionController = new MissionController(
                mission,
                telemetryPane,
                simulationPane,
                missionPane,
                missionLogPane
        );

        // Connect the slider to the rocket through the controller.
        flightControlsPane.setOnThrottleChanged(missionController::setThrottle);

        missionPane.setOnLaunch(missionController::startSimulation);
        missionPane.setOnDeploy(missionController::deploySatellite);
        missionPane.setOnEventResponse(missionController::handleEventResponse);

        BorderPane mainPane = new BorderPane();
        VBox rightVBox = createRightSidebar(
                missionPane,
                telemetryPane,
                flightControlsPane
        );

        mainPane.setCenter(simulationPane);
        mainPane.setRight(rightVBox);
        mainPane.setBottom(missionLogPane);

        Scene scene = new Scene(mainPane, 1200, 750);
        stage.setTitle("Rocket Mission Simulator");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(event -> missionController.shutdown());
    }

    private static VBox createRightSidebar(
            MissionPane missionPane,
            TelemetryPane telemetryPane,
            FlightControlsPane flightControlsPane)
    {
        VBox rightVBox = new VBox();
        rightVBox.setSpacing(10);
        rightVBox.setPrefWidth(280);

        rightVBox.getChildren().addAll(
                telemetryPane,
                flightControlsPane,
                missionPane
        );

        return rightVBox;
    }
}
