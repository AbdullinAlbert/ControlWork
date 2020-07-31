package com.albertabdullin.controlwork;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class ListOfEmplVM extends AndroidViewModel {
    MutableLiveData<Map<Integer, Map<Integer, String>>>  employers;
    private Map<Integer, Map<Integer, String>> helperMapOfEmpVM = new TreeMap<>();

    public ListOfEmplVM(@NonNull Application application) {
        super(application);
    }
    public LiveData<Map<Integer, Map<Integer, String>>>  getLiveDataEmp() {
        if(employers == null) {
            employers = new MutableLiveData<>();
            loadEmployers();
        }
        return employers;
    }

    private void loadEmployers() {
        SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
        Map<Integer, Map<Integer, String>> localHelpMap = new TreeMap<>();
        int i = 0;
        try {
            SQLiteDatabase db = cwdbHelper.getReadableDatabase();
            Cursor cursor = db.query(CWDBHelper.TABLE_NAME_EMP,
                    new String[]{"_id", CWDBHelper.T_EMP_C_FIO},
                    null, null, null, null, null);
            if(cursor.moveToFirst()) {
                Map<Integer, String> m = new HashMap<>();
                m.put(cursor.getInt(0), cursor.getString(1));
                localHelpMap.put(i++, m);
            }
            while(cursor.moveToNext()) {
                Map<Integer, String> m = new HashMap<>();
                m.put(cursor.getInt(0), cursor.getString(1));
                localHelpMap.put(i++, m);
            }
            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(getApplication(), "Something wrong with DB", Toast.LENGTH_SHORT);
            toast.show();
        }
        employers.setValue(localHelpMap);
    }

    public Map<Integer, Map<Integer, String>> getHelperListOfEmp() {
        return helperMapOfEmpVM;
    }

    public void addEmployer(String s) {
        SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
        int idKey;
        try {
            SQLiteDatabase db = cwdbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(CWDBHelper.T_EMP_C_FIO, s);
            idKey = (int) db.insert(CWDBHelper.TABLE_NAME_EMP, null, cv);
            db.close();
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(getApplication(), "DB can't read data", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        Map<Integer, String> m = new HashMap<>();
        m.put(idKey, s);
        helperMapOfEmpVM.put(helperMapOfEmpVM.size(), m);
        employers.setValue(helperMapOfEmpVM);
    }
}
