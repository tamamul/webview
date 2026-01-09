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
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashActivity extends Activity {

    private static final int SPLASH_DURATION = 3500; // 3.5 detik
    private Handler handler = new Handler();
    private ImageView logo;
    private TextView appName;
    private TextView welcomeText;
    private TextView loadingText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Setup window
        setupWindow();
        
        setContentView(R.layout.activity_splash);
        
        initializeViews();
        startAnimations();
        
        // Setup notifications
        NotificationScheduler.setupDailyNotifications(this);
        
        // Navigate setelah delay
        scheduleNavigation();
    }

    private void setupWindow() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    private void initializeViews() {
        logo = findViewById(R.id.splash_logo);
        appName = findViewById(R.id.splash_app_name);
        welcomeText = findViewById(R.id.splash_welcome_text);
        loadingText = findViewById(R.id.splash_loading_text);
        progressBar = findViewById(R.id.splash_progress);
        
        // DEBUG LOG
        Log.d("SplashDebug", "=== VIEW INITIALIZATION ===");
        Log.d("SplashDebug", "Logo: " + (logo != null));
        Log.d("SplashDebug", "AppName: " + (appName != null));
        Log.d("SplashDebug", "WelcomeText: " + (welcomeText != null));
        Log.d("SplashDebug", "LoadingText: " + (loadingText != null));
        Log.d("SplashDebug", "ProgressBar: " + (progressBar != null));
        
        // Force white text untuk memastikan
        if (appName != null) {
            appName.setTextColor(Color.WHITE);
        }
        if (loadingText != null) {
            loadingText.setTextColor(Color.WHITE);
        }
        if (welcomeText != null) {
            welcomeText.setTextColor(Color.WHITE);
        }
    }

    private void startAnimations() {
        // 1. Logo animation
        startLogoAnimation();
        
        // 2. Text animations dengan delay
        handler.postDelayed(this::startWelcomeAnimation, 500);
        handler.postDelayed(this::startAppNameAnimation, 1000);
        handler.postDelayed(this::startLoadingAnimation, 1500);
        
        // 3. Progress bar animation
        if (progressBar != null) {
            progressBar.setAlpha(0f);
            progressBar.animate()
                    .alpha(1f)
                    .setDuration(800)
                    .setStartDelay(2000)
                    .start();
        }
    }

    private void startLogoAnimation() {
        if (logo == null) return;
        
        logo.setAlpha(0f);
        
        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        
        // Scale dari besar ke normal
        ScaleAnimation scale = new ScaleAnimation(
                1.3f, 1.0f, 1.3f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scale.setDuration(1200);
        
        // Fade in
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1200);
        
        set.addAnimation(scale);
        set.addAnimation(fadeIn);
        
        logo.startAnimation(set);
        
        // Pulse effect setelah animasi selesai
        handler.postDelayed(() -> {
            ScaleAnimation pulse = new ScaleAnimation(
                    1.0f, 1.05f, 1.0f, 1.05f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            );
            pulse.setDuration(600);
            pulse.setRepeatMode(Animation.REVERSE);
            pulse.setRepeatCount(Animation.INFINITE);
            logo.startAnimation(pulse);
        }, 1200);
    }

    private void startWelcomeAnimation() {
        if (welcomeText != null) {
            welcomeText.setVisibility(View.VISIBLE);
            welcomeText.setAlpha(0f);
            welcomeText.setTranslationY(20f);
            
            welcomeText.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(800)
                    .start();
        }
    }

    private void startAppNameAnimation() {
        if (appName != null) {
            appName.setAlpha(0f);
            appName.setScaleX(0.8f);
            appName.setScaleY(0.8f);
            
            appName.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(1000)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
        }
    }

    private void startLoadingAnimation() {
        if (loadingText != null) {
            loadingText.setAlpha(0f);
            
            // Simple fade in
            loadingText.animate()
                    .alpha(1f)
                    .setDuration(800)
                    .start();
            
            // Typing effect
            startTypingEffect();
        }
    }

    private void startTypingEffect() {
        if (loadingText == null) return;
        
        final String originalText = loadingText.getText().toString();
        loadingText.setText("");
        
        handler.postDelayed(() -> {
            for (int i = 0; i <= originalText.length(); i++) {
                final int index = i;
                handler.postDelayed(() -> {
                    if (loadingText != null && index <= originalText.length()) {
                        loadingText.setText(originalText.substring(0, index));
                    }
                }, i * 50L);
            }
            
            // Blink dots setelah selesai
            handler.postDelayed(this::startBlinkingDots, originalText.length() * 50L + 300);
        }, 500);
    }

    private void startBlinkingDots() {
        if (loadingText != null) {
            AlphaAnimation blink = new AlphaAnimation(0.3f, 1f);
            blink.setDuration(500);
            blink.setRepeatMode(Animation.REVERSE);
            blink.setRepeatCount(Animation.INFINITE);
            loadingText.startAnimation(blink);
        }
    }

    private void scheduleNavigation() {
        handler.postDelayed(() -> {
            // Exit animation
            View root = findViewById(android.R.id.content);
            if (root != null) {
                root.animate()
                        .alpha(0f)
                        .setDuration(500)
                        .withEndAction(this::navigateToMain)
                        .start();
            } else {
                navigateToMain();
            }
        }, SPLASH_DURATION);
    }

    private void navigateToMain() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Disable back
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}