package com.txkj.smartanswer;

import android.app.Application;
import android.content.Context;
import android.text.format.DateFormat;

import androidx.multidex.MultiDex;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 继承 Application, 用于在全局范围内保存 TencentGeofence.
 *
 * <p>
 * 注意, TencentGeofenceManager 不会将将当前的 TencentGeofence 持久化保存. 应用程序需要根据实际需求,
 * 使用数据库, 文件, SharedPreference 或 网络等方式自行保存 TencentGeofence.
 */
public class AnswerApplication extends Application {
//implements Configuration.Provider
    private final static boolean ENABLE_CRASH_HANDLER = false;

//    //https://blog.csdn.net/ange_li/article/details/122106182
//    @NonNull
//    @Override
//    public Configuration getWorkManagerConfiguration() {
//        return new Configuration.Builder()
//                .setMinimumLoggingLevel(Log.VERBOSE)
//                .build();
//    }

    private String lastTimeStr = "";
    public void setLastTimeStr() {
        lastTimeStr = DateFormat.format("kk:mm:ss", new Date()).toString();
    }
    public String getLastTimeStr() {
        return lastTimeStr;
    }
    private String enterTimeStr = "";
    public void setEnterTimeStr() {
        enterTimeStr = DateFormat.format("kk:mm:ss", new Date()).toString();
    }
    public String getEnterTimeStr() {
        return enterTimeStr;
    }

    private Map<String, String> navIdMap = new HashMap<String, String>();
    public void saveNavId(String key, String value) {
        navIdMap.put(key, value);
    }

    public String getNavId(String key) {
        return navIdMap.get(key);
    }

    //-------------

    private long lastLatNow;
    private Double lastLat;
    private Double lastLng;
    public Double getLastLat() {
        return lastLat;
    }
    public void setLastLat(Double lastLat) {
        this.lastLat = lastLat;
        this.lastLatNow = System.currentTimeMillis();
    }
    public Double getLastLng() {
        return lastLng;
    }
    public void setLastLng(Double lastLng) {
        this.lastLng = lastLng;
        this.lastLatNow = System.currentTimeMillis();
    }
    public long getLastLatNow() {
        return this.lastLatNow;
    }

    //-----------------

    private long localNow = 0L;
    private String now = "";
    private String minNow = "";
    public void setNow(String now) {
        this.now = now;
        if (this.minNow == null || this.minNow.equals("")) {
            if (this.now != null && !this.now.equals("")) {
                this.minNow = this.now;
            }
        }
        this.localNow = System.currentTimeMillis();
    }
    public String getNow() {
        return this.now;
    }
    public String getMinNow() {
        return this.minNow;
    }

    public String getMinNowStr() {
        try {
            Date date = new Date(Long.parseLong(this.minNow));
            //"yyyy-MM-dd HH:mm:ss"
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String fmDate = sdf.format(date);
            return fmDate;
        } catch (Throwable eee) {
            return this.minNow;
        }
    }

    public String getNowStr() {
        try {
            Date date = new Date(Long.parseLong(this.now));
            //"yyyy-MM-dd HH:mm:ss"
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String fmDate = sdf.format(date);
            return fmDate;
        } catch (Throwable eee) {
            return this.now;
        }
    }

    public boolean getNowGood() {
        try {
            if (false) {
                long delta = System.currentTimeMillis() - Long.parseLong(this.now);
                if (delta < 10 * 1000) { //> 5sec < 30sec
                    return true;
                }
            } else {
                long delta = System.currentTimeMillis() - this.localNow;
                if (delta < 10 * 1000) { //> 5sec < 30sec
                    return true;
                }
            }
        } catch (Throwable eee) {

        }
        return false;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 记录已触发的 TencentGeofence 事件
     */
    private static ArrayList<String> sEvents = new ArrayList<String>();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static ArrayList<String> getEvents() {
        return sEvents;
    }
}
