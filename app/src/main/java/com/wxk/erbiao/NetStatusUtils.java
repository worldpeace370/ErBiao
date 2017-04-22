package com.wxk.erbiao;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络连接状态的帮助类
 * Created by lebron on 16-11-2.
 * Contact by wuxiangkun2015@163.com
 */

public class NetStatusUtils {

    /**
     * 判断当前网络是否连接
     *
     * @param context getActivity()
     * @return true 表示连接成功否则你懂的
     */
    public static boolean isNetWorkConnected(Context context) {
        boolean result;
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        result = (info != null && info.isConnected());
        return result;
    }

    /**
     * 判断当前的网络连接是否为WiFi
     *
     * @param context getActivity()
     * @return true 表示是wifi连接否则你懂的
     */
    public static boolean isWiFiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiInfo.isConnected();
    }
}
