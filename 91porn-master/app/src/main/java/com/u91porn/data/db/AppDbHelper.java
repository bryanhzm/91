package com.u91porn.data.db;

import com.bugsnag.android.Bugsnag;
import com.bugsnag.android.Severity;
import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.u91porn.BuildConfig;
import com.u91porn.data.db.dao.CategoryDao;
import com.u91porn.data.db.dao.DaoMaster;
import com.u91porn.data.db.dao.DaoSession;
import com.u91porn.data.db.dao.UnLimit91PornItemDao;
import com.u91porn.data.model.Category;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.VideoResult;

import org.greenrobot.greendao.database.Database;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author flymegoc
 * @date 2018/3/4
 */

@Singleton
public class AppDbHelper implements DbHelper {
    private final DaoSession mDaoSession;

    @Inject
    AppDbHelper(MySQLiteOpenHelper helper) {
        //如果你想查看日志信息，请将DEBUG设置为true
        MigrationHelper.DEBUG = BuildConfig.DEBUG;
        Database db = helper.getWritableDb();
        this.mDaoSession = new DaoMaster(db).newSession();
        initCategory(Category.TYPE_91PORN, Category.CATEGORY_DEFAULT_91PORN_VALUE, Category.CATEGORY_DEFAULT_91PORN_NAME);
        initCategory(Category.TYPE_91PORN_FORUM, Category.CATEGORY_DEFAULT_91PORN_FORUM_VALUE, Category.CATEGORY_DEFAULT_91PORN_FORUM_NAME);
        initCategory(Category.TYPE_MEI_ZI_TU, Category.CATEGORY_DEFAULT_MEI_ZI_TU_VALUE, Category.CATEGORY_DEFAULT_MEI_ZI_TU_NAME);
        initCategory(Category.TYPE_PIG_AV, Category.CATEGORY_DEFAULT_PIG_AV_VALUE, Category.CATEGORY_DEFAULT_PIG_AV_NAME);
        initCategory(Category.TYPE_99_MM, Category.CATEGORY_DEFAULT_99_MM_VALUE, Category.CATEGORY_DEFAULT_99_MM_NAME);
    }

    @Override
    public void initCategory(int type, String[] value, String[] name) {
        int length = value.length;
        List<Category> categoryList = mDaoSession.getCategoryDao().queryBuilder().where(CategoryDao.Properties.CategoryType.eq(type)).build().list();
        if (categoryList.size() == length) {
            return;
        }
        for (int i = 0; i < length; i++) {
            Category category = new Category();
            category.setCategoryName(name[i]);
            category.setCategoryValue(value[i]);
            category.setCategoryType(type);
            category.setIsShow(true);
            category.setSortId(i);
            categoryList.add(category);
        }
        mDaoSession.getCategoryDao().insertOrReplaceInTx(categoryList);
    }

    @Override
    public void updateUnLimit91PornItem(UnLimit91PornItem unLimit91PornItem) {
        mDaoSession.getUnLimit91PornItemDao().update(unLimit91PornItem);
    }

    @Override
    public List<UnLimit91PornItem> loadDownloadingData() {
        return mDaoSession.getUnLimit91PornItemDao().queryBuilder().where(UnLimit91PornItemDao.Properties.Status.notEq(FileDownloadStatus.completed), UnLimit91PornItemDao.Properties.DownloadId.notEq(0)).orderDesc(UnLimit91PornItemDao.Properties.AddDownloadDate).build().list();

    }

    @Override
    public List<UnLimit91PornItem> loadFinishedData() {
        return mDaoSession.getUnLimit91PornItemDao().queryBuilder().where(UnLimit91PornItemDao.Properties.Status.eq(FileDownloadStatus.completed), UnLimit91PornItemDao.Properties.DownloadId.notEq(0)).orderDesc(UnLimit91PornItemDao.Properties.FinishedDownloadDate).build().list();
    }

    @Override
    public List<UnLimit91PornItem> loadHistoryData(int page, int pageSize) {
        return mDaoSession.getUnLimit91PornItemDao().queryBuilder().where(UnLimit91PornItemDao.Properties.ViewHistoryDate.isNotNull()).orderDesc(UnLimit91PornItemDao.Properties.ViewHistoryDate).offset((page - 1) * pageSize).limit(pageSize).build().list();
    }

    @Override
    public long saveUnLimit91PornItem(UnLimit91PornItem unLimit91PornItem) {
        return mDaoSession.getUnLimit91PornItemDao().insertOrReplace(unLimit91PornItem);
    }

    @Override
    public long saveVideoResult(VideoResult videoResult) {
        return mDaoSession.getVideoResultDao().insertOrReplace(videoResult);
    }

    @Override
    public UnLimit91PornItem findUnLimit91PornItemByViewKey(String viewKey) {
        UnLimit91PornItemDao unLimit91PornItemDao = mDaoSession.getUnLimit91PornItemDao();
        try {
            return unLimit91PornItemDao.queryBuilder().where(UnLimit91PornItemDao.Properties.ViewKey.eq(viewKey)).build().unique();
        } catch (Exception e) {
            //暂时先都删除了，之前没有设置唯一约束
            List<UnLimit91PornItem> tmp = unLimit91PornItemDao.queryBuilder().where(UnLimit91PornItemDao.Properties.ViewKey.eq(viewKey)).build().list();
            for (UnLimit91PornItem unLimit91PornItem : tmp) {
                unLimit91PornItemDao.delete(unLimit91PornItem);
            }
            if (!BuildConfig.DEBUG) {
                Bugsnag.notify(new Throwable("findUnLimit91PornItemByViewKey DaoException", e), Severity.WARNING);
            }
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public UnLimit91PornItem findUnLimit91PornItemByDownloadId(int downloadId) {
        try {
            return mDaoSession.getUnLimit91PornItemDao().queryBuilder().where(UnLimit91PornItemDao.Properties.DownloadId.eq(downloadId)).build().unique();
        } catch (Exception e) {
            //暂时先不处理这问题了，理论上一个不会发生，因为时根据url生成
            if (!BuildConfig.DEBUG) {
                Bugsnag.notify(new Throwable("findUnLimit91PornItemByDownloadId DaoException", e), Severity.WARNING);
            }
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<UnLimit91PornItem> loadAllLimit91PornItems() {
        return mDaoSession.getUnLimit91PornItemDao().loadAll();
    }

    @Override
    public List<UnLimit91PornItem> findUnLimit91PornItemsByDownloadStatus(int status) {
        return mDaoSession.getUnLimit91PornItemDao().queryBuilder().where(UnLimit91PornItemDao.Properties.Status.eq(status)).build().list();
    }

    @Override
    public List<Category> loadAllCategoryDataByType(int type) {
        CategoryDao categoryDao = mDaoSession.getCategoryDao();
        categoryDao.detachAll();
        return categoryDao.queryBuilder().where(CategoryDao.Properties.CategoryType.eq(type)).orderAsc(CategoryDao.Properties.SortId).build().list();
    }

    @Override
    public List<Category> loadCategoryDataByType(int type) {
        CategoryDao categoryDao = mDaoSession.getCategoryDao();
        categoryDao.detachAll();
        return categoryDao.queryBuilder().where(CategoryDao.Properties.CategoryType.eq(type), CategoryDao.Properties.IsShow.eq(true)).orderAsc(CategoryDao.Properties.SortId).build().list();
    }

    @Override
    public void updateCategoryData(List<Category> categoryList) {
        mDaoSession.getCategoryDao().updateInTx(categoryList);
    }

    @Override
    public Category findCategoryById(Long id) {
        CategoryDao categoryDao = mDaoSession.getCategoryDao();
        categoryDao.detachAll();
        return categoryDao.load(id);
    }
}
