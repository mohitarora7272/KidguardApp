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
import com.kidguard.interfaces.Constant;
import com.kidguard.model.Contacts;
import com.kidguard.orm.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;

public class ContactsAsyncTask extends AsyncTask<String, Void, ArrayList<Contacts>> implements Constant {

    private DatabaseHelper databaseHelper = null;
    private Dao<Contacts, Integer> contactsDao;
    private ArrayList lstContacts;
    private Context context;

    /* Sms Constructor */
    public ContactsAsyncTask(Context context) {
        this.context = context;
        Log.e("here", "here");
//        try {
//            // This is how, a reference of DAO object can be done
//            contactsDao = getHelper().getContactsDao();
//            Log.e("isTableExists", "isTableExists??" + contactsDao.isTableExists());
//            Contacts contacts = new Contacts();
//            contacts.setContactsName("Mohit");
//            contacts.setContactsPhoneNo("1113123123");
//            contactsDao.create(contacts);
//
//
//        } catch (SQLException e) {
//            Log.e("ContactsAsyncTask", "SQLException???" + e.getMessage());
//            e.printStackTrace();
//        }

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
        lstContacts = new ArrayList<Contacts>();
    }

    @Override
    protected ArrayList<Contacts> doInBackground(String... params) {


        return lstContacts;
    }

    @Override
    protected void onPostExecute(ArrayList<Contacts> smsList) {
        super.onPostExecute(smsList);

    }

}
