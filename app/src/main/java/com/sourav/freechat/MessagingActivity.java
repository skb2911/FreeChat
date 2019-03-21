package com.sourav.freechat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagingActivity extends AppCompatActivity {

    CircleImageView profileImage;
    TextView username;

    DatabaseReference databaseReference;
    Intent intent;

    EditText textSend;
    ImageButton buttonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        profileImage = findViewById(R.id.profileImage);
        username =findViewById(R.id.username);

        textSend = findViewById(R.id.textSend);
        buttonSend = findViewById(R.id.buttonSend);

        intent = getIntent();
        final String userType = intent.getStringExtra("User");

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = textSend.getText().toString();
                if(!msg.equals("")){
                    if(userType.equals("UserOne")){
                        sendMessage("Sourav","Satya", msg);
                    }
                    else if (userType.equals("UserTwo")){
                        sendMessage("Satya", "Sourav", msg);
                    }
                    else
                    {
                        Toast.makeText(MessagingActivity.this, "Error occured!!!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(MessagingActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                textSend.setText("");
            }
        });




        if(userType.equals("UserOne")){
            getSupportActionBar().setTitle("Satya");
        }
        else if(userType.equals("UserTwo")){
            getSupportActionBar().setTitle("Sourav");
        }



    }

    public void sendMessage (String sender, String receiver, String message){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message",message);

        databaseReference.child("Chats").push().setValue(hashMap);
    }
}
