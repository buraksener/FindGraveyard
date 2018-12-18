package com.sener35gmail.burak.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class SplashScreen extends AppCompatActivity {

    TextView SplashText;
    ProgressBar progressBar;

    RingProgressBar ringProgressBar1,ringProgressBar2;
    int progress=0;

    @SuppressLint("HandlerLeak")
    Handler myHandler= new Handler()
    {
        @Override
        public void handleMessage(Message msg){
            if(msg.what==0)
            {
                if(progress<100)
                {
                    progress++;
                    ringProgressBar1.setProgress(progress);
                    ringProgressBar2.setProgress(progress);

                }
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        SplashText = (TextView) findViewById(R.id.SplashText);
        Typeface font = Typeface.createFromAsset(getAssets(), "font/Pacifico-Regular.ttf");
        SplashText.setTypeface(font);
        //progressBar=(ProgressBar) findViewById(R.id.progress);
        //progressBar.setVisibility(View.INVISIBLE);

        ringProgressBar1=(RingProgressBar) findViewById(R.id.progress1);
        ringProgressBar2=(RingProgressBar) findViewById(R.id.progress2);
        ringProgressBar1.setOnProgressListener(new RingProgressBar.OnProgressListener() {
            @Override
            public void progressToComplete() {

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<100;i++)
                {
                    try{
                        Thread.sleep(40);
                        myHandler.sendEmptyMessage(0);

                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    }

                }
                Intent skip = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(skip);
                finish();
            }
        }).start();










        /* Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {

                    progressBar.setVisibility(View.VISIBLE);


                    sleep(5000);
                    //Intent skip=new Intent(getApplicationContext(),MainActivity.class);
                    //startActivity(skip);
                    // finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent skip = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(skip);
                    finish();

                }



            }

        };
       // blink();
        splashThread.start();*/


    }

   /* private void blink() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 1000;    //in milissegunds
                try {
                    Thread.sleep(timeToBlink);
                } catch (Exception e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView txt = (TextView) findViewById(R.id.SplashText);
                        if (txt.getVisibility() == View.VISIBLE) {
                            txt.setVisibility(View.INVISIBLE);
                        } else {
                            txt.setVisibility(View.VISIBLE);
                        }
                        blink();
                    }
                });
            }
        }).start();
    }*/
}
