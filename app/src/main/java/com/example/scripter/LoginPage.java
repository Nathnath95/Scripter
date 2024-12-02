package com.example.scripter;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class LoginPage extends AppCompatActivity {
    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        Button registerButton = findViewById(R.id.registerButton);
        Button forgetpassButton = findViewById(R.id.forgetpassButton);
        registerButton.setPaintFlags(registerButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        forgetpassButton.setPaintFlags(forgetpassButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public void goToForgetPassPage(View view) {
        ImageView imageView = findViewById(R.id.imageView2);
        imageView.setVisibility(View.VISIBLE);
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.setFillAfter(true);
        imageView.startAnimation(fadeIn);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, ForgetPasswordPage.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 1000);
    }

    public void goToRegisterPage(View view) {
        ImageView imageView = findViewById(R.id.imageView2);
        imageView.setVisibility(View.VISIBLE);
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.setFillAfter(true);
        imageView.startAnimation(fadeIn);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, RegisterPage.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 1000);
    }

    public void loginButton(View view) {
        ImageView imageView3 = findViewById(R.id.imageView3);
        ImageView imageView4 = findViewById(R.id.imageView4);

        imageView3.setVisibility(View.VISIBLE);
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.setFillAfter(true);
        imageView3.startAnimation(fadeIn);

        new Handler().postDelayed(() -> {
            imageView4.setVisibility(View.VISIBLE);
            AlphaAnimation fadeIn2 = new AlphaAnimation(0f, 1f);
            fadeIn2.setDuration(1000);
            fadeIn2.setFillAfter(true);
            imageView4.startAnimation(fadeIn2);
        }, 700);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, HomePage.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 1800);
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