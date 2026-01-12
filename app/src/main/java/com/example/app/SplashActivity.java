package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashActivity extends Activity {

    private Handler handler = new Handler();
    private ProgressBar progressBar;
    private TextView loadingText;
    private int progressStatus = 0;
    
    private String[] loadingMessages = {
        "Menyiapkan aplikasi...",
        "Memuat modul presensi...", 
        "Menyambungkan ke server...",
        "Siap digunakan!"
    };
    private int messageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_splash);
        
        // Initialize views
        ImageView logo = findViewById(R.id.splash_logo);
        TextView appName = findViewById(R.id.splash_app_name);
        TextView subtitle = findViewById(R.id.splash_app_subtitle);
        loadingText = findViewById(R.id.splash_loading_text);
        progressBar = findViewById(R.id.splash_progress);
        TextView versionText = findViewById(R.id.splash_version_text);
        
        // Set text colors
        appName.setTextColor(Color.WHITE);
        subtitle.setTextColor(Color.parseColor("#CCFFFFFF"));
        loadingText.setTextColor(Color.WHITE);
        versionText.setTextColor(Color.parseColor("#88FFFFFF"));
        
        // Start animations
        startAnimations();
        
        // Start progress simulation
        startProgressSimulation();
        
        // Navigate to MainActivity after 4 seconds
        handler.postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 4000);
    }
    
    private void startAnimations() {
        // Simple fade in for logo
        ImageView logo = findViewById(R.id.splash_logo);
        TextView appName = findViewById(R.id.splash_app_name);
        TextView subtitle = findViewById(R.id.splash_app_subtitle);
        
        logo.setAlpha(0f);
        appName.setAlpha(0f);
        subtitle.setAlpha(0f);
        loadingText.setAlpha(0f);
        progressBar.setAlpha(0f);
        
        logo.animate().alpha(1f).setDuration(1000).start();
        appName.animate().alpha(1f).setDuration(800).setStartDelay(300).start();
        subtitle.animate().alpha(1f).setDuration(600).setStartDelay(600).start();
        loadingText.animate().alpha(1f).setDuration(500).setStartDelay(900).start();
        progressBar.animate().alpha(1f).setDuration(400).setStartDelay(1200).start();
    }
    
    private void startProgressSimulation() {
        new Thread(() -> {
            while (progressStatus < 100) {
                progressStatus += 1;
                
                // Update progress bar on UI thread
                handler.post(() -> {
                    progressBar.setProgress(progressStatus);
                    
                    // Update loading message every 25%
                    if (progressStatus % 25 == 0 && messageIndex < loadingMessages.length) {
                        loadingText.setText(loadingMessages[messageIndex]);
                        messageIndex++;
                    }
                });
                
                try {
                    Thread.sleep(40); // 40ms per 1% = 4 seconds total
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    @Override
    public void onBackPressed() {
        // Disable back button
    }
}