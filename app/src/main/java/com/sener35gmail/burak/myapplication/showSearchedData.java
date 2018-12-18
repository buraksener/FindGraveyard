package com.sener35gmail.burak.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class showSearchedData extends AppCompatActivity {
TextView textView;
TextView textView_Fullname;
TextView textView_Gender;
TextView textView_Job;
TextView textView_dob;
TextView textView_dod;
TextView textView_bio;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    Query query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_searched_data);
        textView=(TextView) findViewById(R.id.textView10);
        textView_Fullname=(TextView) findViewById(R.id.textView8);
        textView_Gender=(TextView) findViewById(R.id.textView9);
        textView_Job=(TextView) findViewById(R.id.textView12);
        textView_dob=(TextView) findViewById(R.id.textView13);
        textView_dod=(TextView) findViewById(R.id.textView14);
        textView_bio=(TextView) findViewById(R.id.textView15);
        final String Found_name=getIntent().getStringExtra("text");
        textView.setText(Found_name);
        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference("Deceaceds");
        final String name=Found_name.toLowerCase();
        final String name2=Found_name.toUpperCase();
        query=databaseReference.orderByChild("DeceasedFullName").equalTo(name);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    String fullName=(String) snapshot.child("DeceasedFullName").getValue();
                    assert fullName != null;
                    if(fullName.toLowerCase().equals(name))
                    {
                        String deceasedGender = (String) snapshot.child("DeceasedGender").getValue();
                        String deceasedJob = (String) snapshot.child("DeceasedJob").getValue();
                        String deceased_dob = (String) snapshot.child("DateOfBirth").getValue();
                        String deceased_dod = (String) snapshot.child("DateOfDeath").getValue();
                        String bio = (String) snapshot.child("DeceasedBiography").getValue();
                        textView_Fullname.setText(fullName);
                        textView_Gender.setText(deceasedGender);
                        textView_dob.setText(deceased_dob);
                        textView_dod.setText(deceased_dod);
                        textView_Job.setText(deceasedJob);
                        textView_bio.setText(bio);

                    }
                    else if(fullName.toUpperCase().equals(name2))
                    {
                        String deceasedGender = (String) snapshot.child("DeceasedGender").getValue();
                        String deceasedJob = (String) snapshot.child("DeceasedJob").getValue();
                        String deceased_dob = (String) snapshot.child("DateOfBirth").getValue();
                        String deceased_dod = (String) snapshot.child("DateOfDeath").getValue();
                        String bio = (String) snapshot.child("DeceasedBiography").getValue();
                        textView_Fullname.setText(fullName);
                        textView_Gender.setText(deceasedGender);
                        textView_dob.setText(deceased_dob);
                        textView_dod.setText(deceased_dod);
                        textView_Job.setText(deceasedJob);
                        textView_bio.setText(bio);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}
