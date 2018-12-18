package com.sener35gmail.burak.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Admin on 08.08.2018.
 */

public class Frag3 extends Fragment {
    FirebaseDatabase database;
    DatabaseReference reference;
    ListView mList;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> names = new ArrayList<>();
    FirebaseAuth auth;
    String visitor;
    String uid;
    String AuthDeceasedKey;
    String DeceasedKey;
    private Context context;

    Query query;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.frag2_layout,container,false);
        database=FirebaseDatabase.getInstance();
        auth= FirebaseAuth.getInstance();
        mList=(ListView) v.findViewById(R.id.ListViewVisitors);
        arrayAdapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,names);
        reference= database.getReference("Deceaceds");
         uid=auth.getCurrentUser().getUid();
         context=getActivity();



         query=reference.orderByChild("AuthenticatedUserOfDeceased").equalTo(uid);
         query.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {


                 for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                 {
                     AuthDeceasedKey=(String) dataSnapshot1.child("AuthenticatedUserOfDeceased").getValue();
                     DeceasedKey=(String) dataSnapshot1.child("DeceasedKey").getValue();
                 }




                 Toast.makeText(getContext(),AuthDeceasedKey,Toast.LENGTH_LONG).show();
                 Toast.makeText(getContext(),DeceasedKey,Toast.LENGTH_LONG).show();

                 reference.child(DeceasedKey).child("Visitors").addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                         for(DataSnapshot snapshot:dataSnapshot.getChildren())
                         {
                             visitor=(String) snapshot.child("Date").getValue();
                             Toast.makeText(getContext(),visitor,Toast.LENGTH_LONG).show();


                         }

                     }

                     @Override
                     public void onCancelled(DatabaseError databaseError) {

                     }
                 });



             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });






         /* reference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {

                 //Check Data if its exist or not \\
                 if(! dataSnapshot.exists())
                 {
                     Toast.makeText(getActivity(),"Bu merhumun ziyaretçisi yok",Toast.LENGTH_LONG).show();
                 }

                 //Searching algorithm starts here\\
                 for ( DataSnapshot snapshot:dataSnapshot.getChildren())
                 {
                     String Userid=(String) snapshot.child("AuthenticatedUserOfDeceased").getValue();
                     if( uid!= null && uid.equals(Userid))
                     {

                         final String deceasedkey=(String) snapshot.child("DeceasedKey").getValue();

                         if( deceasedkey!=null && deceasedkey.equals("-LRWGWBvXQ2xSr76vefM"))
                         {
                             FirebaseDatabase.getInstance().getReference("Deceaceds").child(deceasedkey).addListenerForSingleValueEvent(new ValueEventListener() {
                             @Override
                             public void onDataChange(DataSnapshot dataSnapshot) {
                                 String fullname=(String) dataSnapshot.child("DeceasedFullName").getValue();
                                 Toast.makeText(getActivity(),"Ziyaret edilen merhum -->"+fullname,Toast.LENGTH_LONG).show();
                             }

                             @Override
                             public void onCancelled(DatabaseError databaseError) {

                             }
                         });
                             FirebaseDatabase.getInstance().getReference("Visitors").child(uid).child(deceasedkey).addValueEventListener(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(DataSnapshot dataSnapshot) {
                                     for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                                     {
                                         visitor=(String) dataSnapshot1.child("Date").getValue();
                                         Toast.makeText(context,visitor,Toast.LENGTH_LONG).show();

                                     }

                                 }

                                 @Override
                                 public void onCancelled(DatabaseError databaseError) {

                                 }
                             });


                         }


                     }

                 }
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });*/

      /*  reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                arrayAdapter.clear();
                if(!dataSnapshot.exists())
                {
                    Toast.makeText(getActivity(),"Ziyaretçi yok",Toast.LENGTH_LONG).show();
                }


                //searching algorithm stars here\\
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    String Userid=(String) snapshot.child("AuthenticatedUserOfDeceased").getValue();


                    if(uid.equals(Userid))
                    {
                        Toast.makeText(getActivity(),Userid,Toast.LENGTH_LONG).show();

                      reference.child("Visitors").addListenerForSingleValueEvent(new ValueEventListener() {
                          @Override
                          public void onDataChange(DataSnapshot dataSnapshot) {
                              for(DataSnapshot snapshot1:dataSnapshot.getChildren())
                              {
                                  visitor=(String) snapshot1.child("Date").getValue();
                                  Toast.makeText(getActivity(),visitor,Toast.LENGTH_LONG).show();
                              }

                          }

                          @Override
                          public void onCancelled(DatabaseError databaseError) {

                          }
                      });

                        String deceasedName = (String) snapshot.child("DeceasedName").getValue();
                        String deceasedSurname = (String) snapshot.child("DeceasedSurname").getValue();

                       // names.add(deceasedName+" "+deceasedSurname+"\t"+" "+"ziyaretçileri:"+"\t"+visitor+"\n");

                    }

                    arrayAdapter.notifyDataSetChanged();



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


       reference.child("Visitors").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    visitor=(String) dataSnapshot1.child("Date").getValue();
                    Toast.makeText(getActivity(),visitor,Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        names.add("deneme");*/






        arrayAdapter.addAll(names);
     // mList.setAdapter(arrayAdapter);

        return v;
    }
}
