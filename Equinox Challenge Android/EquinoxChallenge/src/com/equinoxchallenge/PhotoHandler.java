package com.equinoxchallenge;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import android.graphics.Interpolator.Result;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class PhotoHandler implements PictureCallback {

	private final Context context;
	private Photos phot;

	public PhotoHandler(Context context, Photos p) {
		this.context = context;
	    this.phot = p;
	}
	
	@Override
	public void onPictureTaken(byte[] data, Camera arg1) {
		new savePhoto(context, phot, data, arg1).execute("");
	}
		
}

class savePhoto extends AsyncTask<String, Void, String> {

	private final Context context;
	private Photos phot;
	private byte[] data;
	private Camera arg1;
	
	
	public savePhoto(Context context, Photos p, byte[] d, Camera c) {
		this.context = context;
	    this.phot = p;
	    this.data = d;
	    this.arg1 = c;
	}
	
	@Override
	protected String doInBackground(String... params) {
		File pictureFileDir = getDir();

	    if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
	      Toast.makeText(context, "Can't create directory to save image.", Toast.LENGTH_LONG).show();
	      cancel(true);
	      return "failed to save";
	    }
	    
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
	    String date = dateFormat.format(new Date());
	    String photoFile = "IMG_" + date + ".jpg";
	    
	    String filename = pictureFileDir.getPath() + File.separator + photoFile;
	    File pictureFile = new File(filename);
	    
	    try {
	        FileOutputStream fos = new FileOutputStream(pictureFile);
	        fos.write(data);
	        fos.close(); 
	      } catch (Exception error) {
	        cancel(true);     
	      } 
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Toast.makeText(context, "New Image saved", Toast.LENGTH_LONG).show();
		Intent intent = new Intent(context.getApplicationContext(), PhotoGallery.class);
		context.getApplicationContext().startActivity(intent);  	
	}
	
	protected void onCancelled(Result r) {
		Toast.makeText(context, "Image not saved", Toast.LENGTH_LONG).show();
	}
	
	private File getDir() {
	    File sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	    return new File(sdDir, "Equinox_Challenge");
	}
	
}
