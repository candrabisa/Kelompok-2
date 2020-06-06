package com.candra.ukmupb.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.candra.ukmupb.R;
import com.candra.ukmupb.activity.ChatActivity;
import com.candra.ukmupb.model.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.List;

//Created by Candra Billy Sagita

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.MyHolder> {

    Context context;
    List<ModelUser>userList;
    private ProgressBar progressbar;

    //constructor


    public AdapterUser(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_user, viewGroup, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int position) {
        //get data
        final String hisUID = userList.get(position).getUid();
        String userImage = userList.get(position).getImage();
        final String userNIM = userList.get(position).getNim();
        final String userName= userList.get(position).getNamalengkap();
        final String userOrganisasi = userList.get(position).getAnggotaukm();

        //set data
        myHolder.tv_nim_Search.setText(userNIM);
        myHolder.tv_nama_Search.setText(userName);
        myHolder.tv_organisasi_Search.setText(userOrganisasi);
        try {
            Picasso.get().load(userImage)
                    .placeholder(R.drawable.ic_child_care_black_24dp)
                    .into(myHolder.imgUser_s);
        }
        catch (Exception e){

        }
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid", hisUID);
                context.startActivity(intent);
            }
        });
       
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        ImageView imgUser_s;
        TextView tv_nama_Search, tv_nim_Search, tv_organisasi_Search;

        MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            imgUser_s = itemView.findViewById(R.id.imgUser_search);
            tv_nim_Search = itemView.findViewById(R.id.tvNIM_Search);
            tv_nama_Search = itemView.findViewById(R.id.tvNama_Search);
            tv_organisasi_Search = itemView.findViewById(R.id.tvOrganisasi_Search);

        }
    }
}
