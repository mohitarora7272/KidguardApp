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
import com.kidguard.MainActivity;
import com.kidguard.R;
import com.kidguard.interfaces.Constant;
import com.kidguard.receivers.NotificationReceiver;
import com.kidguard.utilities.Utilities;
import com.rvalerio.fgchecker.AppChecker;


public class LocationService extends Service implements LocationListener, Constant {

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double fusedLatitude = 0.0;
    private double fusedLongitude = 0.0;
    private static LocationService mContext;
    private Intent intent;
    private Location location;


    // HomeActivity Instance
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

    // Stop Apps Up API level > 21
    private void stopAppsUpToLevel21(final Context context) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentapiVersion > 21) {
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

        if (currentapiVersion >= 21) {
            startThreadForBackgroundCheck(context);
        }
    }

    /* startThreadForBackgroundCheck */
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        this.intent = intent;
    }

    /* Start Fused Location */
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

    /* Stop Fused Location */
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
        mLocationRequest.setInterval(1000); // every second
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
        }, 1000);
    }

    public boolean isGoogleApiClientConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        Log.d("Latitude", "Latitude??" + location.getLatitude());
        Log.d("Longitude", "Longitude??" + location.getLongitude());
        generateNotification("Latitude =" + String.valueOf(location.getLatitude() + " " +
                "Longitude=" + String.valueOf(location.getLongitude())));
        setFusedLatitude(location.getLatitude());
        setFusedLongitude(location.getLongitude());
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
}
