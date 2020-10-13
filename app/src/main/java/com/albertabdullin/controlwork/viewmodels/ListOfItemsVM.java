package com.albertabdullin.controlwork.viewmodels;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.albertabdullin.controlwork.activities.ListOfBDItemsActivity;
import com.albertabdullin.controlwork.db_of_app.CWDBHelper;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListOfItemsVM extends AndroidViewModel {
    private MutableLiveData<List<SimpleEntityForDB>>  entities;
    private List<SimpleEntityForDB> adapterListOfEntitiesVM = new ArrayList<>();
    private List<SimpleEntityForDB> hListForWorkWithDB;
    private List<SimpleEntityForDB> cacheForAdapterList;
    private List<SimpleEntityForDB> findedItemsList;
    private List<Integer> listOfDeletedPositions = new ArrayList<>();
    private SimpleEntityForDB eDB;
    private String newDescription;
    private int updatedPosition;
    private boolean activatedDF = false;
    private boolean isBlankCall = true;
    private SearchItemsThread searchItemsThread;

    private class AddItemsThread extends Thread {
        private String item;
        public AddItemsThread(String item) {
            this.item = item;
        }
        @Override
        public void run() {
            SQLiteOpenHelper cwdbHelper = null;
            SQLiteDatabase db = null;
            int idKey;
            try {
                cwdbHelper = new CWDBHelper(getApplication());
                db = cwdbHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put(CWDBHelper.T_EMP_C_FIO, this.item);
                idKey = (int) db.insert(CWDBHelper.TABLE_NAME_EMP, null, cv);
            } catch (SQLiteException e) {
                Toast toast = Toast.makeText(getApplication(), "DB can't write data", Toast.LENGTH_SHORT);
                toast.show();
                return;
            } finally {
                if (db != null) db.close();
                if (cwdbHelper != null) cwdbHelper.close();
            }
            SimpleEntityForDB eDB = new SimpleEntityForDB(idKey, this.item);
            hListForWorkWithDB = new ArrayList<>(getAdapterListOfEntitiesVM());
            hListForWorkWithDB.add(eDB);
            ListOfBDItemsActivity.mHandler.sendEmptyMessage(ListOfBDItemsActivity.ADD);
        }
    }

    private class DeleteItemThread extends Thread {
        private List<SimpleEntityForDB> list;

        private Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        };

        public DeleteItemThread(List<SimpleEntityForDB> list) {
            this.list = list;
        }

        private String makeWhereClause(int size) {
            StringBuilder sb = new StringBuilder();
            sb.append("_id in (");
            for (int i = 0; i < size-1; i ++) sb.append("?, ");
            sb.append("?)");
            return sb.toString();
        }

        @Override
        public void run() {
            Message message;
            int count;
            hListForWorkWithDB = new ArrayList<>(getAdapterListOfEntitiesVM());
            List<Integer> listOfID = new ArrayList<>();
            if (listOfDeletedPositions.size() > 0) listOfDeletedPositions.clear();
            for (int i = 0; i < list.size(); i++) {
                listOfDeletedPositions.add(adapterListOfEntitiesVM.indexOf(list.get(i)));
                hListForWorkWithDB.remove(list.get(i));
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
            if (count != 0) {
                message = ListOfBDItemsActivity.mHandler.obtainMessage(ListOfBDItemsActivity.DELETE, ListOfBDItemsActivity.OK, 0);
                listOfDeletedPositions.sort(comparator);
            } else message = ListOfBDItemsActivity.mHandler.obtainMessage(ListOfBDItemsActivity.DELETE, ListOfBDItemsActivity.NOT_OK, 0);
            ListOfBDItemsActivity.mHandler.sendMessage(message);
        }
    }

    private class UpdateItemThread extends Thread {
        private String newDescription;
        private SimpleEntityForDB eDB;

        public UpdateItemThread(SimpleEntityForDB eDB, String newDescription) {
            this.eDB = eDB;
            this.newDescription = newDescription;
        }

        private void setUpdatedItemPosition(int i) {
            updatedPosition = i;
        }

        @Override
        public void run() {
            Message message;
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
                Log.d("UpdateThread", Integer.toString(idKey));
            } catch (SQLiteException e) {
                Toast toast = Toast.makeText(getApplication(), "DB can't change data", Toast.LENGTH_SHORT);
                toast.show();
                return;
            } finally {
                if(db != null) db.close();
                if(cwdbHelper != null) cwdbHelper.close();
            }
            if(idKey != 0) message = ListOfBDItemsActivity.mHandler.obtainMessage(ListOfBDItemsActivity.UPDATE, ListOfBDItemsActivity.OK, 0);
            else message = ListOfBDItemsActivity.mHandler.obtainMessage(ListOfBDItemsActivity.UPDATE, ListOfBDItemsActivity.NOT_OK, 0);
            ListOfBDItemsActivity.mHandler.sendMessage(message);
        }
    }

    private class LoadItemsThread extends Thread {
        Message message;
        @Override
        public void run() {
            SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
            hListForWorkWithDB = new ArrayList<>();
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
                        hListForWorkWithDB.add(eDB);
                    } while (cursor.moveToNext());
                }
            } catch (SQLiteException e) {
                Toast toast = Toast.makeText(getApplication(), "Something wrong with DB", Toast.LENGTH_SHORT);
                toast.show();
            } finally {
                cwdbHelper.close();
                if(cursor != null) cursor.close();
                if(db != null) db.close();
            }
            message = ListOfBDItemsActivity.mHandler.obtainMessage(ListOfBDItemsActivity.LOAD);
            ListOfBDItemsActivity.mHandler.sendMessage(message);
        }
    }

    private class SearchItemsThread extends Thread {
        public static final String TAG_SEARCH_TREAD = "SearchItemsThread";
        private BlockingQueue<String> store = new ArrayBlockingQueue<>(1);
        private String pattern, regEx, hPattern = "";
        private AtomicBoolean isStopSearch = new AtomicBoolean(false);
        private Pattern p;
        private Matcher m;

        SearchItemsThread(String pattern) {
            try {
                store.put(pattern);
            } catch (InterruptedException e) {
                Log.e(TAG_SEARCH_TREAD, "Поток прервался: " + e.toString());
            }
            cacheForAdapterList = new ArrayList<>(adapterListOfEntitiesVM);
            findedItemsList = new ArrayList<>();
        }

        public void setNewPattern(String newPattern) {
            if (!store.isEmpty()) store.clear();
            try {
                store.put(newPattern);
            } catch (InterruptedException e) {
                Log.e(TAG_SEARCH_TREAD, "Поток прервался: " + e.toString());
                //Вернуть полный список элементов в List адаптера
            }
        }

        public void stopSearch() {
            isStopSearch.set(true);
        }

        public void closeThread() {
            setNewPattern("");
        }

        private void searchInFullList() {
            Log.d(TAG_SEARCH_TREAD, "метод searchInFullList");
            int i = 0;
            findedItemsList.clear();
            while (i < cacheForAdapterList.size()) {
                m = p.matcher(cacheForAdapterList.get(i).getDescription());
                if (m.find()) findedItemsList.add(cacheForAdapterList.get(i));
                i++;
                if (!store.isEmpty()) {
                    hPattern = store.poll();
                    regEx = "(?i)" + hPattern + "";
                    p = Pattern.compile(regEx);
                    if (hPattern.contains(pattern)) searchInFilteredList();
                    else {
                        i = 0;
                        findedItemsList.clear();
                    }
                }
                if (isStopSearch.get()) break;
            }
        }

        private void searchInFilteredList() {
            List<SimpleEntityForDB> helperfindedItemsList = new ArrayList<>();
            for (int j = 0; j < findedItemsList.size(); j++) {
                m = p.matcher(findedItemsList.get(j).getDescription());
                if (m.find()) helperfindedItemsList.add(findedItemsList.get(j));
            }
            findedItemsList.clear();
            findedItemsList.addAll(helperfindedItemsList);
        }

        @Override
        public void run() {
            Message msg;
            while (true) {
                try {
                    pattern = store.take();
                } catch (InterruptedException e) {
                    Log.e(TAG_SEARCH_TREAD, "Поток прервался: " + e.toString());
                    //Вернуть полный список элементов в List адаптера
                }
                if (pattern.equals("")) break;
                regEx = "(?i)" + pattern + "";
                p = Pattern.compile(regEx);
                isStopSearch.set(false);
                if (!hPattern.equals("") && pattern.contains(hPattern)) searchInFilteredList();
                else searchInFullList();
                if (!isStopSearch.get()) {
                    msg = ListOfBDItemsActivity.mHandler.obtainMessage(ListOfBDItemsActivity.SEARCH_IS_DONE, 0, 0);
                    ListOfBDItemsActivity.mHandler.sendMessage(msg);
                } else {
                    isStopSearch.set(false);
                    findedItemsList.clear();
                }
                if (!hPattern.contains(pattern)) hPattern = pattern;
            }
            Log.d(TAG_SEARCH_TREAD, "вышел из потока");
        }
    }

    public ListOfItemsVM(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<SimpleEntityForDB>> getLiveDataEmp() {
        if(entities == null) {
            entities = new MutableLiveData<>();
            LoadItemsThread loadItemsThread = new LoadItemsThread();
            loadItemsThread.start();
        }
        return entities;
    }

    public void notifyAboutLoadItems() {
        entities.setValue(hListForWorkWithDB);
        hListForWorkWithDB = null;
    }

    public List<SimpleEntityForDB> getAdapterListOfEntitiesVM() {
        return adapterListOfEntitiesVM;
    }

    public List<Integer> getListOfDeletedPositions() {
        return listOfDeletedPositions;
    }

    public int getUpdatedItemPosition() {
        return updatedPosition;
    }

    public void setActivatedDF(boolean b) {
        activatedDF = b;
    }

    public boolean isActivatedDF() {
        return activatedDF;
    }


    public void addItem(String s) {
        AddItemsThread addItemsThread = new AddItemsThread(s);
        addItemsThread.start();
    }

    public void notifyAboutAddItem() {
        entities.setValue(hListForWorkWithDB);
        hListForWorkWithDB = null;
    }


    public void deleteItem(List<SimpleEntityForDB> list)  {
        DeleteItemThread deleteItemThread = new DeleteItemThread(list);
        deleteItemThread.start();
    }

    public void notifyAboutDeleteItem(boolean b) {
        if (b) entities.setValue(hListForWorkWithDB);
        else {
            Toast toast = Toast.makeText(getApplication(), "Data did not deleted", Toast.LENGTH_SHORT);
            toast.show();
        }
        hListForWorkWithDB = null;
    }

    public void updateItem(SimpleEntityForDB eDB, String newDescription) {
        this.eDB = eDB;
        this.newDescription = newDescription;
        UpdateItemThread updateItemThread = new UpdateItemThread(eDB, newDescription);
        updateItemThread.start();
    }

    public void notifyAboutUpdateItem(boolean b) {
        if (b) {
            getAdapterListOfEntitiesVM().get(adapterListOfEntitiesVM.indexOf(this.eDB)).setDescription(this.newDescription);
            entities.setValue(adapterListOfEntitiesVM);
        } else {
            Toast toast = Toast.makeText(getApplication(), "Data did not change", Toast.LENGTH_SHORT);
            toast.show();
        }
        this.eDB = null;
        this.newDescription = null;
    }

    public void startSearch(String pattern) {
        searchItemsThread = new SearchItemsThread(pattern);
        searchItemsThread.start();
    }

    public boolean isSearchIsActive() {
        return searchItemsThread != null;
    }

    public void sendNewText(String text) {
        searchItemsThread.setNewPattern(text);
    }

    public void setBlankCallTrue() {
        isBlankCall = true;
    }

    public void sayToStop() {
        if (isBlankCall) isBlankCall = false;
        else {
            searchItemsThread.stopSearch();
            if (cacheForAdapterList.size() == adapterListOfEntitiesVM.size()) return;
            adapterListOfEntitiesVM.clear();
            entities.setValue(cacheForAdapterList);
        }
    }

    public void stopSearch() {
        if (searchItemsThread != null) searchItemsThread.closeThread();
         isBlankCall = true;
    }

    public void updateSearchAdapterList() {
        adapterListOfEntitiesVM.clear();
        entities.setValue(findedItemsList);
    }

}
