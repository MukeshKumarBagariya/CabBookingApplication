package com.example.pickarideapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

public class SignUpActivity extends AppCompatActivity {
    enum STATE{
        SIGNUP,LOGIN
    }
    private STATE state;
    private TextView loginText;
    private RadioButton passengerButton, driverButton;
    private EditText nameText, passwordText;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);
        nameText = findViewById(R.id.nameText);
        passwordText = findViewById(R.id.passwordText);
        signUpButton = findViewById(R.id.signUpButton);
        passengerButton = findViewById(R.id.passengerButton);
        driverButton = findViewById(R.id.driverButton);
        loginText = findViewById(R.id.loginText);
        state = STATE.SIGNUP;
        if (ParseUser.getCurrentUser() != null){
            tranjectionToTheNextActivity();
        }

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == STATE.SIGNUP) {
                    if (passengerButton.isChecked() == false && driverButton.isChecked() == false){
                        Toast.makeText(SignUpActivity.this,"Please make sure that you are a driver or a passenger",Toast.LENGTH_LONG).show();
                        return;
                    }
                        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
                        progressDialog.setMessage("Loading");
                        progressDialog.show();

                        ParseUser parseUser = new ParseUser();
                        parseUser.setUsername(nameText.getText().toString());
                        parseUser.setPassword(passwordText.getText().toString());
                        if (passengerButton.isChecked()){
                            parseUser.put("as","Passenger");
                        } else if (driverButton.isChecked()){
                            parseUser.put("as","Driver");
                        }
                        parseUser.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    tranjectionToTheNextActivity();
                                } else {
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivity.this);
                                    alertDialog.setMessage("Something went wrong or please check your internet connection");
                                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //
                                        }
                                    });
                                    AlertDialog dialog = alertDialog.create();
                                    dialog.show();
                                }
                            }
                        });
                    progressDialog.dismiss();
                } else if (state == STATE.LOGIN){
                    final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
                    progressDialog.setMessage("Loading");
                    progressDialog.show();

                    ParseUser parseUser = new ParseUser();
                            parseUser.logInInBackground(nameText.getText().toString(), passwordText.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
                            progressDialog.setMessage("Loading");
                            progressDialog.show();
                            if (e == null){

                                tranjectionToTheNextActivity();
                            } else {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivity.this);
                                alertDialog.setMessage("Something went wrong or please check your internet connection");
                                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //
                                    }
                                });
                                AlertDialog dialog = alertDialog.create();
                                dialog.show();
                            }
                            progressDialog.dismiss();
                        }
                    });

                }

            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == STATE.SIGNUP){
                    state = STATE.LOGIN;
                    signUpButton.setText("Login");
                    loginText.setText("SignUP");
                    passengerButton.animate().alpha(0.0f);
                    driverButton.animate().alpha(0.0f);
                } else if (state == STATE.LOGIN){
                    state = STATE.SIGNUP;
                    signUpButton.setText("SgnUp");
                    loginText.setText("Login");
                    passengerButton.animate().alpha(1.0f);
                    driverButton.animate().alpha(1.0f);

                }
            }
        });

    }
    private void tranjectionToTheNextActivity(){
        if (ParseUser.getCurrentUser() != null){
            if (ParseUser.getCurrentUser().get("as").equals("Driver")){
                Intent intent = new Intent(SignUpActivity.this, DriverActivity.class);
                startActivity(intent);
            } else if (ParseUser.getCurrentUser().get("as").equals("Passenger")){
                Intent intent = new Intent(SignUpActivity.this, PassengerActivity.class);
                startActivity(intent);

            }
        }
    }
}
