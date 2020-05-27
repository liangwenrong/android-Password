package com.lwr.password.data;

import android.content.Context;

import com.lwr.password.constant.Constants;
import com.lwr.password.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private Context applicationContext;

    public void setApplicationContext(Context applicationContext) {
        this.applicationContext = applicationContext;
    }
    public Result<LoggedInUser> login(String username, String password) {

        try {
            String passwordString = DataPreferences.getStringByKey(username, applicationContext, Constants.PREFERENCES_FILE_NAME_USER);
            if (passwordString == null) {
                return new Result.Error(new IOException("用户名或密码错误"));
            }
            boolean result = checkPassword(password, passwordString);
            if (result){
                return new Result.Success<>(new LoggedInUser(username, username));
            }else{
                throw new Exception("密码错误");
            }
        } catch (Exception e) {
            return new Result.Error(new IOException(e.getMessage(), e));
        }
    }

    /**
     * dondogrcx335gDD123lwr123.的密码是lwr123.（n位+两个相同字符+3位+真实密码）
     */
    private boolean checkPassword(String password, String passwordString) {
        char[] dst = new char[password.length()];
        password.getChars(0,password.length(),dst,0);
        int k = 0;
        for (int i = 0; i < dst.length-1; i++) {//去掉相同字符前面一截
            if(dst[i]==dst[i+1]){
                k=i+2;
                break;
            }
        }
        k=k+3;//前面必须3个随机
        password = password.substring(k);
        return passwordString.equals(password);
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
