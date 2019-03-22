package com.sourav.freechat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserTypeActivity extends AppCompatActivity {
    Button userOneButton, userTwoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);
        userOneButton = findViewById(R.id.userOne);
        userTwoButton = findViewById(R.id.userTwo);

        userOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: check user1 is online or offline
                Intent chat = new Intent(UserTypeActivity.this, MessagingActivity.class);
                chat.putExtra("User", "Sourav");
                startActivity(chat);
            }
        });

        userTwoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: check user2 is online or offline
                Intent chat = new Intent(UserTypeActivity.this, MessagingActivity.class);
                chat.putExtra("User", "Satya");
                startActivity(chat);

            }
        });

    }
}
