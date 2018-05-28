package com.speedata.scanpaidservice.ui.pay;

import android.app.Activity;

import com.speedata.scanpaidservice.mvp.BasePresenterImpl;
import com.speedata.scanservice.bean.backdata.BackData;
import com.speedata.scanservice.bean.member.DataBean;
import com.speedata.scanservice.bean.member.DeviceMemberBean;
import com.speedata.scanservice.bean.member.GetMemberBackData;
import com.speedata.scanservice.bean.member2.GetMember2BackData;
import com.speedata.scanservice.bean.member2.GetMember2DataBean;
import com.speedata.scanservice.bean.price.GetPriceBackData;
import com.speedata.scanservice.bean.price.PriceListBean;
import com.speedata.scanservice.interfaces.OnGetPriceBackListener;
import com.speedata.scanservice.interfaces.OnLogin2BackListener;
import com.speedata.scanservice.interfaces.OnLoginBackListener;
import com.speedata.scanservice.interfaces.OnPayBackListener;
import com.speedata.scanservice.methods.SpeedataMethods;

import java.util.List;

import static com.speedata.scanservice.methods.SpeedataMethods.PAY_ZHIFUBAO;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class PayPresenter extends BasePresenterImpl<PayContract.View> implements PayContract.Presenter {


    @Override
    public void createOrder(final Activity context, String priceId, final String user, int payType) {
        SpeedataMethods.getInstance(context).payForScan(context, priceId, user
                , new OnPayBackListener() {
                    @Override
                    public void onBack(BackData backData) {
                        String message = backData.getMessage();
                        if (backData.isSuccess()) {
                            getUserInfo(context, user);
                        } else {
                            mView.showToast("支付失败" + message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showToast(e.toString());
                    }
                }, payType);

    }

    /**
     * 再次获取会员信息
     *
     * @param context
     * @param user
     */
    private void getUserInfo(Activity context, String user) {
        SpeedataMethods.getInstance(context).login2(context, user, new OnLogin2BackListener() {
            @Override
            public void onBack(GetMember2BackData backData) {
                if (backData.isSuccess()) {
                    GetMember2DataBean data = backData.getData();
                    com.speedata.scanservice.bean.member2.DeviceMemberBean deviceMember = data.getDeviceMember();
                    int expireStatus = deviceMember.getExpireStatus();
                    String expireDate = deviceMember.getExpireDate();
                    mView.openAct(expireStatus, expireDate);
                } else {
                    String errorMessage = backData.getErrorMessage();
                    mView.showToast("登录失败" + errorMessage);
                }
            }

            @Override
            public void onError(Throwable e) {
                mView.showToast(e.toString());
            }
        });
    }

    @Override
    public void getPrice(final Activity context, String user) {
        SpeedataMethods.getInstance(context).getPrice(context, user,
                new OnGetPriceBackListener() {
                    @Override
                    public void onBack(GetPriceBackData backData) {
                        boolean success = backData.isSuccess();
                        if (success) {
                            List<PriceListBean> priceList =
                                    backData.getData().getPriceList();
                            mView.backList(priceList);
                        } else {
                            mView.showToast("获取失败 " + backData.getErrorMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showToast(e.toString());
                    }
                });
    }

}