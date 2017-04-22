package com.wxk.erbiao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SecurityControlActivity extends BaseActivity {

    private TextView mSecurityBashRoom;
    private TextView mSecurityChicken;
    private TextView mSecurityLivingRoom;
    private TextView mSecurityBedRoom;

    private static Executor mExecutor = Executors.newSingleThreadExecutor();//单线程线程池
    private Runnable mTask;
    private PollingServer mPollingServer;
    private String[] mSecurityValues = new String[]{"", "", "", "", "", ""};
    private SocketService mSocketService;
    private ImageView mImgBashRoom;
    private ImageView mImgChicken;
    private ImageView mImgLivingRoom;
    private ImageView mImgBedRoom;

    private int colorBlue;
    private int colorGray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_control);
        bindViews();
        setListener();
        init();
    }

    @Override
    protected void bindViews() {
        mSecurityBashRoom = ((TextView) findViewById(R.id.security_bash_room));
        mSecurityChicken = ((TextView) findViewById(R.id.security_chicken));
        mSecurityLivingRoom = ((TextView) findViewById(R.id.security_living_room));
        mSecurityBedRoom = ((TextView) findViewById(R.id.security_bed_room));
        mImgBashRoom = (ImageView) findViewById(R.id.security_icon_bash_room);
        mImgChicken = (ImageView) findViewById(R.id.security_icon_chicken);
        mImgLivingRoom = (ImageView) findViewById(R.id.security_icon_living_room);
        mImgBedRoom = (ImageView) findViewById(R.id.security_icon_bed_room);
        initToolbar("", R.id.toolbar);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void init() {
        colorBlue = ContextCompat.getColor(this, R.color.colorLed);
        colorGray = ContextCompat.getColor(this, R.color.gray_security_no);
        mSocketService = ((MyApplication) getApplication()).getSocketService();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SocketService.INTENT_ACTION_SECURITY_DATA);
        LocalBroadcastManager.getInstance(ErBiaoContext.getInstance().getContext())
                .registerReceiver(mReceiver, intentFilter);
        Handler handler = new Handler();
        mTask = new Runnable() {
            @Override
            public void run() {
                mExecutor.execute(mSendCommandRunnable);
            }
        };
        //循环发送命令
        mPollingServer = new PollingServer(handler);
        mPollingServer.startPolling(mTask, 1000, true);
        //请求数据
        ((MyApplication) getApplication()).getSocketService().startGetDataFromServer();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("SocketService", "onReceive is running");
            if (intent.getAction().equals(SocketService.INTENT_ACTION_SECURITY_DATA)) {
                String valueString = intent.getStringExtra("security");
                if (valueString != null && valueString.length() > 0) {
                    mSecurityValues = valueString.split(",");
                    setSecurityStatus(mSecurityValues[1], mSecurityBashRoom, mImgBashRoom);
                    setSecurityStatus(mSecurityValues[2], mSecurityChicken, mImgChicken);
                    setSecurityStatus(mSecurityValues[3], mSecurityLivingRoom, mImgLivingRoom);
                    setSecurityStatus(mSecurityValues[4], mSecurityBedRoom, mImgBedRoom);
                }

            }
        }
    };

    private void setSecurityStatus(String status, TextView textView, ImageView imageView) {
        if (status != null && status.equals("1")) {
            textView.setText("有人");
            textView.setTextColor(colorBlue);
            imageView.setImageResource(R.mipmap.security_icon);
        } else if (status != null && status.equals("0")) {
            textView.setText("没人");
            textView.setTextColor(colorGray);
            imageView.setImageResource(R.mipmap.security_icon_no);
        } else {
            textView.setText("异常");
            textView.setTextColor(colorGray);
            imageView.setImageResource(R.mipmap.security_icon_no);
        }
    }

    private Runnable mSendCommandRunnable = new Runnable() {
        @Override
        public void run() {
            mSocketService.sendCommandToServer("AAAAA");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(ErBiaoContext.getInstance().getContext())
                .unregisterReceiver(mReceiver);
        mPollingServer.endPolling(mTask);
    }
}
