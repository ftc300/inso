/*
 * Copyright (c) 2014.
 * This Project and its content is copyright of ftc
 * All rights reserved.
 */

package com.inshow.watch.android.provider;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;

/**
 *
 */
public class DatabaseContext extends ContextWrapper {
    /**
     * @param base
     */
    public DatabaseContext(Context base) {
        super(base);
    }

    /**
     * @param name DB名稱
     * @return
     */
    @Override
    public File getDatabasePath(String name) {
        return new File(getFilePath(),name);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name,int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
    }

    /**
     * 自定义路径
     * @return
     */
    private final String getFilePath() {
        String sd_path = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File sdFile = Environment.getExternalStorageDirectory();
            sd_path = sdFile.getPath() + File.separator + "inshow";
            File dirFile = new File(sd_path);
            if(!dirFile.exists()){
                dirFile.mkdir();
            }
        }
        return sd_path;
    }
}
