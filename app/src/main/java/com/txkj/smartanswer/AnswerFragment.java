package com.txkj.smartanswer;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.txkj.contentbrowser2.R;

import pub.devrel.easypermissions.EasyPermissions;

/*
2022-12-01 11:15:26.486  2309-2309  chromium                com.tongxun.trip.driverapp           I  [INFO:CONSOLE(1)] "getCurrentPosition() and watchPosition() no longer work on insecure origins. To use this feature, you should consider switching your application to a secure origin, such as HTTPS. See https://goo.gl/rStTGz for more details.", source:  (1)
2022-12-01 11:15:26.495  2309-2309  chromium                com.tongxun.trip.driverapp           I  [INFO:CONSOLE(60)] "Get sdkLocation failed.Geolocation permission denied.", source: http://192.168.0.114:8007/back/gaode_demo.html (60)
 */

//Andriod WebView 填坑小结（不定期更新）
//https://www.jianshu.com/p/40c767312103
//see jsInterface.scanCallback
public class AnswerFragment extends
        Fragment {
//        Activity {
    private final static boolean NO_LOCATION_CHECK = true;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }

    //https://androidexample.com/file-chooser-with-camera-option-webview
    //FIXME:20240920,摄像头
    private static final int FILECHOOSER_RESULTCODE_ = 2888;
    private static final int FILECHOOSER_RESULTCODE_2 = 2889;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadMessage_2;
    private Uri mCapturedImageURI = null;

    //-----------------

    private final static boolean PAUSE_CLOSE_TIMER = false; //切换后台时是否关闭secondPush熄屏推送和专车推送
    public final static String EXTRA_TASK_CLICK = "EXTRA_TASK_CLICK";

    private final static boolean ENABLE_CALC_LISTEN_DIS = true; //计算专车距离driDis(服务器端是listenDistance)
    //检查是否需要判断空

    private final static boolean DEBUG_WS_DIALOG = false;//弹出对话框调试ws长连接
    private final static boolean DEBUG_WS = false; //调试，如果正式发布设置为false
    private final static boolean ENABLE_WS = true; //启动websocket功能stomp
    //测试长连接的手机号，正式发布时设置为空
    // 这个号码不要用，实际司机的："13118859904"，可以用这个：18675041585
    private final static String DEBUG_USER_PHONE = DEBUG_WS ? "18675041585" : null;

    private ExecutorService newFixedThreadPool;

    private static final boolean IS_CLEAR_LOGIN = true;

    private static final String STATE_STARTED = "STATE_STARTED";

    private final static boolean IS_SET_ENABLE = false;

    public final static String EXTRA_URL = "EXTRA_URL";
    public final static String EXTRA_URL_TYPE = "EXTRA_URL_TYPE";
    public final static String EXTRA_REFRESH = "EXTRA_REFRESH";
    public final static String EXTRA_TO_REFRESH = "refresh";
    public final static int REQUEST_NEWTAB = 2001;
    private final static int REQUEST_PHOTO = 2002;

    private final static String FILEPATH_PREFIX = "home/";

    //	private final static String FILENAME = "index_test.html";
//    private final static String FILENAME = "home/home.html";
//    private final static String FILENAME = "http://42.192.233.179:8007/back/index.html";

    private final static String FILENAME = "https://cbcx-sj.jmtxkj.cn/aifront/index.html";
//      private final static String FILENAME = "http://47.107.122.182/aifront/index.html";
//      private final static String FILENAME = "https://tx-ai.jmtxkj.cn/aifront/index.html"; //49.51.193.85 | cbcx-sj:47.107.122.182
//    private final static String FILENAME = "http://www.jmtxkj.cn/test/uploader/index.html"; //测试上传文件
//    private final static String FILENAME = "https://cbcx-sj.jmtxkj.cn/h5/index.html"; //"https://www.jmtxkj.cn/h5/index.html";
    private final static String FILENAME_ = "back/nav.html";
    public String getFILENAME() {
        return FILENAME;
    }
    public boolean isMain() {return true;}

    //private final static String FILENAME = "http://192.168.0.101:8007/back/wsclient.html"; //测试声音
    //private final static String FILENAME = "http://192.168.0.101:8007/back/audio.html"; //测试声音
    //private final static String FILENAME = "https://mpreus.github.io/JS_playing_audio_files/"; //测试声音
//    private final static String FILENAME = "http://10.0.2.2:8007/back/index.html";


    //private final static String FILENAME = "home/up4_mod.html";
    //private final static String SIGN_FILE = "/mnt/sdcard/signatureBitmap.jpg";

    private final static String FILENAME_VIEW = "home/view.html";
    private final static String SIGN_FILE_PATH = "/txcxdri/sign";
    private final static String DOWNLOAD_FILE_PATH = "/txcxdri/download";
    private final static String UPDATE_FILE_PATH = "/txcxdri/update";

    private final static boolean D = false;
    private final static String TAG = "WebViewActivity";

    public final static String FILENAME_KEY = "com.iteye.weimingtom.jkanji.WebViewActivity.filename";

    private long m_lastbacktime;
    private MyWebView wv;
    private MyJavascriptInterface jsInterface;
    private boolean mWebViewDisable = false;
    private TextView tvDebug;
    private LinearLayout llTop;
    private Button btnLocation, btnClose;
    private TextView tvOut;

    //-------------------------------

    private LinearLayout bottomSheetLayout;
    private LinearLayout bottomSheetLayout2;
    private LinearLayout linearLayoutMask;
    private LinearLayout gestureLayout;
    protected ImageView bottomSheetArrowImageView;
    private Button swi_button;
    private EditText etYijian;
    private RelativeLayout swi_button_bar;
    private Spinner spinnerInput;
    private ArrayAdapter<String> spinnerInputAdapter;

    //---------------------------------

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Button mClearButton;
    private Button mSaveButton;
    private Button mHistoryButton;
    private Button mChooseButton;
    private Button mEraseButton;

    //---------------------------

    private static final int DIALOG_ID_UPLOAD = 202;
    private static final int DIALOG_ID_EXIT = 212;
    private static final int DIALOG_ID_DOWNLOAD = 222;
    private static final int DIALOG_ID_DOWNLOAD_FAIL = 224;

    //---------------------------

    //https://blog.csdn.net/soha_dong/article/details/88887429
    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    @Override
    public void onConfigurationChanged(Configuration _newConfig) {
        super.onConfigurationChanged(_newConfig);
    }


    private void onCreate2() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.webview, container, false);

        Intent intent2 = getActivity().getIntent();
        if (intent2 != null) {
            String APP_FILE = intent2.getStringExtra("APP_FILE");
            Log.e(TAG, "APP_FILE: " + APP_FILE);
        }

        boolean showTool = false;

        boolean isTaskClick = false; //点击任务栏
        Intent intent = getActivity().getIntent();
        if (intent != null &&
                intent.getStringExtra(AnswerFragment.EXTRA_TASK_CLICK) != null &&
                intent.getStringExtra(AnswerFragment.EXTRA_TASK_CLICK).equals("1")) {
            isTaskClick = true;
        }
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (isMain()) {
            if (isTaskClick) {

            }
        }

//        if (isMain()) {
//            if (isTaskClick || showTool) {
//                ActionBar bar = getSupportActionBar();
//                bar.setDisplayOptions(
//                        ActionBar.DISPLAY_USE_LOGO |
//                                ActionBar.DISPLAY_SHOW_HOME |
//                                //ActionBar.DISPLAY_HOME_AS_UP  |
//                                //ActionBar.DISPLAY_SHOW_CUSTOM |
//                                ActionBar.DISPLAY_SHOW_TITLE
//                );
//                bar.show();
//            } else {
//                ////blog.csdn.net/afufufufu/article/details/118112961
//                getSupportActionBar().hide();
//            }
//        } else {
//            getSupportActionBar().hide();
//        }
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        this.setContentView(R.layout.webview);

        {
            tvDebug = (TextView)view.findViewById(R.id.tvDebug);
            tvDebug.setVisibility(View.GONE);

            llTop = (LinearLayout)view.findViewById(R.id.llTop);
            llTop.setVisibility(View.GONE);
        }

        newFixedThreadPool = Executors.newFixedThreadPool(6);

        int stateStarted = 0;
        if (savedInstanceState != null) {
            stateStarted = savedInstanceState.getInt(STATE_STARTED, 0);
        }

        wv = (MyWebView) view.findViewById(R.id.webView1);
        onCreate2();

        wv.setNetworkAvailable(true);

        //FIXME:优化速度：解决webview调用 goBack() 返回上一页自动刷新闪白的情况
        if (stateStarted == 0) {
            wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        } else {
//        wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            wv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        //wv.getSettings().setDisplayZoomControls(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setSupportZoom(false);
        wv.getSettings().setBuiltInZoomControls(false);
        wv.getSettings().setLightTouchEnabled(true);


        //自动播放声音
        //https://blog.csdn.net/zhanglianyu00/article/details/83660712
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wv.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }

        //for localStorage
        //https://blog.csdn.net/xhf_123/article/details/77893645
        //wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setDomStorageEnabled(true);// 打开本地缓存提供JS调用,至关重要
//        wv.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);// 实现8倍缓存
        //wv.getSettings().setAllowFileAccess(true);
//        wv.getSettings().setAppCacheEnabled(true);
        String appCachePath = getActivity().getApplication().getCacheDir().getAbsolutePath();
//        wv.getSettings().setAppCachePath(appCachePath);
        wv.getSettings().setDatabaseEnabled(true);

        //for local file access
        //https://blog.csdn.net/a2241076850/article/details/52983939
        wv.getSettings().setAllowFileAccess(true);
        wv.getSettings().setAllowFileAccessFromFileURLs(true);
        wv.getSettings().setAllowUniversalAccessFromFileURLs(true);
        wv.getSettings().setAllowContentAccess(true);
//        wv.getSettings().setPluginState(WebSettings.PluginState.ON);

        //https://blog.csdn.net/yingaizhu/article/details/79300793
        WebView.setWebContentsDebuggingEnabled(true);

//        //webView 采用电脑浏览器的模式访问
//        //https://blog.csdn.net/qq_38357358/article/details/81204778
//        wv.getSettings().setUserAgentString(
//                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36");

        if (false) {
            wv.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    //https://blog.csdn.net/lufeifjnu/article/details/45815433
                    if (url != null && url.startsWith("newtab:")) {
                        Intent intent;
                        intent = new Intent(getActivity(), AnswerFragment.class);
                        intent.putExtra(EXTRA_URL, url.substring("newtab:".length()));
                        intent.putExtra(EXTRA_URL_TYPE, "newtab");
                        startActivityForResult(intent, REQUEST_NEWTAB);
                        return true;
                    } else if (url != null && url.startsWith("restab:")) {
                        Intent intent;
                        intent = new Intent(getActivity(), AnswerFragment.class);
                        intent.putExtra(EXTRA_URL, url.substring("restab:".length()));
                        intent.putExtra(EXTRA_URL_TYPE, "restab");
                        startActivityForResult(intent, REQUEST_NEWTAB);
                        return true;
                    } else if (url.startsWith("tel:")) {
                        //https://blog.51cto.com/u_10899894/5077718
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(url));
                        startActivity(intent);
                        return true;
                    } else {
                        view.loadUrl(url);
                        return true;
                    }
//        		Intent intent;
//        		intent = new Intent();
//        		intent.setAction(Intent.ACTION_VIEW);
//        		intent.setData(Uri.parse(url));
//        		try {
//        			startActivity(intent);
//        		} catch (Throwable e) {
//        			e.printStackTrace();
//        			Toast.makeText(getApplicationContext(),
//        				"找不到可用的应用程序", Toast.LENGTH_SHORT)
//        				.show();
//        		}
//            	return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    if (D) {
                        Log.d(TAG, "onPageStarted");
                    }
                    //wv.setVisibility(WebView.INVISIBLE);
                    wv.setVisibility(WebView.VISIBLE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (D) {
                        Log.d(TAG, "onPageFinished");
                    }
                    wv.setVisibility(WebView.VISIBLE);
                }
            });
        } else {
            wv.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    //https://blog.csdn.net/lufeifjnu/article/details/45815433
                    if (url != null && url.startsWith("newtab:")) {
                        Intent intent;
                        intent = new Intent(getActivity(), AnswerFragment.class);
                        intent.putExtra(EXTRA_URL, url.substring("newtab:".length()));
                        intent.putExtra(EXTRA_URL_TYPE, "newtab");
                        startActivityForResult(intent, REQUEST_NEWTAB);
                        return true;
                    } else if (url != null && url.startsWith("restab:")) {
                        Intent intent;
                        intent = new Intent(getActivity(), AnswerFragment.class);
                        intent.putExtra(EXTRA_URL, url.substring("restab:".length()));
                        intent.putExtra(EXTRA_URL_TYPE, "restab");
                        startActivityForResult(intent, REQUEST_NEWTAB);
                        return true;
                    } else if (url.startsWith("tel:")) {
                        //https://blog.51cto.com/u_10899894/5077718
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(url));
                        startActivity(intent);
                        return true;
                    } else {
                        view.loadUrl(url);
                        return true;
                    }
//        		Intent intent;
//        		intent = new Intent();
//        		intent.setAction(Intent.ACTION_VIEW);
//        		intent.setData(Uri.parse(url));
//        		try {
//        			startActivity(intent);
//        		} catch (Throwable e) {
//        			e.printStackTrace();
//        			Toast.makeText(getApplicationContext(),
//        				"找不到可用的应用程序", Toast.LENGTH_SHORT)
//        				.show();
//        		}
//            	return true;
                }

                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                }

                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }
            });
            wv.setWebChromeClient(new WebChromeClient() {
                // 处理javascript中的alert
                public boolean onJsAlert(WebView view, String url, String message,
                                         final JsResult result) {
                    return true;
                };

                // 处理javascript中的confirm
                public boolean onJsConfirm(WebView view, String url,
                                           String message, final JsResult result) {
                    return true;
                };

                // 处理定位权限请求
                @Override
                public void onGeolocationPermissionsShowPrompt(String origin,
                                                               GeolocationPermissions.Callback callback) {
                    callback.invoke(origin, true, false);
                    super.onGeolocationPermissionsShowPrompt(origin, callback);
                }
                @Override
                // 设置网页加载的进度条
                public void onProgressChanged(WebView view, int newProgress) {
//                    CrashReport.setJavascriptMonitor(wv, true); //是否注入bugly.js
//                    AnswerFragment.this.getWindow().setFeatureInt(
//                            Window.FEATURE_PROGRESS, newProgress * 100);
                    super.onProgressChanged(view, newProgress);
                }

                // 设置应用程序的标题title
                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
                }


                //-------------------------
                //FIXME:摄像头：20240920
                // openFileChooser for Android 3.0+

                //https://www.jb51.net/article/104199.htm
                //For Android >= 5.0
                public boolean onShowFileChooser(WebView webView,
                                                 ValueCallback<Uri[]> filePathCallback,
                                                 FileChooserParams fileChooserParams) {
                    mUploadMessage_2 = filePathCallback;
                    try{
                        // Create AndroidExampleFolder at sdcard
                        File imageStorageDir = new File(
                                Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_PICTURES)
                                , "AndroidExampleFolder");
                        if (!imageStorageDir.exists()) {
                            // Create AndroidExampleFolder at sdcard
                            imageStorageDir.mkdirs();
                        }
                        // Create camera captured image file path and name
                        File file = new File(
                                imageStorageDir + File.separator + "IMG_"
                                        + String.valueOf(System.currentTimeMillis())
                                        + ".jpg");
                        mCapturedImageURI = Uri.fromFile(file);
                        // Camera capture image intent
                        final Intent captureIntent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                        i.addCategory(Intent.CATEGORY_OPENABLE);
                        //i.setType("image/*");
                        i.setType("*/*");
                        // Create file chooser intent
                        Intent chooserIntent = Intent.createChooser(i, "File choose"); //"摄像头照片选择"
                        // Set camera intent to file chooser
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                                , new Parcelable[] { captureIntent });
                        // On select image call onActivityResult method of activity
                        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_2);
                    }
                    catch(Exception e){
                        Toast.makeText(getActivity().getBaseContext(), "Exception:"+e,
                                Toast.LENGTH_LONG).show();
                    }

                    return true;
                }

                public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType){
                    // Update message
                    mUploadMessage = uploadMsg;
                    try{
                        // Create AndroidExampleFolder at sdcard
                        File imageStorageDir = new File(
                                Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_PICTURES)
                                , "AndroidExampleFolder");
                        if (!imageStorageDir.exists()) {
                            // Create AndroidExampleFolder at sdcard
                            imageStorageDir.mkdirs();
                        }
                        // Create camera captured image file path and name
                        File file = new File(
                                imageStorageDir + File.separator + "IMG_"
                                        + String.valueOf(System.currentTimeMillis())
                                        + ".jpg");
                        mCapturedImageURI = Uri.fromFile(file);
                        // Camera capture image intent
                        final Intent captureIntent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                        i.addCategory(Intent.CATEGORY_OPENABLE);
                        i.setType("image/*");
                        // Create file chooser intent
                        Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
                        // Set camera intent to file chooser
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                                , new Parcelable[] { captureIntent });
                        // On select image call onActivityResult method of activity
                        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_);
                    }
                    catch(Exception e){
                        Toast.makeText(getActivity().getBaseContext(), "Exception:"+e,
                                Toast.LENGTH_LONG).show();
                    }

                }

                // openFileChooser for Android < 3.0
                public void openFileChooser(ValueCallback<Uri> uploadMsg){
                    openFileChooser(uploadMsg, "");
                }

                //openFileChooser for other Android versions
                public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                            String acceptType,
                                            String capture) {
                    openFileChooser(uploadMsg, acceptType);
                }

                // The webPage has 2 filechoosers and will send a
                // console message informing what action to perform,
                // taking a photo or updating the file
                public boolean onConsoleMessage(ConsoleMessage cm) {
                    onConsoleMessage(cm.message(), cm.lineNumber(), cm.sourceId());
                    return true;
                }

                public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                    //Log.d("androidruntime", "Show console messages, Used for debugging: " + message);

                }

                //---
                //录音提示permission deny

                //https://blog.csdn.net/SeeyouMT/article/details/123347526
                @Override
                public void onPermissionRequest(PermissionRequest request) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        //直接同意即可     deny是拒绝
                        request.grant(request.getResources());
                    }
                }
            });
        }
//		wv.setOnTouchListener(new WebView.OnTouchListener() {
//			@Override
//			public boolean onTouch(View arg0, MotionEvent arg1) {
//				return mWebViewDisable ? true : false;
//			}
//		});

        //FIXME:用其他方法解决蓝色全选问题，这种方法会导致无法复制粘贴
//		//禁用长按选择
//		wv.setOnLongClickListener(new WebView.OnLongClickListener() {
//			@Override
//			public boolean onLongClick(View arg0) {
//				return true;
//			}
//		});

        jsInterface = new MyJavascriptInterface(getActivity(), wv);
        wv.addJavascriptInterface(jsInterface, "JSInterface");
        //String filename = "index_old.html";
        //String filename = "web/index.html";
        //String filename = "home/index.html";
        String filename = getFILENAME();
        //Intent intent = this.getIntent();
        intent = getActivity().getIntent();
        String urlType = "";
        String url = "";
        if (intent != null) {
            url = intent.getStringExtra(EXTRA_URL);
            urlType = intent.getStringExtra(EXTRA_URL_TYPE);
            if (urlType != null && urlType.equals("newtab")) {
                if (url != null && url.length() > 0) {
                    filename = FILEPATH_PREFIX + url;
                }
            }
        }
        //20210924：自动注销
        //如果是第一次启动，清除localStorage登陆信息
        if (urlType == null || urlType.equals("")) {
            if (stateStarted == 0 && IS_CLEAR_LOGIN) {
                //WebStorage.getInstance().deleteAllData(); //清空WebView的localStorage
            }
        }


        if (urlType == null || urlType.equals("") || urlType.equals("newtab")) {
            if (filename != null && filename.startsWith("http")) {
                wv.loadUrl(filename);
            } else {
                wv.loadUrl("file:///android_asset/" + filename);
            }
        } else if (urlType.equals("restab")) {
            wv.loadUrl("file:///android_asset/" + FILENAME_VIEW);
            this.jsInterface.setMyURL(url);
        } else if (urlType.equals("restab_bad")) {
            InputStream ins = null;
            try {
                ins = getActivity().getAssets().open(FILENAME_VIEW);
                byte[] buffer = new byte[ins.available()];
                ins.read(buffer);
                String str = new String(buffer, "UTF-8");
                str.replace("{{url}}", url);
                wv.getSettings().setDefaultTextEncodingName("utf-8");
                wv.loadData(str, "text/html", "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (ins != null) {
                    try {
                        ins.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ins = null;
                }
            }
        } else {
            if (filename != null && filename.startsWith("http")) {
                wv.loadUrl(filename);
            } else {
                wv.loadUrl("file:///android_asset/" + filename);
            }
        }


        if (isMain()) {
            onCreateUpdateReceiver();
            if (urlType == null || urlType.equals("")) {
                if (stateStarted == 0) {
                    checkVersion();
                }
            }

            if (stateStarted == 0) {
                //checkPermissioin();
                requirePermission();
            }

            checkGps();
        }
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void tryConnectWS() {

    }

    private void checkGps() {
        LocationManager mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // 显示 gps 状态
        boolean gpsEnabled = false;

        /* 防止BITA平台兼容性测试时潜在的权限禁止问题导致测试失败 */
        try {
            gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            gpsEnabled = false;
        }
        //mGps.setText(String.format(getString(R.string.gps), gpsEnabled ? "开启" : "关闭"));
        if (!gpsEnabled) {
            //开wifi开gps：有经纬度
            //开wifi关gps：没有经纬度
            //关wifi开gps：有经纬度？
            new AlertDialog.Builder(getActivity())
                    .setTitle("温馨提示")
                    .setMessage("位置信息关闭，无法获取定位信息，请打开位置信息！")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            } catch (Throwable eee) {
                                eee.printStackTrace();
                            }
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putInt(STATE_STARTED, 1);

//			outState.putInt(STATE_SERVER_VERSION, serverVersion);
//			outState.putString(STATE_SERVER_VERSION_STR, serverVersionStr);
//			outState.putString(STATE_SERVER_VERSION_URL, serverVersionUrl);
//			outState.putString(STATE_SERVER_NOTE, serverNote);
        }
    }

//    @Override
//    public void onBackPressed() {
//        //if (bottomSheetLayout.getVisibility() == View.GONE) {
//        if (true) {
//            if (this.wv.canGoBack()) {
//                //https://article.itxueyuan.com/GXmMD
//                //https://zhuanlan.zhihu.com/p/27456323
//                this.wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//                this.wv.goBack();
//                return;
//            }
//            if (true) {
//                if (System.currentTimeMillis() - m_lastbacktime > 1000) {
//                    m_lastbacktime = System.currentTimeMillis();
//                    Toast.makeText(getActivity(), "Press one more time to exit", Toast.LENGTH_SHORT).show();
//                } else {
//                    if (true) {
//                        //FIXME:改成finish了
//                        super.onBackPressed();
//                        this.finish();
//                    } else {
//                        //see https://www.jianshu.com/p/321ae2d90e71
//                        //返回桌面
//                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
//                        homeIntent.addCategory(Intent.CATEGORY_HOME);
//                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(homeIntent);
//                    }
//                }
//            } else {
//                String url = null;
//                Intent intent = getIntent();
//                if (intent != null) {
//                    url = intent.getStringExtra(EXTRA_URL);
//                }
//                if (url != null && url.length() > 0) {
//                    super.onBackPressed();
//                    this.finish();
//                } else {
//                    if (true) {
//                        showDialog(DIALOG_ID_EXIT);
//                    } else {
//                        //see https://www.jianshu.com/p/321ae2d90e71
//                        //返回桌面
//                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
//                        homeIntent.addCategory(Intent.CATEGORY_HOME);
//                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(homeIntent);
//                    }
//                }
//            }
//        }
//    }

    //去掉在Android P上的提醒弹窗 （Detected problems with
    //see https://www.jianshu.com/p/f87fe39caf1d?tdsourcetag=s_pctim_aiomsg
    private void closeAndroidPDialog(){
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            @SuppressLint("SoonBlockedPrivateApi")
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mypost() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mypost_uithread();
            }
        });
    }

    public void setDriverUserId() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setDriverUserId_uithread();
            }
        });
    }
    public void setDriverUserToken() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setDriverUserToken_uithread();
            }
        });
    }
    public void setDriverUserPhone() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setDriverUserPhone_uithread();
            }
        });
    }
    public void clearDriverLogin() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clearDriverLogin_uithread();
            }
        });
    }

    public void uploadAndroidHand() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    public void uploadAndroidFile() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                xxonShowFileChooser();
            }
        });
    }

    public void finishAndroidAct() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().finish();
            }
        });
    }

    public void finishAndroidActRefresh() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent data = new Intent();
                data.putExtra(EXTRA_REFRESH, EXTRA_TO_REFRESH);
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
            }
        });
    }

    public String getSDPathImageFile() throws IOException {
        String sdPath = "/mnt/sdcard";
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist)  {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //Build.VERSION_CODES.R) {
                //https://www.jianshu.com/p/f53294992596
                sdDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);//获取跟目录
            } else {
                sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            }
            sdPath = sdDir.toString();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(sdPath + SIGN_FILE_PATH);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile.toString();
    }

    public String getSDPathImagePath() {
        String sdPath = "/mnt/sdcard";
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist)  {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //Build.VERSION_CODES.R) {
                //https://www.jianshu.com/p/f53294992596
                sdDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);//获取跟目录
            } else {
                sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            }
            sdPath = sdDir.toString();
        }
        File storageDir = new File(sdPath + SIGN_FILE_PATH);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        return storageDir.getAbsolutePath();
    }

    private String mCameraPhotoPath;
    private String mCameraPhotoPath2;
    private static final int INPUT_FILE_REQUEST_CODE = 1001;
    private static final int FILECHOOSER_RESULTCODE = 1002;
    private File createImageFile() throws IOException {
        String sdPath = "/mnt/sdcard";
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist)  {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //Build.VERSION_CODES.R) {
                //https://www.jianshu.com/p/f53294992596
                sdDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);//获取跟目录
            } else {
                sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            }
            sdPath = sdDir.toString();
        }
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PHOTO_" + timeStamp + "_";
        File storageDir = new File(sdPath + SIGN_FILE_PATH);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
//        File storageDir = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }
    // For Android 5.0
    public boolean xxonShowFileChooser() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                //takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, "Unable to create Image File", ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                mCameraPhotoPath2 = photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
            } else {
                takePictureIntent = null;
            }
        }
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");
        Intent[] intentArray;
        if (takePictureIntent != null) {
            intentArray = new Intent[]{takePictureIntent};
        } else {
            intentArray = new Intent[0];
        }
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "选择相机或文件"); //"Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //---------------
        //see https://androidexample.com/file-chooser-with-camera-option-webview
        if (requestCode==FILECHOOSER_RESULTCODE_2) {
            if (null == this.mUploadMessage_2) {
                return;
            }
            Uri[] result=new Uri[1];
            try{
                if (resultCode != Activity.RESULT_OK) {
                    result = null;
                } else {
                    // retrieve from the private variable if the intent is null
                    result[0] = (data == null ? mCapturedImageURI : data.getData());
                }
            }
            catch(Exception e)
            {
                Toast.makeText(getActivity().getApplicationContext(), "activity :"+e,
                        Toast.LENGTH_LONG).show();
            }
            mUploadMessage_2.onReceiveValue(result);
            mUploadMessage_2 = null;
            return;
        }
        if (requestCode==FILECHOOSER_RESULTCODE_) {
            if (null == this.mUploadMessage) {
                return;
            }
            Uri result=null;
            try{
                if (resultCode != Activity.RESULT_OK) {
                    result = null;
                } else {
                    // retrieve from the private variable if the intent is null
                    result = data == null ? mCapturedImageURI : data.getData();
                }
            }
            catch(Exception e)
            {
                Toast.makeText(getActivity().getApplicationContext(), "activity :"+e,
                        Toast.LENGTH_LONG).show();
            }
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
            return;
        }
        //end see https://androidexample.com/file-chooser-with-camera-option-webview
        //--------------------
        if (onActivityResult2(requestCode, resultCode, data)) {
            return;
        }
        if (requestCode == INPUT_FILE_REQUEST_CODE) {
            Uri[] results = null;
            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
//                        Toast.makeText(this, "photo " + mCameraPhotoPath2, Toast.LENGTH_SHORT).show();
                        jsInterface.scanCallback(mCameraPhotoPath2, "fileuploadadd", "", "");
                        uploadFile(mCameraPhotoPath2, jsInterface.getUrl(), false);
                        return;
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                        String path = RealPathFromUriUtils.getRealPathFromUri(getActivity(), data.getData(), "image");
                        //Toast.makeText(this, "file " + path, Toast.LENGTH_SHORT).show();
                        jsInterface.scanCallback(path, "fileuploadadd", "", "");
                        uploadFile(path, jsInterface.getUrl(), false);
                        return;
                    }
                }
            }
            //删除未上传图片
            //FIXME:定期检查文件大小为0的图片
            if (mCameraPhotoPath2 != null && mCameraPhotoPath2.length() > 0) {
                new File(mCameraPhotoPath2).delete();
            }
            return;
        }
        if (requestCode == REQUEST_NEWTAB) {
            if (resultCode == Activity.RESULT_OK) {
                String refresh = data.getStringExtra(AnswerFragment.EXTRA_REFRESH);
                if (refresh != null && refresh.equals(EXTRA_TO_REFRESH)) {
                    this.wv.reload();
                } else {
                    this.wv.loadUrl("javascript:onandroidback();");
                }
            } else {
                this.wv.loadUrl("javascript:onandroidback();");
            }
            return;
        }
        if (requestCode == REQUEST_PHOTO && resultCode == Activity.RESULT_OK) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }






















//    private AlertDialog dialog3;
//    @Override
//    protected Dialog onCreateDialog(int id) {
//        ProgressDialog dialog;
//        Dialog dialog2;
//        switch (id) {
//            case DIALOG_ID_DOWNLOAD:
//            case DIALOG_ID_UPLOAD:
//                dialog = new ProgressDialog(getActivity());
//                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                dialog.setCancelable(true);
//                dialog.setCanceledOnTouchOutside(false);
//                dialog.setIcon(R.mipmap.ic_launcher);
//                dialog.setTitle("提示");
//                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//
//                    }
//                });
//                if (id == DIALOG_ID_DOWNLOAD) {
//                    dialog.setMessage("下载中，请稍候...");
//                } else {
//                    dialog.setMessage("上传中，请稍候...");
//                }
//                return dialog;
//
//            case DIALOG_ID_EXIT:
//                dialog2 = new AlertDialog.Builder(getActivity())
//                        .setTitle("温馨提示")
//                        .setMessage("是否退出程序？")
//                        .setPositiveButton("确定",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        getActivity().finish();
//                                    }
//                                })
//                        .setNegativeButton("取消", null)
//                        .create();
//                return dialog2;
//
//            case DIALOG_ID_DOWNLOAD_FAIL:
//                dialog2 = new AlertDialog.Builder(getActivity())
//                        .setTitle("温馨提示")
//                        .setMessage("下载失败")
//                        .setPositiveButton("确定",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                    }
//                                })
//                        .create();
//                return dialog2;
//
//
//            case DIALOG_UPGRADE:
//                dialog3 = new AlertDialog.Builder(getActivity())
//                        .setTitle("软件升级")
//                        .setMessage("发现新版本,建议立即更新.")
//                        .setNeutralButton("用浏览器更新", null)
//                        .setPositiveButton("更新", null)
//                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .create();
//                dialog3.setCanceledOnTouchOutside(false);
//                dialog3.setCancelable(false);
//                dialog3.setOnShowListener(new OnShowListener() {
//                    @Override
//                    public void onShow(final DialogInterface arg0) {
//                        String strNote = "";
//                        if (serverNote != null && serverNote.length() > 0) {
//                            strNote = "\n" + "更新说明：" + serverNote;
//                        }
//                        dialog3.setMessage("发现新版本,建议立即更新.\n" +
//                                "版本号：" + (serverVersionStr != null ? serverVersionStr : "") +
//                                " (" + serverVersion + ")" +
//                                strNote
//                        );
//                        final Button b2 = dialog3.getButton(AlertDialog.BUTTON_NEUTRAL);
//                        b2.setOnClickListener(new OnClickListener() {
//                            @Override
//                            public void onClick(View arg0) {
//                                String url = serverVersionUrl;
//                                //浏览器打开，如果有pathPage，优先用这个
//                                if (serverPathPage != null && serverPathPage.length() > 0) {
//                                    url = serverPathPage;
//                                }
//                                Uri uri = Uri.parse(url);
//                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                                startActivity(intent);
//
//                                if (dialog3 != null && dialog3.isShowing()) {
//                                    dialog3.dismiss();
//                                }
//                            }
//                        });
//
//                        final Button b = dialog3.getButton(AlertDialog.BUTTON_POSITIVE);
//                        b.setOnClickListener(new OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                b.setEnabled(false);
////		                	String appName = getResources().getString(R.string.mtmobile__app_name);
////							File updateDir = new File(Environment.getExternalStorageDirectory() + "/mtmobile/update");
////							String updateFilePath = updateDir + "/" + appName + ".apk";
//                                mUpdatePath = getUpdatePath();
//                                if (mUpdatePath == null || mUpdatePath.length() == 0) {
//                                    Toast.makeText(getActivity(),
//                                            "下载目录不存在，请重启设备后重试，或使用浏览器更新", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    File updateParent = new File(mUpdatePath);
//                                    updateParent.mkdirs();
//                                    if (!updateParent.isDirectory()) {
//                                        Toast.makeText(getActivity(),
//                                                "下载目录不存在，请重启设备后重试，或使用浏览器更新", Toast.LENGTH_SHORT).show();
//                                    } else {
//                                        String url = serverVersionUrl;//UpdateService.DOWNLOAD_URL;
//                                        if (url == null || url.length() == 0) {
//                                            Toast.makeText(getActivity(),
//                                                    "下载URL为空", Toast.LENGTH_SHORT).show();
//                                        } else {
//                                            String docName = getUrlFileName(url);
//                                            File updateFileObj = new File(mUpdatePath, docName);
//                                            String updateFilePath = updateFileObj.getAbsolutePath();
//                                            if (updateFileObj.isFile() && updateFileObj.canRead()) {
//                                                if (false) {
//
//                                                } else {
//                                                    m_apk = updateFileObj;
//                                                    installProcess();
//                                                }
//                                            } else {
//
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        });
//                    }
//                });
//                dialog3.show();
//                break;
//        }
//        return super.onCreateDialog(id);
//    }

    public void uploadFile(String path, String url, boolean isHand) {
        if (Build.VERSION.SDK_INT < 11) {
            new UploadFileTask(path, url, isHand).execute();
        } else {
            new UploadFileTask(path, url, isHand).executeOnExecutor(newFixedThreadPool);
        }
    }

    public class UploadFileTask extends AsyncTask<Void, Void, Void> {
        private boolean isSuccess = false;
        private File mFile;
        private String mPath;
        private String mResultUrl = "", mFileName = "", mResult = "";
        private String mUrl;
        private boolean mIsHand;

        private String fileName = "";
        private String filePath = "";

        private final static boolean SHOW_DIALOG = false;

        public UploadFileTask(String path, String url, boolean isHand) {
            //mFile = new File("/mnt/sdcard2/mtmobile/document/1418714741953.cebx");
            mPath = path;
            mFile = new File(path);
            mUrl = url;
            mIsHand = isHand;
            //Toast.makeText(BookListActivity.this, "正在上传文件：" + mFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            if (SHOW_DIALOG) {
                getActivity().showDialog(DIALOG_ID_UPLOAD);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (SHOW_DIALOG) {
                try {
                    getActivity().dismissDialog(DIALOG_ID_UPLOAD);
                } catch (Throwable e) {

                }
            }
//			if (isSuccess) {
//				Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
//
//				if (false) {
//					Toast.makeText(MainActivity.this,
//								"fileName : " + fileName + ", filePath : " + filePath,
//								Toast.LENGTH_SHORT).show();
//				}
////				if (fileName != null && fileName.length() > 0 &&
////					filePath != null && filePath.length() > 0) {
////					new AttachFileTask(fileName, filePath, meetingID).execute();
////				}
//			} else {
//				Toast.makeText(MainActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
//			}

            if (mIsHand) {
                //手写文件删除
                mFile.delete();
            }

            if (isSuccess) {
                jsInterface.scanCallback(mPath, "fileuploaddone", mResultUrl, mFileName);
            } else {
                jsInterface.scanCallback(mPath, "fileuploadfail", "", "");
            }
        }
    }

    public void downloadFile(String urlString) {
        try {
            String sdPath = "/mnt/sdcard";
            File sdDir = null;
            boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
            if (sdCardExist)  {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //Build.VERSION_CODES.R) {
                    //https://www.jianshu.com/p/f53294992596
                    sdDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);//获取跟目录
                } else {
                    sdDir = Environment.getExternalStorageDirectory();//获取跟目录
                }
                sdPath = sdDir.toString();
            }
            String filename = MimeTypesUtil.getFileName(urlString);
            if (filename != null && filename.length() > 0) {
                String imageFileName = filename;
                File storageDir = new File(sdPath + DOWNLOAD_FILE_PATH);
                if (!storageDir.exists()) {
                    storageDir.mkdirs();
                }
                File imageFile = new File(storageDir, imageFileName);
                String path = imageFile.toString();

                if (Build.VERSION.SDK_INT < 11) {
                    new DownloadFileTask(path, urlString).execute();
                } else {
                    new DownloadFileTask(path, urlString).executeOnExecutor(newFixedThreadPool);
                }
            } else {
                getActivity().showDialog(DIALOG_ID_DOWNLOAD_FAIL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            getActivity().showDialog(DIALOG_ID_DOWNLOAD_FAIL);
        }
    }

    public class DownloadFileTask extends AsyncTask<Void, Void, Void> {
        private boolean isSuccess = false;
        private File mFile;
        private String mPath;
        private String mResultUrl = "", mFileName = "", mResult = "";
        private String mUrl;

        private String fileName = "";
        private String filePath = "";

        private final static boolean SHOW_DIALOG = true;

        public DownloadFileTask(String path, String url) {
            mPath = path;
            mFile = new File(path);
            mUrl = url;
            if (SHOW_DIALOG) {
                getActivity().showDialog(DIALOG_ID_DOWNLOAD);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                downloadPdfToFile(mFile, mUrl);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (SHOW_DIALOG) {
                try {
                    getActivity().dismissDialog(DIALOG_ID_DOWNLOAD);
                } catch (Throwable e) {

                }
            }

            if (mPath != null && mPath.length() > 0 && new File(mPath).exists()) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                //intent.setDataAndType(UriUtil.fromFile(MainActivity.this, new File(mPath)), "*/*");
                intent.setDataAndType(UriUtil.fromFile(getActivity(), new File(mPath)), MimeTypesUtil.getMimeType(mPath));
                UriUtil.prepare(intent);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "打开失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                getActivity().showDialog(DIALOG_ID_DOWNLOAD_FAIL);
            }
        }

        protected boolean downloadPdfToFile(File file, String urlString){
            FileOutputStream fos = null;
            try {
                URL url = new URL(urlString);
                URLConnection ucon = url.openConnection();
                ucon.setRequestProperty("Connection", "keep-alive");
                InputStream is = ucon.getInputStream();
//	            BufferedInputStream bis = new BufferedInputStream(is);
                //int current = 0;
                byte[] data = new byte[1024 * 8]; //默认一次读8K
                int count = 0;
                fos = new FileOutputStream(file);
                while ((count = is.read(data)) != -1){
                    fos.write(data, 0, count);
                }
                fos.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        }
    }

    @Override
    public void onDestroy() {
        try {
            stopHandleAndPostDelayed();
        } catch (Throwable eee) {
            eee.printStackTrace();
        }

        super.onDestroy();
        onDestroyUpdateReceiver();

    }





















    private MyReceiver receiver;
    private void onCreateUpdateReceiver() {
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_UPGRADE);
        prepareInstall(filter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            getActivity().registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            getActivity().registerReceiver(receiver, filter);
        }
    }
    private void onDestroyUpdateReceiver() {
        if (this.receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
    }


    private class MyReceiver extends BroadcastReceiver {
        private long lastTimer = 0;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (ACTION_UPGRADE.equals(intent.getAction())) {
                    int progress = intent.getIntExtra(EXTRA_UPGRADE_PROGRESS, 0);
                    int total = intent.getIntExtra(EXTRA_UPGRADE_TOTAL, 0);
                    int status = intent.getIntExtra(EXTRA_UPGRADE_STATUS, 0);
//                    getActivity().runOnUiThread(new UiUpdater(progress, total, status));
                } else if (ACTION_UPGRADE_INSTALL.equals(intent.getAction())) {
                    String path = intent.getStringExtra(EXTRA_UPGRADE_PATH);
                    installApk(new File(path));
                }
            }
        }
    }
//    private class UiUpdater implements Runnable {
//        private int m_Progess;
//        private int m_Total;
//        private int m_Status;
//
//        public UiUpdater(int progress, int total, int status) {
//            this.m_Progess = progress;
//            this.m_Total = total;
//            this.m_Status = status;
//        }
//
//        @Override
//        public void run() {
//            if (dialog3 != null) {
//                if (m_Progess < 100 && m_Status == UPGRADE_STATUS_OK) {
//                    dialog3.setMessage("下载中：" + m_Progess + "%");
//                } else if (m_Status == UPGRADE_STATUS_ERROR) {
//                    dialog3.setMessage("下载失败，请检查网络后重试\n(url=" + serverVersionUrl + ")");
//                } else {
//                    try {
//                        dialog3.dismiss();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }

    public static final String ACTION_UPGRADE = "ACTION_UPGRADE";
    public static final String EXTRA_UPGRADE_PROGRESS = "EXTRA_UPGRADE_PROGRESS";
    public static final String EXTRA_UPGRADE_TOTAL = "EXTRA_UPGRADE_TOTAL";
    public static final String EXTRA_UPGRADE_STATUS = "EXTRA_UPGRADE_STATUS";
    public static final int UPGRADE_STATUS_OK = 0;
    public static final int UPGRADE_STATUS_ERROR = 1;

    public String getUpdatePath() {
        try {
            String sdPath = "/mnt/sdcard";
            File sdDir = null;
            boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
            if (sdCardExist)  {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //Build.VERSION_CODES.Q) {
                    //https://www.jianshu.com/p/f53294992596
                    sdDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);//获取跟目录
                } else {
                    sdDir = Environment.getExternalStorageDirectory();//获取跟目录
                }
                sdPath = sdDir.toString();
            }
            File storageDir = new File(sdPath + UPDATE_FILE_PATH);
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
            String path = storageDir.toString();
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String mUpdatePath;
    private final static int DIALOG_UPGRADE = 1201;
    private int serverVersion = 0;
    private String serverVersionStr = null;
    private String serverVersionUrl = null;
    private String serverPathPage = null;
    private String serverNote = null;
    public void checkVersion() {
        if (Build.VERSION.SDK_INT < 11) {
            new CheckVersionTask().execute();
        } else {
            new CheckVersionTask().executeOnExecutor(newFixedThreadPool);
        }
    }
    public class CheckVersionTask extends AsyncTask<Void, Void, Void> {
        private boolean isSuccess = false;

        public CheckVersionTask() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            if (D) {
                Log.e(TAG, "MainActivity.CheckVersionTask begin CheckVersionTask ");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result_) {
            super.onPostExecute(result_);
            if (isSuccess) {
                if (serverVersion > 0 &&
                        serverVersionUrl != null &&
                        serverVersionUrl.length() > 0) {
                    afterCheckVersion();
                }
            } else {

            }
        }
    }

    public void afterCheckVersion() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getActivity().getApplicationContext().getPackageManager()
                    .getPackageInfo(getActivity().getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        int localVersion = packageInfo.versionCode;
        if (localVersion < serverVersion) {
            getActivity().showDialog(DIALOG_UPGRADE);
        }
    }

    public static String getUrlFileName(String url) {
        if (url != null && url.length() > 0) {
            int lastIndex = url.lastIndexOf("/");
            if (lastIndex != -1 && lastIndex < url.length() - 1) {
                String docName = url.substring(lastIndex + 1);
                return docName;
            }
        }
        return null;
    }








    //原文链接：https://blog.csdn.net/zuo_er_lyf/article/details/82659426
    //https://www.dev2qa.com/android-read-write-external-storage-file-example/
    private final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 14466;
    private final int REQUEST_CODE_MY_PERMISSION = 1;
    private void checkPermissioin(){
        // Check whether this app has write external storage permission or not.
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // If do not grant write external storage permission.
        if (writeExternalStoragePermission!= PackageManager.PERMISSION_GRANTED) {
            // Request user to grant write external storage permission.
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
        }
    }

