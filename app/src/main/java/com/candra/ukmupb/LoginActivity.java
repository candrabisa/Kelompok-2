package com.candra.ukmupb;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;

import android.content.Intent;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.candra.ukmupb.fragment.UserFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword;
    private ProgressBar progressBar;

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
           String password = etPassword.getText().toString();

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
                                       Toast.makeText(LoginActivity.this, "Login success.",
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
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }
}
