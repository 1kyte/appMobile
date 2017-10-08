
package com.example.asstmobileapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;


public class SensorActivity extends Activity implements SensorEventListener {
    //Main class that checks for sensor readings and proceeds to send an sms to the
    // emergency contatcs upon critical/extreme readings

    //sensor variables
    private SensorManager mSensorManager;
    private Sensor mPressure;
    private Sensor mTemperature;

    //emergency contatcs
    private String contact1;
    private String contact2;
    private String contact3;

    //sensor thresholds and on/off status
    private String tempThresh;
    private String pressThresh;
    private boolean pressToggle;
    private boolean tempToggle;

    //location variables
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double longitude;
    private double latitude;

    Geocoder geocoder;
    List<Address> addresses;

    String address;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        // Get an instance of the sensor service, and use that to get an instance of
        // a particular sensor.
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mTemperature = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        //call contacts activity
        Button btnContacts = (Button) findViewById(R.id.contacts);
        btnContacts.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(SensorActivity.this, Contacts.class);

                startActivity(intent);
            }
        });

        //call temperature activity
        Button btnTemp = (Button) findViewById(R.id.Temperaturebtn);
        btnTemp.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(SensorActivity.this, Temperature.class);

                startActivity(intent);
            }
        });

        //call pressure activity
        Button btnPressure = (Button) findViewById(R.id.pressBtn);
        btnPressure.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(SensorActivity.this, Pressure.class);

                startActivity(intent);
            }
        });

        //load variable data from saved settings in data file
        SharedPreferences prefs = getSharedPreferences("SafetyApp", 0);

        contact1 = prefs.getString("contact1", "NA");
        contact2 = prefs.getString("contact2", "NA");
        contact3 = prefs.getString("contact3", "NA");
        pressThresh = prefs.getString("pressure", "999");
        pressToggle = prefs.getBoolean("pressureToggle", false);
        tempThresh = prefs.getString("temperaturev1", "999");
        tempToggle = prefs.getBoolean("tempToggle", false);

        //location variable instantiations and code
        geocoder = new Geocoder(this, Locale.getDefault());
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                } catch (java.io.IOException e){}

                address = addresses.get(0).getAddressLine(0);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            //re-direct user to settings if location is disabled
            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        startLocationTracking();

    }
    //---start location function definitions---
    // handle the result of the location permissions request
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startLocationTracking();
                return;
        }
    }

    private void startLocationTracking() {

        //request for location permissions from the user
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }

        //start requesting location updates
        locationManager.requestLocationUpdates("gps", 3000, 0, locationListener);
    }

    //---end location function definitions---

    public void sendSms(String ct1) {

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(ct1,null, String.format("Danger! This person is in an accident at %s\nLONG:%f\nLAT:%f",address,longitude,latitude),null,null);

    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            float temper = event.values[0];
            //if reading is above threshold temperature
            if (temper > Float.parseFloat(tempThresh) && tempToggle) {
                sendSms(contact1);
                sendSms(contact2);
                sendSms(contact3);
            }

        }

        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            float press = event.values[0];
            if (press > Float.parseFloat(pressThresh) && pressToggle) {
                sendSms(contact1);
                sendSms(contact2);
                sendSms(contact3);
            }

        }


    }
    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        mSensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mTemperature, SensorManager.SENSOR_DELAY_NORMAL);



    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}