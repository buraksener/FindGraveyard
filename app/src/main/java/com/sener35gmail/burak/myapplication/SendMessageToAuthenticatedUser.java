package com.sener35gmail.burak.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SendMessageToAuthenticatedUser extends AppCompatActivity {
    Button sendMessage;
    EditText et_message;
    String message;

    String Sendname;
    String Sendsurname;
    String Sendusertype;

    String Recievedname;
    String RecievedSurname;


    FirebaseDatabase database;
    DatabaseReference databaseReferenceRecieved;
    DatabaseReference databaseReferenceSend;
    FirebaseAuth firebaseAuth;

    String uid;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message_to_authenticated_user);
        sendMessage=(Button) findViewById(R.id.send);
        et_message=(EditText) findViewById(R.id.sendMessage);


        database=FirebaseDatabase.getInstance();


        firebaseAuth=FirebaseAuth.getInstance();
        uid=firebaseAuth.getCurrentUser().getUid();
        databaseReferenceSend=database.getReference("Users").child(uid);
        databaseReferenceSend.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 Sendname=(String) dataSnapshot.child("Name").getValue();
                 Sendsurname=(String) dataSnapshot.child("Surname").getValue();
                Sendusertype=(String) dataSnapshot.child("UserType").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });


        databaseReferenceSend.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Recievedname=(String) dataSnapshot.child("Name").getValue();
                RecievedSurname=(String) dataSnapshot.child("Surname").getValue();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                message=et_message.getText().toString();
                if(message.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Lütfen bir mesaj yazınız",Toast.LENGTH_LONG).show();
                }
                else
                {
                 final  String AuthUser=getIntent().getStringExtra("authİd");
                    Toast.makeText(getApplicationContext(),AuthUser,Toast.LENGTH_LONG).show();


                        databaseReferenceRecieved=database.getReference("Users").child(AuthUser);
                        databaseReferenceRecieved.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //AuthUser=getIntent().getStringExtra("Authid");
                               // assert  AuthUser!=null;
                               // Toast.makeText(getApplicationContext(),AuthUser,Toast.LENGTH_LONG).show();
                                String MessageKey=FirebaseDatabase.getInstance().getReference("Messages").push().getKey();
                                databaseReferenceRecieved=database.getReference("Users").child(AuthUser).child("Messages").child(MessageKey);

                               final Map<String, String> newMap = new HashMap<String, String>();
                                newMap.put("Message",Sendname+" "+Sendsurname+" "+"("+Sendusertype+")"+":"+" "+message);
                                databaseReferenceRecieved.setValue(newMap);

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                        Toast.makeText(getApplicationContext(),Recievedname+" "+RecievedSurname+"kişisine mesaj gönderildi!",Toast.LENGTH_LONG).show();



                }



            }
        });









    }
}
