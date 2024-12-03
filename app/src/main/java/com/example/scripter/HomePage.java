package com.example.scripter;

import android.Manifest;
import android.app.MediaRouteButton;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.session.MediaSession;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.widget.Toast;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomePage extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 1;
    private long backPressedTime;

    private SpeechRecognizer speechRecognizer;
    private boolean isRecording = false;
    private boolean isPaused = true;
    private StringBuilder transcribedText = new StringBuilder();
    private ImageButton micButton, pauseOrPlayButton, finishButton, finish_QNA_Button, finish_AI_Button, cancelButton, recordlinglistButton, bluetoothButton;
    TextToSpeech t1;
    private TextView timeTextView;
    private Handler timerHandler = new Handler();
    private int seconds = 0;
    private boolean isTimerRunning = false;
    private Runnable timerRunnable = new Runnable() {
        public void run() {
            if (isRecording && !isPaused) {
                seconds++;
                updateTimerText();
                timerHandler.postDelayed(this, 1000);
            }
        }
    };

    // String for API prompt
    private String promtText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR)
                    t1.setLanguage(Locale.CANADA);
            }
        });

        MediaSession mediaSession = new MediaSession(this, "MyMediaSession");
        mediaSession.setCallback(new MediaSession.Callback() {
            @Override
            public boolean onMediaButtonEvent(@NonNull Intent mediaButtonIntent) {
                if (Intent.ACTION_MEDIA_BUTTON.equals(mediaButtonIntent.getAction())){
                    KeyEvent event = mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                    if (event != null && event.getAction() == KeyEvent.ACTION_DOWN){
                        int keyCode = event.getKeyCode();
                        switch (keyCode){
                            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                                Log.d("Text", "Play pressed");
                                break;
                            case KeyEvent.KEYCODE_MEDIA_NEXT:
                                Log.d("Text", "Next pressed");
                                break;
                            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                                Log.d("Text", "Previous pressed");
                                break;
                        }
                    }
                }
                return super.onMediaButtonEvent(mediaButtonIntent);
            }
        });
        mediaSession.setActive(true);
        micButton = findViewById(R.id.micButton);
        pauseOrPlayButton = findViewById(R.id.pauseOrPlayButton);
        finishButton = findViewById(R.id.finishButton);
        finish_QNA_Button = findViewById(R.id.finish_QNA_Button);
        finish_AI_Button = findViewById(R.id.finish_AI_Button);
        cancelButton = findViewById(R.id.cancelButton);
        recordlinglistButton = findViewById(R.id.recordlinglistButton);
        bluetoothButton = findViewById(R.id.bluetoothButton);

        requestMicrophonePermission();
        setupSpeechRecognizer();

        micButton.setOnClickListener(v -> toggleMic());
        pauseOrPlayButton.setOnClickListener(v -> togglePause());
        finishButton.setOnClickListener(v -> finishRecording());
        cancelButton.setOnClickListener(v -> cancelRecording());

        timeTextView = findViewById(R.id.timeTextView);

        // Headphone buttons stuff


        updateButtonsState(false);
    }

    private void requestMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
        }
    }

    private void setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {}

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}


            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {}

            @Override
            public void onError(int error) {}

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    transcribedText.append(matches.get(0)).append("\n");
                    Log.d("SpeechRecognizer", "Transcription: " + matches.get(0));
                }
            }

        });
    }

    private void toggleMic() {
        if (isRecording) {
            return;
        }
        transcribedText.setLength(0);
        isRecording = true;
        micButton.setImageResource(R.drawable.mic_button_on);

        updateButtonsState(true);
        recordlinglistButton.setClickable(false);
        recordlinglistButton.setImageResource(R.drawable.recordinglist_button_locked);
        bluetoothButton.setClickable(false);
        bluetoothButton.setImageResource(R.drawable.bluetooth_button_locked);

        isPaused = false;
        pauseOrPlayButton.setImageResource(R.drawable.pause_button_states);
        startListening();
        startTimer();
    }

    private void togglePause() {
        isPaused = !isPaused;

        pauseOrPlayButton.setImageResource(isPaused ? R.drawable.continue_button_states : R.drawable.pause_button_states);
        if (isPaused) {
            stopListening();
            stopTimer();
        } else {
            startListening();
            startTimer();
        }
    }

    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        speechRecognizer.startListening(intent);
    }

    private void stopListening() {
        speechRecognizer.stopListening();
    }

    private void finishRecording() {
        stopListening();
        isRecording = false;
        stopTimer();
        resetTimer();
        micButton.setImageResource(R.drawable.mic_button_off);
        updateButtonsState(false);

        recordlinglistButton.setClickable(true);
        recordlinglistButton.setImageResource(R.drawable.recordinglist_button_states);
        bluetoothButton.setClickable(true);
        bluetoothButton.setImageResource(R.drawable.bluetooth_button_states);

        Toast.makeText(this, "Processing... Please wait.", Toast.LENGTH_SHORT).show();

        new android.os.Handler().postDelayed(() -> {
            String finalText = transcribedText.toString();
            Log.d("Text", finalText);
            modelCall(finalText);
        }, 5000);
    }

    private void cancelRecording() {
        stopListening();
        transcribedText.setLength(0);
        isRecording = false;
        stopTimer();
        resetTimer();
        micButton.setImageResource(R.drawable.mic_button_off);
        updateButtonsState(false);

        recordlinglistButton.setClickable(true);
        recordlinglistButton.setImageResource(R.drawable.recordinglist_button_states);
        bluetoothButton.setClickable(true);
        bluetoothButton.setImageResource(R.drawable.bluetooth_button_states);
    }

    private void updateButtonsState(boolean isEnabled) {
        pauseOrPlayButton.setClickable(isEnabled);
        pauseOrPlayButton.setImageResource(isEnabled && !isPaused ? R.drawable.continue_button_states : R.drawable.pause_button_locked);

        finishButton.setClickable(isEnabled);
        finishButton.setImageResource(isEnabled ? R.drawable.finish_button_states : R.drawable.finish_button_locked);

        finish_AI_Button.setClickable(isEnabled);
        finish_AI_Button.setImageResource(isEnabled ? R.drawable.finish_ai_button_states : R.drawable.finish_ai_button_locked);

        finish_QNA_Button.setClickable(isEnabled);
        finish_QNA_Button.setImageResource(isEnabled ? R.drawable.finish_qna_button_states : R.drawable.finish_qna_button_locked);

        cancelButton.setClickable(isEnabled);
        cancelButton.setImageResource(isEnabled ? R.drawable.cancel_button_states : R.drawable.cancel_button_locked);
    }

    @Override
    protected void onDestroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        super.onDestroy();
    }

    public void goToList(View view) {
        ImageView imageView2 = findViewById(R.id.imageView2);
        ImageView imageViewList = findViewById(R.id.imageViewList);

        imageView2.setVisibility(View.VISIBLE);
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.setFillAfter(true);
        imageView2.startAnimation(fadeIn);

        new Handler().postDelayed(() -> {
            imageViewList.setVisibility(View.VISIBLE);
            AlphaAnimation fadeIn2 = new AlphaAnimation(0f, 1f);
            fadeIn2.setDuration(1000);
            fadeIn2.setFillAfter(true);
            imageViewList.startAnimation(fadeIn2);
        }, 1000);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(HomePage.this, RecordingListPage.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2000);
    }

    private void startTimer() {
        isTimerRunning = true;
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    private void stopTimer() {
        isTimerRunning = false;
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void resetTimer() {
        seconds = 0;
        updateTimerText();
    }

    private void updateTimerText() {
        int minutes = seconds / 60;
        int sec = seconds % 60;
        String time = String.format("%02d:%02d", minutes, sec);
        timeTextView.setText(time);
    }

    // Api
    public void modelCall(String text) {
        // Specify a Gemini model appropriate for your use case
        GenerativeModel gm =
                new GenerativeModel(
                        "gemini-1.5-flash", "AIzaSyAVQnIs0KS3JF8G3-qQ32eBYthwHckb1K4");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content =
                new Content.Builder().addText("Generate me a 2 sentences based on this: " + text).build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(
                    response,
                    new FutureCallback<GenerateContentResponse>() {
                        @Override
                        public void onSuccess(GenerateContentResponse result) {
                            String resultText = result.getText();
                            promtText = resultText;

                            ImageView imageView2 = findViewById(R.id.imageView2);
                            ImageView imageViewRecord = findViewById(R.id.imageViewRecord);

                            imageView2.setVisibility(View.VISIBLE);
                            AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
                            fadeIn.setDuration(1000);
                            fadeIn.setFillAfter(true);
                            imageView2.startAnimation(fadeIn);

                            new Handler().postDelayed(() -> {
                                imageViewRecord.setVisibility(View.VISIBLE);
                                AlphaAnimation fadeIn2 = new AlphaAnimation(0f, 1f);
                                fadeIn2.setDuration(1000);
                                fadeIn2.setFillAfter(true);
                                imageViewRecord.startAnimation(fadeIn2);
                            }, 1000);

                            new Handler().postDelayed(() -> {
                                Intent intent = new Intent(HomePage.this, RecordedScriptPage.class);
                                Log.d("Text", promtText);
                                t1.speak(promtText, TextToSpeech.QUEUE_FLUSH, null);
                                intent.putExtra("RECORDED_SCRIPT", promtText);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                            }, 2000);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            t.printStackTrace();
                        }
                    },
                    this.getMainExecutor());
        }
    }

    public void back(View view) {
        ImageView imageView2 = findViewById(R.id.imageView2);
        imageView2.setVisibility(View.VISIBLE);
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.setFillAfter(true);
        imageView2.startAnimation(fadeIn);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(HomePage.this, LoginPage.class);
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