package com.example.thomas.artifact;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class NodeTest extends AppCompatActivity {
    private DatabaseReference studentDB;
    private DatabaseReference messageDB;
    private DatabaseReference mDatabase;
    private DatabaseReference nameDB;
    private ArrayList<String> mStudents = new ArrayList<>();
    private ArrayList<StudentEntity> studentArray = new ArrayList<>();
    private ListView mListView;
    public String studentName;
    public String studentKey;
    public StudentEntity selectedStudent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_test);

        studentName = null;
        studentKey = null;
        selectedStudent = null;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        studentDB = FirebaseDatabase.getInstance().getReference().child("Student");
        messageDB = FirebaseDatabase.getInstance().getReference().child("message");
        nameDB = FirebaseDatabase.getInstance().getReference().child("Name");

        // Populate listView
        mListView = findViewById(R.id.myListView);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, mStudents);
        mListView.setAdapter(arrayAdapter);

        studentDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String name = dataSnapshot.getValue(String.class);
                String key = dataSnapshot.getKey();
                mStudents.add(name);
                arrayAdapter.notifyDataSetChanged();
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
            }
        });


    }
}
