package com.example.scripter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class ViewRecordingPage extends AppCompatActivity {
    private EditText recordingNameTextView, scriptTextView;
    private ImageButton editRecordingName, editRecordingScript, deleteRecording, shareRecording, playButton;
    private boolean isEditingRecordingName = false;
    private boolean isEditingRecordingScript = false;

    private long backPressedTime;

    TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_recording_page);

        recordingNameTextView = findViewById(R.id.recordingNameTextView);
        scriptTextView = findViewById(R.id.scriptTextView);
        editRecordingName = findViewById(R.id.editRecordingName);
        editRecordingScript = findViewById(R.id.editRecordingScript);
        deleteRecording = findViewById(R.id.deleteRecording);
        shareRecording = findViewById(R.id.shareRecording);
        playButton = findViewById(R.id.playButton);

        String recordingName = getIntent().getStringExtra("RECORDING_NAME");
        if (recordingName != null) {
            String scriptContent = readRecordingFromFile(recordingName);
            if (scriptContent != null) {
                scriptTextView.setText(scriptContent);
            } else {
                Toast.makeText(this, "Recording not found!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "No recording selected!", Toast.LENGTH_SHORT).show();
            finish();
        }
        String script = getIntent().getStringExtra("SCRIPT");

        if (recordingName != null) {
            recordingNameTextView.setText(recordingName);
        }
        if (script != null) {
            scriptTextView.setText(script);
        }

        t1 = new TextToSpeech(this, i -> {
            if (i != TextToSpeech.ERROR){
                t1.setLanguage(Locale.CANADA);
            } else {
                Toast.makeText(this, "TextToSpeech initializaiton failed!", Toast.LENGTH_SHORT).show();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (t1 != null) {
                    Log.d("Text", "Play clicked");
                    t1.speak(scriptTextView.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    Log.d("Text", "Not initialized");
                }
            }
        });
    }

    private String readRecordingFromFile(String recordingName) {
        File directory = getExternalFilesDir(null);
        if (directory != null) {
            File file = new File(directory, recordingName + ".txt");
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    return content.toString().trim();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
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

    private void deleteRecording(String recordingName) {
        File directory = getExternalFilesDir(null);
        if (directory != null) {
            File file = new File(directory, recordingName + ".txt");
            if (file.exists() && file.delete()) {
                Toast.makeText(this, "Recording deleted successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to delete recording!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void playRecording(String recordingName) {
        File directory = getExternalFilesDir(null);
        if (directory != null) {
            File file = new File(directory, recordingName + ".txt");
            if (file.exists() && file.delete()) {
                Toast.makeText(this, "Recording Played successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to play recording!", Toast.LENGTH_SHORT).show();
            }
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
            Intent intent = new Intent(ViewRecordingPage.this, RecordingListPage.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 1000);
    }

    public void deleteRecording(View view) {
        String recordingName = recordingNameTextView.getText().toString();
        deleteRecording(recordingName);

        ImageView imageView4 = findViewById(R.id.imageView4);
        imageView4.setVisibility(View.VISIBLE);
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.setFillAfter(true);
        imageView4.startAnimation(fadeIn);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(ViewRecordingPage.this, RecordingListPage.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 1000);
    }

    public void editRecordingName(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if (!isEditingRecordingName) {

            editRecordingScript.setImageResource(R.drawable.save_edit_button_locked);
            editRecordingScript.setClickable(false);

            isEditingRecordingName = true;
            recordingNameTextView.setFocusable(true);
            recordingNameTextView.setFocusableInTouchMode(true);
            recordingNameTextView.setCursorVisible(true);
            recordingNameTextView.requestFocus();
            imm.showSoftInput(recordingNameTextView, InputMethodManager.SHOW_IMPLICIT);
            editRecordingName.setImageResource(R.drawable.save_edit_button_states);
        }
        else {
            isEditingRecordingName = false;
            recordingNameTextView.setFocusable(false);
            recordingNameTextView.setFocusableInTouchMode(false);
            recordingNameTextView.setCursorVisible(false);
            editRecordingName.setImageResource(R.drawable.edit_button_states);
            imm.hideSoftInputFromWindow(recordingNameTextView.getWindowToken(), 0);

            String updatedName = recordingNameTextView.getText().toString();
            String originalName = getIntent().getStringExtra("RECORDING_NAME");

            if (!updatedName.equals(originalName)) {
                File directory = getExternalFilesDir(null);
                File originalFile = new File(directory, originalName + ".txt");
                File updatedFile = new File(directory, updatedName + ".txt");

                if (originalFile.exists()) {
                    boolean renamed = originalFile.renameTo(updatedFile);
                    if (renamed) {
                        getIntent().putExtra("RECORDING_NAME", updatedName);
                        Toast.makeText(this, "Recording name updated successfully!", Toast.LENGTH_SHORT).show();
                        String updatedScript = scriptTextView.getText().toString();
                        try (FileWriter writer = new FileWriter(updatedFile, false)) {
                            writer.write(updatedScript);
//                            Toast.makeText(this, "Recording script updated successfully!", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
//                            Toast.makeText(this, "Failed to update recording script!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to update recording name!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            editRecordingScript.setImageResource(R.drawable.edit_button_states);
            editRecordingScript.setClickable(true);
        }
    }

    public void editRecordingScript(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if (!isEditingRecordingScript) {

            editRecordingName.setImageResource(R.drawable.save_edit_button_locked);
            editRecordingName.setClickable(false);

            isEditingRecordingScript = true;
            scriptTextView.setFocusable(true);
            scriptTextView.setFocusableInTouchMode(true);
            scriptTextView.setCursorVisible(true);
            scriptTextView.requestFocus();
            imm.showSoftInput(scriptTextView, InputMethodManager.SHOW_IMPLICIT);
            editRecordingScript.setImageResource(R.drawable.save_edit_button_states);
        } else {
            isEditingRecordingScript = false;
            scriptTextView.setFocusable(false);
            scriptTextView.setFocusableInTouchMode(false);
            scriptTextView.setCursorVisible(false);
            editRecordingScript.setImageResource(R.drawable.edit_button_states);
            imm.hideSoftInputFromWindow(scriptTextView.getWindowToken(), 0);

            String updatedScript = scriptTextView.getText().toString();
            String recordingName = getIntent().getStringExtra("RECORDING_NAME");

            if (recordingName != null) {
                File directory = getExternalFilesDir(null);
                File file = new File(directory, recordingName + ".txt");

                try (FileWriter writer = new FileWriter(file, false)) {
                    writer.write(updatedScript);
                    Toast.makeText(this, "Recording script updated successfully!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to update recording script!", Toast.LENGTH_SHORT).show();
                }
            }

            editRecordingName.setImageResource(R.drawable.edit_button_states);
            editRecordingName.setClickable(true);
        }
    }

    public void shareRecording(View view) {
        // BLUETOOTH FILE TRANSFER GOES HERE
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