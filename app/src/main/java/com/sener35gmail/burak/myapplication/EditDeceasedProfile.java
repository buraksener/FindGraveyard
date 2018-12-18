package com.sener35gmail.burak.myapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditDeceasedProfile extends AppCompatActivity implements LocationListener {
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;

    Button delete_account;
    Button Update_Location;
    ImageButton edit;

    ImageButton accept;
    ImageButton reject;

    ImageView deceased_Image;

    TextView name_surname;
    TextView birthdate;
    TextView deathdate;
    TextView sex;
    TextView job;
    TextView bio;

    TextView photo_change;

    EditText editText_name_surname;
    EditText editText_birthdate;
    EditText editText_deathdate;
    EditText editText_sex;
    EditText editText_job;
    EditText editText_bio;

    FirebaseDatabase database;
    DatabaseReference reference;
    DatabaseReference reference1;
    DatabaseReference reference2;
    DatabaseReference reference3;
    DatabaseReference reference4;
    DatabaseReference reference5;
    DatabaseReference reference6;

    String deceasedFullname;
    String deceasedGender ;
    String deceasedJob ;
    String deceased_dob ;
    String deceased_dod ;
    String biografi ;
    String DownloadUrl ;
    String latitude;
    String longitude;
    String AutUser;
    String deceasedName;
    String deceasedSurname;
    String LocationAdress;
    private String  key;
    String lat;
    String lng;


    private Calendar startDate;
    private Calendar endDate;
    static final int DATE_DIALOG_ID = 0;
    private Calendar activeDate;


    FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference ref;

    LocationManager locationManager;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_deceased_profile);


        delete_account=(Button) findViewById(R.id.delete_deceased);
        Update_Location=(Button) findViewById(R.id.UpdateLocation);
        edit=(ImageButton) findViewById(R.id.edit);
        accept=(ImageButton) findViewById(R.id.accept);
        reject=(ImageButton) findViewById(R.id.reject);

        deceased_Image=(ImageView) findViewById(R.id.Edit_Deceased_imageView);

        name_surname=(TextView) findViewById(R.id.edit_deceased_name);
        birthdate=(TextView) findViewById(R.id.edit_deceased_birthdate);
        deathdate=(TextView) findViewById(R.id.edit_deceased_deathdate);
        sex=(TextView) findViewById(R.id.edit_deceased_gender);
        job=(TextView) findViewById(R.id.edit_deceased_job);
        bio=(TextView) findViewById(R.id.textView_bio);

        editText_name_surname=(EditText) findViewById(R.id.editText_decesed_name_surname);
        editText_birthdate=(EditText) findViewById(R.id.editText_deceased_birthdate);
        editText_deathdate=(EditText) findViewById(R.id.editText_deceased_deathdate);
        editText_sex=(EditText) findViewById(R.id.editText_deceased_gender);
        editText_job=(EditText) findViewById(R.id.editText_deceased_job);
        editText_bio=(EditText) findViewById(R.id.editText_deceased_bio);

        photo_change=(TextView) findViewById(R.id.change_photo);

        accept.setVisibility(View.GONE);
        reject.setVisibility(View.GONE);
        photo_change.setVisibility(View.GONE);

         /* get the current date */
        startDate = Calendar.getInstance();

         /* add a click listener to the button   */
        editText_birthdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDateDialog(editText_birthdate, startDate);
            }
        });

        endDate = Calendar.getInstance();

        /* add a click listener to the button   */
        editText_deathdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDateDialog(editText_deathdate, endDate);
            }
        });

        /* display the current date (this method is below)  */
        updateDisplay(editText_birthdate, startDate);
        updateDisplay(editText_deathdate, endDate);





        /*TextWatcher tw=new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().equals(current)) {
                    String clean = charSequence.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");


                    int cl = clean.length();
                    int sel = cl;
                    for (int j = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));


                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon-1);
                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;

                    editText_birthdate.setText(current);

                    editText_birthdate.setSelection(sel < current.length() ? sel : current.length());

                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        editText_birthdate.addTextChangedListener(tw);

        TextWatcher tw2=new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().equals(current)) {
                    String clean = charSequence.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");


                    int cl = clean.length();
                    int sel = cl;
                    for (int j = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon-1);
                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;

                    editText_deathdate.setText(current);

                    editText_deathdate.setSelection(sel < current.length() ? sel : current.length());

                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        editText_deathdate.addTextChangedListener(tw2);*/







        Intent intent=getIntent();

        key = intent.getStringExtra("deceased_key");


        if (key != null) {
            reference= FirebaseDatabase.getInstance().getReference("Deceaceds").child(key);
        }

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                 deceasedFullname=(String) dataSnapshot.child("DeceasedFullName").getValue();
                 deceasedName=(String) dataSnapshot.child("DeceasedName").getValue();
                 deceasedSurname=(String) dataSnapshot.child("DeceasedSurname").getValue();
                     deceasedGender = (String) dataSnapshot.child("DeceasedGender").getValue();
                     deceasedJob = (String) dataSnapshot.child("DeceasedJob").getValue();
                     deceased_dob = (String) dataSnapshot.child("DateOfBirth").getValue();
                     deceased_dod = (String) dataSnapshot.child("DateOfDeath").getValue();
                     biografi = (String) dataSnapshot.child("DeceasedBiography").getValue();
                     DownloadUrl = (String) dataSnapshot.child("ImageUrl").getValue();
                     AutUser=(String) dataSnapshot.child("AuthenticatedUserOfDeceased").getValue();
                    latitude=(String) dataSnapshot.child("Latitude").getValue();
                    longitude=(String) dataSnapshot.child("Longitude").getValue();

                    editText_name_surname.setText(deceasedFullname);
                    editText_birthdate.setText(deceased_dob);
                    editText_deathdate.setText(deceased_dod);
                    editText_sex.setText(deceasedGender);
                    editText_job.setText(deceasedJob);
                    editText_bio.setText(biografi);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        editText_name_surname.setFocusable(false);
        editText_birthdate.setFocusable(false);
        editText_deathdate.setFocusable(false);
        editText_bio.setFocusable(false);
        editText_sex.setFocusable(false);
        editText_job.setFocusable(false);

        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        try{
            ref = storageReference.child("images").child(key);
        }
        catch (NullPointerException e)
        {

        }

        if(key!=null)
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


                    Glide.with(getApplicationContext()).load(url).into(deceased_Image);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Toast.makeText(getApplicationContext(),"İşlem başarız", Toast.LENGTH_SHORT).show();
                }
            });
        }



        photo_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                chooseImage();
                //loadImage();


            }
        });

        Update_Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Getlocation();

            }
        });




        //edit info codes starts here\\
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText_name_surname.setTextColor(Color.RED);
                editText_job.setTextColor(Color.RED);
                editText_bio.setTextColor(Color.RED);
                editText_birthdate.setTextColor(Color.RED);
                editText_deathdate.setTextColor(Color.RED);
                editText_sex.setTextColor(Color.RED);

                accept.setVisibility(View.VISIBLE);
                reject.setVisibility(View.VISIBLE);
                photo_change.setVisibility(View.VISIBLE);



                editText_name_surname.setFocusableInTouchMode(true);
                editText_birthdate.setFocusableInTouchMode(true);
                editText_deathdate.setFocusableInTouchMode(true);
                editText_bio.setFocusableInTouchMode(true);
                editText_sex.setFocusableInTouchMode(true);
                editText_job.setFocusableInTouchMode(true);

               view = findViewById(android.R.id.content);
                final Snackbar snackbar = Snackbar.make(view, " Düzenleme modu: aktif", Snackbar.LENGTH_LONG);
                snackbar.show();
                View view2=snackbar.getView();

                TextView textView = (TextView) view2.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.parseColor("#5DADE2"));




                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                       // openDialog();
                        save();
                        view=findViewById(android.R.id.content);
                        Snackbar snackbar1= Snackbar.make(view,"Değişiklikler kaydedildi!",Snackbar.LENGTH_LONG);
                        snackbar1.show();
                        view=snackbar1.getView();
                        TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.parseColor("#5DADE2"));

                        Intent ıntent=new Intent(EditDeceasedProfile.this,Profile.class);
                        startActivity(ıntent);
                        finish();




                    }
                });
                reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view=findViewById(android.R.id.content);
                        Snackbar snackbar1=Snackbar.make(view,"Düzenleme iptal edildi.",Snackbar.LENGTH_LONG);
                        snackbar1.show();

                        View view1=snackbar1.getView();

                        TextView textView = (TextView) view1.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.RED);
                        Intent ıntent=new Intent(EditDeceasedProfile.this,Profile.class);
                        startActivity(ıntent);
                        finish();
                    }
                });

            }
        });

        delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               openDialog();



            }
        });


    }

    private void updateDisplay(TextView dateDisplay, Calendar date) {
        dateDisplay.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(date.get(Calendar.DAY_OF_MONTH)).append("/")
                        .append(date.get(Calendar.MONTH) + 1).append("/")
                        .append(date.get(Calendar.YEAR)).append(" "));

    }

    public void showDateDialog(TextView dateDisplay, Calendar date) {
        photo_change = dateDisplay;
        activeDate = date;
        showDialog(DATE_DIALOG_ID);
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            activeDate.set(Calendar.YEAR, year);
            activeDate.set(Calendar.MONTH, monthOfYear);
            activeDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDisplay(photo_change, activeDate);
            unregisterDateDisplay();
        }
    };
    private void unregisterDateDisplay() {
        photo_change = null;
        activeDate = null;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, dateSetListener, activeDate.get(Calendar.YEAR), activeDate.get(Calendar.MONTH), activeDate.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id) {
            case DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(activeDate.get(Calendar.YEAR), activeDate.get(Calendar.MONTH), activeDate.get(Calendar.DAY_OF_MONTH));
                break;
        }
    }









    private void Getlocation() {
        Toast.makeText(getApplicationContext(),"Konum bilgisi alınıyor Lütfen bekleyin ve bulunduğunuz konumda sabit kalın,Bu işlem 10 saniye kadar sürebilir.",Toast.LENGTH_LONG).show();



        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 5, (LocationListener) this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(Location location) {


        LocationAdress="Latitude: " + location.getLatitude() + " "+" Longitude: " + location.getLongitude();
        lat=String.valueOf(location.getLatitude());
        lng=String.valueOf(location.getLongitude());

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            LocationAdress=lat+" "+lng + " "+addresses.get(0).getAddressLine(0)+", "+
                    addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(3);

        }
        catch(Exception e)
        {

        }


        //Alert dialog codes below
        AlertDialog.Builder builder2 = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder2.setMessage("Uyarı\n"+LocationAdress+" konumu bulundu.Kayıt edilsin mi?");
        builder2.setCancelable(true);
        builder2.setPositiveButton("Kaydet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                reference= FirebaseDatabase.getInstance().getReference("Deceaceds").child(key).child("Latitude");
                reference.setValue(lat);
                reference= FirebaseDatabase.getInstance().getReference("Deceaceds").child(key).child("Longitude");
                reference.setValue(lng);


                View view = findViewById(android.R.id.content);
                final Snackbar snackbar = Snackbar.make(view, " Düzenleme modu: aktif", Snackbar.LENGTH_LONG);
                snackbar.show();
                View view2=snackbar.getView();

                TextView textView = (TextView) view2.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.parseColor("#5DADE2"));

            }
        });
        builder2.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

            }
        });

        AlertDialog alertDialog2=builder2.create();
        alertDialog2.show();

        Button positiveButton = alertDialog2.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = alertDialog2.getButton(AlertDialog.BUTTON_NEGATIVE);


        negativeButton.setTextColor(Color.parseColor("#FF0B8B42"));
        negativeButton.setBackgroundColor(Color.parseColor("#FFE1FCEA"));

        positiveButton.setTextColor(Color.parseColor("#FFFF0400"));
        positiveButton.setBackgroundColor(Color.parseColor("#FFFCB9B7"));

        getWindow().setBackgroundDrawableResource(R.color.white);


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }
    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(EditDeceasedProfile.this, "Konum bulunamıyor! \nLütfen gps ve internet bağlantınızı kontrol edin.", Toast.LENGTH_SHORT).show();
    }






   /* private void loadImage() {
        if(filePath != null)
        {
            StorageReference ref = storageReference.child("images").child(key);

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Uri downloadUri= taskSnapshot.getDownloadUrl();
                            // newMap.put("ImageUrl",downloadUri.toString());

                            // reference.setValue(downloadUri.toString());
                            reference6=FirebaseDatabase.getInstance().getReference("Deceaceds").child(key).child("ImageUrl");
                            DownloadUrl=downloadUri.toString();
                            reference6.setValue(DownloadUrl);

                            Toast.makeText(EditDeceasedProfile.this, "Yüklendi", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //progressDialog.dismiss();
                            Toast.makeText(EditDeceasedProfile.this, "İşlem başarısız oldu! "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                        }
                    });
        }





    }*/


    //Save current info after changes on profile \\

    private void save() {
        final String fullname=editText_name_surname.getText().toString();
        final  String birthdate=editText_birthdate.getText().toString();
        final String deatgdate=editText_deathdate.getText().toString();
        final String gender=editText_sex.getText().toString();
        final  String deceasedBio=editText_bio.getText().toString();
        final String job=editText_job.getText().toString();


        reference= FirebaseDatabase.getInstance().getReference("Deceaceds").child(key).child("DeceasedFullName");
        reference.setValue(fullname);
        reference1=FirebaseDatabase.getInstance().getReference("Deceaceds").child(key).child("DeceasedGender");
        reference1.setValue(gender);
        reference2=FirebaseDatabase.getInstance().getReference("Deceaceds").child(key).child("DeceasedJob");
        reference2.setValue(job);
        reference3=FirebaseDatabase.getInstance().getReference("Deceaceds").child(key).child("DateOfBirth");
        reference3.setValue(birthdate);
        reference4=FirebaseDatabase.getInstance().getReference("Deceaceds").child(key).child("DateOfDeath");
        reference4.setValue(deatgdate);
        reference5=FirebaseDatabase.getInstance().getReference("Deceaceds").child(key).child("DeceasedBiography");
        reference5.setValue(deceasedBio);


         if(filePath != null)
        {
            StorageReference ref = storageReference.child("images").child(key);

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Uri downloadUri= taskSnapshot.getDownloadUrl();
                            // newMap.put("ImageUrl",downloadUri.toString());

                            // reference.setValue(downloadUri.toString());
                            reference6=FirebaseDatabase.getInstance().getReference("Deceaceds").child(key).child("ImageUrl");
                            reference6.setValue(downloadUri.toString());

                            Toast.makeText(EditDeceasedProfile.this, "Yüklendi", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //progressDialog.dismiss();
                            Toast.makeText(EditDeceasedProfile.this, "İşlem başarısız oldu! "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                        }
                    });
        }
        else
        {
            accept.setVisibility(View.INVISIBLE);
        }
        View view=findViewById(android.R.id.content);
        Snackbar snackbar= Snackbar.make(view,"Değişiklikler kayıt edildi!",Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.BLUE);

        View view1=snackbar.getView();
        TextView textView = (TextView) view1.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.GREEN);

        snackbar.show();

    }




    private void openDialog()
    {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder2.setMessage("Hesabın silinmesini onaylıyor musunuz?");
        builder2.setCancelable(true);
        builder2.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                reference= FirebaseDatabase.getInstance().getReference("Deceaceds").child(key);
                reference.removeValue();
                Intent back=new Intent(getApplicationContext(),Profile.class);
                startActivity(back);
                finish();
            }
        });
        builder2.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

            }
        });

        AlertDialog alertDialog2=builder2.create();
        alertDialog2.show();

        Button positiveButton = alertDialog2.getButton(AlertDialog.BUTTON_NEGATIVE);
        Button negativeButton = alertDialog2.getButton(AlertDialog.BUTTON_POSITIVE);


        negativeButton.setTextColor(Color.parseColor("#FFFF0400"));
        positiveButton.setBackgroundColor(Color.parseColor("#FFE1FCEA"));

        positiveButton.setTextColor(Color.parseColor("#FF0B8B42"));
        negativeButton.setBackgroundColor(Color.parseColor("#FFFCB9B7"));

        getWindow().setBackgroundDrawableResource(R.color.white);

    }







    //chose image codes here\\

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                deceased_Image.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else if(resultCode==RESULT_CANCELED)
        {
            Toast.makeText(getApplicationContext(),"İşlem iptal edildi",Toast.LENGTH_SHORT).show();
        }
        else if(resultCode==RESULT_OK)
        {
            // Create a storage reference from our app
            //String uid=(String) auth.getCurrentUser().getUid();
            String deceasedKey=FirebaseDatabase.getInstance().getReference().getKey();
            assert data != null;
            Uri uri = data.getData();
            StorageReference riversRef = storageReference.child("videos"+deceasedKey);
            assert uri != null;
            UploadTask uploadTask = riversRef.putFile(uri);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads

                    Toast.makeText(EditDeceasedProfile.this, "Yükleme Başarısız oldu!", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                    Toast.makeText(EditDeceasedProfile.this, "Yükleme başarıyla gerçekleştirildi!", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

}
