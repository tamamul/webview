package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    
    private static final int SPLASH_DURATION = 1500; // 1.5 detik
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Tidak perlu setContentView karena background sudah diatur di theme
        
        // Handler untuk delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Pindah ke MainActivity
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            
            // Animasi transisi
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            
            // Tutup splash activity
            finish();
        }, SPLASH_DURATION);
    }
    
    @Override
    public void onBackPressed() {
        // Nonaktifkan back button selama splash screen
        // Do nothing
    }
}