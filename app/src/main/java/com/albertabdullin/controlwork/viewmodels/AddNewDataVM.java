package com.albertabdullin.controlwork.viewmodels;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.albertabdullin.controlwork.activities.FillNewData_Activity;
import com.albertabdullin.controlwork.db_of_app.CWDBHelper;

import java.util.concurrent.CountDownLatch;

public class AddNewDataVM extends AndroidViewModel {
    private MutableLiveData<String> employerEditTextLD;
    private MutableLiveData<String> firmEditTextLD;
    private MutableLiveData<String> placeOfWorkEditTextLD;
    private MutableLiveData<String> typeOfWorkEditTextLD;
    private MutableLiveData<Integer> warningEditTextLD;
    private MutableLiveData<Integer> warningTextViewLD;
    private MutableLiveData<Boolean> prepareAddButtonLD;
    private MutableLiveData<String> changeTextAddButtonLD;
    private boolean correctEmployerData;
    private String employerString;
    private int employerId = -1;
    private boolean correctFirmData;
    private String firmString;
    private int firmId = -1;
    private boolean correctPoWData;
    private String powString;
    private int powId = -1;
    private boolean correctToWData;
    private String towString;
    private int towId = -1;
    private boolean correctResultValueData;
    private Long dateForSql;
    private Float resultValueFloat;
    private String resultValueString = "";
    private boolean firstLaunch = true;
    private String note = "";
    public static final int INCORRECT_EMPLOYEE_ET = 0;
    public static final int INCORRECT_FIRM_ET = 1;
    public static final int INCORRECT_TYPE_ET = 2;
    public static final int INCORRECT_PLACE_ET = 3;
    public static final int INCORRECT_RES_VALUE_ET = 4;
    private class getFirstEmployerThread extends Thread {
        public static final String LOAD_ITEMS_TAG = "FirstEmployerThread";
        @Override
        public void run() {
            boolean ok = true;
            Message message;
            try (SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
                 SQLiteDatabase db = cwdbHelper.getReadableDatabase(); Cursor cursor = db.query(CWDBHelper.TABLE_NAME_EMP,
                    new String[]{"_id", CWDBHelper.T_EMP_C_FIO},
                    "_id = ?",
                    new String[]{"1"}, null, null, null)) {
                if (cursor.moveToFirst()) {
                    setEmployerId(cursor.getInt(0));
                    employerString = cursor.getString(1);
                } else ok = false;
            } catch (SQLiteException e) {
                Log.e(LOAD_ITEMS_TAG, "не получилось прочесть данные из таблицы " + CWDBHelper.TABLE_NAME_EMP);
            }
            if (ok) {
                message = FillNewData_Activity.handler.obtainMessage(FillNewData_Activity.GET_EMPLOYER_MESSAGE);
                FillNewData_Activity.handler.sendMessage(message);
            }
        }
    }

    private class getFirstFirmThread extends Thread {
        public static final String LOAD_ITEMS_TAG = "FirstFirmThread";
        @Override
        public void run() {
            boolean ok = true;
            Message message;
            try (SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
                 SQLiteDatabase db = cwdbHelper.getReadableDatabase();
                 Cursor cursor = db.query(CWDBHelper.TABLE_NAME_FIRM,
                    new String[]{"_id", CWDBHelper.T_FIRM_C_DESCRIPTION},
                    "_id = ?",
                    new String[]{"1"}, null, null, null)) {
                if (cursor.moveToFirst()) {
                    setFirmId(cursor.getInt(0));
                    firmString = cursor.getString(1);
                } else ok = false;
            } catch (SQLiteException e) {
                Log.e(LOAD_ITEMS_TAG, "не получилось прочесть данные из таблицы " + CWDBHelper.TABLE_NAME_FIRM);
            }
            if (ok) {
                message = FillNewData_Activity.handler.obtainMessage(FillNewData_Activity.GET_FIRM_MESSAGE);
                FillNewData_Activity.handler.sendMessage(message);
            }
        }
    }

