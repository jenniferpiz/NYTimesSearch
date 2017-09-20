package com.codepath.jennifergodinez.nytimessearch.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.codepath.jennifergodinez.nytimessearch.R;
import com.codepath.jennifergodinez.nytimessearch.models.Article;

public class ArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Article article = (Article)getIntent().getSerializableExtra("article");
        final WebView wvArticle = (WebView)findViewById(R.id.wvArticle);

        wvArticle.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
             }
        });
        wvArticle.loadUrl(article.getWebUrl());

    }

}
