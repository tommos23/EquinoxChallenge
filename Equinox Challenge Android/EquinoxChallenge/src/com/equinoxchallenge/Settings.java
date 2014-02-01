package com.equinoxchallenge;

import java.util.Date;
import java.util.Locale;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends Activity {

    // Handles to UI widgets
    public LocationInfo locationInfo;
    private static final String PHONENUMBER = "phoneNumber";
    private String fixTime;
   	public static final String LOCATION_FILE = "locationFile"; 
    
    public Settings() {
    	if(locationInfo != null){
			updateLocation();
		}
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		// Show the Up button in the action bar.
		setupActionBar();
		if(locationInfo != null){
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
		String pNumber = settings.getString(PHONENUMBER,returnedValue);
		if(pNumber.isEmpty())
		{
			String mPhoneNumber = ((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
			if(mPhoneNumber == null || mPhoneNumber.isEmpty()) {
				//no number found
			} else {
				EditText phoneText = (EditText)findViewById(R.id.enterPhoneNumberID);
				phoneText.setText(mPhoneNumber);

				editPref.putString(PHONENUMBER,mPhoneNumber);
				editPref.commit();
			}
		}
		else
		{
			EditText phoneText = (EditText)findViewById(R.id.enterPhoneNumberID);
			phoneText.setText(pNumber);
		}
	}
	
	//Click enter button
    public void enterPhoneNumber(View view) {
    	SharedPreferences settings = getSharedPreferences(MainActivity.FILENAME, 0);
		SharedPreferences.Editor editPref = settings.edit();
		
    	EditText phoneText = (EditText)findViewById(R.id.enterPhoneNumberID);
    	
    	editPref.putString(PHONENUMBER, phoneText.getText().toString());
    	editPref.commit();
    	
    	Toast toast = Toast.makeText(getApplicationContext(), "Phone number saved.", Toast.LENGTH_SHORT);
		toast.show();
    }
	
	
	public void updateLocation() {
		locationInfo.refresh(getBaseContext());
		String locationLat = Float.toString(locationInfo.lastLat);
		TextView lat = (TextView) findViewById(R.id.latText);
		lat.setText(locationLat);
		String locationLng = Float.toString(locationInfo.lastLong);
		TextView lng = (TextView) findViewById(R.id.longText);
		lng.setText(locationLng);
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.UK);
		dateFormatter.setLenient(false);
		Date today = new Date();
		fixTime = dateFormatter.format(today);
		boolean cached = true;
		RequestParams params = new RequestParams();
		// add parameter mobileNumber
		SharedPreferences settings = getSharedPreferences(MainActivity.FILENAME, 0);
		String returnedValue = "";
		String pNumber = settings.getString(PHONENUMBER,returnedValue);
		params.put("mobileNumber", pNumber);
		params.put("longitude", locationLng);
		params.put("latitude", locationLat);
		params.put("fixTime", fixTime);
		AsyncHttpClient client = new AsyncHttpClient();
    	APIClient.setBasicAuth("mobileApplication","hupad8uC");
    	APIClient.post("team/location", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject data) {        		
            	try {
	                boolean ok = data.getBoolean("ok");
	                if (ok) {
	                	saveLocation(Float.toString(locationInfo.lastLat),Float.toString(locationInfo.lastLong),fixTime,false);
	                }
            	}
            	catch (JSONException e) {
            		e.printStackTrace();
            	}
            }
           
			@Override
            public void onFailure(int statusCode, java.lang.Throwable e, org.json.JSONObject errorResponse) {
            	saveLocation(Float.toString(locationInfo.lastLat),Float.toString(locationInfo.lastLong),fixTime,true);
            }
        });
	}
	
	 private void saveLocation(String lat, String lng, String fixTime, boolean fail) {
		 if (fail) {
			 try {
				String csv = lat + "," + lng + "," + fixTime + System.getProperty("line.separator");
				FileOutputStream fos = openFileOutput(LOCATION_FILE, MODE_APPEND);
				fos.write(csv.getBytes());
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		 }
				 
	}
	
	//Click start updates button
    public void startUpdates() {
    	try{        	
        	LocationLibrary.initialiseLibrary(getBaseContext(), LocationUtils.FAST_CEILING_IN_SECONDS, LocationUtils.UPDATE_INTERVAL_IN_SECONDS, "com.equinoxchallenge");
        	locationInfo = new LocationInfo(getBaseContext());
        	updateLocation();
        } catch (UnsupportedOperationException ex) {
        }    	
    }
    
    public void stopUpdates() {
    	locationInfo = null;
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
