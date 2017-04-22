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

import com.suke.widget.SwitchButton;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LedControlActivity extends BaseActivity implements SwitchButton.OnCheckedChangeListener {

    private TextView mLedBashRoom;
    private TextView mLedChicken;
    private TextView mLedLivingRoom;
    private TextView mLedBedRoom;

    private static Executor mExecutor = Executors.newSingleThreadExecutor();//单线程线程池
    private Runnable mTask;
    private PollingServer mPollingServer;
    private String[] mLedValues = new String[]{"", "", "", "", "", ""};
    private SocketService mSocketService;
    private SwitchButton mSwitchBashRoom;
    private SwitchButton mSwitchChicken;
    private SwitchButton mSwitchLivingRoom;
    private SwitchButton mSwitchBedRoom;

    private int colorBlue;
    private int colorGray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_control);
        bindViews();
        setListener();
        init();
    }

    @Override
    protected void bindViews() {
        mLedBashRoom = ((TextView) findViewById(R.id.status_bash_room));
        mLedChicken = ((TextView) findViewById(R.id.status_chicken));
        mLedLivingRoom = ((TextView) findViewById(R.id.status_living_room));
        mLedBedRoom = ((TextView) findViewById(R.id.status_bed_room));

        mSwitchBashRoom = (SwitchButton) findViewById(R.id.switch_button_bash_room);
        mSwitchChicken = (SwitchButton) findViewById(R.id.switch_button_chicken);
        mSwitchLivingRoom = (SwitchButton) findViewById(R.id.switch_button_living_room);
        mSwitchBedRoom = (SwitchButton) findViewById(R.id.switch_button_bed_room);
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
        mSwitchBashRoom.setOnCheckedChangeListener(this);
        mSwitchChicken.setOnCheckedChangeListener(this);
        mSwitchLivingRoom.setOnCheckedChangeListener(this);
        mSwitchBedRoom.setOnCheckedChangeListener(this);
    }

    @Override
    protected void init() {
        colorBlue = ContextCompat.getColor(this, R.color.colorLed);
        colorGray = ContextCompat.getColor(this, R.color.gray_security_no);
        mSocketService = ((MyApplication) getApplication()).getSocketService();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SocketService.INTENT_ACTION_LED_DATA);
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
            if (intent.getAction().equals(SocketService.INTENT_ACTION_LED_DATA)) {
                String valueString = intent.getStringExtra("led");
                if (valueString != null && valueString.length() > 0) {
                    mLedValues = valueString.split(",");
                    setLedStatus(mLedValues[1], mLedBashRoom, mSwitchBashRoom);
                    setLedStatus(mLedValues[2], mLedChicken, mSwitchChicken);
                    setLedStatus(mLedValues[3], mLedLivingRoom, mSwitchLivingRoom);
                    setLedStatus(mLedValues[4], mLedBedRoom, mSwitchBedRoom);
                }
            }
        }
    };

    private void setLedStatus(String status, TextView textView, SwitchButton button) {
        if (status != null && status.equals("1")) {
            textView.setText("已打开");
            textView.setTextColor(colorBlue);
            button.setChecked(true);
        } else if (status != null && status.equals("0")) {
            textView.setText("已关闭");
            textView.setTextColor(colorGray);
            button.setChecked(false);
        } else if (status != null && status.equals("2")) {
            textView.setText(getResources().getString(R.string.in_reading));
            textView.setTextColor(ContextCompat.getColor(this, R.color.color_std_green));
            button.setChecked(false);
        }
    }

    private Runnable mSendCommandRunnable = new Runnable() {
        @Override
        public void run() {
            mSocketService.sendCommandToServer("DDDDD");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(ErBiaoContext.getInstance().getContext())
                .unregisterReceiver(mReceiver);
        mPollingServer.endPolling(mTask);
    }

    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
        if (view.equals(mSwitchBashRoom)) {
            if (view.isChecked()) {
                mLedBashRoom.setText("已打开");
                mLedBashRoom.setTextColor(colorBlue);
                new ControlThread("11D11").start();
            } else {
                mLedBashRoom.setText("已关闭");
                mLedBashRoom.setTextColor(colorGray);
                new ControlThread("11D00").start();
            }
        } else if (view.equals(mSwitchChicken)) {
            if (view.isChecked()) {
                mLedChicken.setText("已打开");
                mLedChicken.setTextColor(colorBlue);
                new ControlThread("22D11").start();
            } else {
                mLedChicken.setText("已关闭");
                mLedChicken.setTextColor(colorGray);
                new ControlThread("22D00").start();
            }
        } else if (view.equals(mSwitchLivingRoom)) {
            if (view.isChecked()) {
                mLedLivingRoom.setText("已打开");
                mLedLivingRoom.setTextColor(colorBlue);
                new ControlThread("33D11").start();
            } else {
                mLedLivingRoom.setText("已关闭");
                mLedLivingRoom.setTextColor(colorGray);
                new ControlThread("33D00").start();
            }
        } else if (view.equals(mSwitchBedRoom)) {
            if (view.isChecked()) {
                mLedBedRoom.setText("已打开");
                mLedBedRoom.setTextColor(colorBlue);
                new ControlThread("44D11").start();
            } else {
                mLedBedRoom.setText("已关闭");
                mLedBedRoom.setTextColor(colorGray);
                new ControlThread("44D00").start();
            }
        }
    }

    private class ControlThread extends Thread{
        private String command;
        public ControlThread(String command) {
            this.command = command;
        }
        @Override
        public void run() {
            super.run();
            mSocketService.sendCommandToServer(command);
        }
    }
}
