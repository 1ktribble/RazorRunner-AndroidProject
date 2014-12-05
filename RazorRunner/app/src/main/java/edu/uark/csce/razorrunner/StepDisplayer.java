package edu.uark.csce.razorrunner;

import java.util.ArrayList;

/**
 * Created by Kai Tribble on 12/3/2014.
 */
public class StepDisplayer implements StepListener{

    private int mCount = 0;
    PedometerSettings mSettings;
    ActivityUtils mUtils;


    public StepDisplayer(PedometerSettings settings, ActivityUtils utils) {
        mUtils = utils;
        mSettings = settings;
        notifyListener();
    }
    public void setUtils(ActivityUtils utils) {
        mUtils = utils;
    }


    public void setSteps(int steps) {
        mCount = steps;
        notifyListener();
    }
    public void onStep() {
        mCount ++;
        notifyListener();
    }
    public void reloadSettings() {
        notifyListener();
    }
    public void passValue() {
    }




    //-----------------------------------------------------
    // Listener

    public interface Listener {
        public void stepsChanged(int value);
        public void passValue();
    }
    private ArrayList<Listener> mListeners = new ArrayList<Listener>();


    public void addListener(Listener l) {
        mListeners.add(l);
    }
    public void notifyListener() {
        for (Listener listener : mListeners) {
            listener.stepsChanged((int) mCount);
        }
    }
}
