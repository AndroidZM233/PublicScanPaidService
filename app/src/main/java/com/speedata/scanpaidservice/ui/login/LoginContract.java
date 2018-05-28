package com.speedata.scanpaidservice.ui.login;

import android.app.Activity;

import com.speedata.scanpaidservice.mvp.BasePresenter;
import com.speedata.scanpaidservice.mvp.BaseView;
import com.speedata.scanservice.bean.member2.GetMember2DataBean;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class LoginContract {
    interface View extends BaseView {
        void showToast(String msg);
        void openAct(int expireStatus,String expireDate);
        void exchangeBind(GetMember2DataBean dataBean);//设备换绑
        void openPayAct();
    }

    interface Presenter extends BasePresenter<View> {
        void login(Activity activity, String userName);
    }
}
