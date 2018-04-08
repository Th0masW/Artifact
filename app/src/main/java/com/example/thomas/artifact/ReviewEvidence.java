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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.List;


public class ReviewEvidence extends AppCompatActivity {

    private static final String TAG = "ReviewEvidence";
    private DatabaseReference studentDB;
    private DatabaseReference mAssignmentDB;
    private ArrayList<String> mStudents = new ArrayList<>();
    private ArrayList<String> mAssignments = new ArrayList<>();
    private ListView tabListView;
    public String studentName;
    public String studentKey;
    private String assignmentName;
    private String fileName;
    private String type;
    public StudentEntity selectedStudent;
    private Spinner mSpinner;
    private Spinner typeSpinner;
    private ArrayAdapter<String> spinAdapter;
    private ArrayAdapter<String> typeAdapter;
    private List<Assignment> assignmentList = new ArrayList<>();
    TextView lblType;
    TextView lblAssignment;
    Spinner ddlType;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Make sure user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //Redirect to login page
        if(currentUser == null) {
            //create login intent
            Intent loginIntent = new Intent(ReviewEvidence.this, Login.class);
            startActivity(loginIntent);
            finish();
        }
        setContentView(R.layout.activity_review_evidence);

        studentName = null;
        studentKey = null;
        selectedStudent = null;
        assignmentName = null;
        fileName = null;
        type = "photo";
        lblType = findViewById(R.id.lblType);
        ddlType = findViewById(R.id.ddlType);
        lblAssignment = findViewById(R.id.lblAssignment);
        //change title
        setTitle("Review Evidence");

        // get database reference
        studentDB = FirebaseDatabase.getInstance().getReference().child("Student");
        mAssignmentDB = FirebaseDatabase.getInstance().getReference().child("Assignment");
        // populate student spinner
        mSpinner = findViewById(R.id.ddlStudent);
        mStudents.add(""); // add empty to top
        spinAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mStudents);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(spinAdapter);
        // populate type spinner
        List<String> types = new ArrayList<>();
        types.add("photo");
        types.add("video");
        typeSpinner = findViewById(R.id.ddlType);
        typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
        typeAdapter.notifyDataSetChanged();

        studentDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String name = dataSnapshot.getValue(String.class);
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

        // type spinner item select listener
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
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
                type = item.toString();
                emptyAssignments();
                loadView();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        // student spinner item select listener
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

            }
        });
    }
    private void adjustVisibility() {
        if (studentName != null) {
            lblAssignment.setVisibility(View.VISIBLE);
            lblType.setVisibility(View.VISIBLE);
            ddlType.setVisibility(View.VISIBLE);
            //tabListView.setVisibility(View.VISIBLE);
        } else {

            lblAssignment.setVisibility(View.INVISIBLE);
            lblType.setVisibility(View.INVISIBLE);
            ddlType.setVisibility(View.INVISIBLE);
            //tabListView.setVisibility(View.INVISIBLE);
        }


    }

    public void emptyAssignments() {
        mAssignments.clear();
    }

    public void loadView() {
        // Populate listView
        tabListView = findViewById(R.id.tabListView);

        final ArrayAdapter<String> assignmentAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_expandable_list_item_1,
                        mAssignments);
        tabListView.setAdapter(assignmentAdapter);
        mAssignmentDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get nodes
                for (DataSnapshot adSnapshot: dataSnapshot.getChildren()) {
                    Assignment a = adSnapshot.getValue(Assignment.class);
                    Log.d("ReviewEvidence", "Ass name:" + a.getAssignmentName());
                    // check if it's student's assignment
                    String sName = a.getStudentName();
                    String t = a.getType();
                    if (t == null) {t = "PHOTO";}
                    t = t.toLowerCase();
                    Log.d("ReviewEvidence", "Ass type:" + t + ", selected type:" + type);
                    if (sName.equals(studentName) && t.equals(type)){
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
        Intent intent;

        if(type.equals("photo")){
            Log.v(TAG, "Sending to photo activity");
            intent = new Intent(ReviewEvidence.this, ViewAssignment.class);
        } else if(type.equals("video")) {
            Log.v(TAG, "Sending to video activity");
            intent = new Intent(ReviewEvidence.this, ReviewVideo.class);
        } else {
            Log.v(TAG, "Sending to video activity");
            intent = new Intent(ReviewEvidence.this, ReviewAudio.class);
        }
        intent.putExtra("name", studentName);
        intent.putExtra("assignment", assignmentName);
        intent.putExtra("file", fileName);
        startActivity(intent);
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
            Intent go = new Intent(ReviewEvidence.this, CaptureMyEvidence.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.review_evidence) {
            Intent go = new Intent(ReviewEvidence.this, ReviewEvidence.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.add_students) {
            Intent go = new Intent(ReviewEvidence.this, AddStudents.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.edit_student) {
            Intent go = new Intent(ReviewEvidence.this, PickStudent.class);
            startActivity(go);
            finish();
        }else if(item.getItemId() == R.id.log_out) {
            FirebaseAuth.getInstance().signOut();
            sendToMain();
        }

        return true;
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(ReviewEvidence.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
    
}
