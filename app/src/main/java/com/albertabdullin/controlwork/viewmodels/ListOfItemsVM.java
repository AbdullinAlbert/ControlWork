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

import com.albertabdullin.controlwork.activities.FillNewData_Activity;
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
    private String itemSearchText;
    private boolean stateMenuItemSearchText = false;
    private String currentNameOfTable;
    private String currentNameOfColumn;
    private int numberOfNeededTable;

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
                cv.put(currentNameOfColumn, this.item);
                idKey = (int) db.insert(currentNameOfTable, null, cv);
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
            ListOfBDItemsActivity.handler.sendEmptyMessage(ListOfBDItemsActivity.ADD);
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
            String[] arguments = listOfID.toString().replaceAll("[\\[\\]]", "").split(", ");
            String whereClause = makeWhereClause(listOfID.size());
            try (SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
                 SQLiteDatabase db = cwdbHelper.getWritableDatabase()) {
                count = db.delete(currentNameOfTable, whereClause, arguments);
            } catch (SQLiteException e) {
                Toast toast = Toast.makeText(getApplication(), "Something went wrong: DB can't delete data", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            if (count != 0) {
                message = ListOfBDItemsActivity.handler.obtainMessage(ListOfBDItemsActivity.DELETE, ListOfBDItemsActivity.OK, 0);
                listOfDeletedPositions.sort(comparator);
            } else message = ListOfBDItemsActivity.handler.obtainMessage(ListOfBDItemsActivity.DELETE, ListOfBDItemsActivity.NOT_OK, 0);
            ListOfBDItemsActivity.handler.sendMessage(message);
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
            try (SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
                 SQLiteDatabase db = cwdbHelper.getWritableDatabase()) {
                ContentValues cv = new ContentValues();
                cv.put(currentNameOfColumn, newDescription);
                idKey = db.update(currentNameOfTable,
                        cv,
                        "_id = ?",
                        new String[]{Integer.toString(eDB.getID())});
            } catch (SQLiteException e) {
                Toast toast = Toast.makeText(getApplication(), "DB can't change data", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            if(idKey != 0) message = ListOfBDItemsActivity.handler.obtainMessage(ListOfBDItemsActivity.UPDATE, ListOfBDItemsActivity.OK, 0);
            else message = ListOfBDItemsActivity.handler.obtainMessage(ListOfBDItemsActivity.UPDATE, ListOfBDItemsActivity.NOT_OK, 0);
            ListOfBDItemsActivity.handler.sendMessage(message);
        }
    }

    private class LoadItemsThread extends Thread {
        public static final String LOAD_ITEMS_TAG = "LoadItemsThread";
        @Override
        public void run() {
            Message message;
            SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
            hListForWorkWithDB = new ArrayList<>();
            SQLiteDatabase db = null;
            Cursor cursor = null;
            try {
                db = cwdbHelper.getReadableDatabase();
                cursor = db.query(currentNameOfTable,
                        new String[]{"_id", currentNameOfColumn},
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
                Log.e(LOAD_ITEMS_TAG, "не получилось прочесть данные из таблицы " + currentNameOfTable);
            } finally {
                cwdbHelper.close();
                if(cursor != null) cursor.close();
                if(db != null) db.close();
            }
            if (isEntitiesNull()) {
                do {
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (isEntitiesNull());
            }
            message = ListOfBDItemsActivity.handler.obtainMessage(ListOfBDItemsActivity.LOAD);
            ListOfBDItemsActivity.handler.sendMessage(message);
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
                    msg = ListOfBDItemsActivity.handler.obtainMessage(ListOfBDItemsActivity.SEARCH_IS_DONE, 0, 0);
                    ListOfBDItemsActivity.handler.sendMessage(msg);
                } else {
                    if (!store.isEmpty()) store.clear();
                    isStopSearch.set(false);
                    findedItemsList.clear();
                }
                if (!hPattern.contains(pattern)) hPattern = pattern;
            }
        }
    }

    public ListOfItemsVM(@NonNull Application application) {
        super(application);
    }

    public void setCurrentDBTable(int i) {
        numberOfNeededTable = i;
        switch (numberOfNeededTable) {
            case FillNewData_Activity.TABLE_OF_EMPLOYERS:
                currentNameOfTable = CWDBHelper.TABLE_NAME_EMP;
                currentNameOfColumn = CWDBHelper.T_EMP_C_FIO;
                break;
            case FillNewData_Activity.TABLE_OF_FIRMS:
                currentNameOfTable = CWDBHelper.TABLE_NAME_FIRM;
                currentNameOfColumn = CWDBHelper.T_FIRM_C_DESCRIPTION;
                break;
            case FillNewData_Activity.TABLE_OF_TYPES_OF_WORK:
                currentNameOfTable = CWDBHelper.TABLE_NAME_TYPE_OF_WORK;
                currentNameOfColumn =CWDBHelper.T_TYPE_OF_WORK_C_DESCRIPTION;
                break;
            case FillNewData_Activity.TABLE_OF_PLACES_OF_WORK:
                currentNameOfTable = CWDBHelper.TABLE_NAME_PLACE_OF_WORK;
                currentNameOfColumn =CWDBHelper.T_PLACE_OF_WORK_C_DESCRIPTION;
                break;
        }
    }

    public LiveData<List<SimpleEntityForDB>> getLiveData() {
        if(entities == null) {
            entities = new MutableLiveData<>();
            LoadItemsThread loadItemsThread = new LoadItemsThread();
            loadItemsThread.start();
        }
        return entities;
    }

    public synchronized boolean isEntitiesNull() {
        return entities == null;
    }

    public synchronized void notifyAboutLoadItems() {
        entities.setValue(hListForWorkWithDB);
        hListForWorkWithDB = null;
    }

    public int getNumberOfNeededTable() {
        return numberOfNeededTable;
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

    public void setItemSearchText(String s) { itemSearchText = s; }

    public String getItemSearchText() { return itemSearchText; }

    public boolean isStateMenuItemSearchTextActive() {
        return stateMenuItemSearchText;
    }

    public void setStateMenuItemSearchText(boolean b) {
        stateMenuItemSearchText = b;
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

    public void sayToStopSearch(int before) {
        if ((isBlankCall) && (before == 0)) isBlankCall = false;
        else if (searchItemsThread != null) {
            searchItemsThread.stopSearch();
            if (cacheForAdapterList.size() == adapterListOfEntitiesVM.size()) return;
            adapterListOfEntitiesVM.clear();
            entities.setValue(cacheForAdapterList);
        }
    }

    public void closeSearchThread() {
        if (searchItemsThread != null) searchItemsThread.closeThread();
        searchItemsThread = null;
        isBlankCall = true;
    }

    public void updateSearchAdapterList() {
        adapterListOfEntitiesVM.clear();
        entities.setValue(findedItemsList);
    }

}
