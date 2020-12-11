package com.albertabdullin.controlwork.viewmodels;

import android.app.Application;
import android.content.res.Resources;
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
import com.albertabdullin.controlwork.fragments.AddItemOfTypeOfValuesToListDF;
import com.albertabdullin.controlwork.fragments.PickerSignsDF;
import com.albertabdullin.controlwork.fragments.SearchCriteriaFragment;
import com.albertabdullin.controlwork.models.OrderedSign;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;
import com.albertabdullin.controlwork.models.SortedEqualSignsList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    private List<String> adapterListOfOneDateForEqualitySign;
    private List<String> adapterListOfOneDateForInequalitySign;
    private List<String> adapterListOfRangeOfDateForMoreAndLessSigns;
    private List<String> adapterListOfOneNumberForEqualitySign;
    private List<String> adapterListOfOneNumberForInequalitySign;
    private List<String> adapterListOfRangeOfNumbersForMoreAndLessSigns;
    private List<Integer> listOfSelectedDatePositionsToDeleteEqualitySign;
    private List<Integer> listOfSelectedDatePositionsToDeleteInequalitySign;
    private List<Integer> listOfSelectedDatePositionsToDeleteMoreAndLessSigns;
    private List<Integer> listOfSelectedNumberPositionsToDeleteEqualitySign;
    private List<Integer> listOfSelectedNumberPositionsToDeleteInequalitySign;
    private List<Integer> listOfSelectedNumberPositionsToDeleteMoreAndLessSigns;
    private SortedEqualSignsList availableOrderedEqualSignsListForDate;
    private List<OrderedSign> selectedEqualSignsListForDate;
    private SortedEqualSignsList availableOrderedEqualSignsListForNumber;
    private List<OrderedSign> selectedEqualSignsListForNumber;
    private Map<String, String> stringViewOfDate;
    private Map<String, String> stringViewOfNumber;
    private MutableLiveData<Integer> entitiesLD;
    private MutableLiveData<String> employeesEditTextLD;
    private MutableLiveData<String> firmsEditTextLD;
    private MutableLiveData<String> typesOfWorkEditTextLD;
    private MutableLiveData<String> placesOfWorkEditTextLD;
    private MutableLiveData<Boolean> selectedCheckBoxesLD;
    private MutableLiveData<Integer> visibilityOfClearButtonLD;
    private MutableLiveData<Integer> selectedEqualSignRadioButtonLD;
    private MutableLiveData<String> stringViewOfDateMoreSignLD;
    private MutableLiveData<String> stringViewOfDateLessSignLD;
    private MutableLiveData<String> stringViewOfDateEqualitySignLD;
    private MutableLiveData<String> stringViewOfDateInequalitySignLD;
    private MutableLiveData<String> stringViewOfDateMoreAndLessSignsLD;
    private MutableLiveData<String> stringViewOfNumberMoreSignLD;
    private MutableLiveData<String> stringViewOfNumberLessSignLD;
    private MutableLiveData<String> stringViewOfNumberEqualitySignLD;
    private MutableLiveData<String> stringViewOfNumberInequalitySignLD;
    private MutableLiveData<String> stringViewOfNumberMoreAndLessSignsLD;
    private MutableLiveData<Integer> adapterListOfOneDateForEqualitySignLD;
    private MutableLiveData<Integer> adapterListOfOneDateForInequalitySignLD;
    private MutableLiveData<Integer> adapterListOfRangeOfDatesForMoreAndLessSignsLD;
    private MutableLiveData<Integer> adapterListOfOneNumberForEqualitySignLD;
    private MutableLiveData<Integer> adapterListOfOneNumberForInequalitySignLD;
    private MutableLiveData<Integer> adapterListOfRangeOfNumbersForMoreAndLessSignsLD;
    private MutableLiveData<Boolean> deleteImageViewOnDialogEqualitySignDateLD;
    private MutableLiveData<Boolean> deleteImageViewOnDialogInequalitySignDateLD;
    private MutableLiveData<Boolean> deleteImageViewOnDialogMoreAndLessSignsDateLD;
    private MutableLiveData<Boolean> deleteImageViewOnDialogEqualitySignNumberLD;
    private MutableLiveData<Boolean> deleteImageViewOnDialogInequalitySignNumberLD;
    private MutableLiveData<Boolean> deleteImageViewOnDialogMoreAndLessSignsNumberLD;
    private Map<String, List<Long>> searchCriteriaForDate;
    private Map<String, List<Float>> searchCriteriaForNumber;
    private boolean visibilityOfClearButton = false;
    private String selectedEqualSignForDate;
    private String selectedEqualSignForNumber;
    private int mSelectedTable;
    private int positionOfUpdatedItemFromOneDateList;
    private int positionOfUpdatedItemFromOneNumberList;
    private boolean activatedDF = false;

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

    public LiveData<String> getStringViewOfDateMoreSignLD() {
        if(stringViewOfDateMoreSignLD == null) stringViewOfDateMoreSignLD = new MutableLiveData<>();
        return stringViewOfDateMoreSignLD;
    }

    public LiveData<String> getStringViewOfDateLessSignLD() {
        if(stringViewOfDateLessSignLD == null) stringViewOfDateLessSignLD = new MutableLiveData<>();
        return stringViewOfDateLessSignLD;
    }

    public LiveData<String> getStringViewOfDateEqualitySignLD() {
        if(stringViewOfDateEqualitySignLD == null) stringViewOfDateEqualitySignLD = new MutableLiveData<>();
        return stringViewOfDateEqualitySignLD;
    }

    public LiveData<String> getStringViewOfDateInequalitySignLD() {
        if(stringViewOfDateInequalitySignLD == null) stringViewOfDateInequalitySignLD = new MutableLiveData<>();
        return stringViewOfDateInequalitySignLD;
    }

    public LiveData<String> getStringViewOfDateMoreAndLessSignsLD() {
        if(stringViewOfDateMoreAndLessSignsLD == null) stringViewOfDateMoreAndLessSignsLD = new MutableLiveData<>();
        return stringViewOfDateMoreAndLessSignsLD;
    }

    public LiveData<String> getStringViewOfNumberMoreSignLD() {
        if(stringViewOfNumberMoreSignLD == null) stringViewOfNumberMoreSignLD = new MutableLiveData<>();
        return stringViewOfNumberMoreSignLD;
    }

    public LiveData<String> getStringViewOfNumberLessSignLD() {
        if(stringViewOfNumberLessSignLD == null) stringViewOfNumberLessSignLD = new MutableLiveData<>();
        return stringViewOfNumberLessSignLD;
    }

    public LiveData<String> getStringViewOfNumberEqualitySignLD() {
        if(stringViewOfNumberEqualitySignLD == null) stringViewOfNumberEqualitySignLD = new MutableLiveData<>();
        return stringViewOfNumberEqualitySignLD;
    }

    public LiveData<String> getStringViewOfNumberInequalitySignLD() {
        if(stringViewOfNumberInequalitySignLD == null) stringViewOfNumberInequalitySignLD = new MutableLiveData<>();
        return stringViewOfNumberInequalitySignLD;
    }

    public LiveData<String> getStringViewOfNumberMoreAndLessSignsLD() {
        if(stringViewOfNumberMoreAndLessSignsLD == null) stringViewOfNumberMoreAndLessSignsLD = new MutableLiveData<>();
        return stringViewOfNumberMoreAndLessSignsLD;
    }

    public LiveData<Integer> getAdapterListOfOneDateForEqualitySignLD() {
        if(adapterListOfOneDateForEqualitySignLD == null) adapterListOfOneDateForEqualitySignLD = new MutableLiveData<>();
        return adapterListOfOneDateForEqualitySignLD;
    }

    public LiveData<Integer> getAdapterListOfOneDateForInequalitySignLD() {
        if(adapterListOfOneDateForInequalitySignLD == null) adapterListOfOneDateForInequalitySignLD = new MutableLiveData<>();
        return adapterListOfOneDateForInequalitySignLD;
    }

    public LiveData<Integer> getAdapterListOfRangeOfDatesForMoreAndLessSignsLD() {
        if(adapterListOfRangeOfDatesForMoreAndLessSignsLD == null) adapterListOfRangeOfDatesForMoreAndLessSignsLD = new MutableLiveData<>();
        return adapterListOfRangeOfDatesForMoreAndLessSignsLD;
    }

    public LiveData<Integer> getAdapterListOfOneNumberForEqualitySignLD() {
        if(adapterListOfOneNumberForEqualitySignLD == null) adapterListOfOneNumberForEqualitySignLD = new MutableLiveData<>();
        return adapterListOfOneNumberForEqualitySignLD;
    }

    public LiveData<Integer> getAdapterListOfOneNumberForInequalitySignLD() {
        if(adapterListOfOneNumberForInequalitySignLD == null) adapterListOfOneNumberForInequalitySignLD = new MutableLiveData<>();
        return adapterListOfOneNumberForInequalitySignLD;
    }

    public LiveData<Integer> getAdapterListOfRangeOfNumbersForMoreAndLessSignsLD() {
        if(adapterListOfRangeOfNumbersForMoreAndLessSignsLD == null) adapterListOfRangeOfNumbersForMoreAndLessSignsLD = new MutableLiveData<>();
        return adapterListOfRangeOfNumbersForMoreAndLessSignsLD;
    }


    public LiveData<Boolean> getDeleteImageViewOnDialogEqualitySignDateLD() {
        if (deleteImageViewOnDialogEqualitySignDateLD == null) deleteImageViewOnDialogEqualitySignDateLD = new MutableLiveData<>();
        return deleteImageViewOnDialogEqualitySignDateLD;
    }

    public LiveData<Boolean> getDeleteImageViewOnDialogInequalitySignDateLD() {
        if (deleteImageViewOnDialogInequalitySignDateLD == null) deleteImageViewOnDialogInequalitySignDateLD = new MutableLiveData<>();
        return deleteImageViewOnDialogInequalitySignDateLD;
    }

    public LiveData<Boolean> getDeleteImageViewOnDialogMoreAndLessSignsDateLD() {
        if (deleteImageViewOnDialogMoreAndLessSignsDateLD == null) deleteImageViewOnDialogMoreAndLessSignsDateLD = new MutableLiveData<>();
        return deleteImageViewOnDialogMoreAndLessSignsDateLD;
    }

    public LiveData<Boolean> getDeleteImageViewOnDialogEqualitySignNumberLD() {
        if (deleteImageViewOnDialogEqualitySignNumberLD == null) deleteImageViewOnDialogEqualitySignNumberLD = new MutableLiveData<>();
        return deleteImageViewOnDialogEqualitySignNumberLD;
    }

    public LiveData<Boolean> getDeleteImageViewOnDialogInequalitySignNumberLD() {
        if (deleteImageViewOnDialogInequalitySignNumberLD == null) deleteImageViewOnDialogInequalitySignNumberLD = new MutableLiveData<>();
        return deleteImageViewOnDialogInequalitySignNumberLD;
    }

    public LiveData<Boolean> getDeleteImageViewOnDialogMoreAndLessSignsNumberLD() {
        if (deleteImageViewOnDialogMoreAndLessSignsNumberLD == null) deleteImageViewOnDialogMoreAndLessSignsNumberLD = new MutableLiveData<>();
        return deleteImageViewOnDialogMoreAndLessSignsNumberLD;
    }

    public boolean isNotActivatedDF() {
        return activatedDF == false;
    }

    public void setActivatedDF(boolean b) {
        activatedDF = b;
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

    public SortedEqualSignsList getAvailableOrderedEqualSignsListForDate(int selectedTypeOfValue) {
        String[] arraysOfSigns = getApplication().getResources().getStringArray(R.array.full_equal_inequal_signs_array);
        switch (selectedTypeOfValue) {
            case SearchCriteriaFragment.DATES_VALUE:
                if (availableOrderedEqualSignsListForDate == null)
                    availableOrderedEqualSignsListForDate = new SortedEqualSignsList(Arrays.asList(arraysOfSigns));
                return availableOrderedEqualSignsListForDate;
            case SearchCriteriaFragment.NUMBERS_VALUE:
                if (availableOrderedEqualSignsListForNumber == null)
                    availableOrderedEqualSignsListForNumber = new SortedEqualSignsList(Arrays.asList(arraysOfSigns));
                return availableOrderedEqualSignsListForNumber;
            default:
                throw new RuntimeException("Опечатка в константах. " +
                        "Метод SortedEqualSignsList getAvailableOrderedEqualSignsListForDate(int selectedTypeOfValue). selectedTypeOfValue - " + selectedTypeOfValue);
        }
    }

    public SortedEqualSignsList getAvailableOrderedEqualSignsList(int selectedTypeOfValue, String selectedEqualSign) {
        Resources resources = getApplication().getResources();
        List<String> hList;
        switch (selectedTypeOfValue) {
            case SearchCriteriaFragment.DATES_VALUE:
                switch (selectedEqualSign) {
                    case "\u2a7e":
                    case "\u2a7d":
                        hList = new ArrayList<>(Arrays.asList(resources.getStringArray(R.array.less_or_more_equal_inequal_signs_array)));
                        for (OrderedSign orderedSign : selectedEqualSignsListForDate) hList.remove(orderedSign.getSign());
                        break;
                    case "=":
                    case "\u2260":
                        hList = new ArrayList<>(Arrays.asList(resources.getStringArray(R.array.equal_inequal_signs_array)));
                        for (OrderedSign orderedSign : selectedEqualSignsListForDate) hList.remove(orderedSign.getSign());
                        break;
                    case "\u2a7e" + " " + "\u2a7d":
                        hList = Arrays.asList(resources.getStringArray(R.array.no_signs_array));
                        break;
                    default:
                        throw new RuntimeException("Опечатка в константах. Метод " +
                                "getAvailableOrderedEqualSignsList(int selectedTypeOfValue, String selectedEqualSign). selectedEqualSign - " + selectedEqualSign);
                }
                break;
            case SearchCriteriaFragment.NUMBERS_VALUE:
                switch (selectedEqualSign) {
                    case "\u2a7e":
                    case "\u2a7d":
                        hList = new ArrayList<>(Arrays.asList(resources.getStringArray(R.array.less_or_more_equal_inequal_signs_array)));
                        for (OrderedSign orderedSign : selectedEqualSignsListForNumber) hList.remove(orderedSign.getSign());
                        break;
                    case "=":
                    case "\u2260":
                        hList = new ArrayList<>(Arrays.asList(resources.getStringArray(R.array.equal_inequal_signs_array)));
                        for (OrderedSign orderedSign : selectedEqualSignsListForNumber) hList.remove(orderedSign.getSign());
                        break;
                    case "\u2a7e" + " " + "\u2a7d":
                        hList = Arrays.asList(resources.getStringArray(R.array.no_signs_array));
                        break;
                    default:
                        throw new RuntimeException("Опечатка в константах. Метод " +
                                "getAvailableOrderedEqualSignsList(int selectedTypeOfValue, String selectedEqualSign). selectedEqualSign - " + selectedEqualSign);
                }
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод " +
                        "getAvailableOrderedEqualSignsList(int selectedTypeOfValue, String selectedEqualSign). selectedTypeOfValue - " + selectedTypeOfValue);
        }
        return new SortedEqualSignsList(hList);
    }

    public void setSelectedEqualSign(int selectedTypeOfValue, String selectedSign, int position) {
        switch (selectedTypeOfValue) {
            case SearchCriteriaFragment.DATES_VALUE:
                selectedEqualSignForDate = selectedSign;
                break;
            case SearchCriteriaFragment.NUMBERS_VALUE:
                selectedEqualSignForNumber = selectedSign;
                break;
        }
        selectedEqualSignRadioButtonLD.setValue(position);
    }

    public void clearSelectedEqualSign() {
        selectedEqualSignRadioButtonLD.setValue(-1);
    }

    public String getSelectedEqualSignForSelectedTypeOfValue(int selectedType) {
        if (selectedType == SearchCriteriaFragment.DATES_VALUE) return selectedEqualSignForDate;
        else return selectedEqualSignForNumber;
    }

    public String getSelectedEqualSignFromList(int selectedTypeOfValue, int position) {
        if (selectedTypeOfValue == SearchCriteriaFragment.DATES_VALUE) return selectedEqualSignsListForDate.get(position).getSign();
        else return selectedEqualSignsListForNumber.get(position).getSign();
    }

    public int getCountOfAddedCriteriaForDate() {
        if (selectedEqualSignsListForDate == null) return 0;
        return selectedEqualSignsListForDate.size();
    }

    public int getPositionOfAddedCriteriaForSelectedTypeOfValue(int selectedTypeOfValue) {
        if (selectedTypeOfValue == SearchCriteriaFragment.DATES_VALUE) return selectedEqualSignsListForDate.size() - 1;
        else return selectedEqualSignsListForNumber.size() - 1;
    }

    public int getPositionOfSign(int selectedTypeOfValue, String sign) {
        int p = 0;
        List<OrderedSign> hList;
        if (selectedTypeOfValue == SearchCriteriaFragment.DATES_VALUE) hList = selectedEqualSignsListForDate;
        else hList = selectedEqualSignsListForNumber;
        while (!sign.equals(hList.get(p).getSign())) p++;
        return p;
    }

    public void notifyAboutTapAddButton(int selectedTypeOfValue, int action, int position) {
        switch (action) {
            case PickerSignsDF.ADD_ITEM:
                addSignToSelectedSignList(selectedTypeOfValue);
                break;
            case PickerSignsDF.CHANGE_ITEM:
                changeSignFromSelectedSignList(selectedTypeOfValue, position);
                break;
            case PickerSignsDF.DELETE_ITEM:
                deleteSignFromSelectedSignList(selectedTypeOfValue, position);
        }
    }

    private void addSignToSelectedSignList(int selectedTypeOfValue) {
        switch (selectedTypeOfValue) {
            case SearchCriteriaFragment.DATES_VALUE:
                if (selectedEqualSignsListForDate == null) selectedEqualSignsListForDate = new ArrayList<>();
                selectedEqualSignsListForDate.add(availableOrderedEqualSignsListForDate.remove(selectedEqualSignForDate));
                break;
            case SearchCriteriaFragment.NUMBERS_VALUE:
                if (selectedEqualSignsListForNumber == null) selectedEqualSignsListForNumber = new ArrayList<>();
                selectedEqualSignsListForNumber.add(availableOrderedEqualSignsListForNumber.remove(selectedEqualSignForNumber));
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод void addSignToSelectedSignList(int selectedTypeOfValue). selectedTypeOfValue - " + selectedTypeOfValue);
        }
    }

    private void swapAdaptersAndLiveData(int selectedTypeOfValue, String sign) {
        List<String> helperList;
        MutableLiveData<String> helperLDForStringView;
        MutableLiveData<Integer> helperLDForAdapter;
        MutableLiveData<Boolean> helperLDForDeleteImage;
        switch (selectedTypeOfValue) {
            case SearchCriteriaFragment.DATES_VALUE:
                switch (sign) {
                    case "\u2a7e":
                        helperLDForStringView = stringViewOfDateMoreSignLD;
                        stringViewOfDateMoreSignLD = stringViewOfDateLessSignLD;
                        stringViewOfDateLessSignLD = helperLDForStringView;
                        break;
                    case "\u2a7d":
                        helperLDForStringView = stringViewOfDateLessSignLD;
                        stringViewOfDateLessSignLD = stringViewOfDateMoreSignLD;
                        stringViewOfDateMoreSignLD = helperLDForStringView;
                        break;
                    case "=":
                        helperList = adapterListOfOneDateForEqualitySign;
                        adapterListOfOneDateForEqualitySign = adapterListOfOneDateForInequalitySign;
                        adapterListOfOneDateForInequalitySign = helperList;
                        helperLDForStringView = stringViewOfDateEqualitySignLD;
                        stringViewOfDateEqualitySignLD = stringViewOfDateInequalitySignLD;
                        stringViewOfDateInequalitySignLD = helperLDForStringView;
                        helperLDForAdapter = adapterListOfOneDateForEqualitySignLD;
                        adapterListOfOneDateForEqualitySignLD = adapterListOfOneDateForInequalitySignLD;
                        adapterListOfOneDateForInequalitySignLD = helperLDForAdapter;
                        helperLDForDeleteImage = deleteImageViewOnDialogEqualitySignDateLD;
                        deleteImageViewOnDialogEqualitySignDateLD = deleteImageViewOnDialogInequalitySignDateLD;
                        deleteImageViewOnDialogInequalitySignDateLD = helperLDForDeleteImage;
                        break;
                    case "\u2260":
                        helperList = adapterListOfOneDateForInequalitySign;
                        adapterListOfOneDateForInequalitySign = adapterListOfOneDateForEqualitySign;
                        adapterListOfOneDateForEqualitySign = helperList;
                        helperLDForStringView = stringViewOfDateInequalitySignLD;
                        stringViewOfDateInequalitySignLD = stringViewOfDateEqualitySignLD;
                        stringViewOfDateEqualitySignLD = helperLDForStringView;
                        helperLDForAdapter = adapterListOfOneDateForInequalitySignLD;
                        adapterListOfOneDateForInequalitySignLD = adapterListOfOneDateForEqualitySignLD;
                        adapterListOfOneDateForEqualitySignLD = helperLDForAdapter;
                        helperLDForDeleteImage = deleteImageViewOnDialogInequalitySignDateLD;
                        deleteImageViewOnDialogInequalitySignDateLD = deleteImageViewOnDialogEqualitySignDateLD;
                        deleteImageViewOnDialogEqualitySignDateLD = helperLDForDeleteImage;
                        break;
                }
                break;
            case SearchCriteriaFragment.NUMBERS_VALUE:
                switch (sign) {
                    case "\u2a7e":
                        helperLDForStringView = stringViewOfNumberMoreSignLD;
                        stringViewOfNumberMoreSignLD = stringViewOfNumberLessSignLD;
                        stringViewOfNumberLessSignLD = helperLDForStringView;
                        break;
                    case "\u2a7d":
                        helperLDForStringView = stringViewOfNumberLessSignLD;
                        stringViewOfNumberLessSignLD = stringViewOfNumberMoreSignLD;
                        stringViewOfNumberMoreSignLD = helperLDForStringView;
                        break;
                    case "=":
                        helperList = adapterListOfOneNumberForEqualitySign;
                        adapterListOfOneNumberForEqualitySign = adapterListOfOneNumberForInequalitySign;
                        adapterListOfOneNumberForInequalitySign = helperList;
                        helperLDForStringView = stringViewOfNumberEqualitySignLD;
                        stringViewOfNumberEqualitySignLD = stringViewOfNumberInequalitySignLD;
                        stringViewOfNumberInequalitySignLD = helperLDForStringView;
                        helperLDForAdapter = adapterListOfOneNumberForEqualitySignLD;
                        adapterListOfOneNumberForEqualitySignLD = adapterListOfOneNumberForInequalitySignLD;
                        adapterListOfOneNumberForInequalitySignLD = helperLDForAdapter;
                        helperLDForDeleteImage = deleteImageViewOnDialogEqualitySignNumberLD;
                        deleteImageViewOnDialogEqualitySignNumberLD = deleteImageViewOnDialogInequalitySignNumberLD;
                        deleteImageViewOnDialogInequalitySignNumberLD = helperLDForDeleteImage;
                        break;
                    case "\u2260":
                        helperList = adapterListOfOneNumberForInequalitySign;
                        adapterListOfOneNumberForInequalitySign = adapterListOfOneNumberForEqualitySign;
                        adapterListOfOneNumberForEqualitySign = helperList;
                        helperLDForStringView = stringViewOfNumberInequalitySignLD;
                        stringViewOfNumberInequalitySignLD = stringViewOfNumberEqualitySignLD;
                        stringViewOfNumberEqualitySignLD = helperLDForStringView;
                        helperLDForAdapter = adapterListOfOneNumberForInequalitySignLD;
                        adapterListOfOneNumberForInequalitySignLD = adapterListOfOneNumberForEqualitySignLD;
                        adapterListOfOneNumberForEqualitySignLD = helperLDForAdapter;
                        helperLDForDeleteImage = deleteImageViewOnDialogInequalitySignNumberLD;
                        deleteImageViewOnDialogInequalitySignNumberLD = deleteImageViewOnDialogEqualitySignNumberLD;
                        deleteImageViewOnDialogEqualitySignNumberLD = helperLDForDeleteImage;
                        break;
                    default:
                        throw new RuntimeException("Опечатка в константах. " +
                                "Метод  void swapAdapters(int selectedTypeOfValue, String sign). sign - " + sign);
                }
                break;
            default:
                throw new RuntimeException("Опечатка в константах. " +
                        "Метод  void swapAdapters(int selectedTypeOfValue, String sign). selectedTypeOfValue - " + selectedTypeOfValue);
        }
    }

    private void changeSignFromSelectedSignList(int selectedTypeOfValue, int position) {
        String sign, value;
        int id;
        switch (selectedTypeOfValue) {
            case SearchCriteriaFragment.DATES_VALUE:
                sign = selectedEqualSignsListForDate.get(position).getSign();
                availableOrderedEqualSignsListForDate.add(selectedEqualSignsListForDate.get(position));
                availableOrderedEqualSignsListForDate.remove(selectedEqualSignForDate);
                id = SortedEqualSignsList.getID(selectedEqualSignForDate);
                selectedEqualSignsListForDate.set(position, new OrderedSign(id, selectedEqualSignForDate));
                value = stringViewOfDate.remove(sign);
                stringViewOfDate.put(selectedEqualSignForDate, value);
                swapAdaptersAndLiveData(selectedTypeOfValue, selectedEqualSignForDate);
                changeSearchCriteriaSignForDate(sign);
                break;
            case SearchCriteriaFragment.NUMBERS_VALUE:
                sign = selectedEqualSignsListForNumber.get(position).getSign();
                availableOrderedEqualSignsListForNumber.add(selectedEqualSignsListForNumber.get(position));
                availableOrderedEqualSignsListForNumber.remove(selectedEqualSignForNumber);
                id = SortedEqualSignsList.getID(selectedEqualSignForNumber);
                selectedEqualSignsListForNumber.set(position, new OrderedSign(id, selectedEqualSignForNumber));
                value = stringViewOfNumber.remove(sign);
                stringViewOfNumber.put(selectedEqualSignForNumber, value);
                swapAdaptersAndLiveData(selectedTypeOfValue, selectedEqualSignForNumber);
                changeSearchCriteriaSignForNumber(sign);
                break;
            default:
                throw new RuntimeException("Опечатка в константах. " +
                        "Метод void changeSignFromSelectedSignList(int selectedTypeOfValue, int position). selectedTypeOfValue - " + selectedTypeOfValue);
        }

    }

    private void changeSearchCriteriaSignForDate(String key) {
        if (searchCriteriaForDate != null && searchCriteriaForDate.get(key) != null) {
            List<Long> hList = new ArrayList<>(searchCriteriaForDate.get(key));
            searchCriteriaForDate.remove(key);
            searchCriteriaForDate.put(selectedEqualSignForDate, hList);
        }
    }

    private void changeSearchCriteriaSignForNumber(String key) {
        if (searchCriteriaForNumber != null && searchCriteriaForNumber.get(key) != null) {
            List<Float> hList = new ArrayList<>(searchCriteriaForNumber.get(key));
            searchCriteriaForNumber.remove(key);
            searchCriteriaForNumber.put(selectedEqualSignForNumber, hList);
        }
    }

    private void deleteSignFromSelectedSignList(int selectedTypeOfValue, int position) {
        String key;
        switch (selectedTypeOfValue) {
            case SearchCriteriaFragment.DATES_VALUE:
                key = selectedEqualSignsListForDate.get(position).getSign();
                switch (key) {
                    case "\u2a7e":
                        stringViewOfDateMoreSignLD.setValue("");
                        break;
                    case "\u2a7d":
                        stringViewOfDateLessSignLD.setValue("");
                        break;
                    case "=":
                        stringViewOfDateEqualitySignLD.setValue("");
                        if (adapterListOfOneDateForEqualitySign != null)
                            adapterListOfOneDateForEqualitySign.clear();
                        break;
                    case "\u2260":
                        stringViewOfDateInequalitySignLD.setValue("");
                        if (adapterListOfOneDateForInequalitySign != null)
                            adapterListOfOneDateForInequalitySign.clear();
                        break;
                    case ("\u2a7e" + " " + "\u2a7d"):
                        stringViewOfDateMoreAndLessSignsLD.setValue("");
                        if (adapterListOfRangeOfDateForMoreAndLessSigns != null)
                            adapterListOfRangeOfDateForMoreAndLessSigns.clear();
                        break;
                    default:
                        throw new RuntimeException("опечатка в константах. Метод deleteSignFromSelectedSignList(int selectedTypeOfValue, int position). key - " + key);
                }
                availableOrderedEqualSignsListForDate.add(selectedEqualSignsListForDate.remove(position));
                deleteSearchCriteriaForDate(key);
                stringViewOfDate.remove(key);
                break;
            case SearchCriteriaFragment.NUMBERS_VALUE:
                key = selectedEqualSignsListForNumber.get(position).getSign();
                switch (key) {
                    case "\u2a7e":
                        stringViewOfNumberMoreSignLD.setValue("");
                        break;
                    case "\u2a7d":
                        stringViewOfNumberLessSignLD.setValue("");
                        break;
                    case "=":
                        stringViewOfNumberEqualitySignLD.setValue("");
                        if (adapterListOfOneNumberForEqualitySign != null)
                            adapterListOfOneNumberForEqualitySign.clear();
                        break;
                    case "\u2260":
                        stringViewOfNumberInequalitySignLD.setValue("");
                        if (adapterListOfOneNumberForInequalitySign != null)
                            adapterListOfOneNumberForInequalitySign.clear();
                        break;
                    case ("\u2a7e" + " " + "\u2a7d"):
                        stringViewOfNumberMoreAndLessSignsLD.setValue("");
                        if (adapterListOfRangeOfNumbersForMoreAndLessSigns != null)
                            adapterListOfRangeOfNumbersForMoreAndLessSigns.clear();
                        break;
                    default:
                        throw new RuntimeException("опечатка в константах. Метод deleteSignFromSelectedSignList(int selectedTypeOfValue, int position). key - " + key);
                }
                availableOrderedEqualSignsListForNumber.add(selectedEqualSignsListForNumber.remove(position));
                deleteSearchCriteriaForNumber(key);
                stringViewOfNumber.remove(key);
                break;
            default:
                throw new RuntimeException("опечатка в константах. Метод deleteSignFromSelectedSignList(int selectedTypeOfValue, int position). selectedTypeOfValue - " + selectedTypeOfValue);
        }
    }

    private void deleteSearchCriteriaForDate(String key) {
        if (searchCriteriaForDate != null && searchCriteriaForDate.get(key) != null)
            searchCriteriaForDate.remove(key);
    }

    private void deleteSearchCriteriaForNumber(String key) {
        if (searchCriteriaForNumber != null && searchCriteriaForNumber.get(key) != null)
            searchCriteriaForNumber.remove(key);
    }

    public void addSearchCriteriaForDate(Integer selectedPositionOfSign, Long value1, Long value2) {
        if (searchCriteriaForDate == null) searchCriteriaForDate = new HashMap<>();
        value1 /= 1000;
        if (value2 != null) value2 /= 1000;
        String key = selectedEqualSignsListForDate.get(selectedPositionOfSign).getSign();
        switch (key) {
            case "\u2a7e":
            case "\u2a7d":
                if (searchCriteriaForDate.containsKey(key)) {
                    searchCriteriaForDate.get(key).clear();
                    searchCriteriaForDate.get(key).add(value1);
                } else {
                    List<Long> list = new ArrayList<>();
                    list.add(value1);
                    searchCriteriaForDate.put(key, list);
                }
                break;
            case "=":
            case "\u2260":
                if (searchCriteriaForDate.containsKey(key)) {
                    searchCriteriaForDate.get(key).add(value1);
                } else {
                    List<Long> list = new ArrayList<>();
                    list.add(value1);
                    searchCriteriaForDate.put(key, list);
                }
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                if (searchCriteriaForDate.containsKey(key)) {
                    searchCriteriaForDate.get(key).add(value1);
                    searchCriteriaForDate.get(key).add(value2);
                } else {
                    List<Long> list = new ArrayList<>();
                    list.add(value1);
                    list.add(value2);
                    searchCriteriaForDate.put(key, list);
                }
                break;
            default:
                throw new RuntimeException("key - " + key);
        }
    }

    public void addSearchCriteriaForNumber(Integer selectedPositionOfSign, Float value1, Float value2) {
        if (searchCriteriaForNumber == null) searchCriteriaForNumber = new HashMap<>();
        String key = selectedEqualSignsListForNumber.get(selectedPositionOfSign).getSign();
        switch (key) {
            case "\u2a7e":
            case "\u2a7d":
                if (searchCriteriaForNumber.containsKey(key)) {
                    searchCriteriaForNumber.get(key).clear();
                    searchCriteriaForNumber.get(key).add(value1);
                } else {
                    List<Float> list = new ArrayList<>();
                    list.add(value1);
                    searchCriteriaForNumber.put(key, list);
                }
                break;
            case "=":
            case "\u2260":
                if (searchCriteriaForNumber.containsKey(key)) {
                    searchCriteriaForNumber.get(key).add(value1);
                } else {
                    List<Float> list = new ArrayList<>();
                    list.add(value1);
                    searchCriteriaForNumber.put(key, list);
                }
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                if (searchCriteriaForNumber.containsKey(key)) {
                    searchCriteriaForNumber.get(key).add(value1);
                    searchCriteriaForNumber.get(key).add(value2);
                } else {
                    List<Float> list = new ArrayList<>();
                    list.add(value1);
                    list.add(value2);
                    searchCriteriaForNumber.put(key, list);
                }
                break;
            default:
                throw new RuntimeException("key - " + key);
        }
    }

    public String createStringViewOfDate(String key) {
        StringBuilder sb = new StringBuilder();
        int lastIndex;
        List<String> hList;
        switch (key) {
            case "=":
                hList = adapterListOfOneDateForEqualitySign;
                break;
            case "\u2260":
                hList = adapterListOfOneDateForInequalitySign;
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                hList = adapterListOfRangeOfDateForMoreAndLessSigns;
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод String createStringViewOfDate(String key). key - " + key);
        }
        lastIndex = hList.size();
        if (lastIndex != 0) {
            for (int i = 0; i < lastIndex - 1; i++)
                sb.append(hList.get(i)).append(", ");
            sb.append(hList.get(lastIndex - 1));
        }
        return sb.toString();
    }

    public String createStringViewOfNumber(String key) {
        StringBuilder sb = new StringBuilder();
        int lastIndex;
        List<String> hList;
        switch (key) {
            case "=":
                hList = adapterListOfOneNumberForEqualitySign;
                break;
            case "\u2260":
                hList = adapterListOfOneNumberForInequalitySign;
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                hList = adapterListOfRangeOfNumbersForMoreAndLessSigns;
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод String createStringViewOfNumber(String key). key - " + key);
        }
        lastIndex = hList.size();
        if (lastIndex != 0) {
            for (int i = 0; i < lastIndex - 1; i++)
                sb.append(hList.get(i)).append(", ");
            sb.append(hList.get(lastIndex - 1));
        }
        return sb.toString();
    }

    public void setSelectedSignAndStringViewOfDate(String key, String value) {
        stringViewOfDate.remove(key);
        stringViewOfDate.put(key, value);
        switch (key) {
            case "\u2a7e":
                stringViewOfDateMoreSignLD.setValue(value);
                break;
            case "\u2a7d":
                stringViewOfDateLessSignLD.setValue(value);
                break;
            case "=":
                stringViewOfDateEqualitySignLD.setValue(value);
                break;
            case "\u2260":
                stringViewOfDateInequalitySignLD.setValue(value);
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                stringViewOfDateMoreAndLessSignsLD.setValue(value);
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод void setSelectedSignAndStringViewOfDate(String key, String value). key - " + key);
        }
    }

    public void setSelectedSignAndStringViewOfNumber(String key, String value) {
        switch (key) {
            case "\u2a7e":
            case "\u2a7d":
                stringViewOfNumber.remove(key);
                stringViewOfNumber.put(key, value);
                break;
            case "=":
                stringViewOfNumberEqualitySignLD.setValue(value);
                break;
            case "\u2260":
                stringViewOfNumberInequalitySignLD.setValue(value);
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                stringViewOfNumberMoreAndLessSignsLD.setValue(value);
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод void setSelectedSignAndStringViewOfDate(String key, String value). key - " + key);
        }
    }

    public String getStringViewOfDate(String key) {
        if (stringViewOfDate == null) stringViewOfDate = new HashMap<>();
        if (stringViewOfDate.containsKey(key)) {
            return stringViewOfDate.get(key) == null ? "" : stringViewOfDate.get(key);
        }
        return "";
    }

    public String getStringViewOfNumber(String key) {
        if (stringViewOfNumber == null) stringViewOfNumber = new HashMap<>();
        if (stringViewOfNumber.containsKey(key)) {
            return stringViewOfNumber.get(key) == null ? "" : stringViewOfNumber.get(key);
        }
        return "";
    }

    public List<String> getAdapterListOfCurrentSignForDate(String sign) {
        switch (sign) {
            case "=":
                if (adapterListOfOneDateForEqualitySign == null)  adapterListOfOneDateForEqualitySign = new ArrayList<>();
                return adapterListOfOneDateForEqualitySign;
            case "\u2260":
                if (adapterListOfOneDateForInequalitySign == null)  adapterListOfOneDateForInequalitySign = new ArrayList<>();
                return adapterListOfOneDateForInequalitySign;
            case ("\u2a7e" + " " + "\u2a7d"):
                if (adapterListOfRangeOfDateForMoreAndLessSigns == null)  adapterListOfRangeOfDateForMoreAndLessSigns = new ArrayList<>();
                return adapterListOfRangeOfDateForMoreAndLessSigns;
            default:
                throw new RuntimeException("Опечатка в константах. Метод getAdapterListOfCurrentSignForDate(String sign). sign - " + sign);
        }
    }

    public List<String> getAdapterListOfCurrentSignForNumber(String sign) {
        switch (sign) {
            case "=":
                if (adapterListOfOneNumberForEqualitySign == null)  adapterListOfOneNumberForEqualitySign = new ArrayList<>();
                return adapterListOfOneNumberForEqualitySign;
            case "\u2260":
                if (adapterListOfOneNumberForInequalitySign == null)  adapterListOfOneNumberForInequalitySign = new ArrayList<>();
                return adapterListOfOneNumberForInequalitySign;
            case ("\u2a7e" + " " + "\u2a7d"):
                if (adapterListOfRangeOfNumbersForMoreAndLessSigns == null)  adapterListOfRangeOfNumbersForMoreAndLessSigns = new ArrayList<>();
                return adapterListOfRangeOfNumbersForMoreAndLessSigns;
            default:
                throw new RuntimeException("Опечатка в константах. Метод getAdapterListOfCurrentSignForDate(String sign). sign - " + sign);
        }
    }

    public Long getSelection(String key, int position) {
        if (searchCriteriaForDate == null) return null;
        if (searchCriteriaForDate.containsKey(key) && searchCriteriaForDate.get(key) != null) {
            Long selection = searchCriteriaForDate.get(key).get(position);
            if (selection != null) selection *= 1000;
            return selection;
        } else return null;
    }

    public void addItemToDateList(String sign, String date1, String date2) {
        switch (sign) {
            case "=":
                adapterListOfOneDateForEqualitySign.add(date1);
                adapterListOfOneDateForEqualitySignLD.setValue(AddItemOfTypeOfValuesToListDF.ADD_ITEM_TO_LIST);
                break;
            case "\u2260":
                adapterListOfOneDateForInequalitySign.add(date1);
                adapterListOfOneDateForInequalitySignLD.setValue(AddItemOfTypeOfValuesToListDF.ADD_ITEM_TO_LIST);
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                adapterListOfRangeOfDateForMoreAndLessSigns.add(date1 + " - " + date2);
                adapterListOfRangeOfDatesForMoreAndLessSignsLD.setValue(AddItemOfTypeOfValuesToListDF.ADD_ITEM_TO_LIST);
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод addItemToDateList(String sign, String date1, String date2). sign -" + sign);
        }
    }

    public void addItemToNumberList(String sign, String date1, String date2) {
        switch (sign) {
            case "=":
                adapterListOfOneNumberForEqualitySign.add(date1);
                adapterListOfOneNumberForEqualitySignLD.setValue(AddItemOfTypeOfValuesToListDF.ADD_ITEM_TO_LIST);
                break;
            case "\u2260":
                adapterListOfOneNumberForInequalitySign.add(date1);
                adapterListOfOneNumberForInequalitySignLD.setValue(AddItemOfTypeOfValuesToListDF.ADD_ITEM_TO_LIST);
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                adapterListOfRangeOfNumbersForMoreAndLessSigns.add(date1 + " - " + date2);
                adapterListOfRangeOfNumbersForMoreAndLessSignsLD.setValue(AddItemOfTypeOfValuesToListDF.ADD_ITEM_TO_LIST);
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод addItemToDateList(String sign, String date1, String date2). sign -" + sign);
        }
    }

    private void setUpdatedItemPositionForOneDateList(int p) {
        positionOfUpdatedItemFromOneDateList = p;
    }

    public int getPositionOfUpdatedItemFromOneDateList() {
        return positionOfUpdatedItemFromOneDateList;
    }

    private void setUpdatedItemPositionForOneNumberList(int p) {
        positionOfUpdatedItemFromOneNumberList = p;
    }

    public int getPositionOfUpdatedItemFromOneNumberList() {
        return positionOfUpdatedItemFromOneNumberList;
    }

    public void changeItemToOneDateList(String sign, int position, String date1, String date2) {
        switch (sign) {
            case "=":
                adapterListOfOneDateForEqualitySign.set(position, date1);
                adapterListOfOneDateForEqualitySignLD.setValue(AddItemOfTypeOfValuesToListDF.UPDATE_ITEM_FROM_LIST);
                break;
            case "\u2260":
                adapterListOfOneDateForInequalitySign.set(position, date1);
                adapterListOfOneDateForInequalitySignLD.setValue(AddItemOfTypeOfValuesToListDF.UPDATE_ITEM_FROM_LIST);
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                adapterListOfRangeOfDateForMoreAndLessSigns.set(position, date1 + " - " + date2);
                adapterListOfRangeOfDatesForMoreAndLessSignsLD.setValue(AddItemOfTypeOfValuesToListDF.UPDATE_ITEM_FROM_LIST);
                break;
            default: throw new RuntimeException("Опечатка в константах. Метод void changeItemToOneDateList(String sign, int position, String date). Знак - " + sign);
        }
        setUpdatedItemPositionForOneDateList(position);
    }

    public void changeItemToOneNumberList(String sign, int position, String date1, String date2) {
        switch (sign) {
            case "=":
                adapterListOfOneNumberForEqualitySign.set(position, date1);
                adapterListOfOneNumberForEqualitySignLD.setValue(AddItemOfTypeOfValuesToListDF.UPDATE_ITEM_FROM_LIST);
                break;
            case "\u2260":
                adapterListOfOneNumberForInequalitySign.set(position, date1);
                adapterListOfOneNumberForInequalitySignLD.setValue(AddItemOfTypeOfValuesToListDF.UPDATE_ITEM_FROM_LIST);
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                adapterListOfRangeOfNumbersForMoreAndLessSigns.set(position, date1 + " - " + date2);
                adapterListOfRangeOfNumbersForMoreAndLessSignsLD.setValue(AddItemOfTypeOfValuesToListDF.UPDATE_ITEM_FROM_LIST);
                break;
            default: throw new RuntimeException("Опечатка в константах. Метод void changeItemToOneDateList(String sign, int position, String date). Знак - " + sign);
        }
        setUpdatedItemPositionForOneNumberList(position);
    }

    public void changeSearchCriteriaValueForDate(String key, int position, Long value1, Long value2) {
        Long helpValue1 = value1 / 1000;
        if (value2 == null) searchCriteriaForDate.get(key).set(position, helpValue1);
        else {
            Long helpValue2 = value2 / 1000;
            searchCriteriaForDate.get(key).set(position, helpValue1);
            searchCriteriaForDate.get(key).set(position + 1, helpValue2);
        }
    }

    public void changeSearchCriteriaValueForNumber(String key, int position, Float value1, Float value2) {
        if (value2 == null) searchCriteriaForNumber.get(key).set(position, value1);
        else {
            searchCriteriaForNumber.get(key).set(position, value1);
            searchCriteriaForNumber.get(key).set(position + 1, value2);
        }
    }

    public List<Integer> getListOfSelectedPositionForDeleteSign(int selectedTypeOfValue, String sign) {
        switch (selectedTypeOfValue) {
            case SearchCriteriaFragment.DATES_VALUE:
                switch (sign) {
                    case "=": return listOfSelectedDatePositionsToDeleteEqualitySign;
                    case "\u2260": return listOfSelectedDatePositionsToDeleteInequalitySign;
                    case ("\u2a7e" + " " + "\u2a7d"): return listOfSelectedDatePositionsToDeleteMoreAndLessSigns;
                    default: throw new RuntimeException("Опечатка в константах. Метод getListOfSelectedPositionForDelete(int selectedTypeOfValue, String sign). sign - " + sign);
                }
            case SearchCriteriaFragment.NUMBERS_VALUE:
                switch (sign) {
                    case "=": return listOfSelectedNumberPositionsToDeleteEqualitySign;
                    case "\u2260": return listOfSelectedNumberPositionsToDeleteInequalitySign;
                    case ("\u2a7e" + " " + "\u2a7d"): return listOfSelectedNumberPositionsToDeleteMoreAndLessSigns;
                    default: throw new RuntimeException("Опечатка в константах. Метод getListOfSelectedPositionForDelete(int selectedTypeOfValue, String sign). sign - " + sign);
                }
                default:
                    throw new RuntimeException("Опечатка в константах. Метод getListOfSelectedPositionForDelete(int selectedTypeOfValue, String sign). selectedTypeOfValue - " + selectedTypeOfValue);
        }
    }


    public void deleteSearchCriteriaValueForDate(String key) {
        List<Integer> hListOfSelectedPositions;
        List<String> hListOfAdapter;
        MutableLiveData<Integer> hLiveDataAdapter;
        MutableLiveData<Boolean> hLiveDataDeleteImageState;
        switch (key) {
            case "=":
                hListOfSelectedPositions = listOfSelectedDatePositionsToDeleteEqualitySign;
                hListOfAdapter = adapterListOfOneDateForEqualitySign;
                hLiveDataAdapter = adapterListOfOneDateForEqualitySignLD;
                hLiveDataDeleteImageState = deleteImageViewOnDialogEqualitySignDateLD;
                break;
            case "\u2260":
                hListOfSelectedPositions = listOfSelectedDatePositionsToDeleteInequalitySign;
                hListOfAdapter = adapterListOfOneDateForInequalitySign;
                hLiveDataAdapter = adapterListOfOneDateForInequalitySignLD;
                hLiveDataDeleteImageState = deleteImageViewOnDialogInequalitySignDateLD;
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                hListOfSelectedPositions = listOfSelectedDatePositionsToDeleteMoreAndLessSigns;
                hListOfAdapter = adapterListOfRangeOfDateForMoreAndLessSigns;
                hLiveDataAdapter = adapterListOfRangeOfDatesForMoreAndLessSignsLD;
                hLiveDataDeleteImageState = deleteImageViewOnDialogMoreAndLessSignsDateLD;
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод deleteSearchCriteriaValueForDate). Знак - " + key);
        }
        hListOfSelectedPositions.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });
        if (("\u2a7e" + " " + "\u2a7d").equals(key)) {
            for(int i = 0, j = 0; i < hListOfSelectedPositions.size(); i++, j++) {
                int posInAdapter = hListOfSelectedPositions.get(i) - j;
                int posInMap = posInAdapter * 2;
                searchCriteriaForDate.get(key).remove(posInMap + 1);
                searchCriteriaForDate.get(key).remove(posInMap);
                hListOfAdapter.remove(posInAdapter);
            }
        } else {
            for(int i = 0, j = 0; i < hListOfSelectedPositions.size(); i++, j++) {
                int pos = hListOfSelectedPositions.get(i) - j;
                searchCriteriaForDate.get(key).remove(pos);
                hListOfAdapter.remove(pos);
            }
        }
        hLiveDataDeleteImageState.setValue(false);
        hLiveDataAdapter.setValue(AddItemOfTypeOfValuesToListDF.DELETE_ITEM_FROM_LIST);
        hListOfSelectedPositions.clear();
    }

    public void deleteSearchCriteriaValueForNumber(String key) {
        List<Integer> hListOfSelectedPositions;
        List<String> hListOfAdapter;
        MutableLiveData<Integer> hLiveDataAdapter;
        MutableLiveData<Boolean> hLiveDataDeleteImageState;
        switch (key) {
            case "=":
                hListOfSelectedPositions = listOfSelectedNumberPositionsToDeleteEqualitySign;
                hListOfAdapter = adapterListOfOneNumberForEqualitySign;
                hLiveDataAdapter = adapterListOfOneDateForEqualitySignLD;
                hLiveDataDeleteImageState = deleteImageViewOnDialogEqualitySignNumberLD;
                break;
            case "\u2260":
                hListOfSelectedPositions = listOfSelectedNumberPositionsToDeleteInequalitySign;
                hListOfAdapter = adapterListOfOneNumberForInequalitySign;
                hLiveDataAdapter = adapterListOfOneNumberForInequalitySignLD;
                hLiveDataDeleteImageState = deleteImageViewOnDialogInequalitySignNumberLD;
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                hListOfSelectedPositions = listOfSelectedNumberPositionsToDeleteMoreAndLessSigns;
                hListOfAdapter = adapterListOfRangeOfNumbersForMoreAndLessSigns;
                hLiveDataAdapter = adapterListOfRangeOfNumbersForMoreAndLessSignsLD;
                hLiveDataDeleteImageState = deleteImageViewOnDialogMoreAndLessSignsNumberLD;
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод deleteSearchCriteriaValueForDate). Знак - " + key);
        }
        hListOfSelectedPositions.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });
        if (("\u2a7e" + " " + "\u2a7d").equals(key)) {
            for(int i = 0, j = 0; i < hListOfSelectedPositions.size(); i++, j++) {
                int posInAdapter = hListOfSelectedPositions.get(i) - j;
                int posInMap = posInAdapter * 2;
                searchCriteriaForNumber.get(key).remove(posInMap + 1);
                searchCriteriaForNumber.get(key).remove(posInMap);
                hListOfAdapter.remove(posInAdapter);
            }
        } else {
            for(int i = 0, j = 0; i < hListOfSelectedPositions.size(); i++, j++) {
                int pos = hListOfSelectedPositions.get(i) - j;
                searchCriteriaForNumber.get(key).remove(pos);
                hListOfAdapter.remove(pos);
            }
        }
        hLiveDataDeleteImageState.setValue(false);
        hLiveDataAdapter.setValue(AddItemOfTypeOfValuesToListDF.DELETE_ITEM_FROM_LIST);
        hListOfSelectedPositions.clear();
    }

    public void addSelectedItemToListOfDeletedDate(String sign, int id) {
        List<Integer> hList;
        MutableLiveData<Boolean> hLiveData;
        switch (sign) {
            case "=":
                hList = listOfSelectedDatePositionsToDeleteEqualitySign;
                hLiveData = deleteImageViewOnDialogEqualitySignDateLD;
                break;
            case "\u2260":
                hList = listOfSelectedDatePositionsToDeleteInequalitySign;
                hLiveData = deleteImageViewOnDialogInequalitySignDateLD;
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                hList = listOfSelectedDatePositionsToDeleteMoreAndLessSigns;
                hLiveData = deleteImageViewOnDialogMoreAndLessSignsDateLD;
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод addSelectedItemToListOfDeletedDate(String sign, int positon). Знак - " + sign);
        }
        hList.add(id);
        Boolean b = hLiveData.getValue();
        if (b == null || b == false) hLiveData.setValue(true);
    }

    public void addSelectedItemToListOfDeletedNumber(String sign, int id) {
        List<Integer> hList;
        MutableLiveData<Boolean> hLiveData;
        switch (sign) {
            case "=":
                hList = listOfSelectedNumberPositionsToDeleteEqualitySign;
                hLiveData = deleteImageViewOnDialogEqualitySignNumberLD;
                break;
            case "\u2260":
                hList = listOfSelectedNumberPositionsToDeleteInequalitySign;
                hLiveData = deleteImageViewOnDialogInequalitySignNumberLD;
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                hList = listOfSelectedNumberPositionsToDeleteMoreAndLessSigns;
                hLiveData = deleteImageViewOnDialogMoreAndLessSignsNumberLD;
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод  void addSelectedItemToListOfDeletedNumber(String sign, int id). Знак - " + sign);
        }
        hList.add(id);
        Boolean b = hLiveData.getValue();
        if (b == null || b == false) hLiveData.setValue(true);
    }

    public void removeSelectedItemFromListOfDeletedDate(String sign, int id) {
        List<Integer> hList;
        MutableLiveData<Boolean> hLiveData;
        switch (sign) {
            case "=":
                hList = listOfSelectedDatePositionsToDeleteEqualitySign;
                hLiveData = deleteImageViewOnDialogEqualitySignDateLD;
                break;
            case "\u2260":
                hList = listOfSelectedDatePositionsToDeleteInequalitySign;
                hLiveData = deleteImageViewOnDialogInequalitySignDateLD;
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                hList = listOfSelectedDatePositionsToDeleteMoreAndLessSigns;
                hLiveData = deleteImageViewOnDialogMoreAndLessSignsDateLD;
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод removeSelectedItemFromListOfDeletedDate(String sign, int position). sign - " + sign);
        }
        int i = 0;
        while (hList.get(i) != id) i++;
        hList.remove(i);
        if (hList.size() == 0) hLiveData.setValue(false);
    }

    public void removeSelectedItemFromListOfDeletedNumber(String sign, int id) {
        List<Integer> hList;
        MutableLiveData<Boolean> hLiveData;
        switch (sign) {
            case "=":
                hList = listOfSelectedNumberPositionsToDeleteEqualitySign;
                hLiveData = deleteImageViewOnDialogEqualitySignNumberLD;
                break;
            case "\u2260":
                hList = listOfSelectedNumberPositionsToDeleteInequalitySign;
                hLiveData = deleteImageViewOnDialogInequalitySignNumberLD;
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                hList = listOfSelectedNumberPositionsToDeleteMoreAndLessSigns;
                hLiveData = deleteImageViewOnDialogMoreAndLessSignsNumberLD;
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод void removeSelectedItemFromListOfDeletedNumber(String sign, int id). sign - " + sign);
        }
        int i = 0;
        while (hList.get(i) != id) i++;
        hList.remove(i);
        if (hList.size() == 0) hLiveData.setValue(false);
    }

    public boolean isCheckedSelectableItemFromListOfDeletedDatePosition(String sign, int id) {
        List<Integer> hList;
        switch (sign) {
            case "=":
                if (listOfSelectedDatePositionsToDeleteEqualitySign == null)
                    listOfSelectedDatePositionsToDeleteEqualitySign = new ArrayList<>();
                hList = listOfSelectedDatePositionsToDeleteEqualitySign;
                break;
            case "\u2260":
                if (listOfSelectedDatePositionsToDeleteInequalitySign == null)
                    listOfSelectedDatePositionsToDeleteInequalitySign = new ArrayList<>();
                hList = listOfSelectedDatePositionsToDeleteInequalitySign;
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                if (listOfSelectedDatePositionsToDeleteMoreAndLessSigns == null)
                    listOfSelectedDatePositionsToDeleteMoreAndLessSigns = new ArrayList<>();
                hList = listOfSelectedDatePositionsToDeleteMoreAndLessSigns;
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод isCheckedSelectableItemFromListOfDeletedPosition(String sign, int position). Знак - " + sign);
        }
        if (hList.size() == 0) return false;
        else {
            int i = 0;
            while (i < hList.size()) {
                if (hList.get(i) == id) return true;
                i++;
            }
            return false;
        }
    }

    public boolean isCheckedSelectableItemFromListOfDeletedNumberPosition(String sign, int id) {
        List<Integer> hList;
        switch (sign) {
            case "=":
                if (listOfSelectedNumberPositionsToDeleteEqualitySign == null)
                    listOfSelectedNumberPositionsToDeleteEqualitySign = new ArrayList<>();
                hList = listOfSelectedNumberPositionsToDeleteEqualitySign;
                break;
            case "\u2260":
                if (listOfSelectedNumberPositionsToDeleteInequalitySign == null)
                    listOfSelectedNumberPositionsToDeleteInequalitySign = new ArrayList<>();
                hList = listOfSelectedNumberPositionsToDeleteInequalitySign;
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                if (listOfSelectedNumberPositionsToDeleteMoreAndLessSigns == null)
                    listOfSelectedNumberPositionsToDeleteMoreAndLessSigns = new ArrayList<>();
                hList = listOfSelectedNumberPositionsToDeleteMoreAndLessSigns;
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод boolean isCheckedSelectableItemFromListOfDeletedNumberPosition(String sign, int id). sign - " + sign);
        }
        if (hList.size() == 0) return false;
        else {
            int i = 0;
            while (i < hList.size()) {
                if (hList.get(i) == id) return true;
                i++;
            }
            return false;
        }
    }

}
