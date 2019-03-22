package com.sourav.freechat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.sourav.freechat.Adapters.MessageAdapter;
import com.sourav.freechat.Model.Chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagingActivity extends AppCompatActivity {

    CircleImageView profileImage;
    TextView username;

    DatabaseReference databaseReference;
    StorageTask mUploadTask;
    Intent intent;

    EditText textSend;
    ImageButton buttonSend, imageButton;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;

    String userType;
    String receiver;

    ProgressBar progressBar;

    private static final int GALLERY_PIC = 1;

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
                Toast.makeText(MessagingActivity.this, "Here", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView = findViewById(R.id.recyclerViewChats);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profileImage = findViewById(R.id.profileImage);
        username =findViewById(R.id.username);

        profileImage.setImageResource(R.mipmap.ic_launcher);

        textSend = findViewById(R.id.textSend);
        buttonSend = findViewById(R.id.buttonSend);
        imageButton = findViewById(R.id.imageButton);

        intent = getIntent();
        userType = intent.getStringExtra("User");

        progressBar = findViewById(R.id.progressBar);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = textSend.getText().toString();
                if(!msg.equals("")){
                    if(userType.equals("Sourav")){
                        sendMessage("Sourav","Satya", msg);
                    }
                    else if (userType.equals("Satya")){
                        sendMessage("Satya", "Sourav", msg);
                    }
                    else
                    {
                        Toast.makeText(MessagingActivity.this, "Error occurred!!!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(MessagingActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                textSend.setText("");
            }
        });

        if(userType.equals("Sourav")){
            username.setText("Satya");
            receiver = "Satya";
        }
        else if(userType.equals("Satya")){
            username.setText("Sourav");
            receiver = "Sourav";
        }

        readMessage(userType, receiver);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(MessagingActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PIC);

                }else {

                    ActivityCompat.requestPermissions(MessagingActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);

                }

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PIC);

        }else
            Toast.makeText(MessagingActivity.this, "please provide permission", Toast.LENGTH_SHORT).show();

    }

    public void sendMessage (String sender, String receiver, String message){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message",message);
        hashMap.put("image","null");
        hashMap.put("message_type", "text");

        databaseReference.child("Chats").push().setValue(hashMap);
    }

    public void readMessage (final String myId, final String userId){
        mChat = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myId) && chat.getSender().equals(userId) || chat.getReceiver().equals(userId) && chat.getSender().equals(myId)){
                        mChat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessagingActivity.this, mChat, userType);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getFileExtention(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PIC && requestCode == RESULT_OK){
            Uri imageUri = data.getData();

            if(imageUri !=null){
                progressBar.setVisibility(View.VISIBLE);

                final StorageReference storageReference1 = FirebaseStorage.getInstance().getReference().child("Uploads").child(System.currentTimeMillis()+"."+getFileExtention(imageUri));
                mUploadTask = storageReference1.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        storageReference1.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                //String downloadUri = task.getResult().getDownloadUrl().toString();
                                String id = databaseReference.push().getKey();

                                HashMap<String,Object> hashMap = new HashMap<>();
                                hashMap.put("sender",userType);
                                hashMap.put("receiver", receiver);
                                hashMap.put("message","null");
                                hashMap.put("image",id);
                                hashMap.put("message_type", "image");
//                                databaseReference.child(id).setValue(task.getResult().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()){
//                                            Toast.makeText(MessagingActivity.this, "File successfully uploaded", Toast.LENGTH_SHORT).show();
//                                        }
//
//                                        else{
//                                            Toast.makeText(MessagingActivity.this, "File not successfully uploaded", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                });

                                databaseReference.child("Chats").push().setValue(hashMap);
                                progressBar.setVisibility(View.GONE);
                            }
                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(MessagingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });

            }
            else {
                Toast.makeText(this, "No Image selected", Toast.LENGTH_SHORT).show();
            }


        }

    }
}
