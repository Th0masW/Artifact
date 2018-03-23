package com.example.thomas.artifact;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class SnapPicture extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView myImageView;
    Button save_btn;
    Button cancel_btn;
    Bitmap photo;
    String studentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap_picture);

        Button myBtn = (Button) findViewById(R.id.snapBtn);
        myImageView = (ImageView) findViewById(R.id.myImageView);
        save_btn = findViewById(R.id.saveBtn);
        cancel_btn = findViewById(R.id.cancelBtn);
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

    public void submitImage(View view) {
        savePicture(photo);
    }

    private void savePicture(Bitmap photo) {
        try {
            // Use the compress method on the Bitmap object to write image to
            // the OutputStream
            FileOutputStream fos = openFileOutput("desiredFilename.png", Context.MODE_PRIVATE);

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
