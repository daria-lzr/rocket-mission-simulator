
package com.example.rocketmissionsimulatormodule.view;

import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.Objects;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.util.Duration;

public class SimulationPane extends Pane {

    private ImageView starshipView;
    private ImageView satelliteView;
    private ImageView spaceView;
    private VBox recoveryNotification;

    private Label notificationHeader;
    private Label notificationSymbol;
    private Label notificationTitle;
    private Label notificationDescription;
    private VBox notificationMessageBox;
    private StackPane failureOverlay;
    private VBox failureMessage;

    private static final double FADE_START_ALTITUDE = 100;
    private static final double FADE_END_ALTITUDE = 900;

    public SimulationPane() {
        setPrefSize(800, 500);

        setupBackground();
        setupSpace();
        setupStarship();
        ImageView groundView = setupGround();
        setupSatellite();
        setupRecoveryNotification();
        setupFailureOverlay();

        getChildren().addAll(
                spaceView,
                groundView,
                starshipView,
                satelliteView,
                recoveryNotification,
                failureOverlay
        );

        recoveryNotification.layoutXProperty().bind(
                widthProperty()
                        .subtract(recoveryNotification.widthProperty())
                        .divide(2)
        );
        recoveryNotification.setLayoutY(30);

        failureOverlay.prefWidthProperty().bind(widthProperty());
        failureOverlay.prefHeightProperty().bind(heightProperty());
    }

    private void setupBackground() {
        setStyle(
                "-fx-background-color: linear-gradient("
                        + "to bottom, "
                        + "#529BE8 0%, "
                        + "#87C4F5 65%, "
                        + "#C5E5FF 100%"
                        + ");"
        );
    }

    private void setupSpace() {
        URL spaceImageURL = Objects.requireNonNull(
                getClass().getResource("/images/space.png"),
                "Space image resource not found"
        );

        Image spaceImage = new Image(spaceImageURL.toExternalForm());
        spaceView = new ImageView(spaceImage);

        spaceView.setPreserveRatio(false);
        spaceView.setOpacity(0);

        spaceView.fitWidthProperty().bind(widthProperty());
        spaceView.fitHeightProperty().bind(heightProperty());
    }

    private void setupStarship() {
        URL starshipImageURL = Objects.requireNonNull(
                getClass().getResource("/images/starship.png"),
                "Starship image resource not found"
        );

        Image starshipImage = new Image(starshipImageURL.toExternalForm());
        starshipView = new ImageView(starshipImage);

        starshipView.setPreserveRatio(true);
        starshipView.setFitHeight(250);

        //center rocket horizontally
        starshipView.layoutXProperty().bind(
                widthProperty()
                        .subtract(starshipView.getBoundsInLocal().getWidth())
                        .divide(2)
        );

        //position rocket above ground
        starshipView.layoutYProperty().bind(
                heightProperty()
                        .subtract(70)
                        .subtract(starshipView.getBoundsInLocal().getHeight())
        );
    }

    private ImageView setupGround() {
        URL groundImageURL = Objects.requireNonNull(
                getClass().getResource("/images/ground.png"),
                "Ground image resource not found"
        );

        Image groundImage = new Image(groundImageURL.toExternalForm());
        ImageView groundView = new ImageView(groundImage);

        groundView.setFitHeight(70);
        groundView.setPreserveRatio(false);

        groundView.fitWidthProperty().bind(widthProperty());

        groundView.layoutYProperty().bind(
                heightProperty().subtract(70)
        );

        return groundView;
    }

    private void setupSatellite() {
        URL satelliteImageURL = Objects.requireNonNull(
                getClass().getResource("/images/satelite.png"),
                "Satellite image resource not found"
        );

        Image satelliteImage = new Image(satelliteImageURL.toExternalForm());
        satelliteView = new ImageView(satelliteImage);

        satelliteView.setPreserveRatio(true);
        satelliteView.setFitWidth(80);
        satelliteView.setVisible(false);
    }

    public void updateRocketPosition(double altitude) {
        double pixelsPerMeter = 0.1;
        double verticalDisplacement = altitude * pixelsPerMeter;

        starshipView.setTranslateY(-verticalDisplacement);

        //calculate space background opacity based on altitude (using linear interpolation equation)
        double opacity = (altitude - FADE_START_ALTITUDE) / (FADE_END_ALTITUDE - FADE_START_ALTITUDE);

        //keep opacity between 0 and 1
        opacity = Math.max(0, Math.min(1, opacity));

        spaceView.setOpacity(opacity);
    }




    public void showSatellite(Runnable onSeparationFinished) {
        //find rocket's displayed position
        double rocketCenterX = starshipView.getBoundsInParent().getCenterX();
        double rocketCenterY = starshipView.getBoundsInParent().getCenterY();

        //position  satellite at rocket's center
        satelliteView.setLayoutX(
                rocketCenterX - satelliteView.getBoundsInLocal().getWidth() / 2
        );

        satelliteView.setLayoutY(
                rocketCenterY - satelliteView.getBoundsInLocal().getHeight() / 2
        );

        satelliteView.setVisible(true);

        //animate satellite moving away from rocket
        TranslateTransition separation = new TranslateTransition(Duration.seconds(2), satelliteView);

        separation.setByX(150);
        separation.setByY(-50);

        separation.setOnFinished(event -> onSeparationFinished.run());

        separation.play();
    }

    private void setupRecoveryNotification() {
        notificationHeader = new Label("SUCCESSFUL RECOVERY");
        notificationHeader.setStyle(
                "-fx-text-fill: #9BAEC8;"
                        + "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
        );

        notificationSymbol = new Label("❄");
        notificationSymbol.setStyle(
                "-fx-text-fill: #60A5FA;"
                        + "-fx-font-size: 24px;"
        );

        notificationTitle = new Label("EMERGENCY COOLING");
        notificationTitle.setStyle(
                "-fx-text-fill: #93C5FD;"
                        + "-fx-font-size: 16px;"
                        + "-fx-font-weight: bold;"
        );

        notificationDescription = new Label("Engine temperature stabilized");
        notificationDescription.setStyle(
                "-fx-text-fill: #BFDBFE;"
                        + "-fx-font-size: 12px;"
        );

        HBox titleRow = new HBox(10, notificationSymbol, notificationTitle);
        titleRow.setAlignment(Pos.CENTER);

        notificationMessageBox = new VBox(5, titleRow, notificationDescription);
        notificationMessageBox.setAlignment(Pos.CENTER);
        notificationMessageBox.setPadding(new Insets(15));

        notificationMessageBox.setStyle(
                "-fx-background-color: #102F50;"
                        + "-fx-border-color: #2875C0;"
                        + "-fx-border-width: 1;"
                        + "-fx-background-radius: 8;"
                        + "-fx-border-radius: 8;"
        );

        recoveryNotification = new VBox(10, notificationHeader, notificationMessageBox);
        recoveryNotification.setPadding(new Insets(12));
        recoveryNotification.setMaxWidth(350);

        recoveryNotification.setStyle(
                "-fx-background-color: #0D192B;"
                        + "-fx-background-radius: 12;"
        );

        recoveryNotification.setVisible(false);
        recoveryNotification.setMouseTransparent(true);
    }

    public void showRecoveryNotification(boolean cooling) {
        configureRecoveryNotification(cooling);

        recoveryNotification.setOpacity(0);
        recoveryNotification.setVisible(true);

        FadeTransition fadeIn = new FadeTransition(
                Duration.seconds(0.3), recoveryNotification
        );
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition hold = new PauseTransition(Duration.seconds(2));

        FadeTransition fadeOut = new FadeTransition(
                Duration.seconds(0.5), recoveryNotification
        );
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        SequentialTransition animation =
                new SequentialTransition(fadeIn, hold, fadeOut);

        animation.setOnFinished(event -> {
            recoveryNotification.setVisible(false);
        });

        animation.play();
    }


    private void configureRecoveryNotification(boolean cooling) {
        if (cooling) {
            notificationHeader.setText("SUCCESSFUL RECOVERY");
            notificationSymbol.setText("❄");
            notificationTitle.setText("EMERGENCY COOLING");
            notificationDescription.setText("Engine temperature stabilized");

            recoveryNotification.setStyle(
                    "-fx-background-color: #0D192B;"
                            + "-fx-background-radius: 12;"
            );

            notificationMessageBox.setStyle(
                    "-fx-background-color: #102F50;"
                            + "-fx-border-color: #2875C0;"
                            + "-fx-border-width: 1;"
                            + "-fx-background-radius: 8;"
                            + "-fx-border-radius: 8;"
            );

            notificationHeader.setStyle("-fx-text-fill: #9BAEC8; -fx-font-size: 11px; -fx-font-weight: bold;");
            notificationSymbol.setStyle("-fx-text-fill: #60A5FA; -fx-font-size: 24px;");
            notificationTitle.setStyle("-fx-text-fill: #93C5FD; -fx-font-size: 16px; -fx-font-weight: bold;");
            notificationDescription.setStyle("-fx-text-fill: #BFDBFE; -fx-font-size: 12px;");

        } else {
            notificationHeader.setText("ENGINE RECOVERY");
            notificationSymbol.setText("◴");
            notificationTitle.setText("THROTTLE REDUCED");
            notificationDescription.setText("Engine operating at reduced power");

            recoveryNotification.setStyle(
                    "-fx-background-color: #20190E;"
                            + "-fx-background-radius: 12;"
            );

            notificationMessageBox.setStyle(
                    "-fx-background-color: #45300F;"
                            + "-fx-border-color: #B7791F;"
                            + "-fx-border-width: 1;"
                            + "-fx-background-radius: 8;"
                            + "-fx-border-radius: 8;"
            );

            notificationHeader.setStyle("-fx-text-fill: #FCD34D; -fx-font-size: 11px; -fx-font-weight: bold;");
            notificationSymbol.setStyle("-fx-text-fill: #FBBF24; -fx-font-size: 24px;");
            notificationTitle.setStyle("-fx-text-fill: #FDE68A; -fx-font-size: 16px; -fx-font-weight: bold;");
            notificationDescription.setStyle("-fx-text-fill: #FCD34D; -fx-font-size: 12px;");
        }
    }

    private void setupFailureOverlay() {
        Label failureTitle = new Label("FAILURE");
        failureTitle.setStyle(
                "-fx-text-fill: #FCA5A5;"
                        + "-fx-font-size: 42px;"
                        + "-fx-font-weight: bold;"
        );

        Label failureDescription =
                new Label("CRITICAL ENGINE MALFUNCTION");

        failureDescription.setStyle(
                "-fx-text-fill: #FECACA;"
                        + "-fx-font-size: 14px;"
        );

        failureMessage = new VBox(
                10, failureTitle, failureDescription
        );

        failureMessage.setAlignment(Pos.CENTER);
        failureMessage.setPadding(new Insets(25));

        failureMessage.setStyle(
                "-fx-background-color: #491C24;"
                        + "-fx-border-color: #B83B48;"
                        + "-fx-border-width: 1;"
                        + "-fx-background-radius: 10;"
                        + "-fx-border-radius: 10;"
        );

        failureMessage.setMaxSize(
                javafx.scene.layout.Region.USE_PREF_SIZE,
                javafx.scene.layout.Region.USE_PREF_SIZE
        );

        failureOverlay = new StackPane(failureMessage);

        failureOverlay.setStyle(
                "-fx-background-color: rgba(100, 0, 0, 0.35);"
        );

        failureOverlay.setVisible(false);
        failureOverlay.setOpacity(0);
    }

    public void showFailureAnimation() {
        failureOverlay.setOpacity(0);
        failureOverlay.setVisible(true);

        FadeTransition fadeIn = new FadeTransition(
                Duration.seconds(0.8),
                failureOverlay
        );

        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        fadeIn.play();
    }


}
