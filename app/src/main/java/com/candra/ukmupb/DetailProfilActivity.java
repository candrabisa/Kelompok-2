package com.candra.ukmupb;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.candra.ukmupb.fragment.MessageFragment;
import com.candra.ukmupb.fragment.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;


public class DetailProfilActivity extends AppCompatActivity {
    TextView namalengkap, email, nim, anggotaukm, password;
    FirebaseAuth FAuth;
    FirebaseFirestore FStore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user);
        namalengkap = findViewById(R.id.tv_nama_user);
        email = findViewById(R.id.tv_email_user);
        nim = findViewById(R.id.tv_nim_user);
        anggotaukm = findViewById(R.id.tv_oragnisasi_user);
        password = findViewById(R.id.tv_password_user);

        FAuth = FirebaseAuth.getInstance();
        FStore = FirebaseFirestore.getInstance();

        userId = Objects.requireNonNull(FAuth.getCurrentUser()).getUid();

        DocumentReference documentReference = FStore.collection("users").document(userId);
        documentReference.addSnapshotListener(DetailProfilActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                assert documentSnapshot != null;
                namalengkap.setText(documentSnapshot.getString("namalengkap"));
                email.setText(documentSnapshot.getString("email"));
                nim.setText(documentSnapshot.getString("nim"));
                anggotaukm.setText(documentSnapshot.getString("anggotaukm"));
                password.setText(documentSnapshot.getString("password"));
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_user);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigation_user:
                        return true;
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
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
