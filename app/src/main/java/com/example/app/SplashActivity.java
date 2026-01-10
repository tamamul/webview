package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

public class SplashActivity extends Activity {

    private static final int SPLASH_DURATION = 4000; // 4 detik untuk animasi lengkap
    private Handler handler = new Handler();
    
    // Elemen UI
    private ImageView logo;
    private TextView appName;
    private TextView appSubtitle;
    private TextView loadingText;
    private TextView versionText;
    private ProgressBar progressBar;
    
    // Informasi aplikasi
    private String appVersion = "v1.0.0";
    private String schoolName = "SMK MAARIF 9 KEBUMEN";
    private String loadingMessages[] = {
        "Menyiapkan aplikasi...",
        "Memuat modul presensi...",
        "Menyambungkan ke server...",
        "Siap digunakan!"
    };
    private int currentMessage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Setup window untuk immersive experience
        setupImmersiveWindow();
        
        setContentView(R.layout.activity_splash);
        
        initializeViews();
        setupAppInfo();
        startWelcomeSequence();
        
        // Setup background tasks
        setupBackgroundTasks();
        
        // Schedule navigation to MainActivity
        scheduleNavigation();
    }

    private void setupImmersiveWindow() {
        // Fullscreen dengan status bar transparan
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.BLACK);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        
        // Keep screen on selama splash
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void initializeViews() {
        logo = findViewById(R.id.splash_logo);
        appName = findViewById(R.id.splash_app_name);
        appSubtitle = findViewById(R.id.splash_app_subtitle);
        loadingText = findViewById(R.id.splash_loading_text);
        versionText = findViewById(R.id.splash_version_text);
        progressBar = findViewById(R.id.splash_progress);
        
        // Set initial states
        if (logo != null) logo.setAlpha(0f);
        if (appName != null) appName.setAlpha(0f);
        if (appSubtitle != null) appSubtitle.setAlpha(0f);
        if (loadingText != null) loadingText.setAlpha(0f);
        if (versionText != null) versionText.setAlpha(0f);
        if (progressBar != null) progressBar.setAlpha(0f);
    }

    private void setupAppInfo() {
        try {
            // Get version info from package
            appVersion = "v" + getPackageManager()
                .getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            appVersion = "v1.0.0";
        }
        
        if (versionText != null) {
            versionText.setText(appVersion);
        }
        
        if (appSubtitle != null) {
            appSubtitle.setText(schoolName);
        }
    }

    private void startWelcomeSequence() {
        // Sequence 1: Logo masuk dari atas dengan bounce
        handler.postDelayed(this::animateLogoEntrance, 300);
        
        // Sequence 2: App name muncul dengan scale effect
        handler.postDelayed(this::animateAppName, 800);
        
        // Sequence 3: Subtitle slide in
        handler.postDelayed(this::animateSubtitle, 1200);
        
        // Sequence 4: Loading text dengan progresif message
        handler.postDelayed(this::startLoadingSequence, 1600);
        
        // Sequence 5: Progress bar muncul
        handler.postDelayed(this::animateProgressBar, 2000);
        
        // Sequence 6: Version text fade in
        handler.postDelayed(this::animateVersionText, 2400);
    }

    private void animateLogoEntrance() {
        if (logo == null) return;
        
        // Animasi: Logo jatuh dari atas dengan bounce
        TranslateAnimation translate = new TranslateAnimation(
                0, 0, -500, 0
        );
        translate.setDuration(1200);
        translate.setInterpolator(new BounceInterpolator());
        
        // Fade in bersamaan
        AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(800);
        
        // Scale sedikit saat landing
        ScaleAnimation scale = new ScaleAnimation(
                1.1f, 1.0f, 1.1f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scale.setDuration(600);
        scale.setStartOffset(400);
        
        // Gabungkan semua animasi
        AnimationSet set = new AnimationSet(false);
        set.addAnimation(translate);
        set.addAnimation(fadeIn);
        set.addAnimation(scale);
        
        logo.startAnimation(set);
        
        // Setelah animasi selesai, tambahkan glow effect
        handler.postDelayed(() -> {
            logo.animate()
                .scaleX(1.05f)
                .scaleY(1.05f)
                .setDuration(1000)
                .withEndAction(() -> logo.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(1000))
                .start();
        }, 1600);
    }

    private void animateAppName() {
        if (appName == null) return;
        
        // Typewriter effect simulation
        final String fullText = appName.getText().toString();
        appName.setText("");
        
        // Animate each character
        for (int i = 0; i <= fullText.length(); i++) {
            final int index = i;
            handler.postDelayed(() -> {
                if (index <= fullText.length()) {
                    appName.setText(fullText.substring(0, index));
                    
                    // Scale animation untuk setiap huruf baru
                    if (index > 0) {
                        ScaleAnimation charAnim = new ScaleAnimation(
                                1.3f, 1.0f, 1.3f, 1.0f,
                                Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.5f
                        );
                        charAnim.setDuration(150);
                        appName.startAnimation(charAnim);
                    }
                }
            }, i * 100L);
        }
        
        // Fade in setelah typing selesai
        handler.postDelayed(() -> {
            appName.animate()
                .alpha(1f)
                .setDuration(500)
                .start();
        }, fullText.length() * 100L + 300);
    }

    private void animateSubtitle() {
        if (appSubtitle == null) return;
        
        // Slide up dari bawah
        appSubtitle.setTranslationY(50f);
        appSubtitle.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(800)
            .setInterpolator(new BounceInterpolator())
            .start();
    }

    private void startLoadingSequence() {
        if (loadingText == null) return;
        
        loadingText.setAlpha(1f);
        updateLoadingMessage();
        
        // Update progress bar secara progresif
        updateProgressBarProgress();
    }

    private void updateLoadingMessage() {
        if (loadingText == null || currentMessage >= loadingMessages.length) return;
        
        // Fade out message lama
        loadingText.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction(() -> {
                // Ganti teks baru
                loadingText.setText(loadingMessages[currentMessage]);
                
                // Fade in message baru
                loadingText.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start();
                
                currentMessage++;
                
                // Update berikutnya jika masih ada message
                if (currentMessage < loadingMessages.length) {
                    handler.postDelayed(this::updateLoadingMessage, 800);
                }
            })
            .start();
    }

    private void updateProgressBarProgress() {
        if (progressBar == null) return;
        
        // Animate progress dari 0 ke 100 dalam 4 tahap
        final int totalSteps = loadingMessages.length;
        final int stepDuration = 800;
        
        for (int i = 1; i <= totalSteps; i++) {
            final int progress = (i * 100) / totalSteps;
            handler.postDelayed(() -> {
                if (progressBar != null) {
                    progressBar.setProgress(progress);
                    
                    // Pulse effect setiap progress update
                    progressBar.animate()
                        .scaleX(1.05f)
                        .scaleY(1.05f)
                        .setDuration(200)
                        .withEndAction(() -> progressBar.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(200)
                            .start())
                        .start();
                }
            }, i * stepDuration);
        }
    }

    private void animateProgressBar() {
        if (progressBar == null) return;
        
        progressBar.setAlpha(0f);
        progressBar.setScaleX(0.5f);
        progressBar.setScaleY(0.5f);
        
        progressBar.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(600)
            .start();
    }

    private void animateVersionText() {
        if (versionText == null) return;
        
        versionText.setAlpha(0f);
        versionText.setTranslationY(20f);
        
        versionText.animate()
            .alpha(0.7f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(200)
            .start();
    }

    private void setupBackgroundTasks() {
        // Setup notification service
        handler.post(() -> {
            try {
                NotificationScheduler.setupDailyNotifications(this);
                Log.d("SplashActivity", "Notifications scheduled");
            } catch (Exception e) {
                Log.e("SplashActivity", "Failed to setup notifications", e);
            }
        });
        
        // Pre-load webview cache (optional)
        handler.post(() -> {
            // Pre-initialize webview settings untuk performa lebih cepat
            // atau load data awal di background
        });
    }

    private void scheduleNavigation() {
        handler.postDelayed(() -> {
            startExitAnimation();
        }, SPLASH_DURATION);
    }

    private void startExitAnimation() {
        // Animate semua elemen keluar dengan efek zoom out
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            // Zoom out dan fade
            rootView.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .alpha(0f)
                .setDuration(500)
                .withEndAction(this::navigateToMain)
                .start();
        } else {
            navigateToMain();
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        
        // Enhanced transition
        overridePendingTransition(R.anim.fade_in_enhanced, R.anim.fade_out_enhanced);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Disable back button dengan feedback visual
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            // Shake animation sebagai feedback
            rootView.animate()
                .translationX(20)
                .setDuration(50)
                .withEndAction(() -> rootView.animate()
                    .translationX(-20)
                    .setDuration(50)
                    .withEndAction(() -> rootView.animate()
                        .translationX(0)
                        .setDuration(50)
                        .start())
                    .start())
                .start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}