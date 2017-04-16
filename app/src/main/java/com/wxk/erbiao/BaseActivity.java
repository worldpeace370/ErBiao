package com.wxk.erbiao;

import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by wuxia on 2017/4/16.
 * Contracts by wuxiangkun2015@163.com
 */

public abstract class BaseActivity extends AppCompatActivity {
    /**
     * bind views
     */
    protected abstract void bindViews();

    /**
     * set listeners
     */
    protected abstract void setListener();

    /**
     * init data
     */
    protected abstract void init();

    public void showCustomToast(@DrawableRes int iconRes, String msg, int duration) {
        LayoutInflater inflater = this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_common_toast,
                (ViewGroup) this.findViewById(R.id.layout_toast));
        ImageView image = (ImageView) layout.findViewById(R.id.iv_icon);
        image.setImageResource(iconRes);
        TextView text = (TextView) layout.findViewById(R.id.tv_msg);
        text.setText(msg);
        Toast toast = new Toast(this.getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }

    public <T extends BaseActivity> void startActivityByClassName(Class<T> tClass) {
        Intent intent = new Intent(this, tClass);
        startActivity(intent);
    }
}
