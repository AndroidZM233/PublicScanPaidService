package com.speedata.scanpaidservice.ui.main;

import android.app.Activity;
import android.content.Context;

import com.speedata.scanpaidservice.mvp.BasePresenterImpl;
import com.speedata.scanpaidservice.scandemo.ScanActivity;
import com.speedata.scanservice.bean.alltel.AlltelBackData;
import com.speedata.scanservice.bean.member.GetMemberBackData;
import com.speedata.scanservice.bean.myorder.MyOrderBackData;
import com.speedata.scanservice.bean.myorder.MyOrderBean;
import com.speedata.scanservice.interfaces.OnAlltelBackListener;
import com.speedata.scanservice.interfaces.OnGetMyOrderBackListener;
import com.speedata.scanservice.interfaces.OnLoginBackListener;
import com.speedata.scanservice.methods.SpeedataMethods;

import java.util.List;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MainPresenter extends BasePresenterImpl<MainContract.View> implements MainContract.Presenter {

    @Override
    public void getMyOrder(Activity activity, String userName) {
        SpeedataMethods.getInstance(activity).getMyOrder(activity, userName,
                new OnGetMyOrderBackListener() {
                    @Override
                    public void onBack(MyOrderBackData backData) {
                        boolean success = backData.isSuccess();
                        if (success) {
                            List<MyOrderBean> myOrder = backData.getData().getMyOrder();
                            mView.upDateList(myOrder);
                        } else {
                            mView.showToast(backData.getErrorMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showToast(e.toString());
                    }
                });
    }


    /**
     * 激活扫描
     *
     * @param activity
     * @param userName
     */
    @Override
    public void activateScan(final Activity activity, String userName) {
        SpeedataMethods.getInstance(activity).activateScan(activity, userName, new OnLoginBackListener() {
            @Override
            public void onBack(GetMemberBackData backData) {
                boolean success = backData.isSuccess();
                if (success) {
                    mView.openScanAct();
                } else {
                    mView.showToast(backData.getErrorMessage());
                }
            }

            @Override
            public void onError(Throwable e) {
                mView.showToast(e.toString());
            }
        });
    }

    @Override
    public void alltel(Activity activity, String userName) {
        SpeedataMethods.getInstance(activity).alltel(activity, userName, new OnAlltelBackListener() {
            @Override
            public void onBack(AlltelBackData backData) {
                boolean success = backData.isSuccess();
                if (success) {
                    mView.alltelBack(backData.getData());
                } else {
                    mView.showToast(backData.getErrorMessage());
                }
            }

            @Override
            public void onError(Throwable e) {
                mView.showToast(e.toString());
            }
        });
    }
}
