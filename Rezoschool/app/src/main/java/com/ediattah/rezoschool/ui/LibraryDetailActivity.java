package com.ediattah.rezoschool.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_detail);
        library = (Library) getIntent().getSerializableExtra("OBJECT");
        WebView webView = findViewById(R.id.webView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(library.title);
        if (!URLUtil.isValidUrl(library.url)) {
            Utils.showAlert(this, "Error", "Invalid url!");
            return;
        }
        final ProgressDialog pd = ProgressDialog.show(this, "", "Loading...",true);
        String doc="<iframe src='" + library.url + "' width='100%' height='100%' style='border: none;'></iframe>";
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAllowFileAccess(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if(pd!=null && pd.isShowing())
                {
                    pd.dismiss();
                }
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