package com.candra.ukmupb.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;

import android.content.Intent;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.candra.ukmupb.R;
import com.candra.ukmupb.fragment.fragment_user;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//Created by Candra Billy Sagita

public class LoginActivity extends AppCompatActivity {

    private long BackpressedTime;

    private Boolean exit = false;

    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword;
    private ProgressBar progressBar;

    String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        progressBar = findViewById(R.id.progressBar);

        TextView btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goRegister = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(goRegister);
                finish();
            }
        });
        TextView tvLupaPass = findViewById(R.id.tv_lupa_password);
        tvLupaPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goReset = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(goReset);
                finish();
            }
        });
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();

                if (email.equals("")){
                    Toast.makeText(LoginActivity.this, "Anda belum memasukkan email", Toast.LENGTH_SHORT).show();
                    return;
                } else if (password.equals("")){
                    Toast.makeText(LoginActivity.this, "Anda belum memasukkan password", Toast.LENGTH_SHORT).show();
                    return;

                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    if (task.isSuccessful()) {
                                        if (Objects.requireNonNull(mAuth.getCurrentUser()).isEmailVerified()) {
                                            Toast.makeText(LoginActivity.this, "Login Berhasil.",
                                                    Toast.LENGTH_SHORT).show();

                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Anda belum melakukan verifikasi email, Silahkan verify email dahulu",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                        Toast.makeText(LoginActivity.this, " Email belum terdaftar",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(LoginActivity.this, "Email atau password salah!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                }

            }
        });
    }
    private void checkUserStatus(){
        FirebaseUser fUser = mAuth.getCurrentUser();
        if (fUser !=null){
            //ini adalah cara mengecek status online pengguna melalui user id
            myUid = fUser.getUid();

        }else {

        }
    }
    @Override
    public void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(() -> exit = false, 3 * 1000);
        }
    }
}

