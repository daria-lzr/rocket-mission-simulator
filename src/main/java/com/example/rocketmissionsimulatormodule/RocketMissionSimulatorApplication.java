package com.example.rocketmissionsimulatormodule;

import com.example.rocketmissionsimulatormodule.util.Mission;
import com.example.rocketmissionsimulatormodule.util.MissionController;
import com.example.rocketmissionsimulatormodule.view.MissionLogPane;
import com.example.rocketmissionsimulatormodule.view.MissionPane;
import com.example.rocketmissionsimulatormodule.view.SimulationPane;
import com.example.rocketmissionsimulatormodule.view.TelemetryPane;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RocketMissionSimulatorApplication extends Application {



    @Override
    public void start(Stage stage) {
        Mission mission = new Mission();
        MissionPane missionPane = new MissionPane(mission);
        TelemetryPane telemetryPane = new TelemetryPane();
        SimulationPane simulationPane = new SimulationPane();

        MissionLogPane missionLogPane = new MissionLogPane();
        MissionController missionController = new MissionController(
                mission, telemetryPane, simulationPane, missionPane, missionLogPane);

        missionPane.setOnLaunch(() -> {
            missionController.startSimulation();
        });


        missionPane.setOnDeploy(() -> {
            missionController.deploySatellite();
        });


        missionLogPane.setOnEventResponse(missionController::handleEventResponse);



        BorderPane mainPane = new BorderPane();



        ScrollPane rightSidebar = createRightSidebar(missionPane, telemetryPane);
        //assemble  main layout
        mainPane.setCenter(simulationPane);
        mainPane.setRight(rightSidebar);
        mainPane.setBottom(missionLogPane);

        Scene scene = new Scene(mainPane, 1400, 900);
        stage.setTitle("Rocket Mission Simulator");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> missionController.shutdown());
    }



    private static ScrollPane createRightSidebar(MissionPane missionPane,
                                                 TelemetryPane telemetryPane) {
        VBox rightVBox = new VBox(telemetryPane, missionPane);
        rightVBox.setSpacing(10);
        rightVBox.setPrefWidth(280);
        rightVBox.setStyle("-fx-background-color: #050807;");

        ScrollPane sidebar = new ScrollPane(rightVBox);
        sidebar.setFitToWidth(true);
        sidebar.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sidebar.setPrefWidth(290);
        sidebar.setStyle("-fx-background: #050807; -fx-background-color: #050807;");
        return sidebar;
    }

}
