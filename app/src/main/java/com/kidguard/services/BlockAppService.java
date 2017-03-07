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
    private String tag;
    private String packageName;
    private static BlockAppService mContext;

    // BlockAppService Instance
    public static BlockAppService getInstance() {
        return mContext;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mContext = BlockAppService.this;
        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            tag = intent.getStringExtra(KEY_TAG);
            packageName = intent.getStringExtra(KEY_PACKAGE_NAME);
              /* Get Data With Tag */
            if (tag != null && !tag.equals("")) {
                //Log.e("BlockAppService", "tag??" + tag);
                //Log.e("BlockAppService", "packageName??" + packageName);
            }
        }
        if (!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }

    private Runnable myTask = new Runnable() {
        public void run() {
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
