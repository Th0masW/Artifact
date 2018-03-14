package com.example.thomas.artifact;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    public void openEvidenceCapture(View view) {
        startActivity(new Intent(MainActivity.this, CaptureMyEvidence.class));
    }
    public void openReviewEvidence(View view) {
        startActivity(new Intent(MainActivity.this, ReviewEvidence.class));
    }

    private void doSomething() {
        String tag = "Do Something";
        String msg = "Say something";
        Log.e(tag, msg);
        Log.v(tag, "This is a verbose log");
        Log.wtf(tag, "WTF!");


    }

}
