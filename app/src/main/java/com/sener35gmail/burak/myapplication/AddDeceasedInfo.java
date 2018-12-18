package com.sener35gmail.burak.myapplication;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddDeceasedInfo extends AppCompatActivity implements LocationListener {





    private ImageView img;

    TextView Deceased_Name;
    TextView Deceased_Surname;

    EditText Ed_Deceased_Name;
    EditText Ed_Deceased_Surname;
    EditText Ed_Deceased_biography;
    EditText Ed_Deceased_job;
    TextView Deceased_Gender;
    String DeceasedName;
    String DeceasedSurname;
    String gender;
    String biography;
    String DeceasedJob;
    CheckBox Deceased_male;
    CheckBox Deceased_female;

     TextView BirthDate;
    TextView DeathDate;
    TextView Deceased_Job;

     Button ClickDeathDate;
     Button ClickBirthDate;
     Button save_ınfo;

     ImageButton btn_back;

     Button checkInGrave;



    FirebaseDatabase database;
    DatabaseReference  deceaseds;
    FirebaseAuth auth;
    FirebaseStorage storage;
    StorageReference storageReference;
    private int year;
    private int year2;
    private int month;
    private int month2;
    private int day;
    private int day2;
    private String dmy;
    private String dmy2;


    static final int DATE_DIALOG_ID = 1;
    static final int DATE_DIALOG_ID2 = 2;
    int cur = 0;


    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private final int PICK_VIDEO_REQUEST=72;



    TextView locationText;
    LocationManager locationManager;
    private String lat;
    private  String lng;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deceased_info);


        locationText = (TextView)findViewById(R.id.locationText);


        Deceased_Name = (TextView) findViewById(R.id.DeceasedName);
        Deceased_Gender = (TextView) findViewById(R.id.Deceased_gender);
        Deceased_Job=(TextView) findViewById(R.id.deceased_job);
        Deceased_Surname = (TextView) findViewById(R.id.Deceased_Surname);
        Ed_Deceased_Name = (EditText) findViewById(R.id.edit_DeceasedName);
        Ed_Deceased_Surname = (EditText) findViewById(R.id.edit_Deceased_Surname);
        Ed_Deceased_job=(EditText) findViewById(R.id.edit_deceased_job);
        Deceased_male = (CheckBox) findViewById(R.id.Deceased_male);
        Deceased_female = (CheckBox) findViewById(R.id.Deceased_female);
        Ed_Deceased_biography=(EditText) findViewById(R.id.MultiBiography);

        save_ınfo=(Button) findViewById(R.id.saveInfo);
        btn_back=(ImageButton) findViewById(R.id.backtoHomePage);
        checkInGrave=(Button) findViewById(R.id.checkIn_grave);
        database=FirebaseDatabase.getInstance();
        deceaseds=database.getReference();
        auth= FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        setCurrentDateOnView();
        addListenerOnButton();
        img = (ImageView)findViewById(R.id.imageView);




        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }

        checkInGrave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOnline(getApplication())) {
                    //displaying the created dialog.
                    openDialog();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Konum bilgisi alınıyor Lütfen bekleyin ve bulunduğunuz konumda sabit kalın,Bu işlem 10 saniye kadar sürebilir.",Toast.LENGTH_LONG).show();
                    getLocation();
                }

            }
        });


             img.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     if (!isOnline(getApplication())) {
                         //displaying the created dialog.
                         openDialog();
                     }
                     else{chooseImage();}
                 }
             });


                dmy=String.valueOf(day)+"/"+String.valueOf(month+1)+"/"+String.valueOf(year);
                dmy2=String.valueOf(day2)+"/"+String.valueOf(month2+1)+"/"+String.valueOf(year2);

                save_ınfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DeceasedName = Ed_Deceased_Name.getText().toString();
                        DeceasedSurname = Ed_Deceased_Surname.getText().toString();
                        DeceasedJob=Ed_Deceased_job.getText().toString();
                        biography=Ed_Deceased_biography.getText().toString().trim();
                        if (!isOnline(getApplication())) {
                             //displaying the created dialog.
                            openDialog();
                        }

                        else{
                            if( DeceasedName.equals(""))
                            {
                                Toast.makeText(getBaseContext(),"Lütfen bir ad giriniz!",Toast.LENGTH_SHORT).show();

                            }
                            else if(DeceasedSurname.equals(""))
                            {
                                Toast.makeText(getApplicationContext(),"Lütfen bir soyad giriniz!",Toast.LENGTH_SHORT).show();
                            }
                            else if(DeceasedJob.equals(""))
                            {
                                DeceasedJob="not specified";
                                Toast.makeText(getApplicationContext(),"Lütfen bir merhumun mesleğini belirtiniz!",Toast.LENGTH_LONG).show();
                            }
                            else if(TextUtils.isEmpty(dmy) && TextUtils.isEmpty(dmy2))
                            {
                                Toast.makeText(getApplicationContext(),"Lütfen merhumun doğum ve ölüm tarihini  belirtiniz!",Toast.LENGTH_LONG).show();

                            }

                            else
                            { deceaseds.addListenerForSingleValueEvent(new ValueEventListener() {
                                @SuppressLint("ResourceAsColor")
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    final String key = database.getReference().push().getKey();
                                    String uid=(String) auth.getCurrentUser().getUid();
                                    String fullName=DeceasedName+ " " +DeceasedSurname;

                                    deceaseds=FirebaseDatabase.getInstance().getReference("Deceaceds").child(key);
                                    final Map<String, String> newMap = new HashMap<String, String>();
                                    newMap.put("DeceasedName",DeceasedName);
                                    newMap.put("DeceasedSurname",DeceasedSurname);
                                    newMap.put("DeceasedFullName",fullName);
                                    newMap.put("DateOfBirth",dmy);
                                    newMap.put("DateOfDeath",dmy2);
                                    newMap.put("DeceasedJob",DeceasedJob);
                                    newMap.put("DeceasedGender",gender);
                                    newMap.put("DeceasedBiography",biography);
                                    newMap.put("AuthenticatedUserOfDeceased",uid);
                                    newMap.put("DeceasedKey",key);
                                    //newMap.put("Latitude", String.valueOf(Double.valueOf(latitude)));
                                    //newMap.put("Longitude", String.valueOf(Double.valueOf(longitude)));
                                    newMap.put("Latitude",lat);
                                    newMap.put("Longitude",lng);


                                    deceaseds.setValue(newMap);

                                    if(filePath != null)
                                    {
                                        StorageReference ref = storageReference.child("images").child(key);

                                        ref.putFile(filePath)
                                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                        Uri downloadUri= taskSnapshot.getDownloadUrl();
                                                        newMap.put("ImageUrl",downloadUri.toString());
                                                        deceaseds.setValue(newMap);

                                                        Toast.makeText(AddDeceasedInfo.this, "Yüklendi", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        //progressDialog.dismiss();
                                                        Toast.makeText(AddDeceasedInfo.this, "İşlem başarısız oldu! "+e.getMessage(), Toast.LENGTH_SHORT).show();
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


                                    Snackbar.make(getWindow().getDecorView().getRootView(), "Merhum  başarılı bir şekilde kayıt edildi!", Snackbar.LENGTH_LONG).setActionTextColor(R.color.grey).show();

                                    finish();


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                                Intent newIntent=new Intent(getApplicationContext(),HomePage.class);
                                startActivity(newIntent);}
                        }
                    }
                });
         Deceased_male.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(Deceased_male.isChecked() && Deceased_female.isChecked())
        {save_ınfo.setEnabled(false);}
        else if(Deceased_male.isChecked())
        {
            gender="Erkek";
        }
        else
        {save_ınfo.setEnabled(true);}
    }
});
        Deceased_female.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(Deceased_female.isChecked() && Deceased_male.isChecked())
        {save_ınfo.setEnabled(false);}
        else if (Deceased_female.isChecked())
        {
            gender="Kadın";
        }
        else
        {
            save_ınfo.setEnabled(true);
        }
    }
});

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    private AlertDialog openDialog() {
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
        errorAlert.show();
        getWindow().setBackgroundDrawableResource(R.color.white);
        return errorAlert;
    }


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
                img.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else if(resultCode==RESULT_CANCELED)
        {
           // Toast.makeText(getApplicationContext(),"İşlem iptal edildi",Toast.LENGTH_SHORT).show();
            Snackbar.make(getWindow().getDecorView().getRootView(), "İşlemi iptal edildi.", Snackbar.LENGTH_LONG).show();
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
                @SuppressLint("ResourceAsColor")
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads

                    Toast.makeText(AddDeceasedInfo.this, "Yükleme Başarısız oldu!", Toast.LENGTH_SHORT).show();
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Yükleme Başarısız oldu!", Snackbar.LENGTH_LONG).setActionTextColor(R.color.red).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                    Snackbar.make(getWindow().getDecorView().getRootView(), "Yükleme başarıyla gerçekleştirildi!", Snackbar.LENGTH_LONG).setActionTextColor(R.color.grey).show();

                }
            });
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);


    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }









    public void setCurrentDateOnView() {
        BirthDate = (TextView) findViewById(R.id.birthdate);
        DeathDate = (TextView) findViewById(R.id.deathdate);


        final Calendar c = Calendar.getInstance();
        final Calendar c2=Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        year2=c2.get(Calendar.YEAR);
        month2=c2.get(Calendar.MONTH);
        day2=c2.get(Calendar.DAY_OF_MONTH);


        BirthDate.setText(new StringBuilder()

                .append(month + 1).append("-").append(day).append("-")
                .append(year).append(" "));

        //DeathDate.setText(BirthDate.getText().toString());
        DeathDate.setText(new StringBuilder()
                .append(month2 + 1).append("-").append(day2).append("-")
                .append(year2).append(" "));

    }
    public void addListenerOnButton() {

        ClickBirthDate = (Button) findViewById(R.id.DateofBirth);

        ClickBirthDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showDialog(DATE_DIALOG_ID);

            }

        });
        ClickDeathDate = (Button) findViewById(R.id.DateofDeath);

        ClickDeathDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showDialog(DATE_DIALOG_ID2);

            }

        });

    }
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {

            case DATE_DIALOG_ID:
                System.out.println("onCreateDialog  : " + id);
                cur = DATE_DIALOG_ID;

                return new DatePickerDialog(this, datePickerListener, year, month,
                        day);
            case DATE_DIALOG_ID2:
                cur = DATE_DIALOG_ID2;
                System.out.println("onCreateDialog2  : " + id);

                return new DatePickerDialog(this, datePickerListener, year, month,
                        day);

        }
        return null;
    }
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {


        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {



            if(cur == DATE_DIALOG_ID){
                year = selectedYear;
                month = selectedMonth;
                day = selectedDay;
                BirthDate.setText( new StringBuilder().append(month + 1)
                        .append("-").append(day).append("-").append(year)
                        .append(" "));
            }
            else{
                year2=selectedYear;
                month2=selectedMonth;
                day2=selectedDay;
                DeathDate.setText( new StringBuilder().append(month2 + 1)
                        .append("-").append(day2).append("-").append(year2)
                        .append(" "));
            }

        }
    };


    //Get current location GPS

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 5, (LocationListener) this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLocationChanged(Location location) {
        locationText.setText("Latitude: " + location.getLatitude() + " "+" Longitude: " + location.getLongitude());
         lat=String.valueOf(location.getLatitude());
       lng=String.valueOf(location.getLongitude());

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            locationText.setText(locationText.getText() + " "+addresses.get(0).getAddressLine(0)+", "+
                    addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(3));
        }
        catch(Exception e)
        {

        }


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(AddDeceasedInfo.this, "Lütfen gps ve internet bağlantınızı kontrol edin.", Toast.LENGTH_SHORT).show();
    }


    //Check the internet connection
    public boolean isOnline(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        if (ni != null && ni.isConnected())
            return true;
        else
            return false;
    }







}





