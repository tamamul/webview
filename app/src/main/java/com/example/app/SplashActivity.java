package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.Random;

public class SplashActivity extends Activity {
    
    // UI Components
    private ImageView logoImage;
    private TextView appNameText, loadingText, welcomeText, tipText;
    private LinearLayout loadingContainer;
    private ProgressBar circularProgress, horizontalProgress;
    
    // Animation & Handler
    private Handler loadingHandler = new Handler();
    private int progressValue = 0;
    private boolean isFirstLoad = true;
    
    // Random loading tips
    private String[] loadingTips = {
        "Menyiapkan aplikasi...",
        "Memuat data pengguna...",
        "Menyiapkan sesi...",
        "Memuat konten...",
        "Mengoptimalkan performa...",
        "Menyiapkan antarmuka...",
        "Memuat preferensi...",
        "Menyiapkan cache..."
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Fullscreen tanpa action bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_splash);
        
        // Initialize views
        initializeViews();
        
        // Check if first load or returning
        checkFirstLoad();
        
        // Start the awesome loading sequence
        startLoadingSequence();
    }
    
    private void initializeViews() {
        logoImage = findViewById(R.id.logoImage);
        appNameText = findViewById(R.id.appNameText);
        loadingText = findViewById(R.id.loadingText);
        welcomeText = findViewById(R.id.welcomeText);
        tipText = findViewById(R.id.tipText);
        loadingContainer = findViewById(R.id.loadingContainer);
        circularProgress = findViewById(R.id.circularProgress);
        horizontalProgress = findViewById(R.id.horizontalProgress);
        
        // Set initial states
        logoImage.setAlpha(0f);
        appNameText.setAlpha(0f);
        welcomeText.setAlpha(0f);
        tipText.setAlpha(0f);
        loadingContainer.setAlpha(0f);
        
        // Set random tip
        setRandomTip();
    }
    
    private void checkFirstLoad() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        isFirstLoad = prefs.getBoolean("first_load", true);
        
        if (isFirstLoad) {
            welcomeText.setText("Selamat Datang!");
        } else {
            welcomeText.setText("Memuat aplikasi...");
        }
    }
    
    private void setRandomTip() {
        Random random = new Random();
        int index = random.nextInt(loadingTips.length);
        tipText.setText(loadingTips[index]);
    }
    
    private void startLoadingSequence() {
        // Step 1: Logo entry animation (3D flip)
        animateLogoEntry();
        
        // Step 2: App name animation with delay
        loadingHandler.postDelayed(() -> animateAppName(), 500);
        
        // Step 3: Welcome text animation
        loadingHandler.postDelayed(() -> animateWelcomeText(), 800);
        
        // Step 4: Show loading UI
        loadingHandler.postDelayed(() -> showLoadingUI(), 1200);
        
        // Step 5: Start progress simulation
        loadingHandler.postDelayed(() -> startProgressSimulation(), 1500);
        
        // Step 6: Check credentials and proceed
        loadingHandler.postDelayed(() -> checkCredentialsAndProceed(), 3000);
    }
    
    private void animateLogoEntry() {
        // 3D Flip animation
        AnimationSet flipSet = new AnimationSet(true);
        
        // Rotate animation (3D flip effect)
        RotateAnimation rotate = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setInterpolator(new LinearInterpolator());
        
        // Scale animation (pop effect)
        ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(800);
        
        // Fade animation
        AlphaAnimation fade = new AlphaAnimation(0, 1);
        fade.setDuration(800);
        
        flipSet.addAnimation(rotate);
        flipSet.addAnimation(scale);
        flipSet.addAnimation(fade);
        
        // Start animation
        logoImage.startAnimation(flipSet);
        logoImage.setAlpha(1f);
        
        // Add continuous subtle rotation after entry
        loadingHandler.postDelayed(() -> addSubtleRotation(), 1100);
    }
    
    private void addSubtleRotation() {
        RotateAnimation subtleRotate = new RotateAnimation(0, 5,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        subtleRotate.setDuration(2000);
        subtleRotate.setRepeatMode(Animation.REVERSE);
        subtleRotate.setRepeatCount(Animation.INFINITE);
        subtleRotate.setInterpolator(new LinearInterpolator());
        logoImage.startAnimation(subtleRotate);
    }
    
    private void animateAppName() {
        AnimationSet nameSet = new AnimationSet(true);
        
        // Slide up animation
        TranslateAnimation slide = new TranslateAnimation(
                0, 0, 100, 0);
        slide.setDuration(600);
        
        // Fade animation
        AlphaAnimation fade = new AlphaAnimation(0, 1);
        fade.setDuration(600);
        
        // Glow effect (scale)
        ScaleAnimation glow = new ScaleAnimation(1, 1.1f, 1, 1.1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        glow.setDuration(800);
        glow.setRepeatMode(Animation.REVERSE);
        glow.setRepeatCount(Animation.INFINITE);
        
        nameSet.addAnimation(slide);
        nameSet.addAnimation(fade);
        nameSet.addAnimation(glow);
        
        appNameText.startAnimation(nameSet);
        appNameText.setAlpha(1f);
    }
    
    private void animateWelcomeText() {
        AnimationSet welcomeSet = new AnimationSet(true);
        
        // Fade in
        AlphaAnimation fade = new AlphaAnimation(0, 1);
        fade.setDuration(400);
        
        // Typewriter effect simulation
        final String fullText = welcomeText.getText().toString();
        welcomeText.setText("");
        
        loadingHandler.postDelayed(() -> {
            new Thread(() -> {
                for (int i = 0; i <= fullText.length(); i++) {
                    final int index = i;
                    runOnUiThread(() -> {
                        welcomeText.setText(fullText.substring(0, index));
                    });
                    
                    try {
                        Thread.sleep(50); // Speed of typing
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }, 400);
        
        welcomeSet.addAnimation(fade);
        welcomeText.startAnimation(welcomeSet);
        welcomeText.setAlpha(1f);
    }
    
    private void showLoadingUI() {
        // Fade in loading container
        AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(500);
        
        loadingContainer.startAnimation(fadeIn);
        loadingContainer.setAlpha(1f);
        
        // Animate tip text
        animateTipText();
    }
    
    private void animateTipText() {
        // Fade in tip
        AlphaAnimation fade = new AlphaAnimation(0, 1);
        fade.setDuration(300);
        tipText.startAnimation(fade);
        tipText.setAlpha(1f);
        
        // Change tip every 2 seconds
        loadingHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressValue < 100) {
                    setRandomTip();
                    
                    // Pulse animation for new tip
                    ScaleAnimation pulse = new ScaleAnimation(1, 1.05f, 1, 1.05f,
                            Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
                    pulse.setDuration(200);
                    pulse.setRepeatMode(Animation.REVERSE);
                    pulse.setRepeatCount(1);
                    tipText.startAnimation(pulse);
                    
                    loadingHandler.postDelayed(this, 2000);
                }
            }
        }, 2000);
    }
    
    private void startProgressSimulation() {
        new Thread(() -> {
            while (progressValue < 100) {
                progressValue += 2;
                
                runOnUiThread(() -> {
                    circularProgress.setProgress(progressValue);
                    horizontalProgress.setProgress(progressValue);
                    loadingText.setText(progressValue + "%");
                    
                    // Special effects at certain percentages
                    if (progressValue % 25 == 0) {
                        celebrateMilestone(progressValue);
                    }
                });
                
                try {
                    // Simulate different loading times
                    int delay = 50 + (int)(Math.random() * 30);
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    private void celebrateMilestone(int percentage) {
        // Bounce animation for progress bars
        ScaleAnimation bounce = new ScaleAnimation(
                1f, 1.2f, 1f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        bounce.setDuration(200);
        bounce.setRepeatMode(Animation.REVERSE);
        bounce.setRepeatCount(1);
        
        circularProgress.startAnimation(bounce);
        horizontalProgress.startAnimation(bounce);
        
        // Color change at 50%
        if (percentage == 50) {
            // Can add color animation here
        }
    }
    
    private void checkCredentialsAndProceed() {
        // Check credentials in background
        new Thread(() -> {
            try {
                // Simulate credential checking
                Thread.sleep(2500);
                
                // Get credentials
                SharedPreferences prefs = getSharedPreferences("user_credentials", MODE_PRIVATE);
                String username = prefs.getString("username", "");
                String password = prefs.getString("password", "");
                
                runOnUiThread(() -> {
                    if (username.isEmpty() || password.isEmpty()) {
                        // No credentials - go to LoginActivity
                        navigateToLogin();
                    } else {
                        // Has credentials - go to MainActivity
                        navigateToMain();
                    }
                });
                
            } catch (InterruptedException e) {
                e.printStackTrace();
                runOnUiThread(() -> navigateToMain());
            }
        }).start();
    }
    
    private void navigateToLogin() {
        // Smooth exit animation
        AnimationSet exitSet = new AnimationSet(true);
        
        AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(500);
        
        TranslateAnimation slideUp = new TranslateAnimation(
                0, 0, 0, -100);
        slideUp.setDuration(500);
        
        exitSet.addAnimation(fadeOut);
        exitSet.addAnimation(slideUp);
        
        loadingContainer.startAnimation(exitSet);
        
        exitSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            
            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }
    
    private void navigateToMain() {
        // Mark first load as complete
        SharedPreferences.Editor editor = getSharedPreferences("app_prefs", MODE_PRIVATE).edit();
        editor.putBoolean("first_load", false);
        editor.apply();
        
        // Success animation
        ScaleAnimation successScale = new ScaleAnimation(
                1f, 1.5f, 1f, 1.5f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        successScale.setDuration(400);
        
        AlphaAnimation successFade = new AlphaAnimation(1, 0);
        successFade.setDuration(400);
        
        logoImage.startAnimation(successScale);
        loadingContainer.startAnimation(successFade);
        
        successFade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            
            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra("username", getUsernameFromPrefs());
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }
    
    private String getUsernameFromPrefs() {
        SharedPreferences prefs = getSharedPreferences("user_credentials", MODE_PRIVATE);
        return prefs.getString("username", "");
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // Clean up handlers
        loadingHandler.removeCallbacksAndMessages(null);
    }
    
    @Override
    public void onBackPressed() {
        // Disable back button during splash
        // Optional: Add exit confirmation if needed
    }
}