package com.u91porn.ui.porn91video.index;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u91porn.data.DataManager;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.rxjava.RetryWhenProcess;
import com.u91porn.rxjava.RxSchedulersHelper;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

/**
 * @author flymegoc
 * @date 2017/11/15
 * @describe
 */

public class IndexPresenter extends MvpBasePresenter<IndexView> implements IIndex {
    private static final String TAG = IndexPresenter.class.getSimpleName();
    private LifecycleProvider<Lifecycle.Event> provider;
    private DataManager dataManager;

    @Inject
    public IndexPresenter(LifecycleProvider<Lifecycle.Event> provider, DataManager dataManager) {
        this.provider = provider;
        this.dataManager = dataManager;
    }

    /**
     * 加载首页视频数据
     *
     * @param pullToRefresh 是否刷新
     */
    @Override
    public void loadIndexData(final boolean pullToRefresh, boolean cleanCache) {

        dataManager.loadPorn91VideoIndex(cleanCache)
                .retryWhen(new RetryWhenProcess(RetryWhenProcess.PROCESS_TIME))
                .compose(RxSchedulersHelper.<List<UnLimit91PornItem>>ioMainThread())
                .compose(provider.<List<UnLimit91PornItem>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<List<UnLimit91PornItem>>() {
                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<IndexView>() {
                            @Override
                            public void run(@NonNull IndexView view) {
                                if (!pullToRefresh) {
                                    view.showLoading(pullToRefresh);
                                }
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final List<UnLimit91PornItem> unLimit91PornItems) {
                        ifViewAttached(new ViewAction<IndexView>() {
                            @Override
                            public void run(@NonNull IndexView view) {
                                view.setData(unLimit91PornItems);
                                view.showContent();
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<IndexView>() {
                            @Override
                            public void run(@NonNull IndexView view) {
                                view.showError(msg);
                            }
                        });
                    }
                });
    }
}
