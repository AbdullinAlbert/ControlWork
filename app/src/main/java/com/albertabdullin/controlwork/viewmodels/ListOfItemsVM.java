package com.albertabdullin.controlwork.viewmodels;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Process;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.activities.FillNewData_Activity;
import com.albertabdullin.controlwork.activities.ListOfDBItemsActivity;
import com.albertabdullin.controlwork.db_of_app.CWDBHelper;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListOfItemsVM extends AndroidViewModel implements DialogFragmentStateHolder {
    private MutableLiveData<ListOfDBItemsActivity.adapterState>  entities;
    private List<SimpleEntityForDB> adapterListOfEntitiesVM;
    private List<SimpleEntityForDB> cacheForAdapterList;
    private final List<Integer> listOfDeletedPositions = new ArrayList<>();
    private int updatedPosition;
    private boolean activatedDF = false;
    private boolean isBlankCall = true;
    private SearchItemsThread searchItemsThread;
    private String itemSearchText;
    private boolean stateMenuItemSearchText = false;
    private String currentNameOfTable;
    private String currentNameOfColumn;
    private int numberOfNeededTable;
    private Executor mExecutor;

    private class AddItemsThread extends Thread {
        private final String item;
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
                ListOfDBItemsActivity.handler.post(() -> Toast.makeText(getApplication(), getApplication().getString(R.string.fail_attempt_about_write_data_to_table), Toast.LENGTH_SHORT).show());
                return;
            } finally {
                if (db != null) db.close();
                if (cwdbHelper != null) cwdbHelper.close();
            }
            SimpleEntityForDB eDB = new SimpleEntityForDB(idKey, this.item);
            adapterListOfEntitiesVM.add(eDB);
            entities.postValue(ListOfDBItemsActivity.adapterState.ADD);
        }
    }

    private class DeleteItemThread extends Thread {
        private final List<SimpleEntityForDB> list;

        private final Comparator<Integer> comparator = (o1, o2) -> o1.compareTo(o2) * (-1);

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
            int count;
            List<Integer> listOfID = new ArrayList<>();
            if (listOfDeletedPositions.size() > 0) listOfDeletedPositions.clear();
            for (int i = 0; i < list.size(); i++) {
                listOfDeletedPositions.add(adapterListOfEntitiesVM.indexOf(list.get(i)));
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
            if (count == list.size()) {
                listOfDeletedPositions.sort(comparator);
                entities.postValue(ListOfDBItemsActivity.adapterState.DELETE);
            } else {
                ListOfDBItemsActivity.handler.post(() -> Toast.makeText(getApplication(), getApplication().getString(R.string.fail_attempt_about_delete__all_selected_data_from_db), Toast.LENGTH_SHORT).show());
                LoadItemsThread loadItemsThread = new LoadItemsThread();
                loadItemsThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
                mExecutor.execute(loadItemsThread);
            }
        }
    }

    private class UpdateItemThread extends Thread {
        private final String newDescription;
        private final SimpleEntityForDB eDB;

        public UpdateItemThread(SimpleEntityForDB eDB, String newDescription) {
            this.eDB = eDB;
            this.newDescription = newDescription;
        }

        private synchronized void setUpdatedItemPosition(int i) {
            updatedPosition = i;
        }

        @Override
        public void run() {
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
                ListOfDBItemsActivity.handler.post(() -> Toast.makeText(getApplication(), getApplication().getString(R.string.fail_attempt_about_update_data_within_table), Toast.LENGTH_SHORT).show());
                return;
            }
            if (idKey != 0) {
                adapterListOfEntitiesVM.get(getUpdatedItemPosition()).setDescription(newDescription);
                entities.postValue(ListOfDBItemsActivity.adapterState.UPDATE);
            } else ListOfDBItemsActivity.handler.post(() -> Toast.makeText(getApplication(), getApplication().getString(R.string.fail_attempt_about_update_data_within_table), Toast.LENGTH_SHORT).show());
        }
    }

    private class LoadItemsThread extends Thread {
        @Override
        public void run() {
            if (adapterListOfEntitiesVM.size() != 0) adapterListOfEntitiesVM.clear();
            try (SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
                 SQLiteDatabase db = cwdbHelper.getReadableDatabase();
                 Cursor cursor = db.query(currentNameOfTable,
                    new String[] {"_id", currentNameOfColumn},
                    null, null, null, null, null)) {
                if (cursor.moveToFirst()) {
                    do {
                        SimpleEntityForDB eDB = new SimpleEntityForDB();
                        eDB.setId(cursor.getInt(0));
                        eDB.setDescription(cursor.getString(1));
                        adapterListOfEntitiesVM.add(eDB);
                    } while (cursor.moveToNext());
                }
            } catch (SQLiteException e) {
                ListOfDBItemsActivity.handler.post(() -> Toast.makeText(getApplication(), getApplication().getString(R.string.fail_attempt_about_load_data_from_table), Toast.LENGTH_SHORT).show());
                return;
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
            entities.postValue(ListOfDBItemsActivity.adapterState.LOAD);
        }
    }

    private class SearchItemsThread extends Thread {
        private final BlockingQueue<String> store = new ArrayBlockingQueue<>(1);
        private String pattern, regEx, hPattern = "";
        private final AtomicBoolean isStopSearch = new AtomicBoolean(false);
        private Pattern p;
        private Matcher m;

        SearchItemsThread(String pattern) {
            try {
                store.put(pattern);
            } catch (InterruptedException e) {
                stateOfRecyclerViewRecovery(e);
                interrupt();
            }
            cacheForAdapterList = new ArrayList<>(adapterListOfEntitiesVM);
        }

        private void stateOfRecyclerViewRecovery(Exception e) {
            ListOfDBItemsActivity.handler.post(() ->
                    Toast.makeText(getApplication(), getApplication().getString(R.string.thread_for_search_has_been_interrupted) + ": " +
                            e.getMessage(), Toast.LENGTH_SHORT).show());
            adapterListOfEntitiesVM.clear();
            adapterListOfEntitiesVM.addAll(cacheForAdapterList);
            entities.postValue(ListOfDBItemsActivity.adapterState.LOAD);
        }

        public void setNewPattern(String newPattern) {
            if (!store.isEmpty()) store.clear();
            try {
                store.put(newPattern);
            } catch (InterruptedException e) {
                stateOfRecyclerViewRecovery(e);
                interrupt();
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
            adapterListOfEntitiesVM.clear();
            while (i < cacheForAdapterList.size()) {
                m = p.matcher(cacheForAdapterList.get(i).getDescription());
                if (m.find()) adapterListOfEntitiesVM.add(cacheForAdapterList.get(i));
                i++;
                if (!store.isEmpty()) {
                    hPattern = store.poll();
                    regEx = "(?i)" + hPattern;
                    p = Pattern.compile(regEx);
                    if (hPattern.contains(pattern)) searchInFilteredList();
                    else {
                        i = 0;
                        adapterListOfEntitiesVM.clear();
                    }
                }
                if (isStopSearch.get()) break;
            }
        }

        private void searchInFilteredList() {
            List<SimpleEntityForDB> helperFoundItemsList = new ArrayList<>();
            for (int j = 0; j < adapterListOfEntitiesVM.size(); j++) {
                m = p.matcher(adapterListOfEntitiesVM.get(j).getDescription());
                if (m.find()) helperFoundItemsList.add(adapterListOfEntitiesVM.get(j));
            }
            adapterListOfEntitiesVM.clear();
            adapterListOfEntitiesVM.addAll(helperFoundItemsList);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    pattern = store.take();
                } catch (InterruptedException e) {
                    stateOfRecyclerViewRecovery(e);
                    interrupt();
                }
                if (pattern.equals("")) break;
                regEx = "(?i)" + pattern;
                p = Pattern.compile(regEx);
                isStopSearch.set(false);
                if (!hPattern.equals("") && pattern.contains(hPattern)) searchInFilteredList();
                else searchInFullList();
                if (!isStopSearch.get()) entities.postValue(ListOfDBItemsActivity.adapterState.LOAD);
                else {
                    if (!store.isEmpty()) store.clear();
                    isStopSearch.set(false);
                    adapterListOfEntitiesVM.clear();
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

    public LiveData<ListOfDBItemsActivity.adapterState> getLiveData() {
        if(entities == null) {
            entities = new MutableLiveData<>();
            mExecutor = Executors.newSingleThreadExecutor();
            mExecutor.execute(new LoadItemsThread());
        }
        return entities;
    }

    public synchronized boolean isEntitiesNull() {
        return entities == null;
    }


    public int getNumberOfNeededTable() {
        return numberOfNeededTable;
    }

    public List<SimpleEntityForDB> getAdapterListOfEntitiesVM() {
        if (adapterListOfEntitiesVM == null) adapterListOfEntitiesVM = Collections.synchronizedList(new ArrayList<>());
        return adapterListOfEntitiesVM;
    }

    public List<Integer> getListOfDeletedPositions() {
        return listOfDeletedPositions;
    }

    public synchronized int getUpdatedItemPosition() {
        return updatedPosition;
    }

    @Override
    public void setActivatedDF(boolean b) {
        activatedDF = b;
    }

    @Override
    public boolean isNotActivatedDF() {
        return !activatedDF;
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
        mExecutor.execute(new AddItemsThread(s));
    }

    public void deleteItem(List<SimpleEntityForDB> list)  {
        mExecutor.execute(new DeleteItemThread(list));
    }

    public void updateItem(SimpleEntityForDB eDB, String newDescription) {
        mExecutor.execute(new UpdateItemThread(eDB, newDescription));
    }

    public void startSearch(String pattern) {
        if (searchItemsThread == null || !searchItemsThread.isAlive())
            searchItemsThread = new SearchItemsThread(pattern);
        mExecutor.execute(searchItemsThread);
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
            adapterListOfEntitiesVM.addAll(cacheForAdapterList);
            entities.setValue(ListOfDBItemsActivity.adapterState.LOAD);
        }
    }

    public void closeSearchThread() {
        if (searchItemsThread != null) searchItemsThread.closeThread();
        searchItemsThread = null;
        isBlankCall = true;
        setStateMenuItemSearchText(false);
        setItemSearchText("");
    }

}
