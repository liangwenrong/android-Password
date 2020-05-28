package com.lwr.password.ui.config;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lwr.password.R;
import com.lwr.password.constant.Constants;
import com.lwr.password.data.DataPreferences;
import com.lwr.password.ui.gesture.GestureType;
import com.lwr.password.ui.gesture.PwdGestureActivity;
import com.lwr.password.ui.gesture.PwdGestureCheckActivity;

public class Configurater extends Fragment {
    private static String key = "";
    private Switch fingerSwitch;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_config, container, false);

        final EditText keyTextEdit = root.findViewById(R.id.text_key);
        final Switch keySwitch = root.findViewById(R.id.switch_key);
        final TextView aboutmeText = root.findViewById(R.id.about_me);
        fingerSwitch = root.findViewById(R.id.switch_finger);

        key = DataPreferences.getRawString(Constants.CONFIG_AES_KEY, getContext(), Constants.PREFERENCES_FILE_NAME_CONFIG);
        if (key == null) {
            key = "";
        }
        keyTextEdit.setText(key);
        keyTextEdit.setEnabled(false);
        keyTextEdit.setFocusableInTouchMode(false);
        keySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {//显示密钥
                    keyTextEdit.setInputType(InputType.TYPE_CLASS_TEXT);//显示明文
                    keyTextEdit.setFocusableInTouchMode(true);
                    keyTextEdit.setEnabled(true);//可编辑
                } else {
                    keyTextEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);//密码形式
                    keyTextEdit.setFocusableInTouchMode(false);
                    keyTextEdit.setEnabled(false);//不可编辑

                    //保存密钥
                    final String keyEdit = keyTextEdit.getText().toString();
                    if (!key.equals(keyEdit)) {//修改了
                        new AlertDialog.Builder(getContext()).setTitle("检测到修改，是否保存新的密钥？")
                                .setPositiveButton("确定修改", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DataPreferences.saveRawKeyValue(Constants.CONFIG_AES_KEY, keyEdit, getContext(), Constants.PREFERENCES_FILE_NAME_CONFIG);
                                        DataPreferences.loadAndRefreshAESKEY(getContext());
                                        key = keyEdit;
                                        Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("不要保存", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        keyTextEdit.setText(key);
                                    }
                                }).show();
                    }
                }
            }
        });
        /**
         * 手势登录
         */
        fingerSwitch.setChecked(DataPreferences.isIsFingerLogin(getContext()));
        fingerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!buttonView.isPressed()) {
                    return;
                }
                fingerSwitch.setChecked(DataPreferences.isIsFingerLogin(getContext()));//按钮不变，等成功后再
                if (isChecked) {//开启手势
                    /**
                     * 设置手势
                     */
                    Intent intent = new Intent(getActivity(), PwdGestureActivity.class);
                    startActivityForResult(intent, 1);
                } else {
                    /**
                     * 验证手势并关闭
                     */
                    Intent intent = new Intent(getActivity(), PwdGestureCheckActivity.class);
                    intent.putExtra("gestureFlg", GestureType.FOR_CLEAR);//gestureFlg==1表示删除手势
                    startActivityForResult(intent, 2);
                }
            }
        });

        /**
         * 点击关于我
         */
        aboutmeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.aboutme);
                builder.show();
            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        fingerSwitch.setChecked(DataPreferences.isIsFingerLogin(getContext()));
        if (requestCode == 1) {//开启手势成功

        } else if (requestCode == 2) {//关闭手势，验证通过

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}