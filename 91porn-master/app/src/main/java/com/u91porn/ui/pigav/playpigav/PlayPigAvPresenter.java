package com.u91porn.ui.pigav.playpigav;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.u91porn.data.DataManager;
import com.u91porn.data.model.PigAvVideo;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.rxjava.RxSchedulersHelper;
import com.u91porn.ui.MvpBasePresenter;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

/**
 * @author flymegoc
 * @date 2018/1/30
 */

public class PlayPigAvPresenter extends MvpBasePresenter<PlayPigAvView> implements IPlayPigAv {
    private static final String TAG = PlayPigAvPresenter.class.getSimpleName();

    private DataManager dataManager;

    @Inject
    public PlayPigAvPresenter(LifecycleProvider<Lifecycle.Event> provider, DataManager dataManager) {
        super(provider);
        this.dataManager = dataManager;
    }

    @Override
    public void parseVideoUrl(String url, String pId, boolean pullToRefresh) {

        dataManager.loadPigAvVideoUrl(url, pId, pullToRefresh)
                .compose(RxSchedulersHelper.<PigAvVideo>ioMainThread())
                .compose(provider.<PigAvVideo>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<PigAvVideo>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<PlayPigAvView>() {
                            @Override
                            public void run(@NonNull PlayPigAvView view) {
                                view.showLoading(true);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final PigAvVideo pigAvVideo) {
                        ifViewAttached(new ViewAction<PlayPigAvView>() {
                            @Override
                            public void run(@NonNull PlayPigAvView view) {
                                view.showContent();
                                view.playVideo(pigAvVideo);
                                view.listVideo(pigAvVideo.getPigAvList());
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<PlayPigAvView>() {
                            @Override
                            public void run(@NonNull PlayPigAvView view) {
                                view.showError(msg);
                            }
                        });
                    }
                });
    }
}
