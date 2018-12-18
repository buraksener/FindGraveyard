package com.sener35gmail.burak.myapplication;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sener35gmail.burak.myapplication.Model.Deceased;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FindDeceasedProfil extends AppCompatActivity {
    static final int DIALOG_ERROR_CONNECTION = 1;
    EditText et_deceaed_name;
    EditText et_deceaed_surname;
    Button   Search;
    Button Clear;
    ImageButton btn_homepage;
    //Button showComments;
    ImageButton home;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;
    DatabaseReference users;

    ProgressBar progressBar;

    Query query;
    Query query2;
    String Name;
    String Surname;
   // ArrayAdapter<String> arrayAdapter;
    //ArrayList<String> names = new ArrayList<>();
   // ListView mList;
   // ImageView deceased_Image;
    RecyclerView recyclerView;
    private List<Deceased> deceasedList;
    ArrayList<String> keyList=new ArrayList<>();
    ArrayAdapter<String> arrayAdapter_keyList;

    StorageReference storageReference;
    FirebaseStorage storage;
    File Current_file;
    String deceasedKey;

    private Uri downloadUrl;
    String Latitude;
    String Longitude;
    String uid;
    String  CurrentName;
    String  CurrentSurname;
    Context context;

    private String date;

    private InterstitialAd mInterstitialAd;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_deceased_profil);
        et_deceaed_name = (EditText) findViewById(R.id.findDeceasedName);
        et_deceaed_surname = (EditText) findViewById(R.id.findDeceasedSurname);
        Search = (Button) findViewById(R.id.btn_search);
        Clear = (Button) findViewById(R.id.btn_clear);
        home = (ImageButton) findViewById(R.id.Home);
        btn_homepage = (ImageButton) findViewById(R.id.backtoHome);
        context=FindDeceasedProfil.this;
        progressBar = (ProgressBar) findViewById(R.id.progress_search);
        Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);




        recyclerView=(RecyclerView) findViewById(R.id.recycler_view1);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
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
        arrayAdapter_keyList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, keyList);


        MobileAds.initialize(this,

                "ca-app-pub-3343180155039030~8172843888");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("\n" + "ca-app-pub-3343180155039030/9318404599");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        database = FirebaseDatabase.getInstance();

        databaseReference = database.getReference("Deceaceds");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        final ScrollView scrollview = ((ScrollView) findViewById(R.id.scrollView));

        try {
            Current_file = File.createTempFile("images", "jpeg");
        } catch (IOException e) {
            e.printStackTrace();
        }



        //Getting current time infos\\
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
         date = df.format(Calendar.getInstance().getTime());

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        users = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CurrentName = (String) dataSnapshot.child("Name").getValue();
                CurrentSurname = (String) dataSnapshot.child("Surname").getValue();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        users.addListenerForSingleValueEvent(eventListener);








        //Searching processs starts below\\\\
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isOnline(getApplication())) {
                    showDialog(DIALOG_ERROR_CONNECTION); //displaying the created dialog.
                }
                else
                {
                    //if the device is connected to internet
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        Log.d("TAG", "The interstitial wasn't loaded yet.");
                        Toast.makeText(getApplicationContext(), "Hata:Reklam yüklenemedi.", Toast.LENGTH_SHORT).show();
                    }


                    try {
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    Name = et_deceaed_name.getEditableText().toString();
                    Surname = et_deceaed_surname.getEditableText().toString();

                    if (Name.equals("") || Surname.equals("")) {
                        progressBar.setVisibility(View.GONE);

                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                    }


                    query = databaseReference.orderByChild("DeceasedName").equalTo(Name);
                    if (Name.equals("") && Surname.equals("")) {
                        Toast.makeText(getApplicationContext(), "Arama yapmak için lütfen ad ve soyad giriniz!", Toast.LENGTH_LONG).show();

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                scrollview.fullScroll(ScrollView.FOCUS_UP);
                            }
                        };
                    }


                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.equals(null)) {
                                Toast.makeText(getApplicationContext(), "Aranılan merhum sistemde mevcut değildir", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }

                            else {
                                Toast.makeText(getApplicationContext(), "Arama sonucu başarısız,Bu kişi kayıtlı olmayabilir!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                            deceasedList.clear();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String deceasedName = (String) snapshot.child("DeceasedName").getValue();
                                String deceasedSurname = (String) snapshot.child("DeceasedSurname").getValue();

                                try {
                                    if (deceasedName.toLowerCase().equals(Name.toLowerCase()) && deceasedSurname.toLowerCase().equals(Surname.toLowerCase())) {

                                        String deceased_dob = (String) snapshot.child("DateOfBirth").getValue();
                                        String deceased_dod = (String) snapshot.child("DateOfDeath").getValue();
                                        final String DownloadUrl = (String) snapshot.child("ImageUrl").getValue();
                                        deceasedKey = (String) snapshot.child("DeceasedKey").getValue();

                                        keyList.add(deceasedKey);


                                        SimpleRecyclerAdapterForDeceased adapterForDeceased1=new SimpleRecyclerAdapterForDeceased(deceasedList, new CustomItemClickListener() {
                                            @Override
                                            public void onItemClick(View v, int position) {
                                                Deceased deceased=deceasedList.get(position);
                                                deceasedKey=keyList.get(position);
                                                Intent pass=new Intent(FindDeceasedProfil.this,DeceasedProfile.class);
                                                pass.putExtra("DeceasedKey",deceasedKey);
                                                pass.putExtra("VisitorName",CurrentName);
                                                pass.putExtra("VisitorSurname",CurrentSurname);
                                                startActivity(pass);

                                            }
                                        },context);



                                        deceasedList.add(new Deceased("ad soyad: "+deceasedName+" ",deceasedSurname,"Doğum tarihi: "+String.valueOf(deceased_dob)+" ","Ölüm Tarihi: "+String.valueOf(deceased_dod),DownloadUrl));

                                        recyclerView.setHasFixedSize(true);
                                        recyclerView.setAdapter(adapterForDeceased1);
                                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                                        adapterForDeceased1.notifyDataSetChanged();

                                        progressBar.setVisibility(View.GONE);


                                    } else if (deceasedName.toUpperCase().equals(Name.toUpperCase()) && deceasedSurname.toUpperCase().equals(Surname.toUpperCase())) {

                                        String deceased_dob = (String) snapshot.child("DateOfBirth").getValue();
                                        String deceased_dod = (String) snapshot.child("DateOfDeath").getValue();
                                        deceasedKey = (String) snapshot.child("DeceasedKey").getValue();
                                        final String DownloadUrl = (String) snapshot.child("ImageUrl").getValue();
                                        keyList.add(deceasedKey);


                                        SimpleRecyclerAdapterForDeceased adapterForDeceased2=new SimpleRecyclerAdapterForDeceased(deceasedList, new CustomItemClickListener() {
                                            @Override
                                            public void onItemClick(View v, int position) {
                                                Deceased deceased=deceasedList.get(position);
                                                deceasedKey=keyList.get(position);
                                                Intent pass=new Intent(FindDeceasedProfil.this,DeceasedProfile.class);
                                                pass.putExtra("DeceasedKey",deceasedKey);
                                                startActivity(pass);

                                            }
                                        },context);

                                        deceasedList.add(new Deceased("ad soyad: "+deceasedName+" ",deceasedSurname,"Doğum tarihi: "+String.valueOf(deceased_dob)+" ","Ölüm Tarihi: "+String.valueOf(deceased_dod),DownloadUrl));
                                        recyclerView.setHasFixedSize(true);
                                        recyclerView.setAdapter(adapterForDeceased2);
                                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                                        adapterForDeceased2.notifyDataSetChanged();
                                    }

                                }
                                catch (NullPointerException e)
                                {}
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }
               // RecordVisitors();

            }
        });



         //Buttons are in activity\\\
        Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name=et_deceaed_name.getText().toString();
                Surname=et_deceaed_surname.getText().toString();
                et_deceaed_name.setText("");
                et_deceaed_surname.setText("");
            }
        });


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home=new Intent(FindDeceasedProfil.this,HomePage.class);
                startActivity(home);
                finish();            }
        });

        btn_homepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

   /* private void RecordVisitors() {

        //Finding deceased's visitors and recording\\
        if (deceasedKey != null) {

            databaseReference=database.getReference("Deceaceds").child(deceasedKey).child("Visitors");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Map<String,String> newMap=new HashMap<>();
                    newMap.put("Date", date + " "+"Tarihinde " + CurrentName + " " + CurrentSurname+" ziyaret etti");
                    String VisitorKey = database.getReference("Visitors").push().getKey();
                    FirebaseDatabase.getInstance().getReference("Deceaceds").child(deceasedKey).child("Visitors").child(VisitorKey).setValue(newMap);


                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }*/


    //presss back button\\
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);


    }


  //Controlling internet connection\\\
    public boolean isOnline(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        if (ni != null && ni.isConnected())
            return true;
        else
            return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case DIALOG_ERROR_CONNECTION:
                AlertDialog.Builder errorDialog = new AlertDialog.Builder(this);
                errorDialog.setTitle("Hata");
                errorDialog.setMessage("İnternet bağlantısı yok.\nLütfen WIFI erişim noktanızı kontrol edin ve ya Mobil verinizin açık olduğundan emin olun.");
                errorDialog.setNeutralButton("OK",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }


                        });

                AlertDialog errorAlert = errorDialog.create();
                getWindow().setBackgroundDrawableResource(R.color.white);
                return errorAlert;
            default:
                break;
        }
        return dialog;

    }


}
