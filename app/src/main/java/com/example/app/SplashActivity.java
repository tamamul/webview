package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashActivity extends Activity {

    private ProgressBar progressBar;
    private TextView loadingText;
    private Handler handler = new Handler();
    private int progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.parseColor("#1E40AF"));

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setPadding(40, 40, 40, 40);

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.mipmap.ic_launcher);
        LinearLayout.LayoutParams lpLogo = new LinearLayout.LayoutParams(120, 120);
        lpLogo.bottomMargin = 30;
        logo.setLayoutParams(lpLogo);

        TextView title = new TextView(this);
        title.setText("Absen-MARSA");
        title.setTextSize(28);
        title.setTextColor(Color.WHITE);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView subtitle = new TextView(this);
        subtitle.setText("SMK MAARIF 9 KEBUMEN");
        subtitle.setTextSize(14);
        subtitle.setTextColor(Color.parseColor("#CCFFFFFF"));

        loadingText = new TextView(this);
        loadingText.setText("Menyiapkan aplikasi...");
        loadingText.setTextSize(16);
        loadingText.setTextColor(Color.WHITE);

        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(200, 8));

        box.addView(logo);
        box.addView(title);
        box.addView(subtitle);
        box.addView(loadingText);
        box.addView(progressBar);

        root.addView(box);
        setContentView(root);

        startLoading();
    }

    private void startLoading() {
        new Thread(() -> {
            while (progress <= 100) {
                int p = progress;
                handler.post(() -> progressBar.setProgress(p));
                progress += 2;

                try {
                    Thread.sleep(40);
                } catch (InterruptedException ignored) {}

                if (progress >= 100) {
                    handler.post(() -> {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    });
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        // Disable back
    }
}