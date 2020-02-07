package com.car.maintenance.Activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.car.maintenance.R;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

public class WebActivity extends AppCompatActivity {

    private ProgressBar progress;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        String url = getIntent().getStringExtra("url");
        WebView engine = (WebView) findViewById(R.id.web_view);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setMax(100);
        engine.getSettings().setJavaScriptEnabled(true);
        engine.getSettings().setDomStorageEnabled(true);
        engine.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        engine.setWebViewClient(new WebViewClient());
        engine.setWebChromeClient(new MyWebViewClient());
        engine.loadUrl(url);
        loadFacebookAd();
    }

    private void loadFacebookAd() {
        adView = new com.facebook.ads.AdView(this, "261287334429127_261314187759775", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);
        // Request an ad
        adView.loadAd();
    }

    private class MyWebViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progress.setProgress(newProgress);
            if (newProgress == 100)
                progress.setVisibility(View.GONE);
            else progress.setVisibility(View.VISIBLE);
            super.onProgressChanged(view, newProgress);
        }
    }

}
