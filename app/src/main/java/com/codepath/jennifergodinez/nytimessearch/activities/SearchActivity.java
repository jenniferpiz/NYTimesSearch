package com.codepath.jennifergodinez.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.codepath.jennifergodinez.nytimessearch.R;
import com.codepath.jennifergodinez.nytimessearch.adapters.ArticleArrayAdapter;
import com.codepath.jennifergodinez.nytimessearch.models.Article;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements FilterFragment.EditNameDialogListener {
    EditText etQuery;
    GridView gvResults;
    Button btnSearch;
    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;
    String sQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
        //handleIntent(getIntent());
        setupViews();

    }


    public void setupViews() {
        //etQuery = (EditText)findViewById(R.id.etQuery);
        gvResults = (GridView)findViewById(R.id.gvResults);
        //btnSearch = (Button)findViewById(R.id.btnSearch);
        articles = new ArrayList<Article>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,  View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                Article article = articles.get(position);
                i.putExtra("article", article);
                startActivity(i);
            }
        });
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
                Log.d("DEBUG", "onQueryTextSubmit");
                //save this query string
                sQuery = query;
                searchArticle(query, new HashMap());

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
                Log.d("DEBUG", "up pressed?");
                return true;

            case R.id.action_settings:
                showFilterDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showFilterDialog() {
        FragmentManager fm = getSupportFragmentManager();
        FilterFragment filterFragment = FilterFragment.newInstance();
        filterFragment.show(fm, "fragment_filter");
    }


    public void searchArticle(String query, HashMap<String, String> filterMap) {

        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params = new RequestParams();
        params.put("api-key", "3652d8a89c0747fe97783cbd961d4817");
        params.put("q", query);
        params.put("page", 0);

        Toast.makeText(this, "QUERY: "+query+" nFilters="+filterMap.size(), Toast.LENGTH_SHORT).show();
        if (filterMap.size() > 0) {
            for (HashMap.Entry<String, String> entry : filterMap.entrySet()) {
                params.put(entry.getKey(), entry.getValue());
            }
        }

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults = null;
                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    adapter.addAll(Article.fromJSONArray(articleJsonResults));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }
        });
    }

    @Override
    public void onFinishEditDialog(String date, String sortValue, ArrayList<String> deskList) {
        adapter.clear();

        HashMap<String, String> filterMap = new HashMap<>();

        if (!"".equals(date)) {
            filterMap.put("begin_date", date);
        }

        if (!"".equals(sortValue)) {
            filterMap.put("sort", sortValue);
        }

        if (deskList.size()>0) {
            String sNewsDesk= "news_desk:(";
            for (String newsDesk : deskList) {
                sNewsDesk += "\""+newsDesk+"\"%20";
            }
            sNewsDesk += ")";
            filterMap.put("fq", sNewsDesk);
        }
        searchArticle(sQuery, filterMap);
    }
}
