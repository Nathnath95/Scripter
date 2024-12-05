package com.example.scripter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.common.value.qual.StringVal;

import java.util.Objects;

public class RegisterPage extends AppCompatActivity {
    private long backPressedTime;
    private EditText signupEmail, signupPassword, signupPhone;
    private ImageButton registerButton;
    private FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_page);

        signupEmail = findViewById(R.id.signupEmail);
        signupPassword = findViewById(R.id.signupPassword);
        signupPhone = findViewById(R.id.signupPhone);
        registerButton = findViewById(R.id.registerButton);

        Auth = FirebaseAuth.getInstance();
    }

    public void back(View view) {
        ImageView imageView = findViewById(R.id.imageView2);
        imageView.setVisibility(View.VISIBLE);
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.setFillAfter(true);
        imageView.startAnimation(fadeIn);
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, LoginPage.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 1000);
    }

    public void register(View view) {
        String email = signupEmail.getText().toString().trim();
        String password = signupPassword.getText().toString().trim();
        String phone = signupPhone.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        Auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = Auth.getCurrentUser();
                        Toast.makeText(RegisterPage.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                        new AlertDialog.Builder(this)
                                .setTitle("Registration Complete")
                                .setMessage("E-mail Address: " + email + "\n" +
                                        "Password: " + password + "\n" +
                                        "Phone: " + phone + "\n")
                                .setPositiveButton("OK", (dialog, which) -> {
                                    dialog.dismiss();
                                    ImageView imageView = findViewById(R.id.imageView2);
                                    imageView.setVisibility(View.VISIBLE);
                                    AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
                                    fadeIn.setDuration(1000);
                                    fadeIn.setFillAfter(true);
                                    imageView.startAnimation(fadeIn);
                                    new Handler().postDelayed(() -> {
                                        Intent intent = new Intent(RegisterPage.this, LoginPage.class);
                                        startActivity(intent);
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                        finish();
                                    }, 1000);
                                }).show();
                    } else {
                        Toast.makeText(RegisterPage.this, "Registration failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if(backPressedTime+3000>System.currentTimeMillis()){
            super.onBackPressed();
            finish();
        }
        else{
            Toast.makeText(this,"Press back again to exit",Toast.LENGTH_SHORT).show();
        }
        backPressedTime=System.currentTimeMillis();
    }
}