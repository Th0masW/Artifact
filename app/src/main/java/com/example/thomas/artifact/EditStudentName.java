package com.example.thomas.artifact;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.view.Menu;
import android.view.MenuItem;

public class EditStudentName extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText editStudentName;
    private Button edit_students_btn;
    private DatabaseReference mDatabase;
    private DatabaseReference studentDB;
    private DatabaseReference keyDB;

    private String studentName;
    private String studentKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student_name);

        //Make sure user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //Redirect to login page
        if(currentUser == null) {
            //create login intent
            Intent loginIntent = new Intent(EditStudentName.this, Login.class);
            startActivity(loginIntent);
            finish();
        }

        Intent intent = getIntent();
        if (null != intent) {
            studentName = intent.getStringExtra("name");
            studentKey = intent.getStringExtra("key");
            // change title to name
            setTitle("Student: " + studentName);
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        edit_students_btn = findViewById(R.id.edited_name_btn);
        editStudentName = findViewById(R.id.edit_student_name_text_box);
        studentDB = FirebaseDatabase.getInstance().getReference().child("Student");
        keyDB = FirebaseDatabase.getInstance().getReference().child("studentKey");

    }

    public void editStudentName(View view) {
        String name = editStudentName.getText().toString();
        Log.v("AddStudents", "Student name: " + name);

         studentDB.child(studentKey).setValue(name);

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
            Intent go = new Intent(EditStudentName.this, CaptureMyEvidence.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.review_evidence) {
            Intent go = new Intent(EditStudentName.this, ReviewEvidence.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.add_students) {
            Intent go = new Intent(EditStudentName.this, EditStudentName.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.edit_student) {
            Intent go = new Intent(EditStudentName.this, PickStudent.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.log_out) {
            FirebaseAuth.getInstance().signOut();
            sendToMain();
        }

        return true;
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(EditStudentName.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
    

}
