package com.kidguard;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.android.gcm.GCMRegistrar;
import com.kidguard.interfaces.Constant;
import com.kidguard.utilities.Utilities;

import io.fabric.sdk.android.Fabric;

@SuppressWarnings("all")
public class MyAppApplication extends Application implements Constant {

    private static MyAppApplication sInstance;

    public static MyAppApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        sInstance.initializeInstance();

        //Utilities.makeDatabaseFolder();

        // GCM Registration
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        GCMRegistrar.register(this, GCMIntentService.SENDER_ID);
        Fabric.with(this, new Crashlytics());
    }

    private void initializeInstance() {
    }

    @Override
    public void onTerminate() {
        // Do your application wise Termination task
        super.onTerminate();
    }
}
