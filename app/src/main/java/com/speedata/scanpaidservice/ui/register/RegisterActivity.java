package com.speedata.scanpaidservice.ui.register;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.speedata.scanpaidservice.R;
import com.speedata.scanpaidservice.mvp.MVPBaseActivity;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class RegisterActivity extends MVPBaseActivity<RegisterContract.View, RegisterPresenter> implements RegisterContract.View {

    private Toolbar mToolbar;
    private EditText mEtUsertel;
    private EditText mEtCode;
    private Button mBtnSend;
    private EditText mEtPassword;
    private Button mBtnRegister;

    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_register;
    }

    @Override
    public void initView(Bundle bundle, View view) {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("注册");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterActivity.this.finish();
            }
        });

        mEtUsertel = findViewById(R.id.et_usertel);
        mEtCode = findViewById(R.id.et_code);
        mBtnSend = findViewById(R.id.btn_send);
        mBtnSend.setOnClickListener(this);
        mEtPassword = findViewById(R.id.et_password);
        mBtnRegister = findViewById(R.id.btn_register);
        mBtnRegister.setOnClickListener(this);
    }

    @Override
    public void doBusiness() {

    }

    @Override
    public void onWidgetClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                break;
            case R.id.btn_register:
                break;
            default:
                break;
        }
    }

}
