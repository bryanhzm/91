package com.u91porn.ui.porn91forum.browse91porn;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u91porn.data.DataManager;
import com.u91porn.data.model.Porn91ForumContent;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.rxjava.RxSchedulersHelper;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

/**
 * @author flymegoc
 * @date 2018/1/24
 */

public class Browse91Presenter extends MvpBasePresenter<Browse91View> implements IBrowse91 {

    private DataManager dataManager;
    private LifecycleProvider<Lifecycle.Event> provider;

    @Inject
    public Browse91Presenter(LifecycleProvider<Lifecycle.Event> provider, DataManager dataManager) {
        this.provider = provider;
        this.dataManager = dataManager;
    }

    @Override
    public void loadContent(Long tid, final boolean isNightModel) {
        dataManager.loadPorn91ForumContent(tid, isNightModel)
                .compose(RxSchedulersHelper.<Porn91ForumContent>ioMainThread())
                .compose(provider.<Porn91ForumContent>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<Porn91ForumContent>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<Browse91View>() {
                            @Override
                            public void run(@NonNull Browse91View view) {
                                view.showLoading(true);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final Porn91ForumContent porn91ForumContent) {
                        ifViewAttached(new ViewAction<Browse91View>() {
                            @Override
                            public void run(@NonNull Browse91View view) {
                                view.showContent();
                                view.loadContentSuccess(porn91ForumContent);
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<Browse91View>() {
                            @Override
                            public void run(@NonNull Browse91View view) {
                                view.showError(msg);
                            }
                        });
                    }
                });
    }
}
