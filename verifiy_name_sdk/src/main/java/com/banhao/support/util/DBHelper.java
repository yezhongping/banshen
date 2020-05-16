package com.banhao.support.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhongpingye on 2020/4/24.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DataYYY.db";
    public static final String USER_TABLE = "usersinfo";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //新建用户信息表
        db.execSQL("CREATE TABLE " + USER_TABLE + " ("
                + USERNAME + " TEXT PRIMARY KEY, "
                + PASSWORD + " TEXT) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("alter table " + USER_TABLE + " add column other string");
    }
}


