package com.u91porn.data.db;

import com.u91porn.data.model.Category;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.VideoResult;

import java.util.List;

/**
 * @author flymegoc
 * @date 2018/3/4
 */

public interface DbHelper {

    void initCategory(int type, String[] value, String[] name);

    void updateUnLimit91PornItem(UnLimit91PornItem unLimit91PornItem);

    List<UnLimit91PornItem> loadDownloadingData();

    List<UnLimit91PornItem> loadFinishedData();

    List<UnLimit91PornItem> loadHistoryData(int page, int pageSize);

    long saveUnLimit91PornItem(UnLimit91PornItem unLimit91PornItem);

    long saveVideoResult(VideoResult videoResult);

    UnLimit91PornItem findUnLimit91PornItemByViewKey(String viewKey);

    UnLimit91PornItem findUnLimit91PornItemByDownloadId(int downloadId);

    List<UnLimit91PornItem> loadAllLimit91PornItems();

    List<UnLimit91PornItem> findUnLimit91PornItemsByDownloadStatus(int status);

    List<Category> loadAllCategoryDataByType(int type);

    List<Category> loadCategoryDataByType(int type);

    void updateCategoryData(List<Category> categoryList);

    Category findCategoryById(Long id);
}
