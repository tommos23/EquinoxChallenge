package com.equinoxchallenge;

import java.io.File;
import java.io.FileNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Build;


@SuppressLint("SimpleDateFormat")
public class Photos extends Activity {

	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_TAKE_PHOTO = 1;
	final static String DEBUG_TAG = "MakePhotoActivity";
	private Camera camera;
	private int cameraId = 0;
	private Preview mPreview;
    private static final String PHONENUMBER = "phoneNumber";
	
	// Variables for network detection
	ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	State mobile = conMan.getNetworkInfo(0).getState();
	State wifi = conMan.getNetworkInfo(1).getState();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photos);
		// Show the Up button in the action bar.
		setupActionBar();
		setPreview();
		
    	View backgroundimage = findViewById(R.id.photoView);
    	Drawable background = backgroundimage.getBackground();
    	background.setAlpha(30);
	}
	
	private void setPreview() {
		// do we have a camera?
	    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
	      Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG).show();
	    } else {
	        cameraId = findBackFacingCamera();
	        if (cameraId < 0) {
	          Toast.makeText(this, "No back facing camera found.", Toast.LENGTH_LONG).show();
	        } else {
	          camera = Camera.open(cameraId);
	          // Create our Preview view and set it as the content of our activity.
	          mPreview = new Preview(this, camera);
	          
	          CameraInfo info = new CameraInfo();
		      Camera.getCameraInfo(cameraId, info);

		      camera.setDisplayOrientation(info.orientation);

	          
	          FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
	          preview.addView(mPreview);
	        }
	    }
	}
	
	private int findBackFacingCamera() {
	    int cameraId = -1;
	    // Search for the front facing camera
	    int numberOfCameras = Camera.getNumberOfCameras();
	    for (int i = 0; i < numberOfCameras; i++) {
	      CameraInfo info = new CameraInfo();
	      Camera.getCameraInfo(i, info);
	      if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
	        Log.d(DEBUG_TAG, "Camera found");
	        cameraId = i;
	        break;
	      }
	    }
	    return cameraId;
	  }
	
	public void onClick(View view) {
		PhotoHandler pH = new PhotoHandler(getApplicationContext(), this);
	    camera.takePicture(null, null, pH);
	}
	
	public void galleryStart() {
		Intent intent = new Intent(this, PhotoGallery.class);
	    startActivity(intent);
	}
	
	@Override
	protected void onPause() {
		releaseCameraAndPreview();
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		releaseCameraAndPreview();
		super.onStop();
	}
	
	@Override
	protected void onRestart() {
		releaseCameraAndPreview();
		super.onRestart();
	}
	
	private void releaseCameraAndPreview() {
	    if (camera != null) {
	    	camera.stopPreview();
	    	camera.release();
	    	camera = null;
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
		getMenuInflater().inflate(R.menu.photos, menu);
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
	
	String mCurrentPhotoPath;
	
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	/*public void dispatchTakePictureIntent(View view) {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // Ensure that there's a camera activity to handle the intent
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        // Create the File where the photo should go
	        File photoFile = null;
	        try {
	            photoFile = createImageFile();
	        } catch (IOException ex) {
	            // Error occurred while creating the File
	        }
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photoFile));
	            //startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	        	startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	        	galleryAddPic();
	        }
	    }
	}*/
	
	/*private void galleryAddPic() {
	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    File f = new File(mCurrentPhotoPath);
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);
	}*/
	
	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != RESULT_CANCELED && data != null){
			if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
		        Bundle extras = data.getExtras();
		        Bitmap imageBitmap = (Bitmap) extras.get("data");
		        ImageView photoIcon = (ImageView) findViewById(R.id.photoIcon);
		        photoIcon.setImageBitmap(imageBitmap);
			}
		}else{
			Log.e("photo", "Data empty");
		}
	}*/
	
	public void uploadPhotos() {
		// Check WiFi availability
		// If WiFi upload remaining locations, then photos
		if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
			// For each photo with caption
			File myFile = new File("/path/to/file.png");
			RequestParams params = new RequestParams();
			try {
			    params.put("image", myFile);
			} catch(FileNotFoundException e) {
				// Ignore missing file
			}
			// add parameter caption
			params.put("caption", "caption");
			// add parameter mobileNumber
			SharedPreferences settings = getSharedPreferences(MainActivity.FILENAME, 0);
			String returnedValue = "";
			String pNumber = settings.getString(PHONENUMBER,returnedValue);
			params.put("mobileNumber", pNumber);
			// Make and send the request
	    	APIClient.setBasicAuth("mobileApplication","hupad8uC");
	    	APIClient.post("game/image/upload", params, new JsonHttpResponseHandler() {
	            @Override
	            public void onSuccess(JSONObject data) {
	            	try {
		                boolean ok = data.getBoolean("ok");
		                if (ok) {
		                	boolean evacuate = data.getBoolean("data");
		                	if (evacuate) {
		                		// Switch to the evacuate view
		                		Intent evacuateIntent = new Intent(getApplicationContext(), Evacuate.class);
		                    	startActivity(evacuateIntent);
		                	}
		                }
	            	}
	            	catch (JSONException e) {
	            		e.printStackTrace();
	            	}
	            }
	            @Override
	            public void onFailure(int statusCode, java.lang.Throwable e, org.json.JSONObject errorResponse) {
	            	Toast toast = Toast.makeText(getApplicationContext(), "Photo upload failed.", Toast.LENGTH_SHORT);
            		toast.show();
	            }
	        });
		}
		// If 3G just upload remaining locations
		else if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
		    //mobile
			Toast toast = Toast.makeText(getApplicationContext(), "WiFi must be available for photo uploads.", Toast.LENGTH_SHORT);
    		toast.show();
		}
	}
}