    private class getFirstPoWThread extends Thread {
        public static final String LOAD_ITEMS_TAG = "FirstPoWThread";
        @Override
        public void run() {
            boolean ok = true;
            Message message;
            try (SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
                 SQLiteDatabase db = cwdbHelper.getReadableDatabase();
                 Cursor cursor = db.query(CWDBHelper.TABLE_NAME_PLACE_OF_WORK,
                    new String[]{"_id", CWDBHelper.T_PLACE_OF_WORK_C_DESCRIPTION},
                    "_id = ?",
                    new String[]{"1"}, null, null, null)) {
                if (cursor.moveToFirst()) {
                    setPowId(cursor.getInt(0));
                    powString = cursor.getString(1);
                } else ok = false;
            } catch (SQLiteException e) {
                Log.e(LOAD_ITEMS_TAG, "не получилось прочесть данные из таблицы " + CWDBHelper.TABLE_NAME_PLACE_OF_WORK);
            }
            if (ok) {
                message = FillNewData_Activity.handler.obtainMessage(FillNewData_Activity.GET_POW_MESSAGE);
                FillNewData_Activity.handler.sendMessage(message);
            }
        }
    }

    private class getFirstToWThread extends Thread {
        public static final String LOAD_ITEMS_TAG = "FirstToWThread";
        @Override
        public void run() {
            boolean ok = true;
            Message message;
            try (SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
                 SQLiteDatabase db = cwdbHelper.getReadableDatabase();
                 Cursor cursor = db.query(CWDBHelper.TABLE_NAME_TYPE_OF_WORK,
                    new String[]{"_id", CWDBHelper.T_TYPE_OF_WORK_C_DESCRIPTION},
                    "_id = ?",
                    new String[]{"1"}, null, null, null)) {
                if (cursor.moveToFirst()) {
                    setTowId(cursor.getInt(0));
                    towString = cursor.getString(1);
                } else ok = false;
            } catch (SQLiteException e) {
                Log.e(LOAD_ITEMS_TAG, "не получилось прочесть данные из таблицы " + CWDBHelper.TABLE_NAME_TYPE_OF_WORK);
            }
            if (ok) {
                message = FillNewData_Activity.handler.obtainMessage(FillNewData_Activity.GET_TOW_MESSAGE);
                FillNewData_Activity.handler.sendMessage(message);
            }
        }
    }

