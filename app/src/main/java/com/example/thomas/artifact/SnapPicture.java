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
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Random;
import android.view.Menu;
import android.view.MenuItem;

public class SnapPicture extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView myImageView;
    Button save_btn;
    Button cancel_btn;
    Bitmap photo;
    String studentName;
    String studentKey;
    String fileLocation;
    EditText assignmentName;
    TextView lblAssign;
    private DatabaseReference assignmentDB;
    private DatabaseReference studentNode;
    private StorageReference mStorageRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap_picture);

        Button myBtn = (Button) findViewById(R.id.snapBtn);
        myImageView = (ImageView) findViewById(R.id.myImageView);
        save_btn = findViewById(R.id.saveBtn);
        cancel_btn = findViewById(R.id.cancelBtn);
        assignmentName = findViewById(R.id.assignName);
        studentNode = null;
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
            Log.v("SnapPicture", "Name:" + studentName + ", key:" + studentKey);
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
        // open camera
        launchCamera();
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

    private void launchCamera() {
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
            assignmentVisibility(true);
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
        shortName.replaceAll(" ", "_");
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
        shortName = shortName.replaceAll(" ", "_");
        Log.v("RandomName","Short name:"+ shortName);
        Integer num = getRandomNumberInRange(1000,9999);
        fileName = shortName + num.toString() + ".png";
        Log.v("RandomFIleName", fileName);
        return fileName;
    }

    private void uploadToStorage() {
        String storagePath = storageRef.getPath();
        Log.d("SnapPicture", "Storage path:"+storagePath);
        StorageReference imagesRef = storageRef.child("images/" + fileLocation);
        myImageView.setDrawingCacheEnabled(true);
        myImageView.buildDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SnapPicture.this,"Error! Could not upload picture.", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SnapPicture.this,"Success! Picture has been uploaded.", Toast.LENGTH_LONG).show();
            }
        });

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

    public void submitImage(View view) {
        String assName = assignmentName.getText().toString();
        Log.v("SUBMIT IMAGE", "Assignment name:\"" + assName + "\"");
        // create file name
        fileLocation = randomFileName(studentName);

        //make sure name field is not blank
        if(TextUtils.isEmpty(assName)) {
            assignmentName.setError("The name field must have a value.");
        }

        else {
        // upload to student node
        uploadToNode3();
        //upload picture to db
        uploadToStorage();
        // disable assignment controls
        assignmentVisibility(false);
        buttonVisibility(false); }
    }

    private void uploadToNode3() {
        String assName = assignmentName.getText().toString();
        // check for name
        if (!assName.isEmpty()) {
            // upload
            Assignment newAssign = new Pictures(studentName, assName, photo);
            newAssign.setFileName(fileLocation);
            // save to firebase
            HashMap<String, Assignment> datamap = new HashMap<>();
            datamap.put("Assignment",newAssign);
            assignmentDB.push().setValue(newAssign).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SnapPicture.this,"Uploading Assignment...", Toast.LENGTH_LONG).show();
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

       private void resetAssignment() {
        assignmentName.setText("");
        myImageView.setImageBitmap(null);
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
        startActivity(new Intent(SnapPicture.this, GetPicture.class));
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
            Intent go = new Intent(SnapPicture.this, CaptureMyEvidence.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.review_evidence) {
            Intent go = new Intent(SnapPicture.this, ReviewEvidence.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.add_students) {
            Intent go = new Intent(SnapPicture.this, AddStudents.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.edit_student) {
            Intent go = new Intent(SnapPicture.this, PickStudent.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.log_out) {
            FirebaseAuth.getInstance().signOut();
            sendToMain();
        }

        return true;
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(SnapPicture.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
