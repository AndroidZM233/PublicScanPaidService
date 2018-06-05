package com.speedata.scanpaidservice.ui.login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.speedata.scanpaidservice.R;
import com.speedata.scanpaidservice.ui.pay.PayActivity;
import com.speedata.scanpaidservice.utils.SharedXmlUtil;
import com.speedata.scanservice.bean.addDevice.AddDeviceBackData;
import com.speedata.scanservice.bean.backdata.BackData;
import com.speedata.scanservice.bean.exchangeBind.SendVerCodeBackData;
import com.speedata.scanservice.bean.member2.DevicelistBean;
import com.speedata.scanservice.bean.member2.GetMember2DataBean;
import com.speedata.scanservice.interfaces.OnAddDeviceBackListener;
import com.speedata.scanservice.interfaces.OnBackListener;
import com.speedata.scanservice.interfaces.OnSendVerCodeBackListener;
import com.speedata.scanservice.methods.SpeedataMethods;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by 张明_ on 2018/3/3.
 * Email 741183142@qq.com
 */

public class ExchangeBindDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private EditText mEtReason;
    private Button mAgree;
    private Button mAdd;
    private GetMember2DataBean dataBean;
    private RadioGroup mRadio;
    private List<DevicelistBean> devicelist;
    private String imei;
    private String uuid;
    private EditText mEtOwerTel;
    private LinearLayout mLlSendCode;
    private LinearLayout mLlCode;
    private EditText mEtCode;
    private Button mBtnVercode;
    private String exchangeBindId;
    private KProgressHUD kProgressHUD;

    public ExchangeBindDialog(@NonNull Context context, GetMember2DataBean dataBean) {
        super(context);
        this.mContext = context;
        this.dataBean = dataBean;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_exc);
        initView();
        initData();
        boolean isCan = dataBean.isCanExchangeBind();
        if (!isCan) {
            mLlSendCode.setVisibility(View.GONE);
            mAgree.setVisibility(View.GONE);
        }
    }

    private void initView() {
        mEtReason = findViewById(R.id.et_reason);
        mAgree = findViewById(R.id.agree);
        mAgree.setOnClickListener(this);
        mAdd = findViewById(R.id.add);
        mAdd.setOnClickListener(this);
        mRadio = findViewById(R.id.radio);
        mEtOwerTel = findViewById(R.id.et_ower_tel);
        mLlSendCode = findViewById(R.id.ll_send_code);
        mLlCode = findViewById(R.id.ll_code);
        mEtCode = findViewById(R.id.et_code);
        mBtnVercode = findViewById(R.id.btn_vercode);
        mBtnVercode.setOnClickListener(this);
    }

    private void initData() {
        devicelist = dataBean.getDevicelist();
        if (devicelist != null) {
            for (final DevicelistBean devicelistBean : devicelist) {
                String phoneModel = devicelistBean.getPhoneModel();
                String phoneVersion = devicelistBean.getPhoneVersion();
                RadioButton radioButton = new RadioButton(mContext);
                radioButton.setText(phoneModel + " " + phoneVersion);
                radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imei = devicelistBean.getImei();
                        uuid = devicelistBean.getUuid();
                    }
                });
                mRadio.addView(radioButton);
            }
        }

    }

    @Override
    public void onClick(View v) {
        String userName = SharedXmlUtil
                .getInstance(mContext, "scanPaid").read("USER_NAME", "");
        switch (v.getId()) {
            case R.id.agree:
                String reason = mEtReason.getText().toString();
                if (TextUtils.isEmpty(reason)) {
                    Toast.makeText(mContext, "原因不能为空，请认真填写", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(imei) || TextUtils.isEmpty(uuid)) {
                    Toast.makeText(mContext, "请选择要换绑的设备", Toast.LENGTH_SHORT).show();
                    return;
                }
                //工号主人手机号(不能为空)
                String owerTel = mEtOwerTel.getText().toString();
                if (TextUtils.isEmpty(owerTel)) {
                    Toast.makeText(mContext, "工号主人手机号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                kProgressHUD = KProgressHUD.create(mContext)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setCancellable(false)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f)
                        .show();
                SpeedataMethods.getInstance(mContext).sendVerCode(mContext, userName, reason, uuid, imei, owerTel,
                        new OnSendVerCodeBackListener() {
                            @Override
                            public void onBack(SendVerCodeBackData backData) {
                                boolean success = backData.isSuccess();
                                if (success) {
                                    Toast.makeText(mContext, "短信已发送！"
                                            + backData.getMessage(), Toast.LENGTH_SHORT).show();
                                    exchangeBindId = backData.getData().getExchangeBindId();
                                    mLlSendCode.setVisibility(View.GONE);
                                    mAdd.setVisibility(View.GONE);
                                    mAgree.setVisibility(View.GONE);
                                    mLlCode.setVisibility(View.VISIBLE);
                                } else {
                                    Toast.makeText(mContext, "失败！"
                                            + backData.getErrorMessage(), Toast.LENGTH_SHORT).show();
                                }
                                kProgressHUD.dismiss();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                                kProgressHUD.dismiss();
                            }
                        });
                break;
            case R.id.add:
                String name = SharedXmlUtil
                        .getInstance(mContext, "scanPaid").read("USER_NAME", "");
                SpeedataMethods.getInstance(mContext).addDevice(mContext, name, new OnAddDeviceBackListener() {
                    @Override
                    public void onBack(AddDeviceBackData backData) {
                        boolean success = backData.isSuccess();
                        if (success) {
                            Toast.makeText(mContext, ""
                                    + backData.getMessage(), Toast.LENGTH_SHORT).show();

                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setMessage("此为新设备，是否为新设备充值");
                            builder.setPositiveButton("确定", new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(mContext, PayActivity.class);
                                    mContext.startActivity(intent);
                                }
                            });
                            builder.setNegativeButton("取消", new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();

                            dismiss();
                        } else {
                            Toast.makeText(mContext, "失败！"
                                    + backData.getErrorMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            case R.id.btn_vercode:
                String code = mEtCode.getText().toString();
                SpeedataMethods.getInstance(mContext).verCode(mContext, userName, exchangeBindId, code, new OnBackListener() {
                    @Override
                    public void onBack(BackData backData) {
                        boolean success = backData.isSuccess();
                        if (success) {
                            Toast.makeText(mContext, "换绑成功！"
                                    + backData.getMessage(), Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            Toast.makeText(mContext, "失败！"
                                    + backData.getErrorMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            default:
                break;
        }
    }
}
