package com.txkj.smartanswer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;

public class UriUtil {
    public static Uri fromFile(Context context, File mediaFile) {
        return FileProvider.getUriForFile(context.getApplicationContext(), context.getPackageName()+".fileprovider", mediaFile);
    }

    public static void prepare(Intent intent) {
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }
}
