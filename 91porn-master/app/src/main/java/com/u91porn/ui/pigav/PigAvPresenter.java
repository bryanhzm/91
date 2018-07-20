package com.u91porn.ui.pigav;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u91porn.data.DataManager;
import com.u91porn.data.model.PigAv;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.rxjava.RxSchedulersHelper;
import com.u91porn.ui.MvpBasePresenter;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

/**
 * @author flymegoc
 * @date 2018/1/30
 */

public class PigAvPresenter extends MvpBasePresenter<PigAvView> implements IPigAv {
    private static final String TAG = PigAvPresenter.class.getSimpleName();

    private int page = 2;
    private DataManager dataManager;

    @Inject
    public PigAvPresenter(LifecycleProvider<Lifecycle.Event> provider, DataManager dataManager) {
        super(provider);
        this.dataManager = dataManager;
    }

    @Override
    public void videoList(String category, boolean pullToRefresh) {
        if (pullToRefresh) {
            page = 2;
        }
        dataManager.loadPigAvListByCategory(category, pullToRefresh)
                .compose(RxSchedulersHelper.<List<PigAv>>ioMainThread())
                .compose(provider.<List<PigAv>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<List<PigAv>>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<PigAvView>() {
                            @Override
                            public void run(@NonNull PigAvView view) {
                                view.showLoading(true);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final List<PigAv> pigAvs) {
                        ifViewAttached(new ViewAction<PigAvView>() {
                            @Override
                            public void run(@NonNull PigAvView view) {
                                view.setData(pigAvs);
                                view.showContent();
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<PigAvView>() {
                            @Override
                            public void run(@NonNull PigAvView view) {
                                view.showError(msg);
                            }
                        });
                    }
                });

    }

    @Override
    public void moreVideoList(String category, boolean pullToRefresh) {
        dataManager.loadMorePigAvListByCategory(category, page, pullToRefresh)
                .compose(RxSchedulersHelper.<List<PigAv>>ioMainThread())
                .compose(provider.<List<PigAv>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<List<PigAv>>() {
                    @Override
                    public void onSuccess(final List<PigAv> pigAvs) {
                        ifViewAttached(new ViewAction<PigAvView>() {
                            @Override
                            public void run(@NonNull PigAvView view) {
                                if (pigAvs.size() == 0) {
                                    Logger.t(TAG).d("没有数据哦");
                                } else {
                                    view.setMoreData(pigAvs);
                                    page++;
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(String msg, int code) {
                        ifViewAttached(new ViewAction<PigAvView>() {
                            @Override
                            public void run(@NonNull PigAvView view) {
                                view.loadMoreFailed();
                            }
                        });
                    }
                });

    }
}
