package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
        startLogoAnimation();
        startTextAnimation();
        startLoadingIndicator();
        
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
        
        // Set initial alpha untuk animasi
        logo.setAlpha(0f);
        appName.setAlpha(0f);
        loadingText.setAlpha(0f);
    }

    private void startLogoAnimation() {
        // Animation Set: Scale + Rotate + Fade
        AnimationSet logoAnimationSet = new AnimationSet(true);
        logoAnimationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        
        // Scale animation (zoom out dari besar)
        ScaleAnimation scaleAnim = new ScaleAnimation(
                1.5f, 1.0f, // Scale X dari 150% ke 100%
                1.5f, 1.0f, // Scale Y dari 150% ke 100%
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot X di tengah
                Animation.RELATIVE_TO_SELF, 0.5f  // Pivot Y di tengah
        );
        scaleAnim.setDuration(1200);
        
        // Rotate animation (putar halus)
        RotateAnimation rotateAnim = new RotateAnimation(
                -15f, 0f, // Dari -15 derajat ke 0
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
        // Continuous pulse animation
        ScaleAnimation pulseAnim = new ScaleAnimation(
                1.0f, 1.05f, // Scale naik 5%
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
        // App name animation dengan slide up + fade
        AnimationSet nameAnimationSet = new AnimationSet(true);
        
        // Slide up dari bawah
        TranslateAnimation slideUp = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, // Mulai dari 50% bawah
                Animation.RELATIVE_TO_SELF, 0f
        );
        slideUp.setDuration(1000);
        slideUp.setStartOffset(500); // Delay setelah logo
        
        // Fade in
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(800);
        fadeIn.setStartOffset(500);
        
        nameAnimationSet.addAnimation(slideUp);
        nameAnimationSet.addAnimation(fadeIn);
        nameAnimationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        
        appName.startAnimation(nameAnimationSet);
        
        // Loading text dengan typing effect simulation
        startTypingEffect();
    }

    private void startTypingEffect() {
        final String originalText = loadingText.getText().toString();
        loadingText.setText("");
        
        handler.postDelayed(() -> {
            // Animate text appearance dengan delay per karakter
            for (int i = 0; i <= originalText.length(); i++) {
                final int index = i;
                handler.postDelayed(() -> {
                    if (index <= originalText.length()) {
                        loadingText.setText(originalText.substring(0, index));
                        
                        // Scale animation untuk setiap karakter baru
                        if (index > 0) {
                            ScaleAnimation charAnim = new ScaleAnimation(
                                    1.2f, 1.0f, 1.2f, 1.0f,
                                    Animation.RELATIVE_TO_SELF, (index-1)/(float)originalText.length(),
                                    Animation.RELATIVE_TO_SELF, 0.5f
                            );
                            charAnim.setDuration(150);
                            loadingText.startAnimation(charAnim);
                        }
                    }
                }, i * 100L); // 100ms delay per karakter
            }
            
            // Start blinking dots animation setelah text selesai
            handler.postDelayed(this::startBlinkingDots, originalText.length() * 100L + 300);
        }, 1500);
    }

    private void startBlinkingDots() {
        // Animation untuk blinking dots di akhir loading text
        AlphaAnimation blinkAnim = new AlphaAnimation(0.3f, 1.0f);
        blinkAnim.setDuration(500);
        blinkAnim.setRepeatMode(Animation.REVERSE);
        blinkAnim.setRepeatCount(Animation.INFINITE);
        
        loadingText.startAnimation(blinkAnim);
    }

    private void startLoadingIndicator() {
        // Optional: Cek jika ada progress bar di layout
        try {
            // Mencoba mencari progress bar dengan ID yang mungkin
            int progressBarId = getResources().getIdentifier("splash_progress", "id", getPackageName());
            if (progressBarId != 0) {
                View progressBar = findViewById(progressBarId);
                if (progressBar != null) {
                    RotateAnimation rotate = new RotateAnimation(
                            0f, 360f,
                            Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f
                    );
                    rotate.setDuration(1000);
                    rotate.setRepeatCount(Animation.INFINITE);
                    progressBar.startAnimation(rotate);
                }
            }
        } catch (Exception e) {
            // Jika tidak ada progress bar, tidak perlu melakukan apa-apa
            e.printStackTrace();
        }
    }

    private void scheduleNavigation() {
        handler.postDelayed(() -> {
            // Exit animation sebelum pindah activity
            startExitAnimation();
        }, SPLASH_DURATION);
    }

    private void startExitAnimation() {
        // Animation untuk semua elemen sebelum exit
        AnimationSet exitAnimationSet = new AnimationSet(true);
        
        // Fade out semua elemen
        AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
        fadeOut.setDuration(500);
        
        // Scale down sedikit
        ScaleAnimation scaleDown = new ScaleAnimation(
                1.0f, 0.95f, 1.0f, 0.95f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleDown.setDuration(500);
        
        exitAnimationSet.addAnimation(fadeOut);
        exitAnimationSet.addAnimation(scaleDown);
        
        // Apply ke container atau root view
        View rootView = findViewById(android.R.id.content);
        rootView.startAnimation(exitAnimationSet);
        
        exitAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            
            @Override
            public void onAnimationEnd(Animation animation) {
                navigateToMain();
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        
        // Enhanced transition animation
        try {
            // Coba gunakan animasi custom jika ada
            int fadeInId = getResources().getIdentifier("fade_in_enhanced", "anim", getPackageName());
            int fadeOutId = getResources().getIdentifier("fade_out_enhanced", "anim", getPackageName());
            
            if (fadeInId != 0 && fadeOutId != 0) {
                overridePendingTransition(fadeInId, fadeOutId);
            } else {
                // Fallback ke animasi default
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        } catch (Exception e) {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        
        finish();
    }

    @Override
    public void onBackPressed() {
        // Disable back button dengan feedback visual
        // Bisa tambahkan shake animation atau toast
        Animation shake = new TranslateAnimation(-10, 10, 0, 0);
        shake.setDuration(50);
        shake.setRepeatCount(3);
        shake.setRepeatMode(Animation.REVERSE);
        
        View rootView = findViewById(android.R.id.content);
        rootView.startAnimation(shake);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cleanup handler untuk mencegah memory leaks
        handler.removeCallbacksAndMessages(null);
    }
}