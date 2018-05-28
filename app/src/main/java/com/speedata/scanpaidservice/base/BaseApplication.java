package com.speedata.scanpaidservice.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by 张明_ on 2017/8/23.
 * Email 741183142@qq.com
 */

public class BaseApplication extends Application {
    private ArrayList<Activity> activityList = new ArrayList<>();
    private Toast mToast;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * 显示Toast
     *
     * @param msg String
     */
    @SuppressLint("ShowToast")
    public void showToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);// getApplicationContext()
        } else {
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    /**
     * 添加到ArrayList<Activity>
     *
     * @param activity：Activity
     */
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }


    public void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    /**
     * 退出所有的Activity
     */
    public void finishAllActivity() {
        for (Activity activity : activityList) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
