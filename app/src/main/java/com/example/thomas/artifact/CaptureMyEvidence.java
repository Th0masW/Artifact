package com.example.thomas.artifact;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CaptureMyEvidence extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_my_evidence);
        setTitle("Capture Evidence");

        //Make sure user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //Redirect to login page
        if(currentUser == null) {
            //create login intent
            Intent loginIntent = new Intent(CaptureMyEvidence.this, Login.class);
            startActivity(loginIntent);
            finish();
        }
    }

    public void openVideo(View view) {
        startActivity(new Intent(CaptureMyEvidence.this, GetVideo.class));
    }

    public void openAudio(View view) {
        startActivity(new Intent(CaptureMyEvidence.this, AudioSelectStudent.class));
    }

    public void openPicture(View view) {
        startActivity(new Intent(CaptureMyEvidence.this, GetPicture.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.home) {
            sendToMain();
        } else if(item.getItemId() == R.id.capture_evidence) {
            Intent go = new Intent(CaptureMyEvidence.this, CaptureMyEvidence.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.review_evidence) {
            Intent go = new Intent(CaptureMyEvidence.this, ReviewEvidence.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.add_students) {
            Intent go = new Intent(CaptureMyEvidence.this, AddStudents.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.edit_student) {
            Intent go = new Intent(CaptureMyEvidence.this, PickStudent.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.log_out) {
            FirebaseAuth.getInstance().signOut();
            sendToMain();
        }

        return true;
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(CaptureMyEvidence.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
