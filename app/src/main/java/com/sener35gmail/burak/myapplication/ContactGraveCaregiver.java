package com.sener35gmail.burak.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.R.layout.simple_spinner_dropdown_item;

public class ContactGraveCaregiver extends AppCompatActivity {
    ImageButton btn_back;
    DatabaseReference reference;
    FirebaseAuth auth;

   // ListView mList;
  // ArrayList<String> names = new ArrayList<>();
    String uid;
  // ArrayAdapter<String> arrayAdapter;
    Query query;
    ProgressBar progressBar;
    //--recyclerview--\\

    private RecyclerView recycler_view;
    private List<GraveCareGivers> graveCareGiversList;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_grave_caregiver);
        Spinner mySpinner=(Spinner) findViewById(R.id.spinnerTown);
        final Spinner mySpinner2=(Spinner) findViewById(R.id.spinnerCemetery);
        ArrayAdapter<String>Myadapter=new ArrayAdapter<String>(ContactGraveCaregiver.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Cities));
        Myadapter.setDropDownViewResource(simple_spinner_dropdown_item);
        mySpinner.setAdapter(Myadapter);
        btn_back=(ImageButton) findViewById(R.id.Back);
        progressBar = (ProgressBar) findViewById(R.id.progress_search);
        Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);
        //mList=(ListView) findViewById(R.id.ListViewGraveCareGivers);

       // arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
        auth=FirebaseAuth.getInstance();
        uid=auth.getCurrentUser().getUid();
        reference= FirebaseDatabase.getInstance().getReference("Users");


        //recycler view implementations--\\
        recycler_view = (RecyclerView)findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recycler_view.setLayoutManager(layoutManager);

        graveCareGiversList = new ArrayList<GraveCareGivers>();
       // graveCareGiversList.add(new GraveCareGivers("burak ","Şener","gsm: 5549094353"));
       // graveCareGiversList.add(new GraveCareGivers("mehmet ","Şener","gsm:5453012474"));

        final SimpleRecyclerAdapter adapter_items = new SimpleRecyclerAdapter(graveCareGiversList, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d("position", "Tıklanan Pozisyon:" + position);
                GraveCareGivers graveCareGivers = graveCareGiversList.get(position);
              //  Toast.makeText(getApplicationContext(),"pozisyon:"+" "+position+" "+"Ad:"+graveCareGivers.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        recycler_view.setHasFixedSize(true);

        recycler_view.setAdapter(adapter_items);

        recycler_view.setItemAnimator(new DefaultItemAnimator());

        //Recyclerview implementation finished in here\\






        final List<String> keys = new ArrayList<>();
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    ArrayAdapter<String>MyEmptyAdapter=new ArrayAdapter<String>(ContactGraveCaregiver.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Empty));
                    MyEmptyAdapter.setDropDownViewResource(simple_spinner_dropdown_item);
                    mySpinner2.setAdapter(MyEmptyAdapter);
                    //btn_ok.setEnabled(false);
                }
                else if(position==1)
                {
                    ArrayAdapter<String>Myadapter3=new ArrayAdapter<String>(ContactGraveCaregiver.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Adana_Cemeteries));
                    mySpinner2.setAdapter(Myadapter3);

                    mySpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if(position==0)
                            {

                            }
                            else if(position==1)
                            {
                                progressBar.setVisibility(View.VISIBLE);
                                query=reference.orderByChild("Cemetery").equalTo("Akkapı Mezarlığı");
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                      // arrayAdapter.clear();
                                      //  recycler_view.setAdapter(null);
                                        graveCareGiversList.clear();


                                        for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                        {
                                            String Name=(String) childsnapshot.child("Name").getValue();
                                            String Surname=(String) childsnapshot.child("Surname").getValue();
                                            String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                            String city=(String) childsnapshot.child("City").getValue();
                                            String cemetery=(String) childsnapshot.child("Cemetery").getValue();


                                            //names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                            graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                            recycler_view.setHasFixedSize(true);

                                            recycler_view.setAdapter(adapter_items);

                                            recycler_view.setItemAnimator(new DefaultItemAnimator());
                                          //  keys.add(childsnapshot.getKey());
                                        }

                                       // arrayAdapter.notifyDataSetChanged();
                                        adapter_items.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                               // arrayAdapter.addAll(names);
                              // mList.setAdapter(arrayAdapter);

                            }
                            else if(position==2)
                            {
                                progressBar.setVisibility(View.VISIBLE);
                                query=reference.orderByChild("Cemetery").equalTo("Asri Mezarlığı");
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                      // arrayAdapter.clear();
                                        graveCareGiversList.clear();
                                        for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                        {
                                            String Name=(String) childsnapshot.child("Name").getValue();
                                            String Surname=(String) childsnapshot.child("Surname").getValue();
                                            String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                            String city=(String) childsnapshot.child("City").getValue();
                                            String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                          //  names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                          //  keys.add(childsnapshot.getKey());

                                            graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                            recycler_view.setHasFixedSize(true);

                                            recycler_view.setAdapter(adapter_items);

                                            recycler_view.setItemAnimator(new DefaultItemAnimator());
                                        }

                                        //arrayAdapter.notifyDataSetChanged();
                                        adapter_items.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                              // arrayAdapter.addAll(names);
                              //  mList.setAdapter(arrayAdapter);




                            }
                            else if(position==3)
                            {
                                progressBar.setVisibility(View.VISIBLE);
                                query=reference.orderByChild("Cemetery").equalTo("Kabasakal Mezarlığı");
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                      // arrayAdapter.clear();
                                        graveCareGiversList.clear();
                                        for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                        {
                                            String Name=(String) childsnapshot.child("Name").getValue();
                                            String Surname=(String) childsnapshot.child("Surname").getValue();
                                            String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                            String city=(String) childsnapshot.child("City").getValue();
                                            String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                           // names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                           // keys.add(childsnapshot.getKey());
                                            graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                            recycler_view.setHasFixedSize(true);

                                            recycler_view.setAdapter(adapter_items);

                                            recycler_view.setItemAnimator(new DefaultItemAnimator());
                                        }

                                        //arrayAdapter.notifyDataSetChanged();
                                        adapter_items.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);

                                    }

                                    @Override
                                   public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                               // arrayAdapter.addAll(names);
                           // mList.setAdapter(arrayAdapter);

                            }
                            else if(position==4)
                            {
                                progressBar.setVisibility(View.VISIBLE);
                                query=reference.orderByChild("Cemetery").equalTo("Küçükoba Mezarlığı");
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                     //  arrayAdapter.clear();
                                        graveCareGiversList.clear();
                                        for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                        {
                                            String Name=(String) childsnapshot.child("Name").getValue();
                                            String Surname=(String) childsnapshot.child("Surname").getValue();
                                            String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                            String city=(String) childsnapshot.child("City").getValue();
                                            String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                          // names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                           // keys.add(childsnapshot.getKey());
                                            graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                            recycler_view.setHasFixedSize(true);

                                            recycler_view.setAdapter(adapter_items);

                                            recycler_view.setItemAnimator(new DefaultItemAnimator());
                                        }

                                       // arrayAdapter.notifyDataSetChanged();
                                        adapter_items.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                               //arrayAdapter.addAll(names);
                              //  mList.setAdapter(arrayAdapter);

                            }
                            else if(position==5)
                            {
                                progressBar.setVisibility(View.VISIBLE);
                                query=reference.orderByChild("Cemetery").equalTo("Buruk Mezarlığı");
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                      // arrayAdapter.clear();
                                        graveCareGiversList.clear();
                                        for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                        {
                                            String Name=(String) childsnapshot.child("Name").getValue();
                                            String Surname=(String) childsnapshot.child("Surname").getValue();
                                            String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                            String city=(String) childsnapshot.child("City").getValue();
                                            String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                           // names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                           // keys.add(childsnapshot.getKey());
                                            graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                            recycler_view.setHasFixedSize(true);

                                            recycler_view.setAdapter(adapter_items);

                                            recycler_view.setItemAnimator(new DefaultItemAnimator());
                                        }

                                       //arrayAdapter.notifyDataSetChanged();
                                        adapter_items.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                               // arrayAdapter.addAll(names);
                              // mList.setAdapter(arrayAdapter);



                            }
                            else if(position==6)
                            {
                                progressBar.setVisibility(View.VISIBLE);
                                query=reference.orderByChild("Cemetery").equalTo("Adana Emniyet Müdürlüğü Polis Şehitliği");
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                       // arrayAdapter.clear();
                                        graveCareGiversList.clear();
                                        for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                        {
                                            String Name=(String) childsnapshot.child("Name").getValue();
                                            String Surname=(String) childsnapshot.child("Surname").getValue();
                                            String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                            String city=(String) childsnapshot.child("City").getValue();
                                            String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                           // names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                           // keys.add(childsnapshot.getKey());
                                            graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                            recycler_view.setHasFixedSize(true);

                                            recycler_view.setAdapter(adapter_items);

                                            recycler_view.setItemAnimator(new DefaultItemAnimator());
                                        }

                                      // arrayAdapter.notifyDataSetChanged();
                                        adapter_items.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                              // arrayAdapter.addAll(names);
                              // mList.setAdapter(arrayAdapter);



                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
                else if(position==2)
                {
                    ArrayAdapter<String>Myadapter2=new ArrayAdapter<String>(ContactGraveCaregiver.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Adıyaman_Cemeteries));
                    Myadapter2.setDropDownViewResource(simple_spinner_dropdown_item);
                    mySpinner2.setAdapter(Myadapter2);
                    mySpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if(position==0)
                            {

                            }
                            else if(position==1)
                            {
                                progressBar.setVisibility(View.VISIBLE);
                                query=reference.orderByChild("Cemetery").equalTo("Adıyaman Belediyesi Mezarlığı");
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                      //  arrayAdapter.clear();
                                        graveCareGiversList.clear();
                                        for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                        {
                                            String Name=(String) childsnapshot.child("Name").getValue();
                                            String Surname=(String) childsnapshot.child("Surname").getValue();
                                            String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                            String city=(String) childsnapshot.child("City").getValue();
                                            String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                           // names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                          //  keys.add(childsnapshot.getKey());
                                            graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                            recycler_view.setHasFixedSize(true);

                                            recycler_view.setAdapter(adapter_items);

                                            recycler_view.setItemAnimator(new DefaultItemAnimator());
                                        }

                                       // arrayAdapter.notifyDataSetChanged();
                                        adapter_items.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                              // arrayAdapter.addAll(names);
                              // mList.setAdapter(arrayAdapter);


                            }
                            else if(position==2)
                            {
                                progressBar.setVisibility(View.VISIBLE);
                                query=reference.orderByChild("Cemetery").equalTo("Aşağı Karapınar Mezarlığı");
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                      //  arrayAdapter.clear();
                                        graveCareGiversList.clear();
                                        for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                        {
                                            String Name=(String) childsnapshot.child("Name").getValue();
                                            String Surname=(String) childsnapshot.child("Surname").getValue();
                                            String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                            String city=(String) childsnapshot.child("City").getValue();
                                            String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                           // names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                           // keys.add(childsnapshot.getKey());
                                            graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                            recycler_view.setHasFixedSize(true);

                                            recycler_view.setAdapter(adapter_items);

                                            recycler_view.setItemAnimator(new DefaultItemAnimator());
                                        }

                                      // arrayAdapter.notifyDataSetChanged();
                                        adapter_items.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                               //arrayAdapter.addAll(names);
                              // mList.setAdapter(arrayAdapter);



                            }
                            else if(position==3)
                            {
                                progressBar.setVisibility(View.VISIBLE);
                                query=reference.orderByChild("Cemetery").equalTo("Yenı Beledıye Mezarlığı");
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                      // arrayAdapter.clear();
                                        graveCareGiversList.clear();
                                        for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                        {
                                            String Name=(String) childsnapshot.child("Name").getValue();
                                            String Surname=(String) childsnapshot.child("Surname").getValue();
                                            String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                            String city=(String) childsnapshot.child("City").getValue();
                                            String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                           // names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                            //keys.add(childsnapshot.getKey());
                                            graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                            recycler_view.setHasFixedSize(true);

                                            recycler_view.setAdapter(adapter_items);

                                            recycler_view.setItemAnimator(new DefaultItemAnimator());
                                        }

                                        //arrayAdapter.notifyDataSetChanged();
                                        adapter_items.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                               // arrayAdapter.addAll(names);
                              // mList.setAdapter(arrayAdapter);



                            }
                            else if(position==4)
                            {
                                progressBar.setVisibility(View.VISIBLE);

                                query=reference.orderByChild("Cemetery").equalTo("Aşağı Karapınar Mezarlığı");
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                      //  arrayAdapter.clear();
                                        graveCareGiversList.clear();
                                        for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                        {
                                            String Name=(String) childsnapshot.child("Name").getValue();
                                            String Surname=(String) childsnapshot.child("Surname").getValue();
                                            String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                            String city=(String) childsnapshot.child("City").getValue();
                                            String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                          //  names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                           // keys.add(childsnapshot.getKey());
                                            graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                            recycler_view.setHasFixedSize(true);

                                            recycler_view.setAdapter(adapter_items);

                                            recycler_view.setItemAnimator(new DefaultItemAnimator());
                                        }

                                       // arrayAdapter.notifyDataSetChanged();
                                        adapter_items.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                               //arrayAdapter.addAll(names);
                               // mList.setAdapter(arrayAdapter);


                            }
                            else if(position==5)
                            {
                                progressBar.setVisibility(View.VISIBLE);
                                query=reference.orderByChild("Cemetery").equalTo("Süryani Kadim Mezarlığı");
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                       // arrayAdapter.clear();
                                        graveCareGiversList.clear();
                                        for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                        {
                                            String Name=(String) childsnapshot.child("Name").getValue();
                                            String Surname=(String) childsnapshot.child("Surname").getValue();
                                            String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                            String city=(String) childsnapshot.child("City").getValue();
                                            String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                          // names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                           // keys.add(childsnapshot.getKey());
                                            graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                            recycler_view.setHasFixedSize(true);

                                            recycler_view.setAdapter(adapter_items);

                                            recycler_view.setItemAnimator(new DefaultItemAnimator());
                                        }

                                      // arrayAdapter.notifyDataSetChanged();
                                        adapter_items.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                              // arrayAdapter.addAll(names);
                              // mList.setAdapter(arrayAdapter);


                            }
                            else if(position==6)
                            {
                                progressBar.setVisibility(View.VISIBLE);
                                query=reference.orderByChild("Cemetery").equalTo("Karapınar Mezarlığı");
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                       // arrayAdapter.clear();
                                        graveCareGiversList.clear();
                                        for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                        {
                                            String Name=(String) childsnapshot.child("Name").getValue();
                                            String Surname=(String) childsnapshot.child("Surname").getValue();
                                            String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                            String city=(String) childsnapshot.child("City").getValue();
                                            String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                           //names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                           // keys.add(childsnapshot.getKey());
                                            graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                            recycler_view.setHasFixedSize(true);

                                            recycler_view.setAdapter(adapter_items);

                                            recycler_view.setItemAnimator(new DefaultItemAnimator());
                                        }

                                      // arrayAdapter.notifyDataSetChanged();
                                        adapter_items.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                              // arrayAdapter.addAll(names);
                              // mList.setAdapter(arrayAdapter);



                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
                else if(position==3)
                {
                    ArrayAdapter<String>Myadapter2=new ArrayAdapter<String>(ContactGraveCaregiver.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Afyon_Cemeteries));
                    Myadapter2.setDropDownViewResource(simple_spinner_dropdown_item);
                    mySpinner2.setAdapter(Myadapter2);
                    mySpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if(position==0)
                            {

                            }
                            else if(position==1)
                            {
                                progressBar.setVisibility(View.VISIBLE);
                                query=reference.orderByChild("Cemetery").equalTo("Kocatepe Mezarlığı");
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                      //  arrayAdapter.clear();
                                        graveCareGiversList.clear();
                                        for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                        {
                                            String Name=(String) childsnapshot.child("Name").getValue();
                                            String Surname=(String) childsnapshot.child("Surname").getValue();
                                            String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                            String city=(String) childsnapshot.child("City").getValue();
                                            String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                           // names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                           // keys.add(childsnapshot.getKey());
                                            graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                            recycler_view.setHasFixedSize(true);

                                            recycler_view.setAdapter(adapter_items);

                                            recycler_view.setItemAnimator(new DefaultItemAnimator());
                                        }

                                     //  arrayAdapter.notifyDataSetChanged();
                                        adapter_items.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                               //arrayAdapter.addAll(names);
                              // mList.setAdapter(arrayAdapter);


                            }
                            else if(position==2)
                            {
                                progressBar.setVisibility(View.VISIBLE);
                                query=reference.orderByChild("Cemetery").equalTo("Bayraktepe Mezarlığı");
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                      // arrayAdapter.clear();
                                        graveCareGiversList.clear();
                                        for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                        {
                                            String Name=(String) childsnapshot.child("Name").getValue();
                                            String Surname=(String) childsnapshot.child("Surname").getValue();
                                            String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                            String city=(String) childsnapshot.child("City").getValue();
                                            String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                           // names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                           // keys.add(childsnapshot.getKey());
                                            graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                            recycler_view.setHasFixedSize(true);

                                            recycler_view.setAdapter(adapter_items);

                                            recycler_view.setItemAnimator(new DefaultItemAnimator());
                                        }

                                      // arrayAdapter.notifyDataSetChanged();
                                        adapter_items.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                               // arrayAdapter.addAll(names);
                               // mList.setAdapter(arrayAdapter);
                            }
                            else if(position==3)
                            {
                                progressBar.setVisibility(View.VISIBLE);
                                query=reference.orderByChild("Cemetery").equalTo("Afyon cumhuriyet Şehitliği");
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                      //  arrayAdapter.clear();
                                        graveCareGiversList.clear();
                                        for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                        {
                                            String Name=(String) childsnapshot.child("Name").getValue();
                                            String Surname=(String) childsnapshot.child("Surname").getValue();
                                            String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                            String city=(String) childsnapshot.child("City").getValue();
                                            String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                          //  names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                          //  keys.add(childsnapshot.getKey());
                                            graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                            recycler_view.setHasFixedSize(true);

                                            recycler_view.setAdapter(adapter_items);

                                            recycler_view.setItemAnimator(new DefaultItemAnimator());
                                        }

                                      //  arrayAdapter.notifyDataSetChanged();
                                        adapter_items.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                              // arrayAdapter.addAll(names);
                              //  mList.setAdapter(arrayAdapter);


                            }
                            else if(position==4)
                            {
                                progressBar.setVisibility(View.VISIBLE);
                                query=reference.orderByChild("Cemetery").equalTo("Yzb.Agah Efendi Mezarlığı");
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                      //  arrayAdapter.clear();
                                        graveCareGiversList.clear();
                                        for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                        {
                                            String Name=(String) childsnapshot.child("Name").getValue();
                                            String Surname=(String) childsnapshot.child("Surname").getValue();
                                            String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                            String city=(String) childsnapshot.child("City").getValue();
                                            String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                           // names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                           // keys.add(childsnapshot.getKey());
                                            graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                            recycler_view.setHasFixedSize(true);

                                            recycler_view.setAdapter(adapter_items);

                                            recycler_view.setItemAnimator(new DefaultItemAnimator());
                                        }

                                       // mList.setAdapter(arrayAdapter);
                                        adapter_items.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                               // arrayAdapter.addAll(names);
                               // arrayAdapter.notifyDataSetChanged();




                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }

                else if(position==4)
                {

                }
                else if(position==5)
                {
                    ArrayAdapter<String>Myadapter2=new ArrayAdapter<String>(ContactGraveCaregiver.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Amasya_Cemeteries));
                    Myadapter2.setDropDownViewResource(simple_spinner_dropdown_item);
                    mySpinner2.setAdapter(Myadapter2);
                    mySpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if(position==0)
                            {

                            }
                            else if(position==1)
                            {
                                progressBar.setVisibility(View.VISIBLE);
                                query=reference.orderByChild("Cemetery").equalTo("Tekirdede Mezarlığı");
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                       // arrayAdapter.clear();
                                        graveCareGiversList.clear();
                                        for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                        {
                                            String Name=(String) childsnapshot.child("Name").getValue();
                                            String Surname=(String) childsnapshot.child("Surname").getValue();
                                            String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                            String city=(String) childsnapshot.child("City").getValue();
                                            String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                           //names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                           // keys.add(childsnapshot.getKey());
                                            graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                            recycler_view.setHasFixedSize(true);

                                            recycler_view.setAdapter(adapter_items);

                                            recycler_view.setItemAnimator(new DefaultItemAnimator());
                                        }

                                      // mList.setAdapter(arrayAdapter);
                                        adapter_items.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                //arrayAdapter.addAll(names);
                              // arrayAdapter.notifyDataSetChanged();

                            }



                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                }
                else if(position==36)
                {  ArrayAdapter<String>Myadapter2=new ArrayAdapter<String>(ContactGraveCaregiver.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Izmır_Cemeteries));
                    Myadapter2.setDropDownViewResource(simple_spinner_dropdown_item);
                    mySpinner2.setAdapter(Myadapter2);
                mySpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(position==0)
                        {

                        }
                        else if(position==1)
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            query=reference.orderByChild("Cemetery").equalTo("Aşağı Narlıdere Mezarlığı");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                  //  arrayAdapter.clear();
                                    graveCareGiversList.clear();
                                    for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                    {
                                        String Name=(String) childsnapshot.child("Name").getValue();
                                        String Surname=(String) childsnapshot.child("Surname").getValue();
                                        String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                        String city=(String) childsnapshot.child("City").getValue();
                                        String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                      // names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                       // keys.add(childsnapshot.getKey());
                                        graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                        recycler_view.setHasFixedSize(true);

                                        recycler_view.setAdapter(adapter_items);

                                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                                    }

                                   // mList.setAdapter(arrayAdapter);
                                    adapter_items.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                           // arrayAdapter.addAll(names);
                           // arrayAdapter.notifyDataSetChanged();

                        }
                        else if(position==2)
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            query=reference.orderByChild("Cemetery").equalTo("Bornova Yeni Mezarlığı");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                  //  arrayAdapter.clear();
                                    graveCareGiversList.clear();
                                    for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                    {
                                        String Name=(String) childsnapshot.child("Name").getValue();
                                        String Surname=(String) childsnapshot.child("Surname").getValue();
                                        String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                        String city=(String) childsnapshot.child("City").getValue();
                                        String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                     //  names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                       // keys.add(childsnapshot.getKey());
                                        graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                        recycler_view.setHasFixedSize(true);

                                        recycler_view.setAdapter(adapter_items);

                                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                                    }

                                 //  mList.setAdapter(arrayAdapter);
                                    adapter_items.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                          //  arrayAdapter.addAll(names);
                          //  arrayAdapter.notifyDataSetChanged();


                        }
                        else if(position==3)
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            query=reference.orderByChild("Cemetery").equalTo("Bornova Eski Mezarlığı");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                  // arrayAdapter.clear();
                                    graveCareGiversList.clear();
                                    for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                    {
                                        String Name=(String) childsnapshot.child("Name").getValue();
                                        String Surname=(String) childsnapshot.child("Surname").getValue();
                                        String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                        String city=(String) childsnapshot.child("City").getValue();
                                        String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                     //  names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                      //  keys.add(childsnapshot.getKey());
                                        graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                        recycler_view.setHasFixedSize(true);

                                        recycler_view.setAdapter(adapter_items);

                                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                                    }

                                   // mList.setAdapter(arrayAdapter);
                                    adapter_items.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                           // arrayAdapter.addAll(names);
                           // arrayAdapter.notifyDataSetChanged();


                        }
                        else if(position==4)
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            query=reference.orderByChild("Cemetery").equalTo("Buca Gökdere Mezarlığı");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                  //  arrayAdapter.clear();
                                    graveCareGiversList.clear();
                                    for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                    {
                                        String Name=(String) childsnapshot.child("Name").getValue();
                                        String Surname=(String) childsnapshot.child("Surname").getValue();
                                        String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                        String city=(String) childsnapshot.child("City").getValue();
                                        String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                       // names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                       // keys.add(childsnapshot.getKey());
                                        graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                        recycler_view.setHasFixedSize(true);

                                        recycler_view.setAdapter(adapter_items);

                                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                                    }

                                 //   mList.setAdapter(arrayAdapter);
                                    adapter_items.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                           // arrayAdapter.addAll(names);
                         //  arrayAdapter.notifyDataSetChanged();


                        }
                        else if(position==5)
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            query=reference.orderByChild("Cemetery").equalTo("Yeni Buca Mezarlığı");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                 //  arrayAdapter.clear();
                                    graveCareGiversList.clear();
                                    for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                    {
                                        String Name=(String) childsnapshot.child("Name").getValue();
                                        String Surname=(String) childsnapshot.child("Surname").getValue();
                                        String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                        String city=(String) childsnapshot.child("City").getValue();
                                        String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                      // names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                      //  keys.add(childsnapshot.getKey());
                                        graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                        recycler_view.setHasFixedSize(true);

                                        recycler_view.setAdapter(adapter_items);

                                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                                    }

                                   // mList.setAdapter(arrayAdapter);
                                    adapter_items.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                         // arrayAdapter.addAll(names);
                         // arrayAdapter.notifyDataSetChanged();


                        }
                        else if(position==6)
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            query=reference.orderByChild("Cemetery").equalTo("Eski Bornova Mezarlığı");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                  //  arrayAdapter.clear();
                                    graveCareGiversList.clear();
                                    for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                    {
                                        String Name=(String) childsnapshot.child("Name").getValue();
                                        String Surname=(String) childsnapshot.child("Surname").getValue();
                                        String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                        String city=(String) childsnapshot.child("City").getValue();
                                        String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                       // names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                       // keys.add(childsnapshot.getKey());
                                        graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                        recycler_view.setHasFixedSize(true);

                                        recycler_view.setAdapter(adapter_items);

                                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                                    }

                                  //  mList.setAdapter(arrayAdapter);
                                    adapter_items.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                           // arrayAdapter.addAll(names);
                           // arrayAdapter.notifyDataSetChanged();


                        }
                        else if(position==7)
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            query=reference.orderByChild("Cemetery").equalTo("Eski Balçova Mezarlığı");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                   // arrayAdapter.clear();
                                    graveCareGiversList.clear();
                                    for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                    {
                                        String Name=(String) childsnapshot.child("Name").getValue();
                                        String Surname=(String) childsnapshot.child("Surname").getValue();
                                        String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                        String city=(String) childsnapshot.child("City").getValue();
                                        String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                     //  names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                       // keys.add(childsnapshot.getKey());
                                        graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                        recycler_view.setHasFixedSize(true);

                                        recycler_view.setAdapter(adapter_items);

                                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                                    }

                                //   mList.setAdapter(arrayAdapter);
                                    adapter_items.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                           // arrayAdapter.addAll(names);
                           //arrayAdapter.notifyDataSetChanged();


                        }
                        else if(position==8)
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            query=reference.orderByChild("Cemetery").equalTo("Cennet Bahçesi Mezarlığı");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                  //  arrayAdapter.clear();
                                    graveCareGiversList.clear();
                                    for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                    {
                                        String Name=(String) childsnapshot.child("Name").getValue();
                                        String Surname=(String) childsnapshot.child("Surname").getValue();
                                        String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                        String city=(String) childsnapshot.child("City").getValue();
                                        String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                     //  names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                       // keys.add(childsnapshot.getKey());
                                        graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                        recycler_view.setHasFixedSize(true);

                                        recycler_view.setAdapter(adapter_items);

                                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                                    }

                                  // mList.setAdapter(arrayAdapter);
                                    adapter_items.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                          //  arrayAdapter.addAll(names);
                          //  arrayAdapter.notifyDataSetChanged();
                        }
                        else if(position==9)
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            query=reference.orderByChild("Cemetery").equalTo("Dikili Mezarlığı");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                  //  arrayAdapter.clear();
                                    graveCareGiversList.clear();
                                    for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                    {
                                        String Name=(String) childsnapshot.child("Name").getValue();
                                        String Surname=(String) childsnapshot.child("Surname").getValue();
                                        String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                        String city=(String) childsnapshot.child("City").getValue();
                                        String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                      //  names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                      //  keys.add(childsnapshot.getKey());
                                        graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                        recycler_view.setHasFixedSize(true);

                                        recycler_view.setAdapter(adapter_items);

                                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                                    }

                                   // mList.setAdapter(arrayAdapter);
                                    adapter_items.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                         //   arrayAdapter.addAll(names);
                          //  arrayAdapter.notifyDataSetChanged();

                        }
                        else if(position==10)
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            query=reference.orderByChild("Cemetery").equalTo("Doğançay Mezarlığı");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                  //  arrayAdapter.clear();
                                    graveCareGiversList.clear();
                                    for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                    {
                                        String Name=(String) childsnapshot.child("Name").getValue();
                                        String Surname=(String) childsnapshot.child("Surname").getValue();
                                        String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                        String city=(String) childsnapshot.child("City").getValue();
                                        String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                      //  names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                      //  keys.add(childsnapshot.getKey());
                                        graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                        recycler_view.setHasFixedSize(true);

                                        recycler_view.setAdapter(adapter_items);

                                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                                    }

                                   // mList.setAdapter(arrayAdapter);
                                    adapter_items.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                           // arrayAdapter.addAll(names);
                           // arrayAdapter.notifyDataSetChanged();
                        }
                        else if(position==11)
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            query=reference.orderByChild("Cemetery").equalTo("Eski Foça Yeni Halk  Mezarlığı");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                  //  arrayAdapter.clear();
                                    graveCareGiversList.clear();
                                    for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                    {
                                        String Name=(String) childsnapshot.child("Name").getValue();
                                        String Surname=(String) childsnapshot.child("Surname").getValue();
                                        String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                        String city=(String) childsnapshot.child("City").getValue();
                                        String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                      //  names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                      //  keys.add(childsnapshot.getKey());
                                        graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                        recycler_view.setHasFixedSize(true);

                                        recycler_view.setAdapter(adapter_items);

                                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                                    }

                                  //  mList.setAdapter(arrayAdapter);
                                    adapter_items.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                           // arrayAdapter.addAll(names);
                           // arrayAdapter.notifyDataSetChanged();
                        }
                        else if(position==12)
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            query=reference.orderByChild("Cemetery").equalTo("Gaziemir  Mezarlığı");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                  //  arrayAdapter.clear();
                                    graveCareGiversList.clear();
                                    for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                    {
                                        String Name=(String) childsnapshot.child("Name").getValue();
                                        String Surname=(String) childsnapshot.child("Surname").getValue();
                                        String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                        String city=(String) childsnapshot.child("City").getValue();
                                        String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                       // names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                      //  keys.add(childsnapshot.getKey());
                                        graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                        recycler_view.setHasFixedSize(true);

                                        recycler_view.setAdapter(adapter_items);

                                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                                    }

                                   // mList.setAdapter(arrayAdapter);
                                    adapter_items.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                           // arrayAdapter.addAll(names);
                          //  arrayAdapter.notifyDataSetChanged();



                        }
                        else if(position==13)
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            query=reference.orderByChild("Cemetery").equalTo("Hacılarkırı Mezarlığı");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                   // arrayAdapter.clear();
                                    graveCareGiversList.clear();
                                    for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                    {
                                        String Name=(String) childsnapshot.child("Name").getValue();
                                        String Surname=(String) childsnapshot.child("Surname").getValue();
                                        String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                        String city=(String) childsnapshot.child("City").getValue();
                                        String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                       // names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                      //  keys.add(childsnapshot.getKey());
                                        graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                        recycler_view.setHasFixedSize(true);

                                        recycler_view.setAdapter(adapter_items);

                                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                                    }

                                    //mList.setAdapter(arrayAdapter);
                                    adapter_items.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                           // arrayAdapter.addAll(names);
                           // arrayAdapter.notifyDataSetChanged();

                        }
                        else if(position==14)
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            query=reference.orderByChild("Cemetery").equalTo("Işıkkent Mezarlığı");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                  //  arrayAdapter.clear();
                                    graveCareGiversList.clear();
                                    for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                    {
                                        String Name=(String) childsnapshot.child("Name").getValue();
                                        String Surname=(String) childsnapshot.child("Surname").getValue();
                                        String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                        String city=(String) childsnapshot.child("City").getValue();
                                        String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                      //  names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                      //  keys.add(childsnapshot.getKey());
                                        graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                        recycler_view.setHasFixedSize(true);

                                        recycler_view.setAdapter(adapter_items);

                                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                                    }

                                  //  mList.setAdapter(arrayAdapter);
                                    adapter_items.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                           // arrayAdapter.addAll(names);
                           // arrayAdapter.notifyDataSetChanged();

                        }
                        else if(position==15)
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            query=reference.orderByChild("Cemetery").equalTo("Kokluca Mezarlığı");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                  //  arrayAdapter.clear();
                                    graveCareGiversList.clear();
                                    for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                    {
                                        String Name=(String) childsnapshot.child("Name").getValue();
                                        String Surname=(String) childsnapshot.child("Surname").getValue();
                                        String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                        String city=(String) childsnapshot.child("City").getValue();
                                        String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                      //  names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                      //  keys.add(childsnapshot.getKey());
                                        graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                        recycler_view.setHasFixedSize(true);

                                        recycler_view.setAdapter(adapter_items);

                                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                                    }

                                  //  mList.setAdapter(arrayAdapter);
                                    adapter_items.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                           // arrayAdapter.addAll(names);
                          //  arrayAdapter.notifyDataSetChanged();


                        }
                        else if(position==16)
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            query=reference.orderByChild("Cemetery").equalTo("Paşaköprü Hiristiyan  Mezarlığı");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                  //  arrayAdapter.clear();
                                    graveCareGiversList.clear();
                                    for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                    {
                                        String Name=(String) childsnapshot.child("Name").getValue();
                                        String Surname=(String) childsnapshot.child("Surname").getValue();
                                        String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                        String city=(String) childsnapshot.child("City").getValue();
                                        String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                       // names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                       // keys.add(childsnapshot.getKey());
                                        graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                        recycler_view.setHasFixedSize(true);

                                        recycler_view.setAdapter(adapter_items);

                                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                                    }

                                   // mList.setAdapter(arrayAdapter);
                                    adapter_items.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                          //  arrayAdapter.addAll(names);
                          //  arrayAdapter.notifyDataSetChanged();
                        }
                        else if(position==17)
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            query=reference.orderByChild("Cemetery").equalTo("Paşaköprü Müslüman Mezarlığı");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                  //  arrayAdapter.clear();
                                    graveCareGiversList.clear();
                                    for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                    {
                                        String Name=(String) childsnapshot.child("Name").getValue();
                                        String Surname=(String) childsnapshot.child("Surname").getValue();
                                        String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                        String city=(String) childsnapshot.child("City").getValue();
                                        String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                      //  names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                      //  keys.add(childsnapshot.getKey());
                                        graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                        recycler_view.setHasFixedSize(true);

                                        recycler_view.setAdapter(adapter_items);

                                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                                    }

                                  //  mList.setAdapter(arrayAdapter);
                                    adapter_items.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                           // arrayAdapter.addAll(names);
                           // arrayAdapter.notifyDataSetChanged();
                        }
                        else if(position==18)
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            query=reference.orderByChild("Cemetery").equalTo("Soğukkuyu Mezarlığı");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                  //  arrayAdapter.clear();
                                    graveCareGiversList.clear();
                                    for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                    {
                                        String Name=(String) childsnapshot.child("Name").getValue();
                                        String Surname=(String) childsnapshot.child("Surname").getValue();
                                        String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                        String city=(String) childsnapshot.child("City").getValue();
                                        String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                       // names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                       // keys.add(childsnapshot.getKey());
                                        graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                        recycler_view.setHasFixedSize(true);

                                        recycler_view.setAdapter(adapter_items);

                                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                                    }

                                  //  mList.setAdapter(arrayAdapter);
                                    adapter_items.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                           // arrayAdapter.addAll(names);
                           // arrayAdapter.notifyDataSetChanged();
                        }
                        else if(position==19)
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            query=reference.orderByChild("Cemetery").equalTo("Pınarbaşı Mezarlığı");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                  //  arrayAdapter.clear();
                                    graveCareGiversList.clear();
                                    for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                    {
                                        String Name=(String) childsnapshot.child("Name").getValue();
                                        String Surname=(String) childsnapshot.child("Surname").getValue();
                                        String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                        String city=(String) childsnapshot.child("City").getValue();
                                        String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                       // names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                      //  keys.add(childsnapshot.getKey());
                                        graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                        recycler_view.setHasFixedSize(true);

                                        recycler_view.setAdapter(adapter_items);

                                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                                    }

                                 //   mList.setAdapter(arrayAdapter);
                                    adapter_items.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                           // arrayAdapter.addAll(names);
                           // arrayAdapter.notifyDataSetChanged();



                        }
                        else if(position==20)
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            query=reference.orderByChild("Cemetery").equalTo("Sarnıç Mezarlığı");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                   // arrayAdapter.clear();
                                    graveCareGiversList.clear();
                                    for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                    {
                                        String Name=(String) childsnapshot.child("Name").getValue();
                                        String Surname=(String) childsnapshot.child("Surname").getValue();
                                        String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                        String city=(String) childsnapshot.child("City").getValue();
                                        String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                       // names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                       // keys.add(childsnapshot.getKey());
                                        graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                        recycler_view.setHasFixedSize(true);

                                        recycler_view.setAdapter(adapter_items);

                                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                                    }

                                   // mList.setAdapter(arrayAdapter);
                                    adapter_items.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            //arrayAdapter.addAll(names);
                           // arrayAdapter.notifyDataSetChanged();
                        }
                        else if(position==21)
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            query=reference.orderByChild("Cemetery").equalTo("Yeşilyurt Mezarlığı");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                   // arrayAdapter.clear();
                                    graveCareGiversList.clear();
                                    for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                    {
                                        String Name=(String) childsnapshot.child("Name").getValue();
                                        String Surname=(String) childsnapshot.child("Surname").getValue();
                                        String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                        String city=(String) childsnapshot.child("City").getValue();
                                        String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                      //  names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                       // keys.add(childsnapshot.getKey());
                                        graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                        recycler_view.setHasFixedSize(true);

                                        recycler_view.setAdapter(adapter_items);

                                        recycler_view.setItemAnimator(new DefaultItemAnimator());
                                    }


                                   // mList.setAdapter(arrayAdapter);
                                    adapter_items.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                           // arrayAdapter.addAll(names);
                           // arrayAdapter.notifyDataSetChanged();




                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                }


                    else if(position==6)
                {
                    ArrayAdapter<String>Myadapter3=new ArrayAdapter<String>(ContactGraveCaregiver.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Ankara_Cemeteries));
                    mySpinner2.setAdapter(Myadapter3);
                    mySpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if(position==0)
                            {

                            }
                            else if(position==1)
                            {
                                progressBar.setVisibility(View.VISIBLE);
                                query=reference.orderByChild("Cemetery").equalTo("Karşıyaka Mezarlığı");
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                      //  arrayAdapter.clear();
                                        graveCareGiversList.clear();
                                        for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                        {
                                            String Name=(String) childsnapshot.child("Name").getValue();
                                            String Surname=(String) childsnapshot.child("Surname").getValue();
                                            String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                            String city=(String) childsnapshot.child("City").getValue();
                                            String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                          //  names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                           // keys.add(childsnapshot.getKey());
                                            graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                            recycler_view.setHasFixedSize(true);

                                            recycler_view.setAdapter(adapter_items);

                                            recycler_view.setItemAnimator(new DefaultItemAnimator());
                                        }

                                       // mList.setAdapter(arrayAdapter);
                                        adapter_items.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                               // arrayAdapter.addAll(names);
                               // arrayAdapter.notifyDataSetChanged();

                            }
                            else if(position==2)
                            {
                                progressBar.setVisibility(View.VISIBLE);
                                /*Cemetery="Cebeci Mezarlığı";
                                Register.setEnabled(true);*/
                                query=reference.orderByChild("Cemetery").equalTo("Cebeci Mezarlığı");
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                      //  arrayAdapter.clear();
                                        graveCareGiversList.clear();
                                        for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                        {
                                            String Name=(String) childsnapshot.child("Name").getValue();
                                            String Surname=(String) childsnapshot.child("Surname").getValue();
                                            String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                            String city=(String) childsnapshot.child("City").getValue();
                                            String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                           // names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                          //  keys.add(childsnapshot.getKey());
                                            graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                            recycler_view.setHasFixedSize(true);

                                            recycler_view.setAdapter(adapter_items);

                                            recycler_view.setItemAnimator(new DefaultItemAnimator());
                                        }

                                       // mList.setAdapter(arrayAdapter);
                                        adapter_items.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                              //  arrayAdapter.addAll(names);
                               // arrayAdapter.notifyDataSetChanged();
                            }
                            else if(position==3)
                            {
                                progressBar.setVisibility(View.VISIBLE);
                               // Cemetery="Sincan Mezarlığı";
                                //Register.setEnabled(true);
                                query=reference.orderByChild("Cemetery").equalTo("Sincan Mezarlığı");
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                       // arrayAdapter.clear();
                                        graveCareGiversList.clear();
                                        for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                        {
                                            String Name=(String) childsnapshot.child("Name").getValue();
                                            String Surname=(String) childsnapshot.child("Surname").getValue();
                                            String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                            String city=(String) childsnapshot.child("City").getValue();
                                            String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                          //  names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                          //  keys.add(childsnapshot.getKey());
                                            graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                            recycler_view.setHasFixedSize(true);

                                            recycler_view.setAdapter(adapter_items);

                                            recycler_view.setItemAnimator(new DefaultItemAnimator());
                                        }

                                      //  mList.setAdapter(arrayAdapter);
                                        adapter_items.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                               /// arrayAdapter.addAll(names);
                               // arrayAdapter.notifyDataSetChanged();


                            }
                            else if(position==4)
                            {
                                progressBar.setVisibility(View.VISIBLE);
                                //Cemetery="Ortaköy Mezarlığı";
                                //Register.setEnabled(true);
                                query=reference.orderByChild("Cemetery").equalTo("Ortaköy Mezarlığı");
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                      //  arrayAdapter.clear();
                                        graveCareGiversList.clear();
                                        for(DataSnapshot childsnapshot:dataSnapshot.getChildren())
                                        {
                                            String Name=(String) childsnapshot.child("Name").getValue();
                                            String Surname=(String) childsnapshot.child("Surname").getValue();
                                            String phone=(String) childsnapshot.child("phoneNumber").getValue();
                                            String city=(String) childsnapshot.child("City").getValue();
                                            String cemetery=(String) childsnapshot.child("Cemetery").getValue();
                                          //  names.add("Ad: "+Name +"\n"+"Soyad: "+Surname+"\n"+"Telefon no: "+String.valueOf(phone)+"\n"+"Şehir: "+city+"\n"+"Çalıştığı Mezarlık: "+cemetery+"\n");
                                          //  keys.add(childsnapshot.getKey());
                                            graveCareGiversList.add(new GraveCareGivers(Name+" ",Surname,"Gsm no: "+ String.valueOf(phone)));
                                            recycler_view.setHasFixedSize(true);

                                            recycler_view.setAdapter(adapter_items);

                                            recycler_view.setItemAnimator(new DefaultItemAnimator());
                                        }

                                      //  mList.setAdapter(arrayAdapter);
                                        adapter_items.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                //arrayAdapter.addAll(names);
                               // arrayAdapter.notifyDataSetChanged();

                            }

                            else
                            {
                                //Register.setEnabled(true);
                            }


                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });



                }
                else
                {//btn_ok.setEnabled(true);
                    }




            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mySpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    //btn_ok.setEnabled(false);
                }
                else
                {
                //btn_ok.setEnabled(true);
                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);


    }
}
