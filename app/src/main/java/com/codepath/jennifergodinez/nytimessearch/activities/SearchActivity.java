package com.codepath.jennifergodinez.nytimessearch.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.jennifergodinez.nytimessearch.R;
import com.codepath.jennifergodinez.nytimessearch.adapters.ArticlesAdapter;
import com.codepath.jennifergodinez.nytimessearch.fragments.FilterFragment;
import com.codepath.jennifergodinez.nytimessearch.models.Article;
import com.codepath.jennifergodinez.nytimessearch.models.Filter;
import com.codepath.jennifergodinez.nytimessearch.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements FilterFragment.OnFinishFilter {
    ArrayList<Article> articles;
    ArticlesAdapter adapter;
    String savedQuery;
    private Filter savedFilter = new Filter();
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupViews();

    }


    public void setupViews() {
        articles = new ArrayList<Article>();

        // Lookup the recyclerview in activity layout
        RecyclerView rvArticles = (RecyclerView) findViewById(R.id.rvArticles);

        adapter = new ArticlesAdapter(this, articles);

        // Attach the adapter to the recyclerview to populate items
        rvArticles.setAdapter(adapter);

        // decorate with a divider
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvArticles.addItemDecoration(itemDecoration);

        itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
        rvArticles.addItemDecoration(itemDecoration);

        // Set layout manager to position the items
        //GridLayoutManager linearLayoutManager = new GridLayoutManager(this,  2);
        StaggeredGridLayoutManager linearLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvArticles.setLayoutManager(linearLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                final int curSize = adapter.getItemCount();

                searchArticle(page, savedQuery, savedFilter.toMap());

                view.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyItemRangeInserted(curSize, articles.size() - 1);
                    }
                });
            }
        };
        // Adds the scroll listener to RecyclerView
        rvArticles.addOnScrollListener(scrollListener);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();

                //save this query string
                savedQuery = query;
                searchArticle(0, query, savedFilter.toMap());

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.home:
                return true;

            case R.id.miProfile:
                showFilterDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showFilterDialog() {
        FragmentManager fm = getSupportFragmentManager();
        FilterFragment filterFragment = FilterFragment.newInstance(savedFilter);
        filterFragment.show(fm, "fragment_filter");
    }


    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }


    private void resetArticles() {
        articles.clear();
        adapter.notifyDataSetChanged();
        scrollListener.resetState();
    }

    public void searchArticle(final int page, String query, HashMap<String, String> filterMap) {

        if (!isOnline()) {
            Toast.makeText(getApplicationContext(), "No internet detected!",
                    Toast.LENGTH_LONG).show();
            resetArticles();
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params = new RequestParams();
        params.put("api-key", "3652d8a89c0747fe97783cbd961d4817");
        params.put("q", query);
        params.put("hl","true");
        params.put("page", page);

        if (filterMap.size() > 0) {
            for (HashMap.Entry<String, String> entry : filterMap.entrySet()) {
                params.put(entry.getKey(), entry.getValue());
            }
        }

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults;
                try {
                    if (page == 0) {
                        resetArticles();
                    }
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error while trying to reach the server!",
                        Toast.LENGTH_LONG).show();
                resetArticles();
            }
        });
    }


    @Override
    public void onFinishFilterDialog(Filter f) {
        // save this filter
        savedFilter = f;
        searchArticle(0, savedQuery, f.toMap());
    }
}
