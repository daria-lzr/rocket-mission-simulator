
package com.example.rocketmissionsimulatormodule.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.Objects;

import javafx.animation.TranslateTransition;
import javafx.util.Duration;

public class SimulationPane extends Pane {
    private ImageView starshipView;
    private ImageView satelliteView;

    public SimulationPane() {
        setPrefSize(800, 500);

        setStyle(
                "-fx-background-color: linear-gradient("
                        + "to bottom, "
                        + "#529BE8 0%, "
                        + "#87C4F5 65%, "
                        + "#C5E5FF 100%"
                        + ");"
        );

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


        //add satelite image
        URL satelliteImageURL = Objects.requireNonNull(
                getClass().getResource("/images/satelite.png"),
                "Satellite image resource not found"
        );

        Image satelliteImage = new Image(satelliteImageURL.toExternalForm());
        satelliteView = new ImageView(satelliteImage);

        satelliteView.setPreserveRatio(true);
        satelliteView.setFitWidth(80);
        satelliteView.setVisible(false);


        getChildren().addAll(groundView, starshipView, satelliteView);
    }



    public void updateRocketPosition(double altitude) {
        double pixelsPerMeter = 0.1;
        double verticalDisplacement = altitude * pixelsPerMeter;

        starshipView.setTranslateY(-verticalDisplacement);
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
        TranslateTransition separation =
                new TranslateTransition(Duration.seconds(2), satelliteView);

        separation.setByX(150);
        separation.setByY(-50);

        separation.setOnFinished(event -> onSeparationFinished.run());

        separation.play();
    }


}
