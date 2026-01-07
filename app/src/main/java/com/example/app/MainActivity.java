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

public class MainActivity extends Activity {

    private WebView mWebView;
    private ValueCallback<Uri[]> uploadMessage;
    private final static int FILE_CHOOSER_RESULT_CODE = 1;
    private final static int PERMISSION_REQUEST_CODE = 100;

    // Variables untuk credentials
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
        
        // Coba pakai layout XML dulu
        try {
            setContentView(R.layout.activity_main);
        } catch (Exception e) {
            // Fallback ke WebView langsung
            mWebView = new WebView(this);
            setContentView(mWebView);
            setupWebView();
            mWebView.loadUrl("https://smk-maarif9kebumen.com/present/public/");
            return;
        }

        mWebView = findViewById(R.id.activity_main_webview);
        
        if (mWebView == null) {
            // Fallback jika WebView tidak ditemukan di layout
            mWebView = new WebView(this);
            setContentView(mWebView);
        }
        
        setupWebView();
        mWebView.loadUrl("https://smk-maarif9kebumen.com/present/public/");
        
        // Cek permissions
        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
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
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;

            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (!allGranted) {
                Toast.makeText(this, "Beberapa fitur mungkin tidak berfungsi tanpa izin yang diperlukan", Toast.LENGTH_LONG).show();
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

        // Important for camera access and WebRTC
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        // Set WebViewClient to handle links internally
        mWebView.setWebViewClient(new MyWebViewClient());

        // Set WebChromeClient for permissions, file upload, etc.
        mWebView.setWebChromeClient(new MyWebChromeClient());
    }

    private class MyWebViewClient extends WebViewClient {
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

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            
            // Inject JavaScript untuk handle camera dan geolocation
            injectCameraAndGeolocationFallback();
            
            // Auto-fill jika ada saved credentials
            injectAutoLoginIfNeeded();
        }
    }

    private void injectAutoLoginIfNeeded() {
        SharedPreferences prefs = getSharedPreferences("user_credentials", MODE_PRIVATE);
        String savedUsername = prefs.getString("username", "");
        String savedPassword = prefs.getString("password", "");
        
        if (!savedUsername.isEmpty() && !savedPassword.isEmpty()) {
            String jsCode = 
                "setTimeout(function() {" +
                "  var loginForm = document.querySelector('form[action*=\"login\"]');" +
                "  if (loginForm) {" +
                "    var usernameField = loginForm.querySelector('input[name=\"username\"], input[name=\"email\"], input[type=\"text\"]');" +
                "    var passwordField = loginForm.querySelector('input[name=\"password\"], input[type=\"password\"]');" +
                "    " +
                "    if (usernameField && passwordField) {" +
                "      usernameField.value = '" + savedUsername + "';" +
                "      passwordField.value = '" + savedPassword + "';" +
                "      " +
                "      var event = new Event('input', { bubbles: true });" +
                "      usernameField.dispatchEvent(event);" +
                "      passwordField.dispatchEvent(event);" +
                "    }" +
                "  }" +
                "}, 1000);";
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mWebView.evaluateJavascript(jsCode, null);
            } else {
                mWebView.loadUrl("javascript:" + jsCode);
            }
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            // Always grant location permission
            callback.invoke(origin, true, false);
        }

        @Override
        public void onPermissionRequest(final PermissionRequest request) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Grant semua permission yang diminta
                    request.grant(request.getResources());
                }
            });
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

    // Inject JavaScript fallback untuk camera dan geolocation
    private void injectCameraAndGeolocationFallback() {
        String jsCode = 
            "try {" +
            "// Fix untuk getUserMedia" +
            "if (navigator.mediaDevices && !navigator.mediaDevices.getUserMedia) {" +
            "navigator.mediaDevices.getUserMedia = function(constraints) {" +
            "return new Promise(function(resolve, reject) {" +
            "navigator.getUserMedia(constraints, resolve, reject);" +
            "});" +
            "};" +
            "}" +

            "// Backup untuk navigator.getUserMedia lama" +
            "if (!navigator.getUserMedia) {" +
            "navigator.getUserMedia = navigator.webkitGetUserMedia || navigator.mozGetUserMedia;" +
            "}" +

            "// Fix untuk geolocation" +
            "if (!navigator.geolocation) {" +
            "navigator.geolocation = {" +
            "getCurrentPosition: function(success, error) {" +
            "if (error) error({code: 1, message: 'Geolocation not supported'});" +
            "}," +
            "watchPosition: function(success, error) {" +
            "if (error) error({code: 1, message: 'Geolocation not supported'});" +
            "return 1;" +
            "}," +
            "clearWatch: function(id) {}" +
            "};" +
            "}" +

            "console.log('Camera and geolocation fixes applied');" +
            "} catch(e) { console.log('Fix error: ' + e); }";

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
            if (uploadMessage != null) return;

            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(results);
                uploadMessage = null;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
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