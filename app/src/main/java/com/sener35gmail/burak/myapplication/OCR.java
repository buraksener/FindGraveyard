package com.sener35gmail.burak.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OCR extends AppCompatActivity {

    private static final int REQUEST_GALLERY = 0;
    private static final int REQUEST_CAMERA = 1;

    private static final String TAG = MainActivity.class.getSimpleName();

    private Uri imageUri;
    private TextView detectedTextView;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    Query query;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> names = new ArrayList<>();
    ListView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Deceaceds");
        mList = (ListView) findViewById(R.id.ListViewResult);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
        //mList.setVisibility(View.GONE);


        findViewById(R.id.choose_from_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_GALLERY);
            }
        });

        findViewById(R.id.take_a_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = System.currentTimeMillis() + ".jpg";

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, filename);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        });

        detectedTextView = (TextView) findViewById(R.id.detected_text);
        detectedTextView.setMovementMethod(new ScrollingMovementMethod());
    }





        private void inspectFromBitmap (Bitmap bitmap){
        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
        try {
            if (!textRecognizer.isOperational()) {
                new AlertDialog.
                        Builder(this).
                        setMessage("Text recognizer could not be set up on your device").show();
                return;
            }

            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> origTextBlocks = textRecognizer.detect(frame);
            List<TextBlock> textBlocks = new ArrayList<>();
            for (int i = 0; i < origTextBlocks.size(); i++) {
                TextBlock textBlock = origTextBlocks.valueAt(i);
                textBlocks.add(textBlock);
            }
            Collections.sort(textBlocks, new Comparator<TextBlock>() {
                @Override
                public int compare(TextBlock o1, TextBlock o2) {
                    int diffOfTops = o1.getBoundingBox().top - o2.getBoundingBox().top;
                    int diffOfLefts = o1.getBoundingBox().left - o2.getBoundingBox().left;
                    if (diffOfTops != 0) {
                        return diffOfTops;
                    }
                    return diffOfLefts;
                }
            });


            final StringBuilder detectedText = new StringBuilder();
            for (TextBlock textBlock : textBlocks) {
                if (textBlock != null && textBlock.getValue() != null) {
                    detectedText.append(textBlock.getValue());
                    detectedText.append("\n");
                                query = databaseReference.orderByChild("DeceasedFullName").equalTo(detectedText.toString());
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.exists()) {
                                            Toast.makeText(getApplicationContext(), "The deceased does not exist in the system!", Toast.LENGTH_LONG).show();
                                        }
                                        arrayAdapter.clear();

                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            String fullname = (String) snapshot.child("DeceasedFullName").getValue();
                                            assert fullname != null;
                                            if (fullname.equals(detectedText.toString().toLowerCase())) {
                                                String deceasedName = (String) snapshot.child("DeceasedName").getValue();
                                                String deceasedSurname = (String) snapshot.child("DeceasedSurname").getValue();
                                                String deceasedGender = (String) snapshot.child("DeceasedGender").getValue();
                                                String deceasedJob = (String) snapshot.child("DeceasedJob").getValue();
                                                String deceased_dob = (String) snapshot.child("DateOfBirth").getValue();
                                                String deceased_dod = (String) snapshot.child("DateOfDeath").getValue();
                                                String bio = (String) snapshot.child("DeceasedBiography").getValue();
                                                names.add("Deceased Full Name:" + "\t" + fullname + "\n" + "\n" + "\nDeceased Gender:" + deceasedGender + "\n" + "\nDeceased Job:" + deceasedJob + "\n" + "\nDate Of Birth:" + deceased_dob + "\n" + "\nDate Of Dead:" + deceased_dod + "\n" + "\nDeceased Biography:" + bio + "\n");
                                            } else if (fullname.equals(detectedText.toString().toUpperCase())) {
                                                String deceasedGender = (String) snapshot.child("DeceasedGender").getValue();
                                                String deceasedJob = (String) snapshot.child("DeceasedJob").getValue();
                                                String deceased_dob = (String) snapshot.child("DateOfBirth").getValue();
                                                String deceased_dod = (String) snapshot.child("DateOfDeath").getValue();
                                                String bio = (String) snapshot.child("DeceasedBiography").getValue();
                                                names.add("Deceased Full Name:" + "\t" + fullname + "\n" + "\n" + "\nDeceased Gender:" + deceasedGender + "\n" + "\nDeceased Job:" + deceasedJob + "\n" + "\nDate Of Birth:" + deceased_dob + "\n" + "\nDate Of Dead:" + deceased_dod + "\n" + "\nDeceased Biography:" + bio + "\n");
                                            }


                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                                query = databaseReference.orderByChild("DeceasedFullName").equalTo(detectedText.toString());
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.exists()) {
                                            Toast.makeText(getApplicationContext(), "The deceased does not exist in the system!", Toast.LENGTH_LONG).show();
                                        }
                                        arrayAdapter.clear();

                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                                            String fullname = (String) snapshot.child("DeceasedFullName").getValue();


                                            assert fullname != null;
                                            if (fullname.equals(detectedText.toString().toLowerCase())) {
                                                String deceasedName = (String) snapshot.child("DeceasedName").getValue();
                                                String deceasedSurname = (String) snapshot.child("DeceasedSurname").getValue();
                                                String deceasedGender = (String) snapshot.child("DeceasedGender").getValue();
                                                String deceasedJob = (String) snapshot.child("DeceasedJob").getValue();
                                                String deceased_dob = (String) snapshot.child("DateOfBirth").getValue();
                                                String deceased_dod = (String) snapshot.child("DateOfDeath").getValue();
                                                String bio = (String) snapshot.child("DeceasedBiography").getValue();
                                                names.add("Deceased Full Name:" + "\t" + fullname + "\n" + "\n" + "\nDeceased Gender:" + deceasedGender + "\n" + "\nDeceased Job:" + deceasedJob + "\n" + "\nDate Of Birth:" + deceased_dob + "\n" + "\nDate Of Dead:" + deceased_dod + "\n" + "\nDeceased Biography:" + bio + "\n");
                                            } else if (fullname.equals(detectedText.toString().toUpperCase())) {
                                                String deceasedGender = (String) snapshot.child("DeceasedGender").getValue();
                                                String deceasedJob = (String) snapshot.child("DeceasedJob").getValue();
                                                String deceased_dob = (String) snapshot.child("DateOfBirth").getValue();
                                                String deceased_dod = (String) snapshot.child("DateOfDeath").getValue();
                                                String bio = (String) snapshot.child("DeceasedBiography").getValue();
                                                names.add("Deceased Full Name:" + "\t" + fullname + "\n" + "\n" + "\nDeceased Gender:" + deceasedGender + "\n" + "\nDeceased Job:" + deceasedJob + "\n" + "\nDate Of Birth:" + deceased_dob + "\n" + "\nDate Of Dead:" + deceased_dod + "\n" + "\nDeceased Biography:" + bio + "\n");
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }});
                    }}


            Intent ıntent = new Intent(OCR.this, showSearchedData.class);
            ıntent.putExtra("text", detectedText.toString());
            final String s = getIntent().getStringExtra("text");
            if (s == null) {
                Toast.makeText(getApplicationContext(), "Null", Toast.LENGTH_LONG).show();
            }
            detectedTextView.setText(detectedText);
            arrayAdapter.addAll(names);
            mList.setAdapter(arrayAdapter);
        } finally {
            textRecognizer.release();
        }
    }
    private void inspect(Uri uri) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize = 2;
            options.inScreenDensity = DisplayMetrics.DENSITY_LOW;
            bitmap = BitmapFactory.decodeStream(is, null, options);
            inspectFromBitmap(bitmap);
        } catch (FileNotFoundException e) {
            Log.w(TAG, "Failed to find the file: " + uri, e);
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.w(TAG, "Failed to close InputStream", e);
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_GALLERY:
                if (resultCode == RESULT_OK) {
                    inspect(data.getData());
                }
                break;
            case REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    if (imageUri != null) {
                        inspect(imageUri);
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

}
