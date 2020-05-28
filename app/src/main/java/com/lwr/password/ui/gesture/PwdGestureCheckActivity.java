package com.lwr.password.ui.gesture;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lwr.password.MainActivity;
import com.lwr.password.R;
import com.lwr.password.data.DataPreferences;
import com.lwr.password.ui.component.ToastA;

import java.util.List;

public class PwdGestureCheckActivity extends AppCompatActivity implements GestureView.GestureCallBack {
    private GestureView gestureView;
    //    private TextView tv_user_name;
    private int gestureFlg = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_setting);
        TextView gesture_title = findViewById(R.id.gesture_title);
        TextView back = findViewById(R.id.tv_setting_back);
        gestureFlg = getIntent().getIntExtra("gestureFlg", -1);
        if (gestureFlg == GestureType.FOR_CHECK) {
            gesture_title.setText("登录");
            back.setVisibility(View.GONE);
        } else if (gestureFlg == GestureType.FOR_CLEAR) {
            gesture_title.setText("删除密码手势");
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    onKeyDown(KeyEvent.KEYCODE_BACK, null);
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
        }
        gestureView = (GestureView) findViewById(R.id.gesture);
        gestureView.setGestureCallBack(PwdGestureCheckActivity.this);
    }

    /**
     * 手势输入正确后，回调
     *
     * @param stateFlag
     * @param data
     * @param success
     */
    @Override
    public void gestureVerifySuccessListener(int stateFlag, List<GestureView.GestureBean> data, boolean success) {
        if (success) {
            if (gestureFlg == GestureType.FOR_CLEAR) {
                //删除密码
                DataPreferences.setIsFingerLogin(false, getApplicationContext());
                gestureView.clearCache();//设置手势状态为空，下次使用需要先设置手势
                ToastA.showToast(PwdGestureCheckActivity.this, "手势登录已关闭", Toast.LENGTH_SHORT);
                finish();//直接清空当前activity，返回原来页面
            } else if (gestureFlg == 2) {
                //修改密码
                ToastA.showToast(PwdGestureCheckActivity.this, "验证手势密码成功,请重新设置", Toast.LENGTH_SHORT);
                //跳转到设置手势密码页面
                Intent intent = new Intent(PwdGestureCheckActivity.this, PwdGestureActivity.class);
                startActivity(intent);
                finish();
            } else if (gestureFlg == GestureType.FOR_CHECK) {
                //登录成功
                Intent intent = new Intent(PwdGestureCheckActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        } else {

        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}