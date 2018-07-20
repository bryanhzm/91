package com.u91porn.data.network.apiservice;

import com.u91porn.data.network.Api;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Url;

/**
 * @author flymegoc
 * @date 2018/1/17
 */

public interface GitHubServiceApi {
    /**
     * 检查更新
     *
     * @param url 链接
     * @return ob
     */
    @Headers({"Domain-Name: " + Api.GITHUB_DOMAIN_NAME})
    @GET
    Observable<String> checkUpdate(@Url String url);

    /**
     * 检查新公告
     *
     * @param url 链接
     * @return ob
     */
    @Headers({"Domain-Name: " + Api.GITHUB_DOMAIN_NAME})
    @GET
    Observable<String> checkNewNotice(@Url String url);
}
