package dev.dworks.apps.anexplorer.provider;

import static android.content.pm.PackageManager.GET_UNINSTALLED_PACKAGES;
import static android.content.pm.PackageManager.MATCH_UNINSTALLED_PACKAGES;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppsProviderMy {
    public final static class MyResult {
        public String documentId;
        public String displayName;
        public String summary;
        public long size;
//        public String mimeType;
        public long lastModified;
        public String path;
//        public int flags;
        public String packageName;
    };

    private static String getAppName(String packageName){
        String name = packageName;
        try {
            int start = packageName.lastIndexOf('.');
            name = start != -1 ? packageName.substring(start+1) : packageName;
            if(name.equalsIgnoreCase("android")){
                start = packageName.substring(0, start).lastIndexOf('.');
                name = start != -1 ? packageName.substring(start+1) : packageName;
            }
        } catch (Exception e) {
        }
        return capitalize(name);
    }

    private static String capitalize(String string){
        return TextUtils.isEmpty(string) ? string : Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

    private static String getAppVersion(String packageVersion){
        return  TextUtils.isEmpty(packageVersion) ? "" : "-" + packageVersion;
    }

    public static boolean hasNougat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }
    private static int getAppListFlag(){
        return /*Utils.*/hasNougat() ? MATCH_UNINSTALLED_PACKAGES : GET_UNINSTALLED_PACKAGES;
    }

    public static String getDocIdForApp(String rootId, String packageName){
        return rootId + packageName;
    }

    private static boolean isSystemApp(ApplicationInfo appInfo){
        return appInfo.flags != 0 && (appInfo.flags
                & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP | ApplicationInfo.FLAG_SYSTEM)) > 0;
    }

    private static boolean isCanLaunch(ApplicationInfo appInfo, PackageManager packageManager) {
        Intent intent = packageManager.getLaunchIntentForPackage(appInfo.packageName);
        if (intent != null) {
            if (isIntentAvailable(intent, packageManager)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isIntentAvailable(Intent intent, PackageManager packageManager) {
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list != null && list.size() > 0;
    }

    private static void includeAppFromPackage(List<MyResult> result, String docId, PackageInfo packageInfo,
                                       boolean showSystem, String query, PackageManager packageManager) {

        ApplicationInfo appInfo = packageInfo.applicationInfo;
        if(showSystem == isSystemApp(appInfo)){
            if (showSystem) {
                if (!isCanLaunch(appInfo, packageManager)) {
                    return;
                }
            }
            String displayName = "";
            final String packageName = packageInfo.packageName;
            String summary = packageName;
            displayName = packageName;
            String appName = null;
            try {
                appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            } catch (Throwable eee) {
                eee.printStackTrace();
                appName = getAppName(displayName) + getAppVersion(packageInfo.versionName);
            }

            if (null != query && !displayName.toLowerCase().contains(query)) {
                return;
            }
            final String path = appInfo.sourceDir;
//            final String mimeType = DocumentsContract.Document.MIME_TYPE_APK;
//
//            int flags = DocumentsContract.Document.FLAG_SUPPORTS_COPY | DocumentsContract.Document.FLAG_SUPPORTS_DELETE | DocumentsContract.Document.FLAG_SUPPORTS_THUMBNAIL;
//            if(isTelevision()) {
//                flags |= DocumentsContract.Document.FLAG_DIR_PREFERS_GRID;
//            }

            final long size = new File(appInfo.sourceDir).length();
            final long lastModified = packageInfo.lastUpdateTime;
            final MyResult row = new AppsProviderMy.MyResult();
            result.add(row);
            row.documentId = getDocIdForApp(docId, packageName);
            if (false) {
                row.displayName = getAppName(displayName) + getAppVersion(packageInfo.versionName);
            } else {
                //FIXME:use appname
                row.displayName = appName;
            }
            row.summary = summary;
            row.size = size;
//            row.mimeType = mimeType;
            row.lastModified = lastModified;
            row.path = path;
            row.packageName = packageName;
//            row.flags = flags;
        }
    }

    //getRunningAppProcessInfo
    public static List<MyResult> getUserApps(Context context, boolean isUserApp) {
        final List<MyResult> result = new ArrayList<MyResult>();
        if (context == null) return result;
        PackageManager packageManager = context.getPackageManager();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (isUserApp) {
            String docId = "user_apps:";
//            List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
//            List<ApplicationInfo> packages2 = new ArrayList<>();
//            for (ApplicationInfo packageInfo : packages) {
//                if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
//                    packages2.add(packageInfo);
//                }
//                Log.d("InstalledPackages", "Installed package :" + packageInfo.packageName);
//            }
            /*
            https://zhuanlan.zhihu.com/p/699413398
            android 14 need android.permission.QUERY_ALL_PACKAGES permission
             */

            List<PackageInfo> allAppList = packageManager.getInstalledPackages(getAppListFlag());
            for (PackageInfo packageInfo : allAppList) {
                includeAppFromPackage(result, docId, packageInfo, false, null, packageManager);
            }
        } else {
            String docId = "system_apps:";
            List<PackageInfo> allAppList = packageManager.getInstalledPackages( getAppListFlag());
            for (PackageInfo packageInfo : allAppList) {
                includeAppFromPackage(result, docId, packageInfo, true, null, packageManager);
            }
        }
        return result;
    }
}
