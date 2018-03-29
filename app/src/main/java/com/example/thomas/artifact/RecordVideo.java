package com.example.thomas.artifact;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

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
        //File mediaFile = new
               // File(Environment.getExternalStorageDirectory().getAbsolutePath()
               // + "/" + fileLocation);

        ///

        //fileUri = Uri.fromFile(mediaFile);
        ///
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);



    }
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = intent.getData();
            myVideoView.setVideoURI(videoUri);
        }
    }*/
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

    }
}
