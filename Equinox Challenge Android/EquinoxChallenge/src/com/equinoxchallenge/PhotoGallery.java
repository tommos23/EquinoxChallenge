package com.equinoxchallenge;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class PhotoGallery extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_gallery);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photo_gallery, menu);
		return true;
	}

}
