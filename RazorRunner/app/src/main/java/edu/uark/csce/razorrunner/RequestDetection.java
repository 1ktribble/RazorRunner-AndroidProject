package edu.uark.csce.razorrunner;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.ActivityRecognitionClient;

/**
 * Created by Kai Tribble on 11/28/2014.
 */
public class RequestDetection implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener{

    private Context context;
    private PendingIntent pendingIntent;
    private ActivityRecognitionClient activityRecognitionClient;

    public RequestDetection(Context context)
    {
        this.context = context;
        activityRecognitionClient = null;
        pendingIntent = null;
    }

    public PendingIntent getRequestPendingIntent(){return pendingIntent;}

    public void setRequestPendingIntent(PendingIntent pendingIntent){
        this.pendingIntent = pendingIntent;
    }

    public void requestUpdates(){
        requestConnection();
    }

    public void requestConnection(){
        getActivityRecognitionClient().connect();
    }

    private ActivityRecognitionClient getActivityRecognitionClient(){
        if(activityRecognitionClient == null) {
            activityRecognitionClient =
                    new ActivityRecognitionClient(context, this, this);
        }
        return activityRecognitionClient;
    }

    private void requestDisconnection() {
        getActivityRecognitionClient().disconnect();
    }

    private void continueRequestActivityUpdates() {
        getActivityRecognitionClient().requestActivityUpdates(
        ActivityUtils.DETECTION_INTERVAL_MILLISECONDS, createRequestPendingIntent()
        );

        requestDisconnection();
    }

    private PendingIntent createRequestPendingIntent(){
        if(getRequestPendingIntent() != null)
            return pendingIntent;
        else{
            Intent intent = new Intent(context, UserActivityRecognition.class);

            PendingIntent activityPendingIntent = PendingIntent.getService(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            setRequestPendingIntent(activityPendingIntent);

            return  activityPendingIntent;
        }
    }
    @Override
    public void onConnected(Bundle bundle) {
        continueRequestActivityUpdates();
    }

    @Override
    public void onDisconnected() {
        activityRecognitionClient = null;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(connectionResult.hasResolution()) {
            try{
                connectionResult.startResolutionForResult((Activity) context,
                        ActivityUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            }
            catch (IntentSender.SendIntentException e){

            }
        }
        else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                    connectionResult.getErrorCode(),
                    (Activity) context,
                    ActivityUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

            if(dialog != null)
                dialog.show();
        }
    }
}
