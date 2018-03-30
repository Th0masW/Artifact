package com.example.thomas.artifact;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Intent intent = getIntent();
        if (null != intent) {
            studentName = intent.getStringExtra("name");
            studentKey = intent.getStringExtra("key");
            // change title to name
            setTitle("Student: " + studentKey);
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

         //studentDB.setValue(name);
         studentDB.child(studentKey).setValue(name);
        

        //HashMap<String, String> datamap = new HashMap<String,String>();
        //  datamap.put(studentKey, name);

       /* studentDB.push().setValue(datamap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EditStudentName.this,"Stored...", Toast.LENGTH_LONG).show();
                    sendToMain();
                } else {
                    Toast.makeText(EditStudentName.this,"Error...", Toast.LENGTH_LONG).show();
                }
            }
        }); */
        // add name to Name in DB
        //nameDB.push().setValue(name);
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(EditStudentName.this, MainActivity.class);
        finish();
    }

}
