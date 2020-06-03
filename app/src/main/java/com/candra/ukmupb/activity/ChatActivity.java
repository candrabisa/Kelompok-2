package com.candra.ukmupb.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.candra.ukmupb.R;
import com.candra.ukmupb.adapter.AdapterChat;
import com.candra.ukmupb.adapter.AdapterUser;
import com.candra.ukmupb.model.ModelChat;
import com.candra.ukmupb.model.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView imgProfil;
    ImageButton sendBtn;
    TextView tv_nama, tv_userStatus;
    EditText etMessage;

    private AdapterUser adapterUser;
    private List<ModelUser> userList;

    //deklarasi firebase
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDataRef;
    //deklarasi buat tahu kalau pesan udah dibaca atau tidak
    ValueEventListener isSeenListener;
    DatabaseReference userRefForisSeen;
    List<ModelChat> chatList;
    AdapterChat adapterChat;

    String hisUid;
    String myUid;
    String hisImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //init Views
        toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView = findViewById(R.id.chat_recyclerView);
        imgProfil = findViewById(R.id.imgUser_chat);
        sendBtn = findViewById(R.id.btn_sendMessage);
        tv_nama = findViewById(R.id.tvNama_Chat);
        tv_userStatus = findViewById(R.id.userStatusOnline);
        etMessage = findViewById(R.id.etMessage_Typing);

        //Layout (LinearLayout) untuk RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        //recyclerview properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //init firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDataRef = firebaseDatabase.getReference("users");

        //ketika diclik pada list cari akan mengambil info user tersebut dan pindah intent
        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUid");

        //cari user untuk mengambil datanya
        Query userQuery = userDataRef.orderByChild("email").equalTo(hisUid);
        //ambil foto dan nama user
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    //get data
                    String nama =""+ds.child("namalengkap").getValue();
                    hisImage = ""+ds.child("image").getValue();
                    //get value online status
                    String onlineStatus = ""+ds.child("onlineStatus").getValue();
                    if (onlineStatus.equals("online")){
                        tv_userStatus.setText(onlineStatus);
                    } else {
                        //convert dari timestamp
                        //convert timestamp to dd/mm/yyyy hh:mm pm/am
                        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                        calendar.setTimeInMillis(Long.parseLong(onlineStatus));
                        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
                        tv_userStatus.setText("Terakhir dilihat: "+dateTime);
                    }
                    //set data
                    tv_nama.setText(nama);
                    try {
                        Picasso.get().load(hisImage).placeholder(R.drawable.ic_child_care_black_24dp).into(imgProfil);
                    } catch (Exception e){
                        Picasso.get().load(R.drawable.ic_child_care_black_24dp).into(imgProfil);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etMessage.getText().toString().trim();
                if (TextUtils.isEmpty(message)){
                    //pesan kosong
                    Toast.makeText(ChatActivity.this, "tidak dapat mengirimkan pesan kosong", Toast.LENGTH_SHORT).show();
                } else {
                    //pesan tidak kosong
                    sendMessage(message);
                }
            }
        });
        readMessage();
        isSeenMessage();
    }

    private void isSeenMessage() {
        userRefForisSeen = FirebaseDatabase.getInstance().getReference("chats");
        isSeenListener = userRefForisSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)){
                        HashMap<String, Object> hashisSeenMap = new HashMap<>();
                        hashisSeenMap.put("isSeen", true);
                        ds.getRef().updateChildren(hashisSeenMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessage() {
        chatList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid) ||
                    chat.getReceiver().equals(hisUid) && chat.getSender().equals(myUid)){
                        chatList.add(chat);
                    }
                    //adapter
                    adapterChat = new AdapterChat(ChatActivity.this, chatList, hisImage);
                    adapterChat.notifyDataSetChanged();
                    //set adapter to recyclerview
                    recyclerView.setAdapter(adapterChat);
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String message) {

        DatabaseReference databaseReference = firebaseDatabase.getReference();
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver", hisUid);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("isSeen", false);
        databaseReference.child("chats").push().setValue(hashMap);

        //reset edittext setelah sending message
        etMessage.setText("");
    }

    private void checkOnlineStatus(String status){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);
        //update value of onlnestatus pf current user
        dbRef.updateChildren(hashMap);
    }


    @Override
    protected void onStart() {
        checkUserStatus();
        checkOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //get timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());
        //set offline with last seen time stamp
        checkOnlineStatus(timestamp);
        userRefForisSeen.removeEventListener(isSeenListener);
    }

    @Override
    protected void onResume() {
        //set online
        checkOnlineStatus("online");
        super.onResume();
    }

    private void checkUserStatus(){
        FirebaseUser fUser = mAuth.getCurrentUser();
        if (fUser !=null){
            //ini adalah cara mengecek status online pengguna melalui user id
            myUid = fUser.getUid();

        }else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_navbar, menu);
        menu.findItem(R.id.nav_search).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.btn_logout){
            mAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }

}
