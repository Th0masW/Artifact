package com.example.thomas.artifact;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class CaptureMyEvidence extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_my_evidence);
        setTitle("Capture Evidence");
    }

    public void openVideo(View view) {
        startActivity(new Intent(CaptureMyEvidence.this, GetVideo.class));
    }

    public void openAudio(View view) {
        startActivity(new Intent(CaptureMyEvidence.this, GetAudio.class));
    }

    public void openPicture(View view) {
        startActivity(new Intent(CaptureMyEvidence.this, GetPicture.class));
    }
}
