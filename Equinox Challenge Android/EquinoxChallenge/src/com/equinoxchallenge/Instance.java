package com.equinoxchallenge;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;

public class  Instance {
	
	private static Instance instance = null;
	public LocationInfo locationInfo;
	
	public Instance() {
		
	}
	
	public static Instance getSettings() {
    	if(instance == null) {
    		instance = new Instance();
    	}
    	return instance;
    }
	
}
