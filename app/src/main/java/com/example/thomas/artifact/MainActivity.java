package com.example.thomas.artifact;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
// thomas was here
// and again  ....

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int i;

    }

    public void openEvidenceCapture(View view) {
        startActivity(new Intent(MainActivity.this, CaptureMyEvidence.class));

    }


}
