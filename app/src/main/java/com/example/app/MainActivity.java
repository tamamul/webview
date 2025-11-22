package com.example.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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

    // HAPUS ProgressBar declaration

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
        
        // Initialize WebView saja
        mWebView = findViewById(R.id.activity_main_webview);
        
        // HAPUS progressBar initialization
        
        // Cek dan minta permission sebelum setup WebView
        if (checkAndRequestPermissions()) {
            setupWebView();
        }
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
                mWebView.loadUrl("https://smkmaarif9kebumen.sch.id/present/public/");
            } else {
                // Beberapa permission ditolak
                Toast.makeText(this, "Beberapa fitur mungkin tidak berfungsi tanpa izin yang diperlukan", Toast.LENGTH_LONG).show();
                setupWebView();
                mWebView.loadUrl("https://smkmaarif9kebumen.sch.id/present/public/");
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
    
    // ENABLE GOOGLE AUTOFILL (HAPUS DUPLIKASI)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        webSettings.setSaveFormData(true);
        mWebView.setAutofillHints(View.AUTOFILL_HINT_PASSWORD);
    }
    webSettings.setSavePassword(true);
    
    // Set WebViewClient to handle links internally
    mWebView.setWebViewClient(new MyWebViewClient());
    
    // Set WebChromeClient untuk permissions
    mWebView.setWebChromeClient(new MyWebChromeClient());
    
    // Load URL
    mWebView.loadUrl("https://smkmaarif9kebumen.sch.id/present/public/");
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
