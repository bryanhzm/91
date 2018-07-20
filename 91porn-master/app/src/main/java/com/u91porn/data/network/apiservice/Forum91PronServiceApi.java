package com.u91porn.data.network.apiservice;

import com.u91porn.data.network.Api;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * @author flymegoc
 * @date 2018/1/21
 */

public interface Forum91PronServiceApi {

    /**
     * 主页
     *
     * @return ob
     */
    @Headers({"Domain-Name: " + Api.PORN91_FORUM_DOMAIN_NAME})
    @GET("index.php")
    Observable<String> porn91ForumIndex();

    /**
     * 加载板块列表
     *
     * @param fid  板块id
     * @param page 页码
     * @return ob
     */
    @Headers({"Domain-Name: " + Api.PORN91_FORUM_DOMAIN_NAME})
    @GET("forumdisplay.php")
    Observable<String> forumdisplay(@Query("fid") String fid, @Query("page") int page);


    /**
     * 帖子具体内容
     *
     * @param tid 帖子id
     * @return ob
     */
    @Headers({
            "Domain-Name: " + Api.PORN91_FORUM_DOMAIN_NAME
    })
    @GET("viewthread.php")
    Observable<String> forumItemContent(@Query("tid") Long tid);
}
