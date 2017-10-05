package com.example.asstmobileapp;

import android.app.Notification;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    private SensorManager sManager;
    private Sensor mSensorAccelerometer;
    private NotificationManager mNotificationManager;
    private int notificationID = 100;
    private int totalMessages = 0;
    private TextView tv_step;
    private Button button;
    private Button btn_start;
    private int step = 0;   //steps
    private int tempStep = 0;//control the warning toast
    private double oriValue = 0;  //original value
    private double lstValue = 0;  //last value
    private double curValue = 0;  //current value
    private boolean motiveState = true;   //moving status
    private boolean processState = false;   //step counts status
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_count);
        startCountDownTime(10);

        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorAccelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_UI);
        bindViews();
    }

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
                                                if ( tempStep > 20) {
                                                    displayNotification();
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


                            //Toast.makeText(getApplicationContext(), "Please watch out your speed!",
                                   // Toast.LENGTH_SHORT).show();


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

    void displayNotification() {
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(
                this);
        nBuilder.setContentTitle("Notification");
        nBuilder.setContentText("Please slow down!");
        nBuilder.setTicker("New Message");
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
