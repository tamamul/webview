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
    private final static int NOTIFICATION_PERMISSION_CODE = 101;

    // Splash screen components
    private FrameLayout splashContainer;
    private LinearLayout splashContent;
    private ProgressBar splashProgress;
    private TextView splashText, splashTip;
    private Handler splashHandler = new Handler();
    private int splashProgressValue = 0;

    // Daftar permission yang diperlukan
    private String[] requiredPermissions = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO
    };

    // Untuk Android 13+ butuh permission notification
    private String[] notificationPermission = {
            android.Manifest.permission.POST_NOTIFICATIONS
    };

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

        // Setup notifikasi pengingat
        setupPresensiReminder();

        // Langsung ke WebView tanpa cek credentials
        proceedToWebView();
    }

    private void showSplashScreen() {
        // ... [kode splash screen sama seperti sebelumnya]
        // Tetap pertahankan kode splash screen yang ada
    }

    private void startSplashAnimation() {
        // ... [kode animasi sama seperti sebelumnya]
    }

    private void hideSplashScreen() {
        splashContainer.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> {
                    setupMainLayout();
                })
                .start();
    }

    private void setupMainLayout() {
        setContentView(R.layout.activity_main);
        mWebView = findViewById(R.id.activity_main_webview);
        setupWebView();
        mWebView.loadUrl("https://smk-maarif9kebumen.com/present/public/");
    }

    private void proceedToWebView() {
        splashHandler.postDelayed(() -> {
            // Cek permission untuk notifications (Android 13+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) 
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, notificationPermission, NOTIFICATION_PERMISSION_CODE);
                }
            }

            // Hide splash dan tampilkan WebView
            hideSplashScreen();

            // Cek permissions untuk WebView
            if (checkAndRequestPermissions()) {
                // Permissions sudah granted
            } else {
                Toast.makeText(this, "Mohon berikan izin yang diperlukan", Toast.LENGTH_SHORT).show();
            }
        }, 3000);
    }

    private void setupPresensiReminder() {
        // Setup alarm untuk notifikasi pengingat menggunakan PresensiReminder
        PresensiReminder.setupPresensiReminder(this);
        
        // Juga schedule ulang jika device reboot
        scheduleAlarmOnBoot();
    }
    
    private void scheduleAlarmOnBoot() {
        // Untuk menangani jika device reboot, alarm perlu di-setup ulang
        // Kita akan menggunakan BootReceiver (opsional)
        // Untuk sekarang, kita akan setup langsung dari sini
    }

    private boolean checkAndRequestPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();

        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission);
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsNeeded.toArray(new String[0]),
                    PERMISSION_REQUEST_CODE
            );
            return false;
        }

        return true;
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

            if (allGranted) {
                Toast.makeText(this, "Izinkan akses untuk fitur lengkap", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Beberapa fitur mungkin tidak berfungsi tanpa izin yang diperlukan", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifikasi pengingat presensi diaktifkan", Toast.LENGTH_SHORT).show();
                // Setup ulang alarm dengan permission yang sudah diberikan
                PresensiReminder.setupPresensiReminder(this);
            }
        }
    }

    private void setupWebView() {
        if (mWebView == null) return;

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setGeolocationEnabled(true);
        
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            webSettings.setGeolocationDatabasePath(getFilesDir().getPath());
        }

        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }

        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("tel:") || url.startsWith("mailto:") || url.startsWith("whatsapp:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }

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
            injectCameraAndGeolocationFallback();
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
            callback.invoke(origin, true, false);
        }

        @Override
        public void onPermissionRequest(final PermissionRequest request) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    request.grant(request.getResources());
                }
            });
        }

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

    private void injectCameraAndGeolocationFallback() {
        String jsCode = 
            "try {" +
            "if (navigator.mediaDevices && !navigator.mediaDevices.getUserMedia) {" +
            "navigator.mediaDevices.getUserMedia = function(constraints) {" +
            "return new Promise(function(resolve, reject) {" +
            "navigator.getUserMedia(constraints, resolve, reject);" +
            "});" +
            "};" +
            "}" +

            "if (!navigator.getUserMedia) {" +
            "navigator.getUserMedia = navigator.webkitGetUserMedia || navigator.mozGetUserMedia;" +
            "}" +

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