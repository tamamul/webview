package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends Activity {

    private static final int SPLASH_DURATION = 3000; // 3 detik
    private Handler handler = new Handler();
    private ImageView logo;
    private TextView appName;
    private TextView loadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Setup window untuk immersive experience
        setupWindow();
        
        setContentView(R.layout.activity_splash);
        
        initializeViews();
        
        // Pastikan teks muncul sebelum animasi dimulai
        ensureTextVisible();
        
        startLogoAnimation();
        startTextAnimation();
        
        // Setup notification service di background
        NotificationScheduler.setupDailyNotifications(this);
        
        // Pindah ke MainActivity dengan delay
        scheduleNavigation();
    }

    private void setupWindow() {
        // Fullscreen dengan immersive mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // Transparent status bar untuk Android Lollipop+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        
        // Keep screen on selama splash
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void initializeViews() {
        logo = findViewById(R.id.splash_logo);
        appName = findViewById(R.id.splash_app_name);
        loadingText = findViewById(R.id.splash_loading_text);
        
        // DEBUG LOG
        Log.d("SplashDebug", "=== INITIALIZE VIEWS ===");
        Log.d("SplashDebug", "Logo: " + (logo != null ? "FOUND" : "NULL"));
        Log.d("SplashDebug", "AppName: " + (appName != null ? "FOUND" : "NULL"));
        Log.d("SplashDebug", "LoadingText: " + (loadingText != null ? "FOUND" : "NULL"));
        
        if (appName != null) {
            Log.d("SplashDebug", "AppName text: " + appName.getText());
            Log.d("SplashDebug", "AppName color: " + appName.getCurrentTextColor());
            // Force set text color putih jika gelap
            appName.setTextColor(Color.WHITE);
        }
        
        if (loadingText != null) {
            loadingText.setTextColor(Color.WHITE);
        }
    }

    private void ensureTextVisible() {
        // Pastikan teks visible dan dengan warna yang kontras
        new Handler().postDelayed(() -> {
            if (appName != null) {
                appName.setVisibility(View.VISIBLE);
                appName.setTextColor(Color.WHITE);
                appName.bringToFront();
            }
            
            if (loadingText != null) {
                loadingText.setVisibility(View.VISIBLE);
                loadingText.setTextColor(Color.WHITE);
                loadingText.bringToFront();
            }
        }, 100);
    }

    private void startLogoAnimation() {
        if (logo == null) {
            Log.e("SplashDebug", "Logo is null, cannot start animation");
            return;
        }
        
        // Animation Set: Scale + Rotate + Fade
        AnimationSet logoAnimationSet = new AnimationSet(true);
        logoAnimationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        
        // Scale animation (zoom out dari besar)
        ScaleAnimation scaleAnim = new ScaleAnimation(
                1.5f, 1.0f,
                1.5f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleAnim.setDuration(1200);
        
        // Rotate animation (putar halus)
        RotateAnimation rotateAnim = new RotateAnimation(
                -15f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotateAnim.setDuration(1000);
        
        // Fade in
        AlphaAnimation fadeAnim = new AlphaAnimation(0f, 1f);
        fadeAnim.setDuration(1000);
        
        logoAnimationSet.addAnimation(scaleAnim);
        logoAnimationSet.addAnimation(rotateAnim);
        logoAnimationSet.addAnimation(fadeAnim);
        
        logo.startAnimation(logoAnimationSet);
        
        // Pulse animation setelah animasi utama selesai
        handler.postDelayed(this::startPulseAnimation, 1200);
    }

    private void startPulseAnimation() {
        if (logo == null) return;
        
        ScaleAnimation pulseAnim = new ScaleAnimation(
                1.0f, 1.05f,
                1.0f, 1.05f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        pulseAnim.setDuration(800);
        pulseAnim.setRepeatMode(Animation.REVERSE);
        pulseAnim.setRepeatCount(Animation.INFINITE);
        
        logo.startAnimation(pulseAnim);
    }

    private void startTextAnimation() {
        // App name animation
        if (appName != null) {
            AnimationSet nameAnimationSet = new AnimationSet(true);
            
            // Pastikan app name visible
            appName.setVisibility(View.VISIBLE);
            appName.setTextColor(Color.WHITE);
            
            // Fade in sederhana dulu (tanpa slide yang mungkin error)
            AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
            fadeIn.setDuration(1500);
            fadeIn.setStartOffset(800);
            fadeIn.setFillAfter(true);
            
            nameAnimationSet.addAnimation(fadeIn);
            appName.startAnimation(nameAnimationSet);
            
            Log.d("SplashDebug", "AppName animation started");
        }
        
        // Loading text animation
        if (loadingText != null) {
            // Sederhanakan dulu dengan fade in biasa
            AlphaAnimation fadeInText = new AlphaAnimation(0f, 1f);
            fadeInText.setDuration(1000);
            fadeInText.setStartOffset(1500);
            fadeInText.setFillAfter(true);
            
            loadingText.startAnimation(fadeInText);
            loadingText.setTextColor(Color.WHITE);
            
            Log.d("SplashDebug", "LoadingText animation started");
            
            // Typing effect hanya jika text tidak kosong
            final String originalText = loadingText.getText().toString();
            if (!originalText.isEmpty()) {
                startSimpleTypingEffect(originalText);
            }
        }
    }

    private void startSimpleTypingEffect(final String originalText) {
        if (loadingText == null) return;
        
        loadingText.setText("");
        
        handler.postDelayed(() -> {
            for (int i = 0; i <= originalText.length(); i++) {
                final int index = i;
                handler.postDelayed(() -> {
                    if (index <= originalText.length() && loadingText != null) {
                        loadingText.setText(originalText.substring(0, index));
                    }
                }, i * 100L);
            }
        }, 1800);
    }

    private void scheduleNavigation() {
        handler.postDelayed(() -> {
            startExitAnimation();
        }, SPLASH_DURATION);
    }

    private void startExitAnimation() {
        // Fade out root view
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
            fadeOut.setDuration(500);
            fadeOut.setFillAfter(true);
            
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                
                @Override
                public void onAnimationEnd(Animation animation) {
                    navigateToMain();
                }
                
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            
            rootView.startAnimation(fadeOut);
        } else {
            navigateToMain();
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Disable back button
        // Optional: bisa tambahkan toast atau efek getar
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}