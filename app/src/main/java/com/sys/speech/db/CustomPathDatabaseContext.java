package com.sys.speech.db;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

//https://www.cnblogs.com/zzcc/p/4107994.html
public class CustomPathDatabaseContext extends ContextWrapper {
    private String mDirPath;

    public CustomPathDatabaseContext(Context base, String dirPath) {
        super(base);
        this.mDirPath = dirPath;
    }

    @Override
    public File getDatabasePath(String name) {
        if (mDirPath == null) {
            return super.getDatabasePath(name);
        } else {
            File result = new File(mDirPath + File.separator + name);
            if (!result.getParentFile().exists()) {
                result.getParentFile().mkdirs();
            }
            return result;
        }
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory){
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler){
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name).getAbsolutePath(), factory, errorHandler);
    }
}
