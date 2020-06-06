package com.candra.ukmupb.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewParent;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.candra.ukmupb.R;
import com.candra.ukmupb.activity.MainActivity;
import com.candra.ukmupb.adapter.AdapterUser;
import com.candra.ukmupb.model.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

//Created by Candra Billy Sagita

/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_search extends Fragment {

    private FirebaseAuth mAauth;
    private FirebaseUser fUsers;
    ProgressBar progressBar;
    private RecyclerView recyclerView;
    private AdapterUser adapterUser;
    private List<ModelUser> userList;


    public fragment_search() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // init firebase
        mAauth = FirebaseAuth.getInstance();
        fUsers = mAauth.getCurrentUser();

        //init recylerview
        recyclerView = view.findViewById(R.id.user_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //init userlist
        userList = new ArrayList<>();

        //init get all user
        getAllUser();

        return view;
    }

    private void getAllUser() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    assert modelUser != null;
                    assert fUsers != null;
                    Log.d("Cek1", String.valueOf(modelUser.getUid()));
                    Log.d("Cek2", String.valueOf(fUsers.getUid()));
                    if (!modelUser.getUid().equals(fUsers.getUid())){
                        userList.add(modelUser);
                        }
                    //adapter
                    adapterUser = new AdapterUser(getActivity(), userList);
                    recyclerView.setAdapter(adapterUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void searchUsers(final String s) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    Log.d("Cek1", modelUser.getEmail());
                    Log.d("Cek2", fUsers.getUid());
                    if (!modelUser.getEmail().equals(fUsers.getUid())){
                        if (modelUser.getNamalengkap().toLowerCase().contains(s.toLowerCase()) ||
                                modelUser.getNim().toLowerCase().contains(s.toLowerCase()) ||
                                modelUser.getAnggotaukm().toLowerCase().contains(s.toLowerCase())){
                            userList.add(modelUser);
                        }
                    }
                    //adapter
                    adapterUser = new AdapterUser(getActivity(), userList);
                    adapterUser.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserStatus(){
        FirebaseUser fUser = mAauth.getCurrentUser();
        if (fUser !=null){

        }else {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.action_navbar, menu);

        //SearchView
        MenuItem item = menu.findItem(R.id.nav_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        //SearchListener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query.trim())) {
                    searchUsers(query);
                } else {
                    getAllUser();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if(!query.equals("")) {
                    searchUsers(query);
                } else {
                    getAllUser();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //recycler
        RecyclerView recyclerView = view.findViewById(R.id.user_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // define an adapter
        adapterUser = new AdapterUser(null, userList);
        recyclerView.setAdapter(adapterUser);
    }


}
