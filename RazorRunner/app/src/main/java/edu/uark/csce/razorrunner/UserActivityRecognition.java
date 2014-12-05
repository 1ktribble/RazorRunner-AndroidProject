package edu.uark.csce.razorrunner;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by Kai Tribble on 11/27/2014.
 */
public class UserActivityRecognition extends IntentService {

    private String TAG = this.getClass().getSimpleName();
    private SharedPreferences preferences;
    public String KEY_PREVIOUS_ACTIVITY_TYPE = "PreviousActvityTypeKey";

    public UserActivityRecognition() {
        super(".UserActivityRecognition");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result =
                    ActivityRecognitionResult.extractResult(intent);

            DetectedActivity detectedActivity = result.getMostProbableActivity();
            int confidence = detectedActivity.getConfidence();
            int activityType = detectedActivity.getType();

            Log.i(TAG, "Actvity: "+ activityType + "Confidence: " + confidence) ;
             if(!preferences.contains(KEY_PREVIOUS_ACTIVITY_TYPE)) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(ActivityUtils.KEY_PREVIOUS_ACTIVITY_TYPE,
                        getNameFromType(activityType));
                editor.commit();

            }else if(isMoving(activityType) && actvityChanged(activityType)
                    && (confidence >= 50)) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.putString(ActivityUtils.KEY_PREVIOUS_ACTIVITY_TYPE,
                        getNameFromType(activityType));
                editor.commit();
            }
        }
    }

    public boolean isMoving(int type){
        switch (type){
            case DetectedActivity.STILL :
            case DetectedActivity.TILTING :
            case DetectedActivity.UNKNOWN :
                return false;
            default:
                return true;
        }
    }

    public boolean actvityChanged(int currentType){
        int previousType = preferences.getInt(KEY_PREVIOUS_ACTIVITY_TYPE,
                DetectedActivity.UNKNOWN);

        if(previousType != currentType)
            return true;
        else
            return false;
    }
    private String getNameFromType(int activityType) {
        switch(activityType) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.UNKNOWN:
                return "unknown";
            case DetectedActivity.TILTING:
                return "tilting";
        }
        return "unknown";
    }

    public PendingIntent getContentIntent(){
        Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

        return PendingIntent.getActivity(getApplicationContext(), 0, gpsIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}