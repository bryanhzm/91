package com.u91porn.di.component;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;
import com.google.gson.Gson;
import com.u91porn.MyApplication;
import com.u91porn.cookie.CookieManager;
import com.u91porn.data.DataManager;
import com.u91porn.data.model.User;
import com.u91porn.di.ApplicationContext;
import com.u91porn.di.module.ApiServiceModule;
import com.u91porn.di.module.ApplicationModule;
import com.u91porn.utils.AddressHelper;
import com.u91porn.utils.DownloadManager;
import com.u91porn.utils.MyProxySelector;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author flymegoc
 * @date 2018/2/4
 */
@Singleton
@Component(modules = {ApplicationModule.class, ApiServiceModule.class})
public interface ApplicationComponent {
    void inject(MyApplication myApplication);

    void inject(DownloadManager downloadManager);

    @ApplicationContext
    Context getContext();

    HttpProxyCacheServer getHttpProxyCacheServer();

    User getUser();

    AddressHelper getAddressHelper();

    MyProxySelector getMyProxySelector();

    Gson getGson();

    DataManager getDataManager();

    CookieManager getCookieManager();
}
