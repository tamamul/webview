package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends Activity {
    
    private static final int SPLASH_DURATION = 3500; // 3.5 detik
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_splash);
        
        // Animasi untuk logo
        ImageView logo = findViewById(R.id.splash_logo);
        TextView appName = findViewById(R.id.splash_app_name);
        TextView loadingText = findViewById(R.id.splash_loading_text);
        
        // Fade in animation untuk logo
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1500);
        fadeIn.setFillAfter(true);
        logo.startAnimation(fadeIn);
        
        // Animation untuk app name (delay)
        AlphaAnimation fadeInName = new AlphaAnimation(0.0f, 1.0f);
        fadeInName.setDuration(1000);
        fadeInName.setStartOffset(800);
        fadeInName.setFillAfter(true);
        appName.startAnimation(fadeInName);
        
        // Animation untuk loading text (delay lebih lama)
        AlphaAnimation fadeInText = new AlphaAnimation(0.0f, 1.0f);
        fadeInText.setDuration(800);
        fadeInText.setStartOffset(1500);
        fadeInText.setFillAfter(true);
        loadingText.startAnimation(fadeInText);
        
        // Setup notification service di background
        NotificationScheduler.setupDailyNotifications(this);
        
        // Pindah ke MainActivity setelah delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, SPLASH_DURATION);
    }
    
    @Override
    public void onBackPressed() {
        // Disable back button selama splash screen
    }
}