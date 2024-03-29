package com.tokopedia.testproject.problems.news.view;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;

public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {

    private int minItemsBeforeNextLoad = 5;
    private int startingPage = 1;
    private int currentPage = 1;
    private int latestTotalItemCount = 0;
    private boolean isLoading = true;

    GridLayoutManager layoutManager;

    public EndlessScrollListener(GridLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        minItemsBeforeNextLoad *= layoutManager.getSpanCount();
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        int totalItemCount = layoutManager.getItemCount();

        // Assume list was invalidated -- set back to default
        if (totalItemCount < latestTotalItemCount) {
            this.currentPage = this.startingPage;
            this.latestTotalItemCount = totalItemCount;
        }

        // If still loading and dataset size has been updated,
        // update load state and last item count
        if (isLoading && totalItemCount > latestTotalItemCount) {
            isLoading = false;
            latestTotalItemCount = totalItemCount;
        }

        // If not loading and within threashold limit, increase current page and load more data
        if (!isLoading && (lastVisibleItemPosition + minItemsBeforeNextLoad > totalItemCount)) {
            currentPage++;
            onLoadMore(currentPage, totalItemCount, recyclerView);
            isLoading = true;
        }
    }

    public abstract void onLoadMore(int page, int totalItemsCount, RecyclerView view);
}