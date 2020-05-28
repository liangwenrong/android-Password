package com.lwr.password.ui.component;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lwr.password.R;

public class ToastA {
    private static Toast toast = null;

    public static void showToast(Context context, String msg, int duration) {
        if (toast == null) {
            LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflate.inflate(R.layout.view_toast, null);
            TextView tv = (TextView) v.findViewById(android.R.id.message);
            tv.setText(msg);
            toast = new Toast(context);
            toast.setView(v);
            // setGravity方法用于设置位置，此处为垂直居中
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

        }
        toast.setText(msg);
        toast.setDuration(duration);
        toast.show();
    }
}
