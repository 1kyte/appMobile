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
    EditText t1;
    EditText t2;
    EditText t3;

    public static final String PREFS_NAME = "Myfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Button btnSettings = (Button) findViewById(R.id.button);
        t1 = (EditText) findViewById(R.id.ct1);
        t2 = (EditText) findViewById(R.id.ct2);
        t3 = (EditText) findViewById(R.id.ct3);


        SharedPreferences prefs = getSharedPreferences("Myfile", 0);
        String ct1 = prefs.getString("contact1", "default_value");
        String ct2 = prefs.getString("contact2", "default_value");
        String ct3 = prefs.getString("contact3", "default_value");

        t1.setText(ct1);
        t2.setText(ct2);
        t3.setText(ct3);

        t1.setText(ct1);
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
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("contact1", t1.getText().toString());
        editor.putString("contact2", t2.getText().toString());
        editor.putString("contact3", t3.getText().toString());


        editor.commit();

    }
}
