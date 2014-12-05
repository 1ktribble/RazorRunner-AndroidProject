package edu.uark.csce.razorrunner;

import java.util.ArrayList;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OverviewActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{	
	
	Intent intent;
    Criteria criteria;
	ArrayList<WorkoutData> workoutList;
	WorkoutData workout;
    private double latitudeB, longitudeB;
	private static final int Workouts = 1;
	private static final int List = 2;
	private WorkoutItemAdapter adapter;
	boolean gender;
	int workoutNum;
	String userName, weight, age;
	static final private int MENU_PREFERENCES = Menu.FIRST + 1;
	private static final int SHOW_PREFERENCES = 3;
	//WorkoutData workout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overview);
		
		if(workoutList == null)
			workoutList = new ArrayList<WorkoutData>();
		
		
		
		intent = getIntent();	
		//adapter.notifyDataSetChanged();
		LoadPreferences();	
		LoadListView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getLoaderManager().restartLoader(0, null, this);
		LoadPreferences();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_preferences);
		return true;
		}
	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {

		case (MENU_PREFERENCES): {
			@SuppressWarnings("rawtypes")
			Class c = UserSettingsActivity.class;
			Intent i = new Intent(this, c);

			startActivityForResult(i, SHOW_PREFERENCES);
			return true;
		}
		}
		return false;	
	};
	
	private void LoadListView() {
		
		ListView lv = (ListView) findViewById(android.R.id.list);
		adapter = new WorkoutItemAdapter(this, R.layout.workout_item, workoutList);
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				intent = new Intent(OverviewActivity.this, HistoryActivity.class);
				intent.putExtra("position", position);
				intent.putExtra("Workout_Date", workoutList.get(position).getDate());
				intent.putExtra("Workout_Name", workoutList.get(position).getWorkoutName());
				intent.putExtra("Workout_Time", workoutList.get(position).getWorkoutTime());
                intent.putExtra("Latitude_A", workoutList.get(position).getLatitudeA());
                intent.putExtra("Latitude_B", workoutList.get(position).getLatitudeB());
                intent.putExtra("Longitude_A", workoutList.get(position).getLongitudeA());
                intent.putExtra("Longitude_B", workoutList.get(position).getLongitudeB());
                intent.putExtra("Distance", workoutList.get(position).getDistance());
                intent.putExtra("Total_Steps", workoutList.get(position).getSteps());

                setResult(RESULT_OK, intent);
				startActivityForResult(intent, List);
			}
		});
	}

	
	private void LoadPreferences()
	{		
	        SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(this);
	       
	        /** Getting the values stored in the shared object via preference activity */
	        gender	= sharedPreferences.getBoolean("prefUserGender", false);
	        userName = sharedPreferences.getString("prefUserName","");
	        weight = sharedPreferences.getString("prefUserWeight", "");
	        age = sharedPreferences.getString("prefUserAge", "");    
	        
	        TextView textView = (TextView) findViewById(R.id.nameText);
	        textView.setText(userName);	              
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

    public void getLocationInformation(){
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        String provider = locationManager.getBestProvider(criteria, true);
        Location l =
                locationManager.getLastKnownLocation(provider);

        updateWithNewLocation(l);
       // locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);
    }

    private void updateWithNewLocation(Location loc){
        if(loc != null){
            latitudeB = loc.getLatitude();
            longitudeB = loc.getLongitude();
        }
        //LatLng location = new LatLng(loc.getLatitude(), loc.getLongitude());
    }

	@Override
	public void onActivityResult(int reqCode, int resCode, Intent data) {
		super.onActivityResult(reqCode, resCode, data);
		switch (reqCode) {
		case (Workouts): {
			if (resCode == Activity.RESULT_OK && data != null) {
				//lv.setFastScrollAlwaysVisible(true);

				workoutNum++;

                setCriteria();
                getLocationInformation();

				workout = (WorkoutData) data.getParcelableExtra("parcelable.data");


                workout.setWorkoutName("Workout " + workoutNum);
				workout.setDate(System.currentTimeMillis());
                workout.setLatitudeB(latitudeB);
                workout.setLongitudeB(longitudeB);

				ContentResolver cr = getContentResolver();
				ContentValues values = new ContentValues();
				
				values.put(WorkoutContentProvider.KEY_NAME, workout.getWorkoutName());
				values.put(WorkoutContentProvider.KEY_DATE, workout.getDate());
				values.put(WorkoutContentProvider.KEY_DURATION, workout.getWorkoutTime());
                values.put(WorkoutContentProvider.KEY_DISTANCE, String.valueOf(workout.getDistance()));
                values.put(WorkoutContentProvider.KEY_STEPS, workout.getSteps());
				values.put(WorkoutContentProvider.KEY_LAT_A, String.valueOf(workout.getLatitudeA()));
                values.put(WorkoutContentProvider.KEY_LONG_A, String.valueOf(workout.getLongitudeA()));
                values.put(WorkoutContentProvider.KEY_LAT_B, String.valueOf(workout.getLatitudeB()));
                values.put(WorkoutContentProvider.KEY_LONG_B, String.valueOf(workout.getLongitudeB()));

				cr.insert(WorkoutContentProvider.CONTENT_URI, values);
												
				getLoaderManager().restartLoader(0, null, this);
			}
			break;
		}
		case(List): {
			if(resCode == Activity.RESULT_OK)
			{
				ContentResolver cr = getContentResolver();
				int list_position = intent.getExtras().getInt("listPosition");
				String tempTime = workoutList.get(list_position).getWorkoutTime();
				String tempName = workoutList.get(list_position).getWorkoutName();
				
				int deleted = cr.delete(WorkoutContentProvider.CONTENT_URI,
						WorkoutContentProvider.KEY_DURATION + " = ? AND " +
                                WorkoutContentProvider.KEY_NAME + " = ? ",
						new String[]{tempTime, tempName});
				
				Toast.makeText(this, deleted + " Workout Removed", Toast.LENGTH_SHORT).show();
//				getLoaderManager().restartLoader(0, null, this);
			}
		}
		default:
			break;
		}
	}	
	public void openWorkoutActivity(View view)
	{
		intent = new Intent(OverviewActivity.this, WorkoutActivity.class);
		
		startActivityForResult(intent, Workouts);
	}
	public void openProfileActivity(View view)
	{
		intent = new Intent(OverviewActivity.this, ProfileActivity.class);
		startActivity(intent);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		CursorLoader loader = new CursorLoader(this,
				WorkoutContentProvider.CONTENT_URI,
				null, null, null, WorkoutContentProvider.KEY_DATE + " DESC");
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub
		int keyWorkoutName = data.getColumnIndexOrThrow(WorkoutContentProvider.KEY_NAME);
		int keyWorkoutDuration = data.getColumnIndexOrThrow(WorkoutContentProvider.KEY_DURATION);
        int keyWorkoutDate = data.getColumnIndexOrThrow(WorkoutContentProvider.KEY_DATE);
        int keyWorkoutSteps = data.getColumnIndexOrThrow(WorkoutContentProvider.KEY_STEPS);
        int keyWorkoutDistance = data.getColumnIndexOrThrow(WorkoutContentProvider.KEY_DISTANCE);
        int keyWorkoutLatA = data.getColumnIndexOrThrow(WorkoutContentProvider.KEY_LAT_A);
        int keyWorkoutLatB = data.getColumnIndexOrThrow(WorkoutContentProvider.KEY_LAT_B);
        int keyWorkoutLongA = data.getColumnIndexOrThrow(WorkoutContentProvider.KEY_LONG_A);
        int keyWorkoutLongB = data.getColumnIndexOrThrow(WorkoutContentProvider.KEY_LONG_B);

        //TODO: create keys for longitude and latitudes.
		workoutList.clear();
		
		while(data.moveToNext()){
			WorkoutData tempWorkout = new WorkoutData(data.getString(keyWorkoutName),
                    data.getString(keyWorkoutDuration), data.getLong(keyWorkoutDate),
                    data.getInt(keyWorkoutSteps), data.getFloat(keyWorkoutDistance),
                    data.getDouble(keyWorkoutLatA), data.getDouble(keyWorkoutLatB),
                    data.getDouble(keyWorkoutLongA), data.getDouble(keyWorkoutLongB));
			workoutList.add(tempWorkout);
		}
		
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		
	}
}