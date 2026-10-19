package com.example.rocketmissionsimulatormodule;

import com.example.rocketmissionsimulatormodule.util.Mission;
import com.example.rocketmissionsimulatormodule.util.MissionController;
import com.example.rocketmissionsimulatormodule.view.MissionLogPane;
import com.example.rocketmissionsimulatormodule.view.MissionPane;
import com.example.rocketmissionsimulatormodule.view.SimulationPane;
import com.example.rocketmissionsimulatormodule.view.TelemetryPane;

import javafx.application.Application;
import javafx.scene.Scene;
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

        MissionController missionController = new MissionController(mission, telemetryPane, simulationPane, missionPane);

        missionPane.setOnLaunch(() -> {
            missionController.startSimulation();
        });


        missionPane.setOnDeploy(() -> {
            missionController.deploySatellite();
        });




        BorderPane mainPane = new BorderPane();



        VBox rightVBox = createRightSidebar(missionPane, telemetryPane);
        MissionLogPane missionLogPane = new MissionLogPane();

        //assemble  main layout
        mainPane.setCenter(simulationPane);
        mainPane.setRight(rightVBox);
        mainPane.setBottom(missionLogPane);

        Scene scene = new Scene(mainPane, 1200, 750);
        stage.setTitle("Rocket Mission Simulator");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> missionController.shutdown());
    }



    private static VBox createRightSidebar(MissionPane missionPane, TelemetryPane telemetryPane) {
        VBox rightVBox = new VBox();
        rightVBox.setSpacing(10);
        rightVBox.setPrefWidth(280);

        rightVBox.getChildren().addAll(
                telemetryPane,
                missionPane
        );

        return rightVBox;
    }

}