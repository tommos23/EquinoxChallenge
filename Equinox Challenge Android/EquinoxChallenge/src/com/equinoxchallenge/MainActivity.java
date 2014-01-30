package com.equinoxchallenge;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public static final String FILENAME = "equinoxChallengeData";
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    //Click record post button
    public void recordPost(View view) {
    	Intent intent = new Intent(this, RecordPost.class);
    	startActivity(intent);
    }
    
    //Click photos button
    public void photos(View view) {
    	Intent intent = new Intent(this, PhotoGallery.class);
    	startActivity(intent);
    }
    
    public void panicButton(View view) {
    	Intent intent = new Intent(this, PanicButton.class);
    	startActivity(intent);
    }
    
    private void openSettings() {
    	Intent intent = new Intent(this, Settings.class);
    	startActivity(intent);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
        	case R.id.photo_sync:
        		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        		if (mWifi.isConnected()) {
        			Toast.makeText(getBaseContext(), "Photos Synced", Toast.LENGTH_LONG).show();
        		    return true;
        		} else {
        			Toast.makeText(getBaseContext(), "Not Connected to WI-FI", Toast.LENGTH_LONG).show();
        			return true;
        		}
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
}
