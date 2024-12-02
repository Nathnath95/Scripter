package com.example.scripter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.animation.ObjectAnimator;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.os.Handler;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.transition.Fade;
import android.widget.Toast;

public class StartPage extends AppCompatActivity {
    private ImageButton imageButton1;
    private ImageView imageView2;
    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Fade());
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_start_page);

        imageButton1 = findViewById(R.id.imageButton1);
        imageView2 = findViewById(R.id.imageView2);
    }

    public void start(View view) {
        imageButton1.setClickable(false);

        ObjectAnimator fadeOutButton = ObjectAnimator.ofFloat(imageButton1, "alpha", 1f, 0f);
        fadeOutButton.setDuration(1500);
        fadeOutButton.start();

        new Handler().postDelayed(() -> {
            ObjectAnimator fadeOutImageView = ObjectAnimator.ofFloat(imageView2, "alpha", 1f, 0f);
            fadeOutImageView.setDuration(1000);
            fadeOutImageView.start();
        }, 1500);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(StartPage.this, LoginPage.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2500);
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
