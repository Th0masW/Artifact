package com.example.thomas.artifact;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    //Init buttons and fields
    private EditText loginEmailText;
    private EditText loginPasswordText;
    private Button loginBtn;
    private ProgressBar loginProgress;
    //Create variable for is user authorized
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        loginEmailText = (EditText) findViewById(R.id.login_email);
        loginPasswordText = (EditText) findViewById(R.id.login_password);
        loginBtn = (Button) findViewById(R.id.login_btn);
        loginProgress = (ProgressBar) findViewById(R.id.progressBar);
        //clicking the login button to validte
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get email and password
                String loginEmail = loginEmailText.getText().toString();
                String loginPass = loginPasswordText.getText().toString();

               if(!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass)) {
                   loginProgress.setVisibility(View.VISIBLE);
                   mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {

                           if(task.isSuccessful()) {
                            //send to main if valid
                               sendToMain();
                           } else {
                            //failed, display errro
                               String errorMessage = task.getException().getMessage();
                               Toast.makeText(Login.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                                // reset progress bar
                               loginProgress.setVisibility(View.INVISIBLE);
                           }


                       }
                   });
               }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        //Check if user is logged in, if so, send to main
        if(currentUser == null) {
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(Login.this, MainActivity.class);
        finish();
    }

}
