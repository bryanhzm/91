package com.u91porn.data.network;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.model.BaseResult;
import com.u91porn.data.model.Favorite;
import com.u91porn.data.model.Forum91PronItem;
import com.u91porn.data.model.MeiZiTu;
import com.u91porn.data.model.Mm99;
import com.u91porn.data.model.Notice;
import com.u91porn.data.model.PigAv;
import com.u91porn.data.model.PigAvFormRequest;
import com.u91porn.data.model.PigAvLoadMoreResponse;
import com.u91porn.data.model.PigAvVideo;
import com.u91porn.data.model.PinnedHeaderEntity;
import com.u91porn.data.model.Porn91ForumContent;
import com.u91porn.data.model.ProxyModel;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.UpdateVersion;
import com.u91porn.data.model.User;
import com.u91porn.data.model.VideoComment;
import com.u91porn.data.model.VideoCommentResult;
import com.u91porn.data.model.VideoResult;
import com.u91porn.data.network.apiservice.Forum91PronServiceApi;
import com.u91porn.data.network.apiservice.GitHubServiceApi;
import com.u91porn.data.network.apiservice.MeiZiTuServiceApi;
import com.u91porn.data.network.apiservice.Mm99ServiceApi;
import com.u91porn.data.network.apiservice.NoLimit91PornServiceApi;
import com.u91porn.data.network.apiservice.PigAvServiceApi;
import com.u91porn.data.network.apiservice.ProxyServiceApi;
import com.u91porn.exception.FavoriteException;
import com.u91porn.exception.MessageException;
import com.u91porn.parser.Parse91PronVideo;
import com.u91porn.parser.Parse99Mm;
import com.u91porn.parser.ParseForum91Porn;
import com.u91porn.parser.ParseMeiZiTu;
import com.u91porn.parser.ParsePigAv;
import com.u91porn.parser.ParseProxy;
import com.u91porn.rxjava.RetryWhenProcess;
import com.u91porn.utils.AddressHelper;
import com.u91porn.utils.CheckResultUtils;
import com.u91porn.utils.HeaderUtils;
import com.u91porn.utils.UserHelper;
import com.u91porn.utils.constants.Constants;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.DynamicKeyGroup;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.EvictDynamicKeyGroup;
import io.rx_cache2.EvictProvider;
import io.rx_cache2.Reply;

/**
 * @author flymegoc
 * @date 2018/3/4
 */

@Singleton
public class AppApiHelper implements ApiHelper {

    private static final String TAG = AppApiHelper.class.getSimpleName();
    private CacheProviders cacheProviders;

    private NoLimit91PornServiceApi noLimit91PornServiceApi;
    private Forum91PronServiceApi forum91PronServiceApi;
    private GitHubServiceApi gitHubServiceApi;
    private MeiZiTuServiceApi meiZiTuServiceApi;
    private Mm99ServiceApi mm99ServiceApi;
    private PigAvServiceApi pigAvServiceApi;
    private ProxyServiceApi proxyServiceApi;
    private AddressHelper addressHelper;
    private Gson gson;

    @Inject
    public AppApiHelper(CacheProviders cacheProviders, NoLimit91PornServiceApi noLimit91PornServiceApi, Forum91PronServiceApi forum91PronServiceApi, GitHubServiceApi gitHubServiceApi, MeiZiTuServiceApi meiZiTuServiceApi, Mm99ServiceApi mm99ServiceApi, PigAvServiceApi pigAvServiceApi, ProxyServiceApi proxyServiceApi, AddressHelper addressHelper, Gson gson) {
        this.cacheProviders = cacheProviders;
        this.noLimit91PornServiceApi = noLimit91PornServiceApi;
        this.forum91PronServiceApi = forum91PronServiceApi;
        this.gitHubServiceApi = gitHubServiceApi;
        this.meiZiTuServiceApi = meiZiTuServiceApi;
        this.mm99ServiceApi = mm99ServiceApi;
        this.pigAvServiceApi = pigAvServiceApi;
        this.proxyServiceApi = proxyServiceApi;
        this.addressHelper = addressHelper;
        this.gson = gson;
    }

