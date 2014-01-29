package com.equinoxchallenge;

import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

public class PhotoGallery extends Activity {

	private ImageView img = (ImageView)findViewById(R.id.takenPhoto);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_gallery);
		//setPic();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photo_gallery, menu);
		return true;
	}
	
	private void setPic() {
		final ContentResolver cr = getContentResolver();
	    final String[] p1 = new String[] {
	            MediaStore.Images.ImageColumns._ID,
	            MediaStore.Images.ImageColumns.DATE_TAKEN
	    };
	    Cursor c1 = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, p1, null, null,
	            p1[1] + " DESC");

	    if ( c1.moveToFirst() ) {
	    	String photoLocation = c1.getString(1);
	    	// Get the dimensions of the View
		    int targetW = img.getWidth();
		    int targetH = img.getHeight();

		    // Get the dimensions of the bitmap
		    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		    bmOptions.inJustDecodeBounds = true;
		    BitmapFactory.decodeFile(photoLocation, bmOptions);
		    int photoW = bmOptions.outWidth;
		    int photoH = bmOptions.outHeight;

		    // Determine how much to scale down the image
		    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

		    // Decode the image file into a Bitmap sized to fill the View
		    bmOptions.inJustDecodeBounds = false;
		    bmOptions.inSampleSize = scaleFactor;
		    bmOptions.inPurgeable = true;

		    Bitmap bitmap = BitmapFactory.decodeFile(photoLocation, bmOptions);
		    img.setImageBitmap(bitmap);
	    }
	    c1.close();
  
	}
	
	public void saveComment(View v) {
		
	}
	
	public void deletePhoto(View v) {
		
	}

}
