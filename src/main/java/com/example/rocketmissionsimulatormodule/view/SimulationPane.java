package com.example.rocketmissionsimulatormodule.view;

import com.example.rocketmissionsimulatormodule.view.components.FailureOverlay;
import com.example.rocketmissionsimulatormodule.view.components.FlightView;
import com.example.rocketmissionsimulatormodule.view.components.RecoveryNotification;
import com.example.rocketmissionsimulatormodule.view.components.SuccessOverlay;

import javafx.scene.layout.Pane;

public class SimulationPane extends Pane {

    private final FlightView flightView;
    private final RecoveryNotification recoveryNotification;
    private final FailureOverlay failureOverlay;
    private final SuccessOverlay successOverlay;

    public SimulationPane() {
        setPrefSize(800, 500);

        flightView = new FlightView();
        recoveryNotification = new RecoveryNotification();
        failureOverlay = new FailureOverlay();
        successOverlay = new SuccessOverlay();

        getChildren().addAll(
                flightView,
                recoveryNotification,
                failureOverlay,
                successOverlay
        );

        flightView.prefWidthProperty().bind(widthProperty());
        flightView.prefHeightProperty().bind(heightProperty());

        recoveryNotification.layoutXProperty().bind(
                widthProperty()
                        .subtract(recoveryNotification.widthProperty())
                        .divide(2)
        );
        recoveryNotification.setLayoutY(30);

        failureOverlay.prefWidthProperty().bind(widthProperty());
        failureOverlay.prefHeightProperty().bind(heightProperty());
        successOverlay.prefWidthProperty().bind(widthProperty());
        successOverlay.prefHeightProperty().bind(heightProperty());
    }


    public void setOnRestart(Runnable action) {
        failureOverlay.setOnRestart(action);
        successOverlay.setOnRestart(action);
    }

    public void setOnExit(Runnable action) {
        failureOverlay.setOnExit(action);
        successOverlay.setOnExit(action);
    }

    public void playIgnition(Runnable onFinished) {
        flightView.playIgnition(onFinished);
    }

    public void updateEngineExhaust(double throttle, double altitude) {
        flightView.updateEngineExhaust(throttle, altitude);
    }

    public void stopEngines() {
        flightView.stopEngines();
    }

    public void dispose() {
        flightView.dispose();
    }

    public void updateRocketPosition(double altitude) {
        flightView.updateRocketPosition(altitude);
    }

    public void showSatellite(Runnable onSeparationFinished) {
        flightView.showSatellite(onSeparationFinished);
    }

    public void showRecoveryNotification(boolean cooling) {
        recoveryNotification.showNotification(cooling);
    }

    public void showMaxQRecoveryNotification() {
        recoveryNotification.showMaxQRecovery();
    }

    public void showPropellantRecoveryNotification() {
        recoveryNotification.showPropellantRecovery();
    }

    public void showAttitudeRecoveryNotification() {
        recoveryNotification.showAttitudeRecovery();
    }

    public void showFailureAnimation() {
        failureOverlay.showFailure();
    }

    public void showSuccessAnimation() {
        successOverlay.showSuccess();
    }
}
