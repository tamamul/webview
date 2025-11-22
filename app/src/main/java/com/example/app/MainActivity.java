package com.example.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

public class MainActivity extends Activity {

    private WebView mWebView;
    private ValueCallback<Uri[]> uploadMessage;
    private final static int FILE_CHOOSER_RESULT_CODE = 1;
    private final static int PERMISSION_REQUEST_CODE = 100;

    // Variables untuk auto-fill
    private String username = "";
    private String password = "";

    // Daftar permission yang diperlukan
    private String[] requiredPermissions = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO
    };

@Override
@SuppressLint("SetJavaScriptEnabled")
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    // Initialize WebView
    mWebView = findViewById(R.id.activity_main_webview);
    
    // Get credentials dari LoginActivity atau SharedPreferences
    getCredentials();
    
    // Cek dan minta permission sebelum setup WebView
    if (checkAndRequestPermissions()) {
        setupWebView();
        
        // === TAMBAH INI ===
        // Cek apakah ada API token, jika ada langsung ke dashboard
        SharedPreferences prefs = getSharedPreferences("user_credentials", MODE_PRIVATE);
        String apiToken = prefs.getString("api_token", "");
        
        if (!apiToken.isEmpty()) {
            // Langsung buka dashboard, bypass login page
            mWebView.loadUrl("https://smkmaarif9kebumen.sch.id/present/public/dashboard");
        } else {
            // Fallback ke URL biasa (auto login via JavaScript)
            mWebView.loadUrl("https://smkmaarif9kebumen.sch.id/present/public/");
        }
    }
}

private void getCredentials() {
    // Coba ambil dari Intent (langsung dari LoginActivity)
    Intent intent = getIntent();
    if (intent != null && intent.hasExtra("username") && intent.hasExtra("password")) {
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        
        // Juga cek apakah ada API token
        if (intent.hasExtra("api_token")) {
            String apiToken = intent.getStringExtra("api_token");
            SharedPreferences prefs = getSharedPreferences("user_credentials", MODE_PRIVATE);
            prefs.edit().putString("api_token", apiToken).apply();
        }
    } else {
        // Fallback: ambil dari SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_credentials", MODE_PRIVATE);
        username = prefs.getString("username", "");
        password = prefs.getString("password", "");
    }
    
    Toast.makeText(this, "Selamat datang " + username, Toast.LENGTH_SHORT).show();
}

    private boolean checkAndRequestPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();

        // Cek setiap permission
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission);
            }
        }

        // Jika ada permission yang belum granted, minta izin
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsNeeded.toArray(new String[0]),
                    PERMISSION_REQUEST_CODE
            );
            return false;
        }
        
        // Semua permission sudah granted
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            
            // Cek apakah semua permission di-grant
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            
            if (allGranted) {
                // Semua permission granted, setup WebView
                setupWebView();
            } else {
                // Beberapa permission ditolak
                Toast.makeText(this, "Beberapa fitur mungkin tidak berfungsi tanpa izin yang diperlukan", Toast.LENGTH_LONG).show();
                setupWebView();
            }
        }
    }

    private void setupWebView() {
        WebSettings webSettings = mWebView.getSettings();
        
        // Enable JavaScript
        webSettings.setJavaScriptEnabled(true);
        
        // Enable DOM storage
        webSettings.setDomStorageEnabled(true);
        
        // Enable database
        webSettings.setDatabaseEnabled(true);
        
        // Enable geolocation
        webSettings.setGeolocationEnabled(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            webSettings.setGeolocationDatabasePath(getFilesDir().getPath());
        }
        
        // Enable zoom controls
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        
        // Enable wide viewport
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        
        // Enable mixed content (for HTTP/HTTPS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        
        // Cache settings
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        
        // Enable other important settings
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        
        // CRITICAL FOR CAMERA: Enable media playback without gesture
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        
        // Enable WebRTC - Important for camera access
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        
        // Set WebViewClient to handle links internally dengan auto-fill
        mWebView.setWebViewClient(new MyWebViewClient());
        
        // Set WebChromeClient untuk permissions
        mWebView.setWebChromeClient(new MyWebChromeClient());
        
        // Load URL
        mWebView.loadUrl("https://smkmaarif9kebumen.sch.id/present/public/");
    }

    private class MyWebViewClient extends WebViewClient {
        
        @Override
public void onPageFinished(WebView view, String url) {
    super.onPageFinished(view, url);
    
    // Auto login hanya jika:
    // 1. Ada credentials
    // 2. Dan di halaman login (bukan dashboard/halaman lain)
    if (!username.isEmpty() && !password.isEmpty()) {
        if (url.contains("/present/public") && !url.contains("dashboard")) {
            // Tunggu sebentar lalu execute auto login
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    autoLoginToWebsite();
                }
            }, 1000);
        }
    }
}
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Handle all URLs within WebView
            if (url.startsWith("tel:") || url.startsWith("mailto:") || url.startsWith("whatsapp:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
            
            // Load all other URLs in WebView
            view.loadUrl(url);
            return true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, android.webkit.WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String url = request.getUrl().toString();
                
                if (url.startsWith("tel:") || url.startsWith("mailto:") || url.startsWith("whatsapp:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                
                view.loadUrl(url);
                return true;
            }
            return false;
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        
        // Handle geolocation permission prompt
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            // Always grant location permission
            callback.invoke(origin, true, false);
        }
        
        // Handle permission requests (camera, microphone)
        @Override
        public void onPermissionRequest(final PermissionRequest request) {
            // Langsung grant permission tanpa delay
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Grant semua permission yang diminta
                        request.grant(request.getResources());
                    }
                });
            }
        }

        // For file upload (Lollipop and above)
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
            }
            uploadMessage = filePathCallback;

            Intent intent = fileChooserParams.createIntent();
            try {
                startActivityForResult(intent, FILE_CHOOSER_RESULT_CODE);
            } catch (Exception e) {
                uploadMessage = null;
                return false;
            }
            return true;
        }

        // For Android < 5.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "*/*");
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            openFileChooser(uploadMsg, acceptType, null);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILE_CHOOSER_RESULT_CODE);
        }
    }

    // Method BARU - AUTO LOGIN via POST
