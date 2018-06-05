package com.speedata.scanpaidservice.ui.login;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.speedata.scanpaidservice.R;
import com.speedata.scanpaidservice.mvp.MVPBaseActivity;
import com.speedata.scanpaidservice.ui.main.MainActivity;
import com.speedata.scanpaidservice.ui.pay.PayActivity;
import com.speedata.scanpaidservice.utils.DataCleanManager;
import com.speedata.scanpaidservice.utils.DateUtils;
import com.speedata.scanpaidservice.utils.SharedXmlUtil;
import com.speedata.scanservice.bean.member2.GetMember2DataBean;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class LoginActivity extends MVPBaseActivity<LoginContract.View, LoginPresenter> implements LoginContract.View {

    private Toolbar mToolbar;
    private EditText mEtUsertel;
    private EditText mEtPassword;
    private Button mBtnLogin;
    private TextView mTvRegister;
    private KProgressHUD kProgressHUD;
    private TextView mTvInfo;
    private Button mBtnClean;

    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permission();
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void initView(Bundle bundle, View view) {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.this.finish();
            }
        });

        mEtUsertel = findViewById(R.id.et_usertel);
        mEtPassword = findViewById(R.id.et_password);
        mBtnLogin = findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(this);
        mTvRegister = findViewById(R.id.tv_register);
        mTvRegister.setOnClickListener(this);

        mTvInfo = findViewById(R.id.tv_info);
        final String imei = getIMEI(LoginActivity.this);
        mTvInfo.setText("IMEI：" + imei + "\n");
//        String sha1 = Signature.getSHA1("{\"priceList\":[{\"actBeginTime\":\"2018-04-02 16:00:00\",\"actEndTime\":\"2018-04-28 15:59:59\",\"actPrice\":10.0,\"descs\":\"1个月会员充值\",\"isRecommend\":1,\"isShow\":1,\"months\":1,\"price\":10.0,\"priceId\":\"32e55b4057ee40f58de68883c8bc1d1f\",\"sellPrice\":10.0,\"title\":\"1个月会员充值\"},{\"actBeginTime\":\"2018-04-02 16:00:00\",\"actEndTime\":\"2018-04-30 15:59:59\",\"actPrice\":27.0,\"descs\":\"三个月会员充值\",\"isRecommend\":1,\"isShow\":1,\"months\":3,\"price\":30.0,\"priceId\":\"77df78feef3045ffaa6434def45619a8\",\"sellPrice\":30.0,\"title\":\"3个月会员充值\"},{\"actBeginTime\":\"2018-02-05 16:00:00\",\"actEndTime\":\"2018-05-04 15:59:59\",\"actPrice\":50.0,\"descs\":\"\",\"isRecommend\":0,\"isShow\":1,\"months\":6,\"price\":60.0,\"priceId\":\"1fb33797c4424490b5dbfab8316199de\",\"sellPrice\":60.0,\"title\":\"半年会员\"},{\"actBeginTime\":\"2018-02-04 16:00:00\",\"actEndTime\":\"2018-05-04 15:59:59\",\"actPrice\":90.0,\"descs\":\"大促销\",\"isRecommend\":0,\"isShow\":1,\"months\":12,\"price\":120.0,\"priceId\":\"576488c209004c51848a2a367cca7a86\",\"sellPrice\":120.0,\"title\":\"1年\"}]}", HttpMethods.NONCE);
//        mTvInfo.setText(sha1);
        mBtnClean = findViewById(R.id.btn_clean);
        mBtnClean.setOnClickListener(this);
    }

    @Override
    public void doBusiness() {
    }

    @Override
    public void onWidgetClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                String user = mEtUsertel.getText().toString();
                boolean phoneNumberValid = isPhoneNumberValid(user);
                if (!phoneNumberValid){
                    Toast.makeText(mActivity, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                kProgressHUD = KProgressHUD.create(LoginActivity.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setCancellable(false)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f)
                        .show();

                String paw = mEtPassword.getText().toString();
                SharedXmlUtil.getInstance(this, "scanPaid").write("USER_NAME", user);
                mPresenter.login(LoginActivity.this, user);
                break;
            case R.id.tv_register:
                break;
            case R.id.btn_clean:
                DataCleanManager.cleanSharedPreference(mActivity);
                DataCleanManager.cleanInternalCache(mActivity);
                DataCleanManager.cleanExternalCache(mActivity);
                DataCleanManager.cleanDatabases(mActivity);
                break;
            default:
                break;
        }
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
        if (kProgressHUD != null) {
            kProgressHUD.dismiss();
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void openAct(int expireStatus, String expireDate) {
        if (kProgressHUD != null) {
            kProgressHUD.dismiss();
        }
        if (!TextUtils.isEmpty(expireDate)){
            String timeByHour = DateUtils.getTimeByHour(48, DateUtils.FORMAT_YMD);
            Long aLong = DateUtils.convertTimeToLong(DateUtils.FORMAT_YMD, expireDate);
            Long time = DateUtils.convertTimeToLong(DateUtils.FORMAT_YMD, timeByHour);
            if (time > aLong) {
                Toast.makeText(mActivity, "注意！会员将在" + expireDate + "到期", Toast.LENGTH_SHORT).show();
            }
        }
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("expireDate", expireDate);
        bundle.putInt("expireStatus", expireStatus);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 设备换绑
     */
    @Override
    public void exchangeBind(GetMember2DataBean dataBean) {
        if (kProgressHUD != null) {
            kProgressHUD.dismiss();
        }
        ExchangeBindDialog exchangeBindDialog = new ExchangeBindDialog(this, dataBean);
        exchangeBindDialog.setTitle("申请换绑");
        exchangeBindDialog.show();
    }

    @Override
    public void openPayAct() {
        if (kProgressHUD != null) {
            kProgressHUD.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage("此为新设备，是否为新设备充值");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(LoginActivity.this, PayActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void permission() {
        AndPermission.with(LoginActivity.this)
                .permission(Manifest.permission.READ_PHONE_STATE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.GET_ACCOUNTS
                        , Manifest.permission.READ_CONTACTS
                        , Manifest.permission.INTERNET
                        , Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.ACCESS_WIFI_STATE
                        , Manifest.permission.CAMERA)
                .callback(listener)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(LoginActivity.this, rationale).show();
                    }
                }).start();
    }

    PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(LoginActivity.this, deniedPermissions)) {
                AndPermission.defaultSettingDialog(LoginActivity.this, 300).show();
            }
        }
    };


    /**
     * 获取IMEI
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        String deviceId = tm.getDeviceId();
        return deviceId;//android.os.Build.SERIAL;
    }


    /**
     * 判断手机号字符串是否合法
     *
     * @param phoneNumber 手机号字符串
     * @return 手机号字符串是否合法
     */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        String expression = "^1[3|4|5|7|8]\\d{9}$";
        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
}
