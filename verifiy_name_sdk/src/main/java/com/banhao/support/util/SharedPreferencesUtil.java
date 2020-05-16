package com.banhao.support.util;

import android.app.Activity;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by zhongpingye on 2020/3/16.
 */

public class SharedPreferencesUtil {
    public static String SP_NAME = "localtime";
    public static String SP_NAME_INJOY = "localtime_yester";
    public SharedPreferencesUtil() {
    }

    public static void setRecord(Activity activity, String key, String value) {
        SharedPreferences sp = activity.getSharedPreferences("local_history",
                MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    public static String getRecord(Activity activity, String key) {
        SharedPreferences sp = activity.getSharedPreferences("local_history",
                MODE_PRIVATE);
        return sp.getString(key, null);
    }

    public static Long getLongValue(Activity activity, String spName, String key, Long defValue) {
        SharedPreferences sp = activity.getSharedPreferences(spName,
                MODE_PRIVATE);
        return sp.getLong(key,defValue);
    }

    public static void putLongValue(Activity activity, String spName, String key, Long value) {
        SharedPreferences sp = activity.getSharedPreferences(spName,
                MODE_PRIVATE);
        sp.edit().putLong(key, value).apply();
    }

    public static int getIntValue(Activity activity, String spName, String key, int defValue) {
        SharedPreferences sp = activity.getSharedPreferences(spName,
                MODE_PRIVATE);
        return sp.getInt(key,defValue);
    }

    public static void putIntValue(Activity activity, String spName, String key, int value) {
        SharedPreferences sp = activity.getSharedPreferences(spName,
                MODE_PRIVATE);
        sp.edit().putInt(key, value).apply();
    }

    public static void removeData(Activity activity, String spName, String key){
        SharedPreferences sp = activity.getSharedPreferences(spName,
                MODE_PRIVATE);
        sp.edit().remove(key).apply();
    }
}
