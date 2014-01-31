package com.equinoxchallenge;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.telephony.SmsManager;
import android.annotation.TargetApi;
import android.os.Build;

public class PanicButton extends Activity {

	private LocationInfo locationInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_panic_button);
		// Show the Up button in the action bar.
		setupActionBar();
		if(locationInfo != null){
			try{        	
	        	LocationLibrary.initialiseLibrary(getBaseContext(),60 * 1000, 2 * 60 * 1000, "com.equinoxchallenge");
	        } catch (UnsupportedOperationException ex) {
	            Log.d("EquinoxChallenge", "UnsupportedOperationException thrown - the device doesn't have any location providers");
	            Toast.makeText(this, "The device doesn't have any location providers", Toast.LENGTH_LONG).show();
	        }
			locationInfo = new LocationInfo(this);
			LocationLibrary.forceLocationUpdate(PanicButton.this);	
		}
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
		getMenuInflater().inflate(R.menu.panic_button, menu);
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
	
	public void sendHelp(View view) {
		if (locationInfo.anyLocationDataReceived()) {
			String locationLat = Float.toString(locationInfo.lastLat);
			String locationLng = Float.toString(locationInfo.lastLong);
			sendSMS("07598782774", "Emerg:" + locationLat + "," + locationLng);
			Toast.makeText(this, "Help is on its way", Toast.LENGTH_LONG).show();
		} else {
			sendSMS("07598782774", "Emerg:NODATA");
			Toast.makeText(this, "Help is on its way", Toast.LENGTH_LONG).show();
		}
	}
	
	private void sendSMS(String phoneNumber, String message) {
    	SmsManager sms = SmsManager.getDefault();
    	sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

}
