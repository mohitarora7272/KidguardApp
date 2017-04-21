package com.kidguard.asynctask;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.kidguard.interfaces.Constant;
import com.kidguard.model.Calls;
import com.kidguard.orm.DatabaseHelper;
import com.kidguard.services.BackgroundDataService;
import com.kidguard.utilities.Utilities;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CallsAsyncTask extends AsyncTask<String, Void, ArrayList<Calls>> implements Constant {
    private static final String TAG = CallsAsyncTask.class.getSimpleName();

    private DatabaseHelper databaseHelper = null;
    private Dao<Calls, Integer> callsDao;
    private ArrayList<Calls> lstCalls;
    private Context context;

    // CallsAsyncTask Constructor
    public CallsAsyncTask(Context context) {
        this.context = context;
    }

    // Database Helper
    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        lstCalls = new ArrayList<>();
    }

    @Override
    protected ArrayList<Calls> doInBackground(String... params) {
        Cursor managedCursor = null;
        try {
            ContentResolver cr = context.getContentResolver();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                managedCursor = cr.query(CallLog.Calls.CONTENT_URI, null, null, null, null);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    ((Activity) context).startManagingCursor(managedCursor);
                }

                if (managedCursor != null) {
                    callsDao = getHelper().getCallsDao();
                    if (callsDao.isTableExists()) {
                        int id = managedCursor.getColumnIndex(CallLog.Calls._ID);
                        int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
                        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
                        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
                        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
                        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

                        long numRows = callsDao.countOf();
                        if (numRows != 0) {
                            final QueryBuilder<Calls, Integer> queryBuilder = callsDao.queryBuilder();
                            for (int i = 0; i < callsDao.queryForAll().size(); i++) {
                                while (managedCursor.moveToNext()) {
                                    List<Calls> results = queryBuilder.where().eq(Calls.CALLER_ID, managedCursor.getString(id)).query();
                                    if (results.size() == 0) {
                                        setCallsPOJO(managedCursor, id, name, number, type, date, duration);
                                    }
                                }
                            }
                        } else {
                            while (managedCursor.moveToNext()) {
                                setCallsPOJO(managedCursor, id, name, number, type, date, duration);
                            }
                            managedCursor.close();
                        }

                        // Calls Fetch With Tag
                        lstCalls = callsFetchWithTag(callsDao, params[0], params[1], params[2]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (managedCursor != null && !managedCursor.isClosed()) {
                managedCursor.close();
            }
        }

        return lstCalls;
    }

    @Override
    protected void onPostExecute(ArrayList<Calls> list) {
        super.onPostExecute(list);
        // Release databaseHelper
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }

        Log.e(TAG, "CALLS List???" + list.size());
        new BackgroundDataService(context).sendCallsDataToServer(list);
    }

    // Set Calls POJO With Cursor
    private void setCallsPOJO(Cursor managedCursor, int id, int name, int number, int type, int date, int duration) {
        try {
            String dir;
            String fromMe;
            String answered;
            int dirCode = Integer.parseInt(managedCursor.getString(type));
            switch (dirCode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    fromMe = "true";
                    answered = "false";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    fromMe = "false";
                    answered = "true";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    fromMe = "false";
                    answered = "false";
                    break;
                default:
                    dir = "MISSED";
                    fromMe = "false";
                    answered = "false";
            }

            Calls calls = new Calls();
            calls.setCallerId(managedCursor.getString(id));
            calls.setCallerName(managedCursor.getString(name));
            calls.setPhNumber(managedCursor.getString(number));
            calls.setCallType(managedCursor.getString(type));
            calls.setCallDateTimeStamp(managedCursor.getString(date));
            Date callDayTime = new Date(Long.valueOf(managedCursor.getString(date)));
            calls.setCallDateTime(Utilities.changeDateToString(callDayTime));
            calls.setCallDuration(managedCursor.getString(duration));
            calls.setDir(dir);
            calls.setCallerStatus(FALSE);
            calls.setCallerFromMe(fromMe);
            calls.setCallerAnswered(answered);
            callsDao.create(calls);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Set Calls POJO With List
    private void setCallsPOJO(ArrayList<Calls> lstCallsSorted, List<Calls> results, int i) {
        final Calls calls = new Calls();
        calls.setCallerId(results.get(i).getCallerId());
        calls.setCallerName(results.get(i).getCallerName());
        calls.setPhNumber(results.get(i).getPhNumber());
        calls.setCallType(results.get(i).getCallType());
        calls.setCallDateTimeStamp(results.get(i).getCallDateTimeStamp());
        Date callDayTime = new Date(Long.valueOf(results.get(i).getCallDateTimeStamp()));
        calls.setCallDateTime(Utilities.changeDateToString(callDayTime));
        calls.setCallDuration(results.get(i).getCallDuration());
        calls.setDir(results.get(i).getDir());
        calls.setCallerStatus(results.get(i).getCallerStatus());
        calls.setCallerFromMe(results.get(i).getCallerFromMe());
        calls.setCallerAnswered(results.get(i).getCallerAnswered());
        lstCallsSorted.add(calls);
    }

    // Check Count
    private void checkCount(ArrayList<Calls> lstCallsSorted, List<Calls> results, String count, int countNew) {
        if (results != null && results.size() > 0) {
            if (Integer.parseInt(count) == results.size()) {
                countNew = Integer.parseInt(count);
            } else if (Integer.parseInt(count) < results.size()) {
                countNew = Integer.parseInt(count);
            } else if (Integer.parseInt(count) > results.size()) {
                countNew = results.size();
            }

            for (int i = 0; i < countNew; i++) {
                setCallsPOJO(lstCallsSorted, results, i);
            }
        }
    }

    // Calls Fetch With Tag
    private ArrayList<Calls> callsFetchWithTag(Dao<Calls, Integer> callsDao, String count, String dateFrom, String dateTo) {
        ArrayList<Calls> lstCallsSorted = new ArrayList<>();
        int countNew = 0;
        final QueryBuilder<Calls, Integer> queryBuilder = callsDao.queryBuilder();
        try {

            if (!count.equals("") && dateFrom.equals("") && dateTo.equals("")) {
                Log.e("1", "1??");
                List<Calls> results;
                results = queryBuilder.where().eq(Calls.CALLER_STATUS, FALSE).query();
                checkCount(lstCallsSorted, results, count, countNew);
                return lstCallsSorted;
            }

            if (count.equals("") && !dateFrom.equals("") && !dateTo.equals("")) {
                Log.e("2", "2??");
                List<Calls> resultDateFrom = queryBuilder.where().like(Calls.CALLER_DATE_TIME, dateFrom).query();
                List<Calls> resultDateTo = queryBuilder.where().like(Calls.CALLER_DATE_TIME, dateTo).query();
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
                    List<Calls> results;
                    results = queryBuilder.where().eq(Calls.CALLER_DATE_TIME, dateFrom).and().eq(Calls.CALLER_STATUS, FALSE).query();

                    if (results != null && results.size() > 0) {
                        for (int j = 0; j < results.size(); j++) {
                            setCallsPOJO(lstCallsSorted, results, j);
                        }
                    }
                } else {
                    List<Calls> results;
                    results = queryBuilder.where().between(Calls.CALLER_DATE_TIME, dateFrom, dateTo).and().eq(Calls.CALLER_STATUS, FALSE).query();

                    if (results != null && results.size() > 0) {
                        for (int j = 0; j < results.size(); j++) {
                            setCallsPOJO(lstCallsSorted, results, j);
                        }
                    }
                }

                return lstCallsSorted;
            }

            if (!count.equals("") && !dateFrom.equals("") && !dateTo.equals("")) {
                Log.e("3", "3??");
                List<Calls> resultDateFrom = queryBuilder.where().like(Calls.CALLER_DATE_TIME, dateFrom).query();
                List<Calls> resultDateTo = queryBuilder.where().like(Calls.CALLER_DATE_TIME, dateTo).query();

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

                List<Calls> results;
                if (dateFrom.equals(dateTo)) {
                    results = queryBuilder.where().eq(Calls.CALLER_DATE_TIME, dateFrom).and().eq(Calls.CALLER_STATUS, FALSE).query();
                } else {
                    results = queryBuilder.where().between(Calls.CALLER_DATE_TIME, dateFrom, dateTo).and().eq(Calls.CALLER_STATUS, FALSE).query();
                }

                checkCount(lstCallsSorted, results, count, countNew);
                return lstCallsSorted;
            }

            if (count.equals("") && dateFrom.equals("") && dateTo.equals("")) {
                Log.e("4", "4??");
                List<Calls> results;
                results = callsDao.queryForAll();

                if (results != null && results.size() > 0) {
                    for (int j = 0; j < results.size(); j++) {
                        setCallsPOJO(lstCallsSorted, results, j);
                    }
                }
                return lstCallsSorted;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lstCallsSorted;
    }

    // Get Resulted DateFrom To
    private List<String> getResultedDateFromTo(QueryBuilder<Calls, Integer> queryBuilder, String dateFrom, String dateTo) {
        List<String> sortList = new ArrayList<>();
        try {
            List<Calls> results = queryBuilder.where().between(Calls.CALLER_DATE_TIME, dateFrom, dateTo).and().eq(Calls.CALLER_STATUS, FALSE).query();

            if (results != null && results.size() > 0) {
                for (int i = 0; i < results.size(); i++) {
                    sortList.add(results.get(i).getCallDateTime());
                }
                Collections.sort(sortList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sortList;
    }
}