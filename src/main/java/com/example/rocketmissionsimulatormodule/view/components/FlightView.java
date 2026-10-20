package com.example.rocketmissionsimulatormodule.view.components;

import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.net.URL;
import java.util.Objects;

public class FlightView extends Pane {

    private ImageView starshipView;
    private ImageView satelliteView;
    private ImageView spaceView;

    private static final double FADE_START_ALTITUDE = 100;
    private static final double FADE_END_ALTITUDE = 900;

    public FlightView() {
        setupBackground();
        setupSpace();
        setupStarship();
        ImageView groundView = setupGround();
        setupSatellite();

        getChildren().addAll(
                spaceView,
                groundView,
                starshipView,
                satelliteView
        );
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

}