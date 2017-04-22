package com.wxk.erbiao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private LinearLayout mLedControl;
    private LinearLayout mTempControl;
    private LinearLayout mLightControl;
    private LinearLayout mSecurityControl;
    private LinearLayout mGasControl;
    private LinearLayout mVideoControl;
    private long exitTime;
    private MyHandler mHandler;

    private static class MyHandler extends Handler{
        WeakReference<MainActivity> weakReference;
        public MyHandler(MainActivity activity){
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = weakReference.get();
            if (activity != null){//如果activity仍然在弱引用中,执行...
                switch (msg.what){
                    case 0x18:
                        activity.showChangeIpDialog();
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        setListener();
        init();
    }


    @Override
    protected void bindViews() {
        mLedControl = (LinearLayout) findViewById(R.id.led_control);
        mTempControl = (LinearLayout) findViewById(R.id.temp_control);
        mLightControl = (LinearLayout) findViewById(R.id.light_control);
        mSecurityControl = (LinearLayout) findViewById(R.id.security_control);
        mGasControl = (LinearLayout) findViewById(R.id.gas_control);
        mVideoControl = (LinearLayout) findViewById(R.id.video_control);
        initToolbar("", R.id.toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        getToolbar().inflateMenu(R.menu.change_ip);
    }

    @Override
    protected void setListener() {
        mLedControl.setOnClickListener(this);
        mTempControl.setOnClickListener(this);
        mLightControl.setOnClickListener(this);
        mSecurityControl.setOnClickListener(this);
        mGasControl.setOnClickListener(this);
        mVideoControl.setOnClickListener(this);
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.change_ip:
                        showChangeIpDialog();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void init() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SocketService.INTENT_ACTION_NET_BREAK);
        intentFilter.addAction(SocketService.INTENT_ACTION_NET_ERROR);
        LocalBroadcastManager.getInstance(ErBiaoContext.getInstance().getContext())
                .registerReceiver(mReceiver, intentFilter);
        mHandler = new MyHandler(this);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SocketService.INTENT_ACTION_NET_ERROR)) {
                showCustomToast(R.mipmap.input_clean, " 网络连接失败！\r\n       请重试！   ", Toast.LENGTH_SHORT);
            } else if (intent.getAction().equals(SocketService.INTENT_ACTION_NET_BREAK)){
                showCustomToast(R.mipmap.input_clean, " 网络断开连接！\r\n       请重试！   ", Toast.LENGTH_SHORT);
            }
            mHandler.sendEmptyMessageDelayed(0x18, 2000);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(ErBiaoContext.getInstance().getContext())
                .unregisterReceiver(mReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.led_control:
                startActivityByClassName(LedControlActivity.class);
                break;
            case R.id.temp_control:
                startActivityByClassName(TempControlActivity.class);
                break;
            case R.id.light_control:
                startActivityByClassName(LightControlActivity.class);
                break;
            case R.id.security_control:
                startActivityByClassName(SecurityControlActivity.class);
                break;
            case R.id.gas_control:
                startActivityByClassName(GasControlActivity.class);
                break;
            case R.id.video_control:
                startActivityByClassName(VideoControlActivity.class);
                break;
            default:
                break;
        }
    }

    private void showChangeIpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_change_ip, null);
        final EditText editText = (EditText) view.findViewById(R.id.edit_text_ip);
        String saveIp = LebronPreference.getInstance().getChangedIp();
        if ("".equals(saveIp)) {
            saveIp = getResources().getString(R.string.ip);
        }
        editText.setText(saveIp);
        //移动光标到最后
        editText.setSelection(saveIp.length());
        //加载布局View到当前对话框
        builder.setView(view);
        builder.setTitle("更改ip");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "out ip: " + editText.getText().toString());
                if (isLegalIp(editText.getText().toString())) {
                    LebronPreference.getInstance().saveChangedIp(editText.getText().toString());
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 判断是否为合法IP
     *
     * @return the ip
     */
    public boolean isLegalIp(String addr) {
        if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))
        {
            return false;
        }
        /*
          判断IP格式和范围
         */
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        return mat.find();
    }

    /**
     * 如果两秒内按了两次返回键则退出程序,否则不会
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            } else {
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            exitTime = System.currentTimeMillis();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
