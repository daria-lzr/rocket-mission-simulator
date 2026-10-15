package com.example.rocketmissionsimulatormodule.view;


import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class TelemetryPane extends VBox {

    private Label altitudeLabel;
    private Label velocityLabel;

    public TelemetryPane() {
        setPadding(new Insets(15));
        setSpacing(10);
        setStyle("-fx-background-color: #E2E8F0;");

        Label telemetryTitle = new Label("Telemetry");
        altitudeLabel = new Label("Altitude: 0 km");
        velocityLabel = new Label("Velocity: 0 m/s");
        Label fuelLabel = new Label("Fuel: 100%");
        Label throttleLabel = new Label("Throttle: 0%");
        Label orientationLabel = new Label("Orientation: 0°");

        getChildren().addAll(
                telemetryTitle,
                altitudeLabel,
                velocityLabel,
                fuelLabel,
                throttleLabel,
                orientationLabel
        );
    }


    public void updateTelemetry(double altitude, double velocity) {
        altitudeLabel.setText(
                String.format("Altitude: %.2f m", altitude)
        );

        velocityLabel.setText(
                String.format("Velocity: %.2f m/s", velocity)
        );
    }
}

