package com.example.thomas.artifact;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

public class AddStudents extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText addStudentName;
    private Button add_students_btn;

    private DatabaseReference mDatabase;
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

    private void writeNewUser(String userId, String name) {
        Student student = new Student(name);
        mDatabase.child("student").child(userId).setValue(student);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_students);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        add_students_btn = findViewById(R.id.add_student_btn);


        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("James Brown");


        
        //add
        add_students_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              // submitPost();
              //  addStudentName = findViewById(R.id.add_student_text_box);

              //  mDatabase.child("artifact-c6ae0").child(String.valueOf(addStudentName));


            }
        });


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
