package com.speedata.scanpaidservice.ui.main;

import android.app.Activity;
import android.content.Context;

import com.speedata.scanpaidservice.mvp.BasePresenter;
import com.speedata.scanpaidservice.mvp.BaseView;
import com.speedata.scanservice.bean.alltel.AlltelDataBean;
import com.speedata.scanservice.bean.myorder.MyOrderBean;

import java.util.List;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MainContract {
    interface View extends BaseView {
        void showToast(String msg);
        void upDateList(List<MyOrderBean> myOrder);
        void openScanAct();
        void alltelBack(AlltelDataBean alltelDataBean);
    }

    interface Presenter extends BasePresenter<View> {
        void getMyOrder(Activity activity, String userName);

        void activateScan(Activity activity, String userName);

        void alltel(Activity activity, String userName);
    }
}
