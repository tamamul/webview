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
    private Button btnLogin;
    private SharedPreferences sharedPreferences;

    // Key untuk SharedPreferences
    private static final String PREF_NAME = "user_credentials";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER = "remember";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        cbRemember = findViewById(R.id.cbRemember);
        btnLogin = findViewById(R.id.btnLogin);

        // Check if remember me is enabled and auto-fill
        boolean rememberMe = sharedPreferences.getBoolean(KEY_REMEMBER, false);
        if (rememberMe) {
            String savedUsername = sharedPreferences.getString(KEY_USERNAME, "");
            String savedPassword = sharedPreferences.getString(KEY_PASSWORD, "");
            
            etUsername.setText(savedUsername);
            etPassword.setText(savedPassword);
            cbRemember.setChecked(true);
        }

        // Login button click
        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validasi input
        if (username.isEmpty()) {
            etUsername.setError("Username tidak boleh kosong");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password tidak boleh kosong");
            return;
        }

        // Simpan credentials jika remember me dicentang
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (cbRemember.isChecked()) {
            editor.putString(KEY_USERNAME, username);
            editor.putString(KEY_PASSWORD, password);
            editor.putBoolean(KEY_REMEMBER, true);
            editor.apply();
            Toast.makeText(this, "Login berhasil - Data tersimpan", Toast.LENGTH_SHORT).show();
        } else {
            // Hapus data yang tersimpan
            editor.remove(KEY_USERNAME);
            editor.remove(KEY_PASSWORD);
            editor.remove(KEY_REMEMBER);
            editor.apply();
            Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show();
        }

        // Pindah ke MainActivity dan kirim data credentials
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        startActivity(intent);
        finish();
    }

    // Optional: Handle back button press
    @Override
    public void onBackPressed() {
        // Keluar app ketika di login screen
        finishAffinity();
    }
}
