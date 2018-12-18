package com.sener35gmail.burak.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ScanEpitaph extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_epitaph);
        Button scan_epitaph=(Button)findViewById(R.id.scan_image);

        scan_epitaph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent opencamera = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivity(opencamera);
                Toast.makeText(getApplicationContext(),"OCR is running now.."+"Scannig the image..",Toast.LENGTH_LONG).show();
            }
        });
    }

}
