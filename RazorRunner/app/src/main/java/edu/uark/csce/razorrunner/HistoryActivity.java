package edu.uark.csce.razorrunner;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.gms.common.internal.m;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class HistoryActivity extends Activity {
	TextView dateText, stepsText, distanceText, durationText, nameText;
	Intent intent;
	private GoogleMap mMap;
	
	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		
		intent = getIntent();
		nameText = (TextView) findViewById(R.id.wOut_Name);
		dateText = (TextView) findViewById(R.id.date_text);
		distanceText = (TextView) findViewById(R.id.distance_text);
		durationText = (TextView) findViewById(R.id.duration_text);
        stepsText = (TextView) findViewById(R.id.steps_text);

		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	
		setTextViews();
        showLocationA();
        showLocationB();
	}
	
	private void setTextViews() {
		// TODO Auto-generated method stub
		String workoutName = intent.getStringExtra("Workout_Name");
		long dateString = intent.getLongExtra("Workout_Date", 0L);		
		String workoutTime = intent.getStringExtra("Workout_Time");
	    String distance = String.valueOf(intent.getFloatExtra("Distance", 0.0f));
        String steps = String.valueOf(intent.getIntExtra("Total_Steps", 0));

		nameText.setText(workoutName);
		
		Date d = new Date(dateString);
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy 'at' hh:mm a");	
		String _dateText = sdf.format(d);
		dateText.setText(_dateText);
		
		distanceText.setText(distance);
        stepsText.setText(steps);
		durationText.setText(workoutTime);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.history, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    public void showLocationA(){
        double lat = intent.getDoubleExtra("Latitude_A", 0.0),
               lng = intent.getDoubleExtra("Longitude_A", 0.0);

        LatLng location = new LatLng(lat, lng);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location,
                16);
        mMap.animateCamera(update);
        mMap.addMarker(new MarkerOptions().position(location).title(
                "Starting Location"));
    }

    public void showLocationB(){
        double lat = intent.getDoubleExtra("Latitude_B", 0.0),
                lng = intent.getDoubleExtra("Longitude_B", 0.0);

        LatLng location = new LatLng(lat, lng);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location,
                16);
        mMap.animateCamera(update);
        mMap.addMarker(new MarkerOptions().position(location).title(
                "Ending Location"));
    }

	public void deleteWorkout(View view)
	{
		intent.putExtra("listPosition", intent.getExtras().getInt("position"));
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

	    builder.setTitle("Confirm");
	    builder.setMessage("Are you sure?");

	    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

	        public void onClick(DialogInterface dialog, int which) {
	            // Do nothing but close the dialog
	        	setResult(RESULT_OK, intent);
	            dialog.dismiss();
	    		finish();

	        }

	    });
	    
	    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	            // Do nothing
	            dialog.dismiss();
	        }
	    });

	    AlertDialog alert = builder.create();
	    alert.show();
	}
	
	public void onBackButton(View view)
	{
		finish();
	}
}
