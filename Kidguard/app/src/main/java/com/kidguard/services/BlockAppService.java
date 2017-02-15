package com.kidguard.services;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.kidguard.asynctask.ForegroundCheckAsyncTask;
import com.kidguard.interfaces.Constant;
import com.kidguard.utilities.Utilities;
import com.rvalerio.fgchecker.AppChecker;

import java.util.concurrent.ExecutionException;

@SuppressWarnings("all")
public class BlockAppService extends Service implements Constant {

    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;
    @SuppressWarnings("FieldCanBeLocal")
    private String tag;
    @SuppressWarnings("FieldCanBeLocal")
    private String packageName;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //tag = intent.getStringExtra(KEY_TAG);
        //packageName = intent.getStringExtra(KEY_PACKAGE_NAME);

        /* Get Data With Tag */
        if (tag != null && !tag.equals("")) {
            //Log.e("tag", "tag??" + tag);
            //Log.e("packageName", "packageName??" + packageName);
        }

        if (!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }

    private Runnable myTask = new Runnable() {
        public void run() {
            // Log.e("Running", "Running");

//            if (!Utilities.isNetworkAvailable(context)) {
//                context.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS)
//                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//
//            }
//
//            if (!Utilities.isGpsEnabled(context)) {
//                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
//            }


            int currentapiVersion = android.os.Build.VERSION.SDK_INT;

            if (currentapiVersion <= 21) {
                //Log.e("currentapiVersion", "less than equal to??" + currentapiVersion);
                try {
                    if (packageName != null && !packageName.equals("")) {
                        boolean foreground = new ForegroundCheckAsyncTask(context).execute(packageName).get();
                        if (foreground) {
                            Utilities.stopAppIntent(context);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            } else if (currentapiVersion > 21) {
                //Log.e("currentapiVersion", "greater than ??" + currentapiVersion);
                if (packageName != null && !packageName.equals("")) {
                    AppChecker appChecker = new AppChecker();
                    appChecker.when(packageName, new AppChecker.Listener() {
                                @Override
                                public void onForeground(String packageName) {

                                    Utilities.stopAppIntent(context);
                                }

                            }).timeout(TIME_DELAY_FOR_CHECK)
                            .start(context);
                }

            }

            stopSelf();
        }
    };

    @Override
    public void onDestroy() {
        this.isRunning = false;
    }
}
