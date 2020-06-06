package com.candra.ukmupb.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.candra.ukmupb.R;
import com.candra.ukmupb.activity.ChatActivity;
import com.candra.ukmupb.model.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

//Created by Candra Billy Sagita

public class AdapterChatlist extends RecyclerView.Adapter<AdapterChatlist.MyHolder>{

    Context context;
    List<ModelUser> userList;
    private HashMap<String, String> lastMessageMap;

    //contructor
    public AdapterChatlist(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
        lastMessageMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.baris_charlist, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        final String hisUid = userList.get(position).getUid();
        String userImage = userList.get(position).getImage();
        String userName = userList.get(position).getNamalengkap();
        String lasMessage = lastMessageMap.get(hisUid);

        //set data
        holder.nameTv.setText(userName);
        if (lasMessage==null || lasMessage.equals("default")){
            holder.lastMessageTv.setVisibility(View.GONE);
        } else {
            holder.lastMessageTv.setVisibility(View.VISIBLE);
            holder.lastMessageTv.setText(lasMessage);
        }
        try {
            Picasso.get().load(userImage).placeholder(R.drawable.ic_child_care_black_24dp).into(holder.profileIv);
        }
        catch (Exception e){
            Picasso.get().load(R.drawable.ic_child_care_black_24dp).into((holder.profileIv));
        }
        //set online of other users in chatlist
        if (userList.get(position).getOnlineStatus().equals("online")){
            //online
            holder.onlineStatusIv.setImageResource(R.drawable.circle_online);

        }
        else {
            //offline
           holder.onlineStatusIv.setImageResource(R.drawable.circle_offline);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid", hisUid);
                context.startActivity(intent);
            }
        });
    }

    public void setLastMessageMap(String userId, String lasMessage){
        lastMessageMap.put(userId, lasMessage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        //views of baris_chatlist.xml
        ImageView profileIv, onlineStatusIv;
        TextView nameTv, lastMessageTv;

        public MyHolder (@NonNull View itemView){
            super(itemView);

            //init views
            profileIv = itemView.findViewById(R.id.ivProfile_Chatlist);
            onlineStatusIv = itemView.findViewById(R.id.onlineStatusIv);
            nameTv = itemView.findViewById(R.id.tvNama_Chatlist);
            lastMessageTv = itemView.findViewById(R.id.tvLast_Message);
        }
    }
}
