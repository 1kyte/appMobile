package com.example.asstmobileapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;


public class Pressure extends AppCompatActivity {

    //boolean for on/off status and toggle button that sets this value
    ToggleButton togglebtn;
    boolean pressureStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pressure);
        //go to sensor activity on saving
        Button btnSettings = (Button) findViewById(R.id.save2);
        btnSettings.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SensorActivity.class);

                startActivityForResult(intent, 0);
            }
        });
        //find on/off toggle button
        togglebtn = (ToggleButton) findViewById(R.id.togglePress);
        togglebtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                togglefcn();
            }
        });

        //load previously saved threshold
        SharedPreferences prefs = getSharedPreferences("SafetyApp", Context.MODE_PRIVATE);
        String pressThresh = prefs.getString("pressure", "60");

        EditText t1 = (EditText) findViewById(R.id.pressure);
        t1.setText(pressThresh);
    }

    @Override
    protected  void onPause(){

        super.onPause();
        //saving chosen settings into the file (which include on/off and the chosen threshold
        EditText t1 = (EditText) findViewById(R.id.pressure);
        SharedPreferences prefs = getSharedPreferences("SafetyApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("pressure", t1.getText().toString());
        editor.putBoolean("pressureToggle", pressureStatus);
        editor.commit();

    }

    //function to change the boolean value when toggling on/off
    public void togglefcn() {
        boolean isOn = togglebtn.isChecked();

        if (isOn == true) {

            pressureStatus = true;
        } else {
            pressureStatus = false;
        }
    }
}
