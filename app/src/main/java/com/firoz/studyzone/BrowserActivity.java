
package com.firoz.studyzone;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BrowserActivity extends AppCompatActivity {
    public static final String EXTRA_URL = "url";
    private WebView web; private ProgressBar progress; private TextView offline;
    @SuppressLint("SetJavaScriptEnabled")
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout root = Ui.column(this);
        LinearLayout bar = Ui.row(this);
        addBarButton(bar, "‹ Back", () -> { if (web.canGoBack()) web.goBack(); else finish(); });
        addBarButton(bar, "⌂ Home", () -> { web.loadUrl(AppConfig.WEBSITE_URL); });
        addBarButton(bar, "↻ Refresh", () -> load());
        root.addView(bar);
        progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progress.setProgressTintList(android.content.res.ColorStateList.valueOf(Ui.color(AppConfig.GOLD)));
        root.addView(progress, new LinearLayout.LayoutParams(-1, 4));
        offline = Ui.text(this, "◉\nNo Internet Connection\nPlease check your connection and try again.", 18, Ui.color(AppConfig.WHITE), true);
        offline.setGravity(android.view.Gravity.CENTER); offline.setVisibility(View.GONE);
        root.addView(offline, new LinearLayout.LayoutParams(-1, 180));
        web = new WebView(this);
        web.setBackgroundColor(Ui.color(AppConfig.DEEP_NAVY));
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setAllowFileAccess(false);
        web.getSettings().setAllowContentAccess(true);
        web.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String scheme = request.getUrl().getScheme();
                return !("https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme));
            }
            @Override public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError err) {
                if (req.isForMainFrame() && !hasInternet()) showOffline();
            }
        });
        web.setWebChromeClient(new WebChromeClient() {
            @Override public void onProgressChanged(WebView view, int p) {
                progress.setProgress(p); progress.setVisibility(p >= 100 ? View.GONE : View.VISIBLE);
            }
        });
        web.setDownloadListener((url, ua, disposition, mime, length) -> {
            try {
                DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));
                req.setTitle("Study Zone download"); req.setDescription("Downloading file");
                req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                String name = android.webkit.URLUtil.guessFileName(url, disposition, mime);
                req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "StudyZone/" + name);
                ((DownloadManager)getSystemService(DOWNLOAD_SERVICE)).enqueue(req);
                Toast.makeText(this, "Download started", Toast.LENGTH_SHORT).show();
            } catch (Exception e) { Toast.makeText(this, "Could not start download", Toast.LENGTH_LONG).show(); }
        });
        root.addView(web, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(root);
        load();
    }
    private void addBarButton(LinearLayout bar, String label, Runnable action) {
        android.widget.Button b = Ui.button(this, label); b.setTextSize(11);
        bar.addView(b, new LinearLayout.LayoutParams(0, 48, 1)); b.setOnClickListener(v -> action.run());
    }
    private boolean hasInternet() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null || Build.VERSION.SDK_INT < 23) return false;
        android.net.Network n = cm.getActiveNetwork(); NetworkCapabilities c = cm.getNetworkCapabilities(n);
        return c != null && c.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
    private void showOffline() { web.setVisibility(View.GONE); offline.setVisibility(View.VISIBLE); }
    private void load() {
        String url = getIntent().getStringExtra(EXTRA_URL);
        if (url == null || !url.startsWith("https://")) url = AppConfig.WEBSITE_URL;
        if (!hasInternet()) { showOffline(); offline.setOnClickListener(v -> load()); return; }
        offline.setVisibility(View.GONE); web.setVisibility(View.VISIBLE); web.loadUrl(url);
    }
    @Override public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack(); else super.onBackPressed();
    }
    @Override protected void onDestroy() { if (web != null) web.destroy(); super.onDestroy(); }
}
