package com.example.rocketmissionsimulatormodule.view.components;

import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.Objects;

public class FlightView extends Pane {

    private ImageView starshipView;
    private ImageView satelliteView;
    private ImageView spaceView;
    private ImageView groundView;

    private static final double FADE_START_ALTITUDE = 30_000.0;
    private static final double FADE_END_ALTITUDE = 220_000.0;

    // the ground scrolls down and leaves the camera view during initial ascent
    private static final double GROUND_EXIT_ALTITUDE = 1_500.0;
    private static final double GROUND_HEIGHT = 70.0;

    public FlightView() {
        // keep scenery from drawing outside the simulation viewport as it scrolls.
        Rectangle viewportClip = new Rectangle();
        viewportClip.widthProperty().bind(widthProperty());
        viewportClip.heightProperty().bind(heightProperty());
        setClip(viewportClip);

        setupBackground();
        setupSpace();
        setupStarship();
        groundView = setupGround();
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
        groundView.setSmooth(true);

        groundView.setFitHeight(GROUND_HEIGHT);
        groundView.setPreserveRatio(false);

        groundView.fitWidthProperty().bind(widthProperty());

        groundView.layoutYProperty().bind(
                heightProperty().subtract(GROUND_HEIGHT)
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
        // keep the ground anchored at the bottom. A capped, slow upward
        // rocket animation suggests ascent without sending it off-screen
        double rocketHeight = starshipView.getBoundsInLocal().getHeight();
        double startingCenterY = getHeight() - 70 - rocketHeight / 2.0;
        double desiredCenterY = getHeight() * 0.45;
        double maximumShipMovement = Math.max(0.0, startingCenterY - desiredCenterY);
        double initialRise = Math.min(maximumShipMovement * 0.65, altitude * 0.1);
        double continuingRise = maximumShipMovement * 0.35
                * (1.0 - Math.exp(-Math.max(0.0, altitude - 2_000.0) / 65_000.0));
        starshipView.setTranslateY(-Math.min(maximumShipMovement, initialRise + continuingRise));
        // as the rocket climbs, the ground moves DOWN and out of the camera frame
        double groundDistance = Math.clamp(altitude / GROUND_EXIT_ALTITUDE, 0.0, 1.0);
        groundView.setOpacity(1.0);
        double groundY = GROUND_HEIGHT * groundDistance;
        groundView.setTranslateY(groundY);
        groundView.setVisible(groundDistance < 1.0);

        // cinematic sky-to-space transition spread across a large portion
        // of the ascent. Smoothstep avoids an abrupt start and finish
        double progress = (altitude - FADE_START_ALTITUDE)
                / (FADE_END_ALTITUDE - FADE_START_ALTITUDE);
        progress = Math.clamp(progress, 0.0, 1.0);
        spaceView.setOpacity(progress * progress * (3.0 - 2.0 * progress));
    }

    public void showSatellite(Runnable onSeparationFinished) {
        //find rocket displayed position
        double rocketCenterX = starshipView.getBoundsInParent().getCenterX();
        double rocketCenterY = starshipView.getBoundsInParent().getCenterY();

        //position satellite at rocket's center
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