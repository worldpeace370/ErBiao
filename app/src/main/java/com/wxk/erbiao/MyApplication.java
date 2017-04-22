package com.wxk.erbiao;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by wuxia on 2017/4/20.
 * Contracts by wuxiangkun2015@163.com
 */

public class MyApplication extends Application {
    private Handler mHandler;
    private ServiceConnection mServiceConnection = null;
    private SocketService mSocketService = null;
    private Intent mSocketServiceIntent;

    public SocketService getSocketService() {
        return mSocketService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BasePreference.getInstance().setContext(this);
        ErBiaoContext.getInstance().setContext(this);
        initSocketService();
    }

    private void initSocketService() {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mSocketService = ((SocketService.MyBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mSocketService = null;
            }
        };
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(mBindServiceRunnable, 1000);//1s后绑定服务
        mHandler.postDelayed(mStartServiceRunnable, 3000);//3s后启动服务, 开始Socket连接
    }

    private Runnable mBindServiceRunnable = new Runnable() {
        @Override
        public void run() {
            bindSocketService();
        }
    };

    private void bindSocketService() {
        mSocketServiceIntent = new Intent(this, SocketService.class);
        if (mServiceConnection != null) {
            bindService(mSocketServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private Runnable mStartServiceRunnable = new Runnable() {
        @Override
        public void run() {
            startSocketService();
        }
    };

    private void startSocketService() {
        if (mSocketService != null) {
            Log.i("SocketService", "startSocketService has run");
            mSocketService.startConnecting();
        } else {
            Log.i("SocketService", "mSocketService is null");
        }
    }

    // 程序终止的时候执行
    @Override
    public void onTerminate() {
        super.onTerminate();
        stopService(mSocketServiceIntent);//停止服务
        unbindService(mServiceConnection);//解绑服务
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
