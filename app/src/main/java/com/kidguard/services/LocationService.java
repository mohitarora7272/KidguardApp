package com.kidguard.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.kidguard.interfaces.Constant;
import com.kidguard.model.Locations;
import com.kidguard.model.Sms;
import com.kidguard.preference.Preference;
import com.kidguard.utilities.Utilities;

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

        startFusedLocation();
        registerRequestUpdate(mContext);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            tag = intent.getStringExtra(KEY_TAG);
                 /* Get Data With Tag */
            if (tag != null && !tag.equals("")) {
                Log.e("tag", "tag??" + tag);
            }

        }

        return super.onStartCommand(intent, flags, startId);
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
            JsonArray jsonArrayLocation = Utilities.getJsonArray(jsonArray);
            String finalJSON = "\"Location\":" + jsonArrayLocation;
            sbAppend.append(finalJSON);
            finalJSON = "{" + sbAppend + "}";
            Log.e("JSON", "FINAL??" + finalJSON);
            new RestClientService(TAG_LOCATION, Preference.getAccessToken(mContext), finalJSON);
        }
    }
}
