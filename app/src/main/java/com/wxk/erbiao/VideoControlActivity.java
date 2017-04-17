package com.wxk.erbiao;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class VideoControlActivity extends BaseActivity {

    private long exitTime; //按下返回键计时
    private String urlString; //从MainActivity接收到的视频url地址
    private SurfaceView mSurfaceView; //SurfaceView用来显示视频帧的View
    private SurfaceHolder mSurfaceHolder; //用来实现SurfaceView的接口
    private int mWidth;
    private int mHeight;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    createDialog(); //弹出对话框
                    break;
            }
        }
    };
    //每帧图像的Bitmap
    private Bitmap bitmap;
    private boolean isSuccess;
    //存储路径
    String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
            + File.separator;

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

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void init() {
        Intent intent = getIntent();
        urlString = intent.getExtras().getString("url");

        mWidth = getWindowManager().getDefaultDisplay().getWidth();
        mHeight = getWindowManager().getDefaultDisplay().getHeight();

        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        //保持屏幕常亮
        mSurfaceView.setKeepScreenOn(true);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                new Thread(runnable).start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //get请求方式,默认也是,可以不设置
                    connection.setRequestMethod("GET");
                    //设置输入可行,默认也是,可以不设置
                    connection.setDoInput(true);
                    //返回码为200 说明状态正常
                    if (connection.getResponseCode() == 200) {
                        //由于图片是一帧一帧的,所以io流每次都得是新值,drawVideo()需要一直循环执行
                        InputStream is = connection.getInputStream();
                        //得到输入流得来的Bitmap
                        bitmap = BitmapFactory.decodeStream(is);
                        Canvas canvas = mSurfaceHolder.lockCanvas();
                        /*start---画矩形部分*/
                        Paint paint = new Paint();
                        paint.setColor(Color.RED);
                        /*end---画矩形部分*/
                        canvas.drawColor(Color.WHITE);
                        RectF rectF = new RectF(0, 0, mWidth, mHeight);
                        canvas.drawBitmap(bitmap, null, rectF, null);
                        canvas.drawRect(new RectF(20, 20, 300, 300), paint);
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                        isSuccess = true;
                    }
                    //关闭HttpURLConnection连接
                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                    isSuccess = false;
                    Message msg = handler.obtainMessage();
                    msg.what = 0;
                    handler.sendMessage(msg);
                    return;
                }
            }
        }
    };

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


    /**
     * 如果两秒内按了两次返回键则退出程序,否则不会
     *
     * @param keyCode KeyEvent.KEYCODE_BACK
     * @param event   KeyEvent
     * @return true or false
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(VideoControlActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            } else {
                handler.removeCallbacks(runnable);
            }
            exitTime = System.currentTimeMillis();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
