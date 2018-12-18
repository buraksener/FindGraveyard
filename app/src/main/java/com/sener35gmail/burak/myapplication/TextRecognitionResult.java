package com.sener35gmail.burak.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class TextRecognitionResult extends AppCompatActivity {

TextView tw_fullname;
TextView tw_gender;
TextView tw_job;
TextView tw_bd;
TextView tw_dd;
TextView tw_bio;
ImageView img;

String fullname;
String name;
String surname;
String job;
String gender;
String birtdate;
String deathdate;
String bio;
String DownloadUrl;
String deceasedKey;

FirebaseDatabase database;
DatabaseReference reference;

    FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognition_result);
        tw_fullname=(TextView) findViewById(R.id.deceased_fullname);
        tw_gender=(TextView) findViewById(R.id.deceased_gender);
        tw_job=(TextView) findViewById(R.id.deceased_job);
        tw_bd=(TextView) findViewById(R.id.deceased_birthdate);
        tw_dd=(TextView) findViewById(R.id.deceased_deathdate);
        tw_bio=(TextView) findViewById(R.id.d_bio2);
        img=(ImageView) findViewById(R.id.d_img);

        database=FirebaseDatabase.getInstance();
        reference=database.getReference("Deceaceds");

        storage= FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        deceasedKey=getIntent().getStringExtra("DeceasedKey");

        reference.child(deceasedKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name=(String) dataSnapshot.child("DeceasedName").getValue();
                surname=(String) dataSnapshot.child("DeceasedSurname").getValue();
                job=(String) dataSnapshot.child("DeceasedJob").getValue();
                gender=(String) dataSnapshot.child("DeceasedGender").getValue();
                birtdate=(String) dataSnapshot.child("DateOfBirth").getValue();
                deathdate=(String) dataSnapshot.child("DateOfDeath").getValue();
                bio=(String) dataSnapshot.child("DeceasedBiography").getValue();
                DownloadUrl=(String) dataSnapshot.child("ImageUrl").getValue();


                fullname=name+" "+surname;
                tw_fullname.setText(fullname);
                tw_bio.setText(bio);
                tw_bd.setText(birtdate);
                tw_dd.setText(deathdate);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(gender==null || job==null || bio==null)
        {
            tw_gender.setText("Belirtilmemiş");
            tw_job.setText("Belirtilmemiş");
            tw_bio.setText("Belirtilmemiş");
        }
        else{
            tw_gender.setText(gender);
            tw_job.setText(job);
            tw_bio.setText(bio);
        }







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
                        Glide.with(getApplicationContext()).load(url).into(img);
                    }




                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Toast.makeText(getApplicationContext(),"İşlem başarız", Toast.LENGTH_SHORT).show();
                }
            });
        }










       ;


    }
}
