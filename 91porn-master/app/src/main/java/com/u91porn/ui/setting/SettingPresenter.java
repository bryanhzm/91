package com.u91porn.ui.setting;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u91porn.data.DataManager;
import com.u91porn.data.network.Api;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.rxjava.RxSchedulersHelper;
import com.u91porn.ui.MvpBasePresenter;
import com.u91porn.ui.porn91video.search.SearchPresenter;
import com.u91porn.utils.SDCardUtils;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import cn.qqtheme.framework.util.FileUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;

/**
 * @author flymegoc
 * @date 2018/2/6
 */

public class SettingPresenter extends MvpBasePresenter<SettingView> implements ISetting {

    private static final String TAG = SearchPresenter.class.getSimpleName();
    private DataManager dataManager;

    @Inject
    public SettingPresenter(LifecycleProvider<Lifecycle.Event> provider, DataManager dataManager) {
        super(provider);
        this.dataManager = dataManager;
    }

    @Override
    public void test91PornVideo(String baseUrl, final QMUICommonListItemView qmuiCommonListItemView, final String key) {
        // 全局 BaseUrl 的优先级低于 Domain-Name header 中单独配置的,其他未配置的接口将受全局 BaseUrl 的影响
        RetrofitUrlManager.getInstance().putDomain(Api.PORN91_VIDEO_DOMAIN_NAME, baseUrl);
        dataManager.testPorn91VideoAddress()
                .compose(RxSchedulersHelper.<String>ioMainThread())
                .compose(provider.<String>bindToLifecycle())
                .subscribe(new CallBackWrapper<String>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.showTestingAddressDialog(true);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final String s) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.testNewAddressSuccess(s, qmuiCommonListItemView, key);
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.testNewAddressFailure(msg, qmuiCommonListItemView, key);
                            }
                        });
                    }
                });
    }


    @Override
    public void test91PornForum(String baseUrl, final QMUICommonListItemView qmuiCommonListItemView, final String key) {
        RetrofitUrlManager.getInstance().putDomain(Api.PORN91_FORUM_DOMAIN_NAME, baseUrl);
        dataManager.testPorn91ForumAddress()
                .compose(RxSchedulersHelper.<String>ioMainThread())
                .compose(provider.<String>bindToLifecycle())
                .subscribe(new CallBackWrapper<String>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.showTestingAddressDialog(true);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final String s) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.testNewAddressSuccess(s, qmuiCommonListItemView, key);
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.testNewAddressFailure(msg, qmuiCommonListItemView, key);
                            }
                        });
                    }
                });
    }

    @Override
    public void testPigAv(String baseUrl, final QMUICommonListItemView qmuiCommonListItemView, final String key) {
        RetrofitUrlManager.getInstance().putDomain(Api.PIGAV_DOMAIN_NAME, baseUrl);
        dataManager.testPigAvAddress(baseUrl)
                .compose(RxSchedulersHelper.<String>ioMainThread())
                .compose(provider.<String>bindToLifecycle())
                .subscribe(new CallBackWrapper<String>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.showTestingAddressDialog(true);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final String s) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.testNewAddressSuccess(s, qmuiCommonListItemView, key);
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.testNewAddressFailure(msg, qmuiCommonListItemView, key);
                            }
                        });
                    }
                });
    }

    @Override
    public boolean isHaveUnFinishDownloadVideo() {
        return dataManager.loadDownloadingData().size() != 0;
    }

    @Override
    public boolean isHaveFinishDownloadVideoFile() {
        if (!TextUtils.isEmpty(dataManager.getCustomDownloadVideoDirPath())) {
            File file = new File(dataManager.getCustomDownloadVideoDirPath());
            return file.listFiles().length != 0;
        }
        File file = new File(SDCardUtils.DOWNLOAD_VIDEO_PATH);
        //检查是否有MP4文件
        for (File file1 : file.listFiles()) {
            if (file1.getName().endsWith(".mp4")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void moveOldDownloadVideoToNewDir(final String newDirPath, final QMUICommonListItemView qmuiCommonListItemView) {
        Observable.fromCallable(new Callable<File[]>() {
            @Override
            public File[] call() throws Exception {
                File file = new File(dataManager.getCustomDownloadVideoDirPath());
                return file.listFiles();
            }
        }).flatMap(new Function<File[], ObservableSource<File>>() {
            @Override
            public ObservableSource<File> apply(File[] files) throws Exception {
                return Observable.fromArray(files);
            }
        }).filter(new Predicate<File>() {
            @Override
            public boolean test(File file) throws Exception {
                return file.getName().endsWith(".mp4");
            }
        })
                .map(new Function<File, String>() {
                    @Override
                    public String apply(File file) throws Exception {
                        FileUtils.move(file, new File(newDirPath, file.getName()));
                        return file.getAbsolutePath();
                    }
                }).delay(1, TimeUnit.SECONDS)
                .compose(RxSchedulersHelper.<String>ioMainThread())
                .compose(provider.<String>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<String>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.beginMoveOldDirDownloadVideoToNewDir();
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final String s) {
                        Logger.t(TAG).d("正在移动到：" + s);
                    }

                    @Override
                    public void onError(String msg, int code) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.setNewDownloadVideoDirError("移动文件失败，无法设置新目录");
                            }
                        });
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                dataManager.setCustomDownloadVideoDirPath(newDirPath);
                                qmuiCommonListItemView.setDetailText(newDirPath);
                                view.setNewDownloadVideoDirSuccess("移动文件完成,设置新目录成功");
                            }
                        });
                    }
                });
    }
}
