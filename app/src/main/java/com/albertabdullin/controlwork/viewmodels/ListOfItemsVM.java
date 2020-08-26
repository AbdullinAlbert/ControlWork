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

import com.albertabdullin.controlwork.db_of_app.CWDBHelper;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;

import java.util.ArrayList;
import java.util.List;

public class ListOfItemsVM extends AndroidViewModel {
    MutableLiveData<List<SimpleEntityForDB>>  entities;
    private List<SimpleEntityForDB> adapterListOfEntitiesVM = new ArrayList<>();
    private List<Integer> listOfDeletedPositions = new ArrayList<>();
    private int updatedPosition;

    public ListOfItemsVM(@NonNull Application application) {
        super(application);
    }
    public LiveData<List<SimpleEntityForDB>>  getLiveDataEmp() {
        if(entities == null) {
            entities = new MutableLiveData<>();
            loadItems();
        }
        return entities;
    }

    private void loadItems() {
        SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
        List<SimpleEntityForDB> localHelperList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = cwdbHelper.getReadableDatabase();
            cursor = db.query(CWDBHelper.TABLE_NAME_EMP,
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
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(getApplication(), "Something wrong with DB", Toast.LENGTH_SHORT);
            toast.show();
        } finally {
            if(cwdbHelper != null) cwdbHelper.close();
            if(cursor != null) cursor.close();
            if(db != null) db.close();
        }
        entities.setValue(localHelperList);
    }

    public List<SimpleEntityForDB> getAdapterListOfEntitiesVM() { return adapterListOfEntitiesVM; }

    public List<Integer> getListOfDeletedPositions() { return listOfDeletedPositions; }

    public void setUpdatedItemPosition(int i) { updatedPosition = i; }

    public int getUpdatedItemPosition() { return updatedPosition; }

    public void addItem(String s) {
        SQLiteOpenHelper cwdbHelper = null;
        SQLiteDatabase db = null;
        int idKey;
        try {
            cwdbHelper = new CWDBHelper(getApplication());
            db = cwdbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(CWDBHelper.T_EMP_C_FIO, s);
            idKey = (int) db.insert(CWDBHelper.TABLE_NAME_EMP, null, cv);
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(getApplication(), "DB can't write data", Toast.LENGTH_SHORT);
            toast.show();
            return;
        } finally {
            if(db != null) db.close();
            if(cwdbHelper != null) cwdbHelper.close();
        }
        SimpleEntityForDB eDB = new SimpleEntityForDB();
        eDB.setId(idKey);
        eDB.setDescription(s);
        List<SimpleEntityForDB> helperList = new ArrayList<>(adapterListOfEntitiesVM);
        helperList.add(eDB);
        entities.setValue(helperList);
    }

    private String makeWhereClause(int size) {
        StringBuilder sb = new StringBuilder();
        sb.append("_id in (");
        for(int i = 0; i < size-1; i ++) sb.append("?, ");
        sb.append("?)");
        return sb.toString();
    }

    public void deleteItem(List<SimpleEntityForDB> list)  {
        int count;
        List<SimpleEntityForDB> helperList = new ArrayList<>(adapterListOfEntitiesVM);
        List<Integer> listOfID = new ArrayList<>();
        if(listOfDeletedPositions.size() > 0) listOfDeletedPositions.clear();
        for(int i = 0; i < list.size(); i++) {
            listOfDeletedPositions.add(adapterListOfEntitiesVM.indexOf(list.get(i)));
            helperList.remove(list.get(i));
            listOfID.add(list.get(i).getID());
        }
        String[] arguments = listOfID.toString().replaceAll("\\[|\\]", "").split(", ");
        String whereClause = makeWhereClause(listOfID.size());
        SQLiteOpenHelper cwdbHelper = null;
        SQLiteDatabase db = null;
        try {
            cwdbHelper = new CWDBHelper(getApplication());
            db = cwdbHelper.getWritableDatabase();
            count = db.delete(CWDBHelper.TABLE_NAME_EMP, whereClause, arguments);
        }catch (SQLiteException e) {
            Toast toast = Toast.makeText(getApplication(), "Something went wrong: DB can't delete data", Toast.LENGTH_SHORT);
            toast.show();
            return;
        } finally {
            if(db != null) db.close();
            if(cwdbHelper != null) cwdbHelper.close();
        }
        if(count != 0) entities.setValue(helperList);
        else {
            Toast toast = Toast.makeText(getApplication(), "Data did not deleted", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void updateItem(SimpleEntityForDB eDB, String newDescription) {
        int idKey;
        setUpdatedItemPosition(adapterListOfEntitiesVM.indexOf(eDB));
        SQLiteOpenHelper cwdbHelper = null;
        SQLiteDatabase db = null;
        try {
            cwdbHelper = new CWDBHelper(getApplication());
            db = cwdbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(CWDBHelper.T_EMP_C_FIO, newDescription);
            idKey = db.update(CWDBHelper.TABLE_NAME_EMP,
                    cv,
                    "_id = ?",
                    new String[] { Integer.toString(eDB.getID()) });
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(getApplication(), "DB can't change data", Toast.LENGTH_SHORT);
            toast.show();
            return;
        } finally {
            if(db != null) db.close();
            if(cwdbHelper != null) cwdbHelper.close();
        }
        if(idKey != 0) {
            adapterListOfEntitiesVM.get(adapterListOfEntitiesVM.indexOf(eDB)).setDescription(newDescription);
            entities.setValue(adapterListOfEntitiesVM);
        }else {
            Toast toast = Toast.makeText(getApplication(), "Data did not change", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