private void autoLoginToWebsite() {
    String jsCode = 
        "setTimeout(function() {" +
        "  console.log('Starting auto login...');" +
        "  " +
        "  // Cari form dengan action yang mengandung 'login'" +
        "  var loginForm = document.querySelector('form[action*=\"login\"]');" +
        "  " +
        "  if (loginForm) {" +
        "    console.log('Login form found:', loginForm.action);" +
        "    " +
        "    // Input fields berdasarkan name attribute" +
        "    var emailInput = document.querySelector('input[name=\"login\"]');" +
        "    var passwordInput = document.querySelector('input[name=\"password\"]');" +
        "    var rememberCheckbox = document.querySelector('input[name=\"remember\"]');" +
        "    " +
        "    console.log('Email input:', emailInput);" +
        "    console.log('Password input:', passwordInput);" +
        "    " +
        "    if (emailInput && passwordInput) {" +
        "      // Isi credentials" +
        "      emailInput.value = '" + username + "';" +
        "      passwordInput.value = '" + password + "';" +
        "      " +
        "      // Centang remember me jika ada" +
        "      if (rememberCheckbox) {" +
        "        rememberCheckbox.checked = true;" +
        "      }" +
        "      " +
        "      // Trigger semua events yang diperlukan" +
        "      var events = ['input', 'change', 'blur', 'focus'];" +
        "      events.forEach(function(eventType) {" +
        "        var event = new Event(eventType, { bubbles: true });" +
        "        emailInput.dispatchEvent(event);" +
        "        passwordInput.dispatchEvent(event);" +
        "      });" +
        "      " +
        "      console.log('Credentials filled, submitting form...');" +
        "      " +
        "      // Submit form langsung (tunggu 1 detik dulu)" +
        "      setTimeout(function() {" +
        "        loginForm.submit();" +
        "        console.log('Form submitted!');" +
        "      }, 1000);" +
        "      " +
        "    } else {" +
        "      console.log('Required input fields not found');" +
        "    }" +
        "  } else {" +
        "    console.log('No login form found on this page');" +
        "    " +
        "    // Fallback: coba cari form apapun dengan input email & password" +
        "    var forms = document.querySelectorAll('form');" +
        "    forms.forEach(function(form, index) {" +
        "      var hasEmail = form.querySelector('input[type=\"email\"], input[name*=\"login\"]');" +
        "      var hasPassword = form.querySelector('input[type=\"password\"]');" +
        "      " +
        "      if (hasEmail && hasPassword) {" +
        "        console.log('Found potential login form at index:', index);" +
        "        loginForm = form;" +
        "      }" +
        "    });" +
        "  }" +
        "}, 500);"; // Waktu tunggu lebih pendek

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        mWebView.evaluateJavascript(jsCode, null);
    } else {
        mWebView.loadUrl("javascript:" + jsCode);
    }
}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (uploadMessage == null) return;
            
            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }
            uploadMessage.onReceiveValue(results);
            uploadMessage = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            // Kembali ke LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
        }
        super.onDestroy();
    }
}
