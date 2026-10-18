package com.example.rocketmissionsimulatormodule.util;


public class Mission {

    private final Rocket rocket;

    public enum MissionState {
        PRE_LAUNCH,
        ASCENT,
        ORBIT,
        DEPLOYMENT,
        SUCCESS,
        FAILURE
    }


    private volatile MissionState currentState;

    public Mission() {
        this.currentState = MissionState.PRE_LAUNCH;
        this.rocket = new Rocket();
    }

    public MissionState getCurrentState() {
        return currentState;
    }


    public void launch(){
        if(currentState == MissionState.PRE_LAUNCH){
            currentState = MissionState.ASCENT;
        }

    }

    public Rocket getRocket() {
        return rocket;
    }
}
