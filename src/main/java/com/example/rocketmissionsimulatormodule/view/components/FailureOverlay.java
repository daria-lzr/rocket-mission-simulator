package com.example.rocketmissionsimulatormodule.view.components;

public class FailureOverlay extends MissionOutcomeOverlay {

    public FailureOverlay() {
        super("FAILURE", "MISSION TERMINATED");
    }

    @Override
    protected void applyOutcomeAppearance() {
        setOutcomeColors(
                "#FCA5A5",
                "#FECACA",
                "#491C24",
                "#B83B48",
                "rgba(100, 0, 0, 0.35)"
        );
    }
}
