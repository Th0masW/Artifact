package com.example.thomas.artifact;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class ViewVideo extends AppCompatActivity {
    final static String TAG = "ViewVideo";
    String studentName;
    String assignmentName;
    String fileName;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private DatabaseReference mAssignmentDB;
    private VideoView myVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_video);

        myVideoView = findViewById(R.id.myVideoView);

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
            //loadVideo();
            //loadVideo2();
        }
    }

    private void loadVideo() {
        //mAssignmentDB = FirebaseDatabase.getInstance().getReference().child("Assignment");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        Log.v(TAG, "Storage Reference - find an o?-: " + storageRef.toString());
        String folderLocation = "videos/" + fileName + ".mp4";
        StorageReference videoRef = storageRef.child(folderLocation);
        Log.v(TAG, "Storage location: " + storageRef.toString());
        Log.v(TAG, "Folder location: " + folderLocation);
        //storageRef.child(fileName + ".mp4").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
        storageRef.child("videos/" + fileName + ".mp4").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Log.v(TAG, "URL: " + uri.getPath());
                myVideoView.setVideoPath(uri.getPath());
                myVideoView.start();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.v(TAG, "FAILED TO DOWNLOAD");
            }
        });

        /*videoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                //myVideoView.setImageBitmap(bitmap);
                //myImageView.setImageBitmap(bytes.);
                //myVideoView.set


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ViewVideo.this,"Error - Video could not be downloaded.", Toast.LENGTH_LONG).show();

            }
        }); */

    }

    private void loadVideo2() {
        //mAssignmentDB = FirebaseDatabase.getInstance().getReference().child("Assignment");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        Log.v(TAG, "Storage Reference - find an o?-: " + storageRef.toString());
        String folderLocation = "videos/" + fileName + ".mp4";
        StorageReference videoRef = storageRef.child(folderLocation);
        Log.v(TAG, "Storage location: " + storageRef.toString());
        Log.v(TAG, "Folder location: " + folderLocation);
        //storageRef.child(fileName + ".mp4").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
        storageRef.child("videos/" + fileName + ".mp4").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                try {
                    Log.v(TAG, "URL: " + uri.getPath());
                    myVideoView.setVideoURI(uri);
                    //myVideoView.setVideoPath(uri.getPath());
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

}
