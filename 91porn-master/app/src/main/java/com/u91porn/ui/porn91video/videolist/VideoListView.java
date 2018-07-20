package com.u91porn.ui.porn91video.videolist;

import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.ui.BaseView;

import java.util.List;

/**
 * @author flymegoc
 * @date 2017/11/16
 * @describe
 */

public interface VideoListView extends BaseView {
    void loadMoreDataComplete();

    void loadMoreFailed();

    void noMoreData();

    void setMoreData(List<UnLimit91PornItem> unLimit91PornItemList);

    void loadData(boolean pullToRefresh, boolean cleanCache, int skipPage);

    void setData(List<UnLimit91PornItem> data);

    void setPageData(List<Integer> pageData);

    void updateCurrentPage(int currentPage);

    void showSkipPageLoading();

    void hideSkipPageLoading();
}
