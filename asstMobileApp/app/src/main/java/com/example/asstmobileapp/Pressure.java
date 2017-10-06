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

    ToggleButton togglebtn;
    boolean pressureStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pressure);

        Button btnSettings = (Button) findViewById(R.id.save2);
        btnSettings.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SensorActivity.class);

                startActivityForResult(intent, 0);
            }
        });

        togglebtn = (ToggleButton) findViewById(R.id.togglePress);
        togglebtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                togglefcn();
            }
        });
    }

    @Override
    protected  void onPause(){

        super.onPause();

        EditText t1 = (EditText) findViewById(R.id.temp);
        SharedPreferences prefs = getSharedPreferences("myfile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("pressure", t1.toString());
        editor.putBoolean("pressureToggle", pressureStatus);
        editor.commit();

    }

    public void togglefcn() {
        boolean isOn = togglebtn.isChecked();

        if (isOn == true) {

            pressureStatus = true;
        } else {
            pressureStatus = false;
        }
    }
}