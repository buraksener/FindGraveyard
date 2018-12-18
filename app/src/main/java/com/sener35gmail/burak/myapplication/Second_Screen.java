package com.sener35gmail.burak.myapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.R.layout.simple_spinner_dropdown_item;


/**
 * Created by Admin on 11.02.2018.
 */





public  class Second_Screen extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener{

    String gender;
    String phone;
    String UserType;
    String dmy;
    String uid;
    String city;
    String Cemetery;
    private  String wellcome;

    static final int DIALOG_ERROR_CONNECTION = 1;
    private ProgressBar progressBar;
    private FirebaseAuth auth;




    //SharedPreferences preferences;

    FirebaseDatabase database;
    DatabaseReference users;


    @Override


    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_screen);
        final Spinner SpinnerCity=(Spinner) findViewById(R.id.spinnerTown);
        final Spinner SpinnerCemetery=(Spinner) findViewById(R.id.spinnerCemetery);
        ArrayAdapter<String>Myadapter=new ArrayAdapter<String>(Second_Screen.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Cities));
        Myadapter.setDropDownViewResource(simple_spinner_dropdown_item);
        SpinnerCity.setAdapter(Myadapter);

        final Button Register=(Button) findViewById(R.id.button3);

        Spinner Myspinner=(Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> myAdapter= new ArrayAdapter<String>(Second_Screen.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.UserType));
        myAdapter.setDropDownViewResource(simple_spinner_dropdown_item);
        Myspinner.setAdapter(myAdapter);
        final EditText et_name=(EditText) findViewById(R.id.editText3);
        final EditText et_surname=(EditText) findViewById(R.id.editText5);
        final EditText et_password=(EditText) findViewById(R.id.editText6);
        final EditText et_Confirm_password=(EditText) findViewById(R.id.editText7);
        final CheckBox checkbox_male=(CheckBox) findViewById(R.id.checkBoxMale);
        final CheckBox checkBox_female=(CheckBox) findViewById(R.id.checkBoxFemale);
        final EditText et_email=(EditText) findViewById(R.id.User_email);
        final EditText et_phone_number=(EditText) findViewById(R.id.phone_number);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        final String  Control_Pattern="[a-zA-Z ]+";

        phone= String.valueOf(et_phone_number.getText().toString());
        PhoneNumberUtils.formatNumber(String.valueOf(phone));


        auth=FirebaseAuth.getInstance();
          database=FirebaseDatabase.getInstance();

          users=database.getReference("Users");

        //User Type Spinner
        Myspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(position==0)
             {Register.setEnabled(false);
             Toast.makeText(getApplicationContext(),"Lütfen kullanıcı tipinizi seçiniz",Toast.LENGTH_LONG).show();
             et_phone_number.setFocusable(false);
                 et_phone_number.setVisibility(View.INVISIBLE);
                 SpinnerCity.setVisibility(View.GONE);
                 SpinnerCemetery.setVisibility(View.GONE);

             }
             else if(position==1)
             {Register.setEnabled(true);
                 et_phone_number.setFocusable(false);
                 et_phone_number.setVisibility(View.INVISIBLE);
                 UserType="kullanıcı";
                 phone=null;
                 SpinnerCity.setVisibility(View.GONE);
                 SpinnerCemetery.setVisibility(View.GONE);
             }
             else if ( position == 2) {
                Register.setEnabled(true);
                et_phone_number.setFocusableInTouchMode(true);
                et_phone_number.setVisibility(View.VISIBLE);
                UserType="Mezar bakıcısı";
                SpinnerCity.setVisibility(View.VISIBLE);
                SpinnerCemetery.setVisibility(View.VISIBLE);

            }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
               Register.setEnabled(false);
            }
        });

        //Grave Caregiverın spinnerı
        if(SpinnerCity.getVisibility()==View.VISIBLE)
        {
            SpinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if(position==0)
                    {
                        ArrayAdapter<String>MyEmptyAdapter=new ArrayAdapter<String>(Second_Screen.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Empty));
                        MyEmptyAdapter.setDropDownViewResource(simple_spinner_dropdown_item);
                        SpinnerCemetery.setAdapter(MyEmptyAdapter);
                        Register.setEnabled(false);
                        Toast.makeText(getApplicationContext(),"Lütfen bir şehir seçiniz!",Toast.LENGTH_LONG).show();

                    }

                    else if(position==1)
                    {
                        ArrayAdapter<String>Myadapter2=new ArrayAdapter<String>(Second_Screen.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Adana_Cemeteries));
                        Myadapter2.setDropDownViewResource(simple_spinner_dropdown_item);
                        Register.setEnabled(true);
                        SpinnerCemetery.setAdapter(Myadapter2);
                        city="Adana";
                        SpinnerCemetery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(position==0)
                                {
                                    Register.setEnabled(false);
                                    Toast.makeText(getApplicationContext(),"Lütfen bir mezarlık seçiniz!",Toast.LENGTH_LONG).show();
                                }
                                else if(position==1)
                                {
                                    Cemetery="Akkapı Mezarlığı";
                                    Register.setEnabled(true);
                                }
                                else if(position==2)
                                {
                                    Cemetery="Asri Mezarlığı";
                                    Register.setEnabled(true);
                                }
                                else if(position==3)
                                {
                                    Cemetery="Kabasakal Mezarlığı";
                                    Register.setEnabled(true);
                                }
                                else if(position==4)
                                {
                                    Cemetery="Küçükoba Mezarlığı";
                                    Register.setEnabled(true);
                                }
                                else if(position==5)
                                {
                                    Cemetery="Buruk Mezarlığı";
                                    Register.setEnabled(true);
                                }
                                else if(position==6)
                                {
                                    Cemetery="Adana Emniyet Müdürlüğü Polis Şehitliği";
                                    Register.setEnabled(true);
                                }


                                else
                                {
                                    Register.setEnabled(true);
                                }



                            }

                            public void onNothingSelected(AdapterView<?> parent) {
                                //Register.setEnabled(false);
                                Toast.makeText(getApplicationContext(),"Lütfen bir mezarlık seçiniz!",Toast.LENGTH_LONG).show();

                            }
                        });


                    }
                    else if(position==2)
                    {
                        ArrayAdapter<String>Myadapter2=new ArrayAdapter<String>(Second_Screen.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Adıyaman_Cemeteries));
                        Myadapter2.setDropDownViewResource(simple_spinner_dropdown_item);
                        Register.setEnabled(true);
                        SpinnerCemetery.setAdapter(Myadapter2);
                        city="Adıyaman";
                        SpinnerCemetery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(position==0)
                                {
                                    Register.setEnabled(false);
                                    Toast.makeText(getApplicationContext(),"Lütfen bir mezarlık seçiniz!",Toast.LENGTH_LONG).show();
                                }
                                else if(position==1)
                                {
                                    Cemetery="Adıyaman Belediyesi Mezarlığı";
                                    Register.setEnabled(true);
                                }
                                else if(position==2)
                                {
                                    Cemetery="Aşağı Karapınar Mezarlığı";
                                    Register.setEnabled(true);
                                }
                                else if(position==3)
                                {
                                    Cemetery="Yenı Beledıye Mezarlığı";
                                    Register.setEnabled(true);
                                }
                                else if(position==4)
                                {
                                    Cemetery="Aşağı Karapınar Mezarlığı";
                                    Register.setEnabled(true);
                                }
                                else if(position==5)
                                {
                                    Cemetery="Süryani Kadim Mezarlığı";
                                    Register.setEnabled(true);
                                }
                                else if(position==6)
                                {
                                    Cemetery="Karapınar Mezarlığı";
                                    Register.setEnabled(true);
                                }


                                else
                                {
                                    Register.setEnabled(true);
                                }



                            }

                            public void onNothingSelected(AdapterView<?> parent) {
                                Toast.makeText(getApplicationContext(),"Lütfen bir mezarlık seçiniz!",Toast.LENGTH_LONG).show();


                            }
                        });
                    }
                    else if(position==3)
                    {
                        ArrayAdapter<String>Myadapter2=new ArrayAdapter<String>(Second_Screen.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Afyon_Cemeteries));
                        Myadapter2.setDropDownViewResource(simple_spinner_dropdown_item);
                        Register.setEnabled(true);
                        SpinnerCemetery.setAdapter(Myadapter2);
                        city="Afyon";
                        SpinnerCemetery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(position==0)
                                {
                                    Register.setEnabled(false);
                                    Toast.makeText(getApplicationContext(),"Lütfen bir mezarlık seçiniz!",Toast.LENGTH_LONG).show();
                                }
                                else if(position==1)
                                {
                                    Cemetery="Kocatepe Mezarlığı";
                                    Register.setEnabled(true);
                                }
                                else if(position==2)
                                {
                                    Cemetery="Bayraktepe Mezarlığı";
                                    Register.setEnabled(true);
                                }
                                else if(position==3)
                                {
                                    Cemetery="Afyon cumhuriyet Şehitliği";
                                    Register.setEnabled(true);
                                }
                                else if(position==4)
                                {
                                    Cemetery="Yzb.Agah Efendi Mezarlığı";
                                    Register.setEnabled(true);
                                }
                                else
                                {
                                    Register.setEnabled(true);
                                }



                            }

                            public void onNothingSelected(AdapterView<?> parent) {
                                //Register.setEnabled(false);
                                Toast.makeText(getApplicationContext(),"Lütfen bir mezarlık seçiniz!",Toast.LENGTH_LONG).show();

                            }
                        });

                    }
                    else if(position==4)
                    {
                        ArrayAdapter<String>Myadapter2=new ArrayAdapter<String>(Second_Screen.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Empty));
                        Myadapter2.setDropDownViewResource(simple_spinner_dropdown_item);
                        Register.setEnabled(true);
                        SpinnerCemetery.setAdapter(Myadapter2);
                        city="Ağrı";
                    }
                    else if(position==5)
                    {
                        ArrayAdapter<String>Myadapter2=new ArrayAdapter<String>(Second_Screen.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Amasya_Cemeteries));
                        Myadapter2.setDropDownViewResource(simple_spinner_dropdown_item);
                        Register.setEnabled(true);
                        SpinnerCemetery.setAdapter(Myadapter2);
                        city="Amasya";
                        SpinnerCemetery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(position==0)
                                {
                                    Register.setEnabled(false);
                                    Toast.makeText(getApplicationContext(),"Lütfen bir mezarlık seçiniz!",Toast.LENGTH_LONG).show();
                                }
                                else if(position==1)
                                {
                                    Cemetery="Tekirdede Mezarlığı";
                                    Register.setEnabled(true);
                                }

                                else
                                {
                                    Register.setEnabled(true);
                                }



                            }

                            public void onNothingSelected(AdapterView<?> parent) {
                                //Register.setEnabled(false);
                                Toast.makeText(getApplicationContext(),"Lütfen bir mezarlık seçiniz!",Toast.LENGTH_LONG).show();

                            }
                        });


                    }
                    else if(position==6)
                    {
                        ArrayAdapter<String>Myadapter2=new ArrayAdapter<String>(Second_Screen.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Ankara_Cemeteries));
                        Myadapter2.setDropDownViewResource(simple_spinner_dropdown_item);
                        Register.setEnabled(true);
                        SpinnerCemetery.setAdapter(Myadapter2);
                        city="Ankara";
                        SpinnerCemetery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(position==0)
                                {
                                    Register.setEnabled(false);
                                    Toast.makeText(getApplicationContext(),"Lütfen bir mezarlık seçiniz!",Toast.LENGTH_LONG).show();
                                }
                                else if(position==1)
                                {
                                    Cemetery="Karşıyaka Mezarlığı";
                                    Register.setEnabled(true);
                                }
                                else if(position==2)
                                {
                                    Cemetery="Cebeci Mezarlığı";
                                    Register.setEnabled(true);
                                }
                                else if(position==3)
                                {
                                    Cemetery="Sincan Mezarlığı";
                                    Register.setEnabled(true);
                                }
                                else if(position==4)
                                {
                                    Cemetery="Ortaköy Mezarlığı";
                                    Register.setEnabled(true);
                                }

                                else
                                {
                                    Register.setEnabled(true);
                                }



                            }

                            public void onNothingSelected(AdapterView<?> parent) {
                                //Register.setEnabled(false);
                                Toast.makeText(getApplicationContext(),"Lütfen bir mezarlık seçiniz!",Toast.LENGTH_LONG).show();

                            }
                        });



                    }
                    else if(position==36)
                    {
                        ArrayAdapter<String>Myadapter2=new ArrayAdapter<String>(Second_Screen.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Izmır_Cemeteries));
                        Myadapter2.setDropDownViewResource(simple_spinner_dropdown_item);
                        //Register.setEnabled(true);
                        SpinnerCemetery.setAdapter(Myadapter2);
                        city="İzmir";
                        SpinnerCemetery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(position==0)
                                {
                                    Register.setEnabled(false);
                                    Toast.makeText(getApplicationContext(),"Lütfen bir mezarlık seçiniz!",Toast.LENGTH_LONG).show();
                                }
                                else if(position==1)
                                {
                                    Cemetery="Aşağı NarlıdereMezarlığı";
                                    Register.setEnabled(true);
                                }
                                else if(position==2)
                                {
                                    Cemetery="Bornova Yeni Mezarlığı";
                                    Register.setEnabled(true);
                                }
                                else if(position==3)
                                {
                                    Cemetery="Bornova Eski Mezarlığı";
                                    Register.setEnabled(true);
                                }
                                else if(position==4)
                                {
                                    Cemetery="Buca Gökdere Mezarlığı";
                                    Register.setEnabled(true);
                                }
                                else if(position==5)
                                {
                                    Cemetery="Yeni Buca Mezarlığı";
                                    Register.setEnabled(true);

                                }
                                else if(position==7)
                                {
                                    Cemetery="Eski Bornova Mezarlığı";
                                    Register.setEnabled(true);

                                }
                                else if(position==8)
                                {
                                    Cemetery="Eski Balçova Mezarlığı";
                                    Register.setEnabled(true);

                                }
                                else if(position==9)
                                {
                                    Cemetery="Cennet Bahçesi Mezarlığı";
                                    Register.setEnabled(true);

                                }
                                else if(position==10)
                                {
                                    Cemetery="Dikili Mezarlığı";
                                    Register.setEnabled(true);

                                }
                                else if(position==11)
                                {
                                    Cemetery="Doğançay  Mezarlığı";
                                    Register.setEnabled(true);

                                }
                                else if(position==12)
                                {
                                    Cemetery="Eski Foça Yeni Halk  Mezarlığı";
                                    Register.setEnabled(true);

                                }
                                else if(position==13)
                                {
                                    Cemetery="Gaziemir Mezarlığı";
                                    Register.setEnabled(true);

                                }
                                else if(position==14)
                                {
                                    Cemetery="Hacılarkırı Mezarlığı";
                                    Register.setEnabled(true);

                                }
                                else if(position==15)
                                {
                                    Cemetery="Işıkkent Mezarlığı";
                                    Register.setEnabled(true);

                                }
                                else if(position==16)
                                {
                                    Cemetery="Kokluca Mezarlığı";
                                    Register.setEnabled(true);

                                }
                                else if(position==17)
                                {
                                    Cemetery="Paşaköprü Hiristiyan Mezarlığı";
                                    Register.setEnabled(true);
                                }
                                else if(position==18)
                                {
                                    Cemetery="Paşaköprü Müslüman Mezarlığı";
                                    Register.setEnabled(true);
                                }
                                else if(position==19)
                                {
                                    Cemetery="Soğukkuyu  Mezarlığı";
                                    Register.setEnabled(true);
                                }
                                else if(position==20)
                                {
                                    Cemetery="Pınarbaşı  Mezarlığı";
                                    Register.setEnabled(true);
                                }
                                else if(position==21)
                                {
                                    Cemetery="Sarnıç   Mezarlığı";
                                    Register.setEnabled(true);
                                }
                                else if(position==22)
                                {
                                    Cemetery="Yeşilyurt  Mezarlığı";
                                    Register.setEnabled(true);
                                }






                                else
                                {
                                    Register.setEnabled(true);
                                }



                            }

                            public void onNothingSelected(AdapterView<?> parent) {
                                //Register.setEnabled(false);
                                Toast.makeText(getApplicationContext(),"Lütfen bir mezarlık seçiniz!",Toast.LENGTH_LONG).show();

                            }
                        });



                    }
                    else
                    {Register.setEnabled(true);}


                }


                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }





            Register.setOnClickListener(new View.OnClickListener() {
                @Override


                public void onClick(View v) {
                    final String User_name = et_name.getText().toString();
                    final String UserSurname = et_surname.getText().toString();
                    final String UserPassword = et_password.getText().toString();
                    final String UserConfirmPassword = et_Confirm_password.getText().toString().trim();
                    final String UserEmail = et_email.getText().toString().trim();
                    if (!isOnline(getApplication())) {
                        showDialog(DIALOG_ERROR_CONNECTION); //displaying the created dialog.
                    }
                    else{
                        //Internet available. Do what's required when internet is available.





                    progressBar.setVisibility(View.VISIBLE);

                    auth.createUserWithEmailAndPassword(UserEmail, UserPassword).addOnCompleteListener(Second_Screen.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(Second_Screen.this, "Kullanıcı kayıt ediliyor... İşlem: " + task.isSuccessful(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                Toast.makeText(Second_Screen.this, "Kayıt işlemi başarısız!\n Girilen emial adresi başka bir kullanıcı tarafında kullanılıyor olabilir!" + task.getException(), Toast.LENGTH_LONG).show();
                                if (TextUtils.isEmpty(User_name) || TextUtils.isEmpty(UserSurname) || TextUtils.isEmpty(UserPassword) || TextUtils.isEmpty(UserConfirmPassword)) {
                                    Toast.makeText(getBaseContext(), "Lütfen tüm gerekli alanları doldurduğunuzdan emin olun!", Toast.LENGTH_LONG).show();

                                } else if (!UserPassword.equals(UserConfirmPassword)) {
                                    Toast.makeText(getBaseContext(), "Lütfen şifre girin ve ardında tekrar şifre girin!", Toast.LENGTH_LONG).show();

                                } else if (UserPassword.length() < 8 || UserConfirmPassword.length() < 8) {
                                    Toast.makeText(getBaseContext(), "Lütfen şifre için en az 8 (sekiz) karakter giriniz!  ", Toast.LENGTH_LONG).show();

                                } else if (UserPassword.length() > 12 || UserConfirmPassword.length() > 12) {
                                    Toast.makeText(getBaseContext(), "Lütfen şifre için en fazla 12 (on iki) karakter giriniz!", Toast.LENGTH_LONG).show();
                                } else if (!UserEmail.matches(emailPattern)) {
                                    Toast.makeText(getApplicationContext(), "Geçersiz email adres", Toast.LENGTH_SHORT).show();
                                } else if (!User_name.matches(Control_Pattern)) {
                                    Toast.makeText(getApplicationContext(), "Geçersiz isim, \nYardım:Ö,Ü,Ş harflerini içeren bir isim girmeden tekrar deneyiniz", Toast.LENGTH_SHORT).show();
                                } else if (!UserSurname.matches(Control_Pattern)) {
                                    Toast.makeText(getApplicationContext(), "Geçersiz soyisim", Toast.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(phone)) {
                                    Toast.makeText(getApplicationContext(), "Lütfen cep telefon numaranızı giriniz", Toast.LENGTH_LONG).show();
                                }

                            } else {
                                users.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        String uid = (String) auth.getCurrentUser().getUid();
                                        users = FirebaseDatabase.getInstance().getReference("Users").child(uid);
                                        Map<String, String> newMap = new HashMap<String, String>();
                                        newMap.put("Name", User_name);
                                        newMap.put("Surname", UserSurname);
                                        newMap.put("UserType", UserType);
                                        newMap.put("password", UserPassword);
                                        newMap.put("emailAdress", UserEmail);
                                        newMap.put("UserUid", uid);
                                        newMap.put("phoneNumber", phone);
                                        newMap.put("gender", gender);
                                        newMap.put("DateOfBirth", dmy);
                                        newMap.put("City", city);
                                        newMap.put("Cemetery", Cemetery);
                                        users.setValue(newMap);
                                        wellcome="Wellcome";
                                        Intent AccessHome=new Intent(Second_Screen.this,MainActivity.class);
                                        AccessHome.putExtra("Wellcome",wellcome);
                                        startActivity(new Intent(Second_Screen.this, MainActivity.class));
                                        finish();
                                        //Toast.makeText(getApplicationContext(),"Kayıt işlemi başarıyla tamamlandı!",Toast.LENGTH_LONG).show();

                                       /* View parentLayout = findViewById(android.R.id.content);
                                        Snackbar snackbar = Snackbar.make(parentLayout, " Hoşgeldin!", Snackbar.LENGTH_LONG);
                                        snackbar.show();*/


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Toast.makeText(getApplicationContext(),"Kayıt işlemi başarıyla gerçekleştirilemedi!",Toast.LENGTH_LONG).show();
                                        View parentLayout = findViewById(android.R.id.content);
                                        Snackbar snackbar = Snackbar.make(parentLayout, "Kayıt işlemi başarısız!", Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                    }
                                });


                            }
                        }
                    });
                } }
            });







        checkbox_male.setOnClickListener(new View.OnClickListener() {
           @Override
            public  void onClick(View v){
               Boolean condition=checkbox_male.isChecked();

             if (condition && checkBox_female.isChecked() )
             {Register.setEnabled(false);
                 Toast.makeText(getBaseContext(),"Lütfen tek cinsiyet seçin! ",Toast.LENGTH_SHORT).show();
             }
             else if(checkbox_male.isChecked())
             {Toast.makeText(getBaseContext(),"Cinsiyet 'Erkek' olarak seçildi. ",Toast.LENGTH_SHORT).show();
             gender="erkek";}
             else
             {Register.setEnabled(true);
                 gender="null";}

           }

        });
        checkBox_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean condition2=checkBox_female.isChecked();

                if(condition2&& checkbox_male.isChecked() )
                {Register.setEnabled(false);
                    Toast.makeText(getBaseContext(),"Lütfen tek cinsiyet seçin! ",Toast.LENGTH_SHORT).show();
                }

                else if(checkBox_female.isChecked())
                {Toast.makeText(getBaseContext(),"Cinsiyet 'Kadın' olarak seçildi. ",Toast.LENGTH_SHORT).show();
                gender="kadın";}


                else
                {Register.setEnabled(true);
                gender="null";}


            }
        });


        Button OpenDatePicker=(Button)findViewById(R.id.openDatePicker);
        OpenDatePicker.setOnClickListener(new View.OnClickListener() {
         @Override
          public void onClick(View v) {
        DialogFragment datePicker=new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(),"Time Picker");

            }
        });

        et_phone_number.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                // TODO Auto-generated method stub
                phone = et_phone_number.getText().toString();
                if(phone.matches("^0"))
                {
                    Toast.makeText(getApplicationContext(),"Lütfen numaranın başına 0 (sıfır) koymadan giriniz!",Toast.LENGTH_SHORT).show();
                    et_phone_number.setText("");


                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s)
            {
                phone = et_phone_number.getText().toString();
                int  textLength = et_phone_number.getText().length();
                if (phone.endsWith("-") || phone.endsWith(" ") || phone.endsWith(" "))
                    return;
                if (textLength == 1) {
                    if (!phone.contains("("))
                    {
                        et_phone_number.setText(new StringBuilder(phone).insert(phone.length() - 1, "(").toString());
                        et_phone_number.setSelection(et_phone_number.getText().length());
                    }
                }
                else if (textLength == 5)
                {
                    if (!phone.contains(")"))
                    {
                        et_phone_number.setText(new StringBuilder(phone).insert(phone.length() - 1, ")").toString());
                        et_phone_number.setSelection( et_phone_number.getText().length());
                    }
                }
                else if (textLength == 6)
                {
                    et_phone_number.setText(new StringBuilder(phone).insert(phone.length() - 1, " ").toString());
                    et_phone_number.setSelection( et_phone_number.getText().length());
                }
                else if (textLength == 10)
                {
                    if (!phone.contains("-"))
                    {
                        et_phone_number.setText(new StringBuilder(phone).insert(phone.length() - 1, "-").toString());
                        et_phone_number.setSelection( et_phone_number.getText().length());
                    }
                }


               else if(textLength==0)
                {
                    Toast.makeText(Second_Screen.this,"Lütfen geçerli telefon no formatına uygun girin örn;(5xx) xxx-xxxx",Toast.LENGTH_LONG).show();
                    Register.setEnabled(false);
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
                return errorAlert;

            default:
                break;
        }
        return dialog;
    }





    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void onDateSet(DatePicker view,int year, int month, int dayOfMonth ) {
  Calendar c=Calendar.getInstance();
  c.set(Calendar.YEAR,year);
  c.set(Calendar.MONTH,month);
  c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
  String currentDate= DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        TextView textviewDate=(TextView) findViewById(R.id.textviewDate);
        textviewDate.setText(currentDate);


   dmy=String.valueOf(dayOfMonth)+"/"+String.valueOf(month+1)+"/"+String.valueOf(year);


    }



}
