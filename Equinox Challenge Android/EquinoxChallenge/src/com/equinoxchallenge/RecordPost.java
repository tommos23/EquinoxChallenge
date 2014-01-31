package com.equinoxchallenge;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.telephony.SmsManager;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class RecordPost extends Activity {

	private ProgressBar pB;
	private EditText postNumer;
	private EditText postLetter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_post);
		// Show the Up button in the action bar.
		setupActionBar();
		pB = (ProgressBar)findViewById(R.id.saveProgress);
		postNumer = (EditText)findViewById(R.id.postNumberInput);
		postLetter = (EditText)findViewById(R.id.postLettersInput);	
		pB.setVisibility(View.INVISIBLE);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.record_post, menu);
		return true;
	}
	
	private void openSettings() {
    	Intent intent = new Intent(this, Settings.class);
    	startActivity(intent);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
            openSettings();
            return true;
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	public void savePost(View view) {
		pB.setVisibility(View.VISIBLE);
		String postNum = postNumer.getText().toString();
		String postLet = postLetter.getText().toString();
		if((postNum != null && !postNum.isEmpty()) &&  (postLet != null && !postLet.isEmpty())) {
			DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.UK);
			dateFormatter.setLenient(false);
			Date today = new Date();
			String fixTime = dateFormatter.format(today);
			
			sendSMS("07537410103", postNum + " " + postLet + " " + fixTime);
			Toast.makeText(this, "Sent", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "Please Fill in both values", Toast.LENGTH_LONG).show();
		}
		pB.setVisibility(View.INVISIBLE);		
	}
	
	private void sendSMS(String phoneNumber, String message) {
    	SmsManager sms = SmsManager.getDefault();
    	sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
	

}
