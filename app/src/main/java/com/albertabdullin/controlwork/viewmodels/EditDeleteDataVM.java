package com.albertabdullin.controlwork.viewmodels;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.View;

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
import java.util.List;

public class EditDeleteDataVM extends AndroidViewModel {
    private String mQuery;
    private boolean isNeedSearch;
    private PairOfItemPositions pairOfItemPositions;
    private final List<ComplexEntityForDB> listForWorkWithDB = new ArrayList<>();
    private MutableLiveData<DeleteDataFragment.StateOfRecyclerView> stateOfRecyclerView;
    private MutableLiveData<Integer> visibleOfProgressBar;
    private MutableLiveData<String> employeeEditTextLD;
    private MutableLiveData<String> firmEditTextLD;
    private MutableLiveData<String> placeOfWorkEditTextLD;
    private MutableLiveData<String> typeOfWorkEditTextLD;
    private MutableLiveData<PairOfItemPositions> changerColorOfViewHolderLD;

    public EditDeleteDataVM(@NonNull Application application) {
        super(application);
    }

    private class LoadItemsThread extends Thread {
        public static final String LOAD_ITEMS_TAG = "LoadItemsThread";
        @Override
        public void run() {
            Message message;
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
                Log.e(LOAD_ITEMS_TAG, "не получилось прочесть данные из таблицы Result");
            }
            message = EditDeleteDataActivity.mHandler.obtainMessage(EditDeleteDataActivity.LOAD_ITEMS_FROM_DB);
            EditDeleteDataActivity.mHandler.sendMessage(message);
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

    public void notifyAboutLoadItems() {
        visibleOfProgressBar.setValue(View.GONE);
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
        employeeEditTextLD.setValue(listForWorkWithDB.get(i).getEmployerDescription());
        firmEditTextLD.setValue(listForWorkWithDB.get(i).getFirmDescription());
        typeOfWorkEditTextLD.setValue(listForWorkWithDB.get(i).getTOWDescription());
        placeOfWorkEditTextLD.setValue(listForWorkWithDB.get(i).getPOWDescription());
    }

    public void changeColorOfPreviousSelectedItem(PairOfItemPositions pair) {
        if (pairOfItemPositions == null) pairOfItemPositions = new PairOfItemPositions(pair);
        else if (pairOfItemPositions.getNewPos() != pair.getNewPos())
            pairOfItemPositions.setNewPos(pair);
        changerColorOfViewHolderLD.setValue(pairOfItemPositions);
    }

    public void setNullToOldItemPosition() {
        if (pairOfItemPositions !=null)
            pairOfItemPositions.setDefaultValueToOldPos();
    }

    public int getPosOfSelectedItem() {
        return pairOfItemPositions == null ? -1 : pairOfItemPositions.getNewPos();
    }

}
