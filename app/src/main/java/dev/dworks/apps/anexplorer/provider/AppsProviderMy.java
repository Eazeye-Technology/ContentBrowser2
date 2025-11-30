package dev.dworks.apps.anexplorer.provider;

import static android.content.pm.PackageManager.GET_UNINSTALLED_PACKAGES;
import static android.content.pm.PackageManager.MATCH_UNINSTALLED_PACKAGES;

import static dev.dworks.apps.anexplorer.DocumentsApplication.isTelevision;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dev.dworks.apps.anexplorer.misc.Utils;
import dev.dworks.apps.anexplorer.model.DocumentsContract;

public class AppsProviderMy {
    public final static class MyResult {
        public String documentId;
        public String displayName;
        public String summary;
        public long size;
        public String mimeType;
        public long lastModified;
        public String path;
        public int flags;
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

    private static int getAppListFlag(){
        return Utils.hasNougat() ? MATCH_UNINSTALLED_PACKAGES : GET_UNINSTALLED_PACKAGES;
    }

    public static String getDocIdForApp(String rootId, String packageName){
        return rootId + packageName;
    }

    private static boolean isSystemApp(ApplicationInfo appInfo){
        return appInfo.flags != 0 && (appInfo.flags
                & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP | ApplicationInfo.FLAG_SYSTEM)) > 0;
    }

    private static void includeAppFromPackage(List<MyResult> result, String docId, PackageInfo packageInfo,
                                       boolean showSystem, String query, PackageManager packageManager) {

        ApplicationInfo appInfo = packageInfo.applicationInfo;
        if(showSystem == isSystemApp(appInfo)){
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
            final String mimeType = DocumentsContract.Document.MIME_TYPE_APK;

            int flags = DocumentsContract.Document.FLAG_SUPPORTS_COPY | DocumentsContract.Document.FLAG_SUPPORTS_DELETE | DocumentsContract.Document.FLAG_SUPPORTS_THUMBNAIL;
            if(isTelevision()) {
                flags |= DocumentsContract.Document.FLAG_DIR_PREFERS_GRID;
            }

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
            row.mimeType = mimeType;
            row.lastModified = lastModified;
            row.path = path;
            row.flags = flags;
        }
    }

    //getRunningAppProcessInfo
    public static List<MyResult> getUserApps(Context context, boolean isUserApp) {
        final List<MyResult> result = new ArrayList<MyResult>();
        PackageManager packageManager = context.getPackageManager();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (isUserApp) {
            String docId = "system_apps:";
            List<PackageInfo> allAppList = packageManager.getInstalledPackages(getAppListFlag());
            for (PackageInfo packageInfo : allAppList) {
                includeAppFromPackage(result, docId, packageInfo, false, null, packageManager);
            }
        } else {
            String docId = "user_apps:";
            List<PackageInfo> allAppList = packageManager.getInstalledPackages( getAppListFlag());
            for (PackageInfo packageInfo : allAppList) {
                includeAppFromPackage(result, docId, packageInfo, true, null, packageManager);
            }
        }
        return result;
    }
}
