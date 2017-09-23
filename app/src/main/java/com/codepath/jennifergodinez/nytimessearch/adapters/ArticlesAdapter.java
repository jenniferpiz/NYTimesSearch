package com.codepath.jennifergodinez.nytimessearch.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.jennifergodinez.nytimessearch.R;
import com.codepath.jennifergodinez.nytimessearch.activities.ArticleActivity;
import com.codepath.jennifergodinez.nytimessearch.models.Article;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jennifergodinez on 9/20/17.
 */


public class ArticlesAdapter extends
        RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    // Store a member variable for the contacts
    private List<Article> mArticles;
    // Store the context for easy access
    private Context mContext;

    // Pass in the article list into the constructor
    public ArticlesAdapter(Context context, List<Article> articles) {
        mArticles = articles;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageView;
        public TextView tvTitle;
        public TextView tvSnippet;


        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.ivImage);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSnippet = itemView.findViewById(R.id.tvSnippet);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) {
                Article article = mArticles.get(position);

                Intent detailViewIntent = new Intent(mContext, ArticleActivity.class);
                detailViewIntent.putExtra("article", Parcels.wrap(article));
                mContext.startActivity(detailViewIntent);
            }
        }
    }

    public ArticlesAdapter(Context context, ArrayList<Article> articles) {
        mArticles = articles;
        mContext = context;
    }

    @Override
    public ArticlesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View articleView = inflater.inflate(R.layout.item_article_result, parent, false);

        // Return a new holder instance
       return new ViewHolder(articleView);
    }

     @Override
    public void onBindViewHolder(ArticlesAdapter.ViewHolder viewHolder, int position) {
         // Get the data model based on position
         Article article = mArticles.get(position);

         Glide.with(getContext())
                 .load(Uri.parse(article.getThumbNail()))
                 .placeholder(R.drawable.times_logo_291_black)
                 .into(viewHolder.imageView);


         TextView tvTitle = viewHolder.tvTitle;
         tvTitle.setText(article.getHeadline());

         TextView tvSnippet = viewHolder.tvSnippet;
         tvSnippet.setText(article.getSnippet());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mArticles.size();
    }
}