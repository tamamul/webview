package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // Buat layout programmatically
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.parseColor("#1E40AF"));
        root.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT));
        
        // Container utama
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER);
        
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        container.setLayoutParams(params);
        
        // Logo
        ImageView logo = new ImageView(this);
        logo.setImageResource(R.mipmap.ic_launcher);
        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(100, 100);
        logoParams.bottomMargin = 20;
        logo.setLayoutParams(logoParams);
        
        // App name
        TextView appName = new TextView(this);
        appName.setText("Absen-MARSA");
        appName.setTextColor(Color.WHITE);
        appName.setTextSize(24);
        appName.setTypeface(appName.getTypeface(), android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        nameParams.bottomMargin = 10;
        appName.setLayoutParams(nameParams);
        
        // Loading text
        TextView loadingText = new TextView(this);
        loadingText.setText("Memuat aplikasi...");
        loadingText.setTextColor(Color.WHITE);
        loadingText.setTextSize(14);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams.topMargin = 20;
        loadingText.setLayoutParams(textParams);
        
        // Tambahkan semua view
        container.addView(logo);
        container.addView(appName);
        container.addView(loadingText);
        root.addView(container);
        
        setContentView(root);
        
        // Animasi sederhana
        logo.setAlpha(0f);
        appName.setAlpha(0f);
        loadingText.setAlpha(0f);
        
        logo.animate().alpha(1f).setDuration(1500).start();
        appName.animate().alpha(1f).setDuration(1000).setStartDelay(800).start();
        loadingText.animate().alpha(1f).setDuration(800).setStartDelay(1500).start();
        
        // Setup notifications (optional)
        try {
            NotificationScheduler.setupDailyNotifications(this);
        } catch (Exception e) {
            // Ignore
        }
        
        // Pindah ke main activity
        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, 3000);
    }
    
    @Override
    public void onBackPressed() {
        // Disable
    }
}