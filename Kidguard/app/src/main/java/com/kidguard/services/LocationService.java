package com.kidguard.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.kidguard.MainActivity;
import com.kidguard.R;
import com.kidguard.interfaces.Constant;
import com.kidguard.model.Locations;
import com.kidguard.model.Sms;
import com.kidguard.preference.Preference;
import com.kidguard.receivers.NotificationReceiver;
import com.kidguard.utilities.Utilities;
import com.rvalerio.fgchecker.AppChecker;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class LocationService extends Service implements LocationListener, Constant {

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double fusedLatitude = 0.0;
    private double fusedLongitude = 0.0;
    private static LocationService mContext;
    private Intent intent;
    private ArrayList<Locations> lstlocation;

    @SuppressWarnings("FieldCanBeLocal")
    private String tag;


    // LocationService Instance
    public static LocationService getInstance() {
        return mContext;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = LocationService.this;

        /* Get Notification Message Using LocalBroadcastManager */
        NotificationReceiver onNotice = new NotificationReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));

        startFusedLocation();
        registerRequestUpdate(mContext);

        stopAppsUpToLevel21(mContext);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        tag = intent.getStringExtra(KEY_TAG);
//
//        /* Get Data With Tag */
//        if (tag != null && !tag.equals("")) {
//           Log.e("tag","tag??"+tag);
//        }

        return super.onStartCommand(intent, flags, startId);
    }

    // Stop Apps Up API level > 21
    private void stopAppsUpToLevel21(final Context context) {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        //Log.e("LocationService","currentapiVersion??"+currentapiVersion);

        if (currentApiVersion > 21) {
            AppChecker appChecker = new AppChecker();
            appChecker
                    .when(PACKAGE_NAME, new AppChecker.Listener() {
                        @Override
                        public void onForeground(String packageName) {
                            Log.e("STOP", "STOP>21");
                            Utilities.stopAppIntent(context);
                        }

                    }).timeout(TIME_DELAY_FOR_CHECK)
                    .start(context);
        }

        if (currentApiVersion >= 21) {
            //startThreadForBackgroundCheck(context);
        }
    }

    /* Start Thread For Background Check */
    private void startThreadForBackgroundCheck(final Context context) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Log.e("run", ">=21");

                if (!Utilities.isNetworkAvailable(context)) {
                    context.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }

                if (!Utilities.isGpsEnabled(context)) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

                startThreadForBackgroundCheck(context);
            }

        }, TIME_DELAY_FOR_CHECK);

    }


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        this.intent = intent;
    }

    /* Start Fused Locations */
    public void startFusedLocation() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                        }

                        @Override
                        public void onConnectionSuspended(int cause) {
                        }

                    }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {

                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                        }
                    }).build();
            mGoogleApiClient.connect();
        } else {
            mGoogleApiClient.connect();
        }
    }

    /* Stop Fused Locations */
    public void stopFusedLocation() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
            if (intent != null) {
                stopService(intent);
            }
        }
    }

    /* registerRequestUpdate */
    public void registerRequestUpdate(final LocationListener listener) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(60000); // every minute
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                            mLocationRequest, listener);

                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!isGoogleApiClientConnected()) {
                        mGoogleApiClient.connect();
                    }
                    registerRequestUpdate(listener);
                }
            }
        }, 1000); // every second
    }

    public boolean isGoogleApiClientConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("Latitude", "Latitude??" + location.getLatitude());
        Log.e("Longitude", "Longitude??" + location.getLongitude());
//        generateNotification("Latitude =" + String.valueOf(location.getLatitude() + " " +
//                "Longitude=" + String.valueOf(location.getLongitude())));
        setFusedLatitude(location.getLatitude());
        setFusedLongitude(location.getLongitude());

        if (Preference.getLatitude(this) == null && Preference.getLongitude(this) == null) {

            Log.e("if", "if");
            Preference.setLatLong(this, location.getLatitude(), location.getLongitude());

        } else {

            Log.e("distance", "is:->>>" + Utilities.distance(Double.parseDouble(Preference.getLatitude(this))
                    , Double.parseDouble(Preference.getLongitude(this)), location.getLatitude(),
                    location.getLongitude()));
            double distance = Utilities.distance(Double.parseDouble(Preference.getLatitude(this))
                    , Double.parseDouble(Preference.getLongitude(this)), location.getLatitude(),
                    location.getLongitude());

            Preference.setLatLong(this, location.getLatitude(), location.getLongitude());
            if (distance >= 50) {

                Log.e("hit", "Api");
                sendLocationToServer(location.getLatitude(), location.getLongitude(), distance);
            }
        }
    }

    public void setFusedLatitude(double lat) {
        fusedLatitude = lat;
    }

    public void setFusedLongitude(double lon) {
        fusedLongitude = lon;
    }

    /* Generate Notification */
    private void generateNotification(String s) {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("data")
                        .setContentText(String.valueOf(s))
                        .setDefaults(Notification.DEFAULT_ALL) // requires VIBRATE permission
                        .setContentIntent(pIntent)
                        .setDefaults(0)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(String.valueOf(s)));
        final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(0, builder.build());
    }

    /* Send Location To Server */
    private void sendLocationToServer(double latitude, double longitude, double distance) {

        lstlocation = new ArrayList<>();
        Locations locations = new Locations();
        locations.setLatitude(String.valueOf(latitude));
        locations.setLongitude(String.valueOf(longitude));
        locations.setDistance(String.valueOf(distance));
        locations.setDateTime(Utilities.getCurrentDateTime());
        locations.setDateTimeStamp(Utilities.getCurrentDateTimeStamp());
        lstlocation.add(locations);

        if (lstlocation != null && lstlocation.size() > Integer.parseInt(ZERO)) {
            StringBuffer sbAppend = new StringBuffer();
            Gson gson = new Gson();
            Type type = new TypeToken<List<Sms>>() {
            }.getType();
            //String jsonSMS = gson.toJson(lstSms, type);
            JsonArray jsonArray = (JsonArray) gson.toJsonTree(lstlocation, type);
            JsonArray jsonArraySms = Utilities.getJsonArray(jsonArray);
            String finalJSON = "\"Location\":" + jsonArraySms;
            sbAppend.append(finalJSON);
            finalJSON = "{" + sbAppend + "}";
            Log.e("JSON", "FINAL??" + finalJSON);
            new RestClientService(TAG_LOCATION, API_TOKEN, finalJSON);
        }
    }
}
