package com.roninaks.tech.shesos;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;

/**
 * Created by nihalpradeep on 24/02/18.
 */

public class KeyPressService extends Service {
    int countPower;
    boolean prevScreenState;
    boolean startFlag;
    long preTime;
    public static Home home;

    @Override
    public void onCreate() {
        super.onCreate();
        // register receiver that handles screen on and screen off logic
        countPower = 0;
        startFlag = false;
        preTime = 0;
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new KeyPressRecieve();
        registerReceiver(mReceiver, filter);

    }

    @Override
    public void onStart(Intent intent, int startId) {
        try {
            boolean screenOn = intent.getBooleanExtra("screen_state", false);
            boolean startFlag = intent.getBooleanExtra("start_flag", false);
            long currentTime = intent.getLongExtra("time", 0);
            if (startFlag) {
                if (screenOn != prevScreenState) {
                    if (countPower != 0) {
                        if (countPower > 2) {
                            if (currentTime - preTime < 2000) {
                                Log.e("Message:", "Message sending hit " + countPower);
                                SharedPreferences mPrefs = getSharedPreferences("label", 0);
                                String phoneNo = mPrefs.getString("shesosno", "");
                                String msg = mPrefs.getString("shesosmsg", "Please help, I am in danger.");
                                Log.e("Phone no",phoneNo);
                                sendSMS(phoneNo, msg);
                                countPower = 0;
                            } else {
                                countPower = 0;
                            }
                        }
                    }
                    prevScreenState = screenOn;
                    countPower++;
                    preTime = currentTime;
                } else {
                    prevScreenState = screenOn;
                    countPower = 0;
                    preTime = currentTime;
                }
            }
        } catch (Exception ex) {
            Log.e("Error KeyPressService", ex.getMessage());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            String contacts[] = phoneNo.split(";");
            for (int i = 0; i < contacts.length; i++) {
                if (isSMSPermissionGranted()) {
                    String gps = getLocation();
                    Log.e("GPS Status: ", gps);
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(contacts[i], null, msg + getLocation(), null, null);
                    Log.e("SMS Status", "SMS Sent");
                }
            }
        } catch (Exception ex) {
            Log.e("SMS Status", ex.getMessage());
//            ex.printStackTrace();
        }
    }

    public boolean isSMSPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Message Permission", "Permission is granted");
                return true;
            } else {

                Log.e("Message Permission", "Permission is revoked once");
                ActivityCompat.requestPermissions(home, new String[]{Manifest.permission.SEND_SMS}, 100);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Message Permission", "Permission is granted");
            return true;
        }
    }

    public String getLocation() {
        try {
            LocationManager locationManager = (LocationManager) home.getSystemService(Context.LOCATION_SERVICE);
            Location location;
            double latitude,longitude;

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            Log.v("isGPSEnabled", "=" + isGPSEnabled);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.v("isNetworkEnabled", "=" + isNetworkEnabled);
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            if (isGPSEnabled == false && isNetworkEnabled == false) {
                // no network provider is enabled
            } else {
                boolean canGetLocation = false;
                if (isGPSEnabled) {
                    location=null;
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        canGetLocation = false;
                        ActivityCompat.requestPermissions(home, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                    }
                    else{
                        canGetLocation = true;
                    }
                    if(canGetLocation){

                        if (location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                    return "GPS: Lat- " + latitude + " Long- " + longitude;
                                }
                            }
                        }
                    }

                }
                else if(isNetworkEnabled) {
                    location = null;

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        canGetLocation = false;
                        ActivityCompat.requestPermissions(home, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
                    }
                    else{
                        canGetLocation = true;
                    }
                    if(canGetLocation){
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                return "GPS: Lat- " + latitude + " Long- " + longitude;
                            }
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services

            }

        } catch (Exception e) {
            return "";
        }
        return  "";

    }



}
