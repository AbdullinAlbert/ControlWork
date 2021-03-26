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
import com.albertabdullin.controlwork.db_of_app.CWDBHelper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.albertabdullin.controlwork.activities.FillNewData_Activity.TABLE_OF_EMPLOYERS;
import static com.albertabdullin.controlwork.activities.FillNewData_Activity.TABLE_OF_FIRMS;
import static com.albertabdullin.controlwork.activities.FillNewData_Activity.TABLE_OF_PLACES_OF_WORK;
import static com.albertabdullin.controlwork.activities.FillNewData_Activity.TABLE_OF_TYPES_OF_WORK;

public class AddNewDataVM extends AndroidViewModel {
    private MutableLiveData<String> employerEditTextLD;
    private MutableLiveData<String> firmEditTextLD;
    private MutableLiveData<String> placeOfWorkEditTextLD;
    private MutableLiveData<String> typeOfWorkEditTextLD;
    private MutableLiveData<Boolean> warningEditTextForEmployeeLD;
    private MutableLiveData<Boolean> warningTextViewForEmployeeLD;
    private MutableLiveData<Boolean> warningEditTextForFirmLD;
    private MutableLiveData<Boolean> warningTextViewForFirmLD;
    private MutableLiveData<Boolean> warningEditTextForToWLD;
    private MutableLiveData<Boolean> warningTextViewForToWLD;
    private MutableLiveData<Boolean> warningEditTextForPoWLD;
    private MutableLiveData<Boolean> warningTextViewForPoWLD;
    private MutableLiveData<Boolean> warningEditTextForResultLD;
    private MutableLiveData<Boolean> warningTextViewForResultLD;
    private MutableLiveData<Boolean> prepareAddButtonLD;
    private MutableLiveData<String> changeTextAddButtonLD;
    private volatile boolean correctEmployerData;
    private int employerId = -1;
    private volatile boolean correctFirmData;
    private int firmId = -1;
    private volatile boolean correctPoWData;
    private int powId = -1;
    private volatile boolean correctToWData;
    private int towId = -1;
    private volatile boolean correctResultValueData;
    private Long dateForSql;
    private Float resultValueFloat;
    private String resultValueString = "";
    private boolean firstLaunch = true;
    private String note = "";
    private Executor executor;

    private class FirstItemGetterThread extends Thread {
        private final String mTableName;

        FirstItemGetterThread(String tableName) {
            mTableName = tableName;
        }

        @Override
        public void run() {
            try (SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
                 SQLiteDatabase db = cwdbHelper.getReadableDatabase();
                 Cursor cursor = db.rawQuery("SELECT * FROM " + mTableName  + " LIMIT 1", null, null)) {
                if (cursor.moveToFirst()) {
                    switch (mTableName) {
                        case CWDBHelper.TABLE_NAME_EMP:
                            setEmployerId(cursor.getInt(0));
                            employerEditTextLD.postValue(cursor.getString(1));
                            break;
                        case CWDBHelper.TABLE_NAME_FIRM:
                            setFirmId(cursor.getInt(0));
                            firmEditTextLD.postValue(cursor.getString(1));
                            break;
                    }

                }
            } catch (SQLiteException e) {
                FillNewData_Activity.handler.post(() ->
                        Toast.makeText(getApplication(), R.string.fail_attempt_about_load_data_from_primary_table, Toast.LENGTH_SHORT).show());
            }
        }
    }

    private class AddResultDataThread extends Thread {
        @Override
        public void run() {
            long resultOfInsert;
            try (SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
                 SQLiteDatabase db = cwdbHelper.getWritableDatabase()) {
                ContentValues cv = new ContentValues();
                cv.put(CWDBHelper.T_RESULT_C_ID_EMPLOYER, employerId);
                cv.put(CWDBHelper.T_RESULT_C_ID_FIRM, firmId);
                cv.put(CWDBHelper.T_RESULT_C_ID_POW, powId);
                cv.put(CWDBHelper.T_RESULT_C_ID_TOW, towId);
                cv.put(CWDBHelper.T_RESULT_C_DATE, dateForSql);
                cv.put(CWDBHelper.T_RESULT_C_VALUE, resultValueFloat);
                cv.put(CWDBHelper.T_RESULT_C_NOTE, note);
                resultOfInsert = db.insert(CWDBHelper.TABLE_NAME_RESULT, null, cv);
            } catch (SQLiteException e) {
                FillNewData_Activity.handler.post(() -> Toast.makeText(getApplication(), R.string.fail_attempt_about_write_data_to_table, Toast.LENGTH_SHORT).show());
                return;
            }
            if (resultOfInsert != -1) prepareAddButtonLD.postValue(true);
            int message = resultOfInsert != -1 ? R.string.data_has_been_added : R.string.fail_attempt_about_write_data_to_table;
            FillNewData_Activity.handler.post(() ->
                    Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show());
        }
    }


    private class CheckCorrectDataHeadThread extends Thread {
        CountDownLatch latch = new CountDownLatch(4);
        private boolean isItemExist(String tableName, int id) {
            boolean ok = false;
            try (SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
                 SQLiteDatabase db = cwdbHelper.getReadableDatabase();
                 Cursor cursor = db.query(tableName,
                    new String[]{"_id"},
                    "_id = ?",
                    new String[]{Integer.toString(id)}, null, null, null)) {
                if (cursor.moveToFirst()) ok = true;
            } catch (SQLiteException e) {
                FillNewData_Activity.handler.post(() ->
                        Toast.makeText(getApplication(), R.string.fail_attempt_about_get_data_from_table + " " + tableName, Toast.LENGTH_SHORT).show());
                ok = false;
            }
            return ok;
        }

        private class CheckExistDataFromTableThread extends Thread {
            private final CountDownLatch latch;
            private final String tableName;
            private final int id;

            public CheckExistDataFromTableThread(CountDownLatch latch, String tableName, int id) {
                this.latch = latch;
                this.tableName = tableName;
                this.id = id;
            }
            @Override
            public void run() {
                switch (tableName) {
                    case CWDBHelper.TABLE_NAME_EMP:
                        correctEmployerData = isItemExist(tableName, id);
                        break;
                    case CWDBHelper.TABLE_NAME_FIRM:
                        correctFirmData = isItemExist(tableName, id);
                        break;
                    case CWDBHelper.TABLE_NAME_TYPE_OF_WORK:
                        correctToWData = isItemExist(tableName, id);
                        break;
                    case CWDBHelper.TABLE_NAME_PLACE_OF_WORK:
                        correctPoWData = isItemExist(tableName, id);
                        break;
                }
                latch.countDown();
            }
        }

        private void readResultOfCheck() {
            if (correctEmployerData && correctFirmData && correctToWData
                    && correctPoWData && correctResultValueData) {
                changeTextAddButtonLD.postValue(getApplication().getString(R.string.adding_data_process_is_active));
                AddResultDataThread addResultDataThread = new AddResultDataThread();
                addResultDataThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
                executor.execute(addResultDataThread);
            }
            else {
                if (!correctEmployerData) {
                    warningEditTextForEmployeeLD.postValue(true);
                    warningTextViewForEmployeeLD.postValue(true);
                }
                if (!correctFirmData) {
                    warningEditTextForFirmLD.postValue(true);
                    warningTextViewForFirmLD.postValue(true);
                }
                if (!correctToWData) {
                    warningEditTextForToWLD.postValue(true);
                    warningTextViewForToWLD.postValue(true);
                }
                if (!correctPoWData) {
                    warningEditTextForPoWLD.postValue(true);
                    warningTextViewForPoWLD.postValue(true);
                }
                if (!correctResultValueData) {
                    warningEditTextForResultLD.postValue(true);
                    warningTextViewForResultLD.postValue(true);
                }
                prepareAddButtonLD.postValue(false);
                FillNewData_Activity.handler.post(() ->
                    Toast.makeText(getApplication(), R.string.error_check_data_for_correct, Toast.LENGTH_SHORT));
            }
        }


        @Override
        public void run() {
            CheckExistDataFromTableThread t1 =
                    new CheckExistDataFromTableThread(latch, CWDBHelper.TABLE_NAME_EMP, employerId);
            t1.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
            executor.execute(t1);
            CheckExistDataFromTableThread t2 =
                    new CheckExistDataFromTableThread(latch, CWDBHelper.TABLE_NAME_FIRM, firmId);
            t2.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
            executor.execute(t2);
            CheckExistDataFromTableThread t3 =
                    new CheckExistDataFromTableThread(latch, CWDBHelper.TABLE_NAME_TYPE_OF_WORK, towId);
            t3.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
            executor.execute(t3);
            CheckExistDataFromTableThread t4 =
                    new CheckExistDataFromTableThread(latch, CWDBHelper.TABLE_NAME_PLACE_OF_WORK, powId);
            t4.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
            executor.execute(t4);
            try {
                resultValueFloat = Float.parseFloat(resultValueString);
                correctResultValueData = true;
            } catch (NumberFormatException e) {
                correctResultValueData = false;
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                FillNewData_Activity.handler.post(() ->
                        Toast.makeText(getApplication(), R.string.thread_for_check_data_was_interrupted, Toast.LENGTH_SHORT).show());
                return;
            }
            readResultOfCheck();
        }
     }

