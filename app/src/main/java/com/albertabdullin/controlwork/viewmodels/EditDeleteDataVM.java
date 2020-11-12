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

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.activities.EditDeleteDataActivity;
import com.albertabdullin.controlwork.db_of_app.CWDBHelper;
import com.albertabdullin.controlwork.fragments.SearchCriteriaFragment;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;
import com.albertabdullin.controlwork.models.SortedEqualSignsList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EditDeleteDataVM extends AndroidViewModel {
    private List<SimpleEntityForDB> hListForWorkWithDB;
    private List<SimpleEntityForDB> adapterListOfEmployees;
    private List<SimpleEntityForDB> listOfSelectedEmployees;
    private List<SimpleEntityForDB> transientListOfSelectedEmployees;
    private List<SimpleEntityForDB> adapterListOfFirms;
    private List<SimpleEntityForDB> listOfSelectedFirms;
    private List<SimpleEntityForDB> transientListOfSelectedFirms;
    private List<SimpleEntityForDB> adapterListOfTOW;
    private List<SimpleEntityForDB> listOfSelectedTOW;
    private List<SimpleEntityForDB> transientListOfSelectedTOW;
    private List<SimpleEntityForDB> adapterListOfPOW;
    private List<SimpleEntityForDB> listOfSelectedPOW;
    private List<SimpleEntityForDB> transientListOfSelectedPOW;
    private SortedEqualSignsList availableOrderedEqualSignsList;
    private SortedEqualSignsList selectedEqualSignsList;
    private MutableLiveData<Integer> entitiesLD;
    private MutableLiveData<String> employeesEditTextLD;
    private MutableLiveData<String> firmsEditTextLD;
    private MutableLiveData<String> typesOfWorkEditTextLD;
    private MutableLiveData<String> placesOfWorkEditTextLD;
    private MutableLiveData<Boolean> selectedCheckBoxesLD;
    private MutableLiveData<Integer> visibilityOfClearButtonLD;
    private MutableLiveData<Integer> selectedEqualSignRadioButtonLD;
    private boolean visibilityOfClearButton = false;
    private String selectedEqualSign;
    private int countOfAddedCriteriaForDate = 0;
    private int mSelectedTable;

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
            if (hListForWorkWithDB == null) hListForWorkWithDB = new ArrayList<>();
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

    public synchronized LiveData<Integer> getEntitiesLiveData() {
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
        if(selectedEqualSignRadioButtonLD == null) selectedEqualSignRadioButtonLD = new MutableLiveData<>();
        return selectedEqualSignRadioButtonLD;
    }

    public void showFullListOfItems(int selectedTable) {
        List<SimpleEntityForDB> hList;
        String tableName;
        String columnName;
        mSelectedTable = selectedTable;
        switch (mSelectedTable) {
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
        } else entitiesLD.setValue(mSelectedTable);
    }

    public List<SimpleEntityForDB> getAdapterListOfEntities(int selectableTable) {
        switch (selectableTable) {
            case SearchCriteriaFragment.SELECT_EMPLOYEES:
                if (adapterListOfEmployees == null) adapterListOfEmployees = new ArrayList<>();
                return adapterListOfEmployees;
            case SearchCriteriaFragment.SELECT_FIRMS:
                if (adapterListOfFirms == null) adapterListOfFirms = new ArrayList<>();
                return adapterListOfFirms;
            case SearchCriteriaFragment.SELECT_TYPES:
                if (adapterListOfTOW == null) adapterListOfTOW = new ArrayList<>();
                return adapterListOfTOW;
            case SearchCriteriaFragment.SELECT_PLACES:
                if (adapterListOfPOW == null) adapterListOfPOW = new ArrayList<>();
                return adapterListOfPOW;
            default: throw new RuntimeException("ошибка в константах: getAdapterListOfEntities(int selectableTable)");
        }
    }

    public List<SimpleEntityForDB> getTransientListOfSelectedItems(int selectableTable) {
        List<SimpleEntityForDB> hTransientList;
        switch (selectableTable) {
            case SearchCriteriaFragment.SELECT_EMPLOYEES:
                if (transientListOfSelectedEmployees == null) transientListOfSelectedEmployees = new ArrayList<>();
                hTransientList = transientListOfSelectedEmployees;
                break;
            case SearchCriteriaFragment.SELECT_FIRMS:
                if (transientListOfSelectedFirms == null) transientListOfSelectedFirms = new ArrayList<>();
                hTransientList = transientListOfSelectedFirms;
                break;
            case SearchCriteriaFragment.SELECT_TYPES:
                if (transientListOfSelectedTOW == null) transientListOfSelectedTOW = new ArrayList<>();
                hTransientList = transientListOfSelectedTOW;
                break;
            case SearchCriteriaFragment.SELECT_PLACES:
                if (transientListOfSelectedPOW == null) transientListOfSelectedPOW = new ArrayList<>();
                hTransientList = transientListOfSelectedPOW;
                break;
            default:
                throw new RuntimeException("ошибка в константах: getTransientListOfSelectedItems(int selectableTable)");
        }
        visibilityOfClearButton = !hTransientList.isEmpty();
        return hTransientList;
    }

    public void commitSelectedList(int selectableTable) {
        StringBuilder sb = new StringBuilder();
        List<SimpleEntityForDB> permanentListOfSelectedItems;
        List<SimpleEntityForDB> transientListOfSelectedItems;
        MutableLiveData<String> helperLD;
        switch (selectableTable) {
            case SearchCriteriaFragment.SELECT_EMPLOYEES:
                if (listOfSelectedEmployees == null) listOfSelectedEmployees = new ArrayList<>();
                permanentListOfSelectedItems = listOfSelectedEmployees;
                transientListOfSelectedItems = transientListOfSelectedEmployees;
                helperLD = employeesEditTextLD;
                break;
            case SearchCriteriaFragment.SELECT_FIRMS:
                if (listOfSelectedFirms == null) listOfSelectedFirms = new ArrayList<>();
                permanentListOfSelectedItems = listOfSelectedFirms;
                transientListOfSelectedItems = transientListOfSelectedFirms;
                helperLD = firmsEditTextLD;
                break;
            case SearchCriteriaFragment.SELECT_TYPES:
                if (listOfSelectedTOW == null) listOfSelectedTOW = new ArrayList<>();
                permanentListOfSelectedItems = listOfSelectedTOW;
                transientListOfSelectedItems = transientListOfSelectedTOW;
                helperLD = typesOfWorkEditTextLD;
                break;
            case SearchCriteriaFragment.SELECT_PLACES:
                if (listOfSelectedPOW == null) listOfSelectedPOW = new ArrayList<>();
                permanentListOfSelectedItems = listOfSelectedPOW;
                transientListOfSelectedItems = transientListOfSelectedPOW;
                helperLD = placesOfWorkEditTextLD;
                break;
            default:
                throw new RuntimeException("опечатка в константах: commitSelectedList(int selectableTable)");
        }
        permanentListOfSelectedItems.clear();
        if (!transientListOfSelectedItems.isEmpty()) {
            permanentListOfSelectedItems.addAll(transientListOfSelectedItems);
            for (int i = 0; i < transientListOfSelectedItems.size() - 1; i++) {
                sb.append(transientListOfSelectedItems.get(i).getDescription()).append(", ");
            }
            sb.append(transientListOfSelectedItems.get(transientListOfSelectedItems.size() - 1).getDescription());
        }
        helperLD.setValue(sb.toString());

    }

    public void notifyAboutLoadedItems() {
        switch (mSelectedTable) {
            case SearchCriteriaFragment.SELECT_EMPLOYEES:
                adapterListOfEmployees.addAll(hListForWorkWithDB);
                break;
            case SearchCriteriaFragment.SELECT_FIRMS:
                adapterListOfFirms.addAll(hListForWorkWithDB);
                break;
            case SearchCriteriaFragment.SELECT_TYPES:
                adapterListOfTOW.addAll(hListForWorkWithDB);
                break;
            case SearchCriteriaFragment.SELECT_PLACES:
                adapterListOfPOW.addAll(hListForWorkWithDB);
                break;
            default: throw new RuntimeException("опечатка в константах. Метод notifyAboutLoadedItems()");
        }
        entitiesLD.setValue(mSelectedTable);
        hListForWorkWithDB.clear();
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
        if (isClearButtonInvisible()) {
            visibilityOfClearButton = true;
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
            visibilityOfClearButton = false;
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
        visibilityOfClearButton = false;
        visibilityOfClearButtonLD.setValue(View.INVISIBLE);
    }

    private boolean isClearButtonInvisible() { return visibilityOfClearButton == false; }

    public void selectAllCheckBoxes(int selectedTable) {
        switch (selectedTable) {
            case SearchCriteriaFragment.SELECT_EMPLOYEES:
                transientListOfSelectedEmployees.clear();
                transientListOfSelectedEmployees.addAll(adapterListOfEmployees);
                break;
            case SearchCriteriaFragment.SELECT_FIRMS:
                transientListOfSelectedFirms.clear();
                transientListOfSelectedFirms.addAll(adapterListOfFirms);
                break;
            case SearchCriteriaFragment.SELECT_TYPES:
                transientListOfSelectedTOW.clear();
                transientListOfSelectedTOW.addAll(adapterListOfTOW);
                break;
            case SearchCriteriaFragment.SELECT_PLACES:
                transientListOfSelectedPOW.clear();
                transientListOfSelectedPOW.addAll(adapterListOfPOW);
                break;
            default:
                throw new RuntimeException("опечатка в константах: clearSelectedCheckBoxes(int selectedTable)");
        }
        selectedCheckBoxesLD.setValue(true);
        visibilityOfClearButton = true;
        visibilityOfClearButtonLD.setValue(View.VISIBLE);
    }

    public SortedEqualSignsList getAvailableOrderedEqualSignsList() {
        if (availableOrderedEqualSignsList == null) {
            String[] arraysOfSigns = getApplication().getResources().getStringArray(R.array.full_equal_inequal_signs_array);
            availableOrderedEqualSignsList = new SortedEqualSignsList(Arrays.asList(arraysOfSigns));
        }
        return availableOrderedEqualSignsList;
    }

    public void setSelectedEqualSign(String selectedSign, int position) {
        selectedEqualSign = selectedSign;
        selectedEqualSignRadioButtonLD.setValue(position);
    }

    public String getSelectedEqualSign() {
        return selectedEqualSign;
    }

    public int getCountOfAddedCriteriaForDate() { return countOfAddedCriteriaForDate; }

    public void incrementCountOfAddedCriteriaForDate() { countOfAddedCriteriaForDate++; }

    private void addSignToSelectedSignList() {
        if (selectedEqualSignsList == null) selectedEqualSignsList = new SortedEqualSignsList();
        selectedEqualSignsList.add(availableOrderedEqualSignsList.remove(selectedEqualSign));
    }

    public void notifyAboutTapAddButton() {
        addSignToSelectedSignList();
    }

}