    private class AddResultDataThread extends Thread {
        public static final String ADD_DATA_THREAD = "AddResultDataThread";
        @Override
        public void run() {
            Message msg;
            long resultOfInsert = -1;
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
                Log.e(ADD_DATA_THREAD, "не получилось добавить данные");
            }
            if (resultOfInsert != -1) msg = FillNewData_Activity.handler.obtainMessage(FillNewData_Activity.ADD_DATA_TO_BD, 1, 0);
            else msg = FillNewData_Activity.handler.obtainMessage(FillNewData_Activity.ADD_DATA_TO_BD, 0, 0);
            FillNewData_Activity.handler.sendMessage(msg);
        }
    }


    private class CheckCorrectDataHeadThread extends Thread {
        CountDownLatch latch = new CountDownLatch(4);
        private boolean isItemExist(String tableName, int id) {
            String TAG = "Check item for exist";
            boolean ok = false;
            try (SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
                 SQLiteDatabase db = cwdbHelper.getReadableDatabase();
                 Cursor cursor = db.query(tableName,
                    new String[]{"_id"},
                    "_id = ?",
                    new String[]{Integer.toString(id)}, null, null, null)) {
                if (cursor.moveToFirst()) ok = true;
            } catch (SQLiteException e) {
                Log.e(TAG, "не получилось прочесть данные из таблицы " + tableName);
            }
            return ok;
        }

        private class CheckExistDataFromTableThread extends Thread {
            private CountDownLatch latch;
            private String tableName;
            private int id;
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

        @Override
        public void run() {
            CheckExistDataFromTableThread t1 =
                    new CheckExistDataFromTableThread(latch, CWDBHelper.TABLE_NAME_EMP, employerId);
            t1.start();
            CheckExistDataFromTableThread t2 =
                    new CheckExistDataFromTableThread(latch, CWDBHelper.TABLE_NAME_FIRM, firmId);
            t2.start();
            CheckExistDataFromTableThread t3 =
                    new CheckExistDataFromTableThread(latch, CWDBHelper.TABLE_NAME_TYPE_OF_WORK, towId);
            t3.start();
            CheckExistDataFromTableThread t4 =
                    new CheckExistDataFromTableThread(latch, CWDBHelper.TABLE_NAME_PLACE_OF_WORK, powId);
            t4.start();
            try {
                resultValueFloat = Float.parseFloat(resultValueString);
                correctResultValueData = true;
            } catch (NumberFormatException e) {
                Log.e("CheckCorrectDataHeadThread", "не удалость сделать преобразование данных в тип float");
                correctResultValueData = false;
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                Log.e("CheckCorrectDataHeadThread", "поток был прерван во время ожидания защелкой");
            }
            Message msg = FillNewData_Activity.handler.obtainMessage(FillNewData_Activity.NOTIFY_ABOUT_CHECK_DATA_FROM_DB);
            FillNewData_Activity.handler.sendMessage(msg);
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

    public LiveData<Integer> getWarningEmployerETLD() {
        if (warningEditTextLD == null) warningEditTextLD = new MutableLiveData<>();
        return warningEditTextLD;
    }

    public LiveData<Integer> getWarningEmployerTVLD() {
        if (warningTextViewLD == null) warningTextViewLD = new MutableLiveData<>();
        return warningTextViewLD;
    }

    public LiveData<Boolean> getPrepareAddButton() {
        if (prepareAddButtonLD == null) prepareAddButtonLD = new MutableLiveData<>();
        return prepareAddButtonLD;
    }

    public LiveData<String> getChangeTextAddButton() {
        if (changeTextAddButtonLD == null) changeTextAddButtonLD = new MutableLiveData<>();
        return changeTextAddButtonLD;
    }

    public void startGetEmployerThread() {
        getFirstEmployerThread firstEmployerThread = new getFirstEmployerThread();
        firstEmployerThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        firstEmployerThread.start();
    }

    public void setEmployerEditText() {
        employerEditTextLD.setValue(employerString);
    }

    public void startGetFirmThread() {
        getFirstFirmThread firstFirmThread = new getFirstFirmThread();
        firstFirmThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        firstFirmThread.start();
    }

    public void setFirmEditText() {
        firmEditTextLD.setValue(firmString);
    }

    public void startGetPoWThread() {
        getFirstPoWThread firstPoWThread = new getFirstPoWThread();
        firstPoWThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        firstPoWThread.start();
    }

    public void setPlaceOfWorkEditText() {
        placeOfWorkEditTextLD.setValue(powString);
    }

    public void startGetToWThread() {
        getFirstToWThread firstToWThread = new getFirstToWThread();
        firstToWThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        firstToWThread.start();
    }

    public void setTypeOfWorkEditText() {
        typeOfWorkEditTextLD.setValue(towString);
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

    public boolean isCorrectPoWData() {
        return correctPoWData;
    }

    public boolean isCorrectResultValueData() {
        return correctResultValueData;
    }

    public void setCorrectResultValueDataTrue() {
        correctResultValueData = true;
    }

    public void startToCheckCorrectData() {
        CheckCorrectDataHeadThread thread = new CheckCorrectDataHeadThread();
        thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
    }

    public void readResultOfCheck() {
        if (correctEmployerData && correctFirmData && correctToWData
                && correctPoWData && correctResultValueData) {
            changeTextAddButtonLD.setValue("Идёт процесс добавления данных");
            addResultData();
        }
        else {
            if (!correctEmployerData) {
                warningEditTextLD.setValue(INCORRECT_EMPLOYEE_ET);
                warningTextViewLD.setValue(INCORRECT_EMPLOYEE_ET);
            }
            if (!correctFirmData) {
                warningEditTextLD.setValue(INCORRECT_FIRM_ET);
                warningTextViewLD.setValue(INCORRECT_FIRM_ET);
            }
            if (!correctToWData) {
                warningEditTextLD.setValue(INCORRECT_TYPE_ET);
                warningTextViewLD.setValue(INCORRECT_TYPE_ET);
            }
            if (!correctPoWData) {
                warningEditTextLD.setValue(INCORRECT_PLACE_ET);
                warningTextViewLD.setValue(INCORRECT_PLACE_ET);
            }
            if (!correctResultValueData) {
                warningEditTextLD.setValue(INCORRECT_RES_VALUE_ET);
                warningTextViewLD.setValue(INCORRECT_RES_VALUE_ET);
            }
            prepareAddButtonLD.setValue(false);
            Toast toast = Toast.makeText(getApplication(), "Ошибка. Проверь корректность вводимых данных", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void addResultData() {
        AddResultDataThread addResultDataThread = new AddResultDataThread();
        addResultDataThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        addResultDataThread.start();
    }

    public void notifyAboutCompleteOperation(boolean res) {
        prepareAddButtonLD.setValue(true);
        String s;
        if (res) s = "Данные успешно добавлены";
        else s = "Не получилось добавить данные. Сообщи об этом разработчику";
        Toast toast = Toast.makeText(getApplication(), s, Toast.LENGTH_SHORT);
        toast.show();
    }

}
