package com.wxk.erbiao;

import android.content.Context;

/**
 * Created by wuxia on 2017/4/20.
 * Contracts by wuxiangkun2015@163.com
 */

public class ErBiaoContext {
    private Context mContext;
    private static ErBiaoContext mInstance;

    private ErBiaoContext() {

    }

    public static ErBiaoContext getInstance() {
        if (mInstance == null) {
            mInstance = new ErBiaoContext();
        }
        return mInstance;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }
}