    @Override
    public Observable<List<UnLimit91PornItem>> loadPorn91VideoIndex(boolean cleanCache) {
        Observable<String> indexPhpObservable = noLimit91PornServiceApi.porn91VideoIndexPhp(HeaderUtils.getIndexHeader(addressHelper));
        return cacheProviders.getIndexPhp(indexPhpObservable, new EvictProvider(cleanCache))
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> responseBodyReply) throws Exception {
                        switch (responseBodyReply.getSource()) {
                            case CLOUD:
                                Logger.t(TAG).d("数据来自：网络");
                                break;
                            case MEMORY:
                                Logger.t(TAG).d("数据来自：内存");
                                break;
                            case PERSISTENCE:
                                Logger.t(TAG).d("数据来自：磁盘缓存");
                                break;
                            default:
                                break;
                        }
                        return responseBodyReply.getData();
                    }

                })
                .map(new Function<String, List<UnLimit91PornItem>>() {
                    @Override
                    public List<UnLimit91PornItem> apply(String s) throws Exception {
                        return Parse91PronVideo.parseIndex(s);
                    }
                });
    }

    @Override
    public Observable<BaseResult<List<UnLimit91PornItem>>> loadPorn91VideoByCategory(String category, String viewType, int page, String m, boolean cleanCache, boolean isLoadMoreCleanCache) {
        //RxCache条件区别
        String condition;
        if (TextUtils.isEmpty(m)) {
            condition = category;
        } else {
            condition = category + m;
        }
        DynamicKeyGroup dynamicKeyGroup = new DynamicKeyGroup(condition, page);
        EvictDynamicKey evictDynamicKey = new EvictDynamicKey(cleanCache || isLoadMoreCleanCache);

        Observable<String> categoryPage = noLimit91PornServiceApi.getCategoryPage(category, viewType, page, m, HeaderUtils.getIndexHeader(addressHelper));
        return cacheProviders.getCategoryPage(categoryPage, dynamicKeyGroup, evictDynamicKey)
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> responseBody) throws Exception {
                        return responseBody.getData();
                    }
                })
                .map(new Function<String, BaseResult<List<UnLimit91PornItem>>>() {
                    @Override
                    public BaseResult<List<UnLimit91PornItem>> apply(String s) throws Exception {
                        return Parse91PronVideo.parseHot(s);
                    }
                });
    }

    @Override
    public Observable<BaseResult<List<UnLimit91PornItem>>> loadPorn91authorVideos(String uid, String type, int page, boolean cleanCache) {
        //RxCache条件区别
        String condition = null;
        if (!TextUtils.isEmpty(uid)) {
            condition = uid;
        }
        DynamicKeyGroup dynamicKeyGroup = new DynamicKeyGroup(condition, page);
        EvictDynamicKey evictDynamicKey = new EvictDynamicKey(cleanCache);

        Observable<String> stringObservable = noLimit91PornServiceApi.authorVideos(uid, type, page);
        return cacheProviders.authorVideos(stringObservable, dynamicKeyGroup, evictDynamicKey)
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> responseBody) throws Exception {
                        return responseBody.getData();
                    }
                }).map(new Function<String, BaseResult<List<UnLimit91PornItem>>>() {
                    @Override
                    public BaseResult<List<UnLimit91PornItem>> apply(String s) throws Exception {
                        return Parse91PronVideo.parseAuthorVideos(s);
                    }
                });
    }

    @Override
    public Observable<BaseResult<List<UnLimit91PornItem>>> loadPorn91VideoRecentUpdates(String next, int page, boolean cleanCache, boolean isLoadMoreCleanCache) {

        DynamicKeyGroup dynamicKeyGroup = new DynamicKeyGroup(next, page);
        EvictDynamicKey evictDynamicKey = new EvictDynamicKey(cleanCache || isLoadMoreCleanCache);

        Observable<String> categoryPage = noLimit91PornServiceApi.recentUpdates(next, page, HeaderUtils.getIndexHeader(addressHelper));
        return cacheProviders.getRecentUpdates(categoryPage, dynamicKeyGroup, evictDynamicKey)
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> responseBody) throws Exception {
                        return responseBody.getData();
                    }
                }).map(new Function<String, BaseResult<List<UnLimit91PornItem>>>() {
                    @Override
                    public BaseResult<List<UnLimit91PornItem>> apply(String s) throws Exception {
                        return Parse91PronVideo.parseHot(s);
                    }
                });
    }

    @Override
    public Observable<VideoResult> loadPorn91VideoUrl(String viewKey) {
        String ip = addressHelper.getRandomIPAddress();
        Observable<String> stringObservable = noLimit91PornServiceApi.getVideoPlayPage(viewKey, ip, HeaderUtils.getIndexHeader(addressHelper));
        return cacheProviders.getVideoPlayPage(stringObservable, new DynamicKey(viewKey), new EvictDynamicKey(false))
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> responseBodyReply) throws Exception {
                        switch (responseBodyReply.getSource()) {
                            case CLOUD:
                                Logger.t(TAG).d("数据来自：网络");
                                break;
                            case MEMORY:
                                Logger.t(TAG).d("数据来自：内存");
                                break;
                            case PERSISTENCE:
                                Logger.t(TAG).d("数据来自：磁盘缓存");
                                break;
                            default:
                                break;
                        }
                        return responseBodyReply.getData();
                    }
                })
                .map(new Function<String, VideoResult>() {
                    @Override
                    public VideoResult apply(String s) throws Exception {
                        return Parse91PronVideo.parseVideoPlayUrl(s);
                    }
                });
    }

    @Override
    public Observable<List<VideoComment>> loadPorn91VideoComments(String videoId, int page, String viewKey) {
        return noLimit91PornServiceApi.getVideoComments(videoId, page, Constants.PORN91_VIDEO_COMMENT_PER_PAGE_NUM, HeaderUtils.getPlayVideoReferer(viewKey, addressHelper))
                .map(new Function<String, List<VideoComment>>() {
                    @Override
                    public List<VideoComment> apply(String s) throws Exception {
                        return Parse91PronVideo.parseVideoComment(s);
                    }
                });
    }

    @Override
    public Observable<String> commentPorn91Video(String cpaintFunction, String comment, String uid, String vid, String viewKey, String responseType) {
        return noLimit91PornServiceApi.commentVideo(cpaintFunction, comment, uid, vid, responseType, HeaderUtils.getPlayVideoReferer(viewKey, addressHelper))
                .map(new Function<String, VideoCommentResult>() {
                    @Override
                    public VideoCommentResult apply(String s) throws Exception {
                        return new Gson().fromJson(s, VideoCommentResult.class);
                    }
                })
                .map(new Function<VideoCommentResult, String>() {
                    @Override
                    public String apply(VideoCommentResult videoCommentResult) throws Exception {
                        String msg = "评论错误，未知错误";
                        if (videoCommentResult.getA().size() == 0) {
                            throw new MessageException("评论错误，未知错误");
                        } else if (videoCommentResult.getA().get(0).getData() == VideoCommentResult.COMMENT_SUCCESS) {
                            msg = "留言已经提交，审核后通过";
                        } else if (videoCommentResult.getA().get(0).getData() == VideoCommentResult.COMMENT_ALLREADY) {
                            throw new MessageException("你已经在这个视频下留言过");
                        } else if (videoCommentResult.getA().get(0).getData() == VideoCommentResult.COMMENT_NO_PERMISION) {
                            throw new MessageException("不允许留言!");
                        }
                        return msg;
                    }
                });
    }

    @Override
    public Observable<String> replyPorn91VideoComment(String comment, String username, String vid, String commentId, String viewKey) {
        return noLimit91PornServiceApi.replyVideoComment(comment, username, vid, commentId, HeaderUtils.getPlayVideoReferer(viewKey, addressHelper));
    }

    @Override
    public Observable<BaseResult<List<UnLimit91PornItem>>> searchPorn91Videos(String viewType, int page, String searchType, String searchId, String sort) {
        return noLimit91PornServiceApi.searchVideo(viewType, page, searchType, searchId, sort, HeaderUtils.getIndexHeader(addressHelper), addressHelper.getRandomIPAddress())
                .map(new Function<String, BaseResult<List<UnLimit91PornItem>>>() {
                    @Override
                    public BaseResult<List<UnLimit91PornItem>> apply(String s) throws Exception {
                        return Parse91PronVideo.parseSearchVideos(s);
                    }
                });
    }

    @Override
    public Observable<String> favoritePorn91Video(String uId, String videoId, String ownnerId) {
        String cpaintFunction = "addToFavorites";
        String responseType = "json";
        return noLimit91PornServiceApi.favoriteVideo(cpaintFunction, uId, videoId, ownnerId, responseType, HeaderUtils.getIndexHeader(addressHelper))
                .map(new Function<String, Favorite>() {
                    @Override
                    public Favorite apply(String s) throws Exception {
                        Logger.t(TAG).d("favoriteStr: " + s);
                        return new Gson().fromJson(s, Favorite.class);
                    }
                })
                .map(new Function<Favorite, Integer>() {
                    @Override
                    public Integer apply(Favorite favorite) throws Exception {
                        return favorite.getAddFavMessage().get(0).getData();
                    }
                })
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer code) throws Exception {
                        String msg;
                        switch (code) {
                            case Favorite.FAVORITE_SUCCESS:
                                msg = "收藏成功";
                                break;
                            case Favorite.FAVORITE_FAIL:
                                throw new FavoriteException("收藏失败");
                            case Favorite.FAVORITE_ALREADY:
                                throw new FavoriteException("已经收藏过了");
                            case Favorite.FAVORITE_YOURSELF:
                                throw new FavoriteException("不能收藏自己的视频");
                            default:
                                throw new FavoriteException("收藏失败");
                        }
                        return msg;
                    }
                });
    }

    @Override
    public Observable<BaseResult<List<UnLimit91PornItem>>> loadPorn91MyFavoriteVideos(String userName, int page, boolean cleanCache) {
        Observable<String> favoriteObservable = noLimit91PornServiceApi.myFavoriteVideo(page, HeaderUtils.getIndexHeader(addressHelper));
        DynamicKeyGroup dynamicKeyGroup = new DynamicKeyGroup(userName, page);
        EvictDynamicKey evictDynamicKey = new EvictDynamicKey(cleanCache);
        return cacheProviders.getFavorite(favoriteObservable, dynamicKeyGroup, evictDynamicKey)
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> responseBody) throws Exception {
                        return responseBody.getData();
                    }
                })
                .map(new Function<String, BaseResult<List<UnLimit91PornItem>>>() {
                    @Override
                    public BaseResult<List<UnLimit91PornItem>> apply(String s) throws Exception {
                        return Parse91PronVideo.parseMyFavorite(s);
                    }
                });
    }

    @Override
    public Observable<List<UnLimit91PornItem>> deletePorn91MyFavoriteVideo(String rvid) {
        String removeFavour = "Remove Favorite";
        return noLimit91PornServiceApi.deleteMyFavoriteVideo(rvid, removeFavour, 45, 19, HeaderUtils.getFavHeader(addressHelper))
                .map(new Function<String, BaseResult<List<UnLimit91PornItem>>>() {
                    @Override
                    public BaseResult<List<UnLimit91PornItem>> apply(String s) throws Exception {
                        return Parse91PronVideo.parseMyFavorite(s);
                    }
                })
                .map(new Function<BaseResult<List<UnLimit91PornItem>>, List<UnLimit91PornItem>>() {
                    @Override
                    public List<UnLimit91PornItem> apply(BaseResult<List<UnLimit91PornItem>> baseResult) throws Exception {
                        if (baseResult.getCode() == BaseResult.ERROR_CODE) {
                            throw new FavoriteException(baseResult.getMessage());
                        }
                        if (baseResult.getCode() != BaseResult.SUCCESS_CODE || TextUtils.isEmpty(baseResult.getMessage())) {
                            throw new FavoriteException("删除失败了");
                        }
                        return baseResult.getData();
                    }
                });
    }

    @Override
    public Observable<User> userLoginPorn91Video(String username, String password, String captcha) {

        String fingerprint = UserHelper.randomFingerprint();
        String fingerprint2 = UserHelper.randomFingerprint2();
        String actionLogin = "Log In";
        String x = "47";
        String y = "12";
        return noLimit91PornServiceApi.login(username, password, fingerprint, fingerprint2, captcha, actionLogin, x, y, HeaderUtils.getUserHeader(addressHelper, "login"))
                .retryWhen(new RetryWhenProcess(2))
                .map(new Function<String, User>() {
                    @Override
                    public User apply(String s) throws Exception {
                        if (!UserHelper.isPornVideoLoginSuccess(s)) {
                            String errorInfo = Parse91PronVideo.parseErrorInfo(s);
                            if (TextUtils.isEmpty(errorInfo)) {
                                errorInfo = "未知错误，请确认地址是否正确";
                            }
                            throw new MessageException(errorInfo);
                        }
                        return Parse91PronVideo.parseUserInfo(s);
                    }
                });
    }

    @Override
    public Observable<User> userRegisterPorn91Video(String username, String password1, String password2, String email, String captchaInput) {
        String next = "";
//        String fingerprint = "2192328486";
        String fingerprint = UserHelper.randomFingerprint();
        String vip = "";
        String actionSignUp = "Sign Up";
        String submitX = "45";
        String submitY = "13";
        String ipAddress = addressHelper.getRandomIPAddress();
        return noLimit91PornServiceApi.register(next, username, password1, password2, email, captchaInput, fingerprint, vip, actionSignUp, submitX, submitY, HeaderUtils.getUserHeader(addressHelper, "signup"), ipAddress)
                .retryWhen(new RetryWhenProcess(2))
                .map(new Function<String, User>() {
                    @Override
                    public User apply(String s) throws Exception {
                        if (!UserHelper.isPornVideoLoginSuccess(s)) {
                            String errorInfo = Parse91PronVideo.parseErrorInfo(s);
                            throw new MessageException(errorInfo);
                        }
                        return Parse91PronVideo.parseUserInfo(s);
                    }
                });
    }

    @Override
    public Observable<List<PinnedHeaderEntity<Forum91PronItem>>> loadPorn91ForumIndex() {
        return forum91PronServiceApi.porn91ForumIndex()
                .map(new Function<String, List<PinnedHeaderEntity<Forum91PronItem>>>() {
                    @Override
                    public List<PinnedHeaderEntity<Forum91PronItem>> apply(String s) throws Exception {
                        BaseResult<List<PinnedHeaderEntity<Forum91PronItem>>> baseResult = ParseForum91Porn.parseIndex(s);
                        return baseResult.getData();
                    }
                });
    }

    @Override
    public Observable<BaseResult<List<Forum91PronItem>>> loadPorn91ForumListData(String fid, final int page) {
        return forum91PronServiceApi.forumdisplay(fid, page)
                .map(new Function<String, BaseResult<List<Forum91PronItem>>>() {
                    @Override
                    public BaseResult<List<Forum91PronItem>> apply(String s) throws Exception {
                        return ParseForum91Porn.parseForumList(s, page);
                    }
                });
    }

    @Override
    public Observable<Porn91ForumContent> loadPorn91ForumContent(Long tid, final boolean isNightModel) {
        return forum91PronServiceApi.forumItemContent(tid)
                .map(new Function<String, Porn91ForumContent>() {
                    @Override
                    public Porn91ForumContent apply(String s) throws Exception {
                        return ParseForum91Porn.parseContent(s, isNightModel, addressHelper.getForum91PornAddress()).getData();
                    }
                });
    }

    @Override
    public Observable<UpdateVersion> checkUpdate(String url) {
        return gitHubServiceApi.checkUpdate(url)
                .map(new Function<String, UpdateVersion>() {
                    @Override
                    public UpdateVersion apply(String s) throws Exception {
                        Document doc = Jsoup.parse(s);
                        String text = doc.select("table.highlight").text();
                        return gson.fromJson(text, UpdateVersion.class);
                    }
                });
    }

    @Override
    public Observable<Notice> checkNewNotice(String url) {
        return gitHubServiceApi.checkNewNotice(url)
                .map(new Function<String, Notice>() {
                    @Override
                    public Notice apply(String s) throws Exception {
                        Document doc = Jsoup.parse(s);
                        String text = doc.select("table.highlight").text();
                        return gson.fromJson(text, Notice.class);
                    }
                });
    }

    @Override
    public Observable<BaseResult<List<MeiZiTu>>> listMeiZiTu(String tag, int page, boolean pullToRefresh) {
        switch (tag) {
            case "index":
                return action(meiZiTuServiceApi.meiZiTuIndex(page), tag, page, pullToRefresh);
            case "hot":
                return action(meiZiTuServiceApi.meiZiTuHot(page), tag, page, pullToRefresh);
            case "best":
                return action(meiZiTuServiceApi.meiZiTuBest(page), tag, page, pullToRefresh);
            case "japan":
                return action(meiZiTuServiceApi.meiZiTuJapan(page), tag, page, pullToRefresh);
            case "taiwan":
                return action(meiZiTuServiceApi.meiZiTuJaiwan(page), tag, page, pullToRefresh);
            case "xinggan":
                return action(meiZiTuServiceApi.meiZiTuSexy(page), tag, page, pullToRefresh);
            case "mm":
                return action(meiZiTuServiceApi.meiZiTuMm(page), tag, page, pullToRefresh);
            default:
                return null;
        }
    }

    @Override
    public Observable<List<String>> meiZiTuImageList(int id, boolean pullToRefresh) {
        return cacheProviders.meiZiTu(meiZiTuServiceApi.meiZiTuImageList(id), new DynamicKey(id), new EvictDynamicKey(pullToRefresh))
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> stringReply) throws Exception {
                        return stringReply.getData();
                    }
                })
                .map(new Function<String, List<String>>() {
                    @Override
                    public List<String> apply(String s) throws Exception {
                        BaseResult<List<String>> baseResult = ParseMeiZiTu.parsePicturePage(s);
                        return baseResult.getData();
                    }
                });
    }

    @Override
    public Observable<BaseResult<List<Mm99>>> list99Mm(String category, final int page, boolean cleanCache) {
        String url = buildUrl(category, page);
        DynamicKeyGroup dynamicKeyGroup = new DynamicKeyGroup(category, page);
        EvictDynamicKeyGroup evictDynamicKeyGroup = new EvictDynamicKeyGroup(cleanCache);
        return cacheProviders.cacheWithLimitTime(mm99ServiceApi.imageList(url), dynamicKeyGroup, evictDynamicKeyGroup)
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> stringReply) throws Exception {
                        return stringReply.getData();
                    }
                })
                .map(new Function<String, BaseResult<List<Mm99>>>() {
                    @Override
                    public BaseResult<List<Mm99>> apply(String s) throws Exception {
                        return Parse99Mm.parse99MmList(s, page);
                    }
                });
    }

    @Override
    public Observable<List<String>> mm99ImageList(int id, final String imageUrl, boolean pullToRefresh) {
        return cacheProviders.cacheWithNoLimitTime(mm99ServiceApi.imageLists("view", id), new DynamicKey(id), new EvictDynamicKey(pullToRefresh))
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> stringReply) throws Exception {
                        return stringReply.getData();
                    }
                })
                .map(new Function<String, List<String>>() {
                    @Override
                    public List<String> apply(String s) throws Exception {
                        String[] tags = s.split(",");
                        List<String> stringList = new ArrayList<>();
                        for (int i = 0; i < tags.length; i++) {
                            stringList.add(imageUrl.replace("small/", "").replace(".jpg", "/" + (i + 1) + "-" + tags[i]) + ".jpg");
                        }
                        return stringList;
                    }
                });
    }

    @Override
    public Observable<List<PigAv>> loadPigAvListByCategory(String category, boolean pullToRefresh) {
        DynamicKey dynamicKey = new DynamicKey(category);
        EvictDynamicKey evictDynamicKey = new EvictDynamicKey(pullToRefresh);
        if ("index".equals(category)) {
            return action(cacheProviders.cacheWithLimitTime(pigAvServiceApi.pigAvVideoList(addressHelper.getPigAvAddress()), dynamicKey, evictDynamicKey));
        } else {
            return action(cacheProviders.cacheWithLimitTime(pigAvServiceApi.pigAvVideoList(addressHelper.getPigAvAddress() + category + "av線上看"), dynamicKey, evictDynamicKey));
        }
    }

    @Override
    public Observable<List<PigAv>> loadMorePigAvListByCategory(String category, int page, boolean pullToRefresh) {
        DynamicKeyGroup dynamicKeyGroup = new DynamicKeyGroup(category, page);
        EvictDynamicKeyGroup evictDynamicKeyGroup = new EvictDynamicKeyGroup(pullToRefresh);
        String action = "td_ajax_block";
        PigAvFormRequest pigAvFormRequest = new PigAvFormRequest();
        pigAvFormRequest.setLimit("10");
        pigAvFormRequest.setSort("random_posts");
        pigAvFormRequest.setAjax_pagination("load_more");
        pigAvFormRequest.setTd_column_number(3);
        pigAvFormRequest.setTd_filter_default_txt("所有");
        pigAvFormRequest.setClassX("td_uid_7_5a719c1244c2f_rand");
        pigAvFormRequest.setTdc_css_class("td_uid_7_5a719c1244c2f_rand");
        pigAvFormRequest.setTdc_css_class_style("td_uid_7_5a719c1244c2f_rand_style");
        String tdAtts = gson.toJson(pigAvFormRequest);
        String tdBlockId = "td_uid_7_5a719c1244c2f";
        int tdColumnNumber = 3;
        String blockType = "td_block_16";
        return actionMore(cacheProviders.cacheWithLimitTime(pigAvServiceApi.moreVideoList(action, tdAtts, tdBlockId, tdColumnNumber, page, blockType, "", ""), dynamicKeyGroup, evictDynamicKeyGroup), pullToRefresh);
    }

    @Override
    public Observable<PigAvVideo> loadPigAvVideoUrl(String url, String pId, boolean pullToRefresh) {
        if (TextUtils.isEmpty(pId)) {
            pId = "aaa1";
            pullToRefresh = true;
        }
        DynamicKey dynamicKey = new DynamicKey(pId);
        return cacheProviders.cacheWithNoLimitTime(pigAvServiceApi.pigAvVideoUrl(url), dynamicKey, new EvictDynamicKey(pullToRefresh))
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> stringReply) throws Exception {
                        return stringReply.getData();
                    }
                })
                .map(new Function<String, PigAvVideo>() {
                    @Override
                    public PigAvVideo apply(String s) throws Exception {
                        return ParsePigAv.parserVideoUrl(s).getData();
                    }
                });
    }

    @Override
    public Observable<BaseResult<List<ProxyModel>>> loadXiCiDaiLiProxyData(final int page) {
        return proxyServiceApi.proxyXiciDaili(page)
                .map(new Function<String, BaseResult<List<ProxyModel>>>() {
                    @Override
                    public BaseResult<List<ProxyModel>> apply(String s) throws Exception {
                        return ParseProxy.parseXiCiDaiLi(s, page);
                    }
                });
    }

    private Observable<List<PigAv>> actionMore(Observable<Reply<String>> observable, final boolean pullToRefresh) {
        return observable
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> stringReply) throws Exception {
                        return stringReply.getData();
                    }
                })
                .map(new Function<String, List<PigAv>>() {
                    @Override
                    public List<PigAv> apply(String s) throws Exception {
                        PigAvLoadMoreResponse pigAvLoadMoreResponse = gson.fromJson(s, PigAvLoadMoreResponse.class);
                        BaseResult<List<PigAv>> baseResult = ParsePigAv.videoList(pigAvLoadMoreResponse.getTd_data());
                        return baseResult.getData();
                    }
                });
    }

    private Observable<List<PigAv>> action(Observable<Reply<String>> observable) {
        return observable
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> stringReply) throws Exception {
                        return stringReply.getData();
                    }
                })
                .map(new Function<String, List<PigAv>>() {
                    @Override
                    public List<PigAv> apply(String s) throws Exception {
                        BaseResult<List<PigAv>> baseResult = ParsePigAv.videoList(s);
                        return baseResult.getData();
                    }
                });
    }

    private Observable<BaseResult<List<MeiZiTu>>> action(Observable<String> stringObservable, String tag, final int page, final boolean pullToRefresh) {
        DynamicKeyGroup dynamicKeyGroup = new DynamicKeyGroup(tag, page);
        EvictDynamicKeyGroup evictDynamicKeyGroup = new EvictDynamicKeyGroup(pullToRefresh);
        return cacheProviders.meiZiTu(stringObservable, dynamicKeyGroup, evictDynamicKeyGroup)
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> stringReply) throws Exception {
                        return stringReply.getData();
                    }
                })
                .map(new Function<String, BaseResult<List<MeiZiTu>>>() {
                    @Override
                    public BaseResult<List<MeiZiTu>> apply(String s) throws Exception {
                        return ParseMeiZiTu.parseMeiZiTuList(s, page);
                    }
                });
    }

    private String buildUrl(String category, int page) {
        switch (category) {
            case "meitui":
                if (page == 1) {
                    return Api.APP_99_MM_DOMAIN + "meitui/";
                } else {
                    return Api.APP_99_MM_DOMAIN + "meitui/mm_1_" + page + ".html";
                }

            case "xinggan":
                if (page == 1) {
                    return Api.APP_99_MM_DOMAIN + "xinggan/";
                } else {
                    return Api.APP_99_MM_DOMAIN + "xinggan/mm_2_" + page + ".html";
                }

            case "qingchun":
                if (page == 1) {
                    return Api.APP_99_MM_DOMAIN + "qingchun/";
                } else {
                    return Api.APP_99_MM_DOMAIN + "qingchun/mm_3_" + page + ".html";
                }

            case "hot":
                if (page == 1) {
                    return Api.APP_99_MM_DOMAIN + "hot/";
                } else {
                    return Api.APP_99_MM_DOMAIN + "hot/mm_4_" + page + ".html";
                }

            default:
                return Api.APP_99_MM_DOMAIN;
        }
    }

    @Override
    public Observable<String> testPorn91VideoAddress() {
        return noLimit91PornServiceApi.porn91VideoIndexPhp(HeaderUtils.getIndexHeader(addressHelper))
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        if (!CheckResultUtils.check91PronVideoConnectIsSuccess(s)) {
                            throw new MessageException("访问成功，但无法获取内容");
                        }
                        return "恭喜，测试成功了";
                    }
                });
    }

    @Override
    public Observable<String> testPorn91ForumAddress() {
        return forum91PronServiceApi
                .porn91ForumIndex()
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        if (!CheckResultUtils.check91PornForumConnectIsSuccess(s)) {
                            throw new MessageException("很遗憾，测试失败了");
                        }
                        return "恭喜，测试成功了";
                    }
                });
    }

    @Override
    public Observable<String> testPigAvAddress(String url) {
        return pigAvServiceApi
                .pigAvVideoUrl(url)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        if (!CheckResultUtils.checkPigAvVideoConnectIsSuccess(s)) {
                            throw new MessageException("很遗憾，测试失败了");
                        }
                        return "恭喜，测试成功了";
                    }
                });
    }
}
