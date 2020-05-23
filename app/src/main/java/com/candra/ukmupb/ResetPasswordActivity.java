package com.candra.ukmupb;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Candra Billy Sagita 22/05/2020
 */

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText inputEmail;
    private Button btnKirim;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        inputEmail = findViewById(R.id.etEmail_F);
        btnKirim = findViewById(R.id.btnKirim_F);
        progressBar = findViewById(R.id.progressBar_F);

        TextView tv_Login = findViewById(R.id.tvLogin_F);
        tv_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goLogin = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                startActivity(goLogin);
                finish();
            }
        });

        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Enter your registered email", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.VISIBLE);
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ResetPasswordActivity.this, "Kami telah mengirimi Anda petunjuk untuk mereset kata sandi Anda!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ResetPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                    }
                                progressBar.setVisibility(View.INVISIBLE);
                                }
                            });


            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent goLogin = new Intent(ResetPasswordActivity.this, LoginActivity.class);
        startActivity(goLogin);
        finish();
    }

}