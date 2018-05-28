package com.speedata.scanpaidservice.base;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


/**
 * Created by 张明_ on 2017/8/23.
 * Email 741183142@qq.com
 */

public abstract class BaseActivity extends AppCompatActivity implements IBaseView {
    /**
     * 上次点击时间
     */
    private long lastClick = 0;
    /**
     * 当前Activity渲染的视图View
     */
    protected View contentView;
    protected BaseActivity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        Bundle bundle = getIntent().getExtras();
        initData(bundle);
        setBaseView(bindLayout());
        initView(savedInstanceState, contentView);
        doBusiness();
    }

    protected void setBaseView(@LayoutRes int layoutId) {
        setContentView(contentView = LayoutInflater.from(this).inflate(layoutId, null));
    }

    public Context getContext() {
        return this;
    }

    public void openAct(Context packageContext, Class<?> cls) {
        Intent intent = new Intent(packageContext, cls);
        startActivity(intent);
    }

    @Override
    public void onClick(final View view) {
        if (!isFastClick()) {
            onWidgetClick(view);
        }
    }

    /**
     * 清除键监听
     *
     * @param et       EditText
     * @param clearbtn 清除键
     */
    public void setClearBtnListener(final EditText et, final View clearbtn) {
        setClearBtnListener(et, clearbtn, null, null);
    }

    public void setClearBtnListener(final EditText et, final View clearbtn, final TextWatcher textWatcher, final View.OnFocusChangeListener focusChangeListener) {
        clearbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et.setText("");
            }
        });
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (focusChangeListener != null) {
                    focusChangeListener.onFocusChange(v, hasFocus);
                }
                if (v == et) {
                    if (hasFocus) {
                        if (!TextUtils.isEmpty(et.getText())) {
                            clearbtn.setVisibility(View.VISIBLE);
                            return;
                        }
                    }
                    clearbtn.setVisibility(View.GONE);
                }
            }
        });
        et.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!TextUtils.isEmpty(s)) {
                            clearbtn.setVisibility(View.VISIBLE);
                        } else {
                            clearbtn.setVisibility(View.GONE);
                        }
                        if (textWatcher != null) {
                            textWatcher.onTextChanged(s, start, before, count);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
        et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });
        clearbtn.setVisibility(View.GONE);
    }

    /**
     * 隐藏软键盘
     */
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus() != null ? getCurrentFocus() : findViewById(android.R.id.content);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 显示软键盘
     */
    public void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(0, 0);
        }
    }

    /**
     * 判断是否快速点击
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    private boolean isFastClick() {
        long now = System.currentTimeMillis();
        if (now - lastClick >= 200) {
            lastClick = now;
            return false;
        }
        return true;
    }


    /*----------------------以下是android6.0动态授权的封装----------------------------*/
    private int mPermissionIdx = 0x10;//请求权限索引
    private SparseArray<GrantedResult> mPermissions = new SparseArray<>();//请求权限运行列表

    @Override
    @SuppressLint("Override")
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        GrantedResult runnable = mPermissions.get(requestCode);
        if (runnable == null) {
            return;
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            runnable.mGranted = true;
        }
        runOnUiThread(runnable);
    }

    public void requestPermission(String[] permissions, String reason, GrantedResult runnable) {
        if (runnable == null) {
            return;
        }
        runnable.mGranted = false;
        if (Build.VERSION.SDK_INT < 23 || permissions == null || permissions.length == 0) {
            runnable.mGranted = true;//新添加
            runOnUiThread(runnable);
            return;
        }
        final int requestCode = mPermissionIdx++;
        mPermissions.put(requestCode, runnable);

		/*
            是否需要请求权限
		 */
        boolean granted = true;
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                granted = granted && checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            }
        }

        if (granted) {
            runnable.mGranted = true;
            runOnUiThread(runnable);
            return;
        }

		/*
			是否需要请求弹出窗
		 */
        boolean request = true;
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                request = request && !shouldShowRequestPermissionRationale(permission);
            }
        }

        if (!request) {
            final String[] permissionTemp = permissions;
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(reason)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(permissionTemp, requestCode);
                            }
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            GrantedResult runnable = mPermissions.get(requestCode);
                            if (runnable == null) {
                                return;
                            }
                            runnable.mGranted = false;
                            runOnUiThread(runnable);
                        }
                    }).create();
            dialog.show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, requestCode);
            }
        }
    }

    public static abstract class GrantedResult implements Runnable {
        private boolean mGranted;

        public abstract void onResult(boolean granted);

        @Override
        public void run() {
            onResult(mGranted);
        }
    }
}
