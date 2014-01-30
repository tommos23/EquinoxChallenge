package com.equinoxchallenge;


import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;



public class PhotoGallery extends Activity {

	private File file[];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_gallery);
		
		File sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File f = new File(sdDir, "Equinox_Challenge");
		file = f.listFiles();
		
		GridView gridview = (GridView) findViewById(R.id.gridview);
	    gridview.setAdapter(new ImageAdapter(this));

	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            Toast.makeText(PhotoGallery.this, "" + position, Toast.LENGTH_SHORT).show();
	        }
	    });

	}
	
	public class ImageAdapter extends BaseAdapter {
	    private Context mContext;
	    /*private Vector<ImageView> mySDCardImages;
	    private Integer[] mThumbIds;*/
	    

	    public ImageAdapter(Context c) {


	    }

	    public int getCount() {
	        return 9;
	    }

	    public Object getItem(int position) {
	        return null;
	    }

	    public long getItemId(int position) {
	        return 0;
	    }

	    // create a new ImageView for each item referenced by the Adapter
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	ImageView imageView;
	    	//Get the dimensions of the View
	        int targetW = 85;
	        int targetH = 85;

	        // Get the dimensions of the bitmap
	        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	        bmOptions.inJustDecodeBounds = true;
	        BitmapFactory.decodeFile(file[position].getAbsolutePath(), bmOptions);
	        int photoW = bmOptions.outWidth;
	        int photoH = bmOptions.outHeight;

	        // Determine how much to scale down the image
	        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

	        // Decode the image file into a Bitmap sized to fill the View
	        bmOptions.inJustDecodeBounds = false;
	        bmOptions.inSampleSize = scaleFactor;
	        bmOptions.inPurgeable = true;

	        Bitmap bitmap = BitmapFactory.decodeFile(file[position].getAbsolutePath(), bmOptions);
	        
	        
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	            imageView = new ImageView(mContext);
	            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
	            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	            imageView.setPadding(8, 8, 8, 8);
	        } else {
	            imageView = (ImageView) convertView;
	        }

	        imageView.setImageBitmap(bitmap);
	    	
			return parent;
	        
	    	
	    }
	        
	}
}
