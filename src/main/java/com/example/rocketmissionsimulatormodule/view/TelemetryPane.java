package com.example.rocketmissionsimulatormodule.view;

import com.example.rocketmissionsimulatormodule.util.Mission;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class TelemetryPane extends VBox {

    private static final String BACKGROUND = "#050807";
    private static final String FONT = "'Avenir Next', 'Segoe UI', Arial, sans-serif";

    private final Meter altitudeMeter;
    private final Meter velocityMeter;
    private final Meter fuelMeter;
    private final Meter throttleMeter;

    public TelemetryPane() {
        setPadding(new Insets(16));
        setSpacing(13);
        setStyle("-fx-background-color: " + BACKGROUND + ";");

        Label title = new Label("FLIGHT TELEMETRY");
        title.setStyle("-fx-font-family: " + FONT
                + "; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");

        altitudeMeter = new Meter("ALTITUDE", "0 m", "#63C7FF", 0.0, false);
        velocityMeter = new Meter("VELOCITY", "0 m/s", "#7FE0C1", 0.0, false);
        fuelMeter = new Meter("PROPELLANT", "100.0%", "#7FD7A3", 1.0, false);
        throttleMeter = new Meter("THROTTLE", "100.0%", "#E5BB79", 1.0, false);

        getChildren().addAll(title, altitudeMeter, velocityMeter, fuelMeter, throttleMeter);
    }

    public void updateTelemetry(double altitude, double velocity) {
        altitudeMeter.update(
                String.format(java.util.Locale.US, "%.0f m / %.0f m",
                        altitude, Mission.TARGET_ALTITUDE),
                altitude / Mission.TARGET_ALTITUDE
        );
        velocityMeter.update(
                String.format(java.util.Locale.US, "%.0f m/s (vertical)", velocity),
                Math.abs(velocity) / Mission.VELOCITY_DISPLAY_SCALE
        );
    }

    public void updateTelemetry(double altitude, double velocity,
                                double fuelFraction, double throttle) {
        updateTelemetry(altitude, velocity);
        fuelMeter.update(String.format(java.util.Locale.US,
                "%.1f%%", fuelFraction * 100.0), fuelFraction);
        throttleMeter.update(String.format(java.util.Locale.US,
                "%.1f%%", throttle * 100.0), throttle);
    }

    private static final class Meter extends VBox {
        private final Label value;
        private final Region fill;
        private final StackPane track;
        private final String color;

        Meter(String name, String initialValue, String color, double progress,
              boolean inactive) {
            this.color = color;
            setSpacing(5);

            Label nameLabel = new Label(name);
            nameLabel.setStyle("-fx-font-family: " + FONT
                    + "; -fx-font-size: 11px; -fx-font-weight: bold;"
                    + "-fx-text-fill: #9AA8B7;");
            value = new Label(initialValue);
            value.setStyle("-fx-font-family: " + FONT
                    + "; -fx-font-size: 12px; -fx-text-fill: "
                    + (inactive ? "#68737E" : "#F4F8FA") + ";");

            HBox heading = new HBox(8, nameLabel, value);
            heading.setAlignment(Pos.CENTER_LEFT);
            Region gap = new Region();
            HBox.setHgrow(gap, Priority.ALWAYS);
            heading.getChildren().add(1, gap);

            track = new StackPane();
            track.setMinHeight(12);
            track.setPrefHeight(12);
            track.setMaxHeight(12);
            track.setMaxWidth(Double.MAX_VALUE);
            track.setStyle("-fx-background-color: #19242B;"
                    + "-fx-background-radius: 3;"
                    + "-fx-border-color: #34434B; -fx-border-radius: 3;");

            fill = new Region();
            fill.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 2;");
            fill.setMinHeight(6);
            fill.setMaxHeight(6);
            fill.setPrefHeight(6);
            fill.setMinWidth(0);
            fill.setMaxWidth(Region.USE_PREF_SIZE);
            fill.prefWidthProperty().bind(track.widthProperty().subtract(8).multiply(Math.clamp(progress, 0.0, 1.0)));
            track.getChildren().add(fill);
            StackPane.setAlignment(fill, Pos.CENTER_LEFT);
            StackPane.setMargin(fill, new Insets(0, 4, 0, 4));

            getChildren().addAll(heading, track);
        }

        void update(String text, double fraction) {
            value.setText(text);
            fill.prefWidthProperty().unbind();
            //this is a bound width (so it also adjusts when the sidebar resizes)
            fill.prefWidthProperty().bind(track.widthProperty().subtract(8)
                    .multiply(Math.clamp(fraction, 0.0, 1.0)));
        }
    }
}
