package com.example.rocketmissionsimulatormodule.view.components;

public class SuccessOverlay extends MissionOutcomeOverlay {

    public SuccessOverlay() {
        super("SUCCESS", "SATELLITE DEPLOYED SUCCESSFULLY");
    }

    @Override
    protected void applyOutcomeAppearance() {
        setOutcomeColors(
                "#86EFAC",
                "#BBF7D0",
                "#163D30",
                "#339C71",
                "rgba(0, 70, 30, 0.35)"
        );
    }
}
