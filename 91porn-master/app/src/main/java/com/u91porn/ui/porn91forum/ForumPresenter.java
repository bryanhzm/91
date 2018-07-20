package com.u91porn.ui.porn91forum;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u91porn.data.DataManager;
import com.u91porn.data.model.BaseResult;
import com.u91porn.data.model.Forum91PronItem;
import com.u91porn.data.model.PinnedHeaderEntity;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.rxjava.RxSchedulersHelper;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * @author flymegoc
 * @date 2018/1/23
 */

public class ForumPresenter extends MvpBasePresenter<ForumView> implements IForum {

    protected LifecycleProvider<Lifecycle.Event> provider;
    private int page = 1;
    private int totalPage = 1;
    private DataManager dataManager;

    @Inject
    public ForumPresenter(LifecycleProvider<Lifecycle.Event> provider, DataManager dataManager) {
        this.provider = provider;
        this.dataManager = dataManager;
    }

    @Override
    public void loadForumIndexListData(final boolean pullToRefresh) {
        dataManager.loadPorn91ForumIndex()
                .compose(RxSchedulersHelper.<List<PinnedHeaderEntity<Forum91PronItem>>>ioMainThread())
                .compose(provider.<List<PinnedHeaderEntity<Forum91PronItem>>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<List<PinnedHeaderEntity<Forum91PronItem>>>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<ForumView>() {
                            @Override
                            public void run(@NonNull ForumView view) {
                                if (pullToRefresh) {
                                    view.showLoading(pullToRefresh);
                                }
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final List<PinnedHeaderEntity<Forum91PronItem>> pinnedHeaderEntityList) {
                        ifViewAttached(new ViewAction<ForumView>() {
                            @Override
                            public void run(@NonNull ForumView view) {
                                view.setForumIndexListData(pinnedHeaderEntityList);
                                view.showContent();
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<ForumView>() {
                            @Override
                            public void run(@NonNull ForumView view) {
                                view.showError(msg);
                            }
                        });
                    }
                });
    }

    @Override
    public void loadForumListData(final boolean pullToRefresh, String fid) {
        if (pullToRefresh) {
            page = 1;
        }
        dataManager.loadPorn91ForumListData(fid, page)
                .map(new Function<BaseResult<List<Forum91PronItem>>, List<Forum91PronItem>>() {
                    @Override
                    public List<Forum91PronItem> apply(BaseResult<List<Forum91PronItem>> baseResult) throws Exception {
                        if (page == 1) {
                            totalPage = baseResult.getTotalPage();
                        }
                        return baseResult.getData();
                    }
                })
                .compose(RxSchedulersHelper.<List<Forum91PronItem>>ioMainThread())
                .compose(provider.<List<Forum91PronItem>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<List<Forum91PronItem>>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<ForumView>() {
                            @Override
                            public void run(@NonNull ForumView view) {
                                if (pullToRefresh) {
                                    view.showLoading(pullToRefresh);
                                }
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final List<Forum91PronItem> forum91PronItems) {
                        ifViewAttached(new ViewAction<ForumView>() {
                            @Override
                            public void run(@NonNull ForumView view) {
                                if (page == 1) {
                                    view.setForumListData(forum91PronItems);
                                    view.showContent();
                                } else {
                                    view.setMoreData(forum91PronItems);
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
                        ifViewAttached(new ViewAction<ForumView>() {
                            @Override
                            public void run(@NonNull ForumView view) {
                                view.showError(msg);
                            }
                        });
                    }
                });
    }
}
