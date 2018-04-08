package com.example.thomas.artifact;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Random;

public class GetAudio extends AppCompatActivity {
    ImageView myImageView;
    Button save_btn;
    Button cancel_btn;
    Button play_btn;
    Button stop_btn;
    Button record_btn;
    //Bitmap photo;
    String studentName;
    String studentKey;
    String fileLocation;
    EditText assignmentName;
    TextView lblAssign;
    private static final String TAG = "GetAudio";
    private DatabaseReference assignmentDB;
    private DatabaseReference studentNode;
    private StorageReference mStorageRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private static String audioFilePath;
    private static MediaRecorder mediaRecorder;
    private static MediaPlayer mediaPlayer;
    private boolean isRecording = false;
    int YOUR_REQUEST_CODE = 200;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_audio);
        // initialize buttons
        record_btn = (Button) findViewById(R.id.snapBtn);
        myImageView = (ImageView) findViewById(R.id.myImageView);
        save_btn = findViewById(R.id.saveBtn);
        cancel_btn = findViewById(R.id.cancelBtn);
        assignmentName = findViewById(R.id.assignName);
        play_btn = findViewById(R.id.playBtn);
        stop_btn = findViewById(R.id.stopBtn);
        // initialize databases
        studentNode = null;
        assignmentDB = FirebaseDatabase.getInstance().getReference().child("Assignment");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        lblAssign = findViewById(R.id.labelAssign);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        // check for microphone
        if (!hasMicrophone()) {
            // enable buttons
            stop_btn.setEnabled(false);
            play_btn.setEnabled(false);
            record_btn.setEnabled(false);
        } else {
            // disable buttons
            stop_btn.setEnabled(true);
            play_btn.setEnabled(true);
            record_btn.setEnabled(true);
        }

        // get name and key from GetPicture activity
        Intent intent = getIntent();
        if (null != intent) {
            // assign student name and key
            studentName = intent.getStringExtra("name");
            studentKey = intent.getStringExtra("key");
            studentNode = FirebaseDatabase.getInstance().getReference().child("Assignment/" + studentName);
            Log.v("SnapPicture", "Name:" + studentName + ", key:" + studentKey);
            // change title to name
            setTitle("Student: " + studentName);
        }
        // check permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) //check if permission request is necessary
        {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, YOUR_REQUEST_CODE);
        }

        // get audio file path
        String fName = randomFileName(studentName);
        audioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fName;
        fileLocation = fName;

    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(requestCode == YOUR_REQUEST_CODE)
            {
                Log.i(TAG, "Permission granted");
                //do what you wanted to do
            }
        } else {
            Log.d(TAG, "Permission failed");
        }
    }

    protected boolean hasMicrophone() {
        PackageManager pmanager = this.getPackageManager();
        return pmanager.hasSystemFeature(
                PackageManager.FEATURE_MICROPHONE);
    }

    private String randomFileName(String name) {
        String shortName = name;
        String fileName = null;
        shortName.trim();
        shortName = shortName.replaceAll(" ", "_");
        Log.v("RandomName","Short name:"+ shortName);
        Integer num = getRandomNumberInRange(1000,9999);
        fileName = shortName + num.toString() + ".3gp";
        Log.v("RandomFIleName", fileName);
        return fileName;
    }

    private static int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
    ///////////////////////////TESTING/////////////////////////////
    @Override
    public void onStop() {
        super.onStop();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }

        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }
    //////////////////////////////////////////////////////////////

    public void recordAudio (View view) throws IOException
    {
        isRecording = true;
        stop_btn.setEnabled(true);
        play_btn.setEnabled(false);
        record_btn.setEnabled(false);

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            isRecording = true;
            Log.v("GetAudio", "AudioFilePath: " + audioFilePath);
            Log.v("GetAudio", "Now recording");
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
            isRecording = false;
        }

        //mediaRecorder.start();
    }
    public void stopAudio (View view)
    {

        record_btn.setEnabled(false);
        play_btn.setEnabled(true);

        if (isRecording)
        {
            record_btn.setEnabled(false);
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            Log.v("GetAudio", "Stop recording");
        } else {
            //mediaPlayer.release();
            mediaPlayer = null;
            record_btn.setEnabled(true);
        }
    }

    public void playAudio (View view) throws IOException
    {
        play_btn.setEnabled(false);
        record_btn.setEnabled(false);
        stop_btn.setEnabled(true);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(audioFilePath);
        mediaPlayer.prepare();
        mediaPlayer.start();
        Log.v("GetAudio", "Now playing");
    }

    public void submitAudio(View view) {
        String assignName = assignmentName.getText().toString();
        Log.v("SUBMIT IMAGE", "Assignment name:\"" + assignName + "\"");
        // create file name
        fileLocation = randomFileName(studentName);

        // upload to student node
        uploadToNode();
        //upload picture to db
        try{
            uploadToStorage3();
        } catch (FileNotFoundException e) {

        }


        // disable assignment controls
        //assignmentVisibility(false);
        //buttonVisibility(false);
    }

    private void uploadToNode() {
        String assignName = assignmentName.getText().toString();
        // check for name
        if (!assignName.isEmpty()) {
            // upload
            Assignment newAssign = new Audio(studentName, assignName);
            newAssign.setFileName(fileLocation);
            // save to firebase
            HashMap<String, Assignment> datamap = new HashMap<>();
            datamap.put("Assignment",newAssign);
            assignmentDB.push().setValue(newAssign).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(GetAudio.this,"Uploading Assignment...", Toast.LENGTH_LONG).show();
                        // reset image and assignment name
                        //resetAssignment();
                    } else {
                        Toast.makeText(GetAudio.this,"Error...Assignment Not Saved", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Missing: Assignment Name", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadToStorage() {
        String storagePath = storageRef.getPath();
        Log.d(TAG, "Storage path:"+storagePath);
        StorageReference imagesRef = storageRef.child("audio/" + fileLocation);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GetAudio.this,"Error! Could not upload audio file.", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(GetAudio.this,"Success! Audio file has been uploaded.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadToStorage2() {
        String storagePath = storageRef.getPath();
        Log.d("SnapPicture", "Storage path:"+storagePath);
        StorageReference audioRef = storageRef.child("audio/" + fileLocation);
        File file = new File(audioFilePath);

        //UploadTask uploadTask = audioRef.putFile(fileUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //baos.toByteArray(file);
        //photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = audioRef.putBytes(data);
        //UploadTask uploadTask = audioRef.putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GetAudio.this,"Error! Could not upload video.", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(GetAudio.this,"Success! Video has been uploaded.", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void uploadToStorage3() throws FileNotFoundException {
        String storagePath = storageRef.getPath();
        Log.d("SnapPicture", "Storage path:"+storagePath);
        StorageReference audioRef = storageRef.child("audio/" + fileLocation);

        InputStream stream = new FileInputStream(new File(audioFilePath));
        UploadTask uploadTask = audioRef.putStream(stream);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(GetAudio.this,"Error! Could not upload audio file.", Toast.LENGTH_LONG).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(GetAudio.this,"Success! Audio file has been uploaded has been uploaded.", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void cancelBtn(View view) {
        // open activity
        startActivity(new Intent(GetAudio.this, CaptureMyEvidence.class));
    }

}
