package com.wxk.erbiao;

import android.os.Handler;

import java.util.HashMap;
import java.util.Map;

/**
 * 定时任务
 * Created by wuxia on 2017/4/20.
 * Contracts by wuxiangkun2015@163.com
 */

public class PollingServer {
    private Handler mHandler;
    private Map<Runnable, Runnable> mTaskMap = new HashMap<>();

    public PollingServer(Handler handler) {
        mHandler = handler;
    }

    public void startPolling(Runnable runnable, long interval) {
        startPolling(runnable, interval, false);
    }

    /**
     *
     * @param runnable 任务Runnable对象
     * @param interval delayMillis
     * @param runImmediately 是否立即执行
     */
    public void startPolling(final Runnable runnable, final long interval,
                             boolean runImmediately) {
        if (runImmediately) {//为true, 立即执行
            runnable.run();
        }
        Runnable task = mTaskMap.get(runnable);
        if (task == null) {
            task = new Runnable() { //创建Runnable对象, 加入到mTaskMap中, 通过post触发执行
                @Override
                public void run() {
                    runnable.run(); // 执行传入的runnable, 通过post触发
                    post(runnable, interval);
                }
            };
            mTaskMap.put(runnable, task);
        }
        post(runnable, interval);
    }

    public void endPolling(Runnable runnable) {
        if (mTaskMap.containsKey(runnable)) {
            mHandler.removeCallbacks(mTaskMap.get(runnable));
        }
    }

    private void post(Runnable runnable, long interval) {
        Runnable task = mTaskMap.get(runnable);
        mHandler.removeCallbacks(task);
        mHandler.postDelayed(task, interval);
    }

}
