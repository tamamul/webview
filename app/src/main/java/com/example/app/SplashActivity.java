package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.Random;

public class SplashActivity extends Activity {
    
    private ProgressBar progressBar;
    private TextView loadingText, tipText;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    
    private String[] loadingTips = {
        "Menyiapkan aplikasi...",
        "Memuat data pengguna...",
        "Menyiapkan sesi...",
        "Memuat konten...",
        "Mengoptimalkan performa..."
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_splash);
        
        progressBar = findViewById(R.id.circularProgress);
        loadingText = findViewById(R.id.loadingText);
        tipText = findViewById(R.id.tipText);
        
        // Set random tip
        setRandomTip();
        
        // Start progress animation
        startLoadingAnimation();
    }
    
    private void setRandomTip() {
        Random random = new Random();
        int index = random.nextInt(loadingTips.length);
        tipText.setText(loadingTips[index]);
    }
    
    private void startLoadingAnimation() {
        new Thread(() -> {
            while (progressStatus < 100) {
                progressStatus += 2;
                
                handler.post(() -> {
                    progressBar.setProgress(progressStatus);
                    loadingText.setText(progressStatus + "%");
                    
                    // Change tip at certain percentages
                    if (progressStatus % 25 == 0) {
                        setRandomTip();
                    }
                });
                
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            // After loading complete, check credentials
            checkCredentialsAndProceed();
        }).start();
    }
    
    private void checkCredentialsAndProceed() {
        runOnUiThread(() -> {
            SharedPreferences prefs = getSharedPreferences("user_credentials", MODE_PRIVATE);
            String username = prefs.getString("username", "");
            String password = prefs.getString("password", "");
            
            Intent intent;
            
            if (username.isEmpty() || password.isEmpty()) {
                // No credentials, go to Login
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            } else {
                // Has credentials, go to Main
                intent = new Intent(SplashActivity.this, MainActivity.class);
            }
            
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
    }
    
    @Override
    public void onBackPressed() {
        // Disable back button during splash
    }
}