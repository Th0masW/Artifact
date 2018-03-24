package com.example.thomas.artifact;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Random;

public class SnapPicture extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView myImageView;
    Button save_btn;
    Button cancel_btn;
    Bitmap photo;
    String studentName;
    String fileLocation;
    EditText assignmentName;
    private DatabaseReference assignmentDB;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap_picture);

        Button myBtn = (Button) findViewById(R.id.snapBtn);
        myImageView = (ImageView) findViewById(R.id.myImageView);
        save_btn = findViewById(R.id.saveBtn);
        cancel_btn = findViewById(R.id.cancelBtn);
        assignmentName = findViewById(R.id.assignName);
        assignmentDB = FirebaseDatabase.getInstance().getReference().child("Assignment");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        // check for name
        //name = findViewById(R.id)
        Intent intent = getIntent();
        if (null != intent) {
            studentName = intent.getStringExtra("name");
            // change title to name
            setTitle("Student: " + studentName);
        }

        // check for camera
        if(!hasCamera()) {
            myBtn.setEnabled(false);
        }
        // make save and cancel invisible
        buttonVisibility(false);
        //save_btn.setVisibility(View.INVISIBLE);
        //cancel_btn.setVisibility(View.INVISIBLE);
    }

    // check for camera
    private boolean hasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
    // launch camera app
    public void launchCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }
    // return the image taken
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // get photo
            Bundle extras = data.getExtras();
            photo = (Bitmap) extras.get("data");
            myImageView.setImageBitmap(photo);
            // adjust button visibility
            buttonVisibility(true);

            // save picture
            //savePicture(photo);
        }
    }
    private void uploadToDB() {
        String assName = assignmentName.getText().toString();
        // check for name
        if (!assName.isEmpty()) {
            // upload
            Assignment newAssign = new Pictures(studentName, assName, photo);
            String testMsg = "Student:" + newAssign.getStudentName() + ", Assignment:" +
                    newAssign.getAssignmentName() + ", Date:" + newAssign.getDate();
            newAssign.setFileName(fileLocation);
            // save to firebase
            HashMap<String, Assignment> datamap = new HashMap<String,Assignment>();
            datamap.put("ASSIGNMENT", newAssign);
            assignmentDB.push().setValue(datamap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SnapPicture.this,"Assignment Saved", Toast.LENGTH_LONG).show();
                        // reset image and assignment name
                        resetAssignment();
                    } else {
                        Toast.makeText(SnapPicture.this,"Error...Assignment Not Saved", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Missing: Assignment Name", Toast.LENGTH_SHORT).show();
        }
    }
    private String shortenName(String name) {
        String shortName = name;
        shortName.trim();
        shortName.replaceAll(" ", "");
        return shortName;
    }

    private static int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private String randomFileName(String name) {
        String shortName = name;
        String fileName = null;
        shortName.trim();
        shortName.replaceAll(" ", "");
        Integer num = getRandomNumberInRange(1000,9999);
        fileName = shortName + num.toString();
        Log.v("RandomFIleName", fileName);
        return fileName;
    }

    private void uploadToStorage() {
        //String shortName = shortenName(studentName);
        String fileName = randomFileName(studentName);
        //Uri myFile = photo.;
        Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));

        StorageReference riversRef = mStorageRef.child("images/" + fileName);

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(SnapPicture.this,"Photo stored", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Toast.makeText(SnapPicture.this,"Photo could not be stored", Toast.LENGTH_LONG).show();
                    }
                });
    }
    public void submitImage(View view) {
        String assName = assignmentName.getText().toString();
        Log.v("SUBMIT IMAGE", "Assignment name:\"" + assName + "\"");
        // create file name
        fileLocation = randomFileName(studentName);
        // upload info to db
        uploadToDB();

        // upload picture to db - not working
        //uploadToStorage();

        // save locally
        savePicture(photo, fileLocation);


    }
    private void resetAssignment() {
        assignmentName.setText("");
        myImageView.setImageBitmap(null);
    }
    private void savePicture(Bitmap photo, String fileName) {
        try {
            // Use the compress method on the Bitmap object to write image to
            // the OutputStream
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);

            // Writing the bitmap to the output stream
            photo.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Log.v("savePicture", "Image saved");

            //return true;
        } catch (Exception e) {
            Log.e("savePicture()", e.getMessage());
            //return false;
        }
    }

    public void loadPicture(View view) {
        String filename = "desiredFilename.png";
        Bitmap photo = null;
        try {
            File filePath = getFileStreamPath(filename);
            FileInputStream fi = new FileInputStream(filePath);
            photo = BitmapFactory.decodeStream(fi);
            myImageView.setImageBitmap(photo);
        } catch (Exception ex) {
            Log.e("loadPicture", ex.getMessage());
        }
    }

    private void buttonVisibility(boolean x) {
        if (x == true) {
            // make save and cancel invisible
            save_btn.setVisibility(View.VISIBLE);
            cancel_btn.setVisibility(View.VISIBLE);
        } else {
            // make save and cancel invisible
            save_btn.setVisibility(View.INVISIBLE);
            cancel_btn.setVisibility(View.INVISIBLE);
        }
    }

    public void cancelBtn(View view) {
        // open activity
        startActivity(new Intent(SnapPicture.this, GetPicture.class));
    }
}
