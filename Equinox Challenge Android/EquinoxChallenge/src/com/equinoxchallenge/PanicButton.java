package com.equinoxchallenge;

import java.io.DataInputStream;
import java.io.FileInputStream;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.telephony.SmsManager;
import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;

public class PanicButton extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_panic_button);
		// Show the Up button in the action bar.
		setupActionBar();
    	View backgroundimage = findViewById(R.id.panic_view);
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
	
	@SuppressWarnings("deprecation")
	public void sendHelp(View v) {
		 try {
			 FileInputStream fis;
			 String strLine = null;
		     fis = openFileInput(Instance.getSettings(this).LOCATION_FILE);
		     DataInputStream dataIO = new DataInputStream(fis);
		     String fnStr = null;
		     while ((strLine = dataIO.readLine()) != null) {
		    	 fnStr = strLine;
		     }
		     if (fnStr != null) {
		    	 String[] split = fnStr.split(",");
		    	 StringBuilder sb = new StringBuilder();
		    	 for (int i = 0; i < 2; i++) {
		    	     sb.append(split[i]);
		    	     if (i != split.length - 1) {
		    	         sb.append(" ");
		    	     }
		    	 }
		    	 String joined = sb.toString();
		    	 sendSMS("07537410103", "Emerg:" + joined);	 	 
			     Toast.makeText(this, "Help is on its way", Toast.LENGTH_LONG).show();
		     } else if (Instance.getSettings(this).locationInfo != null){
		    	 Instance.getSettings(this).locationInfo.refresh(getBaseContext());
		    	 String lat = Float.toString(Instance.getSettings(this).locationInfo.lastLat);
		    	 String lng = Float.toString(Instance.getSettings(this).locationInfo.lastLong);
		    	 sendSMS("07537410103", "EMERG:" + lat + "," + lng);
		     } else {
		    	 Toast.makeText(this, "Locations must be recorded in settings first", Toast.LENGTH_LONG).show();
		     }
		     dataIO.close();
		     fis.close(); 
		 }
		 catch  (Exception e) {  
		 }
	}
	
	private void sendSMS(String phoneNumber, String message) {
    	SmsManager sms = SmsManager.getDefault();
    	sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

}
