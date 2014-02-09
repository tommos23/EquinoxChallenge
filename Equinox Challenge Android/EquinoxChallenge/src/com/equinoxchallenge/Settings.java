package com.equinoxchallenge;

import java.util.Date;
import java.util.Locale;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;

import org.json.*;
import com.loopj.android.http.*;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends Activity {
  
    public Settings() {
    	
    }  
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		// Show the Up button in the action bar.
		setupActionBar();
		if(Instance.getSettings(this).locationInfo != null){
			updateLocation();
		}
		displayPhoneNumber();
    	View backgroundimage = findViewById(R.id.settings_view);
    	Drawable background = backgroundimage.getBackground();
    	background.setAlpha(30);
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
		return true;
	}
	
	private void openHome() {
    	Intent intent = new Intent(this, MainActivity.class);
    	startActivity(intent);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			openHome();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void displayPhoneNumber() {
		SharedPreferences settings = getSharedPreferences(MainActivity.FILENAME, 0);
		SharedPreferences.Editor editPref = settings.edit();
		String returnedValue = "";
		String pNumber = settings.getString(Instance.PHONENUMBER,returnedValue);
		if(pNumber.isEmpty())
		{
			String mPhoneNumber = ((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
			if(mPhoneNumber == null || mPhoneNumber.isEmpty()) {
				//no number found
			} else {
				EditText phoneText = (EditText)findViewById(R.id.enterPhoneNumberID);
				phoneText.setText(mPhoneNumber);
				editPref.putString(Instance.PHONENUMBER,mPhoneNumber);
				editPref.commit();
				Instance.getSettings(this).enteredNumber = mPhoneNumber;
			}
		}
		else
		{
			EditText phoneText = (EditText)findViewById(R.id.enterPhoneNumberID);
			phoneText.setText(pNumber);
			Instance.getSettings(this).enteredNumber = pNumber;
		}
	}
	
	//Click enter button
    public void enterPhoneNumber(View view) {
    	SharedPreferences settings = getSharedPreferences(MainActivity.FILENAME, 0);
		SharedPreferences.Editor editPref = settings.edit();
		
    	EditText phoneText = (EditText)findViewById(R.id.enterPhoneNumberID);
    	
    	editPref.putString(Instance.getSettings(this).PHONENUMBER, phoneText.getText().toString());
    	editPref.commit();
    	Instance.getSettings(this).enteredNumber = phoneText.getText().toString();
    	Toast toast = Toast.makeText(getApplicationContext(), "Phone number saved.", Toast.LENGTH_SHORT);
		toast.show();
    }
	
	
	public void updateLocation() {
		Instance.getSettings(this).locationInfo.refresh(getBaseContext());
		String locationLat = Float.toString(Instance.getSettings(this).locationInfo.lastLat);
		TextView lat = (TextView) findViewById(R.id.latText);
		lat.setText(locationLat);
		String locationLng = Float.toString(Instance.getSettings(this).locationInfo.lastLong);
		TextView lng = (TextView) findViewById(R.id.longText);
		lng.setText(locationLng);
		Instance.getSettings(this).sendToServer();
	}
	
	//Click start updates button
    public void startUpdates() {
    	try{        	
        	LocationLibrary.initialiseLibrary(getBaseContext(), LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS, LocationUtils.TOP_LIMIT_IN_MILLISECONDS, "com.equinoxchallenge");
        	LocationLibrary.useFineAccuracyForRequests(true);
        	//LocationLibrary.startAlarmAndListener(getBaseContext());
        	Instance.getSettings(this).locationInfo = new LocationInfo(getBaseContext());
        	LocationLibrary.startAlarmAndListener(getBaseContext());
        	
        	updateLocation();
        } catch (UnsupportedOperationException ex) {
        	Toast.makeText(getApplicationContext(), "There was a problem, you do not have location turned on.", Toast.LENGTH_LONG).show();
        }    	
    }
    
    public void stopUpdates() {
    	LocationLibrary.stopAlarmAndListener(getBaseContext());
    	Instance.getSettings(this).locationInfo = null;
    }
    
    public void checkGameStart(View view) throws JSONException {
    	 APIClient.setBasicAuth("mobileApplication","hupad8uC");
    	 APIClient.post("game/status", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject data) {
            	try {
	                boolean ok = data.getBoolean("ok");
	                if (ok) {
	                	boolean gameRunning = data.getBoolean("data");
	                	if (gameRunning) {
	                		startUpdates();
	                		Toast toast = Toast.makeText(getApplicationContext(), "The game is running, tracking enabled.", Toast.LENGTH_SHORT);
	                		toast.show();
	                	}
	                	else {
	                		stopUpdates();
	                		Toast toast = Toast.makeText(getApplicationContext(), "The game isn't running, tracking disabled.", Toast.LENGTH_SHORT);
	                		toast.show();
	                	}
	                }
	                else {
	                	Toast toast = Toast.makeText(getApplicationContext(), "There was a problem, tracking has been left in its previous state.", Toast.LENGTH_SHORT);
                		toast.show();
	                }
            	}
            	catch (JSONException e) {
            		e.printStackTrace();
            	}
            }
            @Override
            public void onFailure(int statusCode, java.lang.Throwable e, org.json.JSONObject errorResponse) {
            	Toast toast = Toast.makeText(getApplicationContext(), "There was a problem, tracking has been left in its previous state.", Toast.LENGTH_SHORT);
        		toast.show();
            }
        });
    }
    
}
