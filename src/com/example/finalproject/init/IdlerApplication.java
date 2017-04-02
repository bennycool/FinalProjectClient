package com.example.finalproject.init;

import com.baidu.mapapi.SDKInitializer;

import android.app.Application;

public class IdlerApplication extends Application{

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		SDKInitializer.initialize(this);
	}
	
}
