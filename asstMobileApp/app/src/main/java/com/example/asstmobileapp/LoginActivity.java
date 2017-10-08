package com.example.asstmobileapp;

/**
 * Created by Kallen on 6/10/2017.
 */
//import android.support.v4.util.Pair;
import android.accounts.Account;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
   // Intent intent;
    private MobileServiceClient mClient;
    @Bind(R.id.account) EditText account_text;
    EditText _emailText;
    @Bind(R.id.password) EditText password_text;
    @Bind(R.id.login) Button login_bt;

    @Override
    //Loading function since frame on create
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ButterKnife.bind(this);
        login_bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    public void login(){
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        login_bt.setEnabled(false);

        //intent = getIntent();
        Log.d("API","load frame" );

        //try to connect to Azure cloud server
        try{
            mClient = new MobileServiceClient("https://asstmobileapp.azurewebsites.net", this);
        }
        catch (Exception e){
            Log.e("API","Exception", e);
        }



        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        //Get value from text view
        String account = account_text.getText().toString();
        String password = password_text.getText().toString();


        //Start button on click listener

                Log.d("API","Button click" );
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


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);


        }

    @Override
    public void onBackPressed() {
        // Disable going back to the Accelerometer
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        login_bt.setEnabled(true);
        finish();
        Intent intent = new Intent(this, SensorActivity.class);
        startActivity(intent);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        login_bt.setEnabled(true);
    }
    public boolean validate() {
        boolean valid = true;

        String email = account_text.getText().toString();
        String password = password_text.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            account_text.setError("enter a valid email address");
            valid = false;
        } else {
            account_text.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            password_text.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            password_text.setError(null);
        }

        return valid;
    }
}