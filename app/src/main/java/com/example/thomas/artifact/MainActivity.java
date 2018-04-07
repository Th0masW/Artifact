package com.example.thomas.artifact;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
// thomas was here
// and again  ....

/**
 * Description of Main Activity
 *
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // change title
        setTitle("Welcome to Artifact!");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser == null) {
            //create login intent
            Intent loginIntent = new Intent(MainActivity.this, Login.class);
            startActivity(loginIntent);
            finish();
        }
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
            Intent go = new Intent(MainActivity.this, CaptureMyEvidence.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.review_evidence) {
            Intent go = new Intent(MainActivity.this, ReviewEvidence.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.add_students) {
            Intent go = new Intent(MainActivity.this, AddStudents.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.edit_student) {
            Intent go = new Intent(MainActivity.this, PickStudent.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.log_out) {
            FirebaseAuth.getInstance().signOut();
            sendToMain();
        }

        return true;
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }


    public void openEvidenceCapture(View view) {
        startActivity(new Intent(MainActivity.this, CaptureMyEvidence.class));
    }

    /**
     * Opens Review Evidence page
     * @param view
     */
    public void openReviewEvidence(View view) {
        startActivity(new Intent(MainActivity.this, ReviewEvidence.class));
    }

    /**
     * Opens Add Student page
     * @param view
     */
    public void openAddStudents(View view) {
        startActivity(new Intent(MainActivity.this, AddStudents.class));
    }

    public void openTest(View view) {
        startActivity(new Intent(MainActivity.this, NodeTest.class));
    }
    private void doSomething() {
        String tag = "Do Something";
        String msg = "Say something";
        Log.e(tag, msg);
        Log.v(tag, "This is a verbose log");
        Log.wtf(tag, "WTF!");
    }

    public void openPickStudents(View view) {
        startActivity(new Intent(MainActivity.this, PickStudent.class));
    }

}
