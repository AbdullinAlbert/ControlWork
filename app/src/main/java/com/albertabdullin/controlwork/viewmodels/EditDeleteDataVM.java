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
import com.albertabdullin.controlwork.fragments.AddItemOfDateToListDF;
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
    private List<Integer> listOfSelectedPositionsToDeleteEqualitySign;
    private List<Integer> listOfSelectedPositionsToDeleteInequalitySign;
    private List<Integer> listOfSelectedPositionsToDeleteMoreAndLessSigns;
    private SortedEqualSignsList availableOrderedEqualSignsList;
    private List<OrderedSign> selectedEqualSignsList;
    private Map<String, String> stringViewOfDate;
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
    private MutableLiveData<Integer> adapterListOfOneDateForEqualitySignLD;
    private MutableLiveData<Integer> adapterListOfOneDateForInequalitySignLD;
    private MutableLiveData<Integer> adapterListOfRangeOfDatesForMoreAndLessSignsLD;
    private MutableLiveData<Boolean> deleteImageViewOnDialogEqualitySignLD;
    private MutableLiveData<Boolean> deleteImageViewOnDialogInequalitySignLD;
    private MutableLiveData<Boolean> deleteImageViewOnDialogMoreAndLessSignsLD;
    private Map<String, List<Long>> searchCriteriaForDate;
    private boolean visibilityOfClearButton = false;
    private String selectedEqualSign;
    private int mSelectedTable;
    private int positionOfUpdatedItemFromOneDateList;

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

    public LiveData<Boolean> getDeleteImageViewOnDialogEqualitySignLD() {
        if (deleteImageViewOnDialogEqualitySignLD == null) deleteImageViewOnDialogEqualitySignLD = new MutableLiveData<>();
        return deleteImageViewOnDialogEqualitySignLD;
    }

    public LiveData<Boolean> getDeleteImageViewOnDialogInequalitySignLD() {
        if (deleteImageViewOnDialogInequalitySignLD == null) deleteImageViewOnDialogInequalitySignLD = new MutableLiveData<>();
        return deleteImageViewOnDialogInequalitySignLD;
    }

    public LiveData<Boolean> getDeleteImageViewOnDialogMoreAndLessSignsLD() {
        if (deleteImageViewOnDialogMoreAndLessSignsLD == null) deleteImageViewOnDialogMoreAndLessSignsLD = new MutableLiveData<>();
        return deleteImageViewOnDialogMoreAndLessSignsLD;
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

    public SortedEqualSignsList getAvailableOrderedEqualSignsList(String selectedEqualSign) {
        Resources resources = getApplication().getResources();
        List<String> hList;
        switch (selectedEqualSign) {
            case "\u2a7e":
            case "\u2a7d":
                hList = new ArrayList<>(Arrays.asList(resources.getStringArray(R.array.less_or_more_equal_inequal_signs_array)));
                for (OrderedSign orderedSign : selectedEqualSignsList) hList.remove(orderedSign.getSign());
                break;
            case "=":
            case "\u2260":
                hList = new ArrayList<>(Arrays.asList(resources.getStringArray(R.array.equal_inequal_signs_array)));
                for (OrderedSign orderedSign : selectedEqualSignsList) hList.remove(orderedSign.getSign());
                break;
            case "\u2a7e" + " " + "\u2a7d":
                hList = Arrays.asList(resources.getStringArray(R.array.no_signs_array));
                break;
            default:
                throw new RuntimeException("вызван метод getAvailableOrderedEqualSignsList(String selectedEqualSign) c опечаткой параметра");
        }
        return new SortedEqualSignsList(hList);
    }

    public void setSelectedEqualSign(String selectedSign, int position) {
        selectedEqualSign = selectedSign;
        selectedEqualSignRadioButtonLD.setValue(position);
    }

    public void clearSelectedEqualSign() {
        selectedEqualSignRadioButtonLD.setValue(-1);
    }

    public String getSelectedEqualSign() {
        return selectedEqualSign;
    }

    public String getSelectedEqualSignFromList(int position) {
        return selectedEqualSignsList.get(position).getSign();
    }

    public int getCountOfAddedCriteriaForDate() {
        if (selectedEqualSignsList == null) return 0;
        return selectedEqualSignsList.size();
    }

    public int getPositionOfAddedCriteriaForDate() {
        return selectedEqualSignsList.size() - 1;
    }

    public int getPositionOfSign(String sign) {
        int p = 0;
        while (!sign.equals(selectedEqualSignsList.get(p).getSign())) p++;
        return p;
    }

    public void notifyAboutTapAddButton(int action, int position) {
        switch (action) {
            case PickerSignsDF.ADD_ITEM:
                addSignToSelectedSignList();
                break;
            case PickerSignsDF.CHANGE_ITEM:
                changeSignFromSelectedSignList(position);
                break;
            case PickerSignsDF.DELETE_ITEM:
                deleteSignFromSelectedSignList(position);
        }
    }

    private void addSignToSelectedSignList() {
        if (selectedEqualSignsList == null) selectedEqualSignsList = new ArrayList<>();
        selectedEqualSignsList.add(availableOrderedEqualSignsList.remove(selectedEqualSign));
    }

    private void changeSignFromSelectedSignList(int position) {
        String sign = selectedEqualSignsList.get(position).getSign();
        availableOrderedEqualSignsList.add(selectedEqualSignsList.get(position));
        availableOrderedEqualSignsList.remove(selectedEqualSign);
        int id = SortedEqualSignsList.getID(selectedEqualSign);
        selectedEqualSignsList.set(position, new OrderedSign(id, selectedEqualSign));
        changeSearchCriteriaSignForDate(sign);
    }

    private void changeSearchCriteriaSignForDate(String key) {
        if (searchCriteriaForDate != null && searchCriteriaForDate.get(key) != null) {
            List<Long> hList = new ArrayList<>(searchCriteriaForDate.get(key));
            searchCriteriaForDate.remove(key);
            searchCriteriaForDate.put(selectedEqualSign, hList);
        }
    }

    private void deleteSignFromSelectedSignList(int position) {
        String key = selectedEqualSignsList.get(position).getSign();
        switch (key) {
            case "\u2a7e":
                stringViewOfDateMoreSignLD.setValue("");
                break;
            case "\u2a7d":
                stringViewOfDateLessSignLD.setValue("");
                break;
            case "=":
                stringViewOfDateEqualitySignLD.setValue("");
                break;
            case "\u2260":
                stringViewOfDateInequalitySignLD.setValue("");
                break;
            default:
                throw new RuntimeException("опечатка в константах. Метод deleteSignFromSelectedSignList(int position). key - " + key);
        }
        availableOrderedEqualSignsList.add(selectedEqualSignsList.remove(position));
        deleteSearchCriteriaForDate(key);
    }

    private void deleteSearchCriteriaForDate(String key) {
        if (searchCriteriaForDate != null && searchCriteriaForDate.get(key) != null)
            searchCriteriaForDate.remove(key);
    }

    public void addSearchCriteriaForDate(Integer selectedPositionOfSign, Long value1, Long value2) {
        if (searchCriteriaForDate == null) searchCriteriaForDate = new HashMap<>();
        value1 /= 1000;
        if (value2 != null) value2 /= 1000;
        String key = selectedEqualSignsList.get(selectedPositionOfSign).getSign();
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

    public String getStringViewOfDate(String key) {
        if (stringViewOfDate == null) stringViewOfDate = new HashMap<>();
        if (stringViewOfDate.containsKey(key)) {
            return stringViewOfDate.get(key) == null ? "" : stringViewOfDate.get(key);
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
                adapterListOfOneDateForEqualitySignLD.setValue(AddItemOfDateToListDF.ADD_ITEM_TO_LIST);
                break;
            case "\u2260":
                adapterListOfOneDateForInequalitySign.add(date1);
                adapterListOfOneDateForInequalitySignLD.setValue(AddItemOfDateToListDF.ADD_ITEM_TO_LIST);
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                adapterListOfRangeOfDateForMoreAndLessSigns.add(date1 + " - " + date2);
                adapterListOfRangeOfDatesForMoreAndLessSignsLD.setValue(AddItemOfDateToListDF.ADD_ITEM_TO_LIST);
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

    public void changeItemToOneDateList(String sign, int position, String date1, String date2) {
        switch (sign) {
            case "=":
                adapterListOfOneDateForEqualitySign.set(position, date1);
                adapterListOfOneDateForEqualitySignLD.setValue(AddItemOfDateToListDF.UPDATE_ITEM_FROM_LIST);
                break;
            case "\u2260":
                adapterListOfOneDateForInequalitySign.set(position, date1);
                adapterListOfOneDateForInequalitySignLD.setValue(AddItemOfDateToListDF.UPDATE_ITEM_FROM_LIST);
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                adapterListOfRangeOfDateForMoreAndLessSigns.set(position, date1 + " - " + date2);
                adapterListOfRangeOfDatesForMoreAndLessSignsLD.setValue(AddItemOfDateToListDF.UPDATE_ITEM_FROM_LIST);
                break;
            default: throw new RuntimeException("Опечатка в константах. Метод void changeItemToOneDateList(String sign, int position, String date). Знак - " + sign);
        }
        setUpdatedItemPositionForOneDateList(position);
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

    public List<Integer> getListOfSelectedPositionForDelete(String sign) {
        switch (sign) {
            case "=": return listOfSelectedPositionsToDeleteEqualitySign;
            case "\u2260": return listOfSelectedPositionsToDeleteInequalitySign;
            case ("\u2a7e" + " " + "\u2a7d"): return listOfSelectedPositionsToDeleteMoreAndLessSigns;
            default: throw new RuntimeException("Опечатка в константах. Метод getListOfSelectedPositionForDelete(String sign). Знак - " + sign);
        }
    }

    public void deleteSearchCriteriaValueForDate(String key) {
        List<Integer> hListOfSelectedPositions;
        List<String> hListOfAdapter;
        MutableLiveData<Integer> hLiveDataAdapter;
        MutableLiveData<Boolean> hLiveDataDeleteImageState;
        switch (key) {
            case "=":
                hListOfSelectedPositions = listOfSelectedPositionsToDeleteEqualitySign;
                hListOfAdapter = adapterListOfOneDateForEqualitySign;
                hLiveDataAdapter = adapterListOfOneDateForEqualitySignLD;
                hLiveDataDeleteImageState = deleteImageViewOnDialogEqualitySignLD;
                break;
            case "\u2260":
                hListOfSelectedPositions = listOfSelectedPositionsToDeleteInequalitySign;
                hListOfAdapter = adapterListOfOneDateForInequalitySign;
                hLiveDataAdapter = adapterListOfOneDateForInequalitySignLD;
                hLiveDataDeleteImageState = deleteImageViewOnDialogEqualitySignLD;
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                hListOfSelectedPositions = listOfSelectedPositionsToDeleteMoreAndLessSigns;
                hListOfAdapter = adapterListOfRangeOfDateForMoreAndLessSigns;
                hLiveDataAdapter = adapterListOfRangeOfDatesForMoreAndLessSignsLD;
                hLiveDataDeleteImageState = deleteImageViewOnDialogMoreAndLessSignsLD;
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
        hLiveDataAdapter.setValue(AddItemOfDateToListDF.DELETE_ITEM_FROM_LIST);
        hListOfSelectedPositions.clear();
    }

    public void addSelectedItemToListOfDeletedDate(String sign, int id) {
        List<Integer> hList;
        MutableLiveData<Boolean> hLiveData;
        switch (sign) {
            case "=":
                hList = listOfSelectedPositionsToDeleteEqualitySign;
                hLiveData = deleteImageViewOnDialogEqualitySignLD;
                break;
            case "\u2260":
                hList = listOfSelectedPositionsToDeleteInequalitySign;
                hLiveData = deleteImageViewOnDialogInequalitySignLD;
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                hList = listOfSelectedPositionsToDeleteMoreAndLessSigns;
                hLiveData = deleteImageViewOnDialogMoreAndLessSignsLD;
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод addSelectedItemToListOfDeletedDate(String sign, int positon). Знак - " + sign);
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
                hList = listOfSelectedPositionsToDeleteEqualitySign;
                hLiveData = deleteImageViewOnDialogEqualitySignLD;
                break;
            case "\u2260":
                hList = listOfSelectedPositionsToDeleteInequalitySign;
                hLiveData = deleteImageViewOnDialogInequalitySignLD;
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                hList = listOfSelectedPositionsToDeleteMoreAndLessSigns;
                hLiveData = deleteImageViewOnDialogMoreAndLessSignsLD;
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод removeSelectedItemFromListOfDeletedDate(String sign, int position). Знак - " + sign);
        }
        int i = 0;
        while (hList.get(i) != id) i++;
        hList.remove(i);
        if (hList.size() == 0) hLiveData.setValue(false);
    }
    public boolean isCheckedSelectableItemFromListOfDeletedPosition(String sign, int id) {
        List<Integer> hList;
        switch (sign) {
            case "=":
                if (listOfSelectedPositionsToDeleteEqualitySign == null)
                    listOfSelectedPositionsToDeleteEqualitySign = new ArrayList<>();
                hList = listOfSelectedPositionsToDeleteEqualitySign;
                break;
            case "\u2260":
                if (listOfSelectedPositionsToDeleteInequalitySign == null)
                    listOfSelectedPositionsToDeleteInequalitySign = new ArrayList<>();
                hList = listOfSelectedPositionsToDeleteInequalitySign;
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                if (listOfSelectedPositionsToDeleteMoreAndLessSigns == null)
                    listOfSelectedPositionsToDeleteMoreAndLessSigns = new ArrayList<>();
                hList = listOfSelectedPositionsToDeleteMoreAndLessSigns;
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
}
