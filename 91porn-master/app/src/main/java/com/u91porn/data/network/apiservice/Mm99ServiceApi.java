package com.u91porn.data.network.apiservice;

import com.u91porn.data.network.Api;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @author flymegoc
 * @date 2018/2/1
 */

public interface Mm99ServiceApi {

    /**
     * 图库列表
     * @param url 链接
     * @return ob
     */
    @Headers({"Domain-Name: " + Api.MM_99_DOMAIN_NAME})
    @GET
    Observable<String> imageList(@Url String url);

    /**
     * 对应图库图片列表
     * @param act act
     * @param id id
     * @return ob
     */
    @Headers({"Referer: http://www.99mm.me/meitui/",
            "Domain-Name: " + Api.MM_99_DOMAIN_NAME})
    @GET("url.php")
    Observable<String> imageLists(@Query("act") String act, @Query("id") int id);
}
