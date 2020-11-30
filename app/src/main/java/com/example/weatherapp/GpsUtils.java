package com.example.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;

import static android.content.ContentValues.TAG;

public class GpsUtils {

    private SettingsClient settingsClient;
    private LocationSettingsRequest locationSettingsRequest;
    private LocationManager locationManager;
    private LocationRequest locationRequest;

    private Context context;

    public GpsUtils (Context context, LocationManager locationManager) {
        this.locationManager = locationManager;
        this.context = context;
        settingsClient = LocationServices.getSettingsClient(context);

        // Building location request (checking for location updates)
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(2 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();

        builder.setAlwaysShow(true);

    }

    public void turnGPSOn(final onGpsListener onGpsListener) {

        // Checking current GPS status
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && onGpsListener != null) {
                onGpsListener.gpsStatus(true);
        } else {
            settingsClient
                .checkLocationSettings(locationSettingsRequest)
                    //.addOnSuccessListener() is unnecessary here, because of previous if statement
                .addOnFailureListener((Activity) context, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            //Location settings are not satisfied. But could be fixed by showing the user a dialog.
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {

                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult((Activity) context, AppConstants.GPS_REQUEST);

                            } catch (IntentSender.SendIntentException sie) {
                                Log.i(TAG, "Unable to execute request.");
                            }
                                break;
                                // Location settings are not satisfied and can't be fixed by dialog
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Sorry, location settings are inadequate. You need to change it in Settings";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
        }
    }

    // Return a current GPS status.
    // "True" means that GPS is on and has all needed permits
    public interface onGpsListener {
        void gpsStatus(boolean isGPSEnable);
    }


}
