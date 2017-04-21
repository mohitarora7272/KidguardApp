package com.kidguard.asynctask;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.kidguard.interfaces.Constant;
import com.kidguard.model.GoogleDrive;
import com.kidguard.preference.Preference;
import com.kidguard.services.BackgroundDataService;
import com.kidguard.utilities.HttpDownloadManager;

import java.io.IOException;
import java.util.ArrayList;

public class DownloadDriveFiles extends AsyncTask<String, Integer, Boolean> implements Constant {
    private static final String TAG = DownloadDriveFiles.class.getSimpleName();
    private Drive mDrive = null;
    private Context ctx;
    private ArrayList<GoogleDrive> lstDrive;

    public DownloadDriveFiles(Context ctx, Drive mDrive, ArrayList<GoogleDrive> lstDrive) {
        this.mDrive = mDrive;
        this.ctx = ctx;
        this.lstDrive = lstDrive;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            final File file = mDrive.files().get(params[0]).execute();
            java.io.File toFiles = new java.io.File(Environment.getExternalStorageDirectory().toString() + java.io.File.separator + DRIVE_NAME);

            if (!toFiles.exists()) {
                toFiles.mkdirs();
            }

            java.io.File toFile = new java.io.File(toFiles.toString(), params[1]);
            HttpDownloadManager downloader = new HttpDownloadManager(file, toFile);
            downloader.setListener(new HttpDownloadManager.FileDownloadProgressListener() {

                public void downloadProgress(long bytesRead, long totalBytes) {
                    Log.d(TAG, String.valueOf(totalBytes));
                    Log.d(TAG, String.valueOf(bytesRead));
                }

                @Override
                public void downloadFinished() {
                    Log.e(TAG, "downloadFinished");
                }

                @Override
                public void downloadFailedWithError(Exception e) {
                    Log.e(TAG, "downloadFailedWithError??" + e.getMessage());
                    new DownloadDriveFiles(ctx, mDrive, lstDrive).execute(file.getId(), file.getTitle());
                }
            });

            return downloader.download(mDrive);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        Log.e(TAG, "Boolean??" + aBoolean);
        int id = Preference.getDriveId(ctx);
        if (id != lstDrive.size()) {
            id++;
            Preference.setDriveId(ctx, id);
        } else {
            new BackgroundDataService(ctx).sendSaveDriveDataToServer(lstDrive);
        }
    }
}