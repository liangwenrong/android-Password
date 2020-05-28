package com.lwr.password.ui.userupdate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lwr.password.R;
import com.lwr.password.constant.Constants;
import com.lwr.password.data.DataPreferences;
import com.lwr.password.ui.login.LoginActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_update);

        final EditText usernameEditText = findViewById(R.id.reg_username);
        final EditText passwordEditText = findViewById(R.id.reg_password);
        final EditText passwordEditText1 = findViewById(R.id.reg_password1);
        final Button registerBtn = findViewById(R.id.register);
        final ProgressBar regProgressBar = findViewById(R.id.reg_loading);

        usernameEditText.setHint("新账号");
        passwordEditText1.setImeActionLabel("注册", EditorInfo.IME_ACTION_DONE);
        registerBtn.setText("注册新账号");

        registerBtn.setEnabled(true);
        passwordEditText1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                            Toast.makeText(getApplicationContext(), "两次输入密码不一致", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "用户名密码格式错误", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), "两次输入密码不一致", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "用户名密码格式错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void doRegister(final String name, final String password, final ProgressBar regProgressBar, final Button registerBtn) {
        registerBtn.setEnabled(false);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                regProgressBar.setVisibility(View.VISIBLE);
                DataPreferences.clearAll(getApplicationContext(), Constants.PREFERENCES_FILE_NAME_USER);
                DataPreferences.saveKeyValue(name, password, getApplicationContext(), Constants.PREFERENCES_FILE_NAME_USER);
                /**
                 * 跳转登录页面
                 */
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                RegisterActivity.this.finish();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                registerBtn.setEnabled(true);
                dialog.dismiss();
            }
        });
        dialog.setTitle("确定注册新账号？").show();
    }
}
