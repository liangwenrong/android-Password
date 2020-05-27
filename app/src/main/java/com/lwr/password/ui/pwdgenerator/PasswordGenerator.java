package com.lwr.password.ui.pwdgenerator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.lwr.password.R;
import com.lwr.password.constant.Constants;
import com.lwr.password.data.DataPreferences;
import com.lwr.password.utils.generator.PwdGenerator;

public class PasswordGenerator extends Fragment {
    private String password = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pwd_generator, container, false);
        final NumberPicker lenpicker = root.findViewById(R.id.lenpicker);
        initNumberPicker(lenpicker);
        final CheckBox ckNum = root.findViewById(R.id.ck_num);
        final CheckBox ckLow = root.findViewById(R.id.ck_low_letter);
        final CheckBox ckUp = root.findViewById(R.id.ck_up_letter);
        final CheckBox ckExtra = root.findViewById(R.id.ck_extra);
        final EditText selfLetter = root.findViewById(R.id.et_self_letter);
        final TextView showPwd = root.findViewById(R.id.tv_show_pwd);

        final Button btnGenerate = root.findViewById(R.id.btn_pwd_generate);
        final TextView addToAccount = root.findViewById(R.id.tv_addto_account);

        //点击生成按钮
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int len = lenpicker.getValue();
                String diy = selfLetter.getText().toString().trim();
                char[] chars = null;
                if (!TextUtils.isEmpty(diy)) {
                    chars = new char[diy.length()];
                    diy.getChars(0, diy.length(), chars, 0);
                }
                password = PwdGenerator.generatePassword(len, ckNum.isChecked(), ckLow.isChecked(), ckUp.isChecked(), ckExtra.isChecked(), chars);
                showPwd.setText(password);
            }
        });
        /**
         * 添加到账号
         */
        addToAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(" ");
                View view = LayoutInflater.from(getContext()).inflate(R.layout.password_add, null);
                builder.setView(view);
                final EditText userEdit = (EditText) view.findViewById(R.id.userEdit);
                final EditText passwdEdit = (EditText) view.findViewById(R.id.passwdEdit);
                final EditText passwdEdit2 = (EditText) view.findViewById(R.id.passwdEdit2);
                Switch pwdSwitch = view.findViewById(R.id.switch_pwd);
                pwdSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {//显示
                            passwdEdit.setInputType(InputType.TYPE_CLASS_TEXT);
                            passwdEdit2.setInputType(InputType.TYPE_CLASS_TEXT);
                        } else {//隐藏
                            passwdEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            passwdEdit2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }
                    }
                });

                passwdEdit.setText(password);
                passwdEdit2.setText(password);
                passwdEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwdEdit2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                final Toast toast = Toast.makeText(getContext(), "密码不一致", Toast.LENGTH_LONG);
                passwdEdit2.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String password = passwdEdit.getText().toString();
                        if (!TextUtils.isEmpty(password) && !password.equals(s.toString())) {
                            toast.show();
                        } else {
                            toast.cancel();

                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                passwdEdit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String password = passwdEdit2.getText().toString();
                        if (!TextUtils.isEmpty(password) && !password.equals(s.toString())) {
                            toast.show();
                        } else {
                            toast.cancel();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                builder.setPositiveButton("提交",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //确定操作的内容
                                final String username = userEdit.getText().toString();
                                final String password = passwdEdit.getText().toString();
                                final String password2 = passwdEdit2.getText().toString();//重复密码
                                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || !password.equals(password2)) {
                                    Toast.makeText(getContext(), "输入有误", Toast.LENGTH_SHORT).show();
                                } else {
                                    String pwd = DataPreferences.getStringByKey(username, getContext(), Constants.PREFERENCES_FILE_NAME_PASSWORD);
                                    if (TextUtils.isEmpty(pwd)) {
                                        //添加
                                        DataPreferences.saveKeyValue(username, password, getContext(), Constants.PREFERENCES_FILE_NAME_PASSWORD);
                                        //刷新listview
//                                        refreshListView();
                                    } else {
                                        //账号已存在
                                        new AlertDialog.Builder(getContext()).setTitle("账号 " + username + " 已存在，是否覆盖？")
                                                .setPositiveButton("确定覆盖", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //添加
                                                        DataPreferences.saveKeyValue(username, password, getContext(), Constants.PREFERENCES_FILE_NAME_PASSWORD);
                                                        Toast.makeText(getContext(), "添加成功", Toast.LENGTH_SHORT).show();
                                                        //刷新listview
//                                                        refreshListView();
                                                    }
                                                })
                                                .setNegativeButton("不要覆盖", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //点击取消触发的事件
                                                        Toast.makeText(getContext(), "取消覆盖", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).show();
                                    }
                                }
                            }
                        });
                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String username = userEdit.getText().toString();
                                String password = passwdEdit.getText().toString();
                                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                                    Toast.makeText(getContext(), "取消操作", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                builder.show();
            }
        });


        return root;
    }

    /**
     * Android NumberPicker
     */

    private void initNumberPicker(NumberPicker numberPicker) {
        //设置最大值
        numberPicker.setMaxValue(64);
        //设置最小值
        numberPicker.setMinValue(1);
        //设置当前值
        numberPicker.setValue(8);
        //设置滑动监听
//        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            //当NunberPicker的值发生改变时，将会激发该方法
//            @Override
//            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                String toast = "oldVal：" + oldVal + "   newVal：" + newVal;
//                Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
//            }
//        });

    }
}
