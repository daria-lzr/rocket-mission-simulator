package com.example.rocketmissionsimulatormodule.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Locale;

public class TelemetryPane extends VBox
{
    private final Label telemetryTitle = new Label("Telemetry");
    private final Label altitudeLabel = new Label("Altitude: 0 m");
    private final Label velocityLabel = new Label("Velocity: 0 m/s");
    private final Label fuelLabel = new Label("Fuel remaining: 100%");
    private final Label throttleLabel = new Label("Throttle: 100%");
    private final Label engineOutputLabel = new Label("Engine output: 100%");
    private final Label orientationLabel = new Label("Orientation: 0°");

    public TelemetryPane()
    {
        setPadding(new Insets(15));
        setSpacing(10);
        setStyle("-fx-background-color: #E2E8F0;");

        getChildren().addAll(
                telemetryTitle,
                altitudeLabel,
                velocityLabel,
                fuelLabel,
                throttleLabel,
                engineOutputLabel,
                orientationLabel
        );
    }

    public void updateTelemetry(
            double altitude,
            double velocity,
            double fuelPercent,
            double throttle,
            double effectiveThrottle)
    {
        altitudeLabel.setText(
                String.format(Locale.US, "Altitude: %.2f m", altitude)
        );

        velocityLabel.setText(
                String.format(Locale.US, "Velocity: %.2f m/s", velocity)
        );

        fuelLabel.setText(
                String.format(Locale.US, "Fuel remaining: %.1f%%", fuelPercent)
        );

        throttleLabel.setText(
                String.format(Locale.US, "Throttle: %.0f%%", throttle * 100.0)
        );

        engineOutputLabel.setText(
                String.format(
                        Locale.US,
                        "Engine output: %.0f%%",
                        effectiveThrottle * 100.0
                )
        );
    }
}
