package com.equinoxchallenge;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class  Instance {
	
	private static Instance instance = null;
	public LocationInfo locationInfo;
	public String pFileName;
	public static final String PHONENUMBER = "phoneNumber";
	public String enteredNumber = null;
	public static final String LOCATION_FILE = "locationFile"; 
	public String fixTime;
	private static Context cont;
	
	public Instance() {
		
	}
	
	public static Instance getSettings(Context ctx) {
    	if(instance == null) {
    		instance = new Instance();
    	}
    	cont = ctx;
    	return instance;
    }
	
	public void showPhoto() {
		Intent intent = new Intent(cont, PhotoComment.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	cont.startActivity(intent);
	}
	
	public void sendToServer() {
		locationInfo.refresh(cont);
		String locationLng = Float.toString(locationInfo.lastLong);
		String locationLat = Float.toString(locationInfo.lastLat);
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.UK);
		dateFormatter.setLenient(false);
		Date today = new Date();
		fixTime = dateFormatter.format(today);
		RequestParams params = new RequestParams();
		params.put("mobileNumber", enteredNumber);
		params.put("longitude", locationLng);
		params.put("latitude", locationLat);
		params.put("fixTime", fixTime);
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
		 try {
			String csv = lat + "," + lng + "," + fixTime + "," + fail + System.getProperty("line.separator");
			FileOutputStream fos = cont.openFileOutput(LOCATION_FILE, Context.MODE_APPEND);
			fos.write(csv.getBytes());
			fos.close();
		 } catch (IOException e) {
			e.printStackTrace();
		 }				 
	 }
	
}
