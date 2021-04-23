package com.albertabdullin.controlwork.viewmodels;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Process;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.activities.EditDeleteDataActivity;
import com.albertabdullin.controlwork.db_of_app.CWDBHelper;
import com.albertabdullin.controlwork.fragments.ReportPreViewFragment;
import com.albertabdullin.controlwork.models.ComplexEntityForDB;
import com.albertabdullin.controlwork.models.PairOfItemPositions;
import com.albertabdullin.controlwork.models.ResultTypeInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportViewModel extends AndroidViewModel {

    private String mQuery;
    private boolean isNeedSearch;
    private PairOfItemPositions pairOfItemPositions;
    private final List<ComplexEntityForDB> listForWorkWithResultTableItems = new ArrayList<>();
    private MutableLiveData<Integer> visibleOfProgressBarForResultList;
    private MutableLiveData<Integer> visibleOfRecyclerView;
    private MutableLiveData<String> employeeEditTextForResultListLD;
    private MutableLiveData<String> firmEditTextForResultListLD;
    private MutableLiveData<String> noteEditTextForResultListLD;
    private MutableLiveData<ReportPreViewFragment.StateOfRecyclerView> stateOfRecyclerViewForResultList;
    private MutableLiveData<PairOfItemPositions> changerColorOfViewHolderLD;

    public ReportViewModel(@NonNull Application application) {
        super(application);
    }

    private class LoadItemsThreadFromResultTable extends Thread {

        private int employeeID = -1;
        private int typeOfWorkID = -1;
        private int placeOfWorkID = -1;
        private int resultTypeID = -1;
        private String stringViewOfResultType = "";
        private float resultSumOfToW = 0;
        private final Map<ResultTypeInfo, Float> totalResultOfResultType = new LinkedHashMap<>();
        private final Map<ResultTypeInfo, Float> commonResultOfResultType = new LinkedHashMap<>();
        private final Map<ResultTypeInfo, Float> resultTypeOfPoW = new LinkedHashMap<>();
        private final Map<ResultTypeInfo, Float> totalResultOfToW = new LinkedHashMap<>();

        private void setID(ComplexEntityForDB entityForDB) {
            employeeID = entityForDB.getEmployerID();
            typeOfWorkID = entityForDB.getTypeOfWorkID();
            placeOfWorkID = entityForDB.getPlaceOfWorkID();
            resultTypeID = entityForDB.getResultTypeID();
            stringViewOfResultType = entityForDB.getStringViewOfResultType();
        }

        private void addResultTypeToCommonResultOfResultType() {
            commonResultOfResultType.put(new ResultTypeInfo(resultTypeID, stringViewOfResultType), resultSumOfToW);
        }

        private void transferDataFromPoWResultTypeToToWResultType(String towDescription) {
            transferDataToCommonResultType(commonResultOfResultType, resultTypeOfPoW);
            addResRows(resultTypeOfPoW, towDescription, getApplication().getString(R.string.common_sum_on_place_of_work));
            transferDataToCommonResultType(resultTypeOfPoW, totalResultOfToW);
        }

        private void transferDataToCommonResultType(Map<ResultTypeInfo, Float> fromTotalResultType, Map<ResultTypeInfo, Float> toTotalResultType) {
            for (Map.Entry<ResultTypeInfo, Float> outerEntry: fromTotalResultType.entrySet()) {
                boolean isNeedAdd = true;
                for (Map.Entry<ResultTypeInfo, Float> innerEntry: toTotalResultType.entrySet()) {
                    if (outerEntry.getKey().getResultTypeID() == innerEntry.getKey().getResultTypeID()) {
                        float sum = outerEntry.getValue() + innerEntry.getValue();
                        toTotalResultType.put(innerEntry.getKey(), sum);
                        isNeedAdd = false;
                    }
                }
                if (isNeedAdd) toTotalResultType.put(outerEntry.getKey(), outerEntry.getValue());
            }
            fromTotalResultType.clear();
        }

        private void addResRows(Map<ResultTypeInfo, Float> fromTotalResultType, String towDescription, String resDescription) {
            for (Map.Entry<ResultTypeInfo, Float> outerEntry : fromTotalResultType.entrySet()) {
                ComplexEntityForDB helperEntity = new ComplexEntityForDB();
                helperEntity.setResultEntity(true);
                helperEntity.setTOWDescription(towDescription);
                helperEntity.setTypeResultSum(outerEntry.getValue());
                helperEntity.setStringViewOfResultType(outerEntry.getKey().getStringViewOfRT());
                helperEntity.setResultDescription(resDescription);
                listForWorkWithResultTableItems.add(helperEntity);
            }
        }

        private void addToWRow(ComplexEntityForDB eDB) {
            ComplexEntityForDB helperEntity = new ComplexEntityForDB();
            helperEntity.setTypeOfWorkEntity(true);
            helperEntity.setTOWDescription(eDB.getTOWDescription());
            listForWorkWithResultTableItems.add(helperEntity);
        }

        @Override
        public void run() {
            if (listForWorkWithResultTableItems.size() != 0) listForWorkWithResultTableItems.clear();
            SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
            try (SQLiteDatabase db = cwdbHelper.getReadableDatabase();
                 Cursor cursor = db.rawQuery(mQuery, null, null)) {
                if(cursor.moveToFirst()) {
                    ComplexEntityForDB eDB;
                    int countOfPoW = 0;
                    int countOfToW = 0;
                    boolean isNeedAdd;
                    String towDescription;
                    do {
                        byte b = 0;
                        eDB = new ComplexEntityForDB();
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
                        eDB.setNote(cursor.getString(b++));
                        eDB.setResultTypeID(cursor.getInt(b++));
                        eDB.setStringViewOfResultType(cursor.getString(b));
                        if (eDB.getEmployerID() == employeeID && eDB.getTypeOfWorkID() == typeOfWorkID
                           && eDB.getPlaceOfWorkID() == placeOfWorkID && eDB.getResultTypeID() == resultTypeID || listForWorkWithResultTableItems.size() == 0) {
                            resultSumOfToW += cursor.getFloat(10);
                        } else if (eDB.getEmployerID() == employeeID && eDB.getTypeOfWorkID() == typeOfWorkID && eDB.getPlaceOfWorkID() == placeOfWorkID) {
                            addResultTypeToCommonResultOfResultType();
                            resultSumOfToW = cursor.getFloat(10);
                        } else if (eDB.getEmployerID() == employeeID && eDB.getTypeOfWorkID() == typeOfWorkID) {
                            addResultTypeToCommonResultOfResultType();
                            transferDataToCommonResultType(commonResultOfResultType, resultTypeOfPoW);
                            addResRows(resultTypeOfPoW, eDB.getTOWDescription(), getApplication().getString(R.string.common_sum_on_place_of_work));
                            transferDataToCommonResultType(resultTypeOfPoW, totalResultOfToW);
                            resultSumOfToW = cursor.getFloat(10);
                            countOfPoW++;
                        } else if (eDB.getEmployerID() == employeeID) {
                            addResultTypeToCommonResultOfResultType();
                            towDescription = listForWorkWithResultTableItems.get(listForWorkWithResultTableItems.size() - 1).getTOWDescription();
                            if (countOfPoW > 0) {
                                transferDataFromPoWResultTypeToToWResultType(towDescription);
                                countOfPoW = 0;
                            } else transferDataToCommonResultType(commonResultOfResultType, totalResultOfToW);
                            addResRows(totalResultOfToW, towDescription, getApplication().getString(R.string.common_sum_on_type_of_work));
                            transferDataToCommonResultType(totalResultOfToW, totalResultOfResultType);
                            addToWRow(eDB);
                            resultSumOfToW = cursor.getFloat(10);
                            countOfToW++;
                        } else {
                            addResultTypeToCommonResultOfResultType();
                            towDescription = listForWorkWithResultTableItems.get(listForWorkWithResultTableItems.size() - 1).getTOWDescription();
                            if (countOfPoW > 0) transferDataFromPoWResultTypeToToWResultType(towDescription);
                            else transferDataToCommonResultType(commonResultOfResultType, totalResultOfToW);
                            if (countOfToW > 0) addResRows(totalResultOfToW, towDescription, getApplication().getString(R.string.common_sum_on_type_of_work));
                            transferDataToCommonResultType(totalResultOfToW, totalResultOfResultType);
                            addResRows(totalResultOfResultType, towDescription, getApplication().getString(R.string.total_sum_on_type_of_result_short));
                            countOfPoW = 0;
                            countOfToW = 0;
                            totalResultOfResultType.clear();
                            resultTypeOfPoW.clear();
                            totalResultOfToW.clear();
                            commonResultOfResultType.clear();
                            resultSumOfToW = cursor.getFloat(10);
                            addToWRow(eDB);
                        }
                        listForWorkWithResultTableItems.add(eDB);
                        setID(eDB);
                    } while (cursor.moveToNext());
                    isNeedAdd = true;
                    for (Map.Entry<ResultTypeInfo, Float> totalResultEntry : commonResultOfResultType.entrySet()) {
                        if (totalResultEntry.getKey().getResultTypeID() == resultTypeID) isNeedAdd = false;
                    }
                    if (isNeedAdd) addResultTypeToCommonResultOfResultType();
                    towDescription = listForWorkWithResultTableItems.get(listForWorkWithResultTableItems.size() - 1).getTOWDescription();
                    if (countOfPoW > 0) transferDataFromPoWResultTypeToToWResultType(towDescription);
                    else transferDataToCommonResultType(commonResultOfResultType, totalResultOfToW);
                    if (countOfToW > 0) addResRows(totalResultOfToW, towDescription, getApplication().getString(R.string.common_sum_on_type_of_work));
                    transferDataToCommonResultType(totalResultOfToW, totalResultOfResultType);
                    addResRows(totalResultOfResultType, towDescription, getApplication().getString(R.string.total_sum_on_type_of_result_short));
                }
            } catch (SQLiteException e) {
                EditDeleteDataActivity.mHandler.post(() ->
                        Toast.makeText(getApplication(), getApplication().getString(R.string.fail_attempt_about_load_data_from_table) + ": " +
                                e.getMessage(), Toast.LENGTH_SHORT).show());
                return;
            }
            visibleOfProgressBarForResultList.postValue(View.GONE);
            visibleOfRecyclerView.postValue(View.VISIBLE);
            stateOfRecyclerViewForResultList.postValue(ReportPreViewFragment.StateOfRecyclerView.LOAD);
        }
    }

    public LiveData<String> getEmployeeEditTextForResultListLD() {
        if (employeeEditTextForResultListLD == null) employeeEditTextForResultListLD = new MutableLiveData<>();
        return employeeEditTextForResultListLD;
    }

    public LiveData<String> getFirmEditTextForResultListLD() {
        if (firmEditTextForResultListLD == null) firmEditTextForResultListLD = new MutableLiveData<>();
        return firmEditTextForResultListLD;
    }

    public LiveData<String> getNoteEditTextLD() {
        if (noteEditTextForResultListLD == null) noteEditTextForResultListLD = new MutableLiveData<>();
        return noteEditTextForResultListLD;
    }

    public LiveData<Integer> getVisibleOfProgressBarLD() {
        if (visibleOfProgressBarForResultList == null) visibleOfProgressBarForResultList = new MutableLiveData<>();
        return visibleOfProgressBarForResultList;
    }

    public LiveData<Integer> getVisibleOfRecyclerViewLD() {
        if (visibleOfRecyclerView == null) visibleOfRecyclerView = new MutableLiveData<>();
        return visibleOfRecyclerView;
    }

    public LiveData<ReportPreViewFragment.StateOfRecyclerView> getStateOfRecyclerViewLD() {
        if (stateOfRecyclerViewForResultList == null) stateOfRecyclerViewForResultList = new MutableLiveData<>();
        return stateOfRecyclerViewForResultList;
    }

    public LiveData<PairOfItemPositions> getChangerColorOfViewHolder() {
        if (changerColorOfViewHolderLD == null) changerColorOfViewHolderLD = new MutableLiveData<>();
        return changerColorOfViewHolderLD;
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

    public void startSearchInResultTable() {
        LoadItemsThreadFromResultTable loadItemsThreadFromResultTable = new LoadItemsThreadFromResultTable();
        loadItemsThreadFromResultTable.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        loadItemsThreadFromResultTable.start();
    }

    private void notifyAboutLoadItems() {
        visibleOfProgressBarForResultList.setValue(View.GONE);
        visibleOfRecyclerView.setValue(View.VISIBLE);
        stateOfRecyclerViewForResultList.setValue(ReportPreViewFragment.StateOfRecyclerView.LOAD);
    }

    public void initializeResultList() {
        if (isNeedSearch) startSearchInResultTable();
        else notifyAboutLoadItems();
    }

    public void notifyEditTexts(int i) {
        if (i == -1) {
            employeeEditTextForResultListLD.setValue("");
            firmEditTextForResultListLD.setValue("");
            noteEditTextForResultListLD.setValue("");
        } else {
            employeeEditTextForResultListLD.setValue(listForWorkWithResultTableItems.get(i).getEmployerDescription());
            firmEditTextForResultListLD.setValue(listForWorkWithResultTableItems.get(i).getFirmDescription());
            noteEditTextForResultListLD.setValue(listForWorkWithResultTableItems.get(i).getNote());
        }
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

    public List<ComplexEntityForDB> getResultList() {
        return listForWorkWithResultTableItems;
    }

    public String getTypeOfWorkDescriptionAtPosition(int pos) {
        if (listForWorkWithResultTableItems.get(pos).isTypeOfWorkEntity()) return listForWorkWithResultTableItems.get(pos - 1).getTOWDescription();
        return listForWorkWithResultTableItems.get(pos).getTOWDescription();
    }

    public void resetInitializeOfLiveData() {
        visibleOfProgressBarForResultList = null;
        visibleOfRecyclerView = null;
        employeeEditTextForResultListLD = null;
        firmEditTextForResultListLD = null;
        noteEditTextForResultListLD = null;
        stateOfRecyclerViewForResultList = null;
        changerColorOfViewHolderLD = null;
        pairOfItemPositions = null;
    }

}
