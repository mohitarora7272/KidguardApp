package com.kidguard.asynctask;


import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

/* Foreground Check Task */
@SuppressWarnings("all")
public class ForegroundCheckAsyncTask extends AsyncTask<String, Void, Boolean> {
    private static final String TAG = ForegroundCheckAsyncTask.class.getSimpleName();

    private Context context;

    public ForegroundCheckAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        return isAppOnForeground(context, params[0]);
    }

    private boolean isAppOnForeground(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses =
                activityManager.getRunningAppProcesses();
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
