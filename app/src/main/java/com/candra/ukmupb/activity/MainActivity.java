package com.candra.ukmupb.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.candra.ukmupb.R;
import com.candra.ukmupb.fragment.ChatListFragment;
import com.candra.ukmupb.fragment.fragment_home;
import com.candra.ukmupb.fragment.fragment_message;
import com.candra.ukmupb.fragment.fragment_search;
import com.candra.ukmupb.fragment.fragment_user;
import com.candra.ukmupb.notifikasi.Token;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

//Created by Candra Billy Sagita

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    String myUid;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    selectedFragment = new fragment_search();
                    break;
                case R.id.navigation_user:
                    selectedFragment = new fragment_user();
                    break;
                case R.id.navigation_message:
                    selectedFragment = new ChatListFragment();
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
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new fragment_search()).commit();

        checkUserStatus();

        //update token
        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    public void updateToken(String token){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        dbRef.child(myUid).setValue(mToken);

    }

    private void checkUserStatus(){
        FirebaseUser fUser = mAuth.getCurrentUser();
        if (fUser !=null){
            //ini adalah cara mengecek status online pengguna melalui user id
            myUid = fUser.getUid();

            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", myUid);
            editor.apply();
        }else {

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onPause();
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(MainActivity.this, "Berhasil Logout.", Toast.LENGTH_LONG).show();
        finish();
    }
}
