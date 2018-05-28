package com.speedata.scanpaidservice.ui.about;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.speedata.scanpaidservice.R;
import com.speedata.scanpaidservice.mvp.MVPBaseActivity;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class AboutActivity extends MVPBaseActivity<AboutContract.View, AboutPresenter> implements AboutContract.View {

    private TextView mTvVersionCode;
    private Toolbar mToolbar;

    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_about;
    }

    @Override
    public void initView(Bundle bundle, View view) {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutActivity.this.finish();
            }
        });

        mTvVersionCode = findViewById(R.id.tv_version_code);
    }

    @Override
    public void doBusiness() {

    }

    @Override
    public void onWidgetClick(View view) {

    }

}
