package edu.uark.csce.razorrunner;

import android.app.Service;
import android.text.format.Time;

/**
 * Created by Kai Tribble on 11/28/2014.
 */
public class ActivityUtils {
    // Used to track what type of request is in process
    public enum REQUEST_TYPE {ADD, REMOVE}

    public static final String APPTAG = "edu.uark.csce.RazorRunner";

    public static final String GENDER = "prefUserGender";
    public static final String AGE = "prefUserAge";
    public static final String WEIGHT = "prefUserWeight";

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // Intent actions and extras for sending information from the IntentService to the Activity
    // Constants used to establish the activity update interval
    public static final int MILLISECONDS_PER_SECOND = 1000;

    public static final int DETECTION_INTERVAL_SECONDS = 20;

    public static final int DETECTION_INTERVAL_MILLISECONDS =
            MILLISECONDS_PER_SECOND * DETECTION_INTERVAL_SECONDS;

//    // Shared Preferences repository name
//    public static final String SHARED_PREFERENCES =
//            "com.example.android.activityrecognition.SHARED_PREFERENCES";

    // Key in the repository for the previous activity
    public static final String KEY_PREVIOUS_ACTIVITY_TYPE =
            "com.example.android.activityrecognition.KEY_PREVIOUS_ACTIVITY_TYPE";

    private Service mService;


    private static ActivityUtils instance = null;


    private ActivityUtils() {
    }

    public static ActivityUtils getInstance() {
        if (instance == null) {
            instance = new ActivityUtils();
        }
        return instance;
    }

    public void setService(Service service) {
        mService = service;
    }

    public static long currentTimeInMillis() {
        Time time = new Time();
        time.setToNow();
        return time.toMillis(false);
    }
}

