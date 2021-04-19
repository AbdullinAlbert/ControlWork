package com.albertabdullin.controlwork.viewmodels;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Process;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.activities.EditDeleteDataActivity;
import com.albertabdullin.controlwork.db_of_app.CWDBHelper;
import com.albertabdullin.controlwork.fragments.DeleteDataFragment;
import com.albertabdullin.controlwork.fragments.ListDBItemsFragment;
import com.albertabdullin.controlwork.models.ComplexEntityForDB;
import com.albertabdullin.controlwork.models.PairOfItemPositions;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditDeleteDataVM extends AndroidViewModel implements DialogFragmentStateHolder {
    private String mQuery;
    private boolean isNeedSearch;
    private PairOfItemPositions pairOfItemPositions;
    private final List<ComplexEntityForDB> listForWorkWithResultTableItems = new ArrayList<>();
    private List<SimpleEntityForDB> listForWorkWithEmployerTableItems;
    private List<SimpleEntityForDB> listForWorkWithFirmTableItems;
    private List<SimpleEntityForDB> listForWorkWithPoWTableItems;
    private List<SimpleEntityForDB> listForWorkWithToWTableItems;
    private List<SimpleEntityForDB> listForWorkWithResTypesTableItems;
    private List<SimpleEntityForDB> cacheForAdapterList;
    private MutableLiveData<DeleteDataFragment.StateOfRecyclerView> stateOfRecyclerViewForResultList;
    private MutableLiveData<Integer> visibleOfProgressBarForResultList;
    private MutableLiveData<Integer> visibleOfRecyclerView;
    private MutableLiveData<Boolean> visibleOfEditMenuItem;
    private MutableLiveData<Integer> visibleOfTextViewResultValue;
    private MutableLiveData<String> employeeEditTextForResultListLD;
    private MutableLiveData<String> firmEditTextForResultListLD;
    private MutableLiveData<String> typeOfWorkEditTextForResultListLD;
    private MutableLiveData<String> noteEditTextForResultListLD;
    private MutableLiveData<String> employeeEditTextForEditDataLD;
    private MutableLiveData<String> firmEditTextForEditDataLD;
    private MutableLiveData<String> placeOfWorkEditTextForEditDataLD;
    private MutableLiveData<String> typeOfWorkEditTextForEditDataLD;
    private MutableLiveData<String> dateEditTextForEditDataLD;
    private MutableLiveData<String> resultEditTextForEditDataLD;
    private MutableLiveData<String> resultTypeEditTextForEditDataLD;
    private MutableLiveData<String> noteEditTextForEditDataLD;
    private MutableLiveData<DeleteDataFragment.StateOfRecyclerView> stateOfRecyclerViewForPrimaryList;
    private MutableLiveData<Integer> visibleOfRecyclerViewForPrimaryList;
    private MutableLiveData<Integer> visibleOfProgressBarForPrimaryTableList;
    private MutableLiveData<Boolean> stateOfSaveChangedDataButton;
    private MutableLiveData<PairOfItemPositions> changerColorOfViewHolderLD;
    private ListDBItemsFragment.TableNameForList selectedTable;
    private LoadItemsFromPrimaryTableThread loadItemsFromPrimaryTableThread;
    private LoadItemsThreadFromResultTable loadItemsThreadFromResultTable;
    private SearchItemsThread searchItemsThread;
    private SimpleEntityForDB mSelectedItemForChangeData;
    private ComplexEntityForDB itemForChangeDataInDB;
    private String textForResultEditText;
    private String textForNoteEditText;
    private boolean mStateOfChangeEmployerEditText = false;
    private boolean mStateOfChangeFirmEditText = false;
    private boolean mStateOfChangePoWEditText = false;
    private boolean mStateOfChangeToWEditText = false;
    private boolean mStateOfChangeDateEditText = false;
    private boolean mStateOfChangeResultEditText = false;
    private boolean mStateOfChangeResultTypeEditText = false;
    private boolean mStateOfChangeNoteEditText = false;
    private boolean pressedBackButton = false;
    private boolean stateMenuItemSearchText = false;
    private boolean isBlankCall = true;
    private List<Integer> listOfDeletedPositions;
    private Set<Integer> itemsOfST;
    private boolean isActivatedDF = false;
    private Executor mExecutor;

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

    private class LoadItemsThreadFromResultTable extends Thread {
        @Override
        public void run() {
            if (listForWorkWithResultTableItems.size() != 0) listForWorkWithResultTableItems.clear();
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
                        eDB.setNote(cursor.getString(b++));
                        eDB.setResultTypeID(cursor.getInt(b++));
                        eDB.setStringViewOfResultType(cursor.getString(b));
                        listForWorkWithResultTableItems.add(eDB);
                    } while (cursor.moveToNext());
                }
            } catch (SQLiteException e) {
                EditDeleteDataActivity.mHandler.post(() ->
                        Toast.makeText(getApplication(), getApplication().getString(R.string.fail_attempt_about_load_data_from_table) + ": " +
                                e.getMessage(), Toast.LENGTH_SHORT).show());
                return;
            }
            visibleOfProgressBarForResultList.postValue(View.GONE);
            visibleOfRecyclerView.postValue(View.VISIBLE);
            stateOfRecyclerViewForResultList.postValue(DeleteDataFragment.StateOfRecyclerView.LOAD);
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

    public void startSearchInResultTable() {
        if (mExecutor == null) mExecutor = Executors.newSingleThreadExecutor();
        loadItemsThreadFromResultTable = new LoadItemsThreadFromResultTable();
        loadItemsThreadFromResultTable.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        mExecutor.execute(loadItemsThreadFromResultTable);
    }

    private class DeleteItemThread extends Thread {
        private final List<ComplexEntityForDB> list;
        private final List<String> IDList = new ArrayList<>();

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
            if (listOfDeletedPositions == null) listOfDeletedPositions = new ArrayList<>();
            if (listOfDeletedPositions.size() > 0) listOfDeletedPositions.clear();
            for (ComplexEntityForDB entity : list) {
                IDList.add(entity.getID());
                listOfDeletedPositions.add(listForWorkWithResultTableItems.indexOf(entity));
            }
            String[] arguments = IDList.toString().replaceAll("[\\[\\]]", "").split(", ");
            String whereClause = makeWhereClause(listOfDeletedPositions.size());
            try (SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
                 SQLiteDatabase db = cwdbHelper.getWritableDatabase()) {
                count = db.delete(CWDBHelper.TABLE_NAME_RESULT, whereClause, arguments);
            } catch (SQLiteException e) {
                EditDeleteDataActivity.mHandler.post(() ->
                        Toast.makeText(getApplication(), getApplication().getString(R.string.fail_attempt_about_delete_data_from_db) + ": " +
                                e.getMessage(), Toast.LENGTH_SHORT).show());
                return;
            }
            if (count == list.size()) {
                listOfDeletedPositions.sort((o1, o2) -> (o1 - o2) * (-1));
                visibleOfProgressBarForResultList.postValue(View.GONE);
                visibleOfRecyclerView.postValue(View.VISIBLE);
                stateOfRecyclerViewForResultList.postValue(DeleteDataFragment.StateOfRecyclerView.DELETE);
            }
            else {
                EditDeleteDataActivity.mHandler.post(() ->
                        Toast.makeText(getApplication(), getApplication().getString(R.string.fail_attempt_about_delete__all_selected_data_from_db), Toast.LENGTH_SHORT).show());
                LoadItemsThreadFromResultTable loadItemsThreadFromResultTable = new LoadItemsThreadFromResultTable();
                loadItemsThreadFromResultTable.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
                mExecutor.execute(loadItemsThreadFromResultTable);
            }
        }
    }

    public void deleteItem(List<ComplexEntityForDB> list)  {
        visibleOfRecyclerView.setValue(View.INVISIBLE);
        visibleOfProgressBarForResultList.setValue(View.VISIBLE);
        DeleteItemThread deleteItemThread = new DeleteItemThread(list);
        deleteItemThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        mExecutor.execute(deleteItemThread);
    }

    public List<Integer> getDeletedPositionsFromDB() {
        return listOfDeletedPositions;
    }

    public LiveData<DeleteDataFragment.StateOfRecyclerView> getStateOfRecyclerViewLD() {
        if (stateOfRecyclerViewForResultList == null) stateOfRecyclerViewForResultList = new MutableLiveData<>();
        return stateOfRecyclerViewForResultList;
    }

    public LiveData<Integer> getVisibleOfProgressBarLD() {
        if (visibleOfProgressBarForResultList == null) visibleOfProgressBarForResultList = new MutableLiveData<>();
        return visibleOfProgressBarForResultList;
    }

    public LiveData<String> getEmployeeEditTextForResultListLD() {
        if (employeeEditTextForResultListLD == null) employeeEditTextForResultListLD = new MutableLiveData<>();
        return employeeEditTextForResultListLD;
    }

    public LiveData<String> getFirmEditTextForResultListLD() {
        if (firmEditTextForResultListLD == null) firmEditTextForResultListLD = new MutableLiveData<>();
        return firmEditTextForResultListLD;
    }

    public LiveData<String> getToWEditTextForResultListLD() {
        if (typeOfWorkEditTextForResultListLD == null) typeOfWorkEditTextForResultListLD = new MutableLiveData<>();
        return typeOfWorkEditTextForResultListLD;
    }

    public LiveData<String> getNoteEditTextLD() {
        if (noteEditTextForResultListLD == null) noteEditTextForResultListLD = new MutableLiveData<>();
        return noteEditTextForResultListLD;
    }

    public LiveData<PairOfItemPositions> getChangerColorOfViewHolder() {
        if (changerColorOfViewHolderLD == null) changerColorOfViewHolderLD = new MutableLiveData<>();
        return changerColorOfViewHolderLD;
    }

    public LiveData<Integer> getVisibleOfRecyclerViewLD() {
        if (visibleOfRecyclerView == null) visibleOfRecyclerView = new MutableLiveData<>();
        return visibleOfRecyclerView;
    }

    public LiveData<Boolean> getVisibleOfEditMenuItem() {
        if (visibleOfEditMenuItem == null) visibleOfEditMenuItem = new MutableLiveData<>();
        return visibleOfEditMenuItem;
    }

    public LiveData<String> getEmployeeEditTextForEditDataLD() {
        if (employeeEditTextForEditDataLD == null) employeeEditTextForEditDataLD = new MutableLiveData<>();
        employeeEditTextForEditDataLD
                    .setValue(listForWorkWithResultTableItems.get(pairOfItemPositions.getNewPos()).getEmployerDescription());
        return employeeEditTextForEditDataLD;
    }

    public LiveData<String> getFirmEditTextForEditDataLD() {
        if (firmEditTextForEditDataLD == null) firmEditTextForEditDataLD = new MutableLiveData<>();
        firmEditTextForEditDataLD
                    .setValue(listForWorkWithResultTableItems.get(pairOfItemPositions.getNewPos()).getFirmDescription());
        return firmEditTextForEditDataLD;
    }

    public LiveData<String> getPlaceOfWorkEditTextForEditDataLD() {
        if (placeOfWorkEditTextForEditDataLD == null) placeOfWorkEditTextForEditDataLD = new MutableLiveData<>();
        placeOfWorkEditTextForEditDataLD
                    .setValue(listForWorkWithResultTableItems.get(pairOfItemPositions.getNewPos()).getPOWDescription());
        return placeOfWorkEditTextForEditDataLD;
    }

    public LiveData<String> getTypeOfWorkEditTextForEditDataLD() {
        if (typeOfWorkEditTextForEditDataLD == null) typeOfWorkEditTextForEditDataLD = new MutableLiveData<>();
        typeOfWorkEditTextForEditDataLD
                    .setValue(listForWorkWithResultTableItems.get(pairOfItemPositions.getNewPos()).getTOWDescription());
        return typeOfWorkEditTextForEditDataLD;
    }

    public LiveData<String> getDateEditTextForEditDataLD() {
        if (dateEditTextForEditDataLD == null) dateEditTextForEditDataLD = new MutableLiveData<>();
        dateEditTextForEditDataLD
                    .setValue(listForWorkWithResultTableItems.get(pairOfItemPositions.getNewPos()).getDate());
        return dateEditTextForEditDataLD;
    }

    public LiveData<String> getResultEditTextForEditDataLD() {
        if (resultEditTextForEditDataLD == null) resultEditTextForEditDataLD = new MutableLiveData<>();
        if (textForResultEditText == null) resultEditTextForEditDataLD
                .setValue(listForWorkWithResultTableItems.get(pairOfItemPositions.getNewPos()).getResult());
        else resultEditTextForEditDataLD.setValue(textForResultEditText);
        return resultEditTextForEditDataLD;
    }

    public LiveData<String> getResultTypeEditTextForEditDataLD() {
        if (resultTypeEditTextForEditDataLD == null) resultTypeEditTextForEditDataLD = new MutableLiveData<>();
        resultTypeEditTextForEditDataLD
                    .setValue(listForWorkWithResultTableItems.get(pairOfItemPositions.getNewPos()).getStringViewOfResultType());
        return resultTypeEditTextForEditDataLD;
    }

   public LiveData<String> getNoteEditTextForEditDataLD() {
        if (noteEditTextForEditDataLD == null) noteEditTextForEditDataLD = new MutableLiveData<>();
        if (textForNoteEditText == null) noteEditTextForEditDataLD
                    .setValue(listForWorkWithResultTableItems.get(pairOfItemPositions.getNewPos()).getNote());
        else noteEditTextForEditDataLD.setValue(textForNoteEditText);
        return noteEditTextForEditDataLD;
    }

    public synchronized LiveData<DeleteDataFragment.StateOfRecyclerView> getStateOfRVForPrimaryTableLD() {
        if (stateOfRecyclerViewForPrimaryList == null) stateOfRecyclerViewForPrimaryList = new MutableLiveData<>();
        return stateOfRecyclerViewForPrimaryList;
    }

    public LiveData<Integer> getVisibleOfRVForPrimaryTableLD() {
        if (visibleOfRecyclerViewForPrimaryList == null) visibleOfRecyclerViewForPrimaryList = new MutableLiveData<>();
        return visibleOfRecyclerViewForPrimaryList;
    }

    public LiveData<Integer> getVisibleOfProgressBarForPrimaryTableListLD() {
        if (visibleOfProgressBarForPrimaryTableList == null) visibleOfProgressBarForPrimaryTableList = new MutableLiveData<>();
        return visibleOfProgressBarForPrimaryTableList;
    }

    public LiveData<Boolean> getStateOfSaveChangedDataButtonLD() {
        if (stateOfSaveChangedDataButton == null) stateOfSaveChangedDataButton = new MutableLiveData<>();
        return stateOfSaveChangedDataButton;
    }

    public LiveData<Integer> getVisibleOfTextViewResultValueLD() {
        if (visibleOfTextViewResultValue == null) {
            visibleOfTextViewResultValue = new MutableLiveData<>();
            visibleOfTextViewResultValue.setValue(View.INVISIBLE);
        }
        return visibleOfTextViewResultValue;
    }

    public void initItemForChangedDataInDB() {
        if (itemForChangeDataInDB == null) itemForChangeDataInDB = new ComplexEntityForDB();
    }

    private void notifyAboutLoadItems() {
        visibleOfProgressBarForResultList.setValue(View.GONE);
        visibleOfRecyclerView.setValue(View.VISIBLE);
        stateOfRecyclerViewForResultList.setValue(DeleteDataFragment.StateOfRecyclerView.LOAD);
    }

    //лист для адаптера в ListDBItemsFragment
    public List<ComplexEntityForDB> getResultList() {
        return listForWorkWithResultTableItems;
    }

    //инициализация списка из таблицы ResultList для DeleteDataFragment
    public void initializeResultList() {
        if (isNeedSearch) startSearchInResultTable();
        else notifyAboutLoadItems();
    }

    public void notifyEditTexts(int i) {
        if (i == -1) {
            if (visibleOfEditMenuItem != null) visibleOfEditMenuItem.setValue(false);
            employeeEditTextForResultListLD.setValue("");
            firmEditTextForResultListLD.setValue("");
            typeOfWorkEditTextForResultListLD.setValue("");
            noteEditTextForResultListLD.setValue("");
        } else {
            if (visibleOfEditMenuItem != null) visibleOfEditMenuItem.setValue(true);
            employeeEditTextForResultListLD.setValue(listForWorkWithResultTableItems.get(i).getEmployerDescription());
            firmEditTextForResultListLD.setValue(listForWorkWithResultTableItems.get(i).getFirmDescription());
            typeOfWorkEditTextForResultListLD.setValue(listForWorkWithResultTableItems.get(i).getTOWDescription());
            noteEditTextForResultListLD.setValue(listForWorkWithResultTableItems.get(i).getNote());
        }
    }

    public String getValueOfETLD() {
        return employeeEditTextForResultListLD.getValue() == null ? "" : employeeEditTextForResultListLD.getValue();
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

    public void setSelectedTable(ListDBItemsFragment.TableNameForList tableName) {
        selectedTable = tableName;
    }

    public ListDBItemsFragment.TableNameForList getSelectedTable() { return selectedTable; }

    private synchronized boolean isLiveDataForPrimaryTableListNull() {
        return stateOfRecyclerViewForPrimaryList == null;
    }

    private class LoadItemsFromPrimaryTableThread extends Thread {

        private final String mTableName;
        private final String mColumnName;
        private final List<SimpleEntityForDB> mListForLoadData;

        LoadItemsFromPrimaryTableThread(String tableName, String columnName, List<SimpleEntityForDB> listForLoadData) {
            mTableName = tableName;
            mColumnName = columnName;
            mListForLoadData = listForLoadData;
        }

        @Override
        public void run() {
            try (SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
                 SQLiteDatabase db = cwdbHelper.getReadableDatabase();
                 Cursor cursor = db.query(mTableName,
                    new String[]{"_id", mColumnName},
                    null, null, null, null, null)
            ) {
                if (cursor.moveToFirst()) {
                    do {
                        SimpleEntityForDB eDB = new SimpleEntityForDB();
                        eDB.setId(cursor.getInt(0));
                        eDB.setDescription(cursor.getString(1));
                        mListForLoadData.add(eDB);
                    } while (cursor.moveToNext());
                }
            } catch (SQLiteException e) {
                EditDeleteDataActivity.mHandler.sendEmptyMessage(EditDeleteDataActivity.FAIL_ABOUT_LOAD_DATA_FROM_PRIMARY_TABLE);
            }
            if (isLiveDataForPrimaryTableListNull()) {
                do {
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (isLiveDataForPrimaryTableListNull());
            }
            stateOfRecyclerViewForPrimaryList.postValue(DeleteDataFragment.StateOfRecyclerView.LOAD);
            visibleOfRecyclerViewForPrimaryList.postValue(View.VISIBLE);
            visibleOfProgressBarForPrimaryTableList.postValue(View.GONE);
        }
    }

    private void initializeViewsFromListDBItemsFragment() {
        stateOfRecyclerViewForPrimaryList.setValue(DeleteDataFragment.StateOfRecyclerView.LOAD);
        visibleOfRecyclerViewForPrimaryList.setValue(View.VISIBLE);
        visibleOfProgressBarForPrimaryTableList.setValue(View.GONE);
    }

    public void startLoadDataFromTable() {
        switch (selectedTable) {
            case EMPLOYEES:
                if (listForWorkWithEmployerTableItems == null) {
                    listForWorkWithEmployerTableItems = new ArrayList<>();
                    loadItemsFromPrimaryTableThread = new LoadItemsFromPrimaryTableThread(
                            CWDBHelper.TABLE_NAME_EMP, CWDBHelper.T_EMP_C_FIO, listForWorkWithEmployerTableItems);
                    break;
                } else {
                    initializeViewsFromListDBItemsFragment();
                    return;
                }
            case FIRMS:
                if (listForWorkWithFirmTableItems == null) {
                    listForWorkWithFirmTableItems = new ArrayList<>();
                    loadItemsFromPrimaryTableThread = new LoadItemsFromPrimaryTableThread(
                            CWDBHelper.TABLE_NAME_FIRM, CWDBHelper.T_FIRM_C_DESCRIPTION, listForWorkWithFirmTableItems);
                    break;
                } else {
                    initializeViewsFromListDBItemsFragment();
                    return;
                }
            case POW:
                if (listForWorkWithPoWTableItems == null) {
                    listForWorkWithPoWTableItems = new ArrayList<>();
                    loadItemsFromPrimaryTableThread = new LoadItemsFromPrimaryTableThread(
                            CWDBHelper.TABLE_NAME_PLACE_OF_WORK, CWDBHelper.T_PLACE_OF_WORK_C_DESCRIPTION, listForWorkWithPoWTableItems);
                    break;
                } else {
                    initializeViewsFromListDBItemsFragment();
                    return;
                }
            case TOW:
                if (listForWorkWithToWTableItems == null) {
                    listForWorkWithToWTableItems = new ArrayList<>();
                    loadItemsFromPrimaryTableThread = new LoadItemsFromPrimaryTableThread(
                            CWDBHelper.TABLE_NAME_TYPE_OF_WORK, CWDBHelper.T_TYPE_OF_WORK_C_DESCRIPTION, listForWorkWithToWTableItems);
                    break;
                } else {
                    initializeViewsFromListDBItemsFragment();
                    return;
                }
            case RESULT_TYPES:
                if (listForWorkWithResTypesTableItems == null) {
                    listForWorkWithResTypesTableItems = new ArrayList<>();
                    loadItemsFromPrimaryTableThread = new LoadItemsFromPrimaryTableThread(
                            CWDBHelper.TABLE_NAME_RESULT_TYPE, CWDBHelper.T_RESULT_TYPE_C_RESULT_TYPE, listForWorkWithResTypesTableItems);
                    break;
                } else {
                    initializeViewsFromListDBItemsFragment();
                    return;
                }
        }
        loadItemsFromPrimaryTableThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        mExecutor.execute(loadItemsFromPrimaryTableThread);
    }

    public List<SimpleEntityForDB> getCurrentListForPrimaryTable() {
        switch (selectedTable) {
            case EMPLOYEES: return listForWorkWithEmployerTableItems;
            case FIRMS: return listForWorkWithFirmTableItems;
            case POW: return listForWorkWithPoWTableItems;
            case TOW: return listForWorkWithToWTableItems;
            case RESULT_TYPES: return listForWorkWithResTypesTableItems;
            default:
                throw new RuntimeException("Опечатка в enum-классе TableNameForList. selectedTable -" + selectedTable);
        }
    }

    public void setSelectedItemForChangeData(SimpleEntityForDB eDB) {
        mSelectedItemForChangeData = eDB;
    }

    public void tryToStopLoadDataFromPrimaryTableThread() {
        if (loadItemsFromPrimaryTableThread.isAlive())
            loadItemsFromPrimaryTableThread.interrupt();
    }

    public void tryToStopLoadDataFromResultTableThread() {
        if (loadItemsThreadFromResultTable.isAlive())
            loadItemsThreadFromResultTable.interrupt();
    }
    

    public void setDefaultValuesToListDBItemsFragmentViews() {
        stateOfRecyclerViewForPrimaryList.setValue(null);
        visibleOfRecyclerViewForPrimaryList.setValue(View.INVISIBLE);
        visibleOfProgressBarForPrimaryTableList.postValue(View.VISIBLE);
    }

    public void setDefaultValuesToEditDataFragmentViews() {
        textForResultEditText = null;
        textForNoteEditText = null;
        itemForChangeDataInDB = null;
        employeeEditTextForEditDataLD = null;
        firmEditTextForEditDataLD = null;
        placeOfWorkEditTextForEditDataLD = null;
        typeOfWorkEditTextForEditDataLD = null;
        dateEditTextForEditDataLD = null;
        mStateOfChangeDateEditText = false;
        mStateOfChangeEmployerEditText = false;
        mStateOfChangeFirmEditText = false;
        mStateOfChangePoWEditText = false;
        mStateOfChangeToWEditText = false;
        mStateOfChangeResultEditText = false;
        mStateOfChangeResultTypeEditText = false;
        mStateOfChangeNoteEditText = false;
    }

    public void setPressedBackButton(boolean b) { pressedBackButton = b; }

    public boolean isBackButtonNotPressed() { return !pressedBackButton; }

    private void attemptToChangeValueOfEmployerData() {
        mStateOfChangeEmployerEditText = mSelectedItemForChangeData.getID() !=
                listForWorkWithResultTableItems.get(pairOfItemPositions.getNewPos()).getEmployerID();
        employeeEditTextForEditDataLD.setValue(mSelectedItemForChangeData.getDescription());
        if (mStateOfChangeEmployerEditText) {
            itemForChangeDataInDB.setEmployerID(mSelectedItemForChangeData.getID());
            itemForChangeDataInDB.setEmployerDescription(mSelectedItemForChangeData.getDescription());
        } else {
            itemForChangeDataInDB.setEmployerID(0);
            itemForChangeDataInDB.setEmployerDescription("");
        }
    }

    private void attemptToChangeValueOfFirmData() {
        mStateOfChangeFirmEditText = mSelectedItemForChangeData.getID() !=
                listForWorkWithResultTableItems.get(pairOfItemPositions.getNewPos()).getFirmID();
        firmEditTextForEditDataLD.setValue(mSelectedItemForChangeData.getDescription());
        if (mStateOfChangeFirmEditText) {
            itemForChangeDataInDB.setFirmID(mSelectedItemForChangeData.getID());
            itemForChangeDataInDB.setFirmDescription(mSelectedItemForChangeData.getDescription());
        } else {
            itemForChangeDataInDB.setFirmID(0);
            itemForChangeDataInDB.setFirmDescription("");
        }
    }

    private void attemptToChangeValueOfPoWData() {
        mStateOfChangePoWEditText = mSelectedItemForChangeData.getID() !=
                listForWorkWithResultTableItems.get(pairOfItemPositions.getNewPos()).getPlaceOfWorkID();
        placeOfWorkEditTextForEditDataLD.setValue(mSelectedItemForChangeData.getDescription());
        if (mStateOfChangePoWEditText) {
            itemForChangeDataInDB.setPlaceOfWorkID(mSelectedItemForChangeData.getID());
            itemForChangeDataInDB.setPOWDescription(mSelectedItemForChangeData.getDescription());
        } else {
            itemForChangeDataInDB.setPlaceOfWorkID(0);
            itemForChangeDataInDB.setPOWDescription("");
        }
    }

    private void attemptToChangeValueOfToWData() {
        mStateOfChangeToWEditText = mSelectedItemForChangeData.getID() !=
                listForWorkWithResultTableItems.get(pairOfItemPositions.getNewPos()).getTypeOfWorkID();
        typeOfWorkEditTextForEditDataLD.setValue(mSelectedItemForChangeData.getDescription());
        if (mStateOfChangeToWEditText) {
            itemForChangeDataInDB.setTypeOfWorkID(mSelectedItemForChangeData.getID());
            itemForChangeDataInDB.setTOWDescription(mSelectedItemForChangeData.getDescription());
        } else {
            itemForChangeDataInDB.setTypeOfWorkID(0);
            itemForChangeDataInDB.setTOWDescription("");
        }
    }

    private void attemptToChangeValueOfResultTypeData() {
        mStateOfChangeResultTypeEditText = mSelectedItemForChangeData.getID() !=
                listForWorkWithResultTableItems.get(pairOfItemPositions.getNewPos()).getResultTypeID();
        resultTypeEditTextForEditDataLD.setValue(mSelectedItemForChangeData.getDescription());
        if (mStateOfChangeResultTypeEditText) {
            itemForChangeDataInDB.setResultTypeID(mSelectedItemForChangeData.getID());
            itemForChangeDataInDB.setStringViewOfResultType(mSelectedItemForChangeData.getDescription());
        } else {
            itemForChangeDataInDB.setResultTypeID(0);
            itemForChangeDataInDB.setStringViewOfResultType("");
        }
    }

    public void tryToChangeStateOfSaveChangedDataButton() {
        if (selectedTable != null && mSelectedItemForChangeData != null) {
            switch (selectedTable) {
                case EMPLOYEES:
                    attemptToChangeValueOfEmployerData();
                    break;
                case FIRMS:
                    attemptToChangeValueOfFirmData();
                    break;
                case POW:
                    attemptToChangeValueOfPoWData();
                    break;
                case TOW:
                    attemptToChangeValueOfToWData();
                    break;
                case RESULT_TYPES:
                    attemptToChangeValueOfResultTypeData();
                    break;
            }
            changeStateOfSaveChangedDataButton();
        }
    }

    public void attemptToChangeValueOfResultData(Editable s) {
        if (visibleOfTextViewResultValue.getValue() != null && visibleOfTextViewResultValue.getValue() != View.INVISIBLE)
            visibleOfTextViewResultValue.setValue(View.INVISIBLE);
        mStateOfChangeResultEditText = !(s.toString().equals(listForWorkWithResultTableItems.get(pairOfItemPositions.getNewPos()).getResult()));
        if (mStateOfChangeResultEditText) {
            textForResultEditText = s.toString();
            itemForChangeDataInDB.setResult(textForResultEditText);
        } else itemForChangeDataInDB.setResult("");
        changeStateOfSaveChangedDataButton();
    }

    public void attemptToChangeValueOfNoteData(Editable s) {
        mStateOfChangeNoteEditText = !(s.toString().equals(listForWorkWithResultTableItems.get(pairOfItemPositions.getNewPos()).getNote()));
        if (mStateOfChangeNoteEditText) {
            textForNoteEditText = s.toString();
            itemForChangeDataInDB.setNote(textForNoteEditText);
        } else itemForChangeDataInDB.setNote(null);
        changeStateOfSaveChangedDataButton();
    }

    public void attemptToChangeValueOfDateData(String stringDate, long longDate) {
        mStateOfChangeDateEditText = !(stringDate.equals(listForWorkWithResultTableItems.get(pairOfItemPositions.getNewPos()).getDate()));
        if (mStateOfChangeDateEditText) {
            itemForChangeDataInDB.setDate(stringDate);
            dateEditTextForEditDataLD.setValue(stringDate);
            itemForChangeDataInDB.setLongPresentationOfDate(longDate / 1000);
        } else itemForChangeDataInDB.setLongPresentationOfDate(0);
        changeStateOfSaveChangedDataButton();
    }

    private void changeStateOfSaveChangedDataButton() {
        boolean valueForButton = (mStateOfChangeEmployerEditText | mStateOfChangeFirmEditText | mStateOfChangePoWEditText |
                mStateOfChangeToWEditText | mStateOfChangeDateEditText | mStateOfChangeResultEditText | mStateOfChangeNoteEditText | mStateOfChangeResultTypeEditText);
        if (stateOfSaveChangedDataButton.getValue() == null || stateOfSaveChangedDataButton.getValue() != valueForButton)
            stateOfSaveChangedDataButton.setValue(valueForButton);
    }

    private class UpdateItemThread extends Thread {
        @Override
        public void run() {
            try {
              if (!itemForChangeDataInDB.getResult().equals("")) Float.parseFloat(itemForChangeDataInDB.getResult());
            } catch (NumberFormatException e) {
                visibleOfTextViewResultValue.postValue(View.VISIBLE);
                return;
            }
            ContentValues cv = new ContentValues();
            if (itemForChangeDataInDB.getEmployerID() != 0)
                cv.put(CWDBHelper.T_RESULT_C_ID_EMPLOYER, itemForChangeDataInDB.getEmployerID());
            if (itemForChangeDataInDB.getFirmID() != 0)
                cv.put(CWDBHelper.T_RESULT_C_ID_FIRM, itemForChangeDataInDB.getFirmID());
            if (itemForChangeDataInDB.getPlaceOfWorkID() != 0)
                cv.put(CWDBHelper.T_RESULT_C_ID_POW, itemForChangeDataInDB.getPlaceOfWorkID());
            if (itemForChangeDataInDB.getTypeOfWorkID() != 0)
                cv.put(CWDBHelper.T_RESULT_C_ID_TOW, itemForChangeDataInDB.getTypeOfWorkID());
            if (itemForChangeDataInDB.getLongPresentationOfDate() != 0)
                cv.put(CWDBHelper.T_RESULT_C_DATE, itemForChangeDataInDB.getLongPresentationOfDate());
            if (!itemForChangeDataInDB.getResult().equals(""))
                cv.put(CWDBHelper.T_RESULT_C_VALUE, Float.parseFloat(itemForChangeDataInDB.getResult()));
            if (itemForChangeDataInDB.getResultTypeID() != 0)
                cv.put(CWDBHelper.T_RESULT_C_RESULT_TYPE, itemForChangeDataInDB.getResultTypeID());
            if (itemForChangeDataInDB.getNote() != null)
                cv.put(CWDBHelper.T_RESULT_C_NOTE, itemForChangeDataInDB.getNote());
            int idKey;
            String resID = listForWorkWithResultTableItems.get(pairOfItemPositions.getNewPos()).getID();
            try (SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
                 SQLiteDatabase db = cwdbHelper.getWritableDatabase()) {
                idKey = db.update(CWDBHelper.TABLE_NAME_RESULT,
                        cv,
                        "_id = ?",
                        new String[] { resID });
            } catch (SQLiteException e) {
                EditDeleteDataActivity.mHandler.post(() ->
                        Toast.makeText(getApplication(), getApplication().getString(R.string.fail_attempt_about_update_data_in_result_table) + " " +
                                e.getMessage(), Toast.LENGTH_SHORT).show());
                return;
            }
            if(idKey != 0) {
                ComplexEntityForDB shortLinkToObject = listForWorkWithResultTableItems.get(pairOfItemPositions.getNewPos());
                if (itemForChangeDataInDB.getEmployerID() != 0) {
                    shortLinkToObject.setEmployerID(itemForChangeDataInDB.getEmployerID());
                    shortLinkToObject.setEmployerDescription(itemForChangeDataInDB.getEmployerDescription());
                    employeeEditTextForResultListLD.postValue(itemForChangeDataInDB.getEmployerDescription());
                }
                if (itemForChangeDataInDB.getFirmID() != 0) {
                    shortLinkToObject.setFirmID(itemForChangeDataInDB.getFirmID());
                    shortLinkToObject.setFirmDescription(itemForChangeDataInDB.getFirmDescription());
                    firmEditTextForResultListLD.postValue(itemForChangeDataInDB.getFirmDescription());
                }
                if (itemForChangeDataInDB.getPlaceOfWorkID() != 0) {
                    shortLinkToObject.setPlaceOfWorkID(itemForChangeDataInDB.getPlaceOfWorkID());
                    shortLinkToObject.setPOWDescription(itemForChangeDataInDB.getPOWDescription());
                }
                if (itemForChangeDataInDB.getTypeOfWorkID() != 0) {
                    shortLinkToObject.setTypeOfWorkID(itemForChangeDataInDB.getTypeOfWorkID());
                    shortLinkToObject.setTOWDescription(itemForChangeDataInDB.getTOWDescription());
                }
                if (itemForChangeDataInDB.getLongPresentationOfDate() != 0)
                    shortLinkToObject.setDate(itemForChangeDataInDB.getDate());
                if (!itemForChangeDataInDB.getResult().equals(""))
                    shortLinkToObject.setResult(itemForChangeDataInDB.getResult());
                if (itemForChangeDataInDB.getResultTypeID() != 0) {
                    shortLinkToObject.setResultTypeID(itemForChangeDataInDB.getResultTypeID());
                    shortLinkToObject.setStringViewOfResultType(itemForChangeDataInDB.getStringViewOfResultType());
                }
                if (itemForChangeDataInDB.getNote() != null)
                    shortLinkToObject.setNote(itemForChangeDataInDB.getNote());
                EditDeleteDataActivity.mHandler.post(() ->
                    Toast.makeText(getApplication(), getApplication().getString(R.string.data_has_been_updated),
                                Toast.LENGTH_SHORT).show());
            } else EditDeleteDataActivity.mHandler.post(() ->
                    Toast.makeText(getApplication(), getApplication().getString(R.string.fail_attempt_about_update_data_in_result_table),
                            Toast.LENGTH_SHORT).show());
        }
    }

    public void tryToSaveChangedData() {
        stateOfSaveChangedDataButton.setValue(false);
        UpdateItemThread updateItemThread = new UpdateItemThread();
        updateItemThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        mExecutor.execute(updateItemThread);
    }

    public void setStateMenuItemSearchText(boolean b) {
        stateMenuItemSearchText = b;
    }

    public boolean isStateMenuItemSearchTextActive() {
        return stateMenuItemSearchText;
    }

    private class SearchItemsThread extends Thread {
        public static final String TAG_SEARCH_TREAD = "SearchItemsThread";
        private final BlockingQueue<String> store = new ArrayBlockingQueue<>(1);
        private String pattern, regEx, hPattern = "";
        private final AtomicBoolean isStopSearch = new AtomicBoolean(false);
        private Pattern p;
        private Matcher m;

        SearchItemsThread(String pattern) {
            try {
                store.put(pattern);
            } catch (InterruptedException e) {
                Log.e(TAG_SEARCH_TREAD, "Поток прервался: " + e.toString());
                return;
            }
            cacheForAdapterList = new ArrayList<>(getCurrentListForPrimaryTable());
        }

        public void setNewPattern(String newPattern) {
            if (!store.isEmpty()) store.clear();
            try {
                store.put(newPattern);
            } catch (InterruptedException e) {
                Log.e(TAG_SEARCH_TREAD, "Поток прервался: " + e.toString());
                //Вернуть полный список элементов в List адаптер
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
            getCurrentListForPrimaryTable().clear();
            while (i < cacheForAdapterList.size()) {
                m = p.matcher(cacheForAdapterList.get(i).getDescription());
                if (m.find()) getCurrentListForPrimaryTable().add(cacheForAdapterList.get(i));
                i++;
                if (!store.isEmpty()) {
                    hPattern = store.poll();
                    regEx = "(?i)" + hPattern;
                    p = Pattern.compile(regEx);
                    if (hPattern.contains(pattern)) searchInFilteredList();
                    else {
                        i = 0;
                        getCurrentListForPrimaryTable().clear();
                    }
                }
                if (isStopSearch.get()) break;
            }
        }

        private void searchInFilteredList() {
            List<SimpleEntityForDB> helperFoundItemsList = new ArrayList<>();
            for (int j = 0; j < getCurrentListForPrimaryTable().size(); j++) {
                m = p.matcher(getCurrentListForPrimaryTable().get(j).getDescription());
                if (m.find()) helperFoundItemsList.add(getCurrentListForPrimaryTable().get(j));
            }
            getCurrentListForPrimaryTable().clear();
            getCurrentListForPrimaryTable().addAll(helperFoundItemsList);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    pattern = store.take();
                } catch (InterruptedException e) {
                    Log.e(TAG_SEARCH_TREAD, "Поток прервался: " + e.toString());
                    return;
                    //Вернуть полный список элементов в List адаптера
                }
                if (pattern.equals("")) break;
                regEx = "(?i)" + pattern;
                p = Pattern.compile(regEx);
                isStopSearch.set(false);
                if (!hPattern.equals("") && pattern.contains(hPattern)) searchInFilteredList();
                else searchInFullList();
                if (!isStopSearch.get()) {
                    stateOfRecyclerViewForPrimaryList.postValue(DeleteDataFragment.StateOfRecyclerView.LOAD);
                } else {
                    if (!store.isEmpty()) store.clear();
                    isStopSearch.set(false);
                    getCurrentListForPrimaryTable().clear();
                }
                if (!hPattern.contains(pattern)) hPattern = pattern;
            }
        }
    }

    public void startSearchInResultTable(String pattern) {
        searchItemsThread = new SearchItemsThread(pattern);
        searchItemsThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        mExecutor.execute(searchItemsThread);
    }

    public boolean isSearchIsActive() {
        if (searchItemsThread == null) return false;
        else return searchItemsThread.isAlive();
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
            if (cacheForAdapterList.size() == getCurrentListForPrimaryTable().size()) return;
            getCurrentListForPrimaryTable().clear();
            getCurrentListForPrimaryTable().addAll(cacheForAdapterList);
            stateOfRecyclerViewForPrimaryList.setValue(DeleteDataFragment.StateOfRecyclerView.LOAD);
        }
    }

    public void closeSearchThread() {
        if (searchItemsThread != null) searchItemsThread.closeThread();
        searchItemsThread = null;
        isBlankCall = true;
        if (cacheForAdapterList != null) cacheForAdapterList.clear();
    }

}
