package com.speedata.scanpaidservice.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.speedata.scanpaidservice.R;
import com.speedata.scanpaidservice.utils.DateUtils;
import com.speedata.scanservice.bean.price.PriceListBean;

import java.math.BigDecimal;
import java.util.List;

import xyz.reginer.baseadapter.BaseAdapterHelper;
import xyz.reginer.baseadapter.CommonRvAdapter;

/**
 * Created by 张明_ on 2018/1/17.
 * Email 741183142@qq.com
 */

public class RVAdapter extends CommonRvAdapter<PriceListBean> {
    public RVAdapter(Context context, int layoutResId, List<PriceListBean> data) {
        super(context, layoutResId, data);
    }

    @Override
    public void convert(BaseAdapterHelper helper, PriceListBean item
            , int position) {

        BigDecimal nowPrice;
        String actBeginTime = item.getActBeginTime();
        Long beginLong = DateUtils.convertTimeToLong(DateUtils.FORMAT_YMDHMS, actBeginTime);
        String actEndTime = item.getActEndTime();
        Long endLong = DateUtils.convertTimeToLong(DateUtils.FORMAT_YMDHMS, actEndTime);
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis >= beginLong && currentTimeMillis <= endLong) {
            nowPrice = item.getActPrice();
        } else {
            nowPrice = item.getSellPrice();
        }
        StringBuilder stringBuilder = new StringBuilder();
        String months = item.getMonths() + "个月会员";
        stringBuilder.append(months);
        stringBuilder.append("原价" + item.getPrice());
        stringBuilder.append("现在只售");

        helper.setText(R.id.title, String.valueOf(stringBuilder));
        helper.setText(R.id.title_price, nowPrice + "");
        helper.setText(R.id.descs, item.getDescs());

    }

}
