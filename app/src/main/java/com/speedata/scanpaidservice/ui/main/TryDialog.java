package com.speedata.scanpaidservice.ui.main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.speedata.scanpaidservice.R;
import com.speedata.scanpaidservice.utils.SharedXmlUtil;
import com.speedata.scanservice.bean.member.DeviceMemberBean;
import com.speedata.scanservice.bean.member.GetMemberBackData;
import com.speedata.scanservice.interfaces.OnApplyBackListener;
import com.speedata.scanservice.methods.SpeedataMethods;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by 张明_ on 2018/2/5.
 * Email 741183142@qq.com
 */

public class TryDialog extends Dialog implements View.OnClickListener {
    private WebView mWebView;
    private Button mAgree;
    private Button mCancel;
    private Context mContext;

    public TryDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_try);
        initView();

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("http://218.247.237.138:8068/jeeplatform/html/user_agreement.html");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mAgree.setVisibility(View.VISIBLE);
                mCancel.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initView() {
        mWebView = findViewById(R.id.webView);
        mAgree = findViewById(R.id.agree);
        mAgree.setOnClickListener(this);
        mCancel = findViewById(R.id.cancel);
        mCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.agree:
                String userName = SharedXmlUtil
                        .getInstance(mContext, "scanPaid").read("USER_NAME", "");

                //申请试用
                SpeedataMethods.getInstance(mContext).apply(mContext, userName
                        , new OnApplyBackListener() {
                            @Override
                            public void onBack(GetMemberBackData backData) {
                                boolean success = backData.isSuccess();
                                if (!success) {
                                    Toast.makeText(mContext, backData.getErrorMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, backData.getMessage(), Toast.LENGTH_SHORT).show();
                                    DeviceMemberBean deviceMember = backData.getData().getDeviceMember();
                                    EventBus.getDefault().post(deviceMember);
                                }
                                dismiss();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                                dismiss();
                            }
                        });
                break;
            case R.id.cancel:
                dismiss();
                break;
            default:
                break;
        }
    }
}
