package com.candra.kelompok2.activity;


import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.candra.kelompok2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private EditText regNama, regEmail,regUsername, regPassword;
    private Button btn_Signup;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }
    private void initView(){
        regNama = findViewById(R.id.etName_r);
        regEmail = findViewById(R.id.etEmail_r);
        regUsername = findViewById(R.id.etUsername_r);
        regPassword = findViewById(R.id.etPassword_r);
        btn_Signup = findViewById(R.id.btnDaftar_r);
        auth = FirebaseAuth.getInstance();
    }
    private void registerUser(){
        btn_Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String namaUser = regNama.getText().toString().trim();
                String emailUser = regEmail.getText().toString().trim();
                String usernameUser = regUsername.getText().toString().trim();
                String passwordUser = regPassword.getText().toString().trim();

                //Validasi jika kolom kosong
                if (emailUser.isEmpty()){
                    regEmail.setError("Email tidak boleh kosong");
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(emailUser).matches()){
                    regEmail.setError("Email tidak valid");
                }
                else if (passwordUser.isEmpty()){
                        regPassword.setError("Password tidak boleh kosong");
                }
                else if (namaUser.isEmpty()){
                    regNama.setError("Nama tidak boleh kosong");
                }
                else if (usernameUser.isEmpty()){
                    regUsername.setError("Username tidak boleh kosong");
                }
                else if (passwordUser.length() < 6 ){
                    regPassword.setError("Password minimal terdiri 6 karakter");
                }
                else {
                    auth.createUserWithEmailAndPassword(emailUser,passwordUser)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()){
                                        Toast.makeText(SignUpActivity.this, "Register Gagal Karena " +task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                    }
                                }
                            });
                }
            }
        });
    }
}

