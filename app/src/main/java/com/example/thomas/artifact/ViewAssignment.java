package com.example.thomas.artifact;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewAssignment extends AppCompatActivity {
    String studentName;
    String assignmentName;
    String fileName;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private DatabaseReference mAssignmentDB;
    private ImageView myImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_assignment);

        myImageView = findViewById(R.id.myImageView);
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
            loadImage();
        }
    }
    private void loadImage() {
        //mAssignmentDB = FirebaseDatabase.getInstance().getReference().child("Assignment");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("images/" + fileName);
        final long ONE_MEGABYTE = 1024 * 1024;

        imagesRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                myImageView.setImageBitmap(bitmap);
                //myImageView.setImageBitmap(bytes.);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ViewAssignment.this,"Error - Image could not be downloaded.", Toast.LENGTH_LONG).show();

            }
        });

    }
}
