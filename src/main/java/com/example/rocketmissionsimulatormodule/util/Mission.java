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

    private static final double TARGET_ALTITUDE = 1000.0;
    private static final double TARGET_VELOCITY = 200.0;


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


    public boolean checkOrbitReached() {
        if (currentState != MissionState.ASCENT) {
            return false;
        }

        if (rocket.getAltitude() >= TARGET_ALTITUDE && rocket.getVelocity() >= TARGET_VELOCITY) {
            currentState = MissionState.ORBIT;
            return true;
        }

        return false;
    }

}
