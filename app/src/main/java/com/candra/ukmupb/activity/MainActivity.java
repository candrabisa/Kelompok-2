package com.candra.ukmupb.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.candra.ukmupb.R;
import com.candra.ukmupb.fragment.fragment_home;
import com.candra.ukmupb.fragment.fragment_message;
import com.candra.ukmupb.fragment.fragment_search;
import com.candra.ukmupb.fragment.fragment_user;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = new fragment_home();
                    break;
                case R.id.navigation_search:
                    selectedFragment = new fragment_search();
                    break;
                case R.id.navigation_user:
                    selectedFragment = new fragment_user();
                    break;
                case R.id.navigation_message:
                    selectedFragment = new fragment_message();
                    break;
            }
            assert selectedFragment != null;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new fragment_home()).commit();
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(MainActivity.this, "Berhasil Logout.", Toast.LENGTH_LONG).show();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}
