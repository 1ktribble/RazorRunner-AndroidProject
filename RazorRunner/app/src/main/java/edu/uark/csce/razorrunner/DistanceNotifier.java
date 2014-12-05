package edu.uark.csce.razorrunner;

/**
 * Created by Kai Tribble on 12/3/2014.
 */
public class DistanceNotifier implements StepListener{

    public interface Listener {
        public void valueChanged(float value);
        public void passValue();
    }
    private Listener mListener;

    float mDistance = 0;

    PedometerSettings mSettings;
    ActivityUtils mUtils;

    boolean mIsMetric;
    float mStepLength;


    public DistanceNotifier(Listener listener, PedometerSettings settings, ActivityUtils utils) {
        mListener = listener;
        mUtils = utils;
        mSettings = settings;
        reloadSettings();
    }
    public void setDistance(float distance) {
        mDistance = distance;
        notifyListener();
    }

    public void reloadSettings() {
        mIsMetric = mSettings.isMetric();
        mStepLength = mSettings.getStepLength();
        notifyListener();
    }

    public void onStep() {

        if (mIsMetric) {
            mDistance += (float)(// kilometers
                    mStepLength // centimeters
                            / 100000.0); // centimeters/kilometer
        }
        else {
            mDistance += (float)(// miles
                    mStepLength // inches
                            / 63360.0); // inches/mile
        }

        notifyListener();
    }

    private void notifyListener() {
        mListener.valueChanged(mDistance);
    }

    @Override
    public void passValue() {

    }
}
