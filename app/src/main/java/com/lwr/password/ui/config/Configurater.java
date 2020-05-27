package com.lwr.password.ui.config;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.lwr.password.R;
import com.lwr.password.constant.Constants;
import com.lwr.password.data.DataPreferences;
import com.lwr.password.ui.gallery.GalleryViewModel;

public class Configurater extends Fragment {
    private static String key = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_config, container, false);

        final EditText keyTextEdit = root.findViewById(R.id.text_key);
        final Switch keySwitch = root.findViewById(R.id.switch_key);
        final TextView aboutmeText = root.findViewById(R.id.about_me);

        key = DataPreferences.getRawString(Constants.CONFIG_KEY, getContext(), Constants.PREFERENCES_FILE_NAME_CONFIG);
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
                                        DataPreferences.saveRawKeyValue(Constants.CONFIG_KEY, keyEdit, getContext(), Constants.PREFERENCES_FILE_NAME_CONFIG);
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
}