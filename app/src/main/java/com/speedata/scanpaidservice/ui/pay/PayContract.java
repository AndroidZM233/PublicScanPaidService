package com.speedata.scanpaidservice.ui.pay;

import android.app.Activity;

import com.speedata.scanpaidservice.mvp.BasePresenter;
import com.speedata.scanpaidservice.mvp.BaseView;
import com.speedata.scanservice.bean.price.PriceListBean;

import java.util.List;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class PayContract {
    interface View extends BaseView {
        void showToast(String msg);

        void backList(List<PriceListBean> priceList);

        void openAct(int expireStatus,String expireDate);
    }

    interface  Presenter extends BasePresenter<View> {
        void createOrder(Activity context,String priceId,String user,int payType);

        void getPrice(Activity context,String user);
    }
}
