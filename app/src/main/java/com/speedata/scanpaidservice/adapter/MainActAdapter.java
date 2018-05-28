package com.speedata.scanpaidservice.adapter;

import android.content.Context;

import com.speedata.scanpaidservice.R;
import com.speedata.scanservice.bean.myorder.MyOrderBean;

import java.util.List;

import xyz.reginer.baseadapter.BaseAdapterHelper;
import xyz.reginer.baseadapter.CommonRvAdapter;

/**
 * Created by 张明_ on 2018/1/22.
 * Email 741183142@qq.com
 */

public class MainActAdapter extends CommonRvAdapter<MyOrderBean> {
    public MainActAdapter(Context context, int layoutResId, List<MyOrderBean> data) {
        super(context, layoutResId, data);
    }

    @Override
    public void convert(BaseAdapterHelper helper, MyOrderBean item, int position) {
        helper.setText(R.id.tv_createDate, "充值日期：" + item.getCreateDate());
        helper.setText(R.id.tv_money, "充值金额：" + item.getMoney());
        if (item.getPayType() == 1) {
            helper.setText(R.id.tv_payType, "支付方式：微信");
        } else {
            helper.setText(R.id.tv_payType, "支付方式：支付宝");
        }
        helper.setText(R.id.tv_month, "月数：" + item.getMonth());
        helper.setText(R.id.tv_status, "支付状态：" + item.getStatus());
    }
}
