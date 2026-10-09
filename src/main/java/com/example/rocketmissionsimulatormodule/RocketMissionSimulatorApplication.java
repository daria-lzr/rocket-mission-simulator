package com.example.rocketmissionsimulatormodule;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

public class RocketMissionSimulatorApplication extends Application {

    private static final Insets COMMON_INSETS = new Insets(15);

    @Override
    public void start(Stage stage) {
        BorderPane mainPane = new BorderPane();

        // Build main UI components using dedicated helper methods
        Pane simulationPane = createSimulationPane();
        VBox rightVBox = createRightSidebar();
        TextArea missionLogArea = createMissionLogArea();

        // Assemble the main layout
        mainPane.setCenter(simulationPane);
        mainPane.setRight(rightVBox);
        mainPane.setBottom(missionLogArea);

        Scene scene = new Scene(mainPane, 1200, 750);
        stage.setTitle("Rocket Mission Simulator");
        stage.setScene(scene);
        stage.show();
    }

    private Pane createSimulationPane() {
        Pane simulationPane = new Pane();
        simulationPane.setPrefSize(800, 500);
        simulationPane.setStyle("-fx-background-color: #101D35;");

        URL starshipImageURL = Objects.requireNonNull(
                getClass().getResource("/images/starship.png"),
                "Starship image resource not found"
        );
        Image starshipImage = new Image(starshipImageURL.toExternalForm());
        ImageView imageView = new ImageView(starshipImage);

        imageView.setPreserveRatio(true);
        imageView.setFitHeight(250);

        //Center the rocket horizontally
        imageView.layoutXProperty().bind(
                simulationPane.widthProperty()
                        .subtract(imageView.getBoundsInLocal().getWidth())
                        .divide(2)
        );

        //Position the rocket 20 pixels above the bottom
        imageView.layoutYProperty().bind(
                simulationPane.heightProperty()
                        .subtract(imageView.getBoundsInLocal().getHeight())
                        .subtract(20)
        );

        simulationPane.getChildren().add(imageView);
        return simulationPane;
    }

    private VBox createRightSidebar() {
        VBox rightVBox = new VBox();
        rightVBox.setSpacing(10);
        rightVBox.setPrefWidth(280);

        rightVBox.getChildren().addAll(
                createTelemetryVBox(),
                createMissionVBox()
        );

        return rightVBox;
    }

    private VBox createTelemetryVBox() {
        VBox telemetryVBox = new VBox();
        telemetryVBox.setPadding(COMMON_INSETS);
        telemetryVBox.setSpacing(10);
        telemetryVBox.setStyle("-fx-background-color: #E2E8F0;");

        Label telemetryTitle = new Label("Telemetry");
        Label altitudeLabel = new Label("Altitude: 0 km");
        Label velocityLabel = new Label("Velocity: 0 m/s");
        Label fuelLabel = new Label("Fuel: 100%");
        Label throttleLabel = new Label("Throttle: 0%");
        Label orientationLabel = new Label("Orientation: 0°");

        telemetryVBox.getChildren().addAll(
                telemetryTitle,
                altitudeLabel,
                velocityLabel,
                fuelLabel,
                throttleLabel,
                orientationLabel
        );

        return telemetryVBox;
    }

    private VBox createMissionVBox() {
        VBox missionVBox = new VBox();
        missionVBox.setPadding(COMMON_INSETS);
        missionVBox.setSpacing(10);
        missionVBox.setStyle("-fx-background-color: #FCE8D5;");

        Label missionTitle = new Label("Mission / Events");
        Label statusLabel = new Label("Status: Pre-launch");
        Label activeEventLabel = new Label("Active Event: None");
        Label objectiveLabel = new Label("Objective: Deploy satellite");

        missionVBox.getChildren().addAll(
                missionTitle,
                statusLabel,
                activeEventLabel,
                objectiveLabel
        );

        return missionVBox;
    }

    private TextArea createMissionLogArea() {
        TextArea missionLogArea = new TextArea();
        missionLogArea.setEditable(false);
        missionLogArea.setPrefRowCount(5);
        missionLogArea.setText("[00:00] Mission initialized");
        missionLogArea.setStyle(
                "-fx-control-inner-background: #4c5057;"
                        + "-fx-text-fill: #FFFFFF;"
        );
        return missionLogArea;
    }
}