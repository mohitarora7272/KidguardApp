package com.kidguard.services;


import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.kidguard.interfaces.Constant;
import com.kidguard.utilities.Utilities;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class BackgroundCheckService extends Service implements Constant{

    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;

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
        if (!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }

    private Runnable myTask = new Runnable() {
        public void run() {
            //Log.e("Running", "Running");

            if (!Utilities.isNetworkAvailable(context)) {
                context.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

            }

            if (!Utilities.isGpsEnabled(context)) {
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }


            int currentapiVersion = android.os.Build.VERSION.SDK_INT;

            if (currentapiVersion <= 21) {

               // Log.e("currentapiVersion", "currentapiVersion<21");

                try {
                    boolean foreground = new BackgroundCheckService.ForegroundCheckTask().execute(PACKAGE_NAME).get();
                    if (foreground) {
                        Utilities.stopAppIntent(context);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            stopSelf();
        }
    };

    /* Foreground Check Task */
    class ForegroundCheckTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            return isAppOnForeground(context, params[0]);
        }

        private boolean isAppOnForeground(Context context, String packageName) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null) {
                return false;
            }

            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        && appProcess.processName.equals(packageName)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public void onDestroy() {
        this.isRunning = false;

    }
}
