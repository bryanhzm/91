package com.u91porn.ui.porn91video.videolist;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.helper.loadviewhelper.help.OnLoadViewListener;
import com.helper.loadviewhelper.load.LoadViewHelper;
import com.orhanobut.logger.Logger;
import com.sdsmdg.tastytoast.TastyToast;
import com.u91porn.R;
import com.u91porn.adapter.SkipPageAdapter;
import com.u91porn.adapter.UnLimit91Adapter;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.ui.MvpFragment;
import com.u91porn.utils.AppUtils;
import com.u91porn.utils.LoadHelperUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 通用
 * A simple {@link Fragment} subclass.
 *
 * @author flymegoc
 */
public class VideoListFragment extends MvpFragment<VideoListView, VideoListPresenter> implements VideoListView, SwipeRefreshLayout.OnRefreshListener {


    private static final String TAG = VideoListFragment.class.getSimpleName();
    @BindView(R.id.recyclerView_common)
    RecyclerView recyclerView;
    Unbinder unbinder;
    @BindView(R.id.contentView)
    SwipeRefreshLayout contentView;

    @BindView(R.id.recyclerView_skip_page)
    RecyclerView skipPageRecyclerView;

    private UnLimit91Adapter mUnLimit91Adapter;

    private LoadViewHelper helper;

    @Inject
    protected VideoListPresenter videoListPresenter;

    private SkipPageAdapter skipPageAdapter;

    @BindView(R.id.ll_skip_page_loading)
    LinearLayout skipLoadingLayout;

    @BindView(R.id.fl_skip_page)
    FrameLayout skipPageLayout;

    public VideoListFragment() {
        // Required empty public constructor
    }

    public static VideoListFragment getInstance() {
        return new VideoListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<UnLimit91PornItem> mUnLimit91PornItemList = new ArrayList<>();
        mUnLimit91Adapter = new UnLimit91Adapter(R.layout.item_unlimit_91porn, mUnLimit91PornItemList);
        skipPageAdapter = new SkipPageAdapter(R.layout.item_skip_page);
    }

    @NonNull
    @Override
    public VideoListPresenter createPresenter() {
        getActivityComponent().inject(this);
        return videoListPresenter;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_video_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        // Setup contentView == SwipeRefreshView
        contentView.setOnRefreshListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mUnLimit91Adapter);
        mUnLimit91Adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UnLimit91PornItem unLimit91PornItems = (UnLimit91PornItem) adapter.getItem(position);
                goToPlayVideo(unLimit91PornItems);
            }
        });
        //使用缓存的FragmentPagerAdapter之后会导致新方法的加载更多失效，暂时切换回过时api，可正常运行
        mUnLimit91Adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                presenter.loadVideoListData(false, false, category.getCategoryValue(), 0);
            }
        });
        helper = new LoadViewHelper(recyclerView);
        helper.setListener(new OnLoadViewListener() {
            @Override
            public void onRetryClick() {
                loadData(false, true, 0);
            }
        });
        //loadData(false);
        AppUtils.setColorSchemeColors(context, contentView);

        handlerSkipPage();
    }

    private void handlerSkipPage() {
        if (dataManager.isOpenSkipPage()) {
            skipPageLayout.setVisibility(View.VISIBLE);
            skipPageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            skipPageRecyclerView.setAdapter(skipPageAdapter);
            skipPageAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    int page = (int) adapter.getItem(position);
                    loadData(false, false, page);
                }
            });
        } else {
            skipPageLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onLazyLoadOnce() {
        loadData(false, false, 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setData(List<UnLimit91PornItem> data) {
        mUnLimit91Adapter.setNewData(data);
        mUnLimit91Adapter.disableLoadMoreIfNotFullPage(recyclerView);
        recyclerView.smoothScrollToPosition(0);
    }

    @Override
    public void setPageData(List<Integer> pageData) {
        skipPageAdapter.setNewData(pageData);
    }

    @Override
    public void updateCurrentPage(final int currentPage) {
        Logger.t(TAG).d("第《" + currentPage + "》页");
        skipPageAdapter.setCurrentPage(currentPage);
        skipPageRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                skipPageRecyclerView.smoothScrollToPosition(currentPage + 2);
            }
        }, 200);
    }

    @Override
    public void showSkipPageLoading() {
        skipLoadingLayout.setVisibility(View.VISIBLE);

    }

    @Override
    public void hideSkipPageLoading() {
        skipLoadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        helper.showLoading();
        LoadHelperUtils.setLoadingText(helper.getLoadIng(), R.id.tv_loading_text, "拼命加载中...");
        contentView.setEnabled(false);
    }

    @Override
    public void loadData(boolean pullToRefresh, boolean cleanCache, int skipPage) {
        presenter.loadVideoListData(pullToRefresh, cleanCache, category.getCategoryValue(), skipPage);
    }

    @Override
    public void onRefresh() {
        loadData(true, true, 0);
    }

    @Override
    public void showContent() {
        helper.showContent();
        contentView.setEnabled(true);
        contentView.setRefreshing(false);
    }

    @Override
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
    }

    @Override
    public void showError(String message) {
        contentView.setRefreshing(false);
        helper.showError();
        showMessage(message, TastyToast.ERROR);
    }

    @Override
    public void loadMoreDataComplete() {
        mUnLimit91Adapter.loadMoreComplete();
    }

    @Override
    public void loadMoreFailed() {
        showMessage("加载更多失败", TastyToast.ERROR);
        mUnLimit91Adapter.loadMoreFail();
    }

    @Override
    public void noMoreData() {
        mUnLimit91Adapter.loadMoreEnd(true);
    }

    @Override
    public void setMoreData(List<UnLimit91PornItem> unLimit91PornItemList) {
        mUnLimit91Adapter.addData(unLimit91PornItemList);
    }

    @Override
    public String getTitle() {
        return category.getCategoryName();
    }
}
