package com.example.scripter;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class RecordingListPage extends AppCompatActivity {
    private long backPressedTime;

    private ListView recordingListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> recordingNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recording_list_page);

        recordingListView = findViewById(R.id.recordingListView);
        recordingNames = new ArrayList<>();
        loadRecordingsFromFiles();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recordingNames);
        recordingListView.setAdapter(adapter);

        recordingListView.setOnItemClickListener((parent, view, position, id) -> {
            String recordingName = adapter.getItem(position);
            if (recordingName != null) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String fileName = userId + "_" + recordingName + ".txt";
                File file = new File(getExternalFilesDir(null), fileName);

                ImageView imageView4 = findViewById(R.id.imageView4);
                ImageView imageView5 = findViewById(R.id.imageView5);

                imageView4.setVisibility(View.VISIBLE);
                AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
                fadeIn.setDuration(1000);
                fadeIn.setFillAfter(true);
                imageView4.startAnimation(fadeIn);

                new Handler().postDelayed(() -> {
                    imageView5.setVisibility(View.VISIBLE);
                    AlphaAnimation fadeIn2 = new AlphaAnimation(0f, 1f);
                    fadeIn2.setDuration(1000);
                    fadeIn2.setFillAfter(true);
                    imageView5.startAnimation(fadeIn2);
                }, 1000);

                new Handler().postDelayed(() -> {
                    if (file.exists()) {
                        Intent intent = new Intent(RecordingListPage.this, ViewRecordingPage.class);
                        intent.putExtra("RECORDING_NAME", recordingName);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                }, 1800);
            }
        });
    }

    private void loadRecordingsFromFiles() {
        File directory = getExternalFilesDir(null);
        if (directory != null) {
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt"));
            if (files != null) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                for (File file : files) {
                    if (file.getName().startsWith(userId)) {
                        String recordingName = file.getName().replace(userId + "_", "").replace(".txt", "");
                        recordingNames.add(recordingName);
                    }
                }
            } else {
                Toast.makeText(this, "No recordings found!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Error accessing storage!", Toast.LENGTH_SHORT).show();
        }
    }

    public void back(View view) {
        ImageView imageView4 = findViewById(R.id.imageView4);
        imageView4.setVisibility(View.VISIBLE);
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.setFillAfter(true);
        imageView4.startAnimation(fadeIn);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(RecordingListPage.this, HomePage.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 1000);
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