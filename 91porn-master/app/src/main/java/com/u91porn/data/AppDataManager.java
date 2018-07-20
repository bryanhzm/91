package com.u91porn.data;

import com.u91porn.data.db.DbHelper;
import com.u91porn.data.model.BaseResult;
import com.u91porn.data.model.Category;
import com.u91porn.data.model.Forum91PronItem;
import com.u91porn.data.model.MeiZiTu;
import com.u91porn.data.model.Mm99;
import com.u91porn.data.model.Notice;
import com.u91porn.data.model.PigAv;
import com.u91porn.data.model.PigAvVideo;
import com.u91porn.data.model.PinnedHeaderEntity;
import com.u91porn.data.model.Porn91ForumContent;
import com.u91porn.data.model.ProxyModel;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.UpdateVersion;
import com.u91porn.data.model.User;
import com.u91porn.data.model.VideoComment;
import com.u91porn.data.model.VideoResult;
import com.u91porn.data.network.ApiHelper;
import com.u91porn.data.prefs.PreferencesHelper;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;

/**
 * @author flymegoc
 * @date 2017/11/22
 * @describe
 */

@Singleton
public class AppDataManager implements DataManager {

    private final DbHelper mDbHelper;
    private final PreferencesHelper mPreferencesHelper;
    private final ApiHelper mApiHelper;

    @Inject
    AppDataManager(DbHelper mDbHelper, PreferencesHelper mPreferencesHelper, ApiHelper mApiHelper) {
        this.mDbHelper = mDbHelper;
        this.mPreferencesHelper = mPreferencesHelper;
        this.mApiHelper = mApiHelper;
    }

    @Override
    public void initCategory(int type, String[] value, String[] name) {
        mDbHelper.initCategory(type, value, name);
    }

    @Override
    public void updateUnLimit91PornItem(UnLimit91PornItem unLimit91PornItem) {
        mDbHelper.updateUnLimit91PornItem(unLimit91PornItem);
    }

    @Override
    public List<UnLimit91PornItem> loadDownloadingData() {
        return mDbHelper.loadDownloadingData();
    }

    @Override
    public List<UnLimit91PornItem> loadFinishedData() {
        return mDbHelper.loadFinishedData();
    }

    @Override
    public List<UnLimit91PornItem> loadHistoryData(int page, int pageSize) {
        return mDbHelper.loadHistoryData(page, pageSize);
    }

    @Override
    public long saveUnLimit91PornItem(UnLimit91PornItem unLimit91PornItem) {
        return mDbHelper.saveUnLimit91PornItem(unLimit91PornItem);
    }

    @Override
    public long saveVideoResult(VideoResult videoResult) {
        return mDbHelper.saveVideoResult(videoResult);
    }

    @Override
    public UnLimit91PornItem findUnLimit91PornItemByViewKey(String viewKey) {
        return mDbHelper.findUnLimit91PornItemByViewKey(viewKey);
    }

    @Override
    public UnLimit91PornItem findUnLimit91PornItemByDownloadId(int downloadId) {
        return mDbHelper.findUnLimit91PornItemByDownloadId(downloadId);
    }

    @Override
    public List<UnLimit91PornItem> loadAllLimit91PornItems() {
        return mDbHelper.loadAllLimit91PornItems();
    }

    @Override
    public List<UnLimit91PornItem> findUnLimit91PornItemsByDownloadStatus(int status) {
        return mDbHelper.findUnLimit91PornItemsByDownloadStatus(status);
    }

    @Override
    public List<Category> loadAllCategoryDataByType(int type) {
        return mDbHelper.loadAllCategoryDataByType(type);
    }

    @Override
    public List<Category> loadCategoryDataByType(int type) {
        return mDbHelper.loadCategoryDataByType(type);
    }

    @Override
    public void updateCategoryData(List<Category> categoryList) {
        mDbHelper.updateCategoryData(categoryList);
    }

    @Override
    public Category findCategoryById(Long id) {
        return mDbHelper.findCategoryById(id);
    }

    @Override
    public Observable<List<UnLimit91PornItem>> loadPorn91VideoIndex(boolean cleanCache) {
        return mApiHelper.loadPorn91VideoIndex(cleanCache);
    }

    @Override
    public Observable<BaseResult<List<UnLimit91PornItem>>> loadPorn91VideoByCategory(String category, String viewType, int page, String m, boolean cleanCache, boolean isLoadMoreCleanCache) {
        return mApiHelper.loadPorn91VideoByCategory(category, viewType, page, m, cleanCache, isLoadMoreCleanCache);
    }

    @Override
    public Observable<BaseResult<List<UnLimit91PornItem>>> loadPorn91authorVideos(String uid, String type, int page, boolean cleanCache) {
        return mApiHelper.loadPorn91authorVideos(uid, type, page, cleanCache);
    }

    @Override
    public Observable<BaseResult<List<UnLimit91PornItem>>> loadPorn91VideoRecentUpdates(String next, int page, boolean cleanCache, boolean isLoadMoreCleanCache) {
        return mApiHelper.loadPorn91VideoRecentUpdates(next, page, cleanCache, isLoadMoreCleanCache);
    }

    @Override
    public Observable<VideoResult> loadPorn91VideoUrl(String viewKey) {
        return mApiHelper.loadPorn91VideoUrl(viewKey);
    }

    @Override
    public Observable<List<VideoComment>> loadPorn91VideoComments(String videoId, int page, String viewKey) {
        return mApiHelper.loadPorn91VideoComments(videoId, page, viewKey);
    }

    @Override
    public Observable<String> commentPorn91Video(String cpaintFunction, String comment, String uid, String vid, String viewKey, String responseType) {
        return mApiHelper.commentPorn91Video(cpaintFunction, comment, uid, vid, viewKey, responseType);
    }

    @Override
    public Observable<String> replyPorn91VideoComment(String comment, String username, String vid, String commentId, String viewKey) {
        return mApiHelper.replyPorn91VideoComment(comment, username, vid, commentId, viewKey);
    }

    @Override
    public Observable<BaseResult<List<UnLimit91PornItem>>> searchPorn91Videos(String viewType, int page, String searchType, String searchId, String sort) {
        return mApiHelper.searchPorn91Videos(viewType, page, searchType, searchId, sort);
    }

    @Override
    public Observable<String> favoritePorn91Video(String uId, String videoId, String ownnerId) {
        return mApiHelper.favoritePorn91Video(uId, videoId, ownnerId);
    }

    @Override
    public Observable<BaseResult<List<UnLimit91PornItem>>> loadPorn91MyFavoriteVideos(String userName, int page, boolean cleanCache) {
        return mApiHelper.loadPorn91MyFavoriteVideos(userName, page, cleanCache);
    }

    @Override
    public Observable<List<UnLimit91PornItem>> deletePorn91MyFavoriteVideo(String rvid) {
        return mApiHelper.deletePorn91MyFavoriteVideo(rvid);
    }

    @Override
    public Observable<User> userLoginPorn91Video(String username, String password, String captcha) {
        return mApiHelper.userLoginPorn91Video(username, password, captcha);
    }

    @Override
    public Observable<User> userRegisterPorn91Video(String username, String password1, String password2, String email, String captchaInput) {
        return mApiHelper.userRegisterPorn91Video(username, password1, password2, email, captchaInput);
    }

    @Override
    public Observable<List<PinnedHeaderEntity<Forum91PronItem>>> loadPorn91ForumIndex() {
        return mApiHelper.loadPorn91ForumIndex();
    }

    @Override
    public Observable<BaseResult<List<Forum91PronItem>>> loadPorn91ForumListData(String fid, int page) {
        return mApiHelper.loadPorn91ForumListData(fid, page);
    }

    @Override
    public Observable<Porn91ForumContent> loadPorn91ForumContent(Long tid, boolean isNightModel) {
        return mApiHelper.loadPorn91ForumContent(tid, isNightModel);
    }

    @Override
    public Observable<UpdateVersion> checkUpdate(String url) {
        return mApiHelper.checkUpdate(url);
    }

    @Override
    public Observable<Notice> checkNewNotice(String url) {
        return mApiHelper.checkNewNotice(url);
    }

    @Override
    public Observable<BaseResult<List<MeiZiTu>>> listMeiZiTu(String tag, int page, boolean pullToRefresh) {
        return mApiHelper.listMeiZiTu(tag, page, pullToRefresh);
    }

    @Override
    public Observable<List<String>> meiZiTuImageList(int id, boolean pullToRefresh) {
        return mApiHelper.meiZiTuImageList(id, pullToRefresh);
    }

    @Override
    public Observable<BaseResult<List<Mm99>>> list99Mm(String category, int page, boolean cleanCache) {
        return mApiHelper.list99Mm(category, page, cleanCache);
    }

