package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashActivity extends Activity {

    private static final int SPLASH_DURATION = 3500;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_splash);
        
        // Inisialisasi views
        ImageView logo = findViewById(R.id.splash_logo);
        TextView appName = findViewById(R.id.splash_app_name);
        TextView loadingText = findViewById(R.id.splash_loading_text);
        ProgressBar progressBar = findViewById(R.id.splash_progress);
        
        // Pastikan teks berwarna putih
        if (appName != null) appName.setTextColor(Color.WHITE);
        if (loadingText != null) loadingText.setTextColor(Color.WHITE);
        
        // 1. Animasi Logo - Fade In
        if (logo != null) {
            logo.setAlpha(0f);
            logo.animate()
                    .alpha(1f)
                    .setDuration(1500)
                    .start();
        }
        
        // 2. Animasi App Name - Fade In dengan delay
        if (appName != null) {
            appName.setAlpha(0f);
            appName.animate()
                    .alpha(1f)
                    .setDuration(1000)
                    .setStartDelay(800)
                    .start();
        }
        
        // 3. Animasi Loading Text - Fade In dengan delay lebih lama
        if (loadingText != null) {
            loadingText.setAlpha(0f);
            loadingText.animate()
                    .alpha(1f)
                    .setDuration(800)
                    .setStartDelay(1500)
                    .start();
            
            // Typing effect sederhana
            startTypingEffect(loadingText);
        }
        
        // 4. Progress Bar - Rotate
        if (progressBar != null) {
            progressBar.setAlpha(0f);
            progressBar.animate()
                    .alpha(1f)
                    .setDuration(500)
                    .setStartDelay(2000)
                    .start();
        }
        
        // Setup notifications
        try {
            NotificationScheduler.setupDailyNotifications(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Navigate setelah delay
        handler.postDelayed(this::goToMainActivity, SPLASH_DURATION);
    }
    
    private void startTypingEffect(final TextView textView) {
        final String original = textView.getText().toString();
        textView.setText("");
        
        handler.postDelayed(() -> {
            for (int i = 0; i <= original.length(); i++) {
                final int index = i;
                handler.postDelayed(() -> {
                    if (textView != null && index <= original.length()) {
                        textView.setText(original.substring(0, index));
                    }
                }, i * 50);
            }
        }, 1800);
    }
    
    private void goToMainActivity() {
        // Fade out animasi
        View root = findViewById(android.R.id.content);
        if (root != null) {
            root.animate()
                    .alpha(0f)
                    .setDuration(500)
                    .withEndAction(() -> {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    })
                    .start();
        } else {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }
    
    @Override
    public void onBackPressed() {
        // Disable back button
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}