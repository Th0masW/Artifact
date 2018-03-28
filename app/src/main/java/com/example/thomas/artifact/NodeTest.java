package com.example.thomas.artifact;

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
import com.google.firebase.database.ValueEventListener;
//import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NodeTest extends AppCompatActivity {
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
        mAssignmentDB = FirebaseDatabase.getInstance().getReference().child("Assignment");
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
                loadView();
            }
        });


    }
    public void loadView() {

        Toast.makeText(getApplicationContext(),
                "You selected : " + studentName, Toast.LENGTH_SHORT).show();
        // Populate listView
        tabListView = findViewById(R.id.tabListView);

        final ArrayAdapter<String> assignmentAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_expandable_list_item_1,
                        mAssignments);
        tabListView.setAdapter(assignmentAdapter);
        mAssignmentDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //String test = dataSnapshot.child("Jill Maybe").toString();
                //String name = dataSnapshot.getValue(String.class);
                //String key = dataSnapshot.getKey();
                //Log.v("NodeTest", "Jill Maybe Child: " + test);
                // get nodes
                List<Assignment> assList = new ArrayList<>();
                for (DataSnapshot adSnapshot: dataSnapshot.getChildren()) {
                    /*String t = adSnapshot.toString();
                    String k = adSnapshot.getKey();
                    Log.d("NodeTest", "Assignment Class:"+t);
                    Log.d("NodeTest", "Assignment Key:"+k); */
                    Assignment a = adSnapshot.getValue(Assignment.class);
                    //String testing = adSnapshot.child(k).getKey();
                    //Log.d("NodeTest", "Assignment Key for "+k +" is:"+ testing);
                    Log.d("NodeTest", "Ass name:" + a.getAssignmentName());
                    assList.add(adSnapshot.getValue(Assignment.class));
                    mAssignments.add(a.getAssignmentName());

                }
                Log.d("NodeTest", "# of records of the search is "+ assList.size());
                //Log.d("NodeTest", "# of records of the search is "+ cnt);

                ////////////////
                //mAssignments.add(test);
                assignmentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
