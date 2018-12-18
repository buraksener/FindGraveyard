package com.sener35gmail.burak.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.sener35gmail.burak.myapplication.MainActivity.DIALOG_ERROR_CONNECTION;

/**
 * Created by Admin on 08.08.2018.
 */

public class Frag1 extends Fragment {


    EditText Current_user_name;
    EditText Current_user_surname;
    EditText Current_user_email;
    EditText Current_user_password;
    EditText Current_user_gender;
    EditText Current_user_phone;
    EditText Current_user_birthdate;
    EditText Current_user_UserType;
    TextView P_name;
    TextView P_Surname;
    TextView P_Email;
    TextView P_Password;
    TextView P_phone;
    TextView P_gender;
    TextView P_birthdate;
    TextView P_userType;

    Button btn_ChangeProfileInfo;



    String UserName;
    String UserSurname;
    String UserPassword;
    String UserEmail;
    String gender;
    String birthdate;
    String phone;
    String userType;
    String uid;







    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View v=inflater.inflate(R.layout.frag1_layout,container,false);

        P_name=(TextView) v.findViewById(R.id.textView17);
        P_Surname=(TextView) v.findViewById(R.id.textView18);
        P_Email=(TextView) v.findViewById(R.id.textView19);
        P_Password=(TextView) v.findViewById(R.id.textView20);
        P_gender=(TextView) v.findViewById(R.id.textView28);
        P_phone=(TextView) v.findViewById(R.id.textView30);
        P_birthdate=(TextView) v.findViewById(R.id.textView29);
        P_userType=(TextView) v.findViewById(R.id.textView21);



        Current_user_name=(EditText) v.findViewById(R.id.editText15);
        Current_user_surname=(EditText) v.findViewById(R.id.editText16);
        Current_user_email=(EditText) v.findViewById(R.id.editText17);
        Current_user_password=(EditText) v.findViewById(R.id.editText18);
        Current_user_gender=(EditText) v.findViewById(R.id.editText20);
        Current_user_phone=(EditText) v.findViewById(R.id.editText22);
        Current_user_birthdate=(EditText) v.findViewById(R.id.editText21);
        Current_user_UserType=(EditText) v.findViewById(R.id.editText19);



        auth=FirebaseAuth.getInstance();
        uid=auth.getCurrentUser().getUid();
        users= FirebaseDatabase.getInstance().getReference("Users").child(uid);

        users2=users;
        ValueEventListener eventListener=new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                try{
                     /* User user=new User();
                      user=ds.getValue(User.class);*/
                    UserName=(String) dataSnapshot.child("Name").getValue();
                    UserSurname=(String) dataSnapshot.child("Surname").getValue();
                    UserEmail=(String) dataSnapshot.child("emailAdress").getValue();
                    UserPassword=(String) dataSnapshot.child("password").getValue();
                    gender=(String) dataSnapshot.child("gender").getValue();
                    userType=(String) dataSnapshot.child("UserType").getValue();
                    phone=(String)dataSnapshot.child("phoneNumber").getValue();
                    birthdate=(String) String.valueOf(dataSnapshot.child("DateOfBirth").getValue());

                    if(String.valueOf(phone).isEmpty()   )
                    {Current_user_phone.setVisibility(View.INVISIBLE);
                        P_phone.setVisibility(View.INVISIBLE);

                    }
                    else if(phone.equals("null"))
                    {
                        Current_user_phone.setVisibility(View.INVISIBLE);
                        P_phone.setVisibility(View.INVISIBLE);
                    }
                    else if(String.valueOf(birthdate).isEmpty())
                    {
                        birthdate="unspecified";
                        Current_user_birthdate.setText(String.valueOf(birthdate),TextView.BufferType.EDITABLE);

                        //Current_user_birthdate.setVisibility(View.INVISIBLE);
                        //P_birthdate.setVisibility(View.INVISIBLE);
                    }

                    else
                    {Current_user_phone.setVisibility(View.VISIBLE);
                        P_phone.setVisibility(View.VISIBLE);
                        Current_user_birthdate.setVisibility(View.VISIBLE);
                        P_birthdate.setVisibility(View.VISIBLE);}







                    //list.add(user);
                }
                catch (NullPointerException e)
                {

                }
                //mListview.setAdapter(adapter);



                Current_user_name.setText(UserName);
                Current_user_surname.setText(UserSurname);
                Current_user_email.setText(UserEmail);
                Current_user_password.setText(UserPassword);
                Current_user_gender.setText(gender);
                Current_user_phone.setText(String.valueOf(phone));
                Current_user_UserType.setText(userType);
                Current_user_birthdate.setText(String.valueOf(birthdate));

                Current_user_UserType.setFocusable(false);
                Current_user_UserType.setEnabled(false);
                Current_user_gender.setFocusable(false);
                Current_user_gender.setEnabled(false);
                Current_user_email.setFocusable(false);
                Current_user_email.setEnabled(false);
                Current_user_password.setFocusable(false);
                Current_user_password.setEnabled(false);

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        users2.addListenerForSingleValueEvent(eventListener);

        btn_ChangeProfileInfo=(Button) v.findViewById(R.id.btn_changeProfileInfo);


        btn_ChangeProfileInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openDialog();



            }
        });


        if (!isOnline(getActivity())) {
            getActivity().showDialog(DIALOG_ERROR_CONNECTION); //displaying the created dialog.
        } else {
            //Internet available. Do what's required when internet is available.
        }





        return v;
    }







    DatabaseReference users;
    DatabaseReference users2;
    FirebaseAuth auth;




    private void openDialog() {

        UserName=Current_user_name.getText().toString();
        UserSurname=Current_user_surname.getText().toString();
        UserPassword=Current_user_password.getText().toString();
        UserEmail=Current_user_email.getText().toString();
        gender=Current_user_gender.getText().toString();
        userType=Current_user_UserType.getText().toString();
        phone=Current_user_phone.getText().toString();
        birthdate=Current_user_birthdate.getText().toString();
        final AlertDialog.Builder builder=new AlertDialog.Builder(getActivity(),R.style.MyDialog);



        builder.setMessage("Yapılan değişiklikler kaydedilsin mi?");
        builder.setCancelable(true);
        builder.setNegativeButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Map<String,String> newMap=new HashMap<>();
                        newMap.put("Name",UserName);
                        newMap.put("Surname",UserSurname);
                        newMap.put("emailAdress",UserEmail);
                        newMap.put("password",UserPassword);
                        newMap.put("gender",gender);
                        newMap.put("UserType",userType);
                        newMap.put("phoneNumber",phone);
                        newMap.put("DateOfBirth",String.valueOf(birthdate));
                        users.setValue(newMap);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Toast.makeText(getActivity(),"Değişiklikler kayıt edildi!",Toast.LENGTH_LONG).show();

            }
        });
        builder.setPositiveButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                Toast.makeText(getActivity(),"Değişiklikler iptal edildi!",Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog=builder.create();
        alertDialog.show();
        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

        positiveButton.setTextColor(Color.parseColor("#FF0B8B42"));
        positiveButton.setBackgroundColor(Color.parseColor("#FFE1FCEA"));

        negativeButton.setTextColor(Color.parseColor("#FFFF0400"));
        negativeButton.setBackgroundColor(Color.parseColor("#FFFCB9B7"));
        getActivity().getWindow().setBackgroundDrawableResource(R.color.white);

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

    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case DIALOG_ERROR_CONNECTION:
                AlertDialog.Builder errorDialog = new AlertDialog.Builder(getActivity());
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
                getActivity().getWindow().setBackgroundDrawableResource(R.color.white);
                return errorAlert;





            default:
                break;
        }
        return dialog;

    }




}
