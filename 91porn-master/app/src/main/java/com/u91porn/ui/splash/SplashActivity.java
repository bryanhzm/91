package com.u91porn.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.u91porn.R;
import com.u91porn.data.DataManager;
import com.u91porn.data.model.User;
import com.u91porn.ui.MvpActivity;
import com.u91porn.ui.main.MainActivity;
import com.u91porn.ui.user.UserPresenter;
import com.u91porn.utils.AddressHelper;
import com.u91porn.utils.UserHelper;

import javax.inject.Inject;

/**
 * @author flymegoc
 */
public class SplashActivity extends MvpActivity<SplashView, SplashPresenter> implements SplashView {

    private static final String TAG = SplashActivity.class.getSimpleName();

    @Inject
    protected AddressHelper addressHelper;

    @Inject
    protected UserPresenter userPresenter;

    @Inject
    protected SplashPresenter splashPresenter;

    @Inject
    protected DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //防止重复开启程序造成多次登录
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            //结束你的activity
            Logger.t(TAG).d("重复打开了....");
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return;
        }

        if (UserHelper.isUserInfoComplete(user)) {
            startMain();
        }
        setContentView(R.layout.activity_splash);
        String username = dataManager.getPorn91VideoLoginUserName();
        String password = dataManager.getPorn91VideoLoginUserPassword();

        boolean isAutoLogin = dataManager.isPorn91VideoUserAutoLogin();

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && isAutoLogin && !TextUtils.isEmpty(addressHelper.getVideo91PornAddress())) {
            String captcha = UserHelper.randomCaptcha();
            login(username, password, captcha);
        } else {
            startMain();
        }
    }

    private void login(String username, String password, String captcha) {
        presenter.login(username, password, captcha);
    }

    private void startMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @NonNull
    @Override
    public SplashPresenter createPresenter() {
        getActivityComponent().inject(this);
        return splashPresenter;
    }

    @Override
    public void loginSuccess(User user) {
        user.copyProperties(this.user);
        startMain();
    }

    @Override
    public void loginError(String message) {
        startMain();
    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void showLoading(boolean pullToRefresh) {

    }

    @Override
    public void showContent() {

    }

    @Override
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
    }
}
