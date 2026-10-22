package com.example.rocketmissionsimulatormodule.view;

import com.example.rocketmissionsimulatormodule.view.components.FailureOverlay;
import com.example.rocketmissionsimulatormodule.view.components.FlightView;
import com.example.rocketmissionsimulatormodule.view.components.RecoveryNotification;

import javafx.scene.layout.Pane;

public class SimulationPane extends Pane {

    private final FlightView flightView;
    private final RecoveryNotification recoveryNotification;
    private final FailureOverlay failureOverlay;

    public SimulationPane() {
        setPrefSize(800, 500);

        flightView = new FlightView();
        recoveryNotification = new RecoveryNotification();
        failureOverlay = new FailureOverlay();

        getChildren().addAll(
                flightView,
                recoveryNotification,
                failureOverlay
        );

        //flightView fills the simulation area
        flightView.prefWidthProperty().bind(widthProperty());
        flightView.prefHeightProperty().bind(heightProperty());

        //center = recovery notification horizontally
        recoveryNotification.layoutXProperty().bind(
                widthProperty()
                        .subtract(recoveryNotification.widthProperty())
                        .divide(2)
        );

        recoveryNotification.setLayoutY(30);

        //failureOverlay covers  entire simulation area
        failureOverlay.prefWidthProperty().bind(widthProperty());
        failureOverlay.prefHeightProperty().bind(heightProperty());
    }

    public void updateRocketPosition(double altitude) {
        flightView.updateRocketPosition(altitude);
    }

    public void updateThrust(
            double effectiveThrottle,
            boolean hasFuel) {
        flightView.updateThrust(
                effectiveThrottle,
                hasFuel);
    }

    public void showSatellite(Runnable onSeparationFinished) {
        flightView.showSatellite(onSeparationFinished);
    }

    public void showRecoveryNotification(boolean cooling) {
        recoveryNotification.showNotification(cooling);
    }

    public void showFailureAnimation() {
        failureOverlay.showFailure();
    }
}