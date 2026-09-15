package com.dseink;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {
    public final static String KEY_CONFIG = "key_config";
    public final static String KEY_PEN_WIDTH = "key_pen_width";

    public static int getInt(Context context, String key, int defValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_CONFIG, Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences != null) {
            int result = sharedPreferences.getInt(key, defValue);
            return result;
        } else {
            return defValue;
        }
    }

    public static void setInt(Context context, String key, int myVal) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_CONFIG, Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences != null) {
            SharedPreferences.Editor prefs = sharedPreferences.edit();
            prefs.putInt(key, myVal);
            prefs.apply();
        }
    }
}
