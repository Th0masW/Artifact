package com.example.thomas.artifact;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
// thomas was here
// and again  ....

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int i;
        doSomething();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser == null) {
            //create login intent
            Intent loginIntent = new Intent(MainActivity.this, Login.class);
            startActivity(loginIntent);
            finish();
        }
    }


    public void openEvidenceCapture(View view) {
        startActivity(new Intent(MainActivity.this, CaptureMyEvidence.class));
    }
    public void openReviewEvidence(View view) {
        startActivity(new Intent(MainActivity.this, ReviewEvidence.class));
    }

    public void openAddStudents(View view) {
        startActivity(new Intent(MainActivity.this, AddStudents.class));
    }

    private void doSomething() {
        String tag = "Do Something";
        String msg = "Say something";
        Log.e(tag, msg);
        Log.v(tag, "This is a verbose log");
        Log.wtf(tag, "WTF!");


    }

}
