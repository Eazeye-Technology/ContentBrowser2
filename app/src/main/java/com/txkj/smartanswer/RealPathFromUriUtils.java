package com.txkj.smartanswer;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

//see https://github.com/j309999174/beibeiwuandroid/blob/5bfbf935cb540c023d8160c6147cd8464023f5bd/contactcard/src/main/java/io/rong/contactcard/RealPathFromUriUtils.java
public class RealPathFromUriUtils {
    public static String getRealPathFromBitmap(Context context, Bitmap bitmap) {
        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, null,null));
        String filePath = RealPathFromUriUtils.getRealPathFromUri(context,uri,"image");
        return filePath;
    }

    public static Uri getUriFromDrawableRes(Context context, int id) {
        Resources res=context.getResources();
        Bitmap bmp= BitmapFactory.decodeResource(res, id);
        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, null,null));
        return uri;
    }

    public static String getRealPathFromUri(Context context, Uri uri,String type) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= 19) { // api >= 19
            return getRealPathFromUriAboveApi19(context, uri,type);
        } else { // api < 19
            return getRealPathFromUriBelowAPI19(context, uri);
        }
    }

    private static String getRealPathFromUriBelowAPI19(Context context, Uri uri) {
        return getDataColumn(context, uri, null, null);
    }

    @SuppressLint("NewApi")
    private static String getRealPathFromUriAboveApi19(Context context, Uri uri,String type) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) {
                String id = documentId.split(":")[1];
                String selection;
                String[] selectionArgs = {id};
                if ("image".equals(type)){
                    selection = MediaStore.Images.Media._ID + "=?";
                    filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
                } else if ("audio".equals(type)) {
                    selection = MediaStore.Audio.Media._ID + "=?";
                    filePath = getDataColumn(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
                } else if ("video".equals(type)) {
                    selection = MediaStore.Video.Media._ID + "=?";
                    filePath = getDataColumn(context, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
                }

            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                filePath = getDataColumn(context, contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            filePath = getDataColumn(context, uri, null, null);
        } else if ("file".equals(uri.getScheme())) {
            filePath = uri.getPath();
        }
        return filePath;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;
        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
}
