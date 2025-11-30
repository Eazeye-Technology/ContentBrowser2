package com.txkj.smartanswer;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import androidx.core.app.ActivityCompat;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

//see https://github.com/lzyzsd/JsBridge
//https://www.jianshu.com/p/49bc3349151b

/**
 * Created by juul on 8/18/16.
 */
public class MyJavascriptInterface {
    public final static String TAG = "MyJ";
    public final static boolean D = true;

    private Activity activity;
    private WebView mWebView;
    private String jsCallbackName;
    private String jsUrl;
    private String jsMyURL;
    private String jsOptions;
    private String jsEdittext;

    public String jsUserToken, jsUserId, jsUserPhone;
    public String jsSoundUrl;

    public String jsWayPoints;
    public String jsSimulator;
    public String jsLicNum; //车牌

    private boolean showEditText = true;

    public void setMyURL(String url) {
        this.jsMyURL = url;
    }

    public String getUrl() {
        return jsUrl != null ? jsUrl : "";
    }

    public String getOptions() {
        return jsOptions != null ? jsOptions : "";
    }

    public String getEdittext() {
        return jsEdittext != null ? jsEdittext : "";
    }

    public boolean getShowEditText() {
        return showEditText;
    }

    public MyJavascriptInterface(Activity activity, WebView webView) {
        this.activity = activity;
        mWebView = webView;
    }

    @JavascriptInterface
    public void uploadAndroidHand(String callbackName, String url, String options, String edittext) {
        jsCallbackName = callbackName;
        jsUrl = url;
        jsOptions = options;
        jsEdittext = edittext;

        if (jsOptions != null && jsOptions.contains("button=1")) {
            showEditText = false;
        } else {
            showEditText = true;
        }
//        if (jsOptions != null && jsOptions.contains("hand=1")) {
//            ((AnswerFragment) this.activity).uploadAndroidHand();
//        } else {
//            ((AnswerFragment) this.activity).uploadAndroidFile();
//        }
    }

    @JavascriptInterface
    public void uploadAndroidFile(String callbackName, String url) {
        jsCallbackName = callbackName;
        jsUrl = url;
//        ((AnswerFragment) this.activity).uploadAndroidFile();
    }

    @JavascriptInterface
    public String getAndroidURL() {
        return jsMyURL;
    }

    @JavascriptInterface
    public String getAndroidPackageName() {
        return getPackName(this.activity);
    }

    public static String getPackName(Context context) {
        return context.getPackageName(); //正式环境
    }

    @JavascriptInterface
    public void downloadAndroidFile(String url) {
//        ((AnswerFragment) this.activity).downloadFile(url);
    }

    public void scanCallback(String result, String eventName, String resultUrl, String resultName) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            try {
                result = URLEncoder.encode(result, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                result = "{}";
            }
            mWebView.loadUrl("javascript:" + jsCallbackName + "(null, "
                    + "\"" + result.toString() + "\", "
                    + "\"" + eventName.toString() + "\", "
                    + "\"" + resultUrl.toString() + "\", "
                    + "\"" + resultName.toString() + "\")");
        } else {
            try {
                result = URLEncoder.encode(result, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                result = "{}";
            }
            //https://blog.csdn.net/u013766357/article/details/52541971
            String jscmd = "" + jsCallbackName + "(null, "
                    + "\"" + result.toString() + "\", "
                    + "\"" + eventName.toString() + "\", "
                    + "\"" + resultUrl.toString() + "\", "
                    + "\"" + resultName.toString() + "\")";
            mWebView.evaluateJavascript(jscmd, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {

                }
            });
        }
        //mWebView.loadUrl("javascript:"+jsCallbackName+"(null, \""+result.toString()+"\", \""+eventName.toString()+"\", \""+resultUrl.toString()+"\")");
        System.out.println("found device: " + result.toString());
    }

    @JavascriptInterface
    public void finishAndroidAct() {
//        ((AnswerFragment) this.activity).finishAndroidAct();
    }

    @JavascriptInterface
    public void finishAndroidActRefresh() {
//        ((AnswerFragment) this.activity).finishAndroidActRefresh();
    }

    @JavascriptInterface
    public void mypost(String callbackName, String url, String options, String edittext) {
        jsCallbackName = callbackName;
        jsUrl = url;
        jsOptions = options;
        jsEdittext = edittext;

//        ((AnswerFragment) this.activity).mypost();
    }


    @JavascriptInterface
    public void setDriverUserId(String userId) {
        jsUserId = userId;

//        ((AnswerFragment) this.activity).setDriverUserId();
    }

    @JavascriptInterface
    public void setDriverUserToken(String userToken) {
        jsUserToken = userToken;

//        ((AnswerFragment) this.activity).setDriverUserToken();
    }

    @JavascriptInterface
    public void setDriverUserPhone(String userPhone) {
        jsUserPhone = userPhone;

//        ((AnswerFragment) this.activity).setDriverUserPhone();
    }

    @JavascriptInterface
    public void clearDriverLogin() {
//        ((AnswerFragment) this.activity).clearDriverLogin();
    }


    @JavascriptInterface
    public void playAudio(String soundUrl) {
        jsSoundUrl = soundUrl;

//        ((AnswerFragment) this.activity).playAudio();
    }

    @JavascriptInterface
    public void pauseAudio() {
//        ((AnswerFragment) this.activity).pauseAudio();
    }


    public String jsSendMsg;

    @JavascriptInterface
    public String getSendMsg() {
        return jsSendMsg;
    }

    //{"type":"number","data":1}
    public String jsOrdListen;
    public boolean isOrdListen = false;

    @JavascriptInterface
    public String saveOrdListen(String ordListen) {
        jsOrdListen = ordListen;
//        try {
//            Log.e(TAG, "saveOrdListen :" + ordListen);
//            Gson mGson = new GsonBuilder().create();
//            JsonObject jsonObject = mGson.fromJson(jsOrdListen, JsonObject.class);
//            if (jsonObject != null) {
//                String data = jsonObject.get("data") != null ?
//                        jsonObject.get("data").getAsString() : null;
//                if (data != null && data.equals("1")) {
//                    isOrdListen = true;
//                } else {
//                    isOrdListen = false;
//                }
//            }
//        } catch (Throwable eee) {
//            Log.e(TAG, "saveOrdListen : ", eee);
//        }
        return "";
    }

    @JavascriptInterface
    public void openNav(String wayPoints, String simulator) {
        jsWayPoints = wayPoints;
//        if (simulator != null && simulator.contains(",")) {
//            jsSimulator = simulator.split(",")[0];
//            jsLicNum = simulator.split(",")[1];
//        } else {
        jsSimulator = simulator;
        jsLicNum = null;
//        }

//        ((AnswerFragment) this.activity).openNav();
    }

    @JavascriptInterface
    public void openNav(String wayPoints, String simulator, String licNum) {
        jsWayPoints = wayPoints;
        jsSimulator = simulator;
        jsLicNum = licNum;

//        ((AnswerFragment) this.activity).openNav();
    }

    @JavascriptInterface
    public void openGPSSettings() {
        try {
            //https://www.cnblogs.com/waniu/p/3679571.html
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            //intent.setData(Uri.parse("package:" + getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            ((AnswerFragment) this.activity).startActivity(intent);
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
    }

    @JavascriptInterface
    public int isGPSGood() {
        if (false) {
            return 0;
        } else {
            // 显示 gps 状态
            boolean gpsEnabled = false;

            /* 防止BITA平台兼容性测试时潜在的权限禁止问题导致测试失败 */
            try {
                LocationManager mLocationManager = (LocationManager) this.activity.getSystemService(Context.LOCATION_SERVICE);
                gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception e) {
                gpsEnabled = false;
            }
            return gpsEnabled ? 1 : 0;
        }
    }

    //跳转到悬浮球
    @JavascriptInterface
    public void jumpToBallSetting() {

    }

    @JavascriptInterface
    public String getDriverUserPhone() {
        String phone = null;
        return phone;
    }

    /*
    android_id
    https://www.cnblogs.com/erdongsir/p/17148263.html
    https://www.cnblogs.com/chenKnowledgeConllection/p/17380960.html
    https://www.modb.pro/db/1753979807348117504
    https://blog.csdn.net/linxinfa/article/details/102910244
     */
    @JavascriptInterface //实际入口
    public String getDeviceMac() {
//        String mac = getDeviceMacAddressOri();
        String mac = getAndroidIdOri();
        return getUserTokenById(mac);
    }

    @JavascriptInterface
    public String getDeviceMacAddress() {
        String mac = getDeviceMacAddressOri();
        return getUserTokenById(mac);
    }

    @JavascriptInterface
    public String getDeviceMacAddressOri() {
        //https://blog.csdn.net/linxinfa/article/details/102910244
        WifiManager wifiManager = (WifiManager) activity.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "";
        }
        String mac = wifiInfo.getMacAddress();
        return mac;
    }

    @JavascriptInterface
    public String getDeviceId() {
        String mac = getDeviceIdOri();
        return getUserTokenById(mac);
    }
    @JavascriptInterface
    public String getDeviceIdOri() {
        try {
            TelephonyManager tm = (TelephonyManager) activity.getApplicationContext()
                    .getSystemService(Service.TELEPHONY_SERVICE);
            return tm.getDeviceId();
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        return "";
    }

    @JavascriptInterface
    public String getAndroidId() {
        String mac = getAndroidIdOri();
        return getUserTokenById(mac);
    }
    @JavascriptInterface
    public String getAndroidIdOri() {
        String ANDROID_ID = Settings.System.getString(
                activity.getContentResolver(), Settings.System.ANDROID_ID);
        return ANDROID_ID;
    }

    public final static boolean USE_XXTEA = true;
    public final static String XXTEA_KEY_PREFIX = "stk:";
    public final static String XXTEA_KEY_USER = "mychatgptsmartanswer";

    /*
     * 用户id转token
     */
    private static String getUserTokenById(String userId) {
        String code = "" + (userId != null ? userId : "") + "|" + System.currentTimeMillis();
        return XXTEA_KEY_PREFIX + XXTEA.Encrypt(code, XXTEA_KEY_USER);
    }
}
