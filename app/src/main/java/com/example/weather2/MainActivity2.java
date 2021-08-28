package com.example.weather2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {
    private TextToSpeech TTS;
    private EditText EditText;
    private SeekBar SeekBarPitch;
    private SeekBar SeekBarSpeed;
    private Button ButtonSpeak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ButtonSpeak = findViewById(R.id.button2);
        EditText = findViewById(R.id.edit_text);
        SeekBarPitch = findViewById(R.id.seek_bar_pitch);
        SeekBarSpeed = findViewById(R.id.seek_bar_speed);
        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = TTS.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(MainActivity2.this, "Language Not Supported!", Toast.LENGTH_SHORT).show();
                    } else {
                        ButtonSpeak.setEnabled(true);
                    }
                } else {
                    Toast.makeText(MainActivity2.this, "Initialization failed", Toast.LENGTH_SHORT).show();
                }
            }
        });


        ButtonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });
    }

    private void speak() {
        String text = EditText.getText().toString();
        float pitch = (float) SeekBarPitch.getProgress() / 50;
        if (pitch < 0.1) pitch = 0.1f;
        float speed = (float) SeekBarSpeed.getProgress() / 50;
        if (speed < 0.1) speed = 0.1f;

        TTS.setPitch(pitch);
        TTS.setSpeechRate(speed);

        TTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    protected void onDestroy() {
        if (TTS != null) {
            TTS.stop();
            TTS.shutdown();
        }
        super.onDestroy();
    }
}