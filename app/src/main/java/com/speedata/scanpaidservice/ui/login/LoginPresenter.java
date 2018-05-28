package com.speedata.scanpaidservice.ui.login;

import android.app.Activity;
import android.util.Log;

import com.speedata.scanpaidservice.mvp.BasePresenterImpl;
import com.speedata.scanpaidservice.utils.SharedXmlUtil;
import com.speedata.scanservice.bean.member2.GetMember2BackData;
import com.speedata.scanservice.bean.member2.GetMember2DataBean;
import com.speedata.scanservice.interfaces.OnLogin2BackListener;
import com.speedata.scanservice.methods.SpeedataMethods;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class LoginPresenter extends BasePresenterImpl<LoginContract.View> implements LoginContract.Presenter {

    @Override
    public void login(final Activity activity, String userName) {
        SpeedataMethods.getInstance(activity).login2(activity, userName
                , new OnLogin2BackListener() {
                    @Override
                    public void onBack(GetMember2BackData backData) {
                        Log.d("ZM", "login2 backData: " + backData.toString());
                        if (backData.isSuccess()) {
                            GetMember2DataBean dataBean = backData.getData();
                            if (dataBean != null) {
                                com.speedata.scanservice.bean.member2.DeviceMemberBean deviceMember = dataBean.getDeviceMember();
                                int expireStatus = deviceMember.getExpireStatus();
                                String expireDate = deviceMember.getExpireDate();
                                String backName = deviceMember.getUserName();
                                SharedXmlUtil
                                        .getInstance(activity, "scanPaid").write("BACK_NAME", backName);
                                mView.openAct(expireStatus, expireDate);
                            }
                        } else {
                            String errorMessage = backData.getErrorMessage();
                            if ("needExchangeBind".equals(errorMessage)) {
                                mView.exchangeBind(backData.getData());
                            } else if (errorMessage.contains("充值")) {
                                mView.openPayAct();
                            } else {
                                mView.showToast("登录失败" + errorMessage);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showToast(e.toString());
                    }
                });
    }
}
