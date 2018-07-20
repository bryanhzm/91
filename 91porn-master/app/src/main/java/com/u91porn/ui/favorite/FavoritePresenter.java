package com.u91porn.ui.favorite;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.bugsnag.android.Bugsnag;
import com.bugsnag.android.Severity;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.sdsmdg.tastytoast.TastyToast;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u91porn.data.DataManager;
import com.u91porn.data.model.BaseResult;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.User;
import com.u91porn.di.PerActivity;
import com.u91porn.exception.ApiException;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.rxjava.RetryWhenProcess;
import com.u91porn.rxjava.RxSchedulersHelper;
import com.u91porn.utils.SDCardUtils;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.common.io.FileUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author flymegoc
 * @date 2017/11/25
 * @describe
 */
@PerActivity
public class FavoritePresenter extends MvpBasePresenter<FavoriteView> implements IFavorite {
    private static final String TAG = FavoriteListener.class.getSimpleName();

    private User user;
    private Integer totalPage = 1;
    private int page = 1;
    private LifecycleProvider<Lifecycle.Event> provider;
    /**
     * 本次强制刷新过那下面的请求也一起刷新
     */
    private boolean cleanCache = false;

    private DataManager dataManager;

    @Inject
    public FavoritePresenter(DataManager dataManager, User user, LifecycleProvider<Lifecycle.Event> provider) {
        this.dataManager = dataManager;
        this.user = user;
        this.provider = provider;
    }

    @Override
    public void favorite(String uId, String videoId, String ownnerId) {
        favorite(uId, videoId, ownnerId, null);
    }

