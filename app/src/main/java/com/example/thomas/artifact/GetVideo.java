package com.example.thomas.artifact;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class GetVideo extends AppCompatActivity {
    private DatabaseReference studentDB;
    private ArrayList<String> mStudents = new ArrayList<>();
    private ArrayList<StudentEntity> studentArray = new ArrayList<>();
    private ListView mListView;
    public String studentName;
    public String studentKey;
    public String fileName;
    public String assignmentName;
    public StudentEntity selectedStudent;
    private static final String TAG = "GetVideo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_video);

        studentName = null;
        studentKey = null;
        assignmentName = null;
        fileName = null;
        selectedStudent = null;
        studentDB = FirebaseDatabase.getInstance().getReference().child("Student");
        // change title
        setTitle("Record Video");
        // Populate listView
        mListView = findViewById(R.id.myListView);

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
    public void openRecordVideo(View view) {
        // pass data
        if (studentName != null) {
            Intent intent = new Intent(GetVideo.this, RecordVideo.class);
            intent.putExtra("name", studentName);
            intent.putExtra("key", studentKey);
            startActivity(intent);
        }
    }


}