    @Override
    public Observable<List<String>> mm99ImageList(int id, String imageUrl, boolean pullToRefresh) {
        return mApiHelper.mm99ImageList(id, imageUrl, pullToRefresh);
    }

    @Override
    public Observable<List<PigAv>> loadPigAvListByCategory(String category, boolean pullToRefresh) {
        return mApiHelper.loadPigAvListByCategory(category, pullToRefresh);
    }

    @Override
    public Observable<List<PigAv>> loadMorePigAvListByCategory(String category, int page, boolean pullToRefresh) {
        return mApiHelper.loadMorePigAvListByCategory(category, page, pullToRefresh);
    }

    @Override
    public Observable<PigAvVideo> loadPigAvVideoUrl(String url, String pId, boolean pullToRefresh) {
        return mApiHelper.loadPigAvVideoUrl(url, pId, pullToRefresh);
    }

    @Override
    public Observable<BaseResult<List<ProxyModel>>> loadXiCiDaiLiProxyData(int page) {
        return mApiHelper.loadXiCiDaiLiProxyData(page);
    }

    @Override
    public Observable<String> testPorn91VideoAddress() {
        return mApiHelper.testPorn91VideoAddress();
    }

    @Override
    public Observable<String> testPorn91ForumAddress() {
        return mApiHelper.testPorn91ForumAddress();
    }

    @Override
    public Observable<String> testPigAvAddress(String url) {
        return mApiHelper.testPigAvAddress(url);
    }

    @Override
    public void setPorn91VideoAddress(String address) {
        mPreferencesHelper.setPorn91VideoAddress(address);
    }

    @Override
    public String getPorn91VideoAddress() {
        return mPreferencesHelper.getPorn91VideoAddress();
    }

    @Override
    public void setPorn91ForumAddress(String address) {
        mPreferencesHelper.setPorn91ForumAddress(address);
    }

    @Override
    public String getPorn91ForumAddress() {
        return mPreferencesHelper.getPorn91ForumAddress();
    }

    @Override
    public void setPigAvAddress(String address) {
        mPreferencesHelper.setPigAvAddress(address);
    }

    @Override
    public String getPigAvAddress() {
        return mPreferencesHelper.getPigAvAddress();
    }

    @Override
    public void setPorn91VideoLoginUserName(String userName) {
        mPreferencesHelper.setPorn91VideoLoginUserName(userName);
    }

    @Override
    public String getPorn91VideoLoginUserName() {
        return mPreferencesHelper.getPorn91VideoLoginUserName();
    }

    @Override
    public void setPorn91VideoLoginUserPassWord(String passWord) {
        mPreferencesHelper.setPorn91VideoLoginUserPassWord(passWord);
    }

    @Override
    public String getPorn91VideoLoginUserPassword() {
        return mPreferencesHelper.getPorn91VideoLoginUserPassword();
    }

    @Override
    public void setPorn91VideoUserAutoLogin(boolean autoLogin) {
        mPreferencesHelper.setPorn91VideoUserAutoLogin(autoLogin);
    }

    @Override
    public boolean isPorn91VideoUserAutoLogin() {
        return mPreferencesHelper.isPorn91VideoUserAutoLogin();
    }

    @Override
    public void setAppBarLayoutHeight(int height) {
        mPreferencesHelper.setAppBarLayoutHeight(height);
    }

    @Override
    public int getAppBarLayoutHeight() {
        return mPreferencesHelper.getAppBarLayoutHeight();
    }

    @Override
    public void setFavoriteNeedRefresh(boolean needRefresh) {
        mPreferencesHelper.setFavoriteNeedRefresh(needRefresh);
    }

    @Override
    public boolean isFavoriteNeedRefresh() {
        return mPreferencesHelper.isFavoriteNeedRefresh();
    }

    @Override
    public void setPlaybackEngine(int playbackEngine) {
        mPreferencesHelper.setPlaybackEngine(playbackEngine);
    }

    @Override
    public int getPlaybackEngine() {
        return mPreferencesHelper.getPlaybackEngine();
    }

    @Override
    public void setFirstInSearchPorn91Video(boolean firstInSearchPorn91Video) {
        mPreferencesHelper.setFirstInSearchPorn91Video(firstInSearchPorn91Video);
    }

    @Override
    public boolean isFirstInSearchPorn91Video() {
        return mPreferencesHelper.isFirstInSearchPorn91Video();
    }

    @Override
    public void setDownloadVideoNeedWifi(boolean downloadVideoNeedWifi) {
        mPreferencesHelper.setDownloadVideoNeedWifi(downloadVideoNeedWifi);
    }