    public void favorite(String uId, String videoId, String ownnerId, final FavoriteListener favoriteListener) {

        dataManager.favoritePorn91Video(uId, videoId, ownnerId)
                .retryWhen(new RetryWhenProcess(RetryWhenProcess.PROCESS_TIME))
                .compose(RxSchedulersHelper.<String>ioMainThread())
                .compose(provider.<String>bindUntilEvent(Lifecycle.Event.ON_STOP))
                .subscribe(new CallBackWrapper<String>() {
                    @Override
                    public void onBegin(Disposable d) {

                    }

                    @Override
                    public void onSuccess(final String msg) {
                        if (favoriteListener != null) {
                            favoriteListener.onSuccess(msg);

                        } else {
                            ifViewAttached(new ViewAction<FavoriteView>() {
                                @Override
                                public void run(@NonNull FavoriteView view) {
                                    view.showMessage(msg, TastyToast.SUCCESS);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        if (code == ApiException.Error.NULLPOINTER_EXCEPTION) {
                            final String message = "收藏失败";
                            if (favoriteListener != null) {
                                favoriteListener.onError(message);
                            } else {
                                ifViewAttached(new ViewAction<FavoriteView>() {
                                    @Override
                                    public void run(@NonNull FavoriteView view) {
                                        view.showMessage(message, TastyToast.ERROR);
                                    }
                                });
                            }
                        } else {
                            if (favoriteListener != null) {
                                favoriteListener.onError(msg);
                            } else {
                                ifViewAttached(new ViewAction<FavoriteView>() {
                                    @Override
                                    public void run(@NonNull FavoriteView view) {
                                        view.showMessage(msg, TastyToast.ERROR);
                                    }
                                });
                            }
                        }
                    }
                });
    }


    @Override
    public void loadRemoteFavoriteData(final boolean pullToRefresh) {
        //如果刷新则重置页数
        if (pullToRefresh) {
            page = 1;
            cleanCache = true;
        }
        //RxCache条件区别
        String condition = null;
        if (user != null) {
            condition = user.getUserName();
        }
        if (TextUtils.isEmpty(condition)) {
            ifViewAttached(new ViewAction<FavoriteView>() {
                @Override
                public void run(@NonNull FavoriteView view) {
                    if (user != null) {
                        Bugsnag.notify(new Throwable(TAG + " user info: " + user.toString()), Severity.WARNING);
                    }
                    view.showError("用户信息不完整，请重新登录后重试！");
                }
            });
            return;
        }
        dataManager.loadPorn91MyFavoriteVideos(condition, page, cleanCache)
                .map(new Function<BaseResult<List<UnLimit91PornItem>>, List<UnLimit91PornItem>>() {
                    @Override
                    public List<UnLimit91PornItem> apply(BaseResult<List<UnLimit91PornItem>> baseResult) throws Exception {
                        if (page == 1) {
                            totalPage = baseResult.getTotalPage();
                        }
                        return baseResult.getData();
                    }
                })
                .retryWhen(new RetryWhenProcess(RetryWhenProcess.PROCESS_TIME))
                .compose(RxSchedulersHelper.<List<UnLimit91PornItem>>ioMainThread())
                .compose(provider.<List<UnLimit91PornItem>>bindUntilEvent(Lifecycle.Event.ON_STOP))
                .subscribe(new CallBackWrapper<List<UnLimit91PornItem>>() {
                    @Override
                    public void onBegin(Disposable d) {
                        //首次加载显示加载页
                        ifViewAttached(new ViewAction<FavoriteView>() {
                            @Override
                            public void run(@NonNull FavoriteView view) {
                                if (page == 1 && !pullToRefresh) {
                                    view.showLoading(pullToRefresh);
                                }
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final List<UnLimit91PornItem> unLimit91PornItems) {
                        ifViewAttached(new ViewAction<FavoriteView>() {
                            @Override
                            public void run(@NonNull FavoriteView view) {
                                if (page == 1) {
                                    view.setFavoriteData(unLimit91PornItems);
                                    view.showContent();
                                } else {
                                    view.loadMoreDataComplete();
                                    view.setMoreData(unLimit91PornItems);
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
                        ifViewAttached(new ViewAction<FavoriteView>() {
                            @Override
                            public void run(@NonNull FavoriteView view) {
                                if (page == 1) {
                                    view.showError(msg);
                                } else {
                                    view.loadMoreFailed();
                                }
                            }
                        });
                    }
                });
    }

    @Override
    public void deleteFavorite(String rvid) {
        dataManager.deletePorn91MyFavoriteVideo(rvid)
                .retryWhen(new RetryWhenProcess(RetryWhenProcess.PROCESS_TIME))
                .compose(RxSchedulersHelper.<List<UnLimit91PornItem>>ioMainThread())
                .compose(provider.<List<UnLimit91PornItem>>bindUntilEvent(Lifecycle.Event.ON_STOP))
                .subscribe(new CallBackWrapper<List<UnLimit91PornItem>>() {
                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<FavoriteView>() {
                            @Override
                            public void run(@NonNull FavoriteView view) {
                                view.showDeleteDialog();
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final List<UnLimit91PornItem> unLimit91PornItemList) {
                        ifViewAttached(new ViewAction<FavoriteView>() {
                            @Override
                            public void run(@NonNull FavoriteView view) {
                                //顺序很重要，涉及缓存
                                view.setFavoriteData(unLimit91PornItemList);
                                view.deleteFavoriteSucc("删除成功");

                            }
                        });

                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<FavoriteView>() {
                            @Override
                            public void run(@NonNull FavoriteView view) {
                                view.deleteFavoriteError(msg);
                            }
                        });
                    }
                })
        ;
    }

    @Override
    public void exportData(final boolean onlyUrl) {
        Observable.create(new ObservableOnSubscribe<List<UnLimit91PornItem>>() {
            @Override
            public void subscribe(ObservableEmitter<List<UnLimit91PornItem>> e) throws Exception {
                List<UnLimit91PornItem> unLimit91PornItems = dataManager.loadAllLimit91PornItems();
                e.onNext(unLimit91PornItems);
                e.onComplete();
            }
        }).map(new Function<List<UnLimit91PornItem>, String>() {
            @Override
            public String apply(List<UnLimit91PornItem> unLimit91PornItems) throws Exception {
                File file = new File(SDCardUtils.EXPORT_FILE);
                if (file.exists()) {
                    if (!file.delete()) {
                        throw new Exception("导出失败,因为删除原文件失败了");
                    }

                }
                if (!file.createNewFile()) {
                    throw new Exception("导出失败,创建新文件失败了");
                }
                if (onlyUrl) {
                    for (UnLimit91PornItem unLimit91PornItem : unLimit91PornItems) {
                        CharSequence data = unLimit91PornItem.getVideoResult().getVideoUrl() + "\r\n\r\n";
                        if (TextUtils.isEmpty(data)) {
                            continue;
                        }
                        FileUtils.writeChars(file, "UTF-8", data);
                    }
                } else {
                    for (UnLimit91PornItem unLimit91PornItem : unLimit91PornItems) {
                        String title = unLimit91PornItem.getTitle();
                        String videoUrl = unLimit91PornItem.getVideoResult().getVideoUrl();
                        CharSequence data = title + "\r\n" + videoUrl + "\r\n\r\n";
                        if (TextUtils.isEmpty(data)) {
                            continue;
                        }
                        FileUtils.writeChars(file, "UTF-8", data);
                    }
                }
                return "导出成功";
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(provider.<String>bindUntilEvent(Lifecycle.Event.ON_STOP))
                .subscribe(new CallBackWrapper<String>() {
                    @Override
                    public void onBegin(Disposable d) {

                    }

                    @Override
                    public void onSuccess(final String s) {
                        ifViewAttached(new ViewAction<FavoriteView>() {
                            @Override
                            public void run(@NonNull FavoriteView view) {
                                view.showMessage(s, TastyToast.SUCCESS);
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<FavoriteView>() {
                            @Override
                            public void run(@NonNull FavoriteView view) {
                                view.showMessage(msg, TastyToast.ERROR);
                            }
                        });
                    }
                });
    }

    public interface FavoriteListener {
        void onSuccess(String message);

        void onError(String message);
    }
}
