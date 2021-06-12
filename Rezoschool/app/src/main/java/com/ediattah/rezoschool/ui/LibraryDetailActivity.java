package com.ediattah.rezoschool.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ediattah.rezoschool.Model.Library;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;

public class LibraryDetailActivity extends AppCompatActivity {
    Library library;
    ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_detail);
        library = (Library) getIntent().getSerializableExtra("OBJECT");
        WebView webView = findViewById(R.id.webView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(library.title);
        if (!URLUtil.isValidUrl(library.url)) {
            Utils.showAlert(this, "Error", getResources().getString(R.string.invalid_url));
            return;
        }

        String doc="<iframe src='" + library.url + "' width='100%' height='100%' style='border: none;'></iframe>";
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAllowFileAccess(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                if(pd == null){
                    pd = ProgressDialog.show(LibraryDetailActivity.this, null, "Loading...");
                    pd.setCancelable(true);
                }
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(pd.isShowing())
                {
                    pd.dismiss();
                }
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

        });
        webView.loadUrl(library.url);
//        webView.loadData( doc, "text/html",  "UTF-8");
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}