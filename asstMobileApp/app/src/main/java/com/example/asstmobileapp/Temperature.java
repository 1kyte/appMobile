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


public class Temperature extends AppCompatActivity {
    ToggleButton togglebtn;
    boolean tempStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);


        Button btnSettings = (Button) findViewById(R.id.save);
        btnSettings.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SensorActivity.class);

                startActivityForResult(intent, 0);
            }
        });

        togglebtn = (ToggleButton) findViewById(R.id.toggletemp);
        togglebtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                togglefcn();
            }
        });

        SharedPreferences prefs = getSharedPreferences("myfile", Context.MODE_PRIVATE);
        String tempThresh = prefs.getString("temperaturev1", "");
        boolean tempToggle = prefs.getBoolean("tempToggle", false);

        EditText t1 = (EditText) findViewById(R.id.temp);
        t1.setText(tempThresh);



    }


    @Override
    protected  void onPause(){

        super.onPause();

        EditText t1 = (EditText) findViewById(R.id.temp);
        SharedPreferences prefs = getSharedPreferences("Myfile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("temperaturev1", t1.getText().toString());
        editor.putBoolean("tempToggle", tempStatus);
        editor.commit();

    }

    public void togglefcn() {
        boolean isOn = togglebtn.isChecked();

        if (isOn == true) {

            tempStatus = true;
        } else {
            tempStatus = false;
        }
    }
}
