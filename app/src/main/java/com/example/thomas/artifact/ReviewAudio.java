package com.example.thomas.artifact;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class ReviewAudio extends AppCompatActivity {
    final static String TAG = "ReviewAudio";
    String studentName;
    String assignmentName;
    String fileName;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private static MediaPlayer mediaPlayer;
    private boolean isRecording = false;
    private String audioFilePath;
    private String downloadUrl;
    private File localFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_audio);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
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
            try {
                loadAudio2();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadAudio() {
        Log.v(TAG, "File name: " + fileName);
        String folderLocation = "audio/" + fileName;
        Toast.makeText(this, "Downloading file...", Toast.LENGTH_SHORT).show();
        storageRef.child(folderLocation).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // get download
                try {
                    Log.v(TAG, "URL: " + uri.getPath());
                    Log.v(TAG, "Uri path: " + uri.getPath());

                    audioFilePath = uri.getPath();
                    mediaPlayer.setDataSource(ReviewAudio.this, uri);
                    Log.v(TAG, "Audio file path: " + audioFilePath);
                    Toast.makeText(ReviewAudio.this, "Audio file downloaded", Toast.LENGTH_SHORT).show();
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
    // try to download to local file
    private void loadAudio2() throws IOException {
        Log.v(TAG, "File name: " + fileName);
        String folderLocation = "audio/" + fileName;
        localFile = File.createTempFile("audio", ".3gp");
        storageRef.child(folderLocation).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Log.v(TAG, "Local file path:"+ localFile.getAbsolutePath());
                try {
                    mediaPlayer.setDataSource(localFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        Toast.makeText(this, "Downloading file...", Toast.LENGTH_SHORT).show();

    }
    public void playAudio(View view) {
        mediaPlayer.start();

    }

    public void stopAudio (View view)
    {
        if (isRecording)
        {
            isRecording = false;
            Log.v("GetAudio", "Stop recording");
        } else {
            mediaPlayer = null;
        }
    }
}
