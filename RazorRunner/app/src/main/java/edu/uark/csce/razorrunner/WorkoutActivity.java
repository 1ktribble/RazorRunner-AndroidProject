package edu.uark.csce.razorrunner;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;


public class WorkoutActivity extends Activity {

    // Constants that define the activity detection interval

    Criteria criteria;
	TextView stepCountView, distanceView;
	Chronometer chronometer;
	static long timeWhenStopped;

	Button startAndStopButton, saveButton;
	Intent intent;
	WorkoutData wData;
    private ActivityUtils activityUtils;

    private PedometerSettings pedometerSettings;
    boolean isRunning;

    private float mDistanceValue;

    private int numSteps, threshold;
    private String userActivityType;

    LocationManager locationManager;
    double longitudeA, latitudeA;

    private SharedPreferences sharedPreferences;
    private ActivityUtils.REQUEST_TYPE request_type;
    private RequestDetection requestDetection;
    private RemoveDetection removeDetection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_workout);

        setCriteria();
        getLocationInformation();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setupUtilities();

        threshold = 10;
        numSteps = 0;
        mDistanceValue = 0.0f;

        requestDetection = new RequestDetection(this);
        removeDetection = new RemoveDetection(this);

        wData = new WorkoutData("" , "", 0L, 0, 0.0f, 0.0, 0.0, 0.0, 0.0);
        distanceView = (TextView) findViewById(R.id.distanceCount);
		stepCountView = (TextView) findViewById(R.id.stepCount);
		chronometer = (Chronometer) findViewById(R.id.chronometer1);
		timeWhenStopped = 0;
		startAndStopButton = (Button) findViewById(R.id.startAndStop);
		saveButton = (Button) findViewById(R.id.backButton);

		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clickHandler();
			}
		});
	}

    @Override
    protected void onStart() {
        Log.i(ActivityUtils.APPTAG, "[Activity] onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i(ActivityUtils.APPTAG, "[Activity] onResume");
        super.onResume();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        pedometerSettings = new PedometerSettings(sharedPreferences);

        isRunning = pedometerSettings.isServiceRunning();

        if(!isRunning && pedometerSettings.isNewStart()){
            startStepService();
            bindStepService();
        }
        else if(isRunning){
            bindStepService();
        }

        pedometerSettings.clearServiceRunning();

        stepCountView = (TextView) findViewById(R.id.stepCount);


    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

    private static final int MENU_RESET = 1;
    private static final int MENU_SETTINGS = 8;
    private static final int MENU_QUIT     = 9;


    /* Creates the menu items */
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();

        menu.add(0, MENU_RESET, 0, "Reset Workout")
                .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
                .setShortcut('2', 'r');
        menu.add(0, MENU_SETTINGS, 0, "Settings")
                .setIcon(android.R.drawable.ic_menu_preferences)
                .setShortcut('8', 's')
                .setIntent(new Intent(this, UserSettingsActivity.class));
        menu.add(0, MENU_QUIT, 0, "Quit")
                .setIcon(android.R.drawable.ic_lock_power_off)
                .setShortcut('9', 'q');
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case MENU_RESET:
                resetValues(true);
                return true;
            case MENU_QUIT:
                unbindStepService();
                stopStepService();
                finish();
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent intent)
    {
        switch (reqCode){
            case ActivityUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :
                switch (resCode){
                    case Activity.RESULT_OK :
                        if(request_type == ActivityUtils.REQUEST_TYPE.ADD)
                        {
                            requestDetection.requestUpdates();
                        }
                        else if(request_type == ActivityUtils.REQUEST_TYPE.REMOVE)
                        {
                            removeDetection.removeUpdates(
                                    requestDetection.getRequestPendingIntent());
                        }
                        break;

                    default:

                }
                default:
                    break;
        }
    }

    public void setupUtilities(){
        activityUtils = ActivityUtils.getInstance();
    }

    public boolean servicesConnected(){
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if(resultCode == ConnectionResult.SUCCESS){
           return true;
        } else{
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0).show();
            return false;
        }
    }
    public void enableAccelerometerListening(){
        onStartUpdates();
    }
    public void disableAccelerometerListening(){
        onStopUpdates();
    }
    public void onStartUpdates()
    {
        if(!servicesConnected()){
            return;
        }

        request_type = ActivityUtils.REQUEST_TYPE.ADD;
        requestDetection.requestUpdates();

        startStepService();
        bindStepService();
    }
    public void onStopUpdates()
    {
        if(!servicesConnected())
        {
            return;
        }

        request_type = ActivityUtils.REQUEST_TYPE.REMOVE;
        removeDetection.removeUpdates(requestDetection.getRequestPendingIntent());
        requestDetection.getRequestPendingIntent().cancel();

        unbindStepService();
        stopStepService();
    }
    public void clickHandler()
    {
        if(timeWhenStopped != 0 && longitudeA != 0.0 && latitudeA != 0.0)
        {
            timeWhenStopped = Math.abs(timeWhenStopped);
            int millis = (int) (timeWhenStopped % 1000);
            int seconds = (int) (timeWhenStopped / 1000) % 60;
            int minutes = (int) ((timeWhenStopped / (1000 * 60)) % 60);
            StringBuilder sb = new StringBuilder();
            intent = getIntent();
            Bundle bundle = new Bundle();
            sb.append((char)('0' + minutes / 10))
                    .append((char)('0' + minutes % 10)).append(":")
                    .append((char)('0' + seconds / 10))
                    .append((char)('0' + seconds % 10)).append(":")
                    .append((char)('0' + millis / 100))
                    .append((char)('0' + millis % 10));
            wData.setWorkoutTime(sb.toString());
            wData.setLatitudeA(latitudeA);
            wData.setLongitudeA(longitudeA);
            wData.setDistance(mDistanceValue);
            wData.setSteps(numSteps);
            bundle.putParcelable("parcelable.data", (Parcelable) wData);
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            resetValues(true);
            finish();
        }
    }

    public void getLocationInformation(){
        locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        String provider = locationManager.getBestProvider(criteria, true);
        Location l =
                locationManager.getLastKnownLocation(provider);

        updateWithNewLocation(l);
        locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);
    }

    public void setCriteria(){
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
    }

    private void updateWithNewLocation(Location loc){
        if(loc != null){
                    latitudeA = loc.getLatitude();
                    longitudeA = loc.getLongitude();
            }

        // On location change, find out if the user is walking, running , etc.
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userActivityType =
                sharedPreferences.getString(ActivityUtils.KEY_PREVIOUS_ACTIVITY_TYPE, "");
            //LatLng location = new LatLng(loc.getLatitude(), loc.getLongitude());
       // Toast.makeText(this, userActivityType, Toast.LENGTH_SHORT).show();

    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {}
    };

	public void startAndStopTimer(View v) {
			if(startAndStopButton.getText() == "Pause")
			{
				chronometer.stop();
				timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
				startAndStopButton.setText("Start");

                getLocationInformation();
                locationManager.removeUpdates(locationListener);

                disableAccelerometerListening();

            }
			else// on next button press
			{
				chronometer.start();
                chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
				startAndStopButton.setText("Pause");

            // if location is not already set
                getLocationInformation();
                enableAccelerometerListening();
            }
    }
	
	public void cancel(View view) {
	    finish();
	}

    private UserStepRecognition mService;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((UserStepRecognition.StepBinder)service).getService();

            mService.registerCallback(mCallback);
            mService.reloadSettings();

        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    private static final int STEPS_MSG = 1;
    private static final int DISTANCE_MSG = 2;


    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case STEPS_MSG:
                    numSteps = (int)msg.arg1;
                    stepCountView.setText("" + numSteps);
                    break;
                case DISTANCE_MSG:
                    mDistanceValue = ((int)msg.arg1)/1000f;
                    if (mDistanceValue <= 0) {
                        distanceView.setText("0");
                    }
                    else {
                        distanceView.setText(
                                ("" + (mDistanceValue + 0.000001f)).substring(0, 5)
                        );
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

    };

    private UserStepRecognition.ICallback mCallback = new UserStepRecognition.ICallback() {
        public void stepsChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
        }
        public void distanceChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int)(value*1000), 0));
        }
    };

    private void startStepService() {
        if (! isRunning) {
            Log.i(ActivityUtils.APPTAG, "[SERVICE] Start");
            isRunning = true;
            startService(new Intent(WorkoutActivity.this,
                    UserStepRecognition.class));
        }
    }

    private void bindStepService() {
        Log.i(ActivityUtils.APPTAG, "[SERVICE] Bind");
        bindService(new Intent(WorkoutActivity.this,
                UserStepRecognition.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }

    private void unbindStepService() {
        Log.i(ActivityUtils.APPTAG, "[SERVICE] Unbind");
        unbindService(mConnection);
    }

    private void stopStepService() {
        Log.i(ActivityUtils.APPTAG, "[SERVICE] Stop");
        if (mService != null) {
            Log.i(ActivityUtils.APPTAG, "[SERVICE] stopService");
            stopService(new Intent(WorkoutActivity.this,
                    UserStepRecognition.class));
        }
        isRunning = false;
    }

    private void resetValues(boolean updateDisplay) {
        if (mService != null && isRunning) {
            mService.resetValues();
        }
        else {
            stepCountView.setText("0");
            distanceView.setText("0");
            SharedPreferences state = getSharedPreferences("state", 0);
            SharedPreferences.Editor stateEditor = state.edit();
            if (updateDisplay) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();

                stateEditor.putInt("steps", 0);
                stateEditor.putFloat("distance", 0);
                stateEditor.commit();
            }
        }
    }
}
