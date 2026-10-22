package com.example.rocketmissionsimulatormodule.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

import java.util.function.DoubleConsumer;

public class FlightControlsPane extends VBox {
    private final Slider throttleSlider;
    private final Label throttleLabel;

    private DoubleConsumer onThrottleChanged;

    public FlightControlsPane() {
        setPadding(new Insets(15));
        setSpacing(10);
        setStyle("-fx-background-color: #E2E8F0;");

        Label title = new Label("Flight Controls");

        throttleLabel = new Label("Throttle: 100%");

        throttleSlider = new Slider(0, 100, 100);
        throttleSlider.setShowTickLabels(true);
        throttleSlider.setShowTickMarks(true);
        throttleSlider.setMajorTickUnit(25);
        throttleSlider.setBlockIncrement(5);
        throttleSlider.setMaxWidth(Double.MAX_VALUE);

        throttleSlider.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    double percent = newValue.doubleValue();

                    throttleLabel.setText(
                            String.format("Throttle: %.0f%%", percent)
                    );
                    if (onThrottleChanged != null) {
                        onThrottleChanged.accept(percent / 100.0);
                    }
                }
        );

        getChildren().addAll(
                title,
                throttleLabel,
                throttleSlider
        );
    }

    public void setOnThrottleChanged(DoubleConsumer callback) {
        onThrottleChanged = callback;
    }
}