//    @AfterPermissionGranted(1)
//    private void requirePermission() {
//        String[] permissions = {
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//        };
//        String[] permissionsForQ = {
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.ACCESS_BACKGROUND_LOCATION, //target为Q时，动态请求后台定位权限
//        };
//        if (Build.VERSION.SDK_INT >= 29 ? EasyPermissions.hasPermissions(this, permissionsForQ) :
//                EasyPermissions.hasPermissions(this, permissions)) {
//            //Toast.makeText(this, "权限OK", Toast.LENGTH_LONG).show();
//        } else {
//            EasyPermissions.requestPermissions(this, "温馨提示：需要权限，请允许授权",
//                    REQUEST_CODE_MY_PERMISSION, Build.VERSION.SDK_INT >= 29 ? permissionsForQ : permissions);
//        }
//    }

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 514;
    //@Override
    protected void requirePermission() {
        //super.onResume();
        if (NO_LOCATION_CHECK) {
            return;
        } else {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                            Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                            Manifest.permission.MODIFY_AUDIO_SETTINGS)
                            != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    }, REQUEST_CODE_LOCATION_PERMISSION);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    }, REQUEST_CODE_LOCATION_PERMISSION);
                }
            } else {
                //setappDirection();
            }
        }
    }

    private void checkPermissioin2() {
        // Check whether this app has write external storage permission or not.
        int requstInstallPacakgesPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.REQUEST_INSTALL_PACKAGES);
        // If do not grant write external storage permission.
        if (requstInstallPacakgesPermission != PackageManager.PERMISSION_GRANTED) {
            // Request user to grant write external storage permission.
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.REQUEST_INSTALL_PACKAGES}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
        }
    }



    //@Override
    public void onRequestPermissionsResult_old(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            int grantResultsLength = grantResults.length;
            if (grantResultsLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(getApplicationContext(), "You grant write external storage permission. Please click original button again to continue.", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(getApplicationContext(), "You denied write external storage permission.", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_CODE_MY_PERMISSION) {
            boolean isGrant = true;
            if (grantResults != null) {
                int grantResultsLength = grantResults.length;
//            int a = PackageManager.PERMISSION_DENIED;
//            int b = PackageManager.PERMISSION_GRANTED;
                for (int i = 0; i < grantResultsLength; ++i) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isGrant = false;
                        break;
                    }
                }
            }
//            if (grantResultsLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(getApplicationContext(),
//                        "You grant",
//                        Toast.LENGTH_LONG).show();
//            }
            if (!isGrant) {

            } else {

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            /*
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!statusCheck(this)) {
                    buildAlertMessageNoGps(this);
                } else {
                    //setappDirection();
                }
            } else {
                //Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            }*/


            boolean isGrant = true;
            if (grantResults != null) {
                int grantResultsLength = grantResults.length;
//            int a = PackageManager.PERMISSION_DENIED;
//            int b = PackageManager.PERMISSION_GRANTED;
                for (int i = 0; i < grantResultsLength; ++i) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isGrant = false;
                        break;
                    }
                }
            }
            if (!isGrant) {

            } else {
                checkOther();
            }
        }
    }

    private void checkOther() {
        try {
            //buildAlertMessageNoGps(MainActivity.this);
            //FIXME:这里尽量不要在安装后检查apk安装权限，因为当时还没有存储权限（会导致崩溃），
            //建议放在后面，等安装apk时再检查apk安装权限
            /*if (!installCheck(MainActivity.this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startInstallPermissionSettingActivity();
                }
            } else */

            if (!statusCheck(getActivity())) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            } else if (!locationCheck(getActivity())) {
                //定位服务，同上
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                //intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } else if (!storageCheck(getActivity())) {
                //startActivity(new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS));
                //http://m.studyofnet.com/147846706.html
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
//                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                    intent.setData(Uri.parse("package:" + getPackageName()));
//                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                } else {
                Toast.makeText(getActivity(), "没有打开存储权限", Toast.LENGTH_LONG).show();
//                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static boolean statusCheck(Context context) {
        if (NO_LOCATION_CHECK) {
            return true;
        } else {
            final LocationManager manager;
            manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                return false;
            } else {
                return true;
            }
        }
    }

    public static boolean locationCheck(Context context) {
        if (NO_LOCATION_CHECK) {
            return true;
        } else {
            return ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    public static boolean storageCheck(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean installCheck(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 判断是否有权限
            boolean haveInstallPermission = context.getPackageManager().canRequestPackageInstalls();
            if(!haveInstallPermission){
                //权限没有打开则提示用户去手动打开
                return false;
            }
        }
        return true;
    }

    public static void buildAlertMessageNoGps(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("您的位置信息或GPS似乎被禁用了，你是否允许打开位置信息或GPS?")
                .setCancelable(false)
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }





    //https://blog.csdn.net/changmu175/article/details/78906829
    private File m_apk;
    //安装应用的流程
    private void installProcess() {
        checkPermissioin2();
        boolean haveInstallPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //先获取是否有安装未知来源应用的权限
            haveInstallPermission = getActivity().getPackageManager().canRequestPackageInstalls();
            if (!haveInstallPermission) {//没有权限
                if (true) {
                    return;
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startInstallPermissionSettingActivity();
                    }
                }
            } else {
                installApk(m_apk);
            }
        } else {
            //有权限，开始安装应用程序
            installApk(m_apk);
        }
    }

    private final static int REQUEST_CODE_MANAGE_UNKNOWN = 10086;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() {
        Uri packageURI = Uri.parse("package:" + getActivity().getPackageName());
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        startActivityForResult(intent, REQUEST_CODE_MANAGE_UNKNOWN);
    }

    //	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
    protected boolean onActivityResult2(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_MANAGE_UNKNOWN) {
            installProcess();//再次执行安装流程，包含权限判等
            return true;
        }
        return  false;
    }

    //安装应用，但必须有权限才能执行
    private void installApk(File apk) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
            } else {//Android7.0之后获取uri要用contentProvider
                Uri uri = UriUtil.fromFile(getActivity(), apk);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                UriUtil.prepare(intent);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "安装apk失败", Toast.LENGTH_LONG).show();
        }
    }

    public static final String ACTION_UPGRADE_INSTALL = "ACTION_UPGRADE_INSTALL";
    public static final String EXTRA_UPGRADE_PATH = "EXTRA_UPGRADE_PATH";
    private void prepareInstall(IntentFilter filter) {
        filter.addAction(ACTION_UPGRADE_INSTALL);
    }





    //mypost用户界面
    private void mypost_uithread() {
        String options = jsInterface.getOptions();
        JSONObject obj = null;
        int page = 1;
        int pageSize = 5;
        try {
            obj = new JSONObject(options);
            page = obj.optInt("page", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String str = "";
    }

    //用户界面
    private void setDriverUserId_uithread() {
        String userId = jsInterface.jsUserId;
        if (userId == null) {
            userId = "";
        }
    }

    //用户界面
    private void setDriverUserToken_uithread() {
        String userToken = jsInterface.jsUserToken;
        if (userToken == null) {
            userToken = "";
        }
    }

    //用户界面
    private void setDriverUserPhone_uithread() {
        String userPhone = jsInterface.jsUserPhone;
        if (userPhone == null) {
            userPhone = "";
        }
        if (ENABLE_WS) {
            tryConnectWS();
        }
    }

    private void clearDriverLogin_uithread() {

    }




    //播放声音
    MediaPlayer mediaPlayer;
    //https://github.com/mardi1505/MediaPlayer-Example
    //https://github.com/saquibhafiz/MP3Player/tree/master/src/com/example/simpleplayer
    private void _playAudio(String media_url) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        try {
            mediaPlayer.setDataSource(media_url);
            mediaPlayer.prepare();
        }catch(IllegalArgumentException e) {

        }catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    private void _pauseAudio() {
        mediaPlayer.pause();
    }

    public void playAudio() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playAudio_uithread();
            }
        });
    }
    //用户界面
    private void playAudio_uithread() {
        String jsSoundUrl = jsInterface.jsSoundUrl;
        if (jsSoundUrl == null) {
            jsSoundUrl = "";
        }
        String path = null;
        if (!jsSoundUrl.startsWith("http")) {
            try {
                path = getUrlPath(wv.getUrl());
            } catch (Throwable eee) {
                eee.printStackTrace();
            }
        }
        if (path != null) {
            jsSoundUrl = path + jsSoundUrl;
        }
        _playAudio(jsSoundUrl);
    }

    public static String getUrlPath(String url) {
        if (url != null && url.length() > 0) {
            int lastIndex = url.lastIndexOf("/");
            if (lastIndex != -1 && lastIndex < url.length() - 1) {
                String path = url.substring(0, lastIndex + 1);
                return path;
            } else if (lastIndex != -1 && lastIndex == url.length() - 1) {
                return url;
            }
        }
        return null;
    }

    public void pauseAudio() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pauseAudio_uithread();
            }
        });
    }
    //用户界面
    private void pauseAudio_uithread() {
        _pauseAudio();
    }

    public void sendWebSocketMessage(String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sendWebSocketMessage_uithread(message);
            }
        });
    }

    private synchronized void sendWebSocketMessage_uithread(String message) {

    }

    @Override
    public void onResume() {
        super.onResume();

        if (PAUSE_CLOSE_TIMER) {
        }
        handleAndPostDelayed();

        if (ENABLE_WS) {
            try {
                tryConnectWS();
            } catch (Throwable eee) {
                eee.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (PAUSE_CLOSE_TIMER) { //切换后台时是否关闭这个
            stopHandleAndPostDelayed();
        }
    }

    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
            Log.d("HandleAndPostDelayed", "HandleAndPostDelayed");
            return super.sendMessageAtTime(msg, uptimeMillis);
        }
    };

    //https://blog.csdn.net/Twan1234/article/details/125050488
    public void handleAndPostDelayed() {
        Log.e(TAG, "myhandle");

        if (runnable != null &&
                System.currentTimeMillis() - runnable.lastTime > 2000 / 2) {
            // 开启定时器
            handler.postDelayed(runnable, 2000);
        } else {
            //ignore，定时器重叠，忽略
        }

        // 停止定时器
//        handler.removeCallbacks(runnable);
    }

    public void stopHandleAndPostDelayed() {
        // 停止定时器
        handler.removeCallbacks(runnable);
    }

    public class MyRunnable implements Runnable {
        public long lastTime = 0;
        @Override
        public void run() {

            updateTitle();
            handleAndPostDelayed();
            lastTime = System.currentTimeMillis();
        }
    }
    MyRunnable runnable = new MyRunnable();


    private void updateTitle() {

    }

    public class MsgModel {
        private String sender;
        private String content;

        public String getSender() {
            return sender;
        }
        public void setSender(String sender) {
            this.sender = sender;
        }
        public String getContent() {
            return content;
        }
        public void setContent(String content) {
            this.content = content;
        }
    }

    private final static int ID_HIDE = 1;
    private final static int ID_TEST_SCREEN_MSG = 100;
    private final static int ID_TEST_SCREEN_SPECIAL_MSG = 200;
    private final static int ID_TEST_DIST_TOOL = 300;
    private final static int ID_TEST_BALL = 400;
    private final static int ID_TEST_EXIT = 500;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    Handler mHandler = new Handler();

    //https://blog.csdn.net/wanghao200906/article/details/41309011
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        return true;
//    }

    public void openNav() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                openNav_uithread();
            }
        });
    }

    private void openNav_uithread() {

    }
}
