package com.kidguard;

import android.app.Application;


public class MyAppApplication extends Application {

    private static MyAppApplication sInstance;

    public static MyAppApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        sInstance.initializeInstance();

    }

    private void initializeInstance() {

    }

    @Override
    public void onTerminate() {
        // Do your application wise Termination task
        super.onTerminate();
    }
}
