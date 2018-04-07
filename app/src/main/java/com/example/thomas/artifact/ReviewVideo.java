package com.example.thomas.artifact;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ReviewVideo extends AppCompatActivity {
    final static String TAG = "ReviewVideo";
    String studentName;
    String assignmentName;
    String fileName;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private VideoView myVideoView;
    MediaController mediaC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Make sure user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //Redirect to login page
        if(currentUser == null) {
            //create login intent
            Intent loginIntent = new Intent(ReviewVideo.this, Login.class);
            startActivity(loginIntent);
            finish();
        }
        setContentView(R.layout.activity_review_video);
        // setup video player
        myVideoView = findViewById(R.id.myVideoView);
        mediaC = new MediaController(this);
        // get name and key from GetPicture activity
        Intent intent = getIntent();
        if (null != intent) {
            // assign student name and key
            studentName = intent.getStringExtra("name");
            assignmentName = intent.getStringExtra("assignment");
            fileName = intent.getStringExtra("file");
            // change title to name
            setTitle(studentName + ": \"" + assignmentName);
            // load image
            loadVideo();
        }
        // set media controller
        myVideoView.setMediaController(mediaC);
        mediaC.setAnchorView(myVideoView);
    }

    private void loadVideo() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        Log.v(TAG, "Storage Reference - find an o?-: " + storageRef.toString());
        String folderLocation = "videos/" + fileName + ".mp4";
        Log.v(TAG, "Storage location: " + storageRef.toString());
        Log.v(TAG, "Folder location: " + folderLocation);
        storageRef.child("videos/" + fileName + ".mp4").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // get download
                try {
                    Log.v(TAG, "URL: " + uri.getPath());
                    myVideoView.setVideoURI(uri);
                    myVideoView.requestFocus();
                    myVideoView.start();
                } catch (Exception e){
                    Log.d(TAG,e.toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.v(TAG, "FAILED TO DOWNLOAD");
            }
        });
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
            Intent go = new Intent(ReviewVideo.this, CaptureMyEvidence.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.review_evidence) {
            Intent go = new Intent(ReviewVideo.this, ReviewEvidence.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.add_students) {
            Intent go = new Intent(ReviewVideo.this, AddStudents.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.edit_student) {
            Intent go = new Intent(ReviewVideo.this, PickStudent.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.log_out) {
            FirebaseAuth.getInstance().signOut();
            sendToMain();
        }

        return true;
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(ReviewVideo.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
