package com.lwr.password;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.lwr.password.data.Permissions;
import com.lwr.password.filechooser.FileUtil;
import com.lwr.password.ui.home.HomeFragment;
import com.lwr.password.utils.DateUtils;
import com.lwr.password.utils.FileUtils;
import com.lwr.password.utils.ThemeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    View fabView;
    PopupWindow mMorePopupWindow = null;
    int mShowMorePopupWindowWidth = 0;
    int mShowMorePopupWindowHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setWindowStatusBarColor(MainActivity.this, R.color.colorAccent);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabView = view;
                showPopUpWindow(getApplicationContext(), view);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_pwd_generator,
                R.id.nav_config, R.id.nav_pwd_manager, R.id.nav_user_update)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        /**
         * 申请读写权限
         */
        Permissions.verifyStoragePermissions(MainActivity.this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
//        menu.findItem(R.id.action_settings)
        return true;
    }

    /**
     * MenuItem选中事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
//            Toast.makeText(getApplicationContext(), "settings", Toast.LENGTH_SHORT).show();
            getAppDetailSettingIntent(getApplicationContext());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 跳转到权限设置界面
     */
    private void getAppDetailSettingIntent(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void showPopUpWindow(Context context, final View clickView) {
        if (mMorePopupWindow == null) {//初始化
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View content = li.inflate(R.layout.popup_menu, null, false);
            mMorePopupWindow = new PopupWindow(content, ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mMorePopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mMorePopupWindow.setOutsideTouchable(true);
            mMorePopupWindow.setTouchable(true);
            content.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            mShowMorePopupWindowWidth = content.getMeasuredWidth();
            mShowMorePopupWindowHeight = content.getMeasuredHeight();
            View parent = mMorePopupWindow.getContentView();
            TextView inport = parent.findViewById(R.id.inport);
            TextView export = parent.findViewById(R.id.export);
            inport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//导入
                    mMorePopupWindow.dismiss();
//                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);//意图：文件浏览器
//                    intent.setType("*/*");//无类型限制
//                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);//关键！多选参数
//                    intent.addCategory(Intent.CATEGORY_OPENABLE);
//                    startActivityForResult(intent, 99);
                    Permissions.verifyStoragePermissions(MainActivity.this);
                    showChooser();
                }
            });
            export.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//导出
                    mMorePopupWindow.dismiss();
                    String relatePath = "/Download/passwordFile_" + DateUtils.format(new Date()) + ".crypt";
                    boolean result = FileUtils.exportPasswordFile(MainActivity.this, relatePath);
                    if (result) {
                        Snackbar.make(clickView, "已导出密码列表加密文件到 手机存储" + relatePath, Snackbar.LENGTH_LONG)
                                .setAction("提示", null).show();
                    } else {
                        Snackbar.make(clickView, "导出失败，请重试", Snackbar.LENGTH_SHORT)
                                .setAction("提示", null).show();
                    }
                }
            });
        }

        if (mMorePopupWindow.isShowing()) {
            mMorePopupWindow.dismiss();
        } else {
            int heightMoreBtnView = clickView.getHeight();
            mMorePopupWindow.showAsDropDown(clickView, -mShowMorePopupWindowWidth,
                    -(mShowMorePopupWindowHeight + heightMoreBtnView) / 2);
        }
    }

    /**
     * 展示自定义文件夹浏览器
     */
    private void showChooser() {
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtil.createGetContentIntent();
        // Create the chooser Intent
        Intent intent = Intent.createChooser(target, "");
        try {
            int REQUEST_CODE = 1;
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }
    }

    /**
     * 获取返回值uri
     */
    @SuppressLint("NewApi")//minSdkVersion需要在15以上
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == 99){
            if (data.getData() != null) {
                //单次点击未使用多选的情况
                try {
                    Uri uri = data.getData();
                    Permissions.verifyStoragePermissions(MainActivity.this);
                    //TODO 对转换得到的真实路径path做相关处理
                    String path = FileUtils.getPath(getApplicationContext(), uri);
                    FileUtils.inportPasswordFile(getApplicationContext(), new File(path));
                    Snackbar.make(fabView, "导入成功", Snackbar.LENGTH_LONG)
                            .setAction("提示", null).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                //长按使用多选的情况
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    List<String> pathList = new ArrayList<>();
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        Uri uri = item.getUri();
                        //TODO 对获得的uri做解析，这部分在另一篇文章讲解
                        //String path = getPath(getApplicationContext(),uri);
                        //routers.add(path);
                        pathList.add(uri.toString());
                    }
                    //TODO 对转换得到的真实路径path做相关处理
                    System.out.println(pathList);
                }
            }
//            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;
    Fragment nav_host_fragment = null;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mMorePopupWindow != null && mMorePopupWindow.isShowing()) {
                mMorePopupWindow.dismiss();
                return true;
            }
            if (nav_host_fragment == null) {
                nav_host_fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            }
            Fragment childPrimaryNavigationFragment = nav_host_fragment.getChildFragmentManager().getPrimaryNavigationFragment();
            if (childPrimaryNavigationFragment instanceof HomeFragment) {//在主页
                if ((System.currentTimeMillis() - exitTime) < 2000) {
                    finish();//退出
                } else {
                    //弹出提示，可以有多种方式
                    Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
