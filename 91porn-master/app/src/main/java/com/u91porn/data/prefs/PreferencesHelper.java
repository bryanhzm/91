package com.u91porn.data.prefs;

/**
 * @author flymegoc
 * @date 2018/2/12
 */

public interface PreferencesHelper {
    void setPorn91VideoAddress(String address);

    String getPorn91VideoAddress();

    void setPorn91ForumAddress(String address);

    String getPorn91ForumAddress();

    void setPigAvAddress(String address);

    String getPigAvAddress();

    void setPorn91VideoLoginUserName(String userName);

    String getPorn91VideoLoginUserName();

    void setPorn91VideoLoginUserPassWord(String passWord);

    String getPorn91VideoLoginUserPassword();

    void setPorn91VideoUserAutoLogin(boolean autoLogin);

    boolean isPorn91VideoUserAutoLogin();

    void setAppBarLayoutHeight(int height);

    int getAppBarLayoutHeight();

    void setFavoriteNeedRefresh(boolean needRefresh);

    boolean isFavoriteNeedRefresh();

    void setPlaybackEngine(int playbackEngine);

    int getPlaybackEngine();

    void setFirstInSearchPorn91Video(boolean firstInSearchPorn91Video);

    boolean isFirstInSearchPorn91Video();

    void setDownloadVideoNeedWifi(boolean downloadVideoNeedWifi);

    boolean isDownloadVideoNeedWifi();

    void setOpenHttpProxy(boolean openHttpProxy);

    boolean isOpenHttpProxy();

    void setOpenNightMode(boolean openNightMode);

    boolean isOpenNightMode();

    void setProxyIpAddress(String proxyIpAddress);

    String getProxyIpAddress();

    void setProxyPort(int port);

    int getProxyPort();

    void setNeverAskForWatchDownloadTip(boolean neverAskForWatchDownloadTip);

    boolean isNeverAskForWatchDownloadTip();

    void setIgnoreThisVersionUpdateTip(int versionCode);

    int getIgnoreThisVersionUpdateTip();

    void setForbiddenAutoReleaseMemory(boolean autoReleaseMemory);

    boolean isForbiddenAutoReleaseMemory();

    void setViewPorn91ForumContentShowTip(boolean contentShowTip);

    boolean isViewPorn91ForumContentShowTip();

    void setNoticeVersionCode(int noticeVersionCode);

    int getNoticeVersionCode();

    void setMainFirstTabShow(int firstTabShow);

    int getMainFirstTabShow();

    void setMainSecondTabShow(int secondTabShow);

    int getMainSecondTabShow();

    void setSettingScrollViewScrollPosition(int position);

    int getSettingScrollViewScrollPosition();

    void setOpenSkipPage(boolean openSkipPage);

    boolean isOpenSkipPage();

    void setCustomDownloadVideoDirPath(String customDirPath);

    String getCustomDownloadVideoDirPath();
}
