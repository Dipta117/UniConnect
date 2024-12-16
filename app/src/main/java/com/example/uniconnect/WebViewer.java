package com.example.uniconnect;

import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class WebViewer extends AppCompatActivity {

    private WebView webView;
    private TextView txt_load;
    private ProgressBar pb;
    private String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = findViewById(R.id.webView);
        txt_load = findViewById(R.id.txt_load);
        pb = findViewById(R.id.pb);

        value = getIntent().getStringExtra("URL");

        if (value != null) {
            displayWebView();
        } else {
            Toast.makeText(this, "No URL provided", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                pb.setProgress(progress);
                if (progress == 100) {
                    pb.setVisibility(View.GONE);
                    txt_load.setVisibility(View.INVISIBLE);
                    webView.setVisibility(View.VISIBLE);
                } else {
                    pb.setVisibility(View.VISIBLE);
                    txt_load.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.INVISIBLE);
                }
            }
        });

        webView.loadUrl(value);
    }
}
