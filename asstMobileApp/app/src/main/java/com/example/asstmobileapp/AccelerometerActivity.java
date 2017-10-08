package com.example.asstmobileapp;

import android.Manifest;
import android.app.Notification;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.app.TaskStackBuilder;
import android.support.v4.app.NotificationCompat;
import android.location.Location;
import android.provider.Settings;
import android.support.annotation.NonNull;
import java.util.List;
import java.util.Locale;


import java.util.List;

public class AccelerometerActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    private SensorManager sManager;
    private Sensor mSensorAccelerometer;
    private NotificationManager mNotificationManager;
    private int notificationID = 100;
    private TextView tv_step;
    private Button btn_start;
    private TextView tv_speed;
    private int step = 0;   //steps
    private int tempStep = 0;//control the warning toast
    private double oriValue = 0;  //original value
    private double lstValue = 0;  //last value
    private double curValue = 0;  //current value
    private boolean motiveState = true;   //moving status
    private boolean processState = false;   //step counts status
    private static final String TAG = "AccelerometerActivity";

    //sensor thresholds and on/off status
    private String tempThresh;
    private String pressThresh;
    private boolean pressToggle;
    private boolean tempToggle;

    //location variables
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double longitude = 0;
    private double latitude = 0;

    Geocoder geocoder;
    List<Address> addresses;
    String address = "n/a";

    //emergency contatcs
    private String contact1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        startCountDownTime(10);

        SharedPreferences prefs = getSharedPreferences("SafetyApp", 0);

        contact1 = prefs.getString("contact1", "NA");
        pressThresh = prefs.getString("pressure", "999");
        pressToggle = prefs.getBoolean("pressureToggle", false);
        tempThresh = prefs.getString("temperaturev1", "999");
        tempToggle = prefs.getBoolean("tempToggle", false);

        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorAccelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_UI);
        bindViews();

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

    //count down 10s
    private void startCountDownTime(long time) {
        final CountDownTimer timer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "onTick  " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                tempStep = 0;
                start();
                Log.d(TAG, "onFinish");
            }
        };
        timer.start();
        //timer.cancel();
    }


    private void bindViews() {

        tv_step = (TextView) findViewById(R.id.tv_step);
        btn_start = (Button) findViewById(R.id.btn_start);
        tv_speed = (TextView) findViewById(R.id.speedControl);
        btn_start.setOnClickListener(this);
    }




    @Override
    public void onSensorChanged(SensorEvent event) {
        double range = 10;   //set a range
        float[] value = event.values;
        curValue = magnitude(value[0], value[1], value[2]);   //calculate the vector


        //accelerometer up
        if (motiveState == true) {

            if (curValue >= lstValue) lstValue = curValue;
            else {
                //detect a peek value
                if (Math.abs(curValue - lstValue) > range) {
                    oriValue = curValue;
                    motiveState = false;
                }
            }
        }

        //accelerometer down
        if (motiveState == false) {
            if (curValue <= lstValue) lstValue = curValue;
            else {
                if (Math.abs(curValue - lstValue) > range) {
                    //detect a peek value
                    oriValue = curValue;

                    if (processState == true) {
                        step++;  //step + 1
                        tempStep++;
                        if (processState == true) {
                            tv_step.setText(step + "");    //update counts
                        }

                            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... params) {
                                    try {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if ( tempStep > Integer.parseInt(tv_speed.getText().toString())) {
                                                    displayNotification();
                                                    sendSms(contact1);
                                                    tempToggle = false;
                                                    pressToggle = false;
                                                }
                                            }
                                        });
                                    } catch (final Exception e) {
                                        createAndShowDialogFromTask(e, "Error");
                                    }

                                    return null;
                                }
                            };

                            runAsyncTask(task);
                    }
                    motiveState = true;
                }
            }
        }
    }

    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }

    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(exception, title);
    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    public void sendSms(String ct1) {

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(ct1,null, String.format("Danger! This person is in an accident at %s\nLONG:%f\nLAT:%f",address,longitude,latitude),null,null);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onClick(View v) {
        step = 0;
        tv_step.setText("0");
        if (processState == true) {
            btn_start.setText("Start");
            processState = false;
        } else {
            btn_start.setText("Stop");
            processState = true;
        }
    }

    //pop up a notification when something happens
    void displayNotification() {
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(
                this);
        nBuilder.setContentTitle("Warning");
        nBuilder.setContentText("Please slow down!");
        nBuilder.setTicker("Speed Message");
        nBuilder.setAutoCancel(true);
        nBuilder.setSmallIcon(R.drawable.ic_launcher);
        nBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        //nBuilder.setNumber(++totalMessages);

        Intent intent = new Intent(this, NotificationClass.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotificationClass.class);

        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.setContentIntent(pendingIntent);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationID, nBuilder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //vector
    public double magnitude(float x, float y, float z) {
        double magnitude = 0;
        magnitude = Math.sqrt(x * x + y * y + z * z);
        return magnitude;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sManager.unregisterListener(this);
    }
}
