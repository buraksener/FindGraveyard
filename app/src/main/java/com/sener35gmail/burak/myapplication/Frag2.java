package com.sener35gmail.burak.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sener35gmail.burak.myapplication.Model.Deceased;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 08.08.2018.
 */

public class Frag2 extends Fragment {



    DatabaseReference reference;
   // ListView mList;
  //  ArrayAdapter<String> arrayAdapter;
    ArrayAdapter<String> arrayAdapter_keyList;
   // ArrayList<String> names = new ArrayList<>();
    ArrayList<String>  keyList=new ArrayList<>();
    FirebaseAuth auth;

   private RecyclerView recyclerView;
   private List<Deceased>  deceasedList;
   private Context context;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.frag2_layout,container,false);
        auth= FirebaseAuth.getInstance();
      //  mList=(ListView)v.findViewById(R.id.ListViewVisitors);
        context=getActivity();


        //Recycler view implementation\\


        recyclerView=(RecyclerView) v.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(linearLayoutManager);

        deceasedList=new ArrayList<Deceased>();
        final SimpleRecyclerAdapterForDeceased adapterForDeceased=new SimpleRecyclerAdapterForDeceased(deceasedList,new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d("position", "Tıklanan Pozisyon:" + position);
                Deceased deceased=deceasedList.get(position);

            }
        },context);

        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapterForDeceased);

        recyclerView.setItemAnimator(new DefaultItemAnimator());



      //  arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, names);
        arrayAdapter_keyList = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, keyList);
        reference= FirebaseDatabase.getInstance().getReference("Deceaceds");
        final  String  uid=auth.getCurrentUser().getUid();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

               // arrayAdapter.clear();
                if(!dataSnapshot.exists())
                {
                    Toast.makeText(getActivity(),"Kayıtlı merhum yok !",Toast.LENGTH_LONG).show();
                }

                for(DataSnapshot snapshot: dataSnapshot.getChildren())

                {



                    String Userid=(String) snapshot.child("AuthenticatedUserOfDeceased").getValue();
                    if(uid.equals(Userid)) {


                        String deceasedName = (String) snapshot.child("DeceasedName").getValue();
                        String deceasedSurname = (String) snapshot.child("DeceasedSurname").getValue();
                        String deceasdGender = (String) snapshot.child("DeceasedGender").getValue();
                        String deceasedJob = (String) snapshot.child("DeceasedJob").getValue();
                        String deceased_dob = (String) snapshot.child("DateOfBirth").getValue();
                        String deceased_dod = (String) snapshot.child("DateOfDeath").getValue();
                        String bio = (String) snapshot.child("DeceasedBiography").getValue();
                        String deceased_key=(String) snapshot.child("DeceasedKey").getValue();
                        String deceased_imgUrl=(String) snapshot.child("ImageUrl").getValue();
                        keyList.add(deceased_key);

                        final SimpleRecyclerAdapterForDeceased adapterForDeceased=new SimpleRecyclerAdapterForDeceased(deceasedList,new CustomItemClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {
                                Log.d("position", "Tıklanan Pozisyon:" + position);
                                Deceased deceased=deceasedList.get(position);
                                String deceased_key=keyList.get(position);
                                Intent pass=new Intent(getActivity(),EditDeceasedProfile.class);
                                pass.putExtra("deceased_key",deceased_key);
                                startActivity(pass);


                            }
                        },context);



                        deceasedList.add(new Deceased("ad soyad: "+deceasedName+" ",deceasedSurname,"Doğum tarihi: "+String.valueOf(deceased_dob)+" ","Ölüm Tarihi: "+String.valueOf(deceased_dod),deceased_imgUrl));
                        recyclerView.setHasFixedSize(true);

                        recyclerView.setAdapter(adapterForDeceased);

                        recyclerView.setItemAnimator(new DefaultItemAnimator());




                       /* names.add("Merhumun Adı:\t" + deceasedName + "\n" + "\n Merhumun Soyadı:\t" + deceasedSurname + "\n" + "\nMerhumun Cinsiyeti:\t" + deceasdGender + "\n" + "\nMerhumun Mesleği:\t" + deceasedJob + "\n" + "\nMerhumun doğum tarihi:\t" + deceased_dob + "\n" + "\nMerhumun ölüm tarihi:\t" + deceased_dod + "\n" + "\nMerhumun biyografisi:\t" + bio+"\n"+"\n"+"----------------------------------------------------------------------");
                        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String deceased_key=keyList.get(position);
                                Intent pass=new Intent(getActivity(),EditDeceasedProfile.class);
                                pass.putExtra("deceased_key",deceased_key);
                                startActivity(pass);



                            }
                        });*/
                    }
                   // arrayAdapter.notifyDataSetChanged();


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });

       // arrayAdapter.addAll(names);
       // mList.setAdapter(arrayAdapter);






        return v;
    }

   /* private class MylistAdapter extends ArrayAdapter<String>{





        public MylistAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {

            super(context, resource, objects);

            layout=resource;

        }



        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder mainViewholder=null;

            if(convertView==null)
            {
                LayoutInflater layoutInflater=LayoutInflater.from(getContext());
                convertView=layoutInflater.inflate(layout,parent,false);
                ViewHolder viewHolder=new ViewHolder();
                viewHolder.imageButton=(ImageButton) convertView.findViewById(R.id.changeDeceasedInfo);
                convertView.setTag(viewHolder);

            }
            else
            {
                mainViewholder=(ViewHolder) convertView.getTag();
                return convertView;
            }


            return super.getView(position, convertView, parent);


        }
    }
    public class ViewHolder{
        ImageButton imageButton;



    }*/



}
