package com.lwr.password.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.lwr.password.R;
import com.lwr.password.data.DataPreferences;
import com.lwr.password.ui.gesture.GestureType;
import com.lwr.password.ui.gesture.PwdGestureCheckActivity;
import com.lwr.password.ui.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 执行跳转
                doJump();
            }
        }, 500);
    }

    private void doJump() {
        boolean isFingerLogin = DataPreferences.isIsFingerLogin(getApplicationContext());
        if (isFingerLogin) {//手势登录
            Intent intent = new Intent(SplashActivity.this, PwdGestureCheckActivity.class);
            intent.putExtra("gestureFlg", GestureType.FOR_CHECK);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        finish();
    }
}
