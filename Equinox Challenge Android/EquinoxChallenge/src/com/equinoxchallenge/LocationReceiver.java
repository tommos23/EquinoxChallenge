package com.equinoxchallenge;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LocationReceiver extends BroadcastReceiver {

	private Settings set;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("LocationBroadcastReceiver", "onReceive: received location update");
		
		final LocationInfo locationInfo = (LocationInfo) intent.getSerializableExtra(LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);
		Log.d("location", locationInfo.toString());
		set.updateLocation();
	}

}
