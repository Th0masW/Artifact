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
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import android.view.Menu;
import android.view.MenuItem;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

public class RecordVideo extends AppCompatActivity {
    private static final int VIDEO_CAPTURE = 101;
    private static final String TAG = "RecordVideo";
    VideoView myVideoView;
    Button save_btn;
    Button cancel_btn;
    String studentName;
    String studentKey;
    String fileLocation;
    EditText assignmentName;
    TextView lblAssign;
    MediaController mediaC;
    private Uri fileUri;
    private DatabaseReference assignmentDB;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Make sure user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //Redirect to login page
        if(currentUser == null) {
            //create login intent
            Intent loginIntent = new Intent(RecordVideo.this, Login.class);
            startActivity(loginIntent);
            finish();
        }
        setContentView(R.layout.activity_record_video);

        Button myBtn = findViewById(R.id.btnCamera);
        myVideoView = findViewById(R.id.myVideoView);
        save_btn = findViewById(R.id.saveBtn);
        cancel_btn = findViewById(R.id.cancelBtn);
        assignmentName = findViewById(R.id.assignName);
        fileUri = null;
        mediaC = new MediaController(this);
        assignmentDB = FirebaseDatabase.getInstance().getReference().child("Assignment");
        lblAssign = findViewById(R.id.labelAssign);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        // get name and key from GetPicture activity
        Intent intent = getIntent();
        if (null != intent) {
            // assign student name and key
            studentName = intent.getStringExtra("name");
            studentKey = intent.getStringExtra("key");
            // change title to name
            setTitle("Record video of " + studentName);
        }

        // check for camera
        if(!hasCamera()) {
            myBtn.setEnabled(false);
        }
        // make save and cancel invisible
        buttonVisibility(false);
        // make assignment invisible
        assignmentVisibility(false);
        // launch camera off the bat
        launchCamera();
    }

    // check for camera
    private boolean hasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private void buttonVisibility(boolean x) {
        if (x) {
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
        if (x) {
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
        Log.v(TAG, "New file location:"+ fileLocation);
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, VIDEO_CAPTURE);

        // set media controller
        myVideoView.setMediaController(mediaC);
        mediaC.setAnchorView(myVideoView);
    }

    private void launchCamera() {
        // create random file name
        NameGenerator ng = new NameGenerator(studentName,"mp4");
        fileLocation = ng.getFileName();
        Log.v(TAG, "File name generated:" + fileLocation);
        Log.v(TAG, "New file location:"+ fileLocation);
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

        //Make sure name field is not blank
        if(TextUtils.isEmpty(assName)) {
            assignmentName.setError("The name field must have a value.");
        }
        else {
        // upload to student node
        uploadToNode();
        //upload picture to db
        uploadToStorage();
        // disable assignment controls
        assignmentVisibility(false);
        buttonVisibility(false); }
    }

    private void uploadToNode() {
        String assName = assignmentName.getText().toString();
        // check for name
        if (!assName.isEmpty()) {
            // upload
            Assignment newAssign = new Videos(studentName, assName);
            newAssign.setFileName(fileUri.getLastPathSegment());
            // save to firebase
            HashMap<String, Assignment> datamap = new HashMap<>();
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
        Log.d("SnapPicture", "Storage path:"+storagePath);
        StorageReference videoRef = storageRef.child("videos/" + fileUri.getLastPathSegment() + ".mp4");
        UploadTask uploadTask = videoRef.putFile(fileUri);
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
            Intent go = new Intent(RecordVideo.this, CaptureMyEvidence.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.review_evidence) {
            Intent go = new Intent(RecordVideo.this, ReviewEvidence.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.add_students) {
            Intent go = new Intent(RecordVideo.this, AddStudents.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.edit_student) {
            Intent go = new Intent(RecordVideo.this, PickStudent.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.log_out) {
            FirebaseAuth.getInstance().signOut();
            sendToMain();
        }

        return true;
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(RecordVideo.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
