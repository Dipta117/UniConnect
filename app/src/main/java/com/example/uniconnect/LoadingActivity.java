package com.example.uniconnect;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // Set a delay (e.g., 3 seconds)
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(LoadingActivity.this, GroupActivity.class);
            startActivity(intent);
            finish(); // Finish LoadingActivity to prevent returning to it
        }, 3000); // 3000 ms = 3 seconds
    }
}
