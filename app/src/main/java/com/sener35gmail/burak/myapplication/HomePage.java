package com.sener35gmail.burak.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import static com.sener35gmail.burak.myapplication.MainActivity.DIALOG_ERROR_CONNECTION;

public class HomePage extends Activity {
    ListView listview;
    List<String> list = new ArrayList<String>();
    ArrayAdapter<String> adapter1;
    ImageButton ProfileButton;
    ImageButton LogoutButton;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private String sent;
    private String sent2;
    Integer[] icon={
            R.drawable.ic_person_add_black_24dp,
            R.drawable.ic_search_black_24dp,
            R.drawable.ic_tap_and_play_black_24dp,
            R.drawable.ic_contact_phone_black_24dp,
    };

    String[] itemName={
           "Merhum Kaydet","Merhum Ara","Mezartaşı Tarat","Mezar bakıcısı Ara"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Typeface font = Typeface.createFromAsset(getAssets(), "font/Pacifico-Regular.ttf");
        TextView Home_page = (TextView) findViewById(R.id.HomePage);
        Home_page.setTypeface(font);
        listview = (ListView) findViewById(R.id.list_view);

        CustomListAdapter adapter=new CustomListAdapter(this,itemName, icon);
        listview.setAdapter(adapter);


        if (!isOnline(this)) {
            showDialog(DIALOG_ERROR_CONNECTION); //displaying the created dialog.
        }


        //Wellcome snackbar\\
        Intent intent=getIntent();
        sent=intent.getStringExtra("WellcomeBack");
        String reciever="WellcomeBack";
        if(TextUtils.equals(sent,reciever))
        {
            Snackbar.make(getWindow().getDecorView().getRootView(), "Tekrar Hoşgeldin!", Snackbar.LENGTH_LONG).show();

        }

        sent2=intent.getStringExtra("Wellcome");
        String reciever2="Wellcome";
         if(TextUtils.equals(sent2,reciever2))
        {
            Snackbar.make(getWindow().getDecorView().getRootView(), " Hoşgeldin! :)", Snackbar.LENGTH_LONG).show();
        }



        list.add("Merhum Kaydet ");
        list.add("Merhum Ara");
        list.add("Mezartaşı Tarat");
        list.add("Mezar bakıcısı Ara");
       // adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);

        //listview.setAdapter(adapter);
        ProfileButton = (ImageButton) findViewById(R.id.imageButton);
        LogoutButton = (ImageButton) findViewById(R.id.imageButton2);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = auth.getCurrentUser();

     /*   ArrayAdapter<String> adapter1=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list){
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                // Initialize a TextView for ListView each Item
                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                // Set the text color of TextView (ListView Item)
                tv.setTextColor(Color.BLACK);

                // Generate ListView Item using TextView
                return view;
            }
        };*/

        // DataBind ListView with items from ArrayAdapter
        listview.setAdapter(adapter);





            LogoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!isOnline(getApplication())) {
                        showDialog(DIALOG_ERROR_CONNECTION); //displaying the created dialog.
                    }
                    //Internet available. Do what's required when internet is available.
                    else{
                        signOut();
                        Intent intent = new Intent(HomePage.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        finish();
                    }
                }
            });








        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isOnline(getApplication())) {
                    showDialog(DIALOG_ERROR_CONNECTION); //displaying the created dialog.

                }
                else
                {

                    if (position == 0) {
                        //  Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in_left);
                        //view.setAnimation(animation);
                        //view.startAnimation(animation);
                        Intent skip = new Intent(view.getContext(), AddDeceasedInfo.class);
                        startActivityForResult(skip, 0);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else if (position == 1) {
                  /*  Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_left);
                    view.setAnimation(animation);
                    view.startAnimation(animation);*/
                        Intent skip2 = new Intent(view.getContext(), FindDeceasedProfil.class);
                        startActivityForResult(skip2, 1);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else if (position == 2) {
                  /*  Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_left);
                    view.setAnimation(animation);
                    view.startAnimation(animation);*/
                        Intent skip3 = new Intent(view.getContext(), TextRecognition.class);
                        startActivityForResult(skip3, 2);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else if (position == 3) {
                   /* Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_left);
                    view.setAnimation(animation);
                    view.startAnimation(animation);*/
                        Intent skip4 = new Intent(view.getContext(), ContactGraveCaregiver.class);
                        startActivityForResult(skip4, 3);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }

                }
            }
        });

        ProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOnline(getApplication())) {
                    showDialog(DIALOG_ERROR_CONNECTION); //displaying the created dialog.
                }
                else{
                    Intent UserProfile = new Intent(getApplicationContext(), Profile.class);
                    startActivity(UserProfile);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });





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


    public void signOut() {
        auth.signOut();
        progressBar.setVisibility(View.VISIBLE);
    }



    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        Toast.makeText(getApplicationContext(),"Çıkmak için butona uzun basılı tutun.",Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            // do your stuff here
            super.onBackPressed();
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }
}