    @Override
    public boolean isDownloadVideoNeedWifi() {
        return mPreferencesHelper.isDownloadVideoNeedWifi();
    }

    @Override
    public void setOpenHttpProxy(boolean openHttpProxy) {
        mPreferencesHelper.setOpenHttpProxy(openHttpProxy);
    }

    @Override
    public boolean isOpenHttpProxy() {
        return mPreferencesHelper.isOpenHttpProxy();
    }

    @Override
    public void setOpenNightMode(boolean openNightMode) {
        mPreferencesHelper.setOpenNightMode(openNightMode);
    }

    @Override
    public boolean isOpenNightMode() {
        return mPreferencesHelper.isOpenNightMode();
    }

    @Override
    public void setProxyIpAddress(String proxyIpAddress) {
        mPreferencesHelper.setProxyIpAddress(proxyIpAddress);
    }

    @Override
    public String getProxyIpAddress() {
        return mPreferencesHelper.getProxyIpAddress();
    }

    @Override
    public void setProxyPort(int port) {
        mPreferencesHelper.setProxyPort(port);
    }

    @Override
    public int getProxyPort() {
        return mPreferencesHelper.getProxyPort();
    }

    @Override
    public void setNeverAskForWatchDownloadTip(boolean neverAskForWatchDownloadTip) {
        mPreferencesHelper.setNeverAskForWatchDownloadTip(neverAskForWatchDownloadTip);
    }

    @Override
    public boolean isNeverAskForWatchDownloadTip() {
        return mPreferencesHelper.isNeverAskForWatchDownloadTip();
    }

    @Override
    public void setIgnoreThisVersionUpdateTip(int versionCode) {
        mPreferencesHelper.setIgnoreThisVersionUpdateTip(versionCode);
    }

    @Override
    public int getIgnoreThisVersionUpdateTip() {
        return mPreferencesHelper.getIgnoreThisVersionUpdateTip();
    }

    @Override
    public void setForbiddenAutoReleaseMemory(boolean autoReleaseMemory) {
        mPreferencesHelper.setForbiddenAutoReleaseMemory(autoReleaseMemory);
    }

    @Override
    public boolean isForbiddenAutoReleaseMemory() {
        return mPreferencesHelper.isForbiddenAutoReleaseMemory();
    }

    @Override
    public void setViewPorn91ForumContentShowTip(boolean contentShowTip) {
        mPreferencesHelper.setViewPorn91ForumContentShowTip(contentShowTip);
    }

    @Override
    public boolean isViewPorn91ForumContentShowTip() {
        return mPreferencesHelper.isViewPorn91ForumContentShowTip();
    }

    @Override
    public void setNoticeVersionCode(int noticeVersionCode) {
        mPreferencesHelper.setNoticeVersionCode(noticeVersionCode);
    }

    @Override
    public int getNoticeVersionCode() {
        return mPreferencesHelper.getNoticeVersionCode();
    }

    @Override
    public void setMainFirstTabShow(int firstTabShow) {
        mPreferencesHelper.setMainFirstTabShow(firstTabShow);
    }

    @Override
    public int getMainFirstTabShow() {
        return mPreferencesHelper.getMainFirstTabShow();
    }

    @Override
    public void setMainSecondTabShow(int secondTabShow) {
        mPreferencesHelper.setMainSecondTabShow(secondTabShow);
    }

    @Override
    public int getMainSecondTabShow() {
        return mPreferencesHelper.getMainSecondTabShow();
    }

    @Override
    public void setSettingScrollViewScrollPosition(int position) {
        mPreferencesHelper.setSettingScrollViewScrollPosition(position);
    }

    @Override
    public int getSettingScrollViewScrollPosition() {
        return mPreferencesHelper.getSettingScrollViewScrollPosition();
    }

    @Override
    public void setOpenSkipPage(boolean openSkipPage) {
        mPreferencesHelper.setOpenSkipPage(openSkipPage);
    }

    @Override
    public boolean isOpenSkipPage() {
        return mPreferencesHelper.isOpenSkipPage();
    }

    @Override
    public void setCustomDownloadVideoDirPath(String customDirPath) {
        mPreferencesHelper.setCustomDownloadVideoDirPath(customDirPath);
    }

    @Override
    public String getCustomDownloadVideoDirPath() {
        return mPreferencesHelper.getCustomDownloadVideoDirPath();
    }
}
