package com.lwr.password;

import android.app.Application;
import android.content.Context;

import com.lwr.password.constant.Constants;
import com.lwr.password.data.DataPreferences;

import java.util.Map;

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
        /**
         * 初始用户
         */
        Map<String, ?> allUsers = DataPreferences.getAllForMap(getApplicationContext(), Constants.PREFERENCES_FILE_NAME_USER);
        if (allUsers.isEmpty()) {
            DataPreferences.saveKeyValue("admin", "admin", getApplicationContext(), Constants.PREFERENCES_FILE_NAME_USER);
        }
    }

    public static Context getAppContext() {
        return mAppContext;
    }
}
