package com.wxk.erbiao;

import android.content.SharedPreferences;

/**
 * Created by wuxiangkun on 2017/2/9.
 * Contact way wuxiangkun2015@163.com
 */

public class LebronPreference extends BasePreference {
    private final static String PREFERENCE_KEY_CHANGED_IP = "changed_ip";
    private final static String PREFERENCE_KEY_HAS_LOGIN = "has_login";
    private static LebronPreference instance;

    public static LebronPreference getInstance() {
        if (instance == null) {
            instance = new LebronPreference();
        }
        return instance;
    }

    /**
     * 保存上次ip地址
     *
     * @param ip id地址
     */
    public void saveChangedIp(String ip) {
        checkPrefs();
        if (sPrefs != null) {
            SharedPreferences.Editor editor = sPrefs.edit();
            editor.putString(PREFERENCE_KEY_CHANGED_IP, ip);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public String getChangedIp() {
        checkPrefs();
        String ip = "";
        if (sPrefs != null) {
            ip = sPrefs.getString(PREFERENCE_KEY_CHANGED_IP, "");
        }
        return ip;
    }

    /**
     * 保存是否登录过
     *
     * @param isLogin hasLogin = true
     */
    public void saveHasLogin(boolean isLogin) {
        checkPrefs();
        if (sPrefs != null) {
            SharedPreferences.Editor editor = sPrefs.edit();
            editor.putBoolean(PREFERENCE_KEY_HAS_LOGIN, isLogin);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public boolean getHasLogin() {
        checkPrefs();
        boolean hasLogin = false;
        if (sPrefs != null) {
            hasLogin = sPrefs.getBoolean(PREFERENCE_KEY_HAS_LOGIN, false);
        }
        return hasLogin;
    }
}
