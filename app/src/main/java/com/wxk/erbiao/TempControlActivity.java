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
import android.widget.TextView;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TempControlActivity extends BaseActivity {

    private TextView mTempBashRoom;
    private TextView mTempChicken;
    private TextView mTempLivingRoom;
    private TextView mTempBedRoom;
    private static Executor mExecutor = Executors.newSingleThreadExecutor();//单线程线程池
    private Runnable mTask;
    private PollingServer mPollingServer;
    private String[] mTempValues = new String[]{"", "", "", "", "", ""};
    private SocketService mSocketService;
    private int colorBlue;
    private int colorGreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_control);
        bindViews();
        setListener();
        init();
    }


    @Override
    protected void bindViews() {
        mTempBashRoom = (TextView) findViewById(R.id.temp_bash_room);
        mTempChicken = (TextView) findViewById(R.id.temp_chicken);
        mTempLivingRoom = (TextView) findViewById(R.id.temp_living_room);
        mTempBedRoom = (TextView) findViewById(R.id.temp_bed_room);
        initToolbar("", R.id.toolbar);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void init() {
        colorBlue = ContextCompat.getColor(this, R.color.colorLed);
        colorGreen = ContextCompat.getColor(this, R.color.color_std_green);
        mSocketService = ((MyApplication) getApplication()).getSocketService();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SocketService.INTENT_ACTION_TEMP_DATA);
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
            if (intent.getAction().equals(SocketService.INTENT_ACTION_TEMP_DATA)) {
                String valueString = intent.getStringExtra("temp");//tempStr = "TTTTT,+  0.0,+  0.0,+  0.0,+  0.0,"
                if (valueString != null && valueString.length() > 0) {
                    mTempValues = valueString.split(",");

                    setTempStatus(mTempValues[1], mTempBashRoom);
                    setTempStatus(mTempValues[2], mTempChicken);
                    setTempStatus(mTempValues[3], mTempLivingRoom);
                    setTempStatus(mTempValues[4], mTempBedRoom);
                }

            }
        }
    };

    private void setTempStatus(String tempValue, TextView textView) {
        if (tempValue.equals("+  0.0")) {
            textView.setText("读取中...");
            textView.setTextColor(colorGreen);
        } else {
            textView.setText(tempValue);
            textView.setTextColor(colorBlue);
        }
    }

    private Runnable mSendCommandRunnable = new Runnable() {
        @Override
        public void run() {
            mSocketService.sendCommandToServer("TTTTT");
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
