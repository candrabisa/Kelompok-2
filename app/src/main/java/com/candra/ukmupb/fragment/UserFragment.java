package com.candra.ukmupb.fragment;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.candra.ukmupb.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class UserFragment extends AppCompatActivity {

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}
