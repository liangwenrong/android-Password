package com.lwr.password;

import android.app.Application;
import android.content.Context;

import com.lwr.password.data.DataPreferences;

/**
 * Created by yc.Zhao on 2017/11/29 0029.
 */

public class MyApplication extends Application {
    private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = getApplicationContext();
        initApp();
    }

    /**
     * 初始化工作
     */
    private void initApp() {
        DataPreferences.initAPP(getApplicationContext());
    }

    public static Context getAppContext() {
        return mAppContext;
    }
}
