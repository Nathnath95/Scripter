package com.example.scripter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ForgetPasswordPage extends AppCompatActivity {
    private long backPressedTime;
    private FirebaseAuth Auth;
    private EditText loginEmail, newPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password_page);

        loginEmail =findViewById(R.id.loginEmail);
        newPassword = findViewById(R.id.newPassword);

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

    public void submit(View view) {
        String email = loginEmail.getText().toString().trim();
        String newPass = newPassword.getText().toString().trim();

        if (email.isEmpty() || newPass.isEmpty()) {
            Toast.makeText(ForgetPasswordPage.this, "Please fill out both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = Auth.getCurrentUser();
                if (user != null && user.getEmail().equals(email)) {
                    user.updatePassword(newPass).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(ForgetPasswordPage.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                            new AlertDialog.Builder(ForgetPasswordPage.this)
                                    .setTitle("Password Updated")
                                    .setMessage("E-mail Address: " + email + "\nNew Password: " + newPass)
                                    .setPositiveButton("OK", (dialog, which) -> {
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
                                    })
                                    .show();
                        } else {
                            Toast.makeText(ForgetPasswordPage.this, "Password update failed: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ForgetPasswordPage.this, "No logged-in user with this email.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ForgetPasswordPage.this, "Email not found or error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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