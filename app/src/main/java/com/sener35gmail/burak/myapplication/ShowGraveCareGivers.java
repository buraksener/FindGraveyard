package com.sener35gmail.burak.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowGraveCareGivers extends AppCompatActivity {
    DatabaseReference reference;
    FirebaseAuth auth;

    ListView mList;
    ArrayList<String> names = new ArrayList<>();
    String uid;
    ArrayAdapter<String> arrayAdapter;
    Query query;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_grave_care_givers);
        mList=(ListView) findViewById(R.id.ListViewGraveCareGivers);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
        auth=FirebaseAuth.getInstance();
        uid=auth.getCurrentUser().getUid();
        reference= FirebaseDatabase.getInstance().getReference("Users");
        final List<String> keys = new ArrayList<>();
        query=reference.orderByChild("User type").equalTo("Grave Caregiver");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayAdapter.clear();
                for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                {
                    String Name=(String) childsnapshot.child("Name").getValue();
                    String Surname=(String) childsnapshot.child("Surname").getValue();
                    String phone=(String) childsnapshot.child("phone number").getValue();
                    names.add("Name:"+Name +"\n"+"Surname:"+Surname+"\n"+"Phone number:"+String.valueOf(phone));
                    keys.add(childsnapshot.getKey());
                }

                arrayAdapter.addAll(names);
                arrayAdapter.notifyDataSetChanged();
                mList.setAdapter(arrayAdapter);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
}
