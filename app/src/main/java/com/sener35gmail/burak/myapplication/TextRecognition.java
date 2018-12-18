package com.sener35gmail.burak.myapplication;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sener35gmail.burak.myapplication.Model.Deceased;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

public class TextRecognition extends AppCompatActivity {
    static final int DIALOG_ERROR_CONNECTION = 1;
    EditText et_result;
    ImageView ımg_preview;

    private  static final int CAMERA_REQUEST_CODE=200;
    private  static final int STORAGE_REQUEST_CODE=400;
    private  static final int IMAGE_PICK_GALLERY_CODE=1000;
    private  static final int IMAGE_PICK_CAMERA_CODE=1001;

    String CameraPermission[];
    String storagePermission[];
    String deceasedKey;

    Uri imageuri;


    Query query;
    ActionBar actionBar;

    RecyclerView recyclerView;
    private List<Deceased> deceasedList;
    ArrayList<String> keyList=new ArrayList<>();
    ArrayAdapter<String> arrayAdapter_keyList;
    Context context;

    FirebaseDatabase database;
    DatabaseReference databaseReference;
    TextView arama_sonucları;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognition);

        et_result=(EditText) findViewById(R.id.resultEt);
        ımg_preview=(ImageView)findViewById(R.id.image_ocr);
        database=FirebaseDatabase.getInstance();
        arama_sonucları=(TextView) findViewById(R.id.arama_sonuclari);
        arama_sonucları.setVisibility(View.INVISIBLE);


        CameraPermission=new  String []{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission=new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        actionBar=getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#808B96")));
        actionBar.setDisplayShowTitleEnabled(false);

        context=TextRecognition.this;
        recyclerView=(RecyclerView) findViewById(R.id.recycler_view2);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(linearLayoutManager);

        deceasedList=new ArrayList<Deceased>();
        recyclerView.setHasFixedSize(true);
        final SimpleRecyclerAdapterForDeceased adapterForDeceased=new SimpleRecyclerAdapterForDeceased(deceasedList,new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d("position", "Tıklanan Pozisyon:" + position);
                Deceased deceased=deceasedList.get(position);

            }
        },context);
        recyclerView.setAdapter(adapterForDeceased);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        arrayAdapter_keyList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, keyList);






    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_for_ocr,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if(id==R.id.addImage)
        {

            showImageImportDialog();
        }
        else if(id==R.id.backtoHome)
        {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showImageImportDialog() {

        String [] items={"Kamera","Galeri"};
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("Resim seçiniz");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0)
                {
                    //camera

                    if(!checkCameraPermission())
                    {
                        requestCameraPermission();
                    }
                    else
                    {
                        pickCamera();
                    }
                }
                else if (i==1)
                {
                    //gallery
                    if(!checkStoragePermission())
                    {
                        requestStoragePermission();
                    }
                    else
                    {
                        pickImage();
                    }
                }


            }
        });
        dialog.create().show();
    }

    private void pickImage() {
        Intent ıntent=new Intent(Intent.ACTION_PICK);
        ıntent.setType("image/*");
        startActivityForResult(ıntent,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image to text");
        imageuri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent CameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        CameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageuri);
        startActivityForResult(CameraIntent,IMAGE_PICK_CAMERA_CODE);

    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;

    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,CameraPermission,CAMERA_REQUEST_CODE);

    }

    private boolean checkCameraPermission() {
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         switch (requestCode)
         {
             case CAMERA_REQUEST_CODE:
             if(grantResults.length>0)
             {
                 boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                 boolean writeStorageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                 if(cameraAccepted && writeStorageAccepted)
                 {
                     pickCamera();
                 }
                 else
                 {
                     Toast.makeText(getApplicationContext(),"Hay aksi! \n izin verilmedi.",Toast.LENGTH_LONG).show();
                 }

             }
             break;

             case STORAGE_REQUEST_CODE:
                 if(grantResults.length>0)
                 {
                     boolean writeStorageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                     if( writeStorageAccepted)
                     {
                         pickImage();
                     }
                     else
                     {
                         Toast.makeText(getApplicationContext(),"Hay aksi! \n izin verilmedi.",Toast.LENGTH_LONG).show();
                     }
                 }
                 break;

         }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {

        if(resultCode==RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).start(this);


            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(imageuri).setGuidelines(CropImageView.Guidelines.ON).start(this);
            }
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK)
            {
                Uri resultUri=result.getUri();
                ımg_preview.setImageURI(resultUri);

                BitmapDrawable bitmapDrawable=(BitmapDrawable) ımg_preview.getDrawable();
                Bitmap bitmap=bitmapDrawable.getBitmap();
                TextRecognizer recognizer=new TextRecognizer.Builder(getApplicationContext()).build();

                if(!recognizer.isOperational())
                {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Hata!", Snackbar.LENGTH_LONG).show();
                }
               else if (!isOnline(getApplication())) {
                    showDialog(DIALOG_ERROR_CONNECTION); //displaying the created dialog.
                }
                else
                {
                    Frame frame=new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock>items=recognizer.detect(frame);
                    final StringBuilder sb=new StringBuilder();
                    for(int i=0;i<items.size();i++)
                    {
                        TextBlock myItem=items.valueAt(i);
                        sb.append(myItem.getValue());
                        //sb.append("\n");
                    }
                    et_result.setText(sb.toString());
                    database=FirebaseDatabase.getInstance();
                    databaseReference=database.getReference("Deceaceds");
                    query=databaseReference.orderByChild("DeceasedFullName").equalTo(sb.toString());

                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists())
                            {
                                Toast.makeText(getApplicationContext(),"The deceased does not exist in the system!",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                deceasedList.clear();
                                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                                {
                                    String fullname=(String) snapshot.child("DeceasedFullName").getValue();
                                    if(fullname.toLowerCase().equals(sb.toString().toLowerCase()))
                                    {
                                        String deceasedName=(String) snapshot.child("DeceasedName").getValue();
                                        String deceasedSurname = (String) snapshot.child("DeceasedSurname").getValue();
                                        String deceasedGender = (String) snapshot.child("DeceasedGender").getValue();
                                        String deceasedJob = (String) snapshot.child("DeceasedJob").getValue();
                                        String deceased_dob = (String) snapshot.child("DateOfBirth").getValue();
                                        String deceased_dod = (String) snapshot.child("DateOfDeath").getValue();
                                        String bio = (String) snapshot.child("DeceasedBiography").getValue();
                                         deceasedKey = (String) snapshot.child("DeceasedKey").getValue();
                                        final String DownloadUrl = (String) snapshot.child("ImageUrl").getValue();
                                      //  Toast.makeText(getApplicationContext(),deceasedName+deceasedSurname+deceasedGender+deceasedJob+deceased_dod+deceased_dob+bio,Toast.LENGTH_LONG).show();
                                      /*  Intent intent=new Intent(TextRecognition.this,TextRecognitionResult.class);
                                        intent.putExtra("name",deceasedName);
                                        intent.putExtra("surname",deceasedSurname);
                                        intent.putExtra("gender",deceasedGender);
                                        intent.putExtra("job",deceasedJob);
                                        intent.putExtra("birthdate",deceased_dob);
                                        intent.putExtra("deathdate",deceased_dob);
                                        intent.putExtra("bio",bio);
                                        startActivity(intent);*/

                                        keyList.add(deceasedKey);
                                        SimpleRecyclerAdapterForDeceased adapterForDeceased1=new SimpleRecyclerAdapterForDeceased(deceasedList, new CustomItemClickListener() {
                                            @Override
                                            public void onItemClick(View v, int position) {
                                                Deceased deceased=deceasedList.get(position);
                                                deceasedKey=keyList.get(position);
                                                Intent pass=new Intent(TextRecognition.this,TextRecognitionResult.class);
                                                pass.putExtra("DeceasedKey",deceasedKey);
                                                startActivity(pass);

                                            }
                                        },context);
                                        deceasedList.add(new Deceased("ad soyad: "+deceasedName+" ",deceasedSurname,"Doğum tarihi: "+String.valueOf(deceased_dob)+" ","Ölüm Tarihi: "+String.valueOf(deceased_dod),DownloadUrl));
                                        recyclerView.setHasFixedSize(true);
                                        recyclerView.setAdapter(adapterForDeceased1);
                                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                                        adapterForDeceased1.notifyDataSetChanged();
                                        arama_sonucları.setVisibility(View.VISIBLE);





                                       // names.add("Deceased Full Name:"+ "\t" + fullname + "\n" + "\n" + "\nDeceased Gender:" + deceasedGender + "\n" + "\nDeceased Job:" + deceasedJob + "\n" + "\nDate Of Birth:" + deceased_dob + "\n" + "\nDate Of Dead:" + deceased_dod + "\n" + "\nDeceased Biography:" + bio+"\n");
                                    }
                                    else if(fullname.toUpperCase().equals(sb.toString().toUpperCase()))
                                    {
                                        String deceasedName=(String) snapshot.child("DeceasedName").getValue();
                                        String deceasedSurname = (String) snapshot.child("DeceasedSurname").getValue();
                                        String deceasedGender = (String) snapshot.child("DeceasedGender").getValue();
                                        String deceasedJob = (String) snapshot.child("DeceasedJob").getValue();
                                        String deceased_dob = (String) snapshot.child("DateOfBirth").getValue();
                                        String deceased_dod = (String) snapshot.child("DateOfDeath").getValue();
                                        String bio = (String) snapshot.child("DeceasedBiography").getValue();
                                        String url=(String) snapshot.child("ImageUrl").getValue();
                                       // Toast.makeText(getApplicationContext(),deceasedName+deceasedSurname+deceasedGender+deceasedJob+deceased_dod+deceased_dob+bio,Toast.LENGTH_LONG).show();
                                        //names.add("Deceased Full Name:"+ "\t" + fullname + "\n" + "\n" + "\nDeceased Gender:" + deceasedGender + "\n" + "\nDeceased Job:" + deceasedJob + "\n" + "\nDate Of Birth:" + deceased_dob + "\n" + "\nDate Of Dead:" + deceased_dod + "\n" + "\nDeceased Biography:" + bio+"\n");

                                        Intent intent=new Intent(TextRecognition.this,TextRecognitionResult.class);
                                        intent.putExtra("name",deceasedName);
                                        intent.putExtra("surname",deceasedSurname);
                                        intent.putExtra("gender",deceasedGender);
                                        intent.putExtra("job",deceasedJob);
                                        intent.putExtra("birthdate",deceased_dob);
                                        intent.putExtra("deathdate",deceased_dob);
                                        intent.putExtra("bio",bio);
                                        intent.putExtra("url",url);
                                        startActivity(intent);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(),"Something went wrong..",Toast.LENGTH_LONG).show();

                        }
                    });


                   // Toast.makeText(getApplicationContext(),sb.toString(),Toast.LENGTH_LONG).show();
                }
            }
            else if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error=result.getError();
                Snackbar.make(getWindow().getDecorView().getRootView(), "Hata!", Snackbar.LENGTH_LONG).show();

            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);


    }

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
