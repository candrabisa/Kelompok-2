package com.candra.ukmupb.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.candra.ukmupb.notifikasi.APIService;
import com.candra.ukmupb.notifikasi.Client;
import com.candra.ukmupb.notifikasi.Data;
import com.candra.ukmupb.notifikasi.Response;
import com.candra.ukmupb.notifikasi.Sender;
import com.candra.ukmupb.notifikasi.Token;
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
import androidx.multidex.MultiDexApplication;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

//Created by Candra Billy Sagita

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

    APIService apiService;
    boolean notify = false;

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

        //buat api service
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

        //init firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDataRef = firebaseDatabase.getReference("users");

        //ketika diclik pada list cari akan mengambil info user tersebut dan pindah intent
        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUid");

        //cari user untuk mengambil datanya
        Query userQuery = userDataRef.orderByChild("Uid").equalTo(hisUid);
        //ambil foto dan nama user
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    //get data
                    String nama =""+ds.child("namalengkap").getValue();
                    hisImage = ""+ds.child("image").getValue();
                    String typingStatus = ""+ds.child("typingTo").getValue();

                    //check typing status
                    if (typingStatus.equals(myUid)){
                        tv_userStatus.setText("Typing...");
                    }
                    else{
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
                notify = true;
                String message = etMessage.getText().toString().trim();
                if (TextUtils.isEmpty(message)){
                    //pesan kosong
                    Toast.makeText(ChatActivity.this, "tidak dapat mengirimkan pesan kosong", Toast.LENGTH_SHORT).show();
                } else {
                    //pesan tidak kosong
                    sendMessage(message);
                }
                //reset edittext setelah sending message
                etMessage.setText("");
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
        //check edit text change listener
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() ==0){
                    checkTypingStatus("noOne");
                }
                else {
                    checkTypingStatus(hisUid); //uid receiver
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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

    private void sendMessage(final String message) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver", hisUid);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("isSeen", false);
        databaseReference.child("chats").push().setValue(hashMap);


        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("users").child(myUid);
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUser users = dataSnapshot.getValue(ModelUser.class);

                if (notify){
                    sendNotification(hisUid, users.getNamalengkap(), message);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //create chatlist node/child in firebase database
        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(myUid)
                .child(hisUid);
        chatRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef1.child("id").setValue(myUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(hisUid)
                .child(myUid);
        chatRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef1.child("id").setValue(hisUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(final String hisUid, final String namaLengkap, final String message) {
    DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
    Query query = allTokens.orderByKey().equalTo(hisUid);
    query.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot ds : dataSnapshot.getChildren()){
                Token token = ds.getValue(Token.class);
                Data data = new Data(myUid, namaLengkap+":"+message, "New Message", hisUid, R.drawable.ic_default);

                Sender sender = new Sender(data, token.getToken());
                apiService.sendNotification(sender)
                        .enqueue(new Callback<Response>() {
                            @Override
                            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                Toast.makeText(ChatActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<Response> call, Throwable t) {

                            }
                        });
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    }

    private void checkOnlineStatus(String status){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);
        //update value of onlnestatus pf current user
        dbRef.updateChildren(hashMap);
    }

    private void checkTypingStatus(String typing){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo", typing);
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
        checkTypingStatus("noOne");
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
