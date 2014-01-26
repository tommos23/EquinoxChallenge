package com.equinoxchallenge;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.NavUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends Activity {

    // Handles to UI widgets
    private LocationInfo locationInfo;
    private static final String PHONENUMBER = "phoneNumber";
   	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		// Show the Up button in the action bar.
		setupActionBar();
		if(locationInfo != null){
			updateLocation();
		}
		displayPhoneNumber();
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
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
				//no number found ask 
				EditText phoneText = (EditText)findViewById(R.id.enterPhoneNumberID);
				phoneText.setVisibility(View.VISIBLE);
				Button enterNumber = (Button)findViewById(R.id.phoneEnterButtonID);
				enterNumber.setVisibility(View.VISIBLE);
				TextView phoneN = (TextView) findViewById(R.id.phoneNumberID);
				phoneN.setVisibility(View.GONE);
			} else {
				TextView phoneN = (TextView) findViewById(R.id.phoneNumberID);
				phoneN.setText(mPhoneNumber);
				EditText phoneText = (EditText)findViewById(R.id.enterPhoneNumberID);
				phoneText.setVisibility(View.GONE);
				Button enterNumber = (Button)findViewById(R.id.phoneEnterButtonID);
				enterNumber.setVisibility(View.GONE);
	
				
				editPref.putString(PHONENUMBER,mPhoneNumber);
				editPref.commit();
			}
		}
		else
		{
			TextView phoneN = (TextView) findViewById(R.id.phoneNumberID);
			phoneN.setText(pNumber);
			EditText phoneText = (EditText)findViewById(R.id.enterPhoneNumberID);
			phoneText.setVisibility(View.GONE);
			Button enterNumber = (Button)findViewById(R.id.phoneEnterButtonID);
			enterNumber.setVisibility(View.GONE);
		}
	}
	
	//Click enter button
    public void enterPhoneNumber(View view) {
    	SharedPreferences settings = getSharedPreferences(MainActivity.FILENAME, 0);
		SharedPreferences.Editor editPref = settings.edit();
		
    	EditText phoneText = (EditText)findViewById(R.id.enterPhoneNumberID);
    	phoneText.setVisibility(View.GONE);
    	
    	editPref.putString(PHONENUMBER, phoneText.getText().toString());
    	editPref.commit();
    	
    	TextView phoneN = (TextView) findViewById(R.id.phoneNumberID);
		phoneN.setText(phoneText.getText().toString());
			
		Button enterNumber = (Button)findViewById(R.id.phoneEnterButtonID);
		enterNumber.setVisibility(View.GONE);
    	
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
		String fixTime = dateFormatter.format(today);
		boolean cached = true;
		AsyncHttpClient client = new AsyncHttpClient();
    	client.setBasicAuth("mobileApplication","hupad8uC");
    	client.get("team/location", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject data) {
            	try {
	                boolean ok = data.getBoolean("ok");
	                if (ok) {
                		// saveLocation(Float.toString(locationInfo.lastLat),Float.toString(locationInfo.lastLng),fixTime,false);
	                }
            	}
            	catch (JSONException e) {
            		e.printStackTrace();
            	}
            }
            @Override
            public void onFailure(int statusCode, java.lang.Throwable e, org.json.JSONObject errorResponse) {
            	// saveLocation(Float.toString(locationInfo.lastLat),Float.toString(locationInfo.lastLng),fixTime,true);
            }
        });
	}
	
	//Click start updates button
    public void startUpdates() {
    	try{        	
        	LocationLibrary.initialiseLibrary(getBaseContext(), LocationUtils.FAST_CEILING_IN_SECONDS, LocationUtils.UPDATE_INTERVAL_IN_SECONDS, "com.equinoxchallenge");
        	locationInfo = new LocationInfo(getBaseContext());
        	updateLocation();
        } catch (UnsupportedOperationException ex) {
            Log.d("EquinoxChallenge", "UnsupportedOperationException thrown - the device doesn't have any location providers");
        }    	
    }
    
    public void checkGameStart(View view) throws JSONException {
    	AsyncHttpClient client = new AsyncHttpClient();
    	client.setBasicAuth("mobileApplication","hupad8uC");
    	client.get("game/status", null, new JsonHttpResponseHandler() {
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
	                		// stopUpdates();
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
