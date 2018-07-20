package com.u91porn.di.module;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.orhanobut.logger.Logger;
import com.u91porn.cookie.SetCookieCache;
import com.u91porn.cookie.SharedPrefsCookiePersistor;
import com.u91porn.data.network.Api;
import com.u91porn.data.network.apiservice.Forum91PronServiceApi;
import com.u91porn.data.network.apiservice.GitHubServiceApi;
import com.u91porn.data.network.apiservice.MeiZiTuServiceApi;
import com.u91porn.data.network.apiservice.Mm99ServiceApi;
import com.u91porn.data.network.apiservice.NoLimit91PornServiceApi;
import com.u91porn.data.network.apiservice.PigAvServiceApi;
import com.u91porn.data.network.apiservice.ProxyServiceApi;
import com.u91porn.data.prefs.PreferencesHelper;
import com.u91porn.di.ApplicationContext;
import com.u91porn.utils.AddressHelper;
import com.u91porn.utils.CommonHeaderInterceptor;
import com.u91porn.utils.MyProxySelector;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * @author flymegoc
 * @date 2018/2/10
 */
@Module
public class ApiServiceModule {

    private static final String TAG = ApiServiceModule.class.getSimpleName();

    @Singleton
    @Provides
    SharedPrefsCookiePersistor providesSharedPrefsCookiePersistor(@ApplicationContext Context context) {
        return new SharedPrefsCookiePersistor(context);
    }

    @Singleton
    @Provides
    SetCookieCache providesSetCookieCache() {
        return new SetCookieCache();
    }

    @Singleton
    @Provides
    PersistentCookieJar providesPersistentCookieJar(SharedPrefsCookiePersistor sharedPrefsCookiePersistor, SetCookieCache setCookieCache) {
        return new PersistentCookieJar(setCookieCache, sharedPrefsCookiePersistor);
    }

    @Singleton
    @Provides
    HttpLoggingInterceptor providesHttpLoggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String message) {
                Logger.t(TAG).d("HttpLog:" + message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        return logging;
    }

    @Singleton
    @Provides
    List<Proxy> providesListProxy() {
        return new ArrayList<>();
    }

    @Singleton
    @Provides
    MyProxySelector providesMyProxySelector(List<Proxy> proxyList, PreferencesHelper preferencesHelper) {
        return new MyProxySelector(proxyList, preferencesHelper);
    }

    @Singleton
    @Provides
    OkHttpClient providesOkHttpClient(CommonHeaderInterceptor commonHeaderInterceptor, HttpLoggingInterceptor httpLoggingInterceptor, PersistentCookieJar persistentCookieJar, MyProxySelector myProxySelector, AddressHelper addressHelper) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(commonHeaderInterceptor);
        builder.addInterceptor(httpLoggingInterceptor);
        builder.cookieJar(persistentCookieJar);
        builder.proxySelector(myProxySelector);
        //动态baseUrl
        RetrofitUrlManager.getInstance().putDomain(Api.GITHUB_DOMAIN_NAME, Api.APP_GITHUB_DOMAIN);
        RetrofitUrlManager.getInstance().putDomain(Api.MEI_ZI_TU_DOMAIN_NAME, Api.APP_MEIZITU_DOMAIN);
        RetrofitUrlManager.getInstance().putDomain(Api.MM_99_DOMAIN_NAME, Api.APP_99_MM_DOMAIN);
        RetrofitUrlManager.getInstance().putDomain(Api.XICI_DAILI_DOMAIN_NAME, Api.APP_PROXY_XICI_DAILI_DOMAIN);
        if (!TextUtils.isEmpty(addressHelper.getVideo91PornAddress())) {
            RetrofitUrlManager.getInstance().putDomain(Api.PORN91_VIDEO_DOMAIN_NAME, addressHelper.getVideo91PornAddress());
        }
        if (!TextUtils.isEmpty(addressHelper.getForum91PornAddress())) {
            RetrofitUrlManager.getInstance().putDomain(Api.PORN91_FORUM_DOMAIN_NAME, addressHelper.getForum91PornAddress());
        }
        if (!TextUtils.isEmpty(addressHelper.getPigAvAddress())) {
            RetrofitUrlManager.getInstance().putDomain(Api.PIGAV_DOMAIN_NAME, addressHelper.getPigAvAddress());
        }
        return RetrofitUrlManager.getInstance().with(builder).build();
    }

    @Singleton
    @Provides
    Retrofit providesRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Api.APP_GITHUB_DOMAIN)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }

    @Singleton
    @Provides
    GitHubServiceApi providesGitHubServiceApi(Retrofit retrofit) {
        return retrofit.create(GitHubServiceApi.class);
    }

    @Singleton
    @Provides
    MeiZiTuServiceApi providesMeiZiTuServiceApi(Retrofit retrofit) {
        return retrofit.create(MeiZiTuServiceApi.class);
    }

    @Singleton
    @Provides
    Mm99ServiceApi providesMm99ServiceApi(Retrofit retrofit) {
        return retrofit.create(Mm99ServiceApi.class);
    }

    @Singleton
    @Provides
    NoLimit91PornServiceApi provides91PornVideoServiceApi(Retrofit retrofit) {
        return retrofit.create(NoLimit91PornServiceApi.class);
    }

    @Singleton
    @Provides
    Forum91PronServiceApi provides91PornForumServiceApi(Retrofit retrofit) {
        return retrofit.create(Forum91PronServiceApi.class);
    }

    @Singleton
    @Provides
    PigAvServiceApi providesPigAvServiceApi(Retrofit retrofit) {
        return retrofit.create(PigAvServiceApi.class);
    }

    @Singleton
    @Provides
    ProxyServiceApi providesProxyServiceApi(Retrofit retrofit) {
        return retrofit.create(ProxyServiceApi.class);
    }
}
