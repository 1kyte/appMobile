package com.example.asstmobileapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class Contacts extends AppCompatActivity {
    //Class for saving the emergency contacts of the user




    //Global variables for the text areas that take the input of emergency contacts
    EditText t1;
    EditText t2;
    EditText t3;

    //file that stores all saved settings of the app
    public static final String PREFS_NAME = "SafetyApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        //finds the save button
        Button btnSettings = (Button) findViewById(R.id.button);

        //find the text areas for input
        t1 = (EditText) findViewById(R.id.ct1);
        t2 = (EditText) findViewById(R.id.ct2);
        t3 = (EditText) findViewById(R.id.ct3);

        //load previously saved contacts
        SharedPreferences prefs = getSharedPreferences("SafetyApp", 0);
        String ct1 = prefs.getString("contact1", "");
        String ct2 = prefs.getString("contact2", "");
        String ct3 = prefs.getString("contact3", "");

        t1.setText(ct1);
        t2.setText(ct2);
        t3.setText(ct3);

        //on save go to sensor activity
        btnSettings.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SensorActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }
    @Override
    protected  void onPause(){

        super.onPause();

        //save the chosen contacts into the file
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("contact1", t1.getText().toString());
        editor.putString("contact2", t2.getText().toString());
        editor.putString("contact3", t3.getText().toString());


        editor.commit();


    }
}
