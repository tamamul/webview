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
            // Login via API
            String apiUrl = "https://smkmaarif9kebumen.sch.id/present/public/api/login";
            String postData = "username=" + URLEncoder.encode(username, "UTF-8") + 
                            "&password=" + URLEncoder.encode(password, "UTF-8");

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            // Send request
            OutputStream os = conn.getOutputStream();
            os.write(postData.getBytes());
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

            runOnUiThread(() -> {
                if (responseCode == 200) {
                    // Login sukses via API
                    try {
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        String token = jsonResponse.getString("token");
                        
                        // Simpan credentials & token
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(KEY_USERNAME, username);
                        editor.putString(KEY_PASSWORD, password);
                        editor.putString("api_token", token);
                        editor.putBoolean(KEY_REMEMBER, cbRemember.isChecked());
                        editor.apply();

                        Toast.makeText(this, "Login berhasil via API", Toast.LENGTH_SHORT).show();
                        
                        // Langsung ke MainActivity (dashboard)
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("password", password);
                        intent.putExtra("api_token", token);
                        startActivity(intent);
                        finish();
                        
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        btnLogin.setText("MASUK");
                        btnLogin.setEnabled(true);
                    }
                } else {
                    // Fallback ke login web biasa
                    Toast.makeText(this, "Login via API gagal, menggunakan login web", Toast.LENGTH_LONG).show();
                    proceedWithWebLogin(username, password);
                }
            });

        } catch (Exception e) {
            runOnUiThread(() -> {
                // Fallback ke login web biasa
                Toast.makeText(this, "Menggunakan login web", Toast.LENGTH_SHORT).show();
                proceedWithWebLogin(username, password);
            });
        }
    }).start();
}

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
