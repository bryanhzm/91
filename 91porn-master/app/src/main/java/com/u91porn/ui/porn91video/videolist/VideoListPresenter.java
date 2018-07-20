package com.u91porn.ui.porn91video.videolist;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u91porn.data.DataManager;
import com.u91porn.data.model.BaseResult;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.di.PerActivity;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.rxjava.RetryWhenProcess;
import com.u91porn.rxjava.RxSchedulersHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * @author flymegoc
 * @date 2017/11/16
 */
@PerActivity
public class VideoListPresenter extends MvpBasePresenter<VideoListView> implements IVideoList {

    private Integer totalPage = 1;
    private int page = 1;
    private LifecycleProvider<Lifecycle.Event> provider;
    /**
     * 本次强制刷新过那下面的请求也一起刷新
     */
    private boolean isLoadMoreCleanCache = false;
    private DataManager dataManager;
    private String m;

    @Inject
    public VideoListPresenter(LifecycleProvider<Lifecycle.Event> provider, DataManager dataManager) {
        this.provider = provider;
        this.dataManager = dataManager;
    }

    @Override
    public void loadVideoListData(final boolean pullToRefresh, boolean cleanCache, String category, int skipPage) {
        String viewType = "basic";
        //如果刷新则重置页数
        if (pullToRefresh) {
            page = 1;
            isLoadMoreCleanCache = true;
        }
        if (skipPage > 0) {
            page = skipPage;
        }
        if ("watch".equalsIgnoreCase(category)) {
            //最近更新
            action(dataManager.loadPorn91VideoRecentUpdates(category, page, cleanCache, isLoadMoreCleanCache)
                    .map(new Function<BaseResult<List<UnLimit91PornItem>>, List<UnLimit91PornItem>>() {
                        @Override
                        public List<UnLimit91PornItem> apply(BaseResult<List<UnLimit91PornItem>> baseResult) throws Exception {
                            if (page == 1) {
                                totalPage = baseResult.getTotalPage();
                            }
                            return baseResult.getData();
                        }
                    }), pullToRefresh, skipPage);
        } else {
            //其他栏目
            if (!"top1".equals(category)) {
                m = null;
            } else {
                m = "-1";
            }
            Observable<List<UnLimit91PornItem>> ob = dataManager.loadPorn91VideoByCategory(category, viewType, page, m, cleanCache, isLoadMoreCleanCache)
                    .map(new Function<BaseResult<List<UnLimit91PornItem>>, List<UnLimit91PornItem>>() {
                        @Override
                        public List<UnLimit91PornItem> apply(BaseResult<List<UnLimit91PornItem>> baseResult) throws Exception {
                            if (page == 1) {
                                totalPage = baseResult.getTotalPage();
                            }
                            return baseResult.getData();
                        }
                    });
            action(ob, pullToRefresh, skipPage);
        }
    }

    private void action(Observable<List<UnLimit91PornItem>> observable, final boolean pullToRefresh, final int skipPage) {
        observable.retryWhen(new RetryWhenProcess(RetryWhenProcess.PROCESS_TIME))
                .compose(RxSchedulersHelper.<List<UnLimit91PornItem>>ioMainThread())
                .compose(provider.<List<UnLimit91PornItem>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<List<UnLimit91PornItem>>() {
                    @Override
                    public void onBegin(Disposable d) {
                        //首次加载显示加载页
                        ifViewAttached(new ViewAction<VideoListView>() {
                            @Override
                            public void run(@NonNull VideoListView view) {
                                if (page == 1 && !pullToRefresh && skipPage != 1) {
                                    view.showLoading(pullToRefresh);
                                }
                                if (skipPage > 0) {
                                    view.showSkipPageLoading();
                                }
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final List<UnLimit91PornItem> unLimit91PornItems) {
                        ifViewAttached(new ViewAction<VideoListView>() {
                            @Override
                            public void run(@NonNull VideoListView view) {
                                if (page == 1 || skipPage > 0) {
                                    view.setData(unLimit91PornItems);
                                    view.showContent();
                                    if (page == 1) {
                                        view.setPageData(getPageList());
                                    }
                                    view.updateCurrentPage(page);
                                } else {
                                    view.loadMoreDataComplete();
                                    view.setMoreData(unLimit91PornItems);
                                    view.updateCurrentPage(page);
                                }
                                if (skipPage > 0) {
                                    view.hideSkipPageLoading();
                                }
                                //已经最后一页了
                                if (page >= totalPage) {
                                    view.noMoreData();
                                } else {
                                    page++;
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        //首次加载失败，显示重试页
                        ifViewAttached(new ViewAction<VideoListView>() {
                            @Override
                            public void run(@NonNull VideoListView view) {
                                if (page == 1) {
                                    view.showError(msg);
                                } else {
                                    view.loadMoreFailed();
                                }
                                if (skipPage > 0) {
                                    view.hideSkipPageLoading();
                                }
                            }
                        });
                    }
                });
    }

    public List<Integer> getPageList() {
        List<Integer> pageList = new ArrayList<>();
        for (int i = 1; i <= totalPage; i++) {
            pageList.add(i);
        }
        return pageList;
    }
}
