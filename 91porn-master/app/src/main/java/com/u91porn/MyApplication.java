package com.u91porn;

import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;

import com.bugsnag.android.Bugsnag;
import com.helper.loadviewhelper.load.LoadViewHelper;
import com.liulishuo.filedownloader.FileDownloader;
import com.squareup.leakcanary.LeakCanary;
import com.u91porn.data.DataManager;
import com.u91porn.di.component.ApplicationComponent;
import com.u91porn.di.component.DaggerApplicationComponent;
import com.u91porn.di.module.ApplicationModule;
import com.u91porn.eventbus.LowMemoryEvent;
import com.u91porn.utils.AppLogger;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;

/**
 * 应用入口
 *
 * @author flymegoc
 * @date 2017/11/14
 */

public class MyApplication extends MultiDexApplication {

    private static final String TAG = MyApplication.class.getSimpleName();

    private ApplicationComponent applicationComponent;

    @Inject
    DataManager dataManager;

    private static MyApplication myApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        applicationComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build();
        applicationComponent.inject(this);
        initNightMode();
        AppLogger.initLogger();
        initLeakCanary();
        initLoadingHelper();
        initFileDownload();
        if (!BuildConfig.DEBUG) {
            //初始化bug收集
            Bugsnag.init(this);
        }
        BGASwipeBackHelper.init(this, null);
    }

    public static MyApplication getInstance() {
        return myApplication;
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    private void initNightMode() {
        boolean isNightMode = dataManager.isOpenNightMode();
        AppCompatDelegate.setDefaultNightMode(isNightMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void initFileDownload() {
        FileDownloader.setup(this);
    }

    /**
     * 初始化加载界面，空界面等
     */
    private void initLoadingHelper() {
        LoadViewHelper.getBuilder()
                .setLoadEmpty(R.layout.empty_view)
                .setLoadError(R.layout.error_view)
                .setLoadIng(R.layout.loading_view);
    }

    /**
     * 初始化内存分析工具
     */
    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        boolean canReleaseMemory = dataManager.isForbiddenAutoReleaseMemory();
        if (!canReleaseMemory) {
            EventBus.getDefault().post(new LowMemoryEvent(TAG));
        }
    }

}
