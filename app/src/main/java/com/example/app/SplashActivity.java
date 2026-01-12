// SplashActivity.java - PROGRAMMATIC LAYOUT
package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashActivity extends Activity {

    private Handler handler = new Handler();
    private ProgressBar progressBar;
    private TextView loadingText;
    private int progressStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // Buat layout secara programmatic
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.parseColor("#1E40AF"));
        
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER);
        container.setPadding(40, 40, 40, 40);
        
        // Logo
        ImageView logo = new ImageView(this);
        logo.setImageResource(R.mipmap.ic_launcher);
        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(120, 120);
        logoParams.bottomMargin = 30;
        logo.setLayoutParams(logoParams);
        
        // App name
        TextView appName = new TextView(this);
        appName.setText("Absen-MARSA");
        appName.setTextColor(Color.WHITE);
        appName.setTextSize(28);
        appName.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        nameParams.bottomMargin = 10;
        appName.setLayoutParams(nameParams);
        
        // Subtitle
        TextView subtitle = new TextView(this);
        subtitle.setText("SMK MAARIF 9 KEBUMEN");
        subtitle.setTextColor(Color.parseColor("#CCFFFFFF"));
        subtitle.setTextSize(14);
        LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        subParams.bottomMargin = 40;
        subtitle.setLayoutParams(subParams);
        
        // Loading text
        loadingText = new TextView(this);
        loadingText.setText("Menyiapkan aplikasi...");
        loadingText.setTextColor(Color.WHITE);
        loadingText.setTextSize(16);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textParams.bottomMargin = 20;
        loadingText.setLayoutParams(textParams);
        
        // Progress bar
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(200, 8);
        progressParams.bottomMargin = 40;
        progressBar.setLayoutParams(progressParams);
        
        // Version
        TextView version = new TextView(this);
        version.setText("v1.0.0");
        version.setTextColor(Color.parseColor("#88FFFFFF"));
        version.setTextSize(11);
        
        // Add all views
        container.addView(logo);
        container.addView(appName);
        container.addView(subtitle);
        container.addView(loadingText);
        container.addView(progressBar);
        container.addView(version);
        
        root.addView(container, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ));
        
        setContentView(root);
        
        // Start progress animation
        startProgressAnimation();
        
        // Navigate after 4 seconds
        handler.postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, 4000);
    }
    
    private void startProgressAnimation() {
        new Thread(() -> {
            String[] messages = {
                "Menyiapkan aplikasi...",
                "Memuat modul presensi...",
                "Menyambungkan ke server...",
                "Siap digunakan!"
            };
            
            for (int i = 0; i <= 100; i++) {
                progressStatus = i;
                
                handler.post(() -> {
                    progressBar.setProgress(progressStatus);
                    
                    if (progressStatus < 25) {
                        loadingText.setText(messages[0]);
                    } else if (progressStatus < 50) {
                        loadingText.setText(messages[1]);
                    } else if (progressStatus < 75) {
                        loadingText.setText(messages[2]);
                    } else {
                        loadingText.setText(messages[3]);
                    }
                });
                
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    @Override
    public void onBackPressed() {
        // Disable back
    }
}