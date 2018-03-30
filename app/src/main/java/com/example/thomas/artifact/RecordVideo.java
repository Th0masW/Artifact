package com.example.thomas.artifact;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

public class RecordVideo extends AppCompatActivity {
    private static final int VIDEO_CAPTURE = 101;
    static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final String TAG = "RecordVideo";
    VideoView myVideoView;
    Button save_btn;
    Button cancel_btn;
    Bitmap photo;
    String studentName;
    String studentKey;
    String fileLocation;
    EditText assignmentName;
    TextView lblAssign;
    MediaController mediaC;
    private Uri fileUri;
    private DatabaseReference assignmentDB;
    private DatabaseReference studentNode;
    private StorageReference mStorageRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private MediaStore.Video video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);

        Button myBtn = findViewById(R.id.btnCamera);
        myVideoView = findViewById(R.id.myVideoView);
        save_btn = findViewById(R.id.saveBtn);
        cancel_btn = findViewById(R.id.cancelBtn);
        assignmentName = findViewById(R.id.assignName);
        studentNode = null;
        fileUri = null;
        video = null;
        mediaC = new MediaController(this);
        assignmentDB = FirebaseDatabase.getInstance().getReference().child("Assignment");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        lblAssign = findViewById(R.id.labelAssign);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        // get name and key from GetPicture activity
        Intent intent = getIntent();
        if (null != intent) {
            // assign student name and key
            studentName = intent.getStringExtra("name");
            studentKey = intent.getStringExtra("key");
            studentNode = FirebaseDatabase.getInstance().getReference().child("Assignment/" + studentName);
            Log.v(TAG, "Name:" + studentName + ", key:" + studentKey);
            // change title to name
            setTitle("Student: " + studentName);
        }

        // check for camera
        if(!hasCamera()) {
            myBtn.setEnabled(false);
        }
        // make save and cancel invisible
        buttonVisibility(false);
        // make assignment invisible
        assignmentVisibility(false);
    }

    // check for camera
    private boolean hasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private void buttonVisibility(boolean x) {
        if (x == true) {
            // make save and cancel visible
            save_btn.setVisibility(View.VISIBLE);
            cancel_btn.setVisibility(View.VISIBLE);
        } else {
            // make save and cancel invisible
            save_btn.setVisibility(View.INVISIBLE);
            cancel_btn.setVisibility(View.INVISIBLE);
        }
    }
    private void assignmentVisibility(boolean x) {
        if (x == true) {
            // make assignment options visible
            lblAssign.setVisibility(View.VISIBLE);
            assignmentName.setVisibility((View.VISIBLE));

        } else {
            // make save and cancel invisible
            lblAssign.setVisibility(View.INVISIBLE);
            assignmentName.setVisibility(View.INVISIBLE);
        }
    }

    public void cancelBtn(View view) {
        // open activity
        startActivity(new Intent(RecordVideo.this, GetVideo.class));
    }

    // launch camera app
    public void launchCamera(View view) {
        // create random file name
        NameGenerator ng = new NameGenerator(studentName,"mp4");
        fileLocation = ng.getFileName();
        Log.v(TAG, "File name generated:" + fileLocation);
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, VIDEO_CAPTURE);

        // set media controller
        myVideoView.setMediaController(mediaC);
        mediaC.setAnchorView(myVideoView);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Video saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
                fileLocation = data.getData().toString();
                Log.v(TAG, "New file location:"+ fileLocation);
                // populate viewer
                fileUri = data.getData();
                myVideoView.setVideoURI(fileUri);

                // make save and cancel visible
                buttonVisibility(true);
                // make assignment invisible
                assignmentVisibility(true);
                // start video
                myVideoView.start();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record video",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void submitVideo(View view) {
        String assName = assignmentName.getText().toString();
        Log.v("SUBMIT IMAGE", "Assignment name:\"" + assName + "\"");

        // upload to student node
        uploadToNode();
        //upload picture to db
        uploadToStorage();
        // disable assignment controls
        assignmentVisibility(false);
        buttonVisibility(false);
    }

    private void uploadToNode() {
        String assName = assignmentName.getText().toString();
        // check for name
        if (!assName.isEmpty()) {
            // upload
            Assignment newAssign = new Videos(studentName, assName);
            newAssign.setFileName(fileUri.getLastPathSegment());
            // save to firebase
            HashMap<String, Assignment> datamap = new HashMap<String,Assignment>();
            datamap.put("Assignment",newAssign);
            assignmentDB.push().setValue(newAssign).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RecordVideo.this,"Your video is being uploaded. This may take some time.", Toast.LENGTH_LONG).show();
                        // reset image and assignment name
                        resetAssignment();
                    } else {
                        Toast.makeText(RecordVideo.this,"Error...Assignment Not Saved", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Missing: Assignment Name", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetAssignment() {
        assignmentName.setText("");
        myVideoView.setVideoURI(null);
    }

    private void uploadToStorage() {
        String storagePath = storageRef.getPath();
        File myVideo = new File(fileUri.getPath());
        Log.d("SnapPicture", "Storage path:"+storagePath);
        //StorageReference videoRef = storageRef.child("videos/" + fileLocation);
        StorageReference videoRef = storageRef.child("videos/" + fileUri.getLastPathSegment());
        UploadTask uploadTask = videoRef.putFile(fileUri);
        //videoRef.put;
        //UploadTask uploadTask = videoRef.putFile(myVideo.toURI().toString());
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RecordVideo.this,"Error! Could not upload video.", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(RecordVideo.this,"Success! Video has been uploaded.", Toast.LENGTH_LONG).show();
            }
        });

    }
}
