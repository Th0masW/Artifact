package com.example.thomas.artifact;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.List;
import java.util.Map;

public class GetPicture extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference studentDB;
    private ArrayList<String> mStudents = new ArrayList<>();
    private ArrayList<StudentEntity> studentArray = new ArrayList<>();
    private ArrayList<Assignment> assignmentArray = new ArrayList<>();
    private DatabaseReference assignmentDB;
    private ArrayList<String> mAssignments = new ArrayList<>();
    private ListView mListView;
    public String studentName;
    public String studentKey;
    public String fileName;
    public String assignmentName;
    public StudentEntity selectedStudent;

    private static final String TAG = "GetPicture";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_picture);

        studentName = null;
        studentKey = null;
        assignmentName = null;
        fileName = null;
        selectedStudent = null;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        studentDB = FirebaseDatabase.getInstance().getReference().child("Student");
        assignmentDB = FirebaseDatabase.getInstance().getReference().child("Assignment");

        // Populate listView
        mListView = findViewById(R.id.myListView);
        // change name
        setTitle("Take a Picture");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, mStudents);
        mListView.setAdapter(arrayAdapter);
        // add to adapter

        studentDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String name = dataSnapshot.getValue(String.class);
                String key = dataSnapshot.getKey();
                mStudents.add(name);
                arrayAdapter.notifyDataSetChanged();
                // add student entity
                StudentEntity student = new StudentEntity(key,name);
                studentArray.add(student);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object item = mListView.getItemAtPosition(position);
                studentName = item.toString();
                // get student key
                StudentEntity myStudent = studentArray.get(position);
                studentKey = myStudent.getKey();


            }
        });


        mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                // On selecting a spinner item
                studentName = adapter.getItemAtPosition(position).toString();

                // Showing selected spinner item
                Toast.makeText(getApplicationContext(),
                        "You selected : " + studentName, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

    }

    /**
     * Opens Snap Picture Student page
     * @param view
     */
    public void openSnapPicture(View view) {
        // pass data
        Intent intent = new Intent(GetPicture.this, SnapPicture.class);
        intent.putExtra("name", studentName);
        intent.putExtra("key", studentKey);
        Log.v("Student name: ", studentName);
        startActivity(intent);
    }


}

