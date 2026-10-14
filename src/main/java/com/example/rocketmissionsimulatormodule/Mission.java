package com.example.rocketmissionsimulatormodule;

public class Mission {

    public enum MissionState {
        PRE_LAUNCH,
        ASCENT,
        ORBIT,
        DEPLOYMENT,
        SUCCESS,
        FAILURE
    }


    private MissionState currentState;

    public Mission() {
        this.currentState = MissionState.PRE_LAUNCH;
    }

    public MissionState getCurrentState() {
        return currentState;
    }
}
