package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

// === TAMBAH IMPORTS INI ===
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONObject;
import org.json.JSONException;

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

    // === METHOD INI HARUS ADA DI DALAM CLASS ===
    private void attemptLogin() {
    String username = etUsername.getText().toString().trim();
    String password = etPassword.getText().toString().trim();

    if (username.isEmpty() || password.isEmpty()) {
        Toast.makeText(this, "Username dan password harus diisi", Toast.LENGTH_SHORT).show();
        return;
    }

    // Tampilkan loading
    btnLogin.setText("Loading...");
    btnLogin.setEnabled(false);

    new Thread(() -> {
        try {
            // Pakai API baru
            String apiUrl = "https://smkmaarif9kebumen.sch.id/present/public/api/auth/login";
            
            // Format JSON sesuai API baru
            String jsonData = "{\"login\":\"" + username + "\",\"password\":\"" + password + "\"}";

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            // Send JSON data
            OutputStream os = conn.getOutputStream();
            os.write(jsonData.getBytes("UTF-8"));
            os.flush();

            // Get response
            int responseCode = conn.getResponseCode();
            InputStream is = responseCode == 200 ? conn.getInputStream() : conn.getErrorStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            final String finalResponse = response.toString();
            
            runOnUiThread(() -> {
                try {
                    JSONObject jsonResponse = new JSONObject(finalResponse);
                    
                    if (responseCode == 200 && jsonResponse.getBoolean("status")) {
                        // Login sukses via API baru
                        Toast.makeText(LoginActivity.this, "Login berhasil via API", Toast.LENGTH_SHORT).show();
                        
                        // Simpan credentials
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(KEY_USERNAME, username);
                        editor.putString(KEY_PASSWORD, password);
                        editor.putBoolean(KEY_REMEMBER, cbRemember.isChecked());
                        editor.apply();
                        
                        // Langsung ke dashboard (bypass login page)
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("password", password);
                        intent.putExtra("api_login", true); // Flag bahwa login via API berhasil
                        startActivity(intent);
                        finish();
                        
                    } else {
                        // Login gagal
                        String errorMessage = "Login gagal";
                        
                        if (jsonResponse.has("message")) {
                            errorMessage = jsonResponse.getString("message");
                        } else if (jsonResponse.has("messages")) {
                            errorMessage = jsonResponse.getJSONObject("messages").getString("error");
                        }
                        
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        btnLogin.setText("MASUK");
                        btnLogin.setEnabled(true);
                    }
                    
                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btnLogin.setText("MASUK");
                    btnLogin.setEnabled(true);
                }
            });

        } catch (Exception e) {
            runOnUiThread(() -> {
                Toast.makeText(LoginActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                btnLogin.setText("MASUK");
                btnLogin.setEnabled(true);
            });
        }
    }).start();
}

    // === METHOD INI JUGA HARUS ADA DI DALAM CLASS ===
    private void proceedWithWebLogin(String username, String password) {
        // Simpan credentials untuk auto-fill di web
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putBoolean(KEY_REMEMBER, cbRemember.isChecked());
        editor.apply();

        // Pindah ke MainActivity (WebView)
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        startActivity(intent);
        finish();
        
        btnLogin.setText("MASUK");
        btnLogin.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        // Keluar app ketika di login screen
        finishAffinity();
    }
}
