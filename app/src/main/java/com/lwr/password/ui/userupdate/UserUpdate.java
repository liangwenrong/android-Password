package com.lwr.password.ui.userupdate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.lwr.password.MainActivity;
import com.lwr.password.R;
import com.lwr.password.constant.Constants;
import com.lwr.password.data.DataPreferences;
import com.lwr.password.ui.login.LoginActivity;
import com.lwr.password.utils.DateUtils;
import com.lwr.password.utils.FileUtils;

import java.util.Date;

public class UserUpdate extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_update, container, false);

        final EditText usernameEditText = root.findViewById(R.id.reg_username);
        final EditText passwordEditText = root.findViewById(R.id.reg_password);
        final EditText passwordEditText1 = root.findViewById(R.id.reg_password1);
        final Button registerBtn = root.findViewById(R.id.register);
        final ProgressBar regProgressBar = root.findViewById(R.id.reg_loading);
        registerBtn.setEnabled(true);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String username = usernameEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    String password1 = passwordEditText1.getText().toString();
                    if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && password.length() > 5) {
                        if (password.equals(password1)) {
                            doRegister(username, password, regProgressBar, registerBtn);
                        } else {
                            Toast.makeText(getContext(), "两次输入密码不一致", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "用户名密码格式错误", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 *
                 */
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String password1 = passwordEditText1.getText().toString();
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && password.length() > 5) {
                    if (password.equals(password1)) {
                        doRegister(username, password, regProgressBar, registerBtn);
                    } else {
                        Toast.makeText(getContext(), "两次输入密码不一致", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "用户名密码格式错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return root;
    }

    void doRegister(final String name, final String password, final ProgressBar regProgressBar, final Button registerBtn) {
        registerBtn.setEnabled(false);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setPositiveButton("确定替换旧账号", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                regProgressBar.setVisibility(View.VISIBLE);
                DataPreferences.clearAll(getContext(), Constants.PREFERENCES_FILE_NAME_USER);
                DataPreferences.saveKeyValue(name, password, getContext(), Constants.PREFERENCES_FILE_NAME_USER);
                /**
                 * 跳转前先导出一份备份
                 */
                String relatePath = "/Download/passwordFile_" + DateUtils.format(new Date()) + ".crypt";
                boolean result = FileUtils.exportPasswordFile(getActivity(), relatePath);
                if(result){
                    Snackbar.make(getView(), "已导出密码列表加密文件到 手机存储" + relatePath, Snackbar.LENGTH_LONG)
                            .setAction("提示", null).show();
                }else{
                    Snackbar.make(getView(), "导出失密码列表加密文件败，请重试", Snackbar.LENGTH_SHORT)
                            .setAction("提示", null).show();
                }
                /**
                 * 跳转登录页面
                 */
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                registerBtn.setEnabled(true);
                dialog.dismiss();
            }
        });
        dialog.setTitle("是否确认修改").show();
    }
}
