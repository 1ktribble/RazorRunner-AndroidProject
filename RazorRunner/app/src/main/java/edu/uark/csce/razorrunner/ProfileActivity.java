package edu.uark.csce.razorrunner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class ProfileActivity extends Activity {

	TextView nameText, genderText, weightText, ageText;
//	RadioButton maleRadioButton, femaleRadioButton;
//	Spinner ageSpinner, weightSpinner;
	
	SharedPreferences sharedPreferences;

	static final private int MENU_PREFERENCES = Menu.FIRST + 1;
	private static final int SHOW_PREFERENCES = 1;


	//TODO: Erase just about everything in this file and replace with textviews and shit
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		nameText = (TextView) findViewById(R.id.nameResult);
		ageText = (TextView) findViewById(R.id.ageResult);
		weightText = (TextView) findViewById(R.id.weightResult);
		genderText = (TextView) findViewById(R.id.genderResult);
		
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		updateUIFromPreferences();
		
	}

	private void updateUIFromPreferences() {

		 /** Getting the values stored in the shared object via preference activity */
        boolean userGender = sharedPreferences.getBoolean("prefUserGender", false);
        String userName = sharedPreferences.getString("prefUserName","");
        String userWeight = sharedPreferences.getString("prefUserWeight", "");
        String userAge = sharedPreferences.getString("prefUserAge", "");
 
        /** Setting the values on textview objects to display in the ShowActivity */
        if(userGender)
        	genderText.setText("Male");
        else
        	genderText.setText("Female");
        
        nameText.setText(userName);
        weightText.setText(userWeight);
        ageText.setText(userAge);
      
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.earthquake, menu);
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_preferences);

		return true;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {

		case (MENU_PREFERENCES): {
			Class c = UserSettingsActivity.class;
			Intent i = new Intent(this, c);

			startActivityForResult(i, SHOW_PREFERENCES);
			return true;
		}
		}
		return false;
	}
	
	@SuppressWarnings("rawtypes")
	public void updateProfile(View view)
	{
		Class c = UserSettingsActivity.class;
		Intent i = new Intent(this, c);

		startActivityForResult(i, SHOW_PREFERENCES);
	}
	@Override
	public void onActivityResult(int reqCode, int resCode, Intent data) {
		if(reqCode == SHOW_PREFERENCES)
		{
			updateUIFromPreferences();
		}
	}

	public void backToHome(View view)
	{
		finish();
	}
}
