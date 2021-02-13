package com.albertabdullin.controlwork.viewmodels;

import android.app.Application;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.albertabdullin.controlwork.R;
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
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MakerSearchCriteriaVM extends AndroidViewModel implements DialogFragmentStateHolder {
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
    private List<SimpleEntityForDB> cacheForAdapterList;
    private List<String> adapterListOfOneDateForEqualitySign;
    private List<String> adapterListOfOneDateForInequalitySign;
    private List<String> adapterListOfRangeOfDateForMoreAndLessSigns;
    private List<String> adapterListOfOneNumberForEqualitySign;
    private List<String> adapterListOfOneNumberForInequalitySign;
    private List<String> adapterListOfRangeOfNumbersForMoreAndLessSigns;
    private List<String> adapterListOfNoteForEqualitySign;
    private List<String> adapterListOfNoteForInequalitySign;
    private List<Integer> listOfSelectedDatePositionsToDeleteEqualitySign;
    private List<Integer> listOfSelectedDatePositionsToDeleteInequalitySign;
    private List<Integer> listOfSelectedDatePositionsToDeleteMoreAndLessSigns;
    private List<Integer> listOfSelectedNumberPositionsToDeleteEqualitySign;
    private List<Integer> listOfSelectedNumberPositionsToDeleteInequalitySign;
    private List<Integer> listOfSelectedNumberPositionsToDeleteMoreAndLessSigns;
    private List<Integer> listOfSelectedNotePositionsToDeleteEqualitySign;
    private List<Integer> listOfSelectedNotePositionsToDeleteInequalitySign;
    private SortedEqualSignsList availableOrderedEqualSignsListForDate;
    private List<OrderedSign> selectedEqualSignsListForDate;
    private SortedEqualSignsList availableOrderedEqualSignsListForNumber;
    private List<OrderedSign> selectedEqualSignsListForNumber;
    private SortedEqualSignsList availableOrderedEqualSignsListForNote;
    private List<OrderedSign> selectedEqualSignsListForNote;
    private Map<String, String> stringViewOfNumber;
    private MutableLiveData<String> exceptionFromBackgroundThreadsLD;
    private MutableLiveData<Integer> entitiesLD;
    private MutableLiveData<String> employeesEditTextLD;
    private MutableLiveData<String> firmsEditTextLD;
    private MutableLiveData<String> typesOfWorkEditTextLD;
    private MutableLiveData<String> placesOfWorkEditTextLD;
    private MutableLiveData<Boolean> selectedCheckBoxesLD;
    private MutableLiveData<Integer> selectedEqualSignRadioButtonLD;
    private MutableLiveData<String> stringViewOfDateMoreSignLD;
    private MutableLiveData<String> stringViewOfDateLessSignLD;
    private MutableLiveData<String> stringViewOfDateEqualitySignLD;
    private MutableLiveData<String> stringViewOfDateInequalitySignLD;
    private MutableLiveData<String> stringViewOfDateMoreAndLessSignsLD;
    private MutableLiveData<String> stringViewOfNumberEqualitySignLD;
    private MutableLiveData<String> stringViewOfNumberInequalitySignLD;
    private MutableLiveData<String> stringViewOfNumberMoreAndLessSignsLD;
    private MutableLiveData<String> stringViewOfNoteEqualitySignLD;
    private MutableLiveData<String> stringViewOfNoteInequalitySignLD;
    private MutableLiveData<Integer> adapterListOfOneDateForEqualitySignLD;
    private MutableLiveData<Integer> adapterListOfOneDateForInequalitySignLD;
    private MutableLiveData<Integer> adapterListOfRangeOfDatesForMoreAndLessSignsLD;
    private MutableLiveData<Integer> adapterListOfOneNumberForEqualitySignLD;
    private MutableLiveData<Integer> adapterListOfOneNumberForInequalitySignLD;
    private MutableLiveData<Integer> adapterListOfRangeOfNumbersForMoreAndLessSignsLD;
    private MutableLiveData<Integer> adapterListOfNoteForEqualitySignLD;
    private MutableLiveData<Integer> adapterListOfNoteForInequalitySignLD;
    private MutableLiveData<Boolean> deleteImageViewOnDialogEqualitySignDateLD;
    private MutableLiveData<Boolean> deleteImageViewOnDialogInequalitySignDateLD;
    private MutableLiveData<Boolean> deleteImageViewOnDialogMoreAndLessSignsDateLD;
    private MutableLiveData<Boolean> deleteImageViewOnDialogEqualitySignNumberLD;
    private MutableLiveData<Boolean> deleteImageViewOnDialogInequalitySignNumberLD;
    private MutableLiveData<Boolean> deleteImageViewOnDialogMoreAndLessSignsNumberLD;
    private MutableLiveData<Boolean> deleteImageViewOnDialogEqualitySignNoteLD;
    private MutableLiveData<Boolean> deleteImageViewOnDialogInequalitySignNoteLD;
    private Map<String, List<Long>> searchCriteriaForDate;
    private Map<String, List<Float>> searchCriteriaForNumber;
    private Map<String, List<String>> searchCriteriaForNote;
    private String selectedEqualSignForDate;
    private String selectedEqualSignForNumber;
    private String selectedEqualSignForNote;
    private int mSelectedTable;
    private int positionOfUpdatedItemFromDateList;
    private int positionOfUpdatedItemFromNumberList;
    private int positionOfUpdatedItemFromNoteList;
    private boolean activatedDF = false;
    private int currentVisiblePositionOfOverFlowMenu;
    private boolean stateMenuItemSearchText = false;
    private boolean isBlankCall = true;
    private MakerSearchCriteriaVM.SearchItemsThread searchItemsThread;
    private Executor executor;

    public MakerSearchCriteriaVM(@NonNull Application application) {
        super(application);
    }

    private class SelectItemsTread extends Thread {
        private final String mCurrentNameOfTable;
        private final String mCurrentNameOfColumn;

        public SelectItemsTread(String currentNameOfTable, String currentNameOfColumn) {
            mCurrentNameOfTable = currentNameOfTable;
            mCurrentNameOfColumn = currentNameOfColumn;
        }

        @Override
        public void run() {
            CWDBHelper cwdbHelper = new CWDBHelper(getApplication());
            try (SQLiteDatabase db = cwdbHelper.getReadableDatabase(); Cursor cursor = db.query(mCurrentNameOfTable,
                    new String[]{"_id", mCurrentNameOfColumn},
                    null, null, null, null, null)) {
                if (cursor.moveToFirst()) {
                    do {
                        SimpleEntityForDB eDB = new SimpleEntityForDB(cursor.getInt(0), cursor.getString(1));
                        getAdapterListOfEntities(mSelectedTable).add(eDB);
                    } while (cursor.moveToNext());
                }
            } catch (SQLiteException e) {
                exceptionFromBackgroundThreadsLD.postValue(getApplication().getResources().getString(R.string.fail_attempt_about_load_data_from_primary_table)
                + ": " + e.getMessage());
                return;
            }
            entitiesLD.postValue(mSelectedTable);
        }
    }

    public LiveData<Integer> getEntitiesLiveData() {
        if (entitiesLD == null) entitiesLD = new MutableLiveData<>();
        return entitiesLD;
    }

    public LiveData<String> getExceptionFromBackgroundThreadsLD() {
        if (exceptionFromBackgroundThreadsLD == null) exceptionFromBackgroundThreadsLD = new MutableLiveData<>();
        return exceptionFromBackgroundThreadsLD;
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

    public LiveData<String> getStringViewOfNoteEqualitySignLD() {
        if(stringViewOfNoteEqualitySignLD == null) stringViewOfNoteEqualitySignLD = new MutableLiveData<>();
        return stringViewOfNoteEqualitySignLD;
    }

    public LiveData<String> getStringViewOfNoteInequalitySignLD() {
        if(stringViewOfNoteInequalitySignLD == null) stringViewOfNoteInequalitySignLD = new MutableLiveData<>();
        return stringViewOfNoteInequalitySignLD;
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

    public LiveData<Boolean> getDeleteImageViewOnDialogEqualitySignNoteLD() {
        if (deleteImageViewOnDialogEqualitySignNoteLD == null) deleteImageViewOnDialogEqualitySignNoteLD = new MutableLiveData<>();
        return deleteImageViewOnDialogEqualitySignNoteLD;
    }

    public LiveData<Boolean> getDeleteImageViewOnDialogInequalitySignNoteLD() {
        if (deleteImageViewOnDialogInequalitySignNoteLD == null) deleteImageViewOnDialogInequalitySignNoteLD = new MutableLiveData<>();
        return deleteImageViewOnDialogInequalitySignNoteLD;
    }

    public LiveData<Integer> getAdapterListOfNoteForEqualitySignLD() {
        if(adapterListOfNoteForEqualitySignLD == null) adapterListOfNoteForEqualitySignLD = new MutableLiveData<>();
        return adapterListOfNoteForEqualitySignLD;
    }

    public LiveData<Integer> getAdapterListOfNoteForInequalitySignLD() {
        if(adapterListOfNoteForInequalitySignLD == null) adapterListOfNoteForInequalitySignLD = new MutableLiveData<>();
        return adapterListOfNoteForInequalitySignLD;
    }

    @Override
    public boolean isNotActivatedDF() {
        return !activatedDF;
    }

    @Override
    public void setActivatedDF(boolean b) {
        activatedDF = b;
    }

    public int getCurrentVisiblePositionOfOverFlowMenu() { return currentVisiblePositionOfOverFlowMenu; }

    public void setCurrentVisiblePositionOfOverFlowMenu(int p) { currentVisiblePositionOfOverFlowMenu = p; }

    public boolean getStateOfSelectAllMenuItem(int selectedType) {
        switch (selectedType) {
            case SearchCriteriaFragment.SELECT_EMPLOYEES:
                if (transientListOfSelectedEmployees == null) return true;
                return transientListOfSelectedEmployees.isEmpty();
            case SearchCriteriaFragment.SELECT_FIRMS:
                if (transientListOfSelectedFirms == null) return true;
                return transientListOfSelectedFirms.isEmpty();
            case SearchCriteriaFragment.SELECT_TYPES:
                if (transientListOfSelectedTOW == null) return true;
                return transientListOfSelectedTOW.isEmpty();
            case SearchCriteriaFragment.SELECT_PLACES:
                if (transientListOfSelectedPOW == null) return true;
                return transientListOfSelectedPOW.isEmpty();
            default:
                throw new RuntimeException("опечатка в константах. boolean getStateOfSelectAllMenuItem(int selectedType). selectedType - " + selectedType);
        }
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
            if (executor == null) executor = Executors.newSingleThreadExecutor();
            executor.execute(selectItemsTread);
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

    public void prepareTransientListOfSelectedItems(int selectedTable) {
        switch (selectedTable) {
            case SearchCriteriaFragment.SELECT_EMPLOYEES:
                if (transientListOfSelectedEmployees == null) transientListOfSelectedEmployees = new ArrayList<>();
                if (listOfSelectedEmployees != null) transientListOfSelectedEmployees.addAll(listOfSelectedEmployees);
                break;
            case SearchCriteriaFragment.SELECT_FIRMS:
                if (transientListOfSelectedFirms == null) transientListOfSelectedFirms = new ArrayList<>();
                if (listOfSelectedFirms != null) transientListOfSelectedFirms.addAll(listOfSelectedFirms);
                break;
            case SearchCriteriaFragment.SELECT_TYPES:
                if (transientListOfSelectedTOW == null) transientListOfSelectedTOW = new ArrayList<>();
                if (listOfSelectedTOW != null) transientListOfSelectedTOW.addAll(listOfSelectedTOW);
                break;
            case SearchCriteriaFragment.SELECT_PLACES:
                if (transientListOfSelectedPOW == null) transientListOfSelectedPOW = new ArrayList<>();
                if (listOfSelectedPOW != null) transientListOfSelectedPOW.addAll(listOfSelectedPOW);
                break;
            default:
                throw new RuntimeException("ошибка в константах. Метод void prepareTransientListOfSelectedItems(int selectedTable). selectedTable - " + selectedTable);
        }
    }

    public List<SimpleEntityForDB> getTransientListOfSelectedItems(int selectedTable) {
        switch (selectedTable) {
            case SearchCriteriaFragment.SELECT_EMPLOYEES:
                return transientListOfSelectedEmployees;
            case SearchCriteriaFragment.SELECT_FIRMS:
                return transientListOfSelectedFirms;
            case SearchCriteriaFragment.SELECT_TYPES:
                return transientListOfSelectedTOW;
            case SearchCriteriaFragment.SELECT_PLACES:
                return transientListOfSelectedPOW;
            default:
                throw new RuntimeException("ошибка в константах. Метод void getTransientListOfSelectedItems(int selectableTable). selectableTable - " + selectedTable);
        }
    }

    public void clearTransientListOfSelectedItems(int selectableTable) {
        switch (selectableTable) {
            case SearchCriteriaFragment.SELECT_EMPLOYEES:
                if (transientListOfSelectedEmployees != null) transientListOfSelectedEmployees.clear();
                break;
            case SearchCriteriaFragment.SELECT_FIRMS:
                if (transientListOfSelectedFirms != null) transientListOfSelectedFirms.clear();
                break;
            case SearchCriteriaFragment.SELECT_TYPES:
                if (transientListOfSelectedTOW != null) transientListOfSelectedTOW.clear();
                break;
            case SearchCriteriaFragment.SELECT_PLACES:
                if (transientListOfSelectedPOW != null) transientListOfSelectedPOW.clear();
                break;
            default:
                throw new RuntimeException("ошибка в константах. Метод void clearTransientListOfSelectedItems(int selectableTable). selectableTable - " + selectableTable);
        }
    }

    public void commitSelectedList(int selectableTable) {
        switch (selectableTable) {
            case SearchCriteriaFragment.SELECT_EMPLOYEES:
                if (listOfSelectedEmployees == null) listOfSelectedEmployees = new ArrayList<>();
                commitSelectedList(listOfSelectedEmployees, transientListOfSelectedEmployees);
                notifyViewAboutChanges(employeesEditTextLD, listOfSelectedEmployees);
                break;
            case SearchCriteriaFragment.SELECT_FIRMS:
                if (listOfSelectedFirms == null) listOfSelectedFirms = new ArrayList<>();
                commitSelectedList(listOfSelectedFirms, transientListOfSelectedFirms);
                notifyViewAboutChanges(firmsEditTextLD, listOfSelectedFirms);
                break;
            case SearchCriteriaFragment.SELECT_TYPES:
                if (listOfSelectedTOW == null) listOfSelectedTOW = new ArrayList<>();
                commitSelectedList(listOfSelectedTOW, transientListOfSelectedTOW);
                notifyViewAboutChanges(typesOfWorkEditTextLD, listOfSelectedTOW);
                break;
            case SearchCriteriaFragment.SELECT_PLACES:
                if (listOfSelectedPOW == null) listOfSelectedPOW = new ArrayList<>();
                commitSelectedList(listOfSelectedPOW, transientListOfSelectedPOW);
                notifyViewAboutChanges(placesOfWorkEditTextLD, listOfSelectedPOW);
                break;
            default:
                throw new RuntimeException("опечатка в константах: commitSelectedList(int selectableTable)");
        }
    }

    private void commitSelectedList(List<SimpleEntityForDB> permanentList, List<SimpleEntityForDB> transientList) {
        permanentList.clear();
        if (!transientList.isEmpty()) permanentList.addAll(transientList);
        transientList.clear();
    }

    private void notifyViewAboutChanges(MutableLiveData<String> liveData, List<SimpleEntityForDB> permanentList) {
        StringBuilder sb = new StringBuilder();
        if (!permanentList.isEmpty()) {
            for (int i = 0; i < permanentList.size() - 1; i++) {
                sb.append(permanentList.get(i).getDescription()).append(", ");
            }
            sb.append(permanentList.get(permanentList.size() - 1).getDescription());
            liveData.setValue(sb.toString());
        }
    }

    public void addSelectedItem(int selectedTable, SimpleEntityForDB eDB) {
        switch (selectedTable) {
            case SearchCriteriaFragment.SELECT_EMPLOYEES:
                if (!transientListOfSelectedEmployees.contains(eDB)) transientListOfSelectedEmployees.add(eDB);
                break;
            case SearchCriteriaFragment.SELECT_FIRMS:
                if (!transientListOfSelectedFirms.contains(eDB)) transientListOfSelectedFirms.add(eDB);
                break;
            case SearchCriteriaFragment.SELECT_TYPES:
                if (!transientListOfSelectedTOW.contains(eDB)) transientListOfSelectedTOW.add(eDB);
                break;
            case SearchCriteriaFragment.SELECT_PLACES:
                if (!transientListOfSelectedPOW.contains(eDB)) transientListOfSelectedPOW.add(eDB);
                break;
            default:
                throw new RuntimeException("опечатка в константах: addSelectedItem(int selectedTable, int selectedItem)");
        }
    }

    public void removeSelectedItem(int selectedTable, SimpleEntityForDB eDB) {
        switch (selectedTable) {
            case SearchCriteriaFragment.SELECT_EMPLOYEES:
                transientListOfSelectedEmployees.remove(eDB);
                break;
            case SearchCriteriaFragment.SELECT_FIRMS:
                transientListOfSelectedFirms.remove(eDB);
                break;
            case SearchCriteriaFragment.SELECT_TYPES:
                transientListOfSelectedTOW.remove(eDB);
                break;
            case SearchCriteriaFragment.SELECT_PLACES:
                transientListOfSelectedPOW.remove(eDB);
                break;
            default:
                throw new RuntimeException("опечатка в константах: removeSelectedItem(int selectedTable, int selectedItem)");
        }
    }

    public void clearSelectedCheckBoxes(int selectedTable) {
        switch (selectedTable) {
            case SearchCriteriaFragment.SELECT_EMPLOYEES:
                transientListOfSelectedEmployees.clear();
                break;
            case SearchCriteriaFragment.SELECT_FIRMS:
                transientListOfSelectedFirms.clear();
                break;
            case SearchCriteriaFragment.SELECT_TYPES:
                transientListOfSelectedTOW.clear();
                break;
            case SearchCriteriaFragment.SELECT_PLACES:
                transientListOfSelectedPOW.clear();
                break;
            default:
                throw new RuntimeException("опечатка в константах: clearSelectedCheckBoxes(int selectedTable)");
        }
        selectedCheckBoxesLD.setValue(false);
    }

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
                exceptionFromBackgroundThreadsLD.postValue(getApplication().getResources().getString(R.string.thread_for_search_has_been_interrupted) + ": " + e.getMessage());
                interrupt();
            }
            cacheForAdapterList = new ArrayList<>(getAdapterListOfEntities(mSelectedTable));
        }

        public void setNewPattern(String newPattern) {
            if (!store.isEmpty()) store.clear();
            try {
                store.put(newPattern);
            } catch (InterruptedException e) {
                exceptionFromBackgroundThreadsLD.postValue(getApplication().getResources().getString(R.string.thread_for_search_has_been_interrupted) + ": " + e.getMessage());
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
            getAdapterListOfEntities(mSelectedTable).clear();
            while (i < cacheForAdapterList.size()) {
                m = p.matcher(cacheForAdapterList.get(i).getDescription());
                if (m.find()) getAdapterListOfEntities(mSelectedTable).add(cacheForAdapterList.get(i));
                i++;
                if (!store.isEmpty()) {
                    hPattern = store.poll();
                    regEx = "(?i)" + hPattern;
                    p = Pattern.compile(regEx);
                    if (hPattern.contains(pattern)) searchInFilteredList();
                    else {
                        i = 0;
                        getAdapterListOfEntities(mSelectedTable).clear();
                    }
                }
                if (isStopSearch.get()) break;
            }
        }

        private void searchInFilteredList() {
            List<SimpleEntityForDB> helperFoundItemsList = new ArrayList<>();
            for (int j = 0; j < getAdapterListOfEntities(mSelectedTable).size(); j++) {
                m = p.matcher(getAdapterListOfEntities(mSelectedTable).get(j).getDescription());
                if (m.find()) helperFoundItemsList.add(getAdapterListOfEntities(mSelectedTable).get(j));
            }
            getAdapterListOfEntities(mSelectedTable).clear();
            getAdapterListOfEntities(mSelectedTable).addAll(helperFoundItemsList);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    pattern = store.take();
                } catch (InterruptedException e) {
                    exceptionFromBackgroundThreadsLD.postValue(getApplication().getResources().getString(R.string.thread_for_search_has_been_interrupted) + ": " + e.getMessage());
                    return;
                }
                if (pattern.equals("")) break;
                regEx = "(?i)" + pattern;
                p = Pattern.compile(regEx);
                isStopSearch.set(false);
                if (!hPattern.equals("") && pattern.contains(hPattern)) searchInFilteredList();
                else searchInFullList();
                if (!isStopSearch.get()) {
                    entitiesLD.postValue(mSelectedTable);
                } else {
                    if (!store.isEmpty()) store.clear();
                    isStopSearch.set(false);
                    getAdapterListOfEntities(mSelectedTable).clear();
                }
                if (!hPattern.contains(pattern)) hPattern = pattern;
            }
        }
    }

    public void startSearch(String pattern) {
        searchItemsThread = new MakerSearchCriteriaVM.SearchItemsThread(pattern);
        searchItemsThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        executor.execute(searchItemsThread);
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
            if (cacheForAdapterList.size() == getAdapterListOfEntities(mSelectedTable).size()) return;
            getAdapterListOfEntities(mSelectedTable).clear();
            getAdapterListOfEntities(mSelectedTable).addAll(cacheForAdapterList);
            entitiesLD.setValue(mSelectedTable);
        }
    }

    public void closeSearchThread() {
        if (searchItemsThread != null) searchItemsThread.closeThread();
        searchItemsThread = null;
        isBlankCall = true;
        if (cacheForAdapterList != null) cacheForAdapterList.clear();
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
            case SearchCriteriaFragment.NOTES_VALUE:
                if (availableOrderedEqualSignsListForNote == null) {
                    arraysOfSigns = getApplication().getResources().getStringArray(R.array.full_array_for_notes);
                    availableOrderedEqualSignsListForNote = new SortedEqualSignsList(Arrays.asList(arraysOfSigns));
                }
                return availableOrderedEqualSignsListForNote;
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
            case SearchCriteriaFragment.NOTES_VALUE:
                switch (selectedEqualSign) {
                    case "=":
                    case "\u2260":
                        hList = new ArrayList<>(Arrays.asList(resources.getStringArray(R.array.equal_inequal_signs_array)));
                        for (OrderedSign orderedSign : selectedEqualSignsListForNote) hList.remove(orderedSign.getSign());
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
            case SearchCriteriaFragment.NOTES_VALUE:
                selectedEqualSignForNote = selectedSign;
                break;
        }
        selectedEqualSignRadioButtonLD.setValue(position);
    }

    public void clearSelectedEqualSign() {
        selectedEqualSignRadioButtonLD.setValue(-1);
    }

    public String getSelectedEqualSignForSelectedTypeOfValue(int selectedType) {
        if (selectedType == SearchCriteriaFragment.DATES_VALUE) return selectedEqualSignForDate;
        else if (selectedType == SearchCriteriaFragment.NUMBERS_VALUE) return selectedEqualSignForNumber;
        else return selectedEqualSignForNote;
    }

    public String getSelectedEqualSignFromList(int selectedTypeOfValue, int position) {
        switch (selectedTypeOfValue) {
            case SearchCriteriaFragment.DATES_VALUE:
                return selectedEqualSignsListForDate.get(position).getSign();
            case SearchCriteriaFragment.NUMBERS_VALUE:
                return selectedEqualSignsListForNumber.get(position).getSign();
            case SearchCriteriaFragment.NOTES_VALUE:
                return selectedEqualSignsListForNote.get(position).getSign();
            default:
                throw new RuntimeException("Опечатка в константах. Метод String getSelectedEqualSignFromList(int selectedTypeOfValue, int position). selectedTypeOfValue - " + selectedTypeOfValue);
        }
    }

    public int getCountOfAddedCriteria(int selectedTypeOfValue) {
        switch (selectedTypeOfValue) {
            case SearchCriteriaFragment.DATES_VALUE:
                return getCountOfAddedCriteriaForCertainTypeOfValue(selectedEqualSignsListForDate);
            case SearchCriteriaFragment.NUMBERS_VALUE:
                return getCountOfAddedCriteriaForCertainTypeOfValue(selectedEqualSignsListForNumber);
            case SearchCriteriaFragment.NOTES_VALUE:
                return getCountOfAddedCriteriaForCertainTypeOfValue(selectedEqualSignsListForNote);
            default:
                throw new RuntimeException("Опечатка в константах. Метод int getCountOfAddedCriteria(int selectedTypeOfValue)." +
                        " selectedTypeOfValue - " + selectedTypeOfValue);
        }
    }

    private int getCountOfAddedCriteriaForCertainTypeOfValue(List<OrderedSign> store) {
        if (store == null) return 0;
        return store.size();
    }

    public int getPositionOfAddedCriteriaForSelectedTypeOfValue(int selectedTypeOfValue) {
        if (selectedTypeOfValue == SearchCriteriaFragment.DATES_VALUE) return selectedEqualSignsListForDate.size() - 1;
        else if (selectedTypeOfValue == SearchCriteriaFragment.NUMBERS_VALUE) return selectedEqualSignsListForNumber.size() - 1;
        else return selectedEqualSignsListForNote.size() - 1;
    }

    public int getPositionOfSign(int selectedTypeOfValue, String sign) {
        switch (selectedTypeOfValue) {
            case SearchCriteriaFragment.DATES_VALUE:
                return helperGetPositionOfSign(selectedEqualSignsListForDate, sign);
            case SearchCriteriaFragment.NUMBERS_VALUE:
                return helperGetPositionOfSign(selectedEqualSignsListForNumber, sign);
            case SearchCriteriaFragment.NOTES_VALUE:
                return helperGetPositionOfSign(selectedEqualSignsListForNote, sign);
            default:
                throw new RuntimeException("Опечатка в константах. Метод  int getPositionOfSign(int selectedTypeOfValue, String sign). selectedTypeOfValue - " + selectedTypeOfValue);
        }
    }

    private int helperGetPositionOfSign(List<OrderedSign> hList, String sign) {
        int p = 0;
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
            case SearchCriteriaFragment.NOTES_VALUE:
                if (selectedEqualSignsListForNote == null) selectedEqualSignsListForNote = new ArrayList<>();
                selectedEqualSignsListForNote.add(availableOrderedEqualSignsListForNote.remove(selectedEqualSignForNote));
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
                    case "\u2a7d":
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
            case SearchCriteriaFragment.NOTES_VALUE:
                switch (sign) {
                    case "=":
                        helperList = adapterListOfNoteForEqualitySign;
                        adapterListOfNoteForEqualitySign = adapterListOfNoteForInequalitySign;
                        adapterListOfNoteForInequalitySign = helperList;
                        helperLDForStringView = stringViewOfNoteEqualitySignLD;
                        stringViewOfNoteEqualitySignLD = stringViewOfNoteInequalitySignLD;
                        stringViewOfNoteInequalitySignLD = helperLDForStringView;
                        helperLDForAdapter = adapterListOfNoteForEqualitySignLD;
                        adapterListOfNoteForEqualitySignLD = adapterListOfNoteForInequalitySignLD;
                        adapterListOfNoteForInequalitySignLD = helperLDForAdapter;
                        helperLDForDeleteImage = deleteImageViewOnDialogEqualitySignNoteLD;
                        deleteImageViewOnDialogEqualitySignNoteLD = deleteImageViewOnDialogInequalitySignNoteLD;
                        deleteImageViewOnDialogInequalitySignNoteLD = helperLDForDeleteImage;
                        break;
                    case "\u2260":
                        helperList = adapterListOfNoteForInequalitySign;
                        adapterListOfNoteForInequalitySign = adapterListOfNoteForEqualitySign;
                        adapterListOfNoteForEqualitySign = helperList;
                        helperLDForStringView = stringViewOfNoteInequalitySignLD;
                        stringViewOfNoteInequalitySignLD = stringViewOfNoteEqualitySignLD;
                        stringViewOfNoteEqualitySignLD = helperLDForStringView;
                        helperLDForAdapter = adapterListOfNoteForInequalitySignLD;
                        adapterListOfNoteForInequalitySignLD = adapterListOfNoteForEqualitySignLD;
                        adapterListOfNoteForEqualitySignLD = helperLDForAdapter;
                        helperLDForDeleteImage = deleteImageViewOnDialogInequalitySignNoteLD;
                        deleteImageViewOnDialogInequalitySignNoteLD = deleteImageViewOnDialogEqualitySignNoteLD;
                        deleteImageViewOnDialogEqualitySignNoteLD = helperLDForDeleteImage;
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
                swapAdaptersAndLiveData(selectedTypeOfValue, selectedEqualSignForDate);
                changeSearchCriteriaSign(searchCriteriaForDate, selectedEqualSignForDate, sign);
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
                changeSearchCriteriaSign(searchCriteriaForNumber, selectedEqualSignForNumber, sign);
                break;
            case SearchCriteriaFragment.NOTES_VALUE:
                sign = selectedEqualSignsListForNote.get(position).getSign();
                availableOrderedEqualSignsListForNote.add(selectedEqualSignsListForNote.get(position));
                availableOrderedEqualSignsListForNote.remove(selectedEqualSignForNote);
                id = SortedEqualSignsList.getID(selectedEqualSignForNote);
                selectedEqualSignsListForNote.set(position, new OrderedSign(id, selectedEqualSignForNote));
                swapAdaptersAndLiveData(selectedTypeOfValue, selectedEqualSignForNote);
                changeSearchCriteriaSign(searchCriteriaForNote, selectedEqualSignForNote, sign);
                break;
            default:
                throw new RuntimeException("Опечатка в константах. " +
                        "Метод void changeSignFromSelectedSignList(int selectedTypeOfValue, int position). selectedTypeOfValue - " + selectedTypeOfValue);
        }

    }

    private <T> void changeSearchCriteriaSign(Map<String, List<T>> store, String selectedEqualSign, String key) {
        if (store != null && store.get(key) != null) {
            List<T> hList = new ArrayList<>(store.get(key));
            store.remove(key);
            store.put(selectedEqualSign, hList);
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
                deleteSearchCriteria(SearchCriteriaFragment.DATES_VALUE, key);
                break;
            case SearchCriteriaFragment.NUMBERS_VALUE:
                key = selectedEqualSignsListForNumber.get(position).getSign();
                switch (key) {
                    case "\u2a7e":
                    case "\u2a7d":
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
                deleteSearchCriteria(SearchCriteriaFragment.NUMBERS_VALUE, key);
                stringViewOfNumber.remove(key);
                break;
            case SearchCriteriaFragment.NOTES_VALUE:
                key = selectedEqualSignsListForNote.get(position).getSign();
                switch (key) {
                    case "=":
                        stringViewOfNoteEqualitySignLD.setValue("");
                        if (adapterListOfNoteForEqualitySign != null)
                            adapterListOfNoteForEqualitySign.clear();
                        break;
                    case "\u2260":
                        stringViewOfNoteInequalitySignLD.setValue("");
                        if (adapterListOfNoteForInequalitySign != null)
                            adapterListOfNoteForInequalitySign.clear();
                        break;
                    default:
                        throw new RuntimeException("опечатка в константах. Метод deleteSignFromSelectedSignList(int selectedTypeOfValue, int position). key - " + key);
                }
                availableOrderedEqualSignsListForNote.add(selectedEqualSignsListForNote.remove(position));
                deleteSearchCriteria(SearchCriteriaFragment.NOTES_VALUE, key);
                break;
            default:
                throw new RuntimeException("опечатка в константах. Метод deleteSignFromSelectedSignList(int selectedTypeOfValue, int position). selectedTypeOfValue - " + selectedTypeOfValue);
        }
    }

    public void deleteStringViewOfNumber(String key) {
        stringViewOfNumber.remove(key);
    }

    public void deleteSearchCriteria(int selectedTypeOfValue, String key) {
        switch (selectedTypeOfValue) {
            case SearchCriteriaFragment.DATES_VALUE:
                deleteSearchCriteriaOfCertainType(searchCriteriaForDate, key);
                break;
            case SearchCriteriaFragment.NUMBERS_VALUE:
                deleteSearchCriteriaOfCertainType(searchCriteriaForNumber, key);
                break;
            case SearchCriteriaFragment.NOTES_VALUE:
                deleteSearchCriteriaOfCertainType(searchCriteriaForNote, key);
                break;
        }
    }

    private <T> void deleteSearchCriteriaOfCertainType(Map<String, List<T>> store, String key) {
        if (store != null && store.get(key) != null)
            store.remove(key);
    }

    public <T> void addSearchCriteria(int selectedTypeOfValue, Integer selectedPositionOfSign, T value1, T value2) {
        String key;
        switch(selectedTypeOfValue) {
            case SearchCriteriaFragment.DATES_VALUE:
                key = selectedEqualSignsListForDate.get(selectedPositionOfSign).getSign();
                Long longValue1 = ((Long) value1) / 1000;
                Long longValue2 = null;
                if (value2 != null) longValue2 = ((Long) value2) / 1000;
                if (searchCriteriaForDate == null) searchCriteriaForDate = new HashMap<>();
                addSearchCriteriaToCertainTypeOfValue(searchCriteriaForDate, key, longValue1, longValue2);
                break;
            case SearchCriteriaFragment.NUMBERS_VALUE:
                key = selectedEqualSignsListForNumber.get(selectedPositionOfSign).getSign();
                Float floatValue1 = (Float) value1;
                Float floatValue2 = null;
                if (value2 != null) floatValue2 = (Float) value2;
                if (searchCriteriaForNumber == null) searchCriteriaForNumber = new HashMap<>();
                addSearchCriteriaToCertainTypeOfValue(searchCriteriaForNumber, key, floatValue1, floatValue2);
                break;
            case SearchCriteriaFragment.NOTES_VALUE:
                key = selectedEqualSignsListForNote.get(selectedPositionOfSign).getSign();
                String stringValue1 = (String) value1;
                if (searchCriteriaForNote == null) searchCriteriaForNote = new HashMap<>();
                addSearchCriteriaToCertainTypeOfValue(searchCriteriaForNote, key, stringValue1, null);
                break;
        }
    }

    private <T> void addSearchCriteriaToCertainTypeOfValue(Map<String, List<T>> store, String key, T value1, T value2) {
        switch (key) {
            case "\u2a7e":
            case "\u2a7d":
                if (store.containsKey(key)) {
                    store.get(key).clear();
                    store.get(key).add(value1);
                } else {
                    List<T> list = new ArrayList<>();
                    list.add(value1);
                    store.put(key, list);
                }
                break;
            case "=":
            case "\u2260":
                if (store.containsKey(key)) {
                    store.get(key).add(value1);
                } else {
                    List<T> list = new ArrayList<>();
                    list.add(value1);
                    store.put(key, list);
                }
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                if (store.containsKey(key)) {
                    store.get(key).add(value1);
                    store.get(key).add(value2);
                } else {
                    List<T> list = new ArrayList<>();
                    list.add(value1);
                    list.add(value2);
                    store.put(key, list);
                }
                break;
            default:
                throw new RuntimeException("key - " + key);
        }
    }

    public String createStringViewOfDate(String key) {
        switch (key) {
            case "=":
                return getStringView(adapterListOfOneDateForEqualitySign);
            case "\u2260":
                return getStringView(adapterListOfOneDateForInequalitySign);
            case ("\u2a7e" + " " + "\u2a7d"):
                return getStringView(adapterListOfRangeOfDateForMoreAndLessSigns);
            default:
                throw new RuntimeException("Опечатка в константах. Метод String createStringViewOfDate(String key). key - " + key);
        }
    }

    public String createStringViewOfNumber(String key) {
        switch (key) {
            case "=":
                return getStringView(adapterListOfOneNumberForEqualitySign);
            case "\u2260":
                return getStringView(adapterListOfOneNumberForInequalitySign);
            case ("\u2a7e" + " " + "\u2a7d"):
                return getStringView(adapterListOfRangeOfNumbersForMoreAndLessSigns);
            default:
                throw new RuntimeException("Опечатка в константах. Метод String createStringViewOfNumber(String key). key - " + key);
        }
    }

    public String createStringViewOfNote(String key) {
        switch (key) {
            case "=":
                return getStringView(adapterListOfNoteForEqualitySign);
            case "\u2260":
                return getStringView(adapterListOfNoteForInequalitySign);
            default:
                throw new RuntimeException("Опечатка в константах. Метод String createStringViewOfNote(String key). key - " + key);
        }
    }

    private String getStringView(List<String> adapterList) {
        StringBuilder sb = new StringBuilder();
        int lastIndex = adapterList.size();
        if (lastIndex != 0) {
            for (int i = 0; i < lastIndex - 1; i++)
                sb.append(adapterList.get(i)).append(", ");
            sb.append(adapterList.get(lastIndex - 1));
        }
        return sb.toString();
    }

    public void setSelectedSignAndStringViewOfDate(String key, String value) {
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

    public void setSelectedSignAndStringViewOfNote(String key, String value) {
        switch (key) {
            case "=":
                stringViewOfNoteEqualitySignLD.setValue(value);
                break;
            case "\u2260":
                stringViewOfNoteInequalitySignLD.setValue(value);
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод void setSelectedSignAndStringViewOfDate(String key, String value). key - " + key);
        }
    }

    public String getStringViewOfSearchCriteria(int selectedTypeOfValue, String key) {
        if (selectedTypeOfValue != SearchCriteriaFragment.NUMBERS_VALUE) return "";
        else {
            if (stringViewOfNumber == null) stringViewOfNumber = new HashMap<>();
            return stringViewOfNumber.get(key) == null ? "" : stringViewOfNumber.get(key);
        }
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

    public List<String> getAdapterListOfCurrentSignForNote(String sign) {
        switch (sign) {
            case "=":
                if (adapterListOfNoteForEqualitySign == null)  adapterListOfNoteForEqualitySign = new ArrayList<>();
                return adapterListOfNoteForEqualitySign;
            case "\u2260":
                if (adapterListOfNoteForInequalitySign == null)  adapterListOfNoteForInequalitySign = new ArrayList<>();
                return adapterListOfNoteForInequalitySign;
            default:
                throw new RuntimeException("Опечатка в константах. Метод getAdapterListOfCurrentSignForDate(String sign). sign - " + sign);
        }
    }

    public Long getSelection(String key, int position) {
        if (searchCriteriaForDate == null) return null;
        if (searchCriteriaForDate.get(key) != null) {
            Long selection = searchCriteriaForDate.get(key).get(position);
            if (selection != null) selection *= 1000;
            return selection;
        } else return null;
    }

    public String getValueOfNumber(String key, int position) {
        if (searchCriteriaForNumber.containsKey(key) && searchCriteriaForNumber.get(key) != null) {
            String stringViewOfNumber = searchCriteriaForNumber.get(key).get(position).toString();
            String regExp = ".0$";
            Pattern pattern = Pattern.compile(regExp);
            Matcher matcher = pattern.matcher(stringViewOfNumber);
            if (matcher.find()) return matcher.replaceFirst("");
            else return stringViewOfNumber;
        } else return null;
    }

    public String getValueOfNote(String key, int position) {
        if (searchCriteriaForNote.containsKey(key) && searchCriteriaForNote.get(key) != null) {
            return searchCriteriaForNote.get(key).get(position);
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

    public void addItemToNumberList(String sign, String number1, String number2) {
        switch (sign) {
            case "=":
                adapterListOfOneNumberForEqualitySign.add(number1);
                adapterListOfOneNumberForEqualitySignLD.setValue(AddItemOfTypeOfValuesToListDF.ADD_ITEM_TO_LIST);
                break;
            case "\u2260":
                adapterListOfOneNumberForInequalitySign.add(number1);
                adapterListOfOneNumberForInequalitySignLD.setValue(AddItemOfTypeOfValuesToListDF.ADD_ITEM_TO_LIST);
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                adapterListOfRangeOfNumbersForMoreAndLessSigns.add(number1 + " - " + number2);
                adapterListOfRangeOfNumbersForMoreAndLessSignsLD.setValue(AddItemOfTypeOfValuesToListDF.ADD_ITEM_TO_LIST);
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод addItemToDateList(String sign, String number1, String number2). sign -" + sign);
        }
    }

    public void addItemToNoteList(String sign, String note) {
        switch (sign) {
            case "=":
                adapterListOfNoteForEqualitySign.add(note);
                adapterListOfNoteForEqualitySignLD.setValue(AddItemOfTypeOfValuesToListDF.ADD_ITEM_TO_LIST);
                break;
            case "\u2260":
                adapterListOfNoteForInequalitySign.add(note);
                adapterListOfNoteForInequalitySignLD.setValue(AddItemOfTypeOfValuesToListDF.ADD_ITEM_TO_LIST);
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод addItemToDateList(String sign, String date1, String date2). sign -" + sign);
        }
    }

    public int getPositionOfUpdatedItem(int selectedTypeOfValue) {
        switch (selectedTypeOfValue) {
            case SearchCriteriaFragment.DATES_VALUE:
                return positionOfUpdatedItemFromDateList;
            case SearchCriteriaFragment.NUMBERS_VALUE:
                return positionOfUpdatedItemFromNumberList;
            case SearchCriteriaFragment.NOTES_VALUE:
                return positionOfUpdatedItemFromNoteList;
            default:
                throw new RuntimeException("Опечатка констант. Метод void helperChangeItemToList. selectedTypeOfValue -" + selectedTypeOfValue);
        }
    }

    private void helperChangeItemToList(List<String> adapterList, MutableLiveData<Integer> mutableLiveData,
                                        int selectedTypeOfValue, int position, String value) {
        adapterList.set(position, value);
        switch (selectedTypeOfValue) {
            case SearchCriteriaFragment.DATES_VALUE:
                positionOfUpdatedItemFromDateList = position;
                break;
            case SearchCriteriaFragment.NUMBERS_VALUE:
                positionOfUpdatedItemFromNumberList = position;
                break;
            case SearchCriteriaFragment.NOTES_VALUE:
                positionOfUpdatedItemFromNoteList = position;
                break;
            default:
                throw new RuntimeException("Опечатка констант. Метод void helperChangeItemToList. selectedTypeOfValue -" + selectedTypeOfValue);
        }
        mutableLiveData.setValue(AddItemOfTypeOfValuesToListDF.UPDATE_ITEM_FROM_LIST);
    }

    public void changeItemToOneDateList(String sign, int position, String date1, String date2) {
        switch (sign) {
            case "=":
                helperChangeItemToList(adapterListOfOneDateForEqualitySign, adapterListOfOneDateForEqualitySignLD,
                        SearchCriteriaFragment.DATES_VALUE, position, date1);
                break;
            case "\u2260":
                helperChangeItemToList(adapterListOfOneDateForInequalitySign, adapterListOfOneDateForInequalitySignLD,
                        SearchCriteriaFragment.DATES_VALUE, position, date1);
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                helperChangeItemToList(adapterListOfRangeOfDateForMoreAndLessSigns, adapterListOfRangeOfDatesForMoreAndLessSignsLD,
                        SearchCriteriaFragment.DATES_VALUE, position, date1 + " - " + date2);
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод void changeItemToOneDateList(String sign, int position, String date). Знак - " + sign);
        }
    }

    public void changeItemToOneNumberList(String sign, int position, String number1, String number2) {
        switch (sign) {
            case "=":
                helperChangeItemToList(adapterListOfOneNumberForEqualitySign, adapterListOfOneNumberForEqualitySignLD,
                        SearchCriteriaFragment.NUMBERS_VALUE, position, number1);
                break;
            case "\u2260":
                helperChangeItemToList(adapterListOfOneNumberForInequalitySign, adapterListOfOneNumberForInequalitySignLD,
                        SearchCriteriaFragment.NUMBERS_VALUE, position, number1);
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                helperChangeItemToList(adapterListOfRangeOfNumbersForMoreAndLessSigns, adapterListOfRangeOfNumbersForMoreAndLessSignsLD,
                        SearchCriteriaFragment.NUMBERS_VALUE, position, number1 + " - " + number2);
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод void changeItemToOneDateList(String sign, int position, String date). Знак - " + sign);
        }
    }

    public void changeItemToNoteList(String sign, int position, String note) {
        switch (sign) {
            case "=":
                helperChangeItemToList(adapterListOfNoteForEqualitySign, adapterListOfNoteForEqualitySignLD,
                        SearchCriteriaFragment.NOTES_VALUE, position, note);
                break;
            case "\u2260":
                helperChangeItemToList(adapterListOfNoteForInequalitySign, adapterListOfNoteForInequalitySignLD,
                        SearchCriteriaFragment.NOTES_VALUE, position, note);
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод void changeItemToOneDateList(String sign, int position, String date). Знак - " + sign);
        }
    }

    public <T> void changeSearchCriteria(int selectedTypeOfValue, String key, int position, T value1, T value2) {
        switch (selectedTypeOfValue) {
            case SearchCriteriaFragment.DATES_VALUE:
                Long longValue1 = ((Long) value1) / 1000;
                Long longValue2 = null;
                if (value2 != null) longValue2 = ((Long) value2) / 1000;
                changeSearchCriteriaValueForCertainTypeOfValue(searchCriteriaForDate, key, position, longValue1, longValue2);
                break;
            case SearchCriteriaFragment.NUMBERS_VALUE:
                Float floatValue1 = (Float) value1;
                Float floatValue2 = null;
                if (value2 != null) floatValue2 = (Float) value2;
                changeSearchCriteriaValueForCertainTypeOfValue(searchCriteriaForNumber, key, position, floatValue1, floatValue2);
                break;
            case SearchCriteriaFragment.NOTES_VALUE:
                String stringValue = (String) value1;
                changeSearchCriteriaValueForCertainTypeOfValue(searchCriteriaForNote, key, position, stringValue, null);
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод <T> void changeSearchCriteria. selectedTypeOfValue -" + selectedTypeOfValue);
        }
    }

    private <T> void changeSearchCriteriaValueForCertainTypeOfValue(Map<String, List<T>> store, String key, int position, T value1, T value2) {
        if (value2 == null) store.get(key).set(position, value1);
        else {
            store.get(key).set(position, value1);
            store.get(key).set(position + 1, value2);
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
            case SearchCriteriaFragment.NOTES_VALUE:
                switch (sign) {
                    case "=": return listOfSelectedNotePositionsToDeleteEqualitySign;
                    case "\u2260": return listOfSelectedNotePositionsToDeleteInequalitySign;
                    default: throw new RuntimeException("Опечатка в константах. Метод getListOfSelectedPositionForDelete(int selectedTypeOfValue, String sign). sign - " + sign);
                }
            default:
                    throw new RuntimeException("Опечатка в константах. Метод getListOfSelectedPositionForDelete(int selectedTypeOfValue, String sign). " +
                            "selectedTypeOfValue - " + selectedTypeOfValue);
        }
    }

    private <T> void helperDSCValue(List<Integer> hListOfSelectedPositions, List<String> hListOfAdapter,
                                    String key, Map<String, List<T>> store) {
        hListOfSelectedPositions.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });
        if (("\u2a7e" + " " + "\u2a7d").equals(key)) {
            for (int i = hListOfSelectedPositions.size() - 1; i > -1; i--) {
                int posInAdapter = hListOfSelectedPositions.get(i);
                int posInMap = posInAdapter * 2;
                store.get(key).remove(posInMap + 1);
                store.get(key).remove(posInMap);
                hListOfAdapter.remove(posInAdapter);
            }
        } else {
            for (int i = hListOfSelectedPositions.size() - 1; i > -1; i--) {
                int pos = hListOfSelectedPositions.get(i);
                store.get(key).remove(pos);
                hListOfAdapter.remove(pos);
            }
        }
    }

    private void notifyViewsAboutDelete(MutableLiveData<Integer> hLiveDataAdapter, MutableLiveData<Boolean> hLiveDataDeleteImageState) {
        hLiveDataDeleteImageState.setValue(false);
        hLiveDataAdapter.setValue(AddItemOfTypeOfValuesToListDF.DELETE_ITEM_FROM_LIST);
    }

    public void deleteSearchCriteriaValueForDate(String key) {
        switch (key) {
            case "=":
                helperDSCValue(listOfSelectedDatePositionsToDeleteEqualitySign, adapterListOfOneDateForEqualitySign, key, searchCriteriaForDate);
                notifyViewsAboutDelete(adapterListOfOneDateForEqualitySignLD, deleteImageViewOnDialogEqualitySignDateLD);
                break;
            case "\u2260":
                helperDSCValue(listOfSelectedDatePositionsToDeleteInequalitySign, adapterListOfOneDateForInequalitySign, key, searchCriteriaForDate);
                notifyViewsAboutDelete(adapterListOfOneDateForInequalitySignLD, deleteImageViewOnDialogInequalitySignDateLD);
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                helperDSCValue(listOfSelectedDatePositionsToDeleteMoreAndLessSigns, adapterListOfRangeOfDateForMoreAndLessSigns, key, searchCriteriaForDate);
                notifyViewsAboutDelete(adapterListOfRangeOfDatesForMoreAndLessSignsLD, deleteImageViewOnDialogMoreAndLessSignsDateLD);
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод deleteSearchCriteriaValueForDate). Знак - " + key);
        }
    }

    public void deleteSearchCriteriaValueForNumber(String key) {
        switch (key) {
            case "=":
                helperDSCValue(listOfSelectedNumberPositionsToDeleteEqualitySign, adapterListOfOneNumberForEqualitySign, key, searchCriteriaForNumber);
                notifyViewsAboutDelete(adapterListOfOneNumberForEqualitySignLD, deleteImageViewOnDialogEqualitySignNumberLD);
                break;
            case "\u2260":
                helperDSCValue(listOfSelectedNumberPositionsToDeleteInequalitySign, adapterListOfOneNumberForInequalitySign, key, searchCriteriaForNumber);
                notifyViewsAboutDelete(adapterListOfOneNumberForInequalitySignLD, deleteImageViewOnDialogInequalitySignNumberLD);
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                helperDSCValue(listOfSelectedNumberPositionsToDeleteMoreAndLessSigns, adapterListOfRangeOfNumbersForMoreAndLessSigns, key, searchCriteriaForNumber);
                notifyViewsAboutDelete(adapterListOfRangeOfNumbersForMoreAndLessSignsLD, deleteImageViewOnDialogMoreAndLessSignsNumberLD);
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод deleteSearchCriteriaValueForDate). Знак - " + key);
        }
    }

    public void deleteSearchCriteriaValueForNote(String key) {
        switch (key) {
            case "=":
                helperDSCValue(listOfSelectedNotePositionsToDeleteEqualitySign, adapterListOfNoteForEqualitySign, key, searchCriteriaForNote);
                notifyViewsAboutDelete(adapterListOfNoteForEqualitySignLD, deleteImageViewOnDialogEqualitySignNoteLD);
                break;
            case "\u2260":
                helperDSCValue(listOfSelectedNotePositionsToDeleteInequalitySign, adapterListOfNoteForInequalitySign, key, searchCriteriaForNote);
                notifyViewsAboutDelete(adapterListOfNoteForInequalitySignLD, deleteImageViewOnDialogInequalitySignNoteLD);
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод deleteSearchCriteriaValueForDate). Знак - " + key);
        }
    }

    private void notifyViewAboutAddSelectedItemToListOfDeletedItems(MutableLiveData<Boolean> hLiveData) {
        Boolean b = hLiveData.getValue();
        if (b == null || !b) hLiveData.setValue(true);
    }

    public void addSelectedItemToListOfDeletedDate(String sign, int id) {
        switch (sign) {
            case "=":
                listOfSelectedDatePositionsToDeleteEqualitySign.add(id);
                notifyViewAboutAddSelectedItemToListOfDeletedItems(deleteImageViewOnDialogEqualitySignDateLD);
                break;
            case "\u2260":
                listOfSelectedDatePositionsToDeleteInequalitySign.add(id);
                notifyViewAboutAddSelectedItemToListOfDeletedItems(deleteImageViewOnDialogInequalitySignDateLD);
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                listOfSelectedDatePositionsToDeleteMoreAndLessSigns.add(id);
                notifyViewAboutAddSelectedItemToListOfDeletedItems(deleteImageViewOnDialogMoreAndLessSignsDateLD);
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод addSelectedItemToListOfDeletedDate(String sign, int positon). Знак - " + sign);
        }
    }

    public void addSelectedItemToListOfDeletedNumber(String sign, int id) {
        switch (sign) {
            case "=":
                listOfSelectedNumberPositionsToDeleteEqualitySign.add(id);
                notifyViewAboutAddSelectedItemToListOfDeletedItems(deleteImageViewOnDialogEqualitySignNumberLD);
                break;
            case "\u2260":
                listOfSelectedNumberPositionsToDeleteInequalitySign.add(id);
                notifyViewAboutAddSelectedItemToListOfDeletedItems(deleteImageViewOnDialogInequalitySignNumberLD);
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                listOfSelectedNumberPositionsToDeleteMoreAndLessSigns.add(id);
                notifyViewAboutAddSelectedItemToListOfDeletedItems(deleteImageViewOnDialogMoreAndLessSignsNumberLD);
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод  void addSelectedItemToListOfDeletedNumber(String sign, int id). Знак - " + sign);
        }
    }

    public void addSelectedItemToListOfDeletedNote(String sign, int id) {
        switch (sign) {
            case "=":
                listOfSelectedNotePositionsToDeleteEqualitySign.add(id);
                notifyViewAboutAddSelectedItemToListOfDeletedItems(deleteImageViewOnDialogEqualitySignNoteLD);
                break;
            case "\u2260":
                listOfSelectedNotePositionsToDeleteInequalitySign.add(id);
                notifyViewAboutAddSelectedItemToListOfDeletedItems(deleteImageViewOnDialogInequalitySignNoteLD);
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод  void addSelectedItemToListOfDeletedNumber(String sign, int id). Знак - " + sign);
        }
    }

    private void helperRemoveSelectedItemFromListOfDeletedItems(List<Integer> hList, int id, MutableLiveData<Boolean> hLiveData) {
        int i = 0;
        while (hList.get(i) != id) i++;
        hList.remove(i);
        if (hList.size() == 0) hLiveData.setValue(false);
    }

    public void removeSelectedItemFromListOfDeletedDate(String sign, int id) {
        switch (sign) {
            case "=":
                helperRemoveSelectedItemFromListOfDeletedItems(listOfSelectedDatePositionsToDeleteEqualitySign, id, deleteImageViewOnDialogEqualitySignDateLD);
                break;
            case "\u2260":
                helperRemoveSelectedItemFromListOfDeletedItems(listOfSelectedDatePositionsToDeleteInequalitySign, id, deleteImageViewOnDialogInequalitySignDateLD);
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                helperRemoveSelectedItemFromListOfDeletedItems(listOfSelectedDatePositionsToDeleteMoreAndLessSigns, id, deleteImageViewOnDialogMoreAndLessSignsDateLD);
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод removeSelectedItemFromListOfDeletedDate(String sign, int position). sign - " + sign);
        }
    }

    public void removeSelectedItemFromListOfDeletedNumber(String sign, int id) {
        switch (sign) {
            case "=":
                helperRemoveSelectedItemFromListOfDeletedItems(listOfSelectedNumberPositionsToDeleteEqualitySign, id, deleteImageViewOnDialogEqualitySignNumberLD);
                break;
            case "\u2260":
                helperRemoveSelectedItemFromListOfDeletedItems(listOfSelectedNumberPositionsToDeleteInequalitySign, id, deleteImageViewOnDialogInequalitySignNumberLD);
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                helperRemoveSelectedItemFromListOfDeletedItems(listOfSelectedNumberPositionsToDeleteMoreAndLessSigns, id, deleteImageViewOnDialogMoreAndLessSignsNumberLD);
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод void removeSelectedItemFromListOfDeletedNumber(String sign, int id). sign - " + sign);
        }
    }

    public void removeSelectedItemFromListOfDeletedNote(String sign, int id) {
        switch (sign) {
            case "=":
                helperRemoveSelectedItemFromListOfDeletedItems(listOfSelectedNotePositionsToDeleteEqualitySign, id, deleteImageViewOnDialogEqualitySignNoteLD);
                break;
            case "\u2260":
                helperRemoveSelectedItemFromListOfDeletedItems(listOfSelectedNotePositionsToDeleteInequalitySign, id, deleteImageViewOnDialogInequalitySignNoteLD);
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод void removeSelectedItemFromListOfDeletedNumber(String sign, int id). sign - " + sign);
        }
    }

    private boolean helperCheckerItemFromListOfDeletedPositions(List<Integer> hList, int id) {
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

    public boolean isCheckedSelectableItemFromListOfDeletedDatePosition(String sign, int id) {
        switch (sign) {
            case "=":
                if (listOfSelectedDatePositionsToDeleteEqualitySign == null)
                    listOfSelectedDatePositionsToDeleteEqualitySign = new ArrayList<>();
                return helperCheckerItemFromListOfDeletedPositions(listOfSelectedDatePositionsToDeleteEqualitySign, id);
            case "\u2260":
                if (listOfSelectedDatePositionsToDeleteInequalitySign == null)
                    listOfSelectedDatePositionsToDeleteInequalitySign = new ArrayList<>();
                return helperCheckerItemFromListOfDeletedPositions(listOfSelectedDatePositionsToDeleteInequalitySign, id);
            case ("\u2a7e" + " " + "\u2a7d"):
                if (listOfSelectedDatePositionsToDeleteMoreAndLessSigns == null)
                    listOfSelectedDatePositionsToDeleteMoreAndLessSigns = new ArrayList<>();
                return helperCheckerItemFromListOfDeletedPositions(listOfSelectedDatePositionsToDeleteMoreAndLessSigns, id);
            default:
                throw new RuntimeException("Опечатка в константах. Метод isCheckedSelectableItemFromListOfDeletedPosition(String sign, int position). Знак - " + sign);
        }
    }

    public boolean isCheckedSelectableItemFromListOfDeletedNumberPosition(String sign, int id) {
        switch (sign) {
            case "=":
                if (listOfSelectedNumberPositionsToDeleteEqualitySign == null)
                    listOfSelectedNumberPositionsToDeleteEqualitySign = new ArrayList<>();
                return helperCheckerItemFromListOfDeletedPositions(listOfSelectedNumberPositionsToDeleteEqualitySign, id);
            case "\u2260":
                if (listOfSelectedNumberPositionsToDeleteInequalitySign == null)
                    listOfSelectedNumberPositionsToDeleteInequalitySign = new ArrayList<>();
                return helperCheckerItemFromListOfDeletedPositions(listOfSelectedNumberPositionsToDeleteInequalitySign, id);
            case ("\u2a7e" + " " + "\u2a7d"):
                if (listOfSelectedNumberPositionsToDeleteMoreAndLessSigns == null)
                    listOfSelectedNumberPositionsToDeleteMoreAndLessSigns = new ArrayList<>();
                return helperCheckerItemFromListOfDeletedPositions(listOfSelectedNumberPositionsToDeleteMoreAndLessSigns, id);
            default:
                throw new RuntimeException("Опечатка в константах. Метод boolean isCheckedSelectableItemFromListOfDeletedNumberPosition(String sign, int id). sign - " + sign);
        }
    }

    public boolean isCheckedSelectableItemFromListOfDeletedNotePosition(String sign, int id) {
        switch (sign) {
            case "=":
                if (listOfSelectedNotePositionsToDeleteEqualitySign == null)
                    listOfSelectedNotePositionsToDeleteEqualitySign = new ArrayList<>();
                return helperCheckerItemFromListOfDeletedPositions(listOfSelectedNotePositionsToDeleteEqualitySign, id);
            case "\u2260":
                if (listOfSelectedNotePositionsToDeleteInequalitySign == null)
                    listOfSelectedNotePositionsToDeleteInequalitySign = new ArrayList<>();
                return helperCheckerItemFromListOfDeletedPositions(listOfSelectedNotePositionsToDeleteInequalitySign, id);
            default:
                throw new RuntimeException("Опечатка в константах. Метод boolean isCheckedSelectableItemFromListOfDeletedNotePosition(String sign, int id). sign - " + sign);
        }
    }

    private <T> String helperMethodForAddValuesToQuery(String nameOfColumn, Map.Entry<String, List<T>> entry, boolean isWhereExist) {
        StringBuilder sb = new StringBuilder();
        if (entry.getValue() != null && entry.getValue().size() > 0) {
            if (isWhereExist) sb.append(" AND ");
            else sb.append(" WHERE ");
            switch (entry.getKey()) {
                case ("\u2a7e"):
                    sb.append(nameOfColumn).append(" >= ").append(entry.getValue().get(0));
                    break;
                case ("\u2a7d"):
                    sb.append(nameOfColumn).append(" <= ").append(entry.getValue().get(0));
                    break;
                case ("="):
                case ("\u2260"):
                    sb.append(nameOfColumn);
                    if (entry.getKey().equals("=")) sb.append(" IN (");
                    else sb.append(" NOT IN (");
                    for (int i = 0; i < entry.getValue().size() - 1; i++)
                        sb.append(entry.getValue().get(i)).append(", ");
                    sb.append(entry.getValue().get(entry.getValue().size() - 1)).append(")");
                    break;
                case ("\u2a7e" + " " + "\u2a7d"):
                    sb.append("(");
                    int m = (entry.getValue().size() / 2) - 1;
                    for (int i = 0; i < m; i++) {
                        sb.append("(").append(nameOfColumn).append(" BETWEEN ").append(entry.getValue().get(i * 2))
                                .append(" AND ").append(entry.getValue().get(i * 2 + 1)).append(") OR ");
                    }
                    sb.append("(").append(nameOfColumn).append(" BETWEEN ").append(entry.getValue().get(m * 2))
                            .append(" AND ").append(entry.getValue().get(m * 2 + 1)).append("))");
                    break;
                default:
                    throw new RuntimeException("Опечатка в константах. Метод String helperMethodForAddValuesToQuery(). key -" + entry.getKey());
            }
        }
        return sb.toString();
    }

    private <T> String addSearchCriteriaOfValuesToQuery(String nameOfColumn, Map<String, List<T>> listOfSelectedValues, boolean isWhereExist) {
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, List<T>>> entrySet = listOfSelectedValues.entrySet();
        if (listOfSelectedValues.containsKey("\u2a7e") && listOfSelectedValues.containsKey("\u2a7d")) {
            List<T> listOfLessValues = listOfSelectedValues.get("\u2a7d");
            List<T> listOfMoreValues = listOfSelectedValues.get("\u2a7e");
            if (listOfLessValues != null && listOfLessValues.size() > 0 && listOfMoreValues != null && listOfMoreValues.size() > 0) {
                if (isWhereExist) sb.append(" AND ");
                else sb.append(" WHERE ");
                String lessThanValue = listOfLessValues.toString().replaceAll("[\\[\\]]", "");
                String moreThanValue = listOfMoreValues.toString().replaceAll("[\\[\\]]", "");
                sb.append("(").append(nameOfColumn).append(" <= ").append(lessThanValue)
                        .append(" OR ").append(nameOfColumn).append(" >= ").append(moreThanValue).append(")");
                for (Map.Entry<String, List<T>> entry : entrySet) {
                    if (!entry.getKey().equals("\u2a7e") && !entry.getKey().equals("\u2a7d")) {
                        sb.append(helperMethodForAddValuesToQuery(nameOfColumn, entry, true));
                    }
                }
            }
        } else {
            for (Map.Entry<String, List<T>> entry : entrySet) {
                if (isWhereExist) sb.append(helperMethodForAddValuesToQuery(nameOfColumn, entry, isWhereExist));
                else {
                    sb.append(helperMethodForAddValuesToQuery(nameOfColumn, entry, isWhereExist));
                    isWhereExist = true;
                }
            }
        }
        return sb.toString();
    }

    private String getRegExp(String s) {
        return "'(?i)" + ".*?" + s + ".*'";
    }

    private String helperMethodForAddNotesToQuery(Map.Entry<String, List<String>> entry, boolean isWhereExist) {
        if (entry.getValue() != null && entry.getValue().size() > 0) {
            StringBuilder sb = new StringBuilder();
            String regex;
            if (isWhereExist) sb.append(" AND (");
            else sb.append(" WHERE (");
            String columnName = CWDBHelper.TABLE_NAME_RESULT + "." + CWDBHelper.T_RESULT_C_NOTE;
            String regExOrNotRegExStatement = entry.getKey().equals("=") ? " REGEXP " : " NOT REGEXP ";
            String AND_OR_Statement =  entry.getKey().equals("=") ? " OR " : " AND ";
            for (int i = 0; i < entry.getValue().size() - 1; i++) {
                regex = getRegExp(entry.getValue().get(i));
                sb.append(columnName).append(regExOrNotRegExStatement).append(regex).append(AND_OR_Statement);
            }
            regex = getRegExp(entry.getValue().get(entry.getValue().size() - 1));
            return sb.append(columnName).append(regExOrNotRegExStatement).append(regex).append(")").toString();
        } else return "";
    }

    private String addSearchCriteriaOfNotesToQuery(boolean isWhereExist) {
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, List<String>>> entrySet = searchCriteriaForNote.entrySet();
        for (Map.Entry<String, List<String>> entry : entrySet) {
            if (isWhereExist)
                sb.append(helperMethodForAddNotesToQuery(entry, isWhereExist));
            else {
                sb.append(helperMethodForAddNotesToQuery(entry, isWhereExist));
                isWhereExist = true;
            }
        }
        return sb.toString();
    }

    private String addSearchCriteriaOfItemsToQuery(String nameOfColumn, List<SimpleEntityForDB> listOfSelectedItems, boolean isWhereExist) {
        StringBuilder sb = new StringBuilder();
        if (isWhereExist) sb.append(" AND ");
        else sb.append(" WHERE ");
        sb.append(nameOfColumn).append(" in (");
        for (int i = 0; i < listOfSelectedItems.size() - 1; i++) sb.append(listOfSelectedItems.get(i).getID()).append(", ");
        sb.append(listOfSelectedItems.get(listOfSelectedItems.size() -1).getID()).append(")");
        return sb.toString();
    }

    private String getFirstPartOfQuery() {
        return getApplication().getResources().getString(R.string.search_criteria_for_edit_delete_data);
    }

    public String getQuery() {
        boolean whereStatement = false;
        String query = getFirstPartOfQuery();
        StringBuilder sb = new StringBuilder(query);
        if (listOfSelectedEmployees != null && listOfSelectedEmployees.size() != 0) {
            sb.append(addSearchCriteriaOfItemsToQuery(CWDBHelper.TABLE_NAME_EMP + "._id", listOfSelectedEmployees, whereStatement));
            whereStatement = true;
        }
        if (listOfSelectedFirms != null && listOfSelectedFirms.size() != 0) {
            sb.append(addSearchCriteriaOfItemsToQuery(CWDBHelper.TABLE_NAME_FIRM + "._id", listOfSelectedFirms, whereStatement));
            if (!whereStatement) whereStatement = true;
        }
        if (listOfSelectedTOW != null && listOfSelectedTOW.size() != 0) {
            sb.append(addSearchCriteriaOfItemsToQuery(CWDBHelper.TABLE_NAME_TYPE_OF_WORK + "._id", listOfSelectedTOW, whereStatement));
            if (!whereStatement) whereStatement = true;
        }
        if (listOfSelectedPOW != null && listOfSelectedPOW.size() != 0) {
            sb.append(addSearchCriteriaOfItemsToQuery(CWDBHelper.TABLE_NAME_PLACE_OF_WORK + "._id", listOfSelectedPOW, whereStatement));
            if (!whereStatement) whereStatement = true;
        }
        if (searchCriteriaForDate != null && searchCriteriaForDate.size() != 0) {
            sb.append(addSearchCriteriaOfValuesToQuery(CWDBHelper.TABLE_NAME_RESULT + "." + CWDBHelper.T_RESULT_C_DATE, searchCriteriaForDate, whereStatement));
            if (!whereStatement) whereStatement = true;
        }
        if (searchCriteriaForNumber != null && searchCriteriaForNumber.size() != 0) {
            sb.append(addSearchCriteriaOfValuesToQuery(CWDBHelper.TABLE_NAME_RESULT + "." + CWDBHelper.T_RESULT_C_VALUE, searchCriteriaForNumber, whereStatement));
            if (!whereStatement) whereStatement = true;
        }
        if (searchCriteriaForNote != null && searchCriteriaForNote.size() != 0) {
            sb.append(addSearchCriteriaOfNotesToQuery(whereStatement));
        }
        return sb.toString();
    }
}
