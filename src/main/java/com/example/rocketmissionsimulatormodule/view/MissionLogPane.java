
package com.example.rocketmissionsimulatormodule.view;

import javafx.scene.control.TextArea;

public class MissionLogPane extends TextArea {

    public MissionLogPane() {
        setEditable(false);
        setPrefRowCount(5);
        setText("[00:00] Mission initialized");

        setStyle(
                "-fx-control-inner-background: #4c5057;"
                        + "-fx-text-fill: #FFFFFF;"
        );
    }
}
