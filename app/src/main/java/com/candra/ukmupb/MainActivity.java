package com.candra.ukmupb;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.candra.ukmupb.fragment.SettingsFragment;
import com.candra.ukmupb.fragment.HomeFragment;
import com.candra.ukmupb.fragment.MessageFragment;
import com.candra.ukmupb.fragment.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    //    public static final String URL = "http://10.10.23.142:8000/";
//    public static final String URL = "http://192.168.43.102:8000/";
    public static final String URL = "http://192.168.0.11/8000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigation_user:
                        startActivity(new Intent(getApplicationContext(), DetailProfilActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_home:
                        return true;
                    case R.id.navigation_message:
                        startActivity(new Intent(getApplicationContext(), MessageActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_settings:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }
}
