package com.kidguard.asynctask;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;
import com.kidguard.MainActivity;
import com.kidguard.R;
import com.kidguard.interfaces.Constant;
import com.kidguard.model.Mail;
import com.kidguard.orm.DatabaseHelper;
import com.kidguard.services.BackgroundDataService;
import com.kidguard.services.GoogleAccountService;
import com.kidguard.utilities.Utilities;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

/* MakeRequestTask */
@SuppressWarnings("all")
public class MakeRequestEmail extends AsyncTask<Void, String, ArrayList<Mail>> implements Constant {
    private static final String TAG = MakeRequestEmail.class.getSimpleName();

    private Context ctx;
    private Gmail mService = null;
    private Exception mLastError = null;
    private String count;
    private String dateFrom;
    private String dateTo;
    private String subject;
    private GoogleAccountCredential mCredential;
    private Dao<Mail, Integer> mailDao;
    private DatabaseHelper databaseHelper = null;
    private int i = 0;

    public MakeRequestEmail(Context ctx, GoogleAccountCredential mCredential, String count,
                            String dateFrom, String dateTo, String subject) {
        this.ctx = ctx;
        this.count = count;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.subject = subject;
        this.mCredential = mCredential;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        mService = new Gmail.Builder(
                transport, jsonFactory, mCredential)
                .setApplicationName(ctx.getString(R.string.app_name))
                .build();

    }

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
            mailDao = getHelper().getMailDao();

            if (mailDao.isTableExists()) {

                if (DatabaseHelper.getInstance() != null) {
                    TableUtils.dropTable(DatabaseHelper.getInstance().getConnectionSource(), Mail.class, true);
                    TableUtils.createTable(DatabaseHelper.getInstance().getConnectionSource(), Mail.class);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Background task to call G-Mail API.
     *
     * @param params no parameters needed for this task.
     */
    @Override
    protected ArrayList<Mail> doInBackground(Void... params) {
        try {
            return listMessagesMatchingQuery(mService, mCredential.getSelectedAccountName(),
                    count, dateFrom, dateTo, subject);

        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    @Override
    protected void onCancelled() {
        if (mLastError != null) {
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                showGooglePlayServicesAvailabilityErrorDialog(
                        ((GooglePlayServicesAvailabilityIOException) mLastError)
                                .getConnectionStatusCode());
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                if (MainActivity.getInstance() != null) {
                    MainActivity.getInstance().startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            REQUEST_AUTHORIZATION);
                }

            } else {
                Log.e(TAG, "The following error occurred:\n" + mLastError.getMessage());
            }
        } else {
            Log.e(TAG, "Request cancelled.");
        }
    }

    /**
     * Fetch a list of Gmail labels attached to the specified account.
     *
     * @return List of Strings labels.
     * @throws IOException
     */
    public ArrayList<Mail> listMessagesMatchingQuery(Gmail service, String userId,
                                                     String count, String dateFrom, String dateTo,
                                                     String subject) throws IOException {

        long x = 0;
        ListMessagesResponse response = null;
        List<Message> messages = new ArrayList<Message>();
        ArrayList<Mail> mailList = new ArrayList<Mail>();

        if (dateFrom.equals("") && dateTo.equals("") && subject.equals("") && !count.equals("")) {

            Log.e("1", "1_UP");
            if (Integer.parseInt(count) < _100) {

                Integer y = Integer.parseInt(count);
                x = y.longValue();

            } else if (Integer.parseInt(count) < _200) {

                Integer y = Integer.parseInt(count);
                x = y.longValue();

            } else if (Integer.parseInt(count) < _300) {

                Integer y = Integer.parseInt(count);
                x = y.longValue();

            } else if (Integer.parseInt(count) < _400) {

                Integer y = Integer.parseInt(count);
                x = y.longValue();

            } else if (Integer.parseInt(count) < _500) {

                Integer y = Integer.parseInt(count);
                x = y.longValue();

            } else {

                Integer y = _500;
                x = y.longValue();
            }
            Log.e(TAG, "x Up>>" + x);
            response = service.users().messages().list(userId).setMaxResults(x).execute();
            Log.e(TAG, "Response Up>>" + response);

            while (response.getMessages() != null) {
                messages.addAll(response.getMessages());
                break;
            }

            for (Message message : messages) {
                try {

                    mailList = getMimeMessageList(mService, mCredential.getSelectedAccountName(), message.getId(), mailList);

                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }

            return mailList;

        }


        Integer y = _500;
        x = y.longValue();
        Log.e(TAG, "x Down>>>" + x);
        response = service.users().messages().list(userId).setMaxResults(x).execute();
        Log.e(TAG, "Response Down>>" + response);

        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            break;
        }

        for (Message message : messages) {
            try {
                mailDao = getMimeMessageDao(mService, mCredential.getSelectedAccountName(), message.getId());
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }


        try {
            int countNew = Integer.parseInt(ZERO);
            long numRows = mailDao.countOf();
            Log.e(TAG, "numRows>>>" + numRows);

            final QueryBuilder<Mail, Integer> queryBuilder = mailDao.queryBuilder();

            if (numRows > Integer.parseInt(ZERO)) {

                List<Mail> results = null;

                if (dateFrom.equals("") && dateTo.equals("") && subject.equals("") && !count.equals("")) {

                    Log.e("1", "1");
                    results = mailDao.queryForAll();

                    checkCount(mailList, results, count, countNew);

                    return mailList;
                }

                if (dateFrom.equals("") && dateTo.equals("") && !subject.equals("") && !count.equals("")) {

                    Log.e("2", "2");

                    results = queryBuilder.where().eq(Mail.MAIL_SUBJECT, subject).query();

                    checkCount(mailList, results, count, countNew);

                    return mailList;
                }

                if (dateFrom.equals("") && dateTo.equals("") && !subject.equals("") && count.equals("")) {

                    Log.e("3", "3");

                    results = queryBuilder.where().eq(Mail.MAIL_SUBJECT, subject).query();

                    if (results != null && results.size() > 0) {
                        for (int j = 0; j < results.size(); j++) {
                            setMailPOJO(mailList, results, j);
                        }
                    }

                    return mailList;

                }

                if (!dateFrom.equals("") && !dateTo.equals("") && subject.equals("") && count.equals("")) {

                    Log.e("4", "4");

                    List<Mail> resultDateFrom = queryBuilder.where().like(Mail.MAIL_DATE, dateFrom).query();
                    List<Mail> resultDateTo = queryBuilder.where().like(Mail.MAIL_DATE, dateTo).query();

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

                        results = queryBuilder.where().eq(Mail.MAIL_DATE, dateFrom)
                                .query();

                        if (results != null && results.size() > 0) {
                            for (int j = 0; j < results.size(); j++) {
                                setMailPOJO(mailList, results, j);
                            }
                        }

                    } else {
                        results = queryBuilder.where().between(Mail.MAIL_DATE, dateFrom, dateTo)
                                .query();

                        if (results != null && results.size() > 0) {
                            for (int j = 0; j < results.size(); j++) {
                                setMailPOJO(mailList, results, j);
                            }
                        }
                    }

                    return mailList;

                }

                if (!dateFrom.equals("") && !dateTo.equals("") && !subject.equals("") && count.equals("")) {

                    Log.e("5", "5");

                    List<Mail> resultDateFrom = queryBuilder.where().like(Mail.MAIL_DATE, dateFrom).query();
                    List<Mail> resultDateTo = queryBuilder.where().like(Mail.MAIL_DATE, dateTo).query();

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

                        results = queryBuilder.where().eq(Mail.MAIL_DATE, dateFrom)
                                .and().eq(Mail.MAIL_SUBJECT, subject).query();

                        if (results != null && results.size() > 0) {
                            for (int j = 0; j < results.size(); j++) {
                                setMailPOJO(mailList, results, j);
                            }
                        }

                    } else {
                        results = queryBuilder.where().between(Mail.MAIL_DATE, dateFrom, dateTo)
                                .and().eq(Mail.MAIL_SUBJECT, subject).query();

                        if (results != null && results.size() > 0) {
                            for (int j = 0; j < results.size(); j++) {
                                setMailPOJO(mailList, results, j);
                            }
                        }
                    }

                    return mailList;

                }

                if (!dateFrom.equals("") && !dateTo.equals("") && subject.equals("") && !count.equals("")) {

                    Log.e("6", "6");

                    List<Mail> resultDateFrom = queryBuilder.where().like(Mail.MAIL_DATE, dateFrom).query();
                    List<Mail> resultDateTo = queryBuilder.where().like(Mail.MAIL_DATE, dateTo).query();

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

                        results = queryBuilder.where().eq(Mail.MAIL_DATE, dateFrom).query();


                    } else {

                        results = queryBuilder.where().between(Mail.MAIL_DATE, dateFrom, dateTo).query();

                    }

                    checkCount(mailList, results, count, countNew);

                    return mailList;

                }

                if (!dateFrom.equals("") && !dateTo.equals("") && !subject.equals("") && !count.equals("")) {

                    Log.e("7", "7");

                    List<Mail> resultDateFrom = queryBuilder.where().like(Mail.MAIL_DATE, dateFrom).query();
                    List<Mail> resultDateTo = queryBuilder.where().like(Mail.MAIL_DATE, dateTo).query();

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

                        results = queryBuilder.where().eq(Mail.MAIL_DATE, dateFrom)
                                .and().eq(Mail.MAIL_SUBJECT, subject).query();


                    } else {

                        results = queryBuilder.where().between(Mail.MAIL_DATE, dateFrom, dateTo)
                                .and().eq(Mail.MAIL_SUBJECT, subject).query();

                    }

                    checkCount(mailList, results, count, countNew);

                    return mailList;

                }

                if (dateFrom.equals("") && dateTo.equals("") && subject.equals("") && count.equals("")) {

                    Log.e("8", "8");
                    results = mailDao.queryForAll();

                    if (results != null && results.size() > 0) {
                        for (int j = 0; j < results.size(); j++) {
                            setMailPOJO(mailList, results, j);
                        }
                    }

                    return mailList;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mailList;
    }

    @Override
    protected void onPostExecute(ArrayList<Mail> output) {
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }

        if (output == null || output.size() == 0) {
            Log.e(TAG, "No results returned.");

            if (BackgroundDataService.getInstance() != null) {
                ctx.stopService(new Intent(ctx, BackgroundDataService.class));
            }

            if (GoogleAccountService.getInstance() != null) {
                ctx.stopService(new Intent(ctx, GoogleAccountService.class));
            }

        } else {
            Log.e(TAG, "Data retrieved using the Gmail API:");
            if (output != null && output.size() > Integer.parseInt(ZERO))
                GoogleAccountService.getInstance().sendEmailDataToServer(output);
        }
    }

    /* Set Mail POJO */
    private void setMailPOJO(List<Mail> mailList, List<Mail> results, int j) {
        Mail mail = new Mail();
        mail.setMail_id(results.get(j).getMail_id());
        mail.setSubject(results.get(j).getSubject());
        mail.setFrom(results.get(j).getFrom());
        mail.setDate(results.get(j).getDate());
        mail.setDate_timestamp(results.get(j).getDate_timestamp());
        mail.setSnippet(results.get(j).getSnippet());
        mailList.add(mail);
    }

    /* checkCount */
    private void checkCount(ArrayList<Mail> lstCallsSorted, List<Mail> results, String count, int countNew) {

        if (results != null && results.size() > 0) {
            if (Integer.parseInt(count) == results.size()) {
                countNew = Integer.parseInt(count);
            } else if (Integer.parseInt(count) < results.size()) {
                countNew = Integer.parseInt(count);
            } else if (Integer.parseInt(count) > results.size()) {
                countNew = results.size();
            }

            for (int i = 0; i < countNew; i++) {
                setMailPOJO(lstCallsSorted, results, i);
            }
        }
    }

    public Message getMessage(Gmail service, String userId, String messageId)
            throws IOException {
        Message message = service.users().messages().get(userId, messageId).execute();
        return message;
    }

    /**
     * Get a Message and use it to create a MimeMessage.
     *
     * @param service   Authorized Gmail API instance.
     * @param userId    User's email address. The special value "me"
     *                  can be used to indicate the authenticated user.
     * @param messageId ID of Message to retrieve.
     * @return MimeMessage MimeMessage populated from retrieved Message.
     * @throws IOException
     * @throws MessagingException
     */
    private Dao<Mail, Integer> getMimeMessageDao(Gmail service, String userId, String messageId)
            throws IOException, MessagingException {
        Message message = service.users().messages().get(userId, messageId).setFormat("raw").execute();
        Log.e("ID", "ID??" + message.getId());
        Log.e(TAG, "email_count" + i++);
        Base64 base64Url = new Base64(true);
        byte[] emailBytes = base64Url.decodeBase64(message.getRaw());

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));

       /* First Check Mail Database is Exists Or Not
          If Exists Then Remove Table From Old Entries
          and Fill With New Mail Entries In the Table*/
        try {
            Mail mail = new Mail();
            mail.setMail_id(message.getId());
            mail.setSubject(email.getSubject());
            mail.setFrom(getFormattedAddresses(email.getFrom()));
            mail.setDate_timestamp(message.getInternalDate().toString());
            mail.setDate(Utilities.getDate(message.getInternalDate()));
            mail.setSnippet(message.getSnippet());
            mailDao.create(mail);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mailDao;
    }

    /**
     * Get a Message and use it to create a MimeMessage.
     *
     * @param service   Authorized Gmail API instance.
     * @param userId    User's email address. The special value "me"
     *                  can be used to indicate the authenticated user.
     * @param messageId ID of Message to retrieve.
     * @return MimeMessage MimeMessage populated from retrieved Message.
     * @throws IOException
     * @throws MessagingException
     */
    private ArrayList<Mail> getMimeMessageList(Gmail service, String userId, String messageId, ArrayList<Mail> mailList)
            throws IOException, MessagingException {

        Message message = service.users().messages().get(userId, messageId).setFormat("raw").execute();
        Log.e("ID", "ID_UP??" + message.getId());
        Log.e(TAG, "email_count" + i++);
        Base64 base64Url = new Base64(true);
        byte[] emailBytes = base64Url.decodeBase64(message.getRaw());

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));

        Mail mail = new Mail();
        mail.setMail_id(message.getId());
        mail.setSubject(email.getSubject());
        mail.setFrom(getFormattedAddresses(email.getFrom()));
        mail.setDate_timestamp(message.getInternalDate().toString());
        mail.setDate(Utilities.getDate(message.getInternalDate()));
        mail.setSnippet(message.getSnippet());
        mailList.add(mail);

        return mailList;
    }

    /* Get Resulted DateFrom To */
    private List<String> getResultedDateFromTo(QueryBuilder<Mail,
            Integer> queryBuilder, String dateFrom, String dateTo) {
        List<String> sortList = new ArrayList<>();
        try {
            List<Mail> results = null;

            results = queryBuilder.where().between(Mail.MAIL_DATE, dateFrom, dateTo).query();

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

    /* GooglePlayServicesAvailabilityErrorDialog */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        if (MainActivity.getInstance() != null) {
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            Dialog dialog = apiAvailability.getErrorDialog(
                    MainActivity.getInstance(),
                    connectionStatusCode,
                    REQUEST_GOOGLE_PLAY_SERVICES);
            dialog.show();
        }
    }

    /* Is GooglePlayServices Available */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(ctx);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /* Acquire GooglePlayServices */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(ctx);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    private String getFormattedAddresses(Address[] addresses) {
        List<String> recipientsList = new ArrayList<String>();
        String receiver = null;
        if (addresses != null && addresses.length != 0) {
            for (Address adress : addresses) {
                receiver = getPersonFromAddress(adress);
            }
        }
        return receiver;
    }

    /**
     * Extracts the person information from Address object.
     *
     * @param address the raw input Address object from the email
     * @return Person object
     */
    private static String getPersonFromAddress(Address address) {

        if (address == null) {
            return null;
        }

        String from = address.toString();
        from = prepareMimeFieldToDecode(from);

        if (from != null) {
            try {
                from = MimeUtility.decodeText(from);
            } catch (java.io.UnsupportedEncodingException ex) {
                Log.d("UnsupportedEncodingException", "exp>>", ex);
            }
        } else {
            return null;
        }

        String fromName;
        String fromEmail;
        String regex = "(.*)<([^<>]*)>";

        if (from.matches(regex)) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(from);
            matcher.find();
            fromName = matcher.group(1);
            fromEmail = matcher.group(2);
        } else {
            fromName = from;
            fromEmail = from;
        }

        return fromEmail;
    }

    /**
     * Replaces the "x-unknown" encoding type with "iso-8859-2" to be able to decode the mime
     * string and removes the quotation marks if there is any.
     *
     * @param text a mime encoded raw text
     * @return a changed raw text with corrected encoding
     */
    private static String prepareMimeFieldToDecode(String text) {
        text = text.trim();
        if (text.indexOf("=?x-unknown?") != -1) {
            text = text.replace("x-unknown", "iso-8859-2");
        }
        int quotStart = text.indexOf("\"");
        int quotEnd = text.lastIndexOf("\"");
        if (quotStart != -1 && quotStart == 0 && quotStart != quotEnd) {
            StringBuilder sb = new StringBuilder(text);
            // replacing the starting quot
            sb.replace(0, 1, "");
            sb.replace(quotEnd - 1, quotEnd, "");
            text = sb.toString();
        }
        return text;
    }
}
