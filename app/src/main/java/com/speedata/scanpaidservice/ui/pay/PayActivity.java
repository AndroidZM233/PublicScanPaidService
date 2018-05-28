package com.speedata.scanpaidservice.ui.pay;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.speedata.scanpaidservice.R;
import com.speedata.scanpaidservice.adapter.RVAdapter;
import com.speedata.scanpaidservice.mvp.MVPBaseActivity;
import com.speedata.scanpaidservice.ui.main.MainActivity;
import com.speedata.scanpaidservice.utils.SharedXmlUtil;
import com.speedata.scanservice.bean.price.PriceListBean;

import java.util.ArrayList;
import java.util.List;

import xyz.reginer.baseadapter.CommonRvAdapter;

import static com.speedata.scanservice.methods.SpeedataMethods.PAY_WEIXIN;
import static com.speedata.scanservice.methods.SpeedataMethods.PAY_ZHIFUBAO;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class PayActivity extends MVPBaseActivity<PayContract.View, PayPresenter>
        implements PayContract.View, CommonRvAdapter.OnItemClickListener {
    private Toolbar mToolbar;
    private RecyclerView rv_content;
    private List<PriceListBean> datas;
    private RVAdapter mAdapter;
    private String user;
    private KProgressHUD kProgressHUD;
    private static final String[] list = {"微信支付", "支付宝支付"};
    private Spinner spinner;

    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_pay;
    }

    @Override
    public void initView(Bundle bundle, View view) {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("充值界面");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayActivity.this.finish();
            }
        });
        rv_content = findViewById(R.id.rv_content);
        datas = new ArrayList<>();
        initRV();

        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        kProgressHUD = KProgressHUD.create(PayActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        mPresenter.getPrice(PayActivity.this, user);
    }

    private void initRV() {
        mAdapter = new RVAdapter(getApplicationContext(), R.layout.info_show, datas);
        rv_content.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.HORIZONTAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);//列表再底部开始展示，反转后由上面开始展示
        layoutManager.setReverseLayout(true);//列表翻转
        rv_content.setLayoutManager(layoutManager);
        rv_content.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void doBusiness() {
        user = SharedXmlUtil.getInstance(this, "scanPaid")
                .read("USER_NAME", "test");
    }

    @Override
    public void onWidgetClick(View view) {

    }


    @Override
    public void showToast(String msg) {
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
        if (kProgressHUD != null) {
            kProgressHUD.dismiss();
            kProgressHUD = null;
        }
    }

    @Override
    public void backList(List<PriceListBean> priceList) {
        if (kProgressHUD != null) {
            kProgressHUD.dismiss();
            kProgressHUD = null;
        }
        datas.clear();
        datas.addAll(priceList);
        handler.sendMessage(handler.obtainMessage());
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder, View view, int position) {
        //请求订单
        String priceId = datas.get(position).getPriceId();
        kProgressHUD = KProgressHUD.create(PayActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        int payType = (spinner.getSelectedItemPosition() == 0 ? PAY_WEIXIN : PAY_ZHIFUBAO);
        mPresenter.createOrder(PayActivity.this, priceId, user,payType);
    }


    @SuppressLint("NewApi")
    @Override
    public void openAct(int expireStatus, String expireDate) {
        if (kProgressHUD != null) {
            kProgressHUD.dismiss();
        }
        Toast.makeText(mActivity, "支付成功", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("expireDate", expireDate);
        bundle.putInt("expireStatus", expireStatus);
        intent.putExtras(bundle);
        startActivity(intent);
        PayActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
