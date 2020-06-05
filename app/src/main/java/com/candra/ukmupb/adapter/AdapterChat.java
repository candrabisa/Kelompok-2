package com.candra.ukmupb.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.LayoutInflaterCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.candra.ukmupb.R;
import com.candra.ukmupb.model.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder>{

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TUPE_RIGHT=1;
    Context context;
    List<ModelChat>chatList;
    String imageUrl;

    //firebase
    FirebaseUser fUser;

    public AdapterChat(Context context, List<ModelChat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == MSG_TUPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.baris_chat_kanan, viewGroup, false);
            return new MyHolder(view);
        }else {
                View view = LayoutInflater.from(context).inflate(R.layout.baris_char_kiri, viewGroup, false);
                return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, final int position) {
        //get data
        String message = chatList.get(position).getMessage();
        String timestamp = chatList.get(position).getTimestamp();

        //convert timestamp to dd/mm/yyyy hh:mm pm/am
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        //set data
        myHolder.tvMessage.setText(message);
        myHolder.tvTime.setText(dateTime);
        try {
            Picasso.get().load(imageUrl).into(myHolder.imageProfile);
        }
        catch (Exception e){

        }
        // klik untuk menampilkan dialog delete
        myHolder.messageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Menarik Pesan");
                builder.setMessage("Apakah yakin pesan ingin ditarik?");
                //button hapus
                builder.setPositiveButton("Tarik", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMessage(position);
                    }
                });
                //Button Cancel
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                //munculkan dialog
                builder.create().show();
            }
        });

        //set seen/delivered status of message
        if (position==chatList.size()-1){
            if (chatList.get(position).isSeen()){
                myHolder.tv_isSeen.setText("Dilihat");
            }
            else {
                myHolder.tv_isSeen.setText("Terkirim");
            }
        }
        else {
            myHolder.tv_isSeen.setVisibility(View.GONE);
        }
    }

    private void deleteMessage(int position) {
        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String msgTimeStamp = chatList.get(position).getTimestamp();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("chats");
        Query query = dbRef.orderByChild("timestamp").equalTo(msgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    if (ds.child("sender").getValue().equals(myUid)){
                        //ds.getRef().removeValue();

                        HashMap<String, Object> hashMap= new HashMap<>();
                        hashMap.put("message", "Pesan ini telah ditarik!");
                        ds.getRef().updateChildren(hashMap);
                        Toast.makeText(context, "Pesan ditarik", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(context, "Hanya bisa menghapus pesan anda", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //get currently sign in user
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(fUser.getUid())){
            return MSG_TUPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    //views holder class
    class MyHolder extends RecyclerView.ViewHolder{

        //views
        ImageView imageProfile;
        TextView tvMessage, tvTime, tv_isSeen;
        LinearLayout messageLayout; //untuk klik listener delete

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            imageProfile = itemView.findViewById(R.id.profil_message);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            tv_isSeen = itemView.findViewById(R.id.tvDibaca);
            messageLayout = itemView.findViewById(R.id.messageLayout);

        }
    }
}
