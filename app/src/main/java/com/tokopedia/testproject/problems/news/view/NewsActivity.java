package com.tokopedia.testproject.problems.news.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.tokopedia.testproject.R;
import com.tokopedia.testproject.problems.news.model.Article;
import com.tokopedia.testproject.problems.news.presenter.NewsPresenter;

import java.util.List;

public class NewsActivity extends AppCompatActivity implements com.tokopedia.testproject.problems.news.presenter.NewsPresenter.View {

    private NewsPresenter newsPresenter;
    private NewsAdapter newsAdapter;
    ProgressBar progressBar;
    CarouselView carouselView;
    Button btnSearch;
    EditText editTextSearch;
    String sampleImagesx[];
    boolean stateData;
    String searchKey;
    RecyclerView recyclerView;

    private EndlessScrollListener scrollListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        newsPresenter = new NewsPresenter(this);
        newsAdapter = new NewsAdapter(null);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBarNews);
        carouselView = findViewById(R.id.carouselView);
        editTextSearch = findViewById(R.id.editTextSearch);
        btnSearch = findViewById(R.id.btnSearch);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        stateData = true;


        if (stateData == true) {
            newsPresenter.getEverything("android", 1);
        } else {
//            newsPresenter.getEverything(searchKey, 1);
        }

        if (stateData == true) {
            scrollListener = new EndlessScrollListener(gridLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    newsPresenter.getEverything("android", page);
                }
            };
        } else {
            scrollListener = new EndlessScrollListener(gridLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    newsPresenter.getEverything(searchKey, page);
                }
            };
        }


        recyclerView.addOnScrollListener(scrollListener);

        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
//                Picasso.with(getContext()).load(sampleImagesx[position]).centerCrop().into(imageView);


                Glide.with(imageView)
                        .load(sampleImagesx[position])
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .fitCenter()
                        .into(imageView);

            }
        });


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateData = false;
                progressBar.setVisibility(View.VISIBLE);
                searchKey = editTextSearch.getText().toString();
                newsPresenter.getEverything(searchKey, 1);
            }
        });


        recyclerView.setAdapter(newsAdapter);
        newsPresenter.getTopHeadlines("techcrunch");
    }

    @Override
    public void onSuccessGetNews(List<Article> articleList) {

        if (articleList.size() > 0) {
            newsAdapter.setArticleList(articleList);
            newsAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);


        } else {
            Toast.makeText(NewsActivity.this, "Empty data", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }


    }


    @Override
    public void onSuccessGetTopHeadlines(List<Article> articleList) {

        if (articleList.size() > 0) {
            sampleImagesx = new String[articleList.size()];

            for (int i = 0; i < 5; i++) {
                sampleImagesx[i] = articleList.get(i).getUrlToImage();
            }

            carouselView.setPageCount(5);

        } else {
            Toast.makeText(NewsActivity.this, "Empty data", Toast.LENGTH_SHORT).show();

        }


    }


    @Override
    public void onErrorGetNews(Throwable throwable) {
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        newsPresenter.unsubscribe();
    }
}
