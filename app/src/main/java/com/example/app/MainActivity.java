package com.example.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    // Splash screen components
    private FrameLayout splashContainer;
    private LinearLayout splashContent;
    private ProgressBar splashProgress;
    private TextView splashText, splashTip;
    private Handler splashHandler = new Handler();
    private int splashProgressValue = 0;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // Tampilkan splash screen terlebih dahulu
        showSplashScreen();
        
        // Check credentials dan lanjutkan
        checkCredentialsAndProceed();
    }

    private void showSplashScreen() {
        // Buat container untuk splash screen
        splashContainer = new FrameLayout(this);
        splashContainer.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        splashContainer.setBackgroundColor(Color.parseColor("#0F172A"));
        
        // Buat content splash screen
        splashContent = new LinearLayout(this);
        splashContent.setOrientation(LinearLayout.VERTICAL);
        splashContent.setGravity(Gravity.CENTER);
        
        FrameLayout.LayoutParams contentParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        contentParams.gravity = Gravity.CENTER;
        splashContent.setLayoutParams(contentParams);
        
        // Logo/Icon
        TextView logo = new TextView(this);
        logo.setText("SMK");
        logo.setTextSize(48);
        logo.setTextColor(Color.WHITE);
        logo.setPadding(0, 0, 0, 20);
        
        // App Name
        TextView appName = new TextView(this);
        appName.setText("MAARIF 9");
        appName.setTextSize(24);
        appName.setTextColor(Color.parseColor("#38BDF8"));
        appName.setPadding(0, 0, 0, 40);
        
        // Progress Bar
        splashProgress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        splashProgress.setLayoutParams(new LinearLayout.LayoutParams(
                200, 30));
        splashProgress.getProgressDrawable().setColorFilter(
                Color.parseColor("#38BDF8"), android.graphics.PorterDuff.Mode.SRC_IN);
        splashProgress.setPadding(0, 0, 0, 10);
        
        // Loading Text
        splashText = new TextView(this);
        splashText.setText("0%");
        splashText.setTextSize(18);
        splashText.setTextColor(Color.WHITE);
        splashText.setPadding(0, 0, 0, 20);
        
        // Loading Tip
        splashTip = new TextView(this);
        splashTip.setText("Menyiapkan aplikasi...");
        splashTip.setTextSize(14);
        splashTip.setTextColor(Color.parseColor("#80FFFFFF"));
        splashTip.setPadding(0, 0, 0, 20);
        
        // Tambahkan semua views ke content
        splashContent.addView(logo);
        splashContent.addView(appName);
        splashContent.addView(splashProgress);
        splashContent.addView(splashText);
        splashContent.addView(splashTip);
        
        // Tambahkan content ke container
        splashContainer.addView(splashContent);
        
        // Set content view ke splash screen
        setContentView(splashContainer);
        
        // Mulai animasi loading
        startSplashAnimation();
    }

    private void startSplashAnimation() {
        new Thread(() -> {
            String[] tips = {
                "Menyiapkan aplikasi...",
                "Memuat data pengguna...",
                "Menyiapkan sesi...",
                "Memuat konten...",
                "Mengoptimalkan performa..."
            };
            Random random = new Random();
            
            while (splashProgressValue < 100) {
                splashProgressValue += 2;
                
                splashHandler.post(() -> {
                    splashProgress.setProgress(splashProgressValue);
                    splashText.setText(splashProgressValue + "%");
                    
                    // Ganti tip setiap 25%
                    if (splashProgressValue % 25 == 0) {
                        int index = random.nextInt(tips.length);
                        splashTip.setText(tips[index]);
                    }
                });
                
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void hideSplashScreen() {
        // Animasi fade out untuk splash screen
        splashContainer.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> {
                    // Setelah splash hilang, tampilkan WebView
                    setupMainLayout();
                })
                .start();
    }

    private void setupMainLayout() {
        // Set layout utama dengan WebView
        setContentView(R.layout.activity_main);
        mWebView = findViewById(R.id.activity_main_webview);
        
        // Setup WebView
        setupWebView();
        
        // Load URL
        mWebView.loadUrl("https://smkmaarif9kebumen.sch.id/present/public/");
    }

    private void checkCredentialsAndProceed() {
        // Tunggu splash screen selesai (3 detik) baru cek credentials
        splashHandler.postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("user_credentials", MODE_PRIVATE);
            String username = prefs.getString("username", "");
            String password = prefs.getString("password", "");
            
            if (username.isEmpty() || password.isEmpty()) {
                // Tidak ada credentials, pergi ke LoginActivity
                goToLoginActivity();
            } else {
                // Ada credentials, simpan dan lanjutkan
                this.username = username;
                this.password = password;
                
                // Hide splash dan tampilkan WebView
                hideSplashScreen();
                
                // Cek permissions
                if (checkAndRequestPermissions()) {
                    // Permissions sudah granted, WebView sudah disetup di hideSplashScreen()
                } else {
                    // Menunggu permission request
                    Toast.makeText(this, "Mohon berikan izin yang diperlukan", Toast.LENGTH_SHORT).show();
                }
            }
        }, 3000); // Delay 3 detik untuk splash animation
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
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
                // Semua permission granted
                Toast.makeText(this, "Selamat datang " + username, Toast.LENGTH_SHORT).show();
            } else {
                // Beberapa permission ditolak
                Toast.makeText(this, "Beberapa fitur mungkin tidak berfungsi tanpa izin yang diperlukan", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setupWebView() {
        if (mWebView == null) return;
        
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

        // Important for camera access and WebRTC
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        // Enable WebRTC features
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }

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

            // AUTO LOGIN BACKGROUND jika di login page
            attemptBackgroundLogin();

            // Inject JavaScript untuk handle camera dan geolocation
            injectCameraAndGeolocationFallback();
        }
    }

    private void attemptBackgroundLogin() {
        if (username.isEmpty() || password.isEmpty()) return;

        String jsCode = 
            "setTimeout(function() {" +
            "  var form = document.querySelector('form[action*=\"login\"]');" +
            "  var isLoggedIn = document.querySelector('[href*=\"logout\"]') !== null;" +
            "  " +
            "  if (form && !isLoggedIn) {" +  // Hanya jika butuh login
            "    var emailInput = document.querySelector('input[name=\"login\"]');" +
            "    var passwordInput = document.querySelector('input[name=\"password\"]');" +
            "    " +
            "    if (emailInput && passwordInput) {" +
            "      emailInput.value = '" + username + "';" +
            "      passwordInput.value = '" + password + "';" +
            "      " +
            "      // Trigger events" +
            "      ['input', 'change'].forEach(function(eventType) {" +
            "        emailInput.dispatchEvent(new Event(eventType, { bubbles: true }));" +
            "        passwordInput.dispatchEvent(new Event(eventType, { bubbles: true }));" +
            "      });" +
            "      " +
            "      // Auto submit" +
            "      setTimeout(function() { form.submit(); }, 1000);" +
            "    }" +
            "  }" +
            "}, 1500);";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.evaluateJavascript(jsCode, null);
        } else {
            mWebView.loadUrl("javascript:" + jsCode);
        }
    }

    private class MyWebChromeClient extends WebChromeClient {

        // Handle geolocation permission prompt
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            // Always grant location permission
            callback.invoke(origin, true, false);
        }

        // Handle permission requests (camera, microphone, location)
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
        if (mWebView != null && mWebView.canGoBack()) {
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