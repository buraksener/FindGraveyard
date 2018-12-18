package com.sener35gmail.burak.myapplication;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowDeceasedComments extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
FirebaseDatabase database;
DatabaseReference reference;
DatabaseReference reference2;
DatabaseReference reference3;
DatabaseReference reference4;
DatabaseReference users;
    FirebaseAuth auth;
EditText ed_comment;
Button Docomment;
ImageButton back;
String Comment;
String Comment2;
String Permit_comment;



String CurrentUserName;
String CurrentUserSurname;
String CommentKey;
String Usertype;
String authenticatedUser;
ListView mList;
Switch switch_comments;
RelativeLayout relativeLayout;
AlertDialog alertDialog;



    public static String RISULTATO = "RISULTATO";
    private SwipeRefreshLayout swipeRefreshLayout;


    ArrayAdapter<String> arrayAdapter;
    ArrayAdapter<String> arrayAdapter2;
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String>  keyList=new ArrayList<>();



    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_deceased_comments);

        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

        database=FirebaseDatabase.getInstance();
        reference=database.getReference("Deceaceds");

        ed_comment=(EditText) findViewById(R.id.Comment);
        Docomment=(Button) findViewById(R.id.Docomment);
        back=(ImageButton) findViewById(R.id.back);
        mList=(ListView) findViewById(R.id.ListViewComments);
        switch_comments=(Switch) findViewById(R.id.switch_comment);
        swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swipe_down_refresh);


        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_light,android.R.color.holo_green_light,android.R.color.holo_red_light,android.R.color.holo_orange_light);






        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
        arrayAdapter2=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,keyList);




        auth= FirebaseAuth.getInstance();
       final String uid=(String) auth.getCurrentUser().getUid();

       relativeLayout=(RelativeLayout) findViewById(R.id.myRelativeLayout);


       users=FirebaseDatabase.getInstance().getReference("Users").child(uid);
       ValueEventListener eventListener=new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
                CurrentUserName=(String) dataSnapshot.child("Name").getValue();
                CurrentUserSurname=(String) dataSnapshot.child("Surname").getValue();
                Usertype=(String) dataSnapshot.child("UserType").getValue();
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       };
       users.addListenerForSingleValueEvent(eventListener);
        switch_comments.setVisibility(View.GONE);


            Intent intent=getIntent();
            final String  key;
        key = intent.getExtras().getString("key");




        assert key != null;
        reference2=database.getReference("Deceaceds").child(key);
        reference3=database.getReference("Deceaceds").child(key);
        reference4=database.getReference("Deceaceds").child(key);


        reference3=FirebaseDatabase.getInstance().getReference("Deceaceds").child(key).child("User Permits");






        switch_comments.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                reference3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {



                        String Current_permit=(String) dataSnapshot.child("CommentPermit").getValue();
                        String g="GONE";
                        String v="VISIBLE";


                        //assert Current_permit != null;
                        if (Current_permit != null) {
                            if(Current_permit.equals(g))
                            {
                                mList.setVisibility(View.INVISIBLE);
                                switch_comments.setChecked(true);
                            }
                            else if(Current_permit.equals(v))
                            {
                                mList.setVisibility(View.VISIBLE);
                                switch_comments.setChecked(false);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                if(switch_comments.isChecked() && uid.equals(authenticatedUser))
                {
                    openDialog();






                }

                else
                {
                   // openDialog();
                    alertDialog.cancel();

                }
            }
        });

        reference=FirebaseDatabase.getInstance().getReference("Deceaceds").child(key).child("Comments");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot childsnapshot:dataSnapshot.getChildren()) {


                    Comment2 = (String) childsnapshot.child("Comment").getValue();
                    keyList.add(childsnapshot.getKey());
                    names.add(Comment2 + "\n");


                    // Toast.makeText(getApplicationContext(),Comment2,Toast.LENGTH_LONG).show();
                }
                arrayAdapter.notifyDataSetChanged();





            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });






        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
                authenticatedUser=(String) dataSnapshot.child("AuthenticatedUserOfDeceased").getValue();
               if(uid.equals(authenticatedUser))
               {
                   switch_comments.setVisibility(View.VISIBLE);

                   mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                       @Override
                       public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {

                           AlertDialog.Builder builder=new AlertDialog.Builder(ShowDeceasedComments.this,R.style.MyDialog);
                           builder.setTitle("Uyarı");
                           builder.setMessage("Yorumu silmek istiyor musunuz?");
                           builder.setCancelable(true);

                           builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                   dialogInterface.cancel();

                               }
                           });

                           builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                    names.remove(position);
                                   arrayAdapter.notifyDataSetChanged();
                                   reference.child(keyList.get(position)).removeValue();
                                  // String key=keyList.get(position);
                                   //Toast.makeText(getApplicationContext(),key,Toast.LENGTH_SHORT).show();
                                   keyList.remove(position);
                                   View parentLayout = findViewById(android.R.id.content);


                                   Snackbar snackbar=Snackbar.make(parentLayout,"Yorum silindi!!",Snackbar.LENGTH_LONG);
                                   snackbar.show();


                               }
                           });
                           final AlertDialog alertDialog=builder.create();

                           alertDialog.show();

                           Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                           Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

                           positiveButton.setTextColor(Color.parseColor("#FF0B8B42"));
                           positiveButton.setBackgroundColor(Color.parseColor("#FFE1FCEA"));

                           negativeButton.setTextColor(Color.parseColor("#FFFF0400"));
                           negativeButton.setBackgroundColor(Color.parseColor("#FFFCB9B7"));
                           getWindow().setBackgroundDrawableResource(R.color.white);

                       }
                   });

               }

               else
               {
                   switch_comments.setVisibility(View.INVISIBLE);
               }

           }

           public void onCancelled(DatabaseError databaseError) {

           }

       });



        CommentKey=database.getReference("Comments").push().getKey();
        Docomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Comment=ed_comment.getText().toString().trim();
                if(Comment.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Lütfen bir yorum giriniz",Toast.LENGTH_LONG).show();
                }
                else
                {
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            reference=FirebaseDatabase.getInstance().getReference("Deceaceds").child(key).child("Comments").child(CommentKey);
                            final Map<String, String> newMap = new HashMap<>();
                            newMap.put("Comment",CurrentUserName+" "+CurrentUserSurname+"("+Usertype+")"+":"+" "+Comment);
                            reference.setValue(newMap);
                            Intent intent = getIntent();


                          startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    Toast.makeText(ShowDeceasedComments.this, "Yorumunuz başarılı bir şekilde paylaşıldı.", Toast.LENGTH_LONG).show();
                }


                ed_comment.setText("");

                //Intent intent = getIntent();
                //finish();
                //startActivity(intent);
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }


            }
        });

        arrayAdapter.addAll(names);
        mList.setAdapter(arrayAdapter);
      // onBackPressed();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



    }


    private void openDialog() {
       final AlertDialog.Builder builder=new AlertDialog.Builder(ShowDeceasedComments.this,R.style.MyDialog);
      builder.setTitle("Uyarı");
       builder.setMessage("Yorumlar Kapatılsın mı?");
       builder.setCancelable(true);
       builder.setNegativeButton("Evet", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
               dialogInterface.cancel();


               switch_comments.setChecked(true);
                   mList.setVisibility(View.GONE);
                   Permit_comment="GONE";
                   reference3.addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshot) {
                           final Map<String, String> newMap = new HashMap<>();
                           newMap.put("CommentPermit",Permit_comment);
                           reference3.setValue(newMap);

                       }

                       @Override
                       public void onCancelled(DatabaseError databaseError) {

                       }
                   });


           }
       });
       builder.setPositiveButton("Hayır", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {


                   switch_comments.setChecked(false);
                   mList.setVisibility(View.VISIBLE);
                   Permit_comment="VISIBLE";
                   reference3.addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshot) {
                           final Map<String, String> newMap = new HashMap<>();
                           newMap.put("CommentPermit",Permit_comment);
                           reference3.setValue(newMap);

                       }

                       @Override
                       public void onCancelled(DatabaseError databaseError) {

                       }
                   });




           }
       });
        alertDialog=builder.create();
       alertDialog.show();

        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

        positiveButton.setTextColor(Color.parseColor("#FF0B8B42"));
        positiveButton.setBackgroundColor(Color.parseColor("#FFE1FCEA"));

        negativeButton.setTextColor(Color.parseColor("#FFFF0400"));
        negativeButton.setBackgroundColor(Color.parseColor("#FFFCB9B7"));
        getWindow().setBackgroundDrawableResource(R.color.white);


    }




    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();


    }

    @Override
    public void onRefresh() {

        new Handler().postDelayed(new Runnable() {

            @Override public void run() {

                swipeRefreshLayout.setRefreshing(false);

            }

        }, 5000);

        Intent intent = getIntent();
        finish();

        startActivity(intent);


    }
}
