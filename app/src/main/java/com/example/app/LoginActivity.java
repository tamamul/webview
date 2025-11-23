package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

    private EditText etUsername, etPassword;
    private CheckBox cbRemember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        cbRemember = findViewById(R.id.cbRemember);
        Button btnLogin = findViewById(R.id.btnLogin);

        // Check if remember me is enabled and auto-fill
        SharedPreferences prefs = getSharedPreferences("user_credentials", MODE_PRIVATE);
        boolean rememberMe = prefs.getBoolean("remember", false);
        if (rememberMe) {
            String savedUsername = prefs.getString("username", "");
            String savedPassword = prefs.getString("password", "");
            
            etUsername.setText(savedUsername);
            etPassword.setText(savedPassword);
            cbRemember.setChecked(true);
        }

        // Login button click - SIMPLE VERSION
        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username dan password harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        // SIMPAN CREDENTIALS SAJA
        SharedPreferences.Editor editor = getSharedPreferences("user_credentials", MODE_PRIVATE).edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putBoolean("remember", cbRemember.isChecked());
        editor.apply();

        Toast.makeText(this, "Membuka aplikasi...", Toast.LENGTH_SHORT).show();

        // LANGSUNG KE MAIN ACTIVITY (nanti auto login di background)
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Keluar app ketika di login screen
        finishAffinity();
    }
}
