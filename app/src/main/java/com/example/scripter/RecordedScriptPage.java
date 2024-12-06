package com.example.scripter;

import android.bluetooth.BluetoothHeadset;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Locale;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RecordedScriptPage extends AppCompatActivity {
    private long backPressedTime;

    private EditText scriptTextView, recordingName;
    private ImageButton saveRecording, deleteRecording;
    static final HashMap<String, String> recordings = new HashMap<>();
    TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recorded_script_page);
        // Text to speech

        scriptTextView = findViewById(R.id.scriptTextView);
        recordingName = findViewById(R.id.recordingName);
        saveRecording = findViewById(R.id.saveRecording);
        deleteRecording = findViewById(R.id.deleteRecording);


        // Initialize TextToSpeech
        t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int langResult = t1.setLanguage(Locale.CANADA);
                    if (langResult == TextToSpeech.LANG_MISSING_DATA
                            || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language is not supported or missing data");
                    } else {
                        Log.d("TTS", "Language is set successfully");

                        String recordedScript = getIntent().getStringExtra("RECORDED_SCRIPT");
                        Log.d("Test", "This is recorded: " + recordedScript);

                        if (recordedScript != null) {
                            scriptTextView.setText(recordedScript);
                            String text = scriptTextView.getText().toString();
                            Log.d("Text", "Second");
                            Log.d("Text", text);

                            t1.speak(recordedScript, TextToSpeech.QUEUE_FLUSH, null, null);
                        } else {
                            Log.e("TTS", "Recorded script is empty or null");
                        }
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        saveRecording.setOnClickListener(v -> saveRecording());
        deleteRecording.setOnClickListener(v -> deleteRecording());
    }

    private void text2speech(String text){
        t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR)
                    t1.setLanguage(Locale.CANADA);
            }
        });

        t1.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void saveRecording() {
        String recordingNameText = recordingName.getText().toString().trim();
        String scriptText = scriptTextView.getText().toString().trim();
        if (!recordingNameText.isEmpty() && !scriptText.isEmpty()) {
            try {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                File directory = getExternalFilesDir(null);
                if (directory != null) {
                    File file = new File(directory, userId + "_" + recordingNameText + ".txt");
                    File file_to_download = new File(Environment.getExternalStorageDirectory(), "Download/" + userId + "_" + recordingNameText + ".txt");


                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(scriptText.getBytes());
                    fos.close();
                    Log.d("Test", directory + recordingNameText);

                    FileOutputStream fts = new FileOutputStream(file_to_download);
                    fts.write(scriptText.getBytes());
                    fts.close();

                    Toast.makeText(this, "Recording saved!", Toast.LENGTH_SHORT).show();

                    ImageView imageView4 = findViewById(R.id.imageView4);
                    imageView4.setVisibility(View.VISIBLE);
                    AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
                    fadeIn.setDuration(1000);
                    fadeIn.setFillAfter(true);
                    imageView4.startAnimation(fadeIn);

                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(RecordedScriptPage.this, HomePage.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }, 1000);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error saving recording to file!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Recording name or script cannot be empty!", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteRecording() {
        scriptTextView.setText("");
        recordingName.setText("");
        Toast.makeText(this, "Recording deleted", Toast.LENGTH_SHORT).show();

        ImageView imageView4 = findViewById(R.id.imageView4);
        imageView4.setVisibility(View.VISIBLE);
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.setFillAfter(true);
        imageView4.startAnimation(fadeIn);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(RecordedScriptPage.this, HomePage.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        t1.stop();
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