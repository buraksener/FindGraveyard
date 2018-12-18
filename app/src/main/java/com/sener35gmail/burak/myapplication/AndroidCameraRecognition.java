package com.sener35gmail.burak.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.IOException;
import java.util.ArrayList;

public class AndroidCameraRecognition extends AppCompatActivity {
    SurfaceView cameraView;
    TextView textView;
    CameraSource cameraSource;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    Query query;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> names = new ArrayList<>();
    ListView mList;
    Button arat;
    final int RequestCameraPermissionID = 1001;

    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            break;
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_camera_recognition);

        cameraView = (SurfaceView) findViewById(R.id.surface_view);
        textView = (TextView) findViewById(R.id.text_view);
        arat=(Button) findViewById(R.id.ARA);
        arat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Intent intent=new Intent(AndroidCameraRecognition.this,ExampleActivity.class);
                //startActivity(intent);
            }
        });

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Log.w("MainActivity", "Detector dependencies are not yet available");
        } else {

            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();

            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {

                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(AndroidCameraRecognition.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    RequestCameraPermissionID);
                            return;
                        }
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    cameraSource.stop();
                }
            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {

                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if(items.size() != 0)
                    {
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                final StringBuilder stringBuilder = new StringBuilder();
                                for(int i =0;i<items.size();++i)
                                {
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }


                                textView.setText(stringBuilder.toString());


                               /* query=databaseReference.orderByChild("DeceasedFullName").equalTo(stringBuilder.toString().toLowerCase());
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(!dataSnapshot.exists())
                                        {
                                            Toast.makeText(getApplicationContext(),"The deceased does not exist in the system!",Toast.LENGTH_LONG).show();
                                        }
                                        arrayAdapter.clear();

                                        for(DataSnapshot snapshot:dataSnapshot.getChildren())
                                        {



                                            String fullname=(String) snapshot.child("DeceasedFullName").getValue();


                                            assert fullname != null;
                                            if(fullname.toLowerCase().equals(stringBuilder.toString().toLowerCase()))
                                            {
                                                String deceasedName=(String) snapshot.child("DeceasedName").getValue();
                                                String deceasedSurname = (String) snapshot.child("DeceasedSurname").getValue();
                                                String deceasedGender = (String) snapshot.child("DeceasedGender").getValue();
                                                String deceasedJob = (String) snapshot.child("DeceasedJob").getValue();
                                                String deceased_dob = (String) snapshot.child("DateOfBirth").getValue();
                                                String deceased_dod = (String) snapshot.child("DateOfDeath").getValue();
                                                String bio = (String) snapshot.child("DeceasedBiography").getValue();
                                                names.add("Deceased Full Name:"+ "\t" + fullname + "\n" + "\n" + "\nDeceased Gender:" + deceasedGender + "\n" + "\nDeceased Job:" + deceasedJob + "\n" + "\nDate Of Birth:" + deceased_dob + "\n" + "\nDate Of Dead:" + deceased_dod + "\n" + "\nDeceased Biography:" + bio+"\n");
                                            }
                                            else if(fullname.toUpperCase().equals(stringBuilder.toString().toUpperCase()))
                                            {
                                                String deceasedGender = (String) snapshot.child("DeceasedGender").getValue();
                                                String deceasedJob = (String) snapshot.child("DeceasedJob").getValue();
                                                String deceased_dob = (String) snapshot.child("DateOfBirth").getValue();
                                                String deceased_dod = (String) snapshot.child("DateOfDeath").getValue();
                                                String bio = (String) snapshot.child("DeceasedBiography").getValue();
                                                names.add("Deceased Full Name:"+ "\t" + fullname + "\n" + "\n" + "\nDeceased Gender:" + deceasedGender + "\n" + "\nDeceased Job:" + deceasedJob + "\n" + "\nDate Of Birth:" + deceased_dob + "\n" + "\nDate Of Dead:" + deceased_dod + "\n" + "\nDeceased Biography:" + bio+"\n");
                                            }



                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                arrayAdapter.addAll(names);
                                mList.setAdapter(arrayAdapter);*/

                            }
                        });
                    }
                }
            });
        }
    }







}
