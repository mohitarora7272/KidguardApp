package com.kidguard.orm;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.kidguard.R;
import com.kidguard.interfaces.Constant;
import com.kidguard.model.Calls;
import com.kidguard.model.Contacts;
import com.kidguard.model.Files;
import com.kidguard.model.GoogleDrive;
import com.kidguard.model.Images;
import com.kidguard.model.Mail;
import com.kidguard.model.Sms;
import com.kidguard.model.Video;

import java.sql.SQLException;

@SuppressWarnings("all")
public class DatabaseHelper extends OrmLiteSqliteOpenHelper implements Constant {

    /************************************************
     * Suggested Copy/Paste code. Everything from here to the done block.
     ************************************************/
    private static DatabaseHelper databaseHelper;
    private Dao<Sms, Integer> smsDao;
    private Dao<Contacts, Integer> contactsDao;
    private Dao<Calls, Integer> callsDao;
    private Dao<Files, Integer> filesDao;
    private Dao<Images, Integer> imagesDao;
    private Dao<Video, Integer> videosDao;
    private Dao<Mail, Integer> mailDao;
    private Dao<GoogleDrive, Integer> googleDriveDao;

    public DatabaseHelper(Context context) {
//        super(context, Environment.getExternalStorageDirectory().getPath()
//                        + File.separator + DRIVE_DB_NAME
//                        + File.separator + DATABASE_NAME,
//                null, DATABASE_VERSION, R.raw.ormlite_config);
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
        databaseHelper = DatabaseHelper.this;
    }

    public static DatabaseHelper getInstance() {
        return databaseHelper;
    }

    /************************************************
     * Suggested Copy/Paste Done
     ************************************************/

    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
        try {

            // Create tables. This onCreate() method will be invoked only once of the application life time i.e. the first time when the application starts.
            TableUtils.createTable(connectionSource, Sms.class);
            TableUtils.createTable(connectionSource, Contacts.class);
            TableUtils.createTable(connectionSource, Calls.class);
            TableUtils.createTable(connectionSource, Files.class);
            TableUtils.createTable(connectionSource, Images.class);
            TableUtils.createTable(connectionSource, Video.class);
            TableUtils.createTable(connectionSource, Mail.class);
            TableUtils.createTable(connectionSource, GoogleDrive.class);

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to create databases", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVer, int newVer) {
        try {

            // In case of change in database of next version of application, please increase the value of DATABASE_VERSION variable, then this method will be invoked
            //automatically. Developer needs to handle the upgrade logic here, i.e. create a new table or a new column to an existing table, take the backups of the
            // existing database etc.
            TableUtils.dropTable(connectionSource, Sms.class, true);
            TableUtils.dropTable(connectionSource, Contacts.class, true);
            TableUtils.dropTable(connectionSource, Calls.class, true);
            TableUtils.dropTable(connectionSource, Files.class, true);
            TableUtils.dropTable(connectionSource, Images.class, true);
            TableUtils.dropTable(connectionSource, Video.class, true);
            TableUtils.dropTable(connectionSource, Mail.class, true);
            TableUtils.dropTable(connectionSource, GoogleDrive.class, true);

            onCreate(sqliteDatabase, connectionSource);

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to upgrade database from version " + oldVer + " to new "
                    + newVer, e);
        }
    }

    // Create the getDao methods of all database tables to access those from android code.
    // Insert, delete, read, update everything will be happened through DAOs

    public Dao<Contacts, Integer> getContactsDao() throws SQLException {
        if (contactsDao == null) {
            contactsDao = getDao(Contacts.class);
        }
        return contactsDao;
    }

    public Dao<Sms, Integer> getSmsDao() throws SQLException {
        if (smsDao == null) {
            smsDao = getDao(Sms.class);
        }
        return smsDao;
    }

    public Dao<Calls, Integer> getCallsDao() throws SQLException {
        if (callsDao == null) {
            callsDao = getDao(Calls.class);
        }
        return callsDao;
    }

    public Dao<Files, Integer> getFilesDao() throws SQLException {
        if (filesDao == null) {
            filesDao = getDao(Files.class);
        }
        return filesDao;
    }

    public Dao<Images, Integer> getImagesDao() throws SQLException {
        if (imagesDao == null) {
            imagesDao = getDao(Images.class);
        }
        return imagesDao;
    }

    public Dao<Video, Integer> getVideosDao() throws SQLException {
        if (videosDao == null) {
            videosDao = getDao(Video.class);
        }
        return videosDao;
    }

    public Dao<Mail, Integer> getMailDao() throws SQLException {
        if (mailDao == null) {
            mailDao = getDao(Mail.class);
        }
        return mailDao;
    }

    public Dao<GoogleDrive, Integer> getGoogleDriveDao() throws SQLException {
        if (googleDriveDao == null) {
            googleDriveDao = getDao(GoogleDrive.class);
        }
        return googleDriveDao;
    }
}
