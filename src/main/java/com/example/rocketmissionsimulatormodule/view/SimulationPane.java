
package com.example.rocketmissionsimulatormodule.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.Objects;

public class SimulationPane extends Pane {

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
        ImageView starshipView = new ImageView(starshipImage);

        starshipView.setPreserveRatio(true);
        starshipView.setFitHeight(250);

        // Center rocket horizontally
        starshipView.layoutXProperty().bind(
                widthProperty()
                        .subtract(starshipView.getBoundsInLocal().getWidth())
                        .divide(2)
        );

        // Position rocket above ground
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

        getChildren().addAll(groundView, starshipView);
    }
}
