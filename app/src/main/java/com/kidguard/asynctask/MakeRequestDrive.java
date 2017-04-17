package com.kidguard.asynctask;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;
import com.kidguard.MainActivity;
import com.kidguard.R;
import com.kidguard.interfaces.Constant;
import com.kidguard.model.GoogleDrive;
import com.kidguard.orm.DatabaseHelper;
import com.kidguard.services.BackgroundDataService;
import com.kidguard.services.GoogleAccountService;
import com.kidguard.utilities.Utilities;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MakeRequestDrive extends AsyncTask<Void, String, ArrayList<GoogleDrive>> implements Constant {
    private static final String TAG = MakeRequestDrive.class.getSimpleName();

    private Drive mDrive = null;
    private Exception mLastError = null;
    private String count;
    private String dateFrom;
    private String dateTo;
    private String extention;
    private String size;
    private Context ctx;
    private Dao<GoogleDrive, Integer> googleDriveDao;
    private DatabaseHelper databaseHelper = null;

    // MakeRequestDrive Constructor
    public MakeRequestDrive(Context ctx, GoogleAccountCredential credential, String count, String dateFrom, String dateTo, String subject, String size) {
        this.ctx = ctx;
        this.count = count;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.extention = subject;
        this.size = size;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        mDrive = new Drive.Builder(transport, jsonFactory, credential).setApplicationName(ctx.getString(R.string.app_name)).build();
    }

    // Database Helper
    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(ctx, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            googleDriveDao = getHelper().getGoogleDriveDao();
            if (googleDriveDao.isTableExists()) {
                if (DatabaseHelper.getInstance() != null) {
                    TableUtils.dropTable(DatabaseHelper.getInstance().getConnectionSource(), GoogleDrive.class, true);
                    TableUtils.createTable(DatabaseHelper.getInstance().getConnectionSource(), GoogleDrive.class);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Background task to call G-Drive API.
     *
     * @param params no parameters needed for this task.
     */
    @Override
    protected ArrayList<GoogleDrive> doInBackground(Void... params) {
        try {
            return getDataFromDriveApi();
        } catch (IOException e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    @Override
    protected void onCancelled() {
        if (mLastError != null) {
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                showGooglePlayServicesAvailabilityErrorDialog(((GooglePlayServicesAvailabilityIOException) mLastError).getConnectionStatusCode());
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                if (MainActivity.getInstance() != null) {
                    MainActivity.getInstance().startActivityForResult(((UserRecoverableAuthIOException) mLastError).getIntent(), REQUEST_AUTHORIZATION);
                }
            } else {
                Log.e(TAG, "The following error occurred:\n" + mLastError.getMessage());
            }
        } else {
            Log.e(TAG, "Request cancelled.");
        }
    }

    // GooglePlayServicesAvailabilityErrorDialog
    private void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        if (MainActivity.getInstance() != null) {
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            Dialog dialog = apiAvailability.getErrorDialog(MainActivity.getInstance(), connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES);
            dialog.show();
        }
    }

    private ArrayList<GoogleDrive> getDataFromDriveApi() throws IOException {
        ArrayList<GoogleDrive> fileInfo = new ArrayList<>();
        int x;
        if (dateFrom.equals("") && dateTo.equals("") && extention.equals("") && size.equals("") && !count.equals("")) {
            Log.e("1", "1_UP_DRIVE");
            if (Integer.parseInt(count) < _100) {
                x = Integer.parseInt(count);
            } else if (Integer.parseInt(count) < _200) {
                x = Integer.parseInt(count);
            } else if (Integer.parseInt(count) < _300) {
                x = Integer.parseInt(count);
            } else if (Integer.parseInt(count) < _400) {
                x = Integer.parseInt(count);
            } else if (Integer.parseInt(count) < _500) {
                x = Integer.parseInt(count);
            } else {
                x = _500;
            }

            FileList result = mDrive.files().list().setMaxResults(x).execute();
            List<File> files = result.getItems();

            if (files != null) {
                for (File file : files) {
                    GoogleDrive googleDrive = new GoogleDrive();
                    googleDrive.setFileId(file.getId());
                    googleDrive.setFileTitle(file.getTitle());
                    googleDrive.setFileDate(Utilities.splitDate(file.getModifiedDate().toString()));
                    googleDrive.setFileSize(String.valueOf(file.getFileSize()));
                    googleDrive.setFileExtention(file.getFileExtension());
                    googleDrive.setFileDownloadUrl(file.getDownloadUrl());
                    fileInfo.add(googleDrive);
                }
            }
            return fileInfo;
        }

        Log.e("1", "1_DOWN_DRIVE");
        x = _500;

        FileList result = mDrive.files().list().setMaxResults(x).execute();
        List<File> files = result.getItems();
        Log.e("drive_file_size1", "" + files.size());
        for (File file : files) {
            try {
                GoogleDrive googleDrive = new GoogleDrive();
                googleDrive.setFileId(file.getId());
                googleDrive.setFileTitle(file.getTitle());
                googleDrive.setFileDate(Utilities.splitDate(file.getModifiedDate().toString()));
                googleDrive.setFileSize(String.valueOf(file.getFileSize()));
                googleDrive.setFileExtention(file.getFileExtension());
                googleDrive.setFileDownloadUrl(file.getDownloadUrl());
                googleDriveDao.create(googleDrive);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        try {
            int countNew = Integer.parseInt(ZERO);
            long numRows = googleDriveDao.countOf();
            final QueryBuilder<GoogleDrive, Integer> queryBuilder = googleDriveDao.queryBuilder();

            if (numRows > Integer.parseInt(ZERO)) {
                List<GoogleDrive> results;
                if (dateFrom.equals("") && dateTo.equals("") && extention.equals("") && size.equals("") && !count.equals("")) {
                    Log.e("1", "1");
                    results = googleDriveDao.queryForAll();
                    checkCount(fileInfo, results, count, countNew);
                    return fileInfo;
                }

                if (dateFrom.equals("") && dateTo.equals("") && !extention.equals("") && !count.equals("")) {
                    Log.e("2", "2");
                    results = queryBuilder.where().eq(GoogleDrive.FILE_EXTENSION, extention).query();
                    checkCount(fileInfo, results, count, countNew);
                    return fileInfo;
                }

                if (dateFrom.equals("") && dateTo.equals("") && !extention.equals("") && count.equals("")) {
                    Log.e("3", "3");
                    results = queryBuilder.where().eq(GoogleDrive.FILE_EXTENSION, extention).query();
                    if (results != null && results.size() > 0) {
                        for (int j = 0; j < results.size(); j++) {
                            setDrivePOJO(fileInfo, results, j);
                        }
                    }
                    return fileInfo;
                }

                if (!dateFrom.equals("") && !dateTo.equals("") && extention.equals("") && size.equals("") && count.equals("")) {
                    Log.e("4", "4");
                    List<GoogleDrive> resultDateFrom = queryBuilder.where().like(GoogleDrive.FILE_DATE, dateFrom).query();
                    List<GoogleDrive> resultDateTo = queryBuilder.where().like(GoogleDrive.FILE_DATE, dateTo).query();
                    if (resultDateFrom.size() == 0 && resultDateTo.size() == 0) {
                        if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                            return null;
                        }
                        dateFrom = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(0);
                        dateTo = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() - 1);
                    } else if (resultDateFrom.size() == 0) {
                        if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                            dateFrom = dateTo;
                        } else {
                            dateFrom = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(0);
                        }
                    } else if (resultDateTo.size() == 0) {
                        if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                            dateTo = dateFrom;
                        } else {
                            dateTo = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() - 1);
                        }
                    }

                    if (dateFrom.equals(dateTo)) {
                        results = queryBuilder.where().eq(GoogleDrive.FILE_DATE, dateFrom).query();
                        if (results != null && results.size() > 0) {
                            for (int j = 0; j < results.size(); j++) {
                                setDrivePOJO(fileInfo, results, j);
                            }
                        }
                    } else {
                        results = queryBuilder.where().between(GoogleDrive.FILE_DATE, dateFrom, dateTo).query();
                        if (results != null && results.size() > 0) {
                            for (int j = 0; j < results.size(); j++) {
                                setDrivePOJO(fileInfo, results, j);
                            }
                        }
                    }
                    return fileInfo;
                }

                if (!dateFrom.equals("") && !dateTo.equals("") && !extention.equals("") && size.equals("") && count.equals("")) {
                    Log.e("5", "5");
                    List<GoogleDrive> resultDateFrom = queryBuilder.where().like(GoogleDrive.FILE_DATE, dateFrom).query();
                    List<GoogleDrive> resultDateTo = queryBuilder.where().like(GoogleDrive.FILE_DATE, dateTo).query();

                    if (resultDateFrom.size() == 0 && resultDateTo.size() == 0) {
                        if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                            return null;
                        }
                        dateFrom = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(0);
                        dateTo = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() - 1);
                    } else if (resultDateFrom.size() == 0) {
                        if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                            dateFrom = dateTo;
                        } else {
                            dateFrom = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(0);
                        }
                    } else if (resultDateTo.size() == 0) {
                        if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                            dateTo = dateFrom;
                        } else {
                            dateTo = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() - 1);
                        }
                    }

                    if (dateFrom.equals(dateTo)) {
                        results = queryBuilder.where().eq(GoogleDrive.FILE_DATE, dateFrom).and().eq(GoogleDrive.FILE_EXTENSION, extention).query();
                        if (results != null && results.size() > 0) {
                            for (int j = 0; j < results.size(); j++) {
                                setDrivePOJO(fileInfo, results, j);
                            }
                        }
                    } else {
                        results = queryBuilder.where().between(GoogleDrive.FILE_DATE, dateFrom, dateTo).and().eq(GoogleDrive.FILE_EXTENSION, extention).query();
                        if (results != null && results.size() > 0) {
                            for (int j = 0; j < results.size(); j++) {
                                setDrivePOJO(fileInfo, results, j);
                            }
                        }
                    }
                    return fileInfo;
                }

                if (!dateFrom.equals("") && !dateTo.equals("") && extention.equals("") && size.equals("") && !count.equals("")) {
                    Log.e("6", "6");
                    List<GoogleDrive> resultDateFrom = queryBuilder.where().like(GoogleDrive.FILE_DATE, dateFrom).query();
                    List<GoogleDrive> resultDateTo = queryBuilder.where().like(GoogleDrive.FILE_DATE, dateTo).query();
                    if (resultDateFrom.size() == 0 && resultDateTo.size() == 0) {
                        if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                            return null;
                        }
                        dateFrom = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(0);
                        dateTo = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() - 1);
                    } else if (resultDateFrom.size() == 0) {
                        if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                            dateFrom = dateTo;
                        } else {
                            dateFrom = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(0);
                        }
                    } else if (resultDateTo.size() == 0) {
                        if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                            dateTo = dateFrom;
                        } else {
                            dateTo = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() - 1);
                        }
                    }

                    if (dateFrom.equals(dateTo)) {
                        results = queryBuilder.where().eq(GoogleDrive.FILE_DATE, dateFrom).query();
                    } else {
                        results = queryBuilder.where().between(GoogleDrive.FILE_DATE, dateFrom, dateTo).query();
                    }

                    checkCount(fileInfo, results, count, countNew);
                    return fileInfo;
                }

                if (!dateFrom.equals("") && !dateTo.equals("") && !extention.equals("") && size.equals("") && !count.equals("")) {
                    Log.e("7", "7");
                    List<GoogleDrive> resultDateFrom = queryBuilder.where().like(GoogleDrive.FILE_DATE, dateFrom).query();
                    List<GoogleDrive> resultDateTo = queryBuilder.where().like(GoogleDrive.FILE_DATE, dateTo).query();
                    if (resultDateFrom.size() == 0 && resultDateTo.size() == 0) {
                        if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                            return null;
                        }
                        dateFrom = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(0);
                        dateTo = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() - 1);
                    } else if (resultDateFrom.size() == 0) {
                        if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                            dateFrom = dateTo;
                        } else {
                            dateFrom = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(0);
                        }
                    } else if (resultDateTo.size() == 0) {
                        if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                            dateTo = dateFrom;
                        } else {
                            dateTo = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() - 1);
                        }
                    }

                    if (dateFrom.equals(dateTo)) {
                        results = queryBuilder.where().eq(GoogleDrive.FILE_DATE, dateFrom).and().eq(GoogleDrive.FILE_EXTENSION, extention).query();
                    } else {
                        results = queryBuilder.where().between(GoogleDrive.FILE_DATE, dateFrom, dateTo).and().eq(GoogleDrive.FILE_EXTENSION, extention).query();
                    }

                    checkCount(fileInfo, results, count, countNew);
                    return fileInfo;
                }

                if (dateFrom.equals("") && dateTo.equals("") && extention.equals("") && size.equals("") && count.equals("")) {
                    Log.e("8", "8");
                    results = googleDriveDao.queryForAll();
                    if (results != null && results.size() > 0) {
                        for (int j = 0; j < results.size(); j++) {
                            setDrivePOJO(fileInfo, results, j);
                        }
                    }
                    return fileInfo;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fileInfo;
    }

    @Override
    protected void onPostExecute(ArrayList<GoogleDrive> output) {
        super.onPostExecute(output);
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }

        if (output == null || output.size() == 0) {
            Log.e("No Result", "No results returned.");
            if (BackgroundDataService.getInstance() != null) {
                ctx.stopService(new Intent(ctx, BackgroundDataService.class));
            }

            if (GoogleAccountService.getInstance() != null) {
                ctx.stopService(new Intent(ctx, GoogleAccountService.class));
            }

        } else {
            Log.e("Data retrieved", "Data retrieved using the Google Drive API:");
            GoogleAccountService.getInstance().sendGoogleDriveDataToServer(output, mDrive);
        }
    }

    // Set GoogleDrive POJO
    private void setDrivePOJO(List<GoogleDrive> driveList, List<GoogleDrive> results, int j) {
        GoogleDrive drive = new GoogleDrive();
        drive.setFileId(results.get(j).getFileId());
        drive.setFileTitle(results.get(j).getFileTitle());
        drive.setFileDate(results.get(j).getFileDate());
        drive.setFileSize(results.get(j).getFileSize());
        drive.setFileExtention(results.get(j).getFileExtention());
        drive.setFileDownloadUrl(results.get(j).getFileDownloadUrl());
        driveList.add(drive);
    }

    // checkCount
    private void checkCount(ArrayList<GoogleDrive> lstCallsSorted, List<GoogleDrive> results, String count, int countNew) {
        if (results != null && results.size() > 0) {
            if (Integer.parseInt(count) == results.size()) {
                countNew = Integer.parseInt(count);
            } else if (Integer.parseInt(count) < results.size()) {
                countNew = Integer.parseInt(count);
            } else if (Integer.parseInt(count) > results.size()) {
                countNew = results.size();
            }

            for (int i = 0; i < countNew; i++) {
                setDrivePOJO(lstCallsSorted, results, i);
            }
        }
    }

    // Get Resulted Date From To
    private List<String> getResultedDateFromTo(QueryBuilder<GoogleDrive, Integer> queryBuilder, String dateFrom, String dateTo) {
        List<String> sortList = new ArrayList<>();
        try {
            List<GoogleDrive> results = queryBuilder.where().between(GoogleDrive.FILE_DATE, dateFrom, dateTo).query();
            if (results != null && results.size() > 0) {
                for (int i = 0; i < results.size(); i++) {
                    sortList.add(results.get(i).getFileDate());
                }
                Collections.sort(sortList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sortList;
    }
}