package com.example.thomas.artifact;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReviewEvidence extends AppCompatActivity {
    private DatabaseReference studentDB;
    private DatabaseReference mAssignmentDB;
    private DatabaseReference mDatabase;
    private DatabaseReference nameDB;
    private ArrayList<String> mStudents = new ArrayList<>();
    private ArrayList<String> mAssignments = new ArrayList<>();
    private ArrayList<StudentEntity> studentArray = new ArrayList<>();
    private ListView mListView;
    private ListView tabListView;
    public String studentName;
    public String studentKey;
    private String assignmentName;
    private String fileName;
    public StudentEntity selectedStudent;
    private Spinner mSpinner;
    private ArrayAdapter<String> spinAdapter;
    private List<Assignment> assignmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_evidence);

        studentName = null;
        studentKey = null;
        selectedStudent = null;
        assignmentName = null;
        fileName = null;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        studentDB = FirebaseDatabase.getInstance().getReference().child("Student");
        mAssignmentDB = FirebaseDatabase.getInstance().getReference().child("Assignment");
        nameDB = FirebaseDatabase.getInstance().getReference().child("Name");
        // populate spinner
        mSpinner = findViewById(R.id.ddlStudent);
        mStudents.add(""); // add empty to top
        spinAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mStudents);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(spinAdapter);

        studentDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String name = dataSnapshot.getValue(String.class);
                String key = dataSnapshot.getKey();
                mStudents.add(name);
                spinAdapter.notifyDataSetChanged();
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

        // Spinner item select listener
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            protected Adapter initializedAdapter=null;
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                if(initializedAdapter !=parentView.getAdapter() ) {
                    initializedAdapter = parentView.getAdapter();
                    return;
                }
                String item = parentView.getItemAtPosition(position).toString();
                studentName = item.toString();
                emptyAssignments();
                loadView();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

    }

    public void emptyAssignments() {
        mAssignments.clear();
    }

    public void loadView() {
        // Populate listView
        tabListView = findViewById(R.id.tabListView);

        final ArrayAdapter<String> assignmentAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_expandable_list_item_1,
                        mAssignments);
        tabListView.setAdapter(assignmentAdapter);
        mAssignmentDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get nodes
                for (DataSnapshot adSnapshot: dataSnapshot.getChildren()) {
                    Assignment a = adSnapshot.getValue(Assignment.class);
                    Log.d("NodeTest", "Ass name:" + a.getAssignmentName());
                    // check if it's student's assignment
                    String sName = a.getStudentName();
                    if (sName.equals(studentName)){
                        assignmentList.add(adSnapshot.getValue(Assignment.class));
                        mAssignments.add(a.getAssignmentName());
                    }
                }
                assignmentAdapter.notifyDataSetChanged();
                Integer cnt = mAssignments.size();
                Toast.makeText(ReviewEvidence.this, cnt.toString() + " assignments found for student " + studentName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        tabListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object item = tabListView.getItemAtPosition(position);
                 assignmentName = item.toString();
                 // get file name
                 assignFileName();
            }
        });
    }
    private void assignFileName(){
        // iterate through assignment list
        for(int i = 0; i < assignmentList.size(); i++) {
            Assignment a = assignmentList.get(i);
            String fn = a.getFileName();
            String aN = a.getAssignmentName();
            String sN = a.getStudentName();
            Log.v("ReviewEvidence", "Student:"+ sN + ", file:" + fn);
            if (aN == assignmentName) {
                fileName = fn;
            }
        }
    }
    public void openViewAssignment(View view) {
        // pass data
        Intent intent = new Intent(ReviewEvidence.this, ViewAssignment.class);
        intent.putExtra("name", studentName);
        //intent.putExtra("key", studentKey);
        intent.putExtra("assignment", assignmentName);
        intent.putExtra("file", fileName);
        //Log.v("Student name: ", studentName);
        startActivity(intent);
    }
}
