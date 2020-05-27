package com.lwr.password.ui.pwdmanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckedTextView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lwr.password.R;
import com.lwr.password.constant.Constants;
import com.lwr.password.data.DataPreferences;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class PasswordManager extends Fragment {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> accountList = new ArrayList<String>();;
    private ArrayList<String> accountRealList;
    private EditText searchText;
    //下拉刷新
    private float flagY;
    private int firstVisibleItem;
    private int visibleItemCount;
    private int totalItemCount;
    private int startIndex;
    private int length;

    @SuppressLint("ClickableViewAccessibility")//onTouch拦截了onClick事件，忽略
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_pwd_manager, container, false);
        searchText = root.findViewById(R.id.search_text);
        //下拉刷新，onRefresh导致listview数据绑定失败，所以不用
//        final SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);//初始化下拉刷新控件，
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                //清空搜索,如果不行把搜索filter内容也清空
//                searchText.setText(null);
//                //刷新数据
//                refreshListView();
//                //停止圈圈
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });

        //查找所有账号密码
        accountRealList = DataPreferences.getAllForList(root.getContext(), Constants.PREFERENCES_FILE_NAME_PASSWORD);
        accountList.addAll(accountRealList);
        //显示账号列表
        adapter = new ArrayAdapter<String>(
                root.getContext(), android.R.layout.simple_list_item_single_choice, accountList);
        adapter.setNotifyOnChange(false);
        listView = root.findViewById(R.id.userlistview);
        listView.setAdapter(adapter);
        //允许查询列表
        listView.setTextFilterEnabled(true);
//        changeSearchBlack(listView);///todo


        //添加按钮
        root.findViewById(R.id.add_id).setOnClickListener(new View.OnClickListener() {
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
                                        refreshListView();
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
                                                        refreshListView();
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
        //删除按钮
        root.findViewById(R.id.del_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(" ");
                View view = LayoutInflater.from(getContext()).inflate(R.layout.password_del, null);
                builder.setView(view);

                final EditText userDelete = (EditText) view.findViewById(R.id.user_del);
                builder.setPositiveButton("确认删除",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //确定操作的内容
                                String username = userDelete.getText().toString();
                                if (TextUtils.isEmpty(username)) {
                                    Toast.makeText(getContext(), "请输入要删除的账号", Toast.LENGTH_SHORT).show();
                                } else {
                                    String password = DataPreferences.getStringByKey(username, getContext(), Constants.PREFERENCES_FILE_NAME_PASSWORD);
                                    if (TextUtils.isEmpty(password)) {
                                        Toast.makeText(getContext(), "账号不存在", Toast.LENGTH_SHORT).show();
                                    } else {
                                        DataPreferences.remove(username, getContext(), Constants.PREFERENCES_FILE_NAME_PASSWORD);
                                        Toast.makeText(getContext(), "删除 " + username + " 成功", Toast.LENGTH_SHORT).show();
                                        //刷新listview
                                        refreshListView();
                                    }
                                }
                            }
                        });
                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String username = userDelete.getText().toString();
                                if (!TextUtils.isEmpty(username)) {
                                    Toast.makeText(getContext(), "取消操作", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                builder.show();
            }
        });

        /**
         * 点击事件
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (isFastDoubleClick()) {//双击
                    String username = ((AppCompatCheckedTextView) view).getText().toString();
                    String password = DataPreferences.getStringByKey(username, root.getContext(), Constants.PREFERENCES_FILE_NAME_PASSWORD);
                    //展示
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    AlertDialog alertDialog = builder.setTitle(username).setMessage(password).create();
                    alertDialog.show();
                }

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String username = ((AppCompatCheckedTextView) view).getText().toString();
                String password = DataPreferences.getStringByKey(username, root.getContext(), Constants.PREFERENCES_FILE_NAME_PASSWORD);

                ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("", password));
                Toast.makeText(getContext(), "复制成功", Toast.LENGTH_SHORT).show();
                return true;
            }
        });


        /**
         * 搜索
         */
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if (s == null || s.length() < 1) {
//                    listView.clearTextFilter();
//                    adapter.getFilter().filter("");
                    accountList.clear();
                    accountList.addAll(accountRealList);
                    adapter.notifyDataSetChanged();
                    return;
                }
//                adapter.getFilter().filter(s.toString());//这种方式无法搜索空显示全部
//                listView.setFilterText(s.toString()); // 使用用户输入的内容对 ListView 列表项进行过滤
                //自定义
                accountList.clear();
                for (String key: accountRealList) {
                    if(key!=null && key.contains(text)){
                        accountList.add(key);
                    }
                }
                adapter.notifyDataSetChanged();
                listView.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        /**
         * 下拉刷新
         */
        listView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //   Log.i("XCF","onTouch");
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
//                    Log.i("XCF", "按下");
                    flagY = event.getY();
                } else if (action == MotionEvent.ACTION_UP) {
//                    Log.i("XCF", "抬起");
                    float y = event.getY() - flagY;
                    if (y > 250) {
                        if (firstVisibleItem == 0) {
//                            Log.i("XCF", "下拉正在刷新");
                            refreshListView();
                        }
                    } else if (y < -250) {
                        if ((firstVisibleItem + visibleItemCount - 1) == (totalItemCount - 1)) {
//                            Log.i("XCF", "上拉正在加载");
//                            Toast.makeText(getActivity(), "上拉正在加载", Toast.LENGTH_SHORT).show();
//                            startIndex = startIndex + length;

                        }
                    }
                }
//                v.performClick();//继续触发click事件
                return false;
            }
        });


        return root;
    }

    private void refreshListView() {
        searchText.setText("");
        Toast toast = Toast.makeText(getActivity(), "正在刷新", Toast.LENGTH_SHORT);
        toast.show();
        accountRealList = DataPreferences.getAllForList(getContext(), Constants.PREFERENCES_FILE_NAME_PASSWORD);
        accountList.clear();
        accountList.addAll(accountRealList);
        adapter.notifyDataSetChanged();
        toast.cancel();
    }

    private static long lastClickTime;
    private static long during = 500;

    //防止重复点击 事件间隔，在这里我定义的是1000毫秒
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (timeD >= 0 && timeD <= during) {
            return true;
        } else {
            lastClickTime = time;
            return false;
        }
    }


    /**
     * 去掉搜索的黑色弹框
     * @param listView
     */
//    private void changeSearchBlack(ListView listView) {
//        try {
//            Field field = listView.getClass().getSuperclass().getDeclaredField("mTextFilter");
//            field.setAccessible(true);
//            EditText searchAutoComplete = (EditText) field.get(listView);
////            searchAutoComplete.setTextColor(getResources().getColor(android.R.color.transparent));
////            searchAutoComplete.setBackgroundColor(getResources().getColor(android.R.color.transparent));
//            searchAutoComplete.setVisibility(View.GONE);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
