package com.example.lab_7;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonAudio = findViewById(R.id.button_audio);
        Button buttonVideo = findViewById(R.id.button_video);
        Button buttonPhoto = findViewById(R.id.button_photo);
        buttonAudio.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AudioActivity.class);
            startActivity(intent);
        });

        buttonVideo.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, VideoActivity.class);
            startActivity(intent);
        });

        buttonPhoto.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PhotoActivity.class);
            startActivity(intent);
        });
    }
}