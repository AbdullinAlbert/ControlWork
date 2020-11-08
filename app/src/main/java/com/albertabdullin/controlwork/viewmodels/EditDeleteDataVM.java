package com.albertabdullin.controlwork.viewmodels;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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
import com.albertabdullin.controlwork.fragments.SearchCriteriaFragment;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;

import java.util.ArrayList;
import java.util.List;


public class EditDeleteDataVM extends AndroidViewModel {
    private List<SimpleEntityForDB> hListForWorkWithDB = new ArrayList<>();
    private List<SimpleEntityForDB> adapterListOfEmployees = new ArrayList<>();
    private List<SimpleEntityForDB> listOfSelectedEmployees = new ArrayList<>();
    private List<SimpleEntityForDB> transientListOfSelectedEmployees = new ArrayList<>();
    private List<SimpleEntityForDB> adapterListOfFirms = new ArrayList<>();
    private List<SimpleEntityForDB> listOfSelectedFirms = new ArrayList<>();
    private List<SimpleEntityForDB> transientListOfSelectedFirms = new ArrayList<>();
    private List<SimpleEntityForDB> adapterListOfTOW = new ArrayList<>();
    private List<SimpleEntityForDB> listOfSelectedTOW = new ArrayList<>();
    private List<SimpleEntityForDB> transientListOfSelectedTOW = new ArrayList<>();
    private List<SimpleEntityForDB> adapterListOfPOW = new ArrayList<>();
    private List<SimpleEntityForDB> listOfSelectedPOW = new ArrayList<>();
    private List<SimpleEntityForDB> transientListOfSelectedPOW = new ArrayList<>();
    private MutableLiveData<List<SimpleEntityForDB>> entitiesLD;
    private MutableLiveData<String> employeesEditTextLD;
    private MutableLiveData<String> firmsEditTextLD;
    private MutableLiveData<String> typesOfWorkEditTextLD;
    private MutableLiveData<String> placesOfWorkEditTextLD;
    private MutableLiveData<Boolean> selectedCheckBoxesLD;
    private MutableLiveData<Integer> visibilityOfClearButtonLD;
    private MutableLiveData<Integer> selectedEqualSignLD;
    private boolean isClearButtonInvisible = true;
    private int selectedEqualSign = -1;

    public EditDeleteDataVM(@NonNull Application application) {
        super(application);
    }

    private class SelectItemsTread extends Thread {
        private String mCurrentNameOfTable;
        private String mCurrentNameOfColumn;
        public static final String LOAD_ITEMS_TAG = "SelectItemsTreadVM";

        public SelectItemsTread(String currentNameOfTable, String currentNameOfColumn) {
            mCurrentNameOfTable = currentNameOfTable;
            mCurrentNameOfColumn = currentNameOfColumn;
        }

        @Override
        public void run() {
            hListForWorkWithDB.clear();
            Message msg;
            CWDBHelper cwdbHelper = new CWDBHelper(getApplication());
            try (SQLiteDatabase db = cwdbHelper.getReadableDatabase(); Cursor cursor = db.query(mCurrentNameOfTable,
                    new String[]{"_id", mCurrentNameOfColumn},
                    null, null, null, null, null)) {
                if (cursor.moveToFirst()) {
                    do {
                        SimpleEntityForDB eDB = new SimpleEntityForDB(cursor.getInt(0), cursor.getString(1));
                        hListForWorkWithDB.add(eDB);
                    } while (cursor.moveToNext());
                }
            } catch (SQLiteException e) {
                Log.e(LOAD_ITEMS_TAG, "не получилось прочесть данные из таблицы " + mCurrentNameOfTable);
                msg = EditDeleteDataActivity.mHandler.obtainMessage(EditDeleteDataActivity.LIST_OF_ENTITIES_IS_READY);
                EditDeleteDataActivity.mHandler.sendMessage(msg);
                return;
            }
            msg = EditDeleteDataActivity.mHandler.obtainMessage(EditDeleteDataActivity.LIST_OF_ENTITIES_IS_READY, 1,0);
            if (isEntitiesLDNull()) do {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (isEntitiesLDNull());
            EditDeleteDataActivity.mHandler.sendMessage(msg);
        }
    }

    private synchronized boolean isEntitiesLDNull() {
        return entitiesLD == null;
    }

    public synchronized LiveData<List<SimpleEntityForDB>> getEntitiesLiveData() {
        if (entitiesLD == null) entitiesLD = new MutableLiveData<>();
        return entitiesLD;
    }

    public LiveData<String> getEmployeesEditTextLD() {
        if (employeesEditTextLD == null) employeesEditTextLD = new MutableLiveData<>();
        return employeesEditTextLD;
    }

    public LiveData<String> getFirmsEditTextLD() {
        if (firmsEditTextLD == null) firmsEditTextLD = new MutableLiveData<>();
        return firmsEditTextLD;
    }

    public LiveData<String> getToWEditTextLD() {
        if (typesOfWorkEditTextLD == null) typesOfWorkEditTextLD = new MutableLiveData<>();
        return typesOfWorkEditTextLD;
    }

    public LiveData<String> getPoWEditTextLD() {
        if (placesOfWorkEditTextLD == null) placesOfWorkEditTextLD = new MutableLiveData<>();
        return placesOfWorkEditTextLD;
    }

    public LiveData<Boolean> getSelectedCheckBoxesLD() {
        if(selectedCheckBoxesLD == null) selectedCheckBoxesLD = new MutableLiveData<>();
        return selectedCheckBoxesLD;
    }

    public LiveData<Integer> getVisibilityOfClearButtonLD() {
        if(visibilityOfClearButtonLD == null) visibilityOfClearButtonLD = new MutableLiveData<>();
        return visibilityOfClearButtonLD;

    }

    public LiveData<Integer> getSelectedEqualSignLD() {
        if(selectedEqualSignLD == null) selectedEqualSignLD = new MutableLiveData<>();
        return selectedEqualSignLD;
    }

    public void showFullListOfItems(int selectedTable) {
        List<SimpleEntityForDB> hList;
        String tableName;
        String columnName;
        switch (selectedTable) {
            case SearchCriteriaFragment.SELECT_EMPLOYEES:
                hList = adapterListOfEmployees;
                tableName = CWDBHelper.TABLE_NAME_EMP;
                columnName = CWDBHelper.T_EMP_C_FIO;
                break;
            case SearchCriteriaFragment.SELECT_FIRMS:
                hList = adapterListOfFirms;
                tableName = CWDBHelper.TABLE_NAME_FIRM;
                columnName = CWDBHelper.T_FIRM_C_DESCRIPTION;
                break;
            case SearchCriteriaFragment.SELECT_TYPES:
                hList = adapterListOfTOW;
                tableName = CWDBHelper.TABLE_NAME_TYPE_OF_WORK;
                columnName = CWDBHelper.T_TYPE_OF_WORK_C_DESCRIPTION;
                break;
            case SearchCriteriaFragment.SELECT_PLACES:
                hList = adapterListOfPOW;
                tableName = CWDBHelper.TABLE_NAME_PLACE_OF_WORK;
                columnName = CWDBHelper.T_PLACE_OF_WORK_C_DESCRIPTION;
                break;
            default:
                throw new RuntimeException("опечатка в константах: showFullListOfItems(int selectedTable)");
        }
        if (hList.isEmpty()) {
            SelectItemsTread selectItemsTread = new SelectItemsTread(tableName, columnName);
            selectItemsTread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
            selectItemsTread.start();
        } else entitiesLD.setValue(hList);
    }

    public List<SimpleEntityForDB> getAdapterListOfEntities(int selectableTable) {
        switch (selectableTable) {
            case SearchCriteriaFragment.SELECT_EMPLOYEES:
                return adapterListOfEmployees;
            case SearchCriteriaFragment.SELECT_FIRMS:
                return adapterListOfFirms;
            case SearchCriteriaFragment.SELECT_TYPES:
                return adapterListOfTOW;
            case SearchCriteriaFragment.SELECT_PLACES:
                return adapterListOfPOW;
            default: return null;
        }
    }

    public List<SimpleEntityForDB> getTransientListOfSelectedItems(int selectableTable) {
        switch (selectableTable) {
            case SearchCriteriaFragment.SELECT_EMPLOYEES:
                return transientListOfSelectedEmployees;
            case SearchCriteriaFragment.SELECT_FIRMS:
                return transientListOfSelectedFirms;
            case SearchCriteriaFragment.SELECT_TYPES:
                return transientListOfSelectedTOW;
            case SearchCriteriaFragment.SELECT_PLACES:
                return transientListOfSelectedPOW;
            default:
                throw new RuntimeException("ошибка в константах: getTransientListOfSelectedItems(int selectableTable)");
        }
    }

    public void commitSelectedList(int selectableTable) {
        StringBuilder sb = new StringBuilder();
        List<SimpleEntityForDB> permanentListOfSelectedItems;
        List<SimpleEntityForDB> transientListOfSelectedItems;
        MutableLiveData<String> helperLD;
        switch (selectableTable) {
            case SearchCriteriaFragment.SELECT_EMPLOYEES:
                permanentListOfSelectedItems = listOfSelectedEmployees;
                transientListOfSelectedItems = transientListOfSelectedEmployees;
                helperLD = employeesEditTextLD;
                break;
            case SearchCriteriaFragment.SELECT_FIRMS:
                permanentListOfSelectedItems = listOfSelectedFirms;
                transientListOfSelectedItems = transientListOfSelectedFirms;
                helperLD = firmsEditTextLD;
                break;
            case SearchCriteriaFragment.SELECT_TYPES:
                permanentListOfSelectedItems = listOfSelectedTOW;
                transientListOfSelectedItems = transientListOfSelectedTOW;
                helperLD = typesOfWorkEditTextLD;
                break;
            case SearchCriteriaFragment.SELECT_PLACES:
                permanentListOfSelectedItems = listOfSelectedPOW;
                transientListOfSelectedItems = transientListOfSelectedPOW;
                helperLD = placesOfWorkEditTextLD;
                break;
            default:
                throw new RuntimeException("опечатка в константах: commitSelectedList(int selectableTable)");
        }
        permanentListOfSelectedItems.clear();
        permanentListOfSelectedItems.addAll(transientListOfSelectedItems);
        if (!transientListOfSelectedItems.isEmpty()) {
            for (int i = 0; i < transientListOfSelectedItems.size() - 1; i++) {
                sb.append(transientListOfSelectedItems.get(i).getDescription()).append(", ");
            }
            sb.append(transientListOfSelectedItems.get(transientListOfSelectedItems.size() - 1).getDescription());
        }
        helperLD.setValue(sb.toString());
    }

    public void notifyAboutLoadedItems() {
        entitiesLD.setValue(hListForWorkWithDB);
    }

    public void addSelectedItem(int selectedTable, int selectedItem) {
        List<SimpleEntityForDB> transientListOfItems;
        List<SimpleEntityForDB> fullListOfItems;
        switch (selectedTable) {
            case SearchCriteriaFragment.SELECT_EMPLOYEES:
                transientListOfItems = transientListOfSelectedEmployees;
                fullListOfItems = adapterListOfEmployees;
                break;
            case SearchCriteriaFragment.SELECT_FIRMS:
                transientListOfItems = transientListOfSelectedFirms;
                fullListOfItems = adapterListOfFirms;
                break;
            case SearchCriteriaFragment.SELECT_TYPES:
                transientListOfItems = transientListOfSelectedTOW;
                fullListOfItems = adapterListOfTOW;
                break;
            case SearchCriteriaFragment.SELECT_PLACES:
                transientListOfItems = transientListOfSelectedPOW;
                fullListOfItems = adapterListOfPOW;
                break;
            default:
                throw new RuntimeException("опечатка в константах: addSelectedItem(int selectedTable, int selectedItem)");
        }
        transientListOfItems.add(fullListOfItems.get(selectedItem));
        if (isClearButtonInvisible) {
            isClearButtonInvisible = false;
            visibilityOfClearButtonLD.setValue(View.VISIBLE);
        }
    }

    public void removeSelectedItem(int selectedTable, int selectedItem) {
        List<SimpleEntityForDB> fullListOfItems;
        List<SimpleEntityForDB> transientListOfItems;
        switch (selectedTable) {
            case SearchCriteriaFragment.SELECT_EMPLOYEES:
                transientListOfItems = transientListOfSelectedEmployees;
                fullListOfItems = adapterListOfEmployees;
                break;
            case SearchCriteriaFragment.SELECT_FIRMS:
                transientListOfItems = transientListOfSelectedFirms;
                fullListOfItems = adapterListOfFirms;
                break;
            case SearchCriteriaFragment.SELECT_TYPES:
                transientListOfItems = transientListOfSelectedTOW;
                fullListOfItems = adapterListOfTOW;
                break;
            case SearchCriteriaFragment.SELECT_PLACES:
                transientListOfItems = transientListOfSelectedPOW;
                fullListOfItems = adapterListOfPOW;
                break;
            default:
                throw new RuntimeException("опечатка в константах: removeSelectedItem(int selectedTable, int selectedItem)");
        }
        SimpleEntityForDB eDB = fullListOfItems.get(selectedItem);
        transientListOfItems.remove(eDB);
        if (transientListOfItems.isEmpty()) {
            visibilityOfClearButtonLD.setValue(View.INVISIBLE);
            isClearButtonInvisible = true;
        }
    }

    public void clearSelectedCheckBoxes(int selectedTable) {
        List<SimpleEntityForDB> transientListOfItems;
        switch (selectedTable) {
            case SearchCriteriaFragment.SELECT_EMPLOYEES:
                transientListOfItems = transientListOfSelectedEmployees;
                break;
            case SearchCriteriaFragment.SELECT_FIRMS:
                transientListOfItems = transientListOfSelectedFirms;
                break;
            case SearchCriteriaFragment.SELECT_TYPES:
                transientListOfItems = transientListOfSelectedTOW;
                break;
            case SearchCriteriaFragment.SELECT_PLACES:
                transientListOfItems = transientListOfSelectedPOW;
                break;
            default:
                throw new RuntimeException("опечатка в константах: clearSelectedCheckBoxes(int selectedTable)");
        }
        transientListOfItems.clear();
        selectedCheckBoxesLD.setValue(false);
        isClearButtonInvisible = true;
    }

    public void setSelectedEqualSign(int i) {
        selectedEqualSign = i;
        selectedEqualSignLD.setValue(i);
    }

    public int getSelectedEqualSign() { return selectedEqualSign; }

}
