package com.albertabdullin.controlwork.viewmodels;

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
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.albertabdullin.controlwork.db_of_app.CWDBHelper;
import com.albertabdullin.controlwork.recycler_views.selection_trackers.SimpleEntityForDB;

import java.util.ArrayList;
import java.util.List;

public class ListOfItemsVM extends AndroidViewModel {
    MutableLiveData<List<SimpleEntityForDB>>  entities;
    private List<SimpleEntityForDB> adapterListOfEmpVM = new ArrayList<>();

    public ListOfItemsVM(@NonNull Application application) {
        super(application);
    }
    public LiveData<List<SimpleEntityForDB>>  getLiveDataEmp() {
        if(entities == null) {
            entities = new MutableLiveData<>();
            loadEmployers();
        }
        return entities;
    }

    private void loadEmployers() {
        SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
        List<SimpleEntityForDB> localHelperList = new ArrayList<>();
        try {
            SQLiteDatabase db = cwdbHelper.getReadableDatabase();
            Cursor cursor = db.query(CWDBHelper.TABLE_NAME_EMP,
                    new String[]{"_id", CWDBHelper.T_EMP_C_FIO},
                    null, null, null, null, null);
            if(cursor.moveToFirst()) {
                do {
                    SimpleEntityForDB eDB = new SimpleEntityForDB();
                    eDB.setId(cursor.getInt(0));
                    eDB.setDescription(cursor.getString(1));
                    localHelperList.add(eDB);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(getApplication(), "Something wrong with DB", Toast.LENGTH_SHORT);
            toast.show();
        }
        entities.setValue(localHelperList);
    }

    public List<SimpleEntityForDB> getHelperListOfEntities() {
        return adapterListOfEmpVM;
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
        SimpleEntityForDB eDB = new SimpleEntityForDB();
        eDB.setId(idKey);
        eDB.setDescription(s);
        adapterListOfEmpVM.add(eDB);
        entities.setValue(adapterListOfEmpVM);
    }

    public static class ViewModelFactory implements ViewModelProvider.Factory {
        private Application mApplication;

        public ViewModelFactory(Application application) {
            mApplication = application;
        }

        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ListOfItemsVM(mApplication);
        }
    }
}
