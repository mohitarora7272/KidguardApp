package com.kidguard.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
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

public class LocationService extends Service implements LocationListener, Constant {
    private static final String TAG = LocationService.class.getSimpleName();

    private static LocationService mContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Intent intent;

    // Location Service Instance
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
            String tag = intent.getStringExtra(KEY_TAG);
            // Get Data With Tag
            if (tag != null && !tag.equals("")) {
                Log.e(TAG, "tag location services>>" + tag);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        this.intent = intent;
    }

    // Start Fused Locations
    private void startFusedLocation() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle bundle) {
                }

                @Override
                public void onConnectionSuspended(int cause) {
                }

            }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {

                @Override
                public void onConnectionFailed(@NonNull ConnectionResult result) {
                }
            }).build();
            mGoogleApiClient.connect();
        } else {
            mGoogleApiClient.connect();
        }
    }

    // Stop Fused Locations
    public void stopFusedLocation() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
            if (intent != null) {
                stopService(intent);
            }
        }
    }

    // Register Request Update
    private void registerRequestUpdate(final LocationListener listener) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(60000); // every minute
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, listener);
                } catch (SecurityException e) {
                    //e.printStackTrace();
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

    private boolean isGoogleApiClientConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    // onLocationChanged
    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "Latitude>>" + location.getLatitude());
        Log.e(TAG, "Longitude>>" + location.getLongitude());
        if (Preference.getActiveSubscriber(this) != null && !Preference.getActiveSubscriber(this).equals(FALSE)) {
            if (Preference.getLatitude(this) == null && Preference.getLongitude(this) == null) {
                Preference.setLatLong(this, location.getLatitude(), location.getLongitude());
                sendLocationToServer(location.getLatitude(), location.getLongitude(), 0.0);
            } else {
                Log.e(TAG, "distance is:->>>" + Utilities.distance(Double.parseDouble(Preference.getLatitude(this)), Double.parseDouble(Preference.getLongitude(this)), location.getLatitude(), location.getLongitude()));
                double distance = Utilities.distance(Double.parseDouble(Preference.getLatitude(this)), Double.parseDouble(Preference.getLongitude(this)), location.getLatitude(), location.getLongitude());

                Preference.setLatLong(this, location.getLatitude(), location.getLongitude());
                if (distance >= 50) {
                    Log.e(TAG, "Hit Location Api");
                    sendLocationToServer(location.getLatitude(), location.getLongitude(), distance);
                }
            }
        }
    }

    // Send locations to server when the distance of the user(children) is greater than 50 meter with the last location.
    private void sendLocationToServer(double latitude, double longitude, double distance) {
        ArrayList<Locations> lstLocation = new ArrayList<>();
        Locations locations = new Locations();
        locations.setLatitude(String.valueOf(latitude));
        locations.setLongitude(String.valueOf(longitude));
        locations.setDistance(String.valueOf(distance));
        locations.setDateTime(Utilities.getCurrentDateTime());
        locations.setDateTimeStamp(Utilities.getCurrentDateTimeStamp());
        lstLocation.add(locations);
        if (lstLocation.size() > Integer.parseInt(ZERO)) {
            StringBuilder sbAppend = new StringBuilder();
            Gson gson = new Gson();
            Type type = new TypeToken<List<Sms>>() {
            }.getType();
            JsonArray jsonArray = (JsonArray) gson.toJsonTree(lstLocation, type);
            JsonArray jsonArrayLocation = Utilities.getJsonArray(jsonArray);
            String finalJSON = "\"Location\":" + jsonArrayLocation;
            sbAppend.append(finalJSON);
            finalJSON = "{" + sbAppend + "}";
            Log.e("JSON", "FINAL??" + finalJSON);
            new RestClientService(TAG_LOCATION, Preference.getAccessToken(mContext), finalJSON);
        }
    }
}