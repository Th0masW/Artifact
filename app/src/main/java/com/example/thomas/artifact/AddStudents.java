package com.example.thomas.artifact;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
    private DatabaseReference studentDB;
    private Boolean allSaved;

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
        addStudentName = findViewById(R.id.add_student_text_box);
        studentGrade = findViewById(R.id.grade_text_box);
        studentDB = FirebaseDatabase.getInstance().getReference().child("Student");
        allSaved = null;
        setTitle("Add Student");
    }
    public void addStudent(View view) {
        String name = addStudentName.getText().toString();
        String grade = studentGrade.getText().toString();

        Log.e("AddStudents", "Student name: " + name);
        Log.e("AddStudents", "Student grade: " + grade);

        HashMap<String, String> datamap = new HashMap<String,String>();
        datamap.put("Name", name);
        datamap.put("Grade", grade);

        if(TextUtils.isEmpty(name)) {addStudentName.setError("This can not be blank");}
        else
        {
            // add to "student" node
            insertIntoDatabase(name);

            // communicate if saved
            if (allSaved== null) {
                Toast.makeText(AddStudents.this,name + " has been added", Toast.LENGTH_LONG).show();
                } else {
                Toast.makeText(AddStudents.this,"Error: student could not be added. " +
                        "Make sure to use a unique name.", Toast.LENGTH_LONG).show();
            }

           //finished, return to main page
            sendToMain();
        }

    }

    private void insertIntoDatabase(String name) {

        studentDB.push().setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                } else {
                    allSaved = false;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(AddStudents.this, MainActivity.class);
        finish();
    }


}