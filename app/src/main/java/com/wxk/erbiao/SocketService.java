package com.wxk.erbiao;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Socket连接Mini2440的Service
 * Created by wuxia on 2017/4/20.
 * Contracts by wuxiangkun2015@163.com
 */

public class SocketService extends Service {
    private static final int SOCKET_PORT = 51706;
    //网络未连接成功
    public static final String INTENT_ACTION_NET_ERROR = "com.wxk.erbiao.SocketService.net_error";
    //网络连接成功后又断开了
    public static final String INTENT_ACTION_NET_BREAK = "com.wxk.erbiao.SocketService.net_break";
    //空数据
    public static final String INTENT_ACTION_EMPTY_DATA = "com.wxk.erbiao.SocketService.empty_data";

    //灯控数据广播
    public static final String INTENT_ACTION_LED_DATA = "com.wxk.erbiao.SocketService.led_data";

    //温度数据广播
    public static final String INTENT_ACTION_TEMP_DATA = "com.wxk.erbiao.SocketService.temp_data";

    //光强数据广播
    public static final String INTENT_ACTION_LIGHT_DATA = "com.wxk.erbiao.SocketService.light_data";

    //瓦斯数据广播
    public static final String INTENT_ACTION_GAS_DATA = "com.wxk.erbiao.SocketService.gas_data";

    //安防数据广播
    public static final String INTENT_ACTION_SECURITY_DATA = "com.wxk.erbiao.SocketService.security_data";


    private Socket mSocket = null;
    private static Executor mExecutor = Executors.newSingleThreadExecutor();//单线程线程池
    private PollingServer mPollingServer = new PollingServer(new Handler());

    private IBinder mBinder = null;
    private MyHandler mHandler;
    private boolean isConnectedSuccess = false; //网络连接成功标志位

    private static class MyHandler extends Handler {
        WeakReference<SocketService> weakReference;

        public MyHandler(SocketService service) {
            weakReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SocketService service = weakReference.get();
            if (service != null) {//如果activity仍然在弱引用中,执行...
                switch (msg.what) {
                    case 0x01:
                        service.isConnectedSuccess = true;
                        break;
                }
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    class MyBinder extends Binder {
        public SocketService getService() {
            return SocketService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new MyBinder();
        mHandler = new MyHandler(this);
    }

    public void startConnecting() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isConnectSuccess()) {
                    mHandler.sendEmptyMessage(0x01);
                } else {
                    Log.i("SocketService", "socket 连接失败...in startConnecting");
                }
            }
        }).start();
    }

    public void startGetDataFromServer() {
        if (isConnectedSuccess) {
            mPollingServer.startPolling(mGetDataTask, 300, true);
        }
    }

    public boolean isConnectSuccess() {
        InetAddress serverAddress = null;
        try {
            String serverIp = LebronPreference.getInstance().getChangedIp();
            if (serverIp.equals("")) {
                serverIp = "192.168.0.118";
            }
            serverAddress = InetAddress.getByName(serverIp);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            mSocket = new Socket(serverAddress, SOCKET_PORT);
            return !mSocket.isClosed();
        } catch (IOException e) {
            e.printStackTrace();
            //发送网络连接失败广播
            Log.i("SocketService", "socket 连接失败...in isConnectSuccess");
            Intent intent = new Intent(INTENT_ACTION_NET_ERROR);
            LocalBroadcastManager.getInstance(ErBiaoContext.getInstance()
                    .getContext()).sendBroadcast(intent);
        }
        return false;
    }

    /*
      跟Mini2440 socket请求数据任务
     */
    private Runnable mGetDataFromServerRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i("SocketService", "mGetDataFromServerRunnable...run...");
            if (mSocket != null && !mSocket.isClosed()) {
                try {
                    InputStream is = mSocket.getInputStream();
                    byte data[] = new byte[512];
                    int n = is.read(data);//阻塞读,直到有数据
                    if (n > 0) {
                        String val = new String(data);
                        if (val.length() > 0) {
                            //解析数据
                            switch (val.charAt(0)) {
                                case 'T':
                                    Intent intentTemp = new Intent(INTENT_ACTION_TEMP_DATA);
                                    intentTemp.putExtra("temp", val);
                                    LocalBroadcastManager.getInstance(ErBiaoContext.getInstance()
                                            .getContext()).sendBroadcast(intentTemp);
                                    Log.i("SocketService", "温度数据..." + val);
                                    break;
                                case 'L':
                                    Intent intentLight = new Intent(INTENT_ACTION_LIGHT_DATA);
                                    intentLight.putExtra("light", val);
                                    LocalBroadcastManager.getInstance(ErBiaoContext.getInstance()
                                            .getContext()).sendBroadcast(intentLight);
                                    Log.i("SocketService", "光强数据..." + val);
                                    break;
                                case 'G':
                                    Intent intentGas = new Intent(INTENT_ACTION_GAS_DATA);
                                    intentGas.putExtra("gas", val);
                                    LocalBroadcastManager.getInstance(ErBiaoContext.getInstance()
                                            .getContext()).sendBroadcast(intentGas);
                                    Log.i("SocketService", "瓦斯数据..." + val);
                                    break;
                                case 'A'://报警
                                    Intent intentSecurity = new Intent(INTENT_ACTION_SECURITY_DATA);
                                    intentSecurity.putExtra("security", val);
                                    LocalBroadcastManager.getInstance(ErBiaoContext.getInstance()
                                            .getContext()).sendBroadcast(intentSecurity);
                                    Log.i("SocketService", "安防数据..." + val);
                                    break;
                                case 'D'://灯状态
                                    Intent intentLed = new Intent(INTENT_ACTION_LED_DATA);
                                    intentLed.putExtra("led", val);
                                    LocalBroadcastManager.getInstance(ErBiaoContext.getInstance()
                                            .getContext()).sendBroadcast(intentLed);
                                    Log.i("SocketService", "灯控数据..." + val);
                                    break;
                            }
                        } else {
                            //发送空数据广播, 不知道给谁
                            Log.i("SocketService", "空数据...in mGetDataFromServerRunnable");
                            Intent intent = new Intent(INTENT_ACTION_EMPTY_DATA);
                            LocalBroadcastManager.getInstance(ErBiaoContext.getInstance()
                                    .getContext()).sendBroadcast(intent);
                        }

                    } else {
                        Log.i("SocketService", "is.read(data) <= 0");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //发送网络断开广播
                Log.i("SocketService", "socket 断开连接...in mGetDataFromServerRunnable");
                Intent intent = new Intent(INTENT_ACTION_NET_BREAK);
                LocalBroadcastManager.getInstance(ErBiaoContext.getInstance()
                        .getContext()).sendBroadcast(intent);
            }
        }
    };

    //从服务器轮询请求数据的Task, 通过PollingServer
    private Runnable mGetDataTask = new Runnable() {
        @Override
        public void run() {
            mExecutor.execute(mGetDataFromServerRunnable);
        }
    };

    @Override
    public boolean onUnbind(Intent intent) {
        if (mSocket != null && mSocket.isClosed()) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.onUnbind(intent);
    }

    public void sendCommandToServer(String command) {
        if (isConnectedSuccess && mSocket != null && !mSocket.isClosed()) {
            try {
                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(mSocket.getOutputStream())), true);
                out.println(command);
                out.flush();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocket != null && mSocket.isClosed()) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
