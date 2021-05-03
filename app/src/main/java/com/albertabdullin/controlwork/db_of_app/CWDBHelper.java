package com.albertabdullin.controlwork.db_of_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CWDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "ControlOfWork";
    //Info about first BD's table "Employers"
    public static final String TABLE_NAME_EMP = "Employer";
    public static final String T_EMP_C_FIO = "fio";
    private static final String SQL_CREATE_EMP_TABLE =
            "CREATE TABLE " + TABLE_NAME_EMP + " (" +
                    "_id" + " INTEGER PRIMARY KEY, " +
                    T_EMP_C_FIO + " TEXT);";
    private static final String SQL_DELETE_EMP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME_EMP + ";";
    //Info about first BD's table "Firm"
    public static final String TABLE_NAME_FIRM = "Firm";
    public static final String T_FIRM_C_DESCRIPTION = "description";
    private static final String SQL_CREATE_FIRM_TABLE =
            "CREATE TABLE " + TABLE_NAME_FIRM + " (" +
                    "_id" + " INTEGER PRIMARY KEY, " +
                    T_FIRM_C_DESCRIPTION + " TEXT);";
    private static final String SQL_DELETE_FIRM_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME_FIRM + ";";
    //Info about first BD's table "PlaceOfWork"
    public static final String TABLE_NAME_PLACE_OF_WORK = "PlaceOfWork";
    public static final String T_PLACE_OF_WORK_C_DESCRIPTION = "description";
    private static final String SQL_CREATE_PLACE_OF_WORK_TABLE =
            "CREATE TABLE " + TABLE_NAME_PLACE_OF_WORK + " (" +
                    "_id" + " INTEGER PRIMARY KEY, " +
                    T_PLACE_OF_WORK_C_DESCRIPTION + " TEXT);";
    private static final String SQL_DELETE_PLACE_OF_WORK_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME_PLACE_OF_WORK + ";";
    //Info about first BD's table "TypeOfWork"
    public static final String TABLE_NAME_TYPE_OF_WORK = "TypeOfWork";
    public static final String T_TYPE_OF_WORK_C_DESCRIPTION = "description";
    private static final String SQL_CREATE_TYPE_OF_WORK_TABLE =
            "CREATE TABLE " + TABLE_NAME_TYPE_OF_WORK + " (" +
                    "_id" + " INTEGER PRIMARY KEY, " +
                    T_TYPE_OF_WORK_C_DESCRIPTION + " TEXT);";
    private static final String SQL_DELETE_TYPE_OF_WORK_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME_TYPE_OF_WORK + ";";
    //info about DB's table "Type Of Result"
    public static final String TABLE_NAME_RESULT_TYPE = "ResultType";
    public static final String T_RESULT_TYPE_C_RESULT_TYPE = "ResultType";
    private static final String SQL_CREATE_RESULT_TYPE_TABLE =
            "CREATE TABLE " + TABLE_NAME_RESULT_TYPE + " ("+
                    "_id" + " INTEGER PRIMARY KEY, " +
                    T_RESULT_TYPE_C_RESULT_TYPE + " TEXT);";
    //Info about first DB's table "ResultOfWork"
    public static final String TABLE_NAME_RESULT = "Result";
    public static final String T_RESULT_C_ID_EMPLOYER = "IDEmployer";
    public static final String T_RESULT_C_ID_FIRM = "IDFirm";
    public static final String T_RESULT_C_ID_POW = "IDPoW";
    public static final String T_RESULT_C_ID_TOW = "IDToW";
    public static final String T_RESULT_C_DATE = "Date";
    public static final String T_RESULT_C_VALUE = "Value";
    public static final String T_RESULT_C_NOTE = "Note";
    public static final String T_RESULT_C_RESULT_TYPE = "idResultType";
    private static final String SQL_CREATE_RESULT_TABLE =
            "CREATE TABLE " + TABLE_NAME_RESULT + " (" +
                    "_id" + " INTEGER PRIMARY KEY, " +
                    T_RESULT_C_ID_EMPLOYER + " INTEGER, " +
                    T_RESULT_C_ID_FIRM + " INTEGER, " +
                    T_RESULT_C_ID_POW + " INTEGER, " +
                    T_RESULT_C_ID_TOW + " INTEGER, " +
                    T_RESULT_C_DATE + " INTEGER, " +
                    T_RESULT_C_VALUE + " REAL, " +
                    T_RESULT_C_NOTE + " TEXT, " +
                    T_RESULT_C_RESULT_TYPE + " INTEGER);";
    private static final String SQL_DELETE_RESULT_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME_RESULT + ";";

    public CWDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_EMP_TABLE);
        db.execSQL(SQL_CREATE_FIRM_TABLE);
        db.execSQL(SQL_CREATE_PLACE_OF_WORK_TABLE);
        db.execSQL(SQL_CREATE_TYPE_OF_WORK_TABLE);
        db.execSQL(SQL_CREATE_RESULT_TABLE);
        db.execSQL(SQL_CREATE_RESULT_TYPE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            db.execSQL("ALTER TABLE " + TABLE_NAME_RESULT + " ADD COLUMN " + T_RESULT_C_RESULT_TYPE + " INTEGER;" );
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
