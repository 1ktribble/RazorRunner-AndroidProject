package edu.uark.csce.razorrunner;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.ActivityRecognitionClient;

import java.sql.Connection;

/**
 * Created by Kai Tribble on 11/28/2014.
 */
public class RemoveDetection implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener{

    private Context context;
    private ActivityRecognitionClient activityRecognitionClient;
    private PendingIntent pendingIntent;

    public RemoveDetection(Context context){
        this.context = context;
        activityRecognitionClient = null;
    }

    public void removeUpdates(PendingIntent reqIntent){
        pendingIntent = reqIntent;
        requestConnection();
    }

    public void requestConnection(){
        getActivityRecognitionClient().connect();
    }

    public ActivityRecognitionClient getActivityRecognitionClient(){
        if(activityRecognitionClient == null){
            setActivityRecognitionClient(new ActivityRecognitionClient(
                    context, this, this));
        }
        return activityRecognitionClient;
    }

    public void requestDisconnection(){
        getActivityRecognitionClient().disconnect();
        setActivityRecognitionClient(null);
    }

    public void setActivityRecognitionClient(ActivityRecognitionClient client){
        activityRecognitionClient = client;
    }

    public void continueRemoveUpdates(){
        activityRecognitionClient.removeActivityUpdates(pendingIntent);
        pendingIntent.cancel();
        requestDisconnection();
    }

    @Override
    public void onConnected(Bundle bundle) {
        continueRemoveUpdates();
    }

    @Override
    public void onDisconnected() {
        activityRecognitionClient = null;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(connectionResult.hasResolution()){
            try {
                connectionResult.startResolutionForResult((Activity) context,
                        ActivityUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e){
                e.printStackTrace();
            }
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                    connectionResult.getErrorCode(), (Activity) context,
                    ActivityUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            if(dialog != null)
                dialog.show();
        }
    }
}
