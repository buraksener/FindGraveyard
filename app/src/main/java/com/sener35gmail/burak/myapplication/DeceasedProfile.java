package com.sener35gmail.burak.myapplication;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DeceasedProfile extends AppCompatActivity {

    TextView et_DeceasedFullnane;
    TextView et_DeceasedDOB;
    TextView et_DeceasedDOD;
    TextView et_DeceasedGender;
    TextView et_DeceasedJOb;
    TextView et_Bio;

    TextView et_ShowDeceasedFullname;
    TextView et_ShowDeceasedDOB;
    TextView et_ShowDeceasedDOD;
    TextView et_ShowDeceasedGender;
    TextView et_ShowDeceasedJOb;
    TextView et_ShowDeceasedBio;
    ImageView deceased_Image;
    FirebaseStorage storage;

    Button btn_showComment;
    Button btn_showGravelocation;

    ImageButton back;

    StorageReference storageReference;
    StorageReference ref;

    String Fullname;
    String job;
    String gender;
    String DOB;
    String DOD;
    String bio;


    String deceasedKey;
    String Latitude;
    String Longitude;
    String DownloadUrl;

    AlertDialog alertDialog;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;
    String VisitorName;
    String VisitorSurname;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deceased_profile);
        et_DeceasedFullnane=(TextView) findViewById(R.id.et_DecesedFullname);
        et_DeceasedDOB=(TextView) findViewById(R.id.et_DeceasedDOB);
        et_DeceasedDOD=(TextView) findViewById(R.id.et_DeceasedDOD);
        et_DeceasedGender=(TextView) findViewById(R.id.et_DeceasedGender);
        et_DeceasedJOb=(TextView) findViewById(R.id.et_DeceasedJob);
        et_Bio=(TextView) findViewById(R.id.et_DeceasedBio);
        et_ShowDeceasedFullname=(TextView) findViewById(R.id.textView22);
        et_ShowDeceasedDOB=(TextView) findViewById(R.id.textView23);
        et_ShowDeceasedDOD=(TextView) findViewById(R.id.textView24);
        et_ShowDeceasedGender=(TextView) findViewById(R.id.textView25);
        et_ShowDeceasedJOb=(TextView) findViewById(R.id.textView26);
        et_ShowDeceasedBio=(TextView) findViewById(R.id.textView27);
        deceased_Image=(ImageView) findViewById(R.id.Deceased_imageView);
        btn_showComment=(Button) findViewById(R.id.showComments);
        btn_showGravelocation=(Button) findViewById(R.id.showGraveLocation);
        back=(ImageButton) findViewById(R.id.backToSearch);

        database=FirebaseDatabase.getInstance();
        reference=database.getReference("Deceaceds");

      /*  String Fullname=getIntent().getStringExtra("Fullname");
        String DOB=getIntent().getStringExtra("DateOfBirth");
        String DOD=getIntent().getStringExtra("DateOfDeath");
        String gender=getIntent().getStringExtra("Gender");
        String job=getIntent().getStringExtra("DeceasedJob");
        String bio=getIntent().getStringExtra("DeceasedBio");*/

       // DownloadUrl=getIntent().getStringExtra("URL");
        deceasedKey=getIntent().getStringExtra("DeceasedKey");
        VisitorName=getIntent().getStringExtra("VisitorName");
        VisitorSurname=getIntent().getStringExtra("VisitorSurname");






        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

       Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        reference.child(deceasedKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 Fullname=(String) dataSnapshot.child("DeceasedFullName").getValue();
                 job=(String) dataSnapshot.child("DeceasedJob").getValue();
                 gender=(String) dataSnapshot.child("DeceasedGender").getValue();
                DOB= (String)  dataSnapshot.child("DateOfBirth").getValue();
                DOD=(String) dataSnapshot.child("DateOfDeath").getValue();
                bio=(String) dataSnapshot.child("DeceasedBiography").getValue();
                DownloadUrl=(String) dataSnapshot.child("ImageUrl").getValue();
                Latitude = (String) dataSnapshot.child("Latitude").getValue();
                Longitude = (String) dataSnapshot.child("Longitude").getValue();

                et_ShowDeceasedFullname.setText(Fullname);
                et_ShowDeceasedJOb.setText(job);
                et_ShowDeceasedGender.setText(gender);
                et_ShowDeceasedDOB.setText(DOB);
                et_ShowDeceasedDOD.setText(DOD);
                et_ShowDeceasedBio.setText(bio);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });




       try{
            ref = storageReference.child("images").child(deceasedKey);
       }
       catch (NullPointerException e)
       {

       }


        if(deceasedKey!=null)
        {
            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    URL url= null;
                    try {
                        url = new URL(DownloadUrl);
                        uri= Uri.parse(url.toURI().toString());


                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    if(DownloadUrl!=null)
                    {
                        Glide.with(getApplicationContext()).load(url).into(deceased_Image);
                    }




                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Toast.makeText(getApplicationContext(),"İşlem başarız", Toast.LENGTH_SHORT).show();
                }
            });
        }




      /*  et_ShowDeceasedFullname.setText(Fullname);
        et_ShowDeceasedJOb.setText(job);
        et_ShowDeceasedGender.setText(gender);
        et_ShowDeceasedDOB.setText(DOB);
        et_ShowDeceasedDOD.setText(DOD);
        et_ShowDeceasedBio.setText(bio);*/


       //display grave location \\
        btn_showGravelocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Latitude=getIntent().getStringExtra("latitude");
               // Longitude=getIntent().getStringExtra("longitude");
                if(Latitude!=null && Longitude!=null)
                {
                    Intent showLocation=new Intent(DeceasedProfile.this,MapsActivity.class);
                    showLocation.putExtra("latitude",Latitude);
                    showLocation.putExtra("longitude",Longitude);


                    startActivity(showLocation);
                    finish();

                }
                else
                {
                    AlertDialog.Builder builder=new AlertDialog.Builder(DeceasedProfile.this,R.style.MyDialog);
                    builder.setTitle("Uyarı");
                    builder.setMessage("Mezar konumu bu profilini oluşturan kişi tarafından kayıt edilmemiştir!");
                    builder.setCancelable(true);
                    builder.setNeutralButton("Tamam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    alertDialog=builder.create();
                    alertDialog.show();
                    Button neutralButton=alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                    neutralButton.setTextColor(Color.parseColor("#FF0B8B42"));
                    neutralButton.setBackgroundColor(Color.parseColor("#FFE1FCEA"));
                    getWindow().setBackgroundDrawableResource(R.color.white);


                }

            }
        });


        //Display  deceased's comments \\\
        btn_showComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent=new Intent(DeceasedProfile.this,ShowDeceasedComments.class);
                newIntent.putExtra("key",deceasedKey);


                startActivity(newIntent);
                finish();

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onBackPressed();

                Intent back=new Intent(getApplicationContext(),FindDeceasedProfil.class);
               startActivity(back);
                finish();
            }
        });



       String AuthUser= getIntent().getExtras().getString("AuthenticatedUser");

        //Intent intent=new Intent(getApplicationContext(),SendMessageToAuthenticatedUser.class);
       // intent.putExtra("authİd",AuthUser);


        // tapping and display deceased image for big size
        deceased_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder ImageDialog=new AlertDialog.Builder(DeceasedProfile.this);
                deceased_Image=new ImageView(DeceasedProfile.this);
                ImageDialog.setView(deceased_Image);
                ImageDialog.setNeutralButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                ImageDialog.show();

            }
        });
        //Save visitors
        SaveVisitors();





    }

    private void SaveVisitors() {

        //Getting current time infos\\

        if(deceasedKey!=null)
        {
            @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
             date = df.format(Calendar.getInstance().getTime());

            reference=database.getReference("Deceaceds").child(deceasedKey).child("Visitors");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Map<String,String> newMap= new HashMap<>();
                    newMap.put("Date", date + " "+"Tarihinde " + VisitorName + " " + VisitorSurname+" "+Fullname+" profilini ziyaret etti");
                    String VisitorKey = reference.push().getKey();
                    reference.child(VisitorKey).setValue(newMap);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

    }

    //Toolbar items\\\
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.deceased_profile_menu,menu);
        return true;
    }

    //defining toolbar items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
      /*  if(id==R.id.action_message)
        {
            Intent message=new Intent(getApplicationContext(),ChatMessageUsertoUser.class);
            startActivity(message);

        }
        else if(id==R.id.action_report)
        {

        }

        else if(id==R.id.action_auth)
        {

        }*/
      if(id==R.id.report)

      {
          View parentlaout=findViewById(android.R.id.content);
          Snackbar snackbar=Snackbar.make(parentlaout,"Bildiriniz İletildi.",Snackbar.LENGTH_LONG);
          snackbar.show();
      }


        return super.onOptionsItemSelected(item);
    }
}
