package com.speedata.scanpaidservice.ui.main;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.honeywell.license.ActivationManager;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.speedata.scanpaidservice.R;
import com.speedata.scanpaidservice.adapter.MainActAdapter;
import com.speedata.scanpaidservice.mvp.MVPBaseActivity;
import com.speedata.scanpaidservice.scandemo.ScanActivity;
import com.speedata.scanpaidservice.ui.about.AboutActivity;
import com.speedata.scanpaidservice.ui.pay.PayActivity;
import com.speedata.scanpaidservice.utils.SharedXmlUtil;
import com.speedata.scanservice.bean.alltel.AlltelDataBean;
import com.speedata.scanservice.bean.alltel.TelsBean;
import com.speedata.scanservice.bean.member.DeviceMemberBean;
import com.speedata.scanservice.bean.myorder.MyOrderBean;
import com.speedata.scanservice.methods.SpeedataMethods;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.reginer.baseadapter.CommonRvAdapter;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MainActivity extends MVPBaseActivity<MainContract.View, MainPresenter>
        implements MainContract.View, CommonRvAdapter.OnItemClickListener {
    private Toolbar mToolbar;
    private String userName;
    private int expireStatus;
    private CircleImageView mCivHead;
    private TextView mTvName;
    private TextView mTvOverTime;
    private RecyclerView mRvContent;
    private Button mBtnScan;
    private Button mBtnPay;
    private MainActAdapter mAdapter;
    private List<MyOrderBean> datas;
    private KProgressHUD kProgressHUD;
    private Button mBtnTry;
    private String expireDate;
    private TextView mTvStatus;
    private TextView mTvTelNum;

    @Override
    public void initData(Bundle bundle) {
        userName = SharedXmlUtil
                .getInstance(this, "scanPaid").read("USER_NAME", "");
        if (bundle != null) {
            expireDate = bundle.getString("expireDate");
            expireStatus = bundle.getInt("expireStatus");
        }
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(Bundle bundle, View view) {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("VIP会员");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.finish();
            }
        });

        mTvName = findViewById(R.id.tv_name);
        mTvOverTime = findViewById(R.id.tv_over_time);
        mBtnPay = findViewById(R.id.btn_pay);
        mBtnPay.setOnClickListener(this);
        mBtnScan = findViewById(R.id.btn_scan);
        mBtnScan.setOnClickListener(this);
        mRvContent = findViewById(R.id.rv_content);
        mBtnTry = findViewById(R.id.btn_try);
        mBtnTry.setOnClickListener(this);
        datas = new ArrayList<>();
        initRV();
        mTvStatus = findViewById(R.id.tv_status);
        mTvTelNum = findViewById(R.id.tv_telNum);
    }

    private void initRV() {
        mAdapter = new MainActAdapter(getApplicationContext(), R.layout.info_main, datas);
        mRvContent.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);//列表再底部开始展示，反转后由上面开始展示
        layoutManager.setReverseLayout(true);//列表翻转
        mRvContent.setLayoutManager(layoutManager);
        mRvContent.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }


    @Override
    public void doBusiness() {
        String read = SharedXmlUtil
                .getInstance(this, "scanPaid").read("BACK_NAME", "");
        mTvName.setText("机主用户名：" + read);
        mTvOverTime.setText("到期日期：" + expireDate);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mPresenter.alltel(mActivity, userName);
    }

    //申请试用结果更新界面显示
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMain(DeviceMemberBean deviceMember) {
        mTvOverTime.setText("到期日期：" + deviceMember.getExpireDate());
        expireStatus = deviceMember.getExpireStatus();
        setButtonVisibility();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setButtonVisibility();
        kProgressHUD = KProgressHUD.create(MainActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        mPresenter.getMyOrder(MainActivity.this, userName);


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (kProgressHUD != null) {
            kProgressHUD.dismiss();
        }
    }

    private void setButtonVisibility() {
        switch (expireStatus) {
            case 0:
            case 2:
                mBtnTry.setVisibility(View.GONE);
                mBtnScan.setVisibility(View.VISIBLE);
                mBtnPay.setVisibility(View.VISIBLE);
                break;
            case 1:
            case 3:
                mBtnTry.setVisibility(View.GONE);
                mBtnScan.setVisibility(View.GONE);
                mBtnPay.setVisibility(View.VISIBLE);
                break;

            case -1:
                mBtnTry.setVisibility(View.VISIBLE);
                mBtnScan.setVisibility(View.GONE);
                mBtnPay.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onWidgetClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pay:
                openAct(getApplicationContext(), PayActivity.class);
                break;
            case R.id.btn_scan:
                kProgressHUD = KProgressHUD.create(MainActivity.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setCancellable(false)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f)
                        .show();

                mPresenter.activateScan(MainActivity.this, userName);
                break;

            case R.id.btn_try:
                TryDialog tryDialog = new TryDialog(this);
                tryDialog.setTitle("申请试用");
                tryDialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                openAct(getApplicationContext(), AboutActivity.class);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder, View view, int position) {

    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(mActivity, "" + msg, Toast.LENGTH_SHORT).show();
        if (kProgressHUD != null) {
            kProgressHUD.dismiss();
        }
    }

    @Override
    public void upDateList(List<MyOrderBean> myOrder) {
        if (kProgressHUD != null) {
            kProgressHUD.dismiss();
        }
        datas.clear();
        datas.addAll(myOrder);
        handler.sendMessage(handler.obtainMessage());
    }

    @Override
    public void openScanAct() {
        if (kProgressHUD != null) {
            kProgressHUD.dismiss();
        }
        openAct(getApplicationContext(), ScanActivity.class);
    }

    @Override
    public void alltelBack(AlltelDataBean alltelDataBean) {
        List<TelsBean> tels = alltelDataBean.getTels();
        for (TelsBean tel : tels) {
            mTvTelNum.append(tel.getName() + "：" + tel.getPhoneNum() + "\n");
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mAdapter.notifyDataSetChanged();
        }
    };

}
