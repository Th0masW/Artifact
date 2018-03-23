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
    private DatabaseReference messageDB;
    private DatabaseReference nameDB;
    private ArrayList<String> mStudents = new ArrayList<>();
    private ListView mListView;
    public String studentName;

    private static final String TAG = "GetPicture";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_picture);

        studentName = null;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        studentDB = FirebaseDatabase.getInstance().getReference().child("Student");
        messageDB = FirebaseDatabase.getInstance().getReference().child("message");
        nameDB = FirebaseDatabase.getInstance().getReference().child("Name");

        // Populate listView
        mListView = findViewById(R.id.myListView);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, mStudents);
        mListView.setAdapter(arrayAdapter);
        // add to adapter
        nameDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.getValue(String.class);
                mStudents.add(value);
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

                Toast.makeText(GetPicture.this, "Selected :" + " " + studentName, Toast.LENGTH_LONG).show();
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
     * Opens Add Student page
     * @param view
     */
    public void openSnapPicture(View view) {
        startActivity(new Intent(GetPicture.this, SnapPicture.class));
    }


}

