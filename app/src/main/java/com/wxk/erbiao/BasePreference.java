package com.wxk.erbiao;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

/**
 * 利用SharedPreference 进行存储的父类
 * Created by wuxiangkun on 2017/2/9.
 * Contact way wuxiangkun2015@163.com
 */
@SuppressLint("NewApi")
public class BasePreference {
    protected static SharedPreferences sPrefs;
    protected static BasePreference instance = new BasePreference();
    protected Context mContext;
    protected boolean mUseApply;

    public BasePreference() {
        mUseApply = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static BasePreference getInstance() {
        return instance;
    }

    public boolean isContextLive() {
        return mContext != null;
    }

    public void setContext(Context context) {
        instance.mContext = context;
        sPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    protected void checkPrefs() {
        if (sPrefs == null && mContext != null) {
            sPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        }
    }

    public void saveString(String key, String value) {
        checkPrefs();
        if (sPrefs != null) {
            SharedPreferences.Editor editor = sPrefs.edit();
            editor.putString(key, value);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public void saveInt(String key, int value) {
        checkPrefs();
        if (sPrefs != null) {
            SharedPreferences.Editor editor = sPrefs.edit();
            editor.putInt(key, value);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public void saveLong(String key, long value) {
        checkPrefs();
        if (sPrefs != null) {
            SharedPreferences.Editor editor = sPrefs.edit();
            editor.putLong(key, value);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public void saveBoolean(String key, boolean value) {
        checkPrefs();
        if (sPrefs != null) {
            SharedPreferences.Editor editor = sPrefs.edit();
            editor.putBoolean(key, value);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public int getInt(String key) {
        checkPrefs();
        if (sPrefs != null) {
            return sPrefs.getInt(key, 0);
        } else {
            return 0;
        }
    }

    public int getInt(String key, int defaultValue) {
        checkPrefs();
        if (sPrefs != null) {
            return sPrefs.getInt(key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    public String getString(String key) {
        checkPrefs();
        if (sPrefs != null) {
            return sPrefs.getString(key, "");
        } else {
            return "";
        }
    }

    public boolean getBoolean(String key) {
        checkPrefs();
        return sPrefs != null && sPrefs.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        checkPrefs();
        if (sPrefs != null) {
            return sPrefs.getBoolean(key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    public void deleteKey(String key) {
        checkPrefs();
        if (sPrefs != null) {
            SharedPreferences.Editor editor = sPrefs.edit();
            editor.remove(key);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }
}