    public AddNewDataVM(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getLiveDataEmployerText() {
        if (employerEditTextLD == null) employerEditTextLD = new MutableLiveData<>();
        return employerEditTextLD;
    }

    public LiveData<String> getLiveDataFirmText() {
        if (firmEditTextLD == null) firmEditTextLD = new MutableLiveData<>();
        return firmEditTextLD;
    }

    public LiveData<String> getLiveDataPoWText() {
        if (placeOfWorkEditTextLD == null) placeOfWorkEditTextLD = new MutableLiveData<>();
        return placeOfWorkEditTextLD;
    }

    public LiveData<String> getLiveDataToWText() {
        if (typeOfWorkEditTextLD == null) typeOfWorkEditTextLD = new MutableLiveData<>();
        return typeOfWorkEditTextLD;
    }

    public LiveData<Boolean> getWarningEmployerETLD() {
        if (warningEditTextForEmployeeLD == null) warningEditTextForEmployeeLD = new MutableLiveData<>();
        return warningEditTextForEmployeeLD;
    }

    public LiveData<Boolean> getWarningEmployerTVLD() {
        if (warningTextViewForEmployeeLD == null) warningTextViewForEmployeeLD = new MutableLiveData<>();
        return warningTextViewForEmployeeLD;
    }

    public LiveData<Boolean> getWarningFirmETLD() {
        if (warningEditTextForFirmLD == null) warningEditTextForFirmLD = new MutableLiveData<>();
        return warningEditTextForFirmLD;
    }

    public LiveData<Boolean> getWarningFirmTVLD() {
        if (warningTextViewForFirmLD == null) warningTextViewForFirmLD = new MutableLiveData<>();
        return warningTextViewForFirmLD;
    }

    public LiveData<Boolean> getWarningTypeOfWorkETLD() {
        if (warningEditTextForToWLD == null) warningEditTextForToWLD = new MutableLiveData<>();
        return warningEditTextForToWLD;
    }

    public LiveData<Boolean> getWarningTypeOfWorkTVLD() {
        if (warningTextViewForToWLD == null) warningTextViewForToWLD = new MutableLiveData<>();
        return warningTextViewForToWLD;
    }

    public LiveData<Boolean> getWarningPlaceOfWorkETLD() {
        if (warningEditTextForPoWLD == null) warningEditTextForPoWLD = new MutableLiveData<>();
        return warningEditTextForPoWLD;
    }

    public LiveData<Boolean> getWarningPlaceOfWorkTVLD() {
        if (warningTextViewForPoWLD == null) warningTextViewForPoWLD = new MutableLiveData<>();
        return warningTextViewForPoWLD;
    }

    public LiveData<Boolean> getWarningResultETLD() {
        if (warningEditTextForResultLD == null) warningEditTextForResultLD = new MutableLiveData<>();
        return warningEditTextForResultLD;
    }

    public LiveData<Boolean> getWarningResultTVLD() {
        if (warningTextViewForResultLD == null) warningTextViewForResultLD = new MutableLiveData<>();
        return warningTextViewForResultLD;
    }

    public LiveData<Boolean> getPrepareAddButton() {
        if (prepareAddButtonLD == null) prepareAddButtonLD = new MutableLiveData<>();
        return prepareAddButtonLD;
    }

    public LiveData<String> getChangeTextAddButton() {
        if (changeTextAddButtonLD == null) changeTextAddButtonLD = new MutableLiveData<>();
        return changeTextAddButtonLD;
    }

    public void getFirstItemsFromDBTables() {
        if (executor == null) executor = Executors.newFixedThreadPool(2);
        FirstItemGetterThread getFirstEmployer = new FirstItemGetterThread(CWDBHelper.TABLE_NAME_EMP);
        getFirstEmployer.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        FirstItemGetterThread getFirstFirm = new FirstItemGetterThread(CWDBHelper.TABLE_NAME_FIRM);
        getFirstFirm.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        executor.execute(getFirstEmployer);
        executor.execute(getFirstFirm);
    }

    public void setEmployerId(int i) { employerId = i; }

    public void setFirmId(int i) { firmId = i; }

    public void setPowId(int i) { powId = i; }

    public void setTowId(int i) { towId = i; }

    public void setNote(String s) { note = s; }

    public void setResultValueString(String s) { resultValueString = s; }

    public boolean isFirstLaunch() { return firstLaunch; }

    public void setFirstLaunchFalse() {
        firstLaunch = false;
    }

    public void setDateForSql(Long l) {
        dateForSql = l / 1000L;
    }

    public boolean isCorrectEmployerData() {
        return correctEmployerData;
    }

    public boolean isCorrectFirmData() {
        return correctFirmData;
    }

    public boolean isCorrectToWData() {
        return correctToWData;
    }

    public boolean isCorrectPoWData() { return correctPoWData; }

    public boolean isCorrectResultValueData() {
        return correctResultValueData;
    }

    public void setCorrectResultValueDataTrue() {
        correctResultValueData = true;
    }

    public void startToCheckCorrectData() {
        CheckCorrectDataHeadThread thread = new CheckCorrectDataHeadThread();
        thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        executor.execute(thread);
    }

    public void deleteEmphasizeFromEditTextAndTextView(int tableName) {
        switch (tableName) {
            case TABLE_OF_EMPLOYERS:
                warningEditTextForEmployeeLD.setValue(false);
                warningTextViewForEmployeeLD.setValue(false);
                break;
            case TABLE_OF_FIRMS:
                warningEditTextForFirmLD.setValue(false);
                warningTextViewForFirmLD.setValue(false);
                break;
            case TABLE_OF_PLACES_OF_WORK:
                warningEditTextForPoWLD.setValue(false);
                warningTextViewForPoWLD.setValue(false);
                break;
            case TABLE_OF_TYPES_OF_WORK:
                warningEditTextForToWLD.setValue(false);
                warningTextViewForToWLD.setValue(false);
                break;
        }
    }

}
