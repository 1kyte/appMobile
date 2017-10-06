package com.example.asstmobileapp;

/**
 * Created by Kallen on 6/10/2017.
 */
//import android.support.v4.util.Pair;
import android.accounts.Account;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private TextView account_text;
    private TextView password_text;
    private Button login_bt;
    Intent intent;
    private MobileServiceClient mClient;
    //private String result;
    @Override
    //Loading function since frame on create
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        intent = getIntent();
        Log.d("API","load frame" );

        //try to connect to Azure cloud server
        try{
            mClient = new MobileServiceClient("https://asstmobileapp.azurewebsites.net", this);
        }
        catch (Exception e){
            Log.e("API","Exception", e);
        }

        //Get value from text view
        account_text = (TextView) findViewById(R.id.account);
        password_text = (TextView) findViewById(R.id.password);
        login_bt = (Button) findViewById(R.id.login);

        //Start button on click listener
        login_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("API","Button click" );
                String account = account_text.getText().toString();
                String password = password_text.getText().toString();
                LoginInfo accountInfo = new LoginInfo();
                accountInfo.setAccount(account);
                accountInfo.setPassword(password);

                //put values into array list
                ArrayList<Pair<String,String>> parameters = new ArrayList<>();
                parameters.add(new Pair<>("account",account));
                parameters.add(new Pair<>("password",password));
                Log.d("API","parameters is:" + parameters.toString());

                //try to send http to invoke Azure custom controller
                try{
                    ListenableFuture<String> result = mClient.invokeApi("app", "GET", parameters, String.class);
                    Log.d("API","return value is: " + result.toString());

                    Futures.addCallback(result, new FutureCallback<String>() {
                        @Override
                        //when call Azure api success
                        public void onSuccess(String s) {
                            Log.d("API","return value is: " + s.toString());

                            if(s.toString().equals("Login Success!")){
                                Log.d("API","jump back to main page...");

                                //display login successful message
                                Toast toast=Toast.makeText(getApplicationContext(), s.toString(), Toast.LENGTH_SHORT);
                                toast.show();
                                Intent intent = new Intent(LoginActivity.this, AccelerometerActivity.class);
                                startActivity(intent);
                            }else
                            {
                                //display login faild message
                                Toast toast=Toast.makeText(getApplicationContext(), s.toString(), Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }

                        @Override
                        //Failed to call Azure API
                        public void onFailure(Throwable throwable) {
                            Log.d("API","call back on failure ");
                            Log.e("API","Exception", throwable);
                        }
                    });

                }
                catch (Exception e){
                    Log.d("API","Exception get services respond" + e.toString());
                }

//                intent.putExtra("account",account);
//                setResult(1,intent);
//                finish();
            }
        });
    }
}
