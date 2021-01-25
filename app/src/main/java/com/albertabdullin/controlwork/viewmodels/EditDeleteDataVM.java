package com.albertabdullin.controlwork.viewmodels;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Message;
import android.os.Process;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.albertabdullin.controlwork.activities.EditDeleteDataActivity;
import com.albertabdullin.controlwork.db_of_app.CWDBHelper;
import com.albertabdullin.controlwork.fragments.DeleteDataFragment;
import com.albertabdullin.controlwork.models.ComplexEntityForDB;
import com.albertabdullin.controlwork.models.PairOfItemPositions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class EditDeleteDataVM extends AndroidViewModel implements DialogFragmentStateHolder {
    private String mQuery;
    private boolean isNeedSearch;
    private PairOfItemPositions pairOfItemPositions;
    private final List<ComplexEntityForDB> listForWorkWithDB = new ArrayList<>();
    private MutableLiveData<DeleteDataFragment.StateOfRecyclerView> stateOfRecyclerView;
    private MutableLiveData<Integer> visibleOfProgressBar;
    private MutableLiveData<Integer> visibleOfRecyclerView;
    private MutableLiveData<String> employeeEditTextLD;
    private MutableLiveData<String> firmEditTextLD;
    private MutableLiveData<String> placeOfWorkEditTextLD;
    private MutableLiveData<String> typeOfWorkEditTextLD;
    private MutableLiveData<PairOfItemPositions> changerColorOfViewHolderLD;
    private List<Integer> listOfDeletedRowsFromDB;
    private Set<Integer> itemsOfST;
    private boolean isActivatedDF = false;

    public EditDeleteDataVM(@NonNull Application application) {
        super(application);
    }

    @Override
    public void setActivatedDF(boolean b) {
        isActivatedDF = b;
    }

    @Override
    public boolean isNotActivatedDF() {
        return !isActivatedDF;
    }

    private class LoadItemsThread extends Thread {
        @Override
        public void run() {
            SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
            try (SQLiteDatabase db = cwdbHelper.getReadableDatabase();
                 Cursor cursor = db.rawQuery(mQuery, null, null)) {
                if(cursor.moveToFirst()) {
                    do {
                        byte b = 0;
                        ComplexEntityForDB eDB = new ComplexEntityForDB();
                        eDB.setID(cursor.getInt(b++));
                        eDB.setEmployerID(cursor.getInt(b++));
                        eDB.setEmployerDescription(cursor.getString(b++));
                        eDB.setFirmID(cursor.getInt(b++));
                        eDB.setFirmDescription(cursor.getString(b++));
                        eDB.setTypeOfWorkID(cursor.getInt(b++));
                        eDB.setTOWDescription(cursor.getString(b++));
                        eDB.setPlaceOfWorkID(cursor.getInt(b++));
                        eDB.setPOWDescription(cursor.getString(b++));
                        eDB.setDate(cursor.getString(b++));
                        eDB.setResult(cursor.getFloat(b++));
                        eDB.setNote(cursor.getString(b));
                        listForWorkWithDB.add(eDB);
                    } while (cursor.moveToNext());
                }
            } catch (SQLiteException e) {
                Message message = EditDeleteDataActivity.mHandler.obtainMessage(EditDeleteDataActivity.FAIL_ABOUT_LOAD_DATA_FROM_DB);
                EditDeleteDataActivity.mHandler.sendMessage(message);
            }
            visibleOfProgressBar.postValue(View.GONE);
            visibleOfRecyclerView.postValue(View.VISIBLE);
            stateOfRecyclerView.postValue(DeleteDataFragment.StateOfRecyclerView.LOAD);
        }
    }

    public void setQuery(String query) {
        if (mQuery == null) {
            mQuery = query;
            isNeedSearch = true;
        } else if (!mQuery.equals(query)) {
            mQuery = query;
            isNeedSearch = true;
        } else isNeedSearch = false;
    }

    public void startSearch() {
        LoadItemsThread loadItemsThread = new LoadItemsThread();
        loadItemsThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        loadItemsThread.start();
    }

    private class DeleteItemThread extends Thread {
        private final List<ComplexEntityForDB> list;

        public DeleteItemThread(List<ComplexEntityForDB> list) {
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
            if (listOfDeletedRowsFromDB == null) listOfDeletedRowsFromDB = new ArrayList<>();
            if (listOfDeletedRowsFromDB.size() > 0) listOfDeletedRowsFromDB.clear();
            for (int i = 0; i < list.size(); i++) listOfDeletedRowsFromDB.add(Integer.parseInt(list.get(i).getID()));
            String[] arguments = listOfDeletedRowsFromDB.toString().replaceAll("[\\[\\]]", "").split(", ");
            String whereClause = makeWhereClause(listOfDeletedRowsFromDB.size());
            try (SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
                 SQLiteDatabase db = cwdbHelper.getWritableDatabase()) {
                count = db.delete(CWDBHelper.TABLE_NAME_RESULT, whereClause, arguments);
            } catch (SQLiteException e) {
                Toast toast = Toast.makeText(getApplication(), "Something went wrong: DB can't delete data", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            if (count == list.size()) {
                listForWorkWithDB.removeAll(list);
                stateOfRecyclerView.postValue(DeleteDataFragment.StateOfRecyclerView.DELETE);
                visibleOfProgressBar.postValue(View.GONE);
                visibleOfRecyclerView.postValue(View.VISIBLE);
            }
            else {
                Message message = EditDeleteDataActivity.mHandler.obtainMessage(EditDeleteDataActivity.FAIL_ABOUT_DELETE_DATA_FROM_DB);
                EditDeleteDataActivity.mHandler.sendMessage(message);
            }
        }
    }

    public void deleteItem(List<ComplexEntityForDB> list)  {
        visibleOfRecyclerView.setValue(View.INVISIBLE);
        visibleOfProgressBar.setValue(View.VISIBLE);
        DeleteItemThread deleteItemThread = new DeleteItemThread(list);
        deleteItemThread.start();
    }

    public List<Integer> getDeletedPositionsFromDB() {
        for (int i = 0; i < listOfDeletedRowsFromDB.size(); i++)
            listOfDeletedRowsFromDB.set(i, (listOfDeletedRowsFromDB.get(i) - 1));
        Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return (o1 - o2) * (-1);
            }
        };
        listOfDeletedRowsFromDB.sort(comparator);
        return listOfDeletedRowsFromDB;
    }

    public LiveData<DeleteDataFragment.StateOfRecyclerView> getStateOfRecyclerViewLD() {
        if (stateOfRecyclerView == null) stateOfRecyclerView = new MutableLiveData<>();
        return stateOfRecyclerView;
    }

    public LiveData<Integer> getVisibleOfProgressBarLD() {
        if (visibleOfProgressBar == null) visibleOfProgressBar = new MutableLiveData<>();
        return visibleOfProgressBar;
    }

    public LiveData<String> getEmployeeEditTextLD() {
        if (employeeEditTextLD == null) employeeEditTextLD = new MutableLiveData<>();
        return employeeEditTextLD;
    }

    public LiveData<String> getFirmEditTextLD() {
        if (firmEditTextLD == null) firmEditTextLD = new MutableLiveData<>();
        return firmEditTextLD;
    }

    public LiveData<String> getPOWEditTextLD() {
        if (placeOfWorkEditTextLD == null) placeOfWorkEditTextLD = new MutableLiveData<>();
        return placeOfWorkEditTextLD;
    }

    public LiveData<String> getTOWEditTextLD() {
        if (typeOfWorkEditTextLD == null) typeOfWorkEditTextLD = new MutableLiveData<>();
        return typeOfWorkEditTextLD;
    }

    public LiveData<PairOfItemPositions> getChangerColorOfViewHolder() {
        if (changerColorOfViewHolderLD == null) changerColorOfViewHolderLD = new MutableLiveData<>();
        return changerColorOfViewHolderLD;
    }

    public LiveData<Integer> getVisibleOfRecyclerViewLD() {
        if (visibleOfRecyclerView == null) visibleOfRecyclerView = new MutableLiveData<>();
        return visibleOfRecyclerView;
    }

    private void notifyAboutLoadItems() {
        visibleOfProgressBar.setValue(View.GONE);
        visibleOfRecyclerView.setValue(View.VISIBLE);
        stateOfRecyclerView.setValue(DeleteDataFragment.StateOfRecyclerView.LOAD);
    }

    public List<ComplexEntityForDB> getResultList() {
        return listForWorkWithDB;
    }

    public void initializeResultList() {
        if (isNeedSearch) startSearch();
        else notifyAboutLoadItems();
    }

    public void notifyEditTexts(int i) {
        if (i == -1) {
            employeeEditTextLD.setValue("");
            firmEditTextLD.setValue("");
            typeOfWorkEditTextLD.setValue("");
            placeOfWorkEditTextLD.setValue("");
        } else {
            employeeEditTextLD.setValue(listForWorkWithDB.get(i).getEmployerDescription());
            firmEditTextLD.setValue(listForWorkWithDB.get(i).getFirmDescription());
            typeOfWorkEditTextLD.setValue(listForWorkWithDB.get(i).getTOWDescription());
            placeOfWorkEditTextLD.setValue(listForWorkWithDB.get(i).getPOWDescription());
        }
    }

    public String getValueOfETLD() {
        return employeeEditTextLD.getValue() == null ? "" : employeeEditTextLD.getValue();
    }

    public void changeColorOfPreviousSelectedItem(PairOfItemPositions pair) {
        if (pairOfItemPositions == null) pairOfItemPositions = new PairOfItemPositions(pair.getNewPos());
        else if (pairOfItemPositions.getNewPos() != pair.getNewPos())
            pairOfItemPositions.setNewPos(pair);
        changerColorOfViewHolderLD.setValue(pairOfItemPositions);
    }

    public void setNullToOldItemPosition() {
        if (pairOfItemPositions != null)
            pairOfItemPositions.setDefaultValueToOldPos();
    }

    public int getPosOfSelectedItem() {
        return pairOfItemPositions == null ? -1 : pairOfItemPositions.getNewPos();
    }

    public void setDefaultValueToNewPosOfPair() {
        pairOfItemPositions.setDefaultValueToNewPos();
    }

    public void addItemOfST(int position) {
        if (itemsOfST == null) itemsOfST = new HashSet<>();
        itemsOfST.add(position);
    }

    public void removeItemOfST(int position) {
        if (itemsOfST != null) {
            itemsOfST.remove(position);
            if (itemsOfST.size() == 0) itemsOfST = null;
        }
    }

    public int getItemsCountOfST() { return itemsOfST == null ? -1 : itemsOfST.size(); }

    public boolean isItemNotSelected(int position) {
        if (itemsOfST == null) return true;
        else return !itemsOfST.contains(position);
    }

    //возвращает итератор выбранных элементов SelectionTracker
    public Iterator<Integer> itemsOfSTsIterator() {
        return itemsOfST.iterator();
    }

}
