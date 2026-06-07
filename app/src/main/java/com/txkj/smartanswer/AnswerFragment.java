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

public class AnswerFragment extends
        Fragment {
//        Activity {
    private final static boolean USE_CACHE = true;
    private final static boolean NO_LOCATION_CHECK = true;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }

    //https://androidexample.com/file-chooser-with-camera-option-webview
    private static final int FILECHOOSER_RESULTCODE_ = 2888;
    private static final int FILECHOOSER_RESULTCODE_2 = 2889;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadMessage_2;
    private Uri mCapturedImageURI = null;

    //-----------------

    private final static boolean PAUSE_CLOSE_TIMER = false;
    public final static String EXTRA_TASK_CLICK = "EXTRA_TASK_CLICK";

    private final static boolean ENABLE_CALC_LISTEN_DIS = true;

    private final static boolean DEBUG_WS = false;
    private final static boolean ENABLE_WS = true;

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

    private final static String FILENAME = "https://cbcx-sj.jmtxkj.cn/aifront/index.html";
    private final static String FILENAME_ = "back/nav.html";
    public String getFILENAME() {
        return FILENAME;
    }
    public boolean isMain() {return true;}

    private final static String FILENAME_VIEW = "home/view.html";
    private final static String SIGN_FILE_PATH = "/txcxdri/sign";
    private final static String DOWNLOAD_FILE_PATH = "/txcxdri/download";
    private final static String UPDATE_FILE_PATH = "/txcxdri/update";

    private final static boolean D = false;
    private final static String TAG = "WebViewActivity";

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

        boolean isTaskClick = false;
        Intent intent = getActivity().getIntent();
        if (intent != null &&
                intent.getStringExtra(AnswerFragment.EXTRA_TASK_CLICK) != null &&
                intent.getStringExtra(AnswerFragment.EXTRA_TASK_CLICK).equals("1")) {
            isTaskClick = true;
        }

        if (isMain()) {
            if (isTaskClick) {

            }
        }

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

        if (USE_CACHE) {
            wv.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            if (stateStarted == 0) {
                wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            } else {
                wv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            }
        }

        //wv.getSettings().setDisplayZoomControls(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setSupportZoom(false);
        wv.getSettings().setBuiltInZoomControls(false);
        wv.getSettings().setLightTouchEnabled(true);


        //https://blog.csdn.net/zhanglianyu00/article/details/83660712
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wv.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }

        //for localStorage
        //https://blog.csdn.net/xhf_123/article/details/77893645
        wv.getSettings().setDomStorageEnabled(true);
        String appCachePath = getActivity().getApplication().getCacheDir().getAbsolutePath();
        wv.getSettings().setDatabaseEnabled(true);

        //for local file access
        //https://blog.csdn.net/a2241076850/article/details/52983939
        wv.getSettings().setAllowFileAccess(true);
        wv.getSettings().setAllowFileAccessFromFileURLs(true);
        wv.getSettings().setAllowUniversalAccessFromFileURLs(true);
        wv.getSettings().setAllowContentAccess(true);

        WebView.setWebContentsDebuggingEnabled(true);

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
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    if (D) {
                        Log.d(TAG, "onPageStarted");
                    }
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
                }

                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                }

                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }
            });
            wv.setWebChromeClient(new WebChromeClient() {
                public boolean onJsAlert(WebView view, String url, String message,
                                         final JsResult result) {
                    return true;
                };

                public boolean onJsConfirm(WebView view, String url,
                                           String message, final JsResult result) {
                    return true;
                };

                @Override
                public void onGeolocationPermissionsShowPrompt(String origin,
                                                               GeolocationPermissions.Callback callback) {
                    callback.invoke(origin, true, false);
                    super.onGeolocationPermissionsShowPrompt(origin, callback);
                }
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                }

                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
                }

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
                        Intent chooserIntent = Intent.createChooser(i, "File choose");
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

                }

                @Override
                public void onPermissionRequest(PermissionRequest request) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        request.grant(request.getResources());
                    }
                }
            });
        }
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
        if (urlType == null || urlType.equals("")) {
            if (stateStarted == 0 && IS_CLEAR_LOGIN) {
                //WebStorage.getInstance().deleteAllData();
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
            if (stateStarted == 0) {

            }
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putInt(STATE_STARTED, 1);
        }
    }

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
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist)  {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //Build.VERSION_CODES.R) {
                //https://www.jianshu.com/p/f53294992596
                sdDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            } else {
                sdDir = Environment.getExternalStorageDirectory();
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
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist)  {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //Build.VERSION_CODES.R) {
                //https://www.jianshu.com/p/f53294992596
                sdDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            } else {
                sdDir = Environment.getExternalStorageDirectory();
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
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist)  {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //Build.VERSION_CODES.R) {
                //https://www.jianshu.com/p/f53294992596
                sdDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            } else {
                sdDir = Environment.getExternalStorageDirectory();
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
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Choose camera or files"); //"Image Chooser");
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
            mPath = path;
            mFile = new File(path);
            mUrl = url;
            mIsHand = isHand;
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
            if (mIsHand) {
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
            boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
            if (sdCardExist)  {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //Build.VERSION_CODES.R) {
                    //https://www.jianshu.com/p/f53294992596
                    sdDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                } else {
                    sdDir = Environment.getExternalStorageDirectory();
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
                    Toast.makeText(getActivity(), "Open Failed", Toast.LENGTH_SHORT).show();
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
                byte[] data = new byte[1024 * 8];
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
    }















    public String getUpdatePath() {
        try {
            String sdPath = "/mnt/sdcard";
            File sdDir = null;
            boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
            if (sdCardExist)  {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //Build.VERSION_CODES.Q) {
                    //https://www.jianshu.com/p/f53294992596
                    sdDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                } else {
                    sdDir = Environment.getExternalStorageDirectory();
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


    private final static int REQUEST_CODE_MANAGE_UNKNOWN = 10086;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() {
        Uri packageURI = Uri.parse("package:" + getActivity().getPackageName());
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        startActivityForResult(intent, REQUEST_CODE_MANAGE_UNKNOWN);
    }

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

    private void setDriverUserId_uithread() {
        String userId = jsInterface.jsUserId;
        if (userId == null) {
            userId = "";
        }
    }

    private void setDriverUserToken_uithread() {
        String userToken = jsInterface.jsUserToken;
        if (userToken == null) {
            userToken = "";
        }
    }

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



    MediaPlayer mediaPlayer;
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

        if (PAUSE_CLOSE_TIMER) {
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
            handler.postDelayed(runnable, 2000);
        } else {
        }
    }

    public void stopHandleAndPostDelayed() {
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
