package com.sener35gmail.burak.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.github.library.bubbleview.BubbleTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatMessageUsertoUser extends AppCompatActivity {

    private FirebaseListAdapter<ChatMessage> adapter;
    private static  int SIGN_IN_REQUEST_CODE=1;
    RelativeLayout activity_chat_message;
    FloatingActionButton fab;
    FirebaseAuth auth;
    String uid;
    FirebaseDatabase database;
    DatabaseReference reference;
    String email;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SIGN_IN_REQUEST_CODE)
        {
            if(resultCode==RESULT_OK)
            {
                Snackbar.make(activity_chat_message,"Successfull signed in,Wellcome!",Snackbar.LENGTH_SHORT).show();
                DisplayChatMessage();
            }
            else
            {
                Snackbar.make(activity_chat_message,"We could not sign you in.Please try it again later!",Snackbar.LENGTH_SHORT).show();
            finish();
            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message_userto_user);
        activity_chat_message=(RelativeLayout) findViewById(R.id.activity_chat_message);
        fab=(FloatingActionButton) findViewById(R.id.fab);
        auth= FirebaseAuth.getInstance();
        uid=auth.getCurrentUser().getUid();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference("Users").child(uid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 email=(String) dataSnapshot.child("emailAdress").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),SIGN_IN_REQUEST_CODE);

        }
        else
        {
            Snackbar.make(activity_chat_message,"Wellcome"+" "+FirebaseAuth.getInstance().getCurrentUser().getEmail(),Snackbar.LENGTH_SHORT).show();
            DisplayChatMessage();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input=(EditText) findViewById(R.id.input);
                FirebaseDatabase.getInstance().getReference("Users").child("Messages").push().setValue(new ChatMessage(input.getText().toString(),email));
                input.setText("");
            }
        });





    }

    private void DisplayChatMessage() {
        ListView listOfMessage = (ListView)findViewById(R.id.list_of_message);
        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class, R.layout.list_item, FirebaseDatabase.getInstance().getReference("Users").child("Messages")) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView messageText, messageUser, messageTime;
                messageText = (BubbleTextView) v.findViewById(R.id.message_text);
                messageUser = (TextView) v.findViewById(R.id.message_user);
                messageTime = (TextView) v.findViewById(R.id.message_time);

                messageText.setText(model.getMessageText());
                messageUser.setText(email);
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));

            }
        };
        listOfMessage.setAdapter(adapter);

    }
}
