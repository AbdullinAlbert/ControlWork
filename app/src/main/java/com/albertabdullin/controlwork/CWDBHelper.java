package com.albertabdullin.controlwork;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CWDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ControlOfWork";
    //Info about first BD's table "Employers"
    public static final String TABLE_NAME_EMP = "Employer";
    public static final String T_EMP_C_FIO = "fio";
    private static final String SQL_CREATE_EMP_TABLE =
            "CREATE TABLE " + TABLE_NAME_EMP + " (" +
                    "_id" + " INTEGER PRIMARY KEY, " +
                    T_EMP_C_FIO + " TEXT NOT NULL);";
    private static final String SQL_DELETE_EMP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME_EMP + ";";

    public CWDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_EMP_TABLE);
        ContentValues employer = new ContentValues();
        employer.put(T_EMP_C_FIO, "Роберт Абдуллин");
        db.insert(TABLE_NAME_EMP, null, employer);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_EMP_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
