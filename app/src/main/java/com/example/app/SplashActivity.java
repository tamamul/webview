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
import android.widget.TextView;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // Buat layout 100% programmatic
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.parseColor("#1E40AF"));
        
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER);
        
        // Convert dp to pixels
        int padding = (int) (40 * getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding, padding, padding);
        
        // Logo
        ImageView logo = new ImageView(this);
        try {
            logo.setImageResource(R.mipmap.ic_launcher);
        } catch (Exception e) {
            // Fallback shape
            logo.setBackgroundColor(Color.WHITE);
        }
        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(
            (int) (150 * getResources().getDisplayMetrics().density),
            (int) (150 * getResources().getDisplayMetrics().density)
        );
        logoParams.bottomMargin = (int) (30 * getResources().getDisplayMetrics().density);
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
        nameParams.bottomMargin = (int) (10 * getResources().getDisplayMetrics().density);
        appName.setLayoutParams(nameParams);
        
        // Subtitle
        TextView subtitle = new TextView(this);
        subtitle.setText("SMK MAARIF 9 KEBUMEN");
        subtitle.setTextColor(Color.argb(204, 255, 255, 255)); // 80% white
        subtitle.setTextSize(14);
        LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        subParams.bottomMargin = (int) (40 * getResources().getDisplayMetrics().density);
        subtitle.setLayoutParams(subParams);
        
        // Loading text
        TextView loadingText = new TextView(this);
        loadingText.setText("Menyiapkan aplikasi...");
        loadingText.setTextColor(Color.WHITE);
        loadingText.setTextSize(16);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textParams.topMargin = (int) (20 * getResources().getDisplayMetrics().density);
        loadingText.setLayoutParams(textParams);
        
        // Add views to container
        container.addView(logo);
        container.addView(appName);
        container.addView(subtitle);
        container.addView(loadingText);
        
        // Add container to root
        root.addView(container, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT,
            Gravity.CENTER
        ));
        
        setContentView(root);
        
        // Navigate to MainActivity after 3 seconds
        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, 3000);
    }
    
    @Override
    public void onBackPressed() {
        // Disable back button
    }
}