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
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GasControlActivity extends BaseActivity {

    private TextView mGasBashRoom;
    private TextView mGasChicken;
    private TextView mGasLivingRoom;
    private TextView mGasBedRoom;

    private static Executor mExecutor = Executors.newSingleThreadExecutor();//单线程线程池
    private Runnable mTask;
    private PollingServer mPollingServer;
    private String[] mGasValues = new String[]{"", "", "", "", "", ""};
    private SocketService mSocketService;
    private int colorBlue;
    private int colorGreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas_control);
        bindViews();
        setListener();
        init();
    }

    @Override
    protected void bindViews() {
        mGasBashRoom = ((TextView) findViewById(R.id.gas_bash_room));
        mGasChicken = ((TextView) findViewById(R.id.gas_chicken));
        mGasLivingRoom = ((TextView) findViewById(R.id.gas_living_room));
        mGasBedRoom = ((TextView) findViewById(R.id.gas_bed_room));
        initToolbar("", R.id.toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        intentFilter.addAction(SocketService.INTENT_ACTION_GAS_DATA);
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
            if (intent.getAction().equals(SocketService.INTENT_ACTION_GAS_DATA)) {
                String valueString = intent.getStringExtra("gas");
                if (valueString != null && valueString.length() > 0) {
                    mGasValues = valueString.split(",");
                    setGasStatus(mGasValues[1], mGasBashRoom);
                    setGasStatus(mGasValues[2], mGasChicken);
                    setGasStatus(mGasValues[3], mGasLivingRoom);
                    setGasStatus(mGasValues[4], mGasBedRoom);
                }
            }
        }
    };

    private void setGasStatus(String tempValue, TextView textView) {
        if (tempValue.equals("    0.00")) {
            textView.setText(getResources().getString(R.string.in_reading));
            textView.setTextColor(colorGreen);
        } else {
            textView.setText(tempValue);
            textView.setTextColor(colorBlue);
        }
    }

    private Runnable mSendCommandRunnable = new Runnable() {
        @Override
        public void run() {
            mSocketService.sendCommandToServer("GGGGG");
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
