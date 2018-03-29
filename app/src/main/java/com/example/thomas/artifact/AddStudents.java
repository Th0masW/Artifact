package com.example.thomas.artifact;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

public class AddStudents extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText addStudentName;
    private EditText studentGrade;
    private Button add_students_btn;
    private DatabaseReference mDatabase;
    private DatabaseReference studentDB;
    private DatabaseReference nameDB;
    private Boolean allSaved;

// ...
    //mDatabase = FirebaseDatabase.getInstance().getReference();

    @IgnoreExtraProperties
    public class Student {
        public String studentName;

        public Student() {
            //default constructor
        }

        public Student(String studentName) {
            this.studentName = studentName;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_students);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        add_students_btn = findViewById(R.id.add_student_btn);
        addStudentName = findViewById(R.id.add_student_text_box);
        studentGrade = findViewById(R.id.grade_text_box);
        studentDB = FirebaseDatabase.getInstance().getReference().child("Student");
        nameDB = FirebaseDatabase.getInstance().getReference().child("Name");
        allSaved = null;
    }
    public void addStudent(View view) {
        String name = addStudentName.getText().toString();
        String grade = studentGrade.getText().toString();

        Log.v("AddStudents", "Student name: " + name);
        Log.v("AddStudents", "Student grade: " + grade);

        HashMap<String, String> datamap = new HashMap<String,String>();
        datamap.put("Name", name);
        datamap.put("Grade", grade);
        // add to "student" node
        /*studentDB.push().setValue(datamap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //Toast.makeText(AddStudents.this,"Stored...", Toast.LENGTH_LONG).show();
                } else {
                    allSaved = false;
                    //Toast.makeText(AddStudents.this,"Error...", Toast.LENGTH_LONG).show();
                }
            }
        });*/
        studentDB.push().setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //Toast.makeText(AddStudents.this,"Stored...", Toast.LENGTH_LONG).show();
                } else {
                    allSaved = false;
                    //Toast.makeText(AddStudents.this,"Error...", Toast.LENGTH_LONG).show();
                }
            }
        });
        // add name to Name in DB
        //nameDB.push().setValue(name);
        nameDB.push().setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //Toast.makeText(AddStudents.this,"Stored...", Toast.LENGTH_LONG).show();
                } else {
                    allSaved = false;
                }
            }
        });
        //add name as a node to use for assignments
        mDatabase.push().setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                } else {
                    allSaved = false;
                }
            }
        });
        // communicate if saved
        if (allSaved== null) {
            Toast.makeText(AddStudents.this,"Student has been added", Toast.LENGTH_LONG).show();
            sendToMain();
        } else {
            Toast.makeText(AddStudents.this,"Error: student could not be added. " +
                    "Make sure to use a unique name.", Toast.LENGTH_LONG).show();
        }

    }

    private void writeNewUser(String userId, String name) {
        Student student = new Student(name);
        mDatabase.child("student").child(userId).setValue(student);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        //Check if user is logged in, if so, send to main
       // if(currentUser != null) {
      //      sendToMain();
      //  }
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(AddStudents.this, MainActivity.class);
        finish();
    }



}
