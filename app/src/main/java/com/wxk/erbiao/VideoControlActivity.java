package com.wxk.erbiao;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class VideoControlActivity extends BaseActivity {
    private static final String TAG = "VideoControlActivity";
    private String mVideoUrlString;     //视频地址
    private SurfaceView mSurfaceView; //SurfaceView用来显示视频帧的View
    private SurfaceHolder mSurfaceHolder; //用来实现SurfaceView的接口
    private int mWidth;
    private MyHandler mMyHandler;
    private SurfaceThread mSurfaceThread;
    private static class MyHandler extends Handler{
        WeakReference<VideoControlActivity> weakReference;
        public MyHandler(VideoControlActivity activity){
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            VideoControlActivity activity = weakReference.get();
            if (activity != null){//如果activity仍然在弱引用中,执行...
                switch (msg.what){
                    case 0:
                        activity.createDialog();
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //强制横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_video_control);
        bindViews();
        setListener();
        init();
    }

    @Override
    protected void bindViews() {
        mSurfaceView = ((SurfaceView) findViewById(R.id.surfaceView));
        mWidth = getWindowManager().getDefaultDisplay().getWidth(); //红米2A 720
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void init() {
        mMyHandler = new MyHandler(this);
        String ip = LebronPreference.getInstance().getChangedIp();
        mVideoUrlString = "http://" + ip + ":8080/?action=snapshot";
        initSurfaceView();
    }

    private void initSurfaceView() {
        mSurfaceView.setKeepScreenOn(true); //保持屏幕常亮
        mSurfaceHolder = mSurfaceView.getHolder();
        /*
          按下home键之后先是执行surfaceDestroyed方法,当前线程其实结束了
          重新打开app时会重新执行下面前两个个方法,由于之前的线程销毁了所以需要在surfaceCreated重新开启线程
         */
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                Log.i(TAG, "surfaceCreated: ");
                //如果网络没问题,开始刷新SurfaceView,线程一定要在这里创建.因为按下home键重新返回的时候还会再次执行该方法
                if (NetStatusUtils.isWiFiConnected(ErBiaoContext.getInstance().getContext())) { //判断是否是wifi网络
                    mSurfaceThread = new SurfaceThread(mSurfaceHolder, mVideoUrlString);
                    mSurfaceThread.isStartRefresh = true;
                    mSurfaceThread.start();
                } else {
                    createDialog();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                Log.i(TAG, "surfaceChanged: ");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                Log.i(TAG, "surfaceDestroyed: ");
                mSurfaceThread.isStartRefresh = false; //取消线程的刷新,退出线程
                try {
                    mSurfaceThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 自定义线程类,方便刷新
     */
    private class SurfaceThread extends Thread {
        final SurfaceHolder surfaceHolder;
        String videoUrl;
        boolean isStartRefresh;

        SurfaceThread(SurfaceHolder holder, String videoUrl) {
            this.surfaceHolder = holder;
            this.videoUrl = videoUrl;
        }

        @Override
        public void run() {
            super.run();
            URL url;
            HttpURLConnection connection;
            InputStream inputStream;
            RectF rectF = new RectF(0, 0, mWidth, 2 * mWidth / 3);
            Canvas canvas = null;
            while (isStartRefresh) { //由于是无状态的连接,所以需要一直不断的申请连接
                try {
                    url = new URL(this.videoUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    if (connection.getResponseCode() == 200) { //如果返回码是200表示状态正常
                        inputStream = connection.getInputStream(); //由于图片是一帧一帧的,所以io流每次都是新值
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream); //得到输入流来的Bitmap
                        synchronized (this.surfaceHolder) {
                            canvas = surfaceHolder.lockCanvas();
                            if (canvas != null) { //避免报空指针异常
                                canvas.drawColor(Color.WHITE);
                                canvas.drawBitmap(bitmap, null, rectF, null);
                            }
                        }
                    }
                    connection.disconnect();
                } catch (IOException e) {
                    isStartRefresh = false;
                    e.printStackTrace();
                    Message message = mMyHandler.obtainMessage();
                    message.what = 0;
                    mMyHandler.sendMessage(message);
                    Log.i(TAG, "connect is fail!");
                    return;
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas); //放在这里为了保证正常的提交画布,避免报空指针异常
                    }
                }
            }
        }
    }

    /**
     * 网络连接失败弹出警告框
     */
    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(VideoControlActivity.this);
        builder.setMessage("确认退出吗？");
        builder.setTitle("提示:网络连接失败!");

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                VideoControlActivity.this.finish();
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
}
