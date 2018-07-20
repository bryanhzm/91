package com.u91porn.ui.basemain;

import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.u91porn.data.DataManager;
import com.u91porn.data.model.Category;
import com.u91porn.di.PerActivity;

import java.util.List;

import javax.inject.Inject;

/**
 * @author flymegoc
 * @date 2018/1/25
 */
@PerActivity
public class BaseMainPresenter extends MvpBasePresenter<BaseMainView> implements IBaseMain {
    protected DataManager dataManager;

    @Inject
    public BaseMainPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void loadAllCategoryData(int type) {
        final List<Category> categoryList = dataManager.loadAllCategoryDataByType(type);
        ifViewAttached(new ViewAction<BaseMainView>() {
            @Override
            public void run(@NonNull BaseMainView view) {
                view.onLoadAllCategoryData(categoryList);
            }
        });
    }

    @Override
    public void loadCategoryData(int type) {
        final List<Category> categoryList = dataManager.loadCategoryDataByType(type);
        ifViewAttached(new ViewAction<BaseMainView>() {
            @Override
            public void run(@NonNull BaseMainView view) {
                view.onLoadCategoryData(categoryList);
            }
        });
    }

    @Override
    public Category findCategoryById(Long id) {
        return dataManager.findCategoryById(id);
    }

    @Override
    public void updateCategoryData(List<Category> categoryList) {
        dataManager.updateCategoryData(categoryList);
    }
}
