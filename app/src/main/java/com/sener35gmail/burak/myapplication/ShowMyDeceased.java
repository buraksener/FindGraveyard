package com.sener35gmail.burak.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ShowMyDeceased extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    Query query;
    TextView et_deceased;

    ListView mList;

    ArrayList<String> names = new ArrayList<>();

     String uid;
    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_deceased);
        et_deceased=(TextView) findViewById(R.id.et_deceaseds);





        mList=(ListView) findViewById(R.id.ListView);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);

        auth=FirebaseAuth.getInstance();


        reference=FirebaseDatabase.getInstance().getReference("Deceaceds");

        final List<String> keys = new ArrayList<>();

        uid=auth.getCurrentUser().getUid();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayAdapter.clear();
                if(!dataSnapshot.exists())
                {
                    Toast.makeText(getApplicationContext(),"İt seems you haven't added any Deceased!",Toast.LENGTH_LONG).show();
                }

                for(DataSnapshot snapshot: dataSnapshot.getChildren())

                {


                    String Userid=(String) snapshot.child("AuthenticatedUserOfDeceased").getValue();
                    if(uid==Userid) {
                        String deceasedName = (String) snapshot.child("DeceasedName").getValue();
                        String deceasedSurname = (String) snapshot.child("DeceasedSurname").getValue();
                        String deceasdGender = (String) snapshot.child("DeceasedGender").getValue();
                        String deceasedJob = (String) snapshot.child("DeceasedJob").getValue();
                        String deceased_dob = (String) snapshot.child("DateOfBirth").getValue();
                        String deceased_dod = (String) snapshot.child("DateOfDeath").getValue();
                        String bio = (String) snapshot.child("DeceasedBiography").getValue();


                        names.add("Deceased Name:" + deceasedName + "\n" + "\n Deceased Surname:" + deceasedSurname + "\n" + "\nDeceased Gender:" + deceasdGender + "\n" + "\nDeceased Job:" + deceasedJob + "\n" + "\nDate Of Birth:" + deceased_dob + "\n" + "\nDate Of Dead:" + deceased_dod + "\n" + "\nDeceased Biography:" + bio+"\n");
                        keys.add(snapshot.getKey());


                        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                            }
                        });
                    }
                    arrayAdapter.notifyDataSetChanged();


                }







            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });
        arrayAdapter.addAll(names);
        mList.setAdapter(arrayAdapter);


    }
}
