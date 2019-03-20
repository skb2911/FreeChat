package com.sourav.freechat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagingActivity extends AppCompatActivity {

    CircleImageView profileImage;
    TextView username;

    DatabaseReference databaseReference;

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



    }
}
