package com.u91porn.di.module;

import android.app.Application;
import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.u91porn.cookie.AppCookieManager;
import com.u91porn.cookie.CookieManager;
import com.u91porn.data.AppDataManager;
import com.u91porn.data.DataManager;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.db.AppDbHelper;
import com.u91porn.data.db.DbHelper;
import com.u91porn.data.model.User;
import com.u91porn.data.network.ApiHelper;
import com.u91porn.data.network.AppApiHelper;
import com.u91porn.data.prefs.AppPreferencesHelper;
import com.u91porn.data.prefs.PreferencesHelper;
import com.u91porn.di.ApplicationContext;
import com.u91porn.di.DatabaseInfo;
import com.u91porn.di.PreferenceInfo;
import com.u91porn.utils.AddressHelper;
import com.u91porn.utils.AppCacheUtils;
import com.u91porn.utils.VideoCacheFileNameGenerator;
import com.u91porn.utils.constants.Constants;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;

/**
 * @author flymegoc
 * @date 2018/2/4
 */
@Module
public class ApplicationModule {

    private final Application mApplication;

    public ApplicationModule(Application mApplication) {
        this.mApplication = mApplication;
    }

    @Provides
    public Application providesApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationContext
    Context providesContext() {
        return mApplication;
    }

    @Singleton
    @Provides
    HttpProxyCacheServer providesHttpProxyCacheServer(@ApplicationContext Context context) {
        return new HttpProxyCacheServer.Builder(context)
                // 1 Gb for cache
                .maxCacheSize(AppCacheUtils.MAX_VIDEO_CACHE_SIZE)
                .cacheDirectory(AppCacheUtils.getVideoCacheDir(context))
                .fileNameGenerator(new VideoCacheFileNameGenerator())
                .build();
    }

    @Singleton
    @Provides
    CacheProviders providesCacheProviders(@ApplicationContext Context context) {
        File cacheDir = AppCacheUtils.getRxCacheDir(context);
        return new RxCache.Builder()
                .persistence(cacheDir, new GsonSpeaker())
                .using(CacheProviders.class);
    }

    @Singleton
    @Provides
    User providesUser() {
        return new User();
    }

    @Singleton
    @Provides
    Gson providesGson() {
        return new GsonBuilder().create();
    }

    @Singleton
    @Provides
    AddressHelper providesAddressHelper(PreferencesHelper preferencesHelper) {
        return new AddressHelper(preferencesHelper);
    }

    @Provides
    @DatabaseInfo
    String providesDatabaseName() {
        return Constants.DB_NAME;
    }

    @Provides
    @PreferenceInfo
    String providePreferenceName(@ApplicationContext Context context) {
        return context.getPackageName() + "_preferences";
    }

    @Provides
    @Singleton
    DataManager provideDataManager(AppDataManager appDataManager) {
        return appDataManager;
    }

    @Provides
    @Singleton
    DbHelper provideDbHelper(AppDbHelper appDbHelper) {
        return appDbHelper;
    }

    @Provides
    @Singleton
    PreferencesHelper providePreferencesHelper(AppPreferencesHelper appPreferencesHelper) {
        return appPreferencesHelper;
    }

    @Provides
    @Singleton
    ApiHelper providesApiHelper(AppApiHelper appApiHelper) {
        return appApiHelper;
    }

    @Provides
    @Singleton
    CookieManager providesCookieManager(AppCookieManager appCookieManager) {
        return appCookieManager;
    }
}
