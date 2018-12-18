package com.sener35gmail.burak.myapplication;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.text.TextUtils.isEmpty;


public class MainActivity extends AppCompatActivity {
    static final int DIALOG_ERROR_CONNECTION = 1;
    TextView tv;
    TextView ResetPassword;
    EditText et_email;
    EditText et_password;
    Button Sign_Up;
    Button Sign_In;
    String uid;


    private FirebaseAuth auth = FirebaseAuth.getInstance();
    ProgressBar progressBar;
    DatabaseReference reference;
    FirebaseDatabase database;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Typeface font = Typeface.createFromAsset(getAssets(), "font/Pacifico-Regular.ttf");

        auth = FirebaseAuth.getInstance();

        tv = (TextView) findViewById(R.id.textView);
        ResetPassword = (TextView) findViewById(R.id.reset_password);
        tv.setTypeface(font);
        Sign_In = (Button) findViewById(R.id.button);
        Sign_Up = (Button) findViewById(R.id.button2);
        et_email = (EditText) findViewById(R.id.editText2);
        et_password = (EditText) findViewById(R.id.editText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);

        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");
        // Sign_In.setOnClickListener(this);
        // preferences = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        // database = FirebaseDatabase.getInstance();
        //users = database.getReference("Users");
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, HomePage.class));
            finish();
        }

        auth = FirebaseAuth.getInstance();

        Sign_Up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v.getId() == R.id.button2) {
                    if (!isOnline(getApplication())) {
                        showDialog(DIALOG_ERROR_CONNECTION); //displaying the created dialog.
                    }
                    else{
                        Intent intent1 = new Intent(getApplicationContext(), Second_Screen.class);
                        startActivity(intent1);
                    }

                }
            }
        });

        ResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isOnline(getApplication())) {
                    showDialog(DIALOG_ERROR_CONNECTION); //displaying the created dialog.
                }

                else
                {
                    Intent skipReset = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                    startActivity(skipReset);
                }

            }
        });








            Sign_In.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isOnline(getApplication())) {
                        showDialog(DIALOG_ERROR_CONNECTION); //displaying the created dialog.
                    }

                    else{
                        //Internet available. Do what's required when internet is available.
                        String email = et_email.getText().toString();
                        final String password = et_password.getText().toString();
                        if (isEmpty(email)) {

                            Toast.makeText(getApplicationContext(), "Lütfen email adres giriniz!", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (isEmpty(password)) {
                            Toast.makeText(getApplicationContext(), "Lütfen şifre giriniz!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        else if (!email.matches(emailPattern)) {
                            Toast.makeText(getApplicationContext(), "Geçersiz email adres!", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.VISIBLE);
                        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);


                                if (!task.isSuccessful()) {

                                    if (password.length() < 8 || password.length() > 12) {
                                        Sign_In.setEnabled(false);
                                        Toast.makeText(getBaseContext(), "Şifre için minimum 8 karakter giriniz!", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Giriş başarısız,şifre ve ya email adresinizi kontrol edip,tekrar deneyin!", Toast.LENGTH_LONG).show();
                                    }
                                } else {

                                    // View parentLayout = findViewById(android.R.id.content);


                                    //Snackbar snackbar=Snackbar.make(parentLayout,"Tekrar Hoşgeldin!",Snackbar.LENGTH_LONG);
                                    //snackbar.show();


                                    String name="WellcomeBack";
                                    Intent intent = new Intent(MainActivity.this, HomePage.class);
                                    intent.putExtra("WellcomeBack",name);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                                    Snackbar.make(getWindow().getDecorView().getRootView(), "Tekrar Hoşgeldin!", Snackbar.LENGTH_LONG).show();
                                    finish();


                                }

                            }
                        });




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



}










































