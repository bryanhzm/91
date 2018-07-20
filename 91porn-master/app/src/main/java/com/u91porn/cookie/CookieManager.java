package com.u91porn.cookie;

/**
 * @author flymegoc
 * @date 2018/3/5
 */

public interface CookieManager {
    /**
     * 重置观看次数
     *
     * @param forceReset 强制重置
     */
    void resetPorn91VideoWatchTiem(boolean forceReset);

    void cleanAllCookies();
}
