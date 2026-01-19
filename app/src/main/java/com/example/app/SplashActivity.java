package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    
    // Durasi tampilan splash screen (dalam milidetik)
    private static final int SPLASH_DURATION = 2000; // 2 detik
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Catatan: Tidak perlu setContentView() karena 
        // background sudah diatur melalui android:theme di manifest
        
        // Handler untuk delay dan pindah ke MainActivity
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Pindah ke MainActivity
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                
                // Animasi transisi (opsional)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                
                // Tutup splash activity agar tidak bisa kembali
                finish();
            }
        }, SPLASH_DURATION);
    }
    
    @Override
    public void onBackPressed() {
        // Nonaktifkan back button selama di splash screen
        // Supaya user tidak bisa membatalkan splash screen
        // Do nothing
    }
}