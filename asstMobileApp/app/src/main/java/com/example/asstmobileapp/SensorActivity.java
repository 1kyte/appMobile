package com.example.asstmobileapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



public class SensorActivity extends Activity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mPressure;
    private Sensor mTemperature;
    private TextView t1;

    private String contact1;
    private String contact2;
    private String contact3;

    private float tempThresh;
    private float pressThresh;
    private boolean pressToggle;
    private boolean tempToggle;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        // Get an instance of the sensor service, and use that to get an instance of
        // a particular sensor.
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mTemperature = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);


        Button btnContacts = (Button) findViewById(R.id.contacts);
        btnContacts.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(SensorActivity.this, Contacts.class);

                startActivity(intent);
            }
        });


        Button btnTemp = (Button) findViewById(R.id.Temperaturebtn);
        btnTemp.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(SensorActivity.this, Temperature.class);

                startActivity(intent);
            }
        });


        Button btnPressure = (Button) findViewById(R.id.pressBtn);
        btnPressure.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(SensorActivity.this, Pressure.class);

                startActivity(intent);
            }
        });


        SharedPreferences prefs = getSharedPreferences("Myfile", 0);

        contact1 = prefs.getString("contact1", "NA");
        contact2 = prefs.getString("contact2", "NA");
        contact3 = prefs.getString("contact3", "NA");
        pressThresh = prefs.getFloat("pressure", 999);
        pressToggle = prefs.getBoolean("pressureToggle", false);
        tempThresh = prefs.getFloat("temperature", 999);
        tempToggle = prefs.getBoolean("tempToggle", false);

    }

    public void sendSms(String ct1) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + ct1));
        intent.putExtra("sms_body", "DANGER MESSAGE");
        startActivity(intent);

    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            float temper = event.values[0];
            if (temper > tempThresh && tempToggle) {
                sendSms(contact1);
                sendSms(contact2);
                sendSms(contact3);
            }

        }

        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            float press = event.values[0];
            if (press > pressThresh && pressToggle) {
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