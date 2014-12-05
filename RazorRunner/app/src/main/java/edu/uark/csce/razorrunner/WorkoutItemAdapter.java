package edu.uark.csce.razorrunner;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WorkoutItemAdapter extends ArrayAdapter<WorkoutData>{
	
	int resource;	
	public WorkoutItemAdapter(Context context, int resource,
			ArrayList<WorkoutData> workoutList) {
		super(context, resource, workoutList);
		// TODO Auto-generated constructor stub
		
		this.resource = resource;
	}
		
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout workoutView;
		
		
		WorkoutData item = getItem(position);
		
		String workoutName = item.getWorkoutName();
		String workoutTime = item.getWorkoutTime();
		
		//Date createdDate = item.getCreatedDate();
		//SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		//String dateString = sdf.format(createdDate);
		
		if (convertView == null) {
			workoutView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater lifr = (LayoutInflater)getContext().getSystemService(inflater);
			lifr.inflate(resource, workoutView, true);
		}
		else {
			workoutView = (LinearLayout)convertView;
		}
		
		TextView workoutNameView = (TextView)workoutView.findViewById(R.id.workout_col);
		TextView workoutTimeView = (TextView)workoutView.findViewById(R.id.time_col);
		
		workoutNameView.setText(workoutName);
		workoutTimeView.setText(workoutTime);
		
		return workoutView;
	}	
}