package com.kidguard;

import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.google.android.gcm.GCMRegistrar;
import com.kidguard.interfaces.Constant;

import io.fabric.sdk.android.Fabric;

@SuppressWarnings("all")
public class MyAppApplication extends MultiDexApplication implements Constant {

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

        /* GCM Registration */
        try {
            GCMRegistrar.checkDevice(this);
            GCMRegistrar.checkManifest(this);
            GCMRegistrar.register(this, GCMIntentService.SENDER_ID);

            /* Fabrics Crashlytics Initialize */
            final Fabric fabric = new Fabric.Builder(this)
                    .kits(new Crashlytics())
                    .debuggable(true).build();
            Fabric.with(fabric);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeInstance() {
    }

    @Override
    public void onTerminate() {
        // Do your application wise Termination task
        super.onTerminate();
    }
}
