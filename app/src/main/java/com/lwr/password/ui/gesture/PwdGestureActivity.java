package com.lwr.password.ui.gesture;

import android.app.Instrumentation;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lwr.password.R;
import com.lwr.password.data.DataPreferences;
import com.lwr.password.ui.component.ToastA;

import java.util.List;

public class PwdGestureActivity extends AppCompatActivity implements GestureView.GestureCallBack {
    private int jumpFlg;//跳转传来的参数
    private int flag;//跳转传来的参数


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_setting);
//        TextView tv_setting_back = findViewById(R.id.tv_setting_back);
//        tv_setting_back.setVisibility(View.VISIBLE);
        TextView gesture_title = findViewById(R.id.gesture_title);
        gesture_title.setText("设置手势密码");
        jumpFlg = getIntent().getIntExtra("jumpFlg", 0);
        flag = getIntent().getIntExtra("flag", 0);
        TextView back = findViewById(R.id.tv_setting_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        try {
                            new Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                        } catch (Exception e) {

                        }
                    }
                }.start();
            }
        });
        /**
         * init view
         */
        final TextView tv_back = (TextView) findViewById(R.id.tv_setting_back);
        GestureView gestureView = (GestureView) findViewById(R.id.gesture);
        gestureView.setGestureCallBack(this);

        //不调用这个方法会造成第二次启动程序直接进入手势识别而不是手势设置
        gestureView.clearCache();
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @Override
    public void gestureVerifySuccessListener(int stateFlag, List<GestureView.GestureBean> data, boolean success) {
        if (stateFlag == GestureView.STATE_LOGIN) {
            DataPreferences.setIsFingerLogin(true, getApplicationContext());
            ToastA.showToast(PwdGestureActivity.this, "手势登录已开启", Toast.LENGTH_SHORT);
            finish();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
