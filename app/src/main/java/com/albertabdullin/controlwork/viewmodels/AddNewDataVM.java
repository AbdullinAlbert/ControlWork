package com.albertabdullin.controlwork.viewmodels;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.albertabdullin.controlwork.activities.FillNewData_Activity;
import com.albertabdullin.controlwork.db_of_app.CWDBHelper;

import java.util.Calendar;
import java.util.concurrent.CountDownLatch;

public class AddNewDataVM extends AndroidViewModel {
    private MutableLiveData<String> employerEditTextLD;
    private MutableLiveData<String> firmEditTextLD;
    private MutableLiveData<String> placeOfWorkEditTextLD;
    private MutableLiveData<String> typeOfWorkEditTextLD;
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
    private String dateForSql;
    private Float resultValue;
    private boolean firstLaunch = true;
    private FillNewData_Activity context;

    private class getFirstEmployerThread extends Thread {
        public static final String LOAD_ITEMS_TAG = "FirstEmployerThread";
        @Override
        public void run() {
            boolean ok = true;
            Message message;
            SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
            SQLiteDatabase db = null;
            Cursor cursor = null;
            try {
                db = cwdbHelper.getReadableDatabase();
                cursor = db.query(CWDBHelper.TABLE_NAME_EMP,
                        new String[]{"_id", CWDBHelper.T_EMP_C_FIO},
                        "_id = ?", new String[] {"1"}, null, null, null);
                if(cursor.moveToFirst()) {
                    setEmployerId(cursor.getInt(0));
                    employerString = cursor.getString(1);
                } else ok = false;
            } catch (SQLiteException e) {
                Log.e(LOAD_ITEMS_TAG, "не получилось прочесть данные из таблицы " + CWDBHelper.TABLE_NAME_EMP);
            } finally {
                cwdbHelper.close();
                if(cursor != null) cursor.close();
                if(db != null) db.close();
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
            SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
            SQLiteDatabase db = null;
            Cursor cursor = null;
            try {
                db = cwdbHelper.getReadableDatabase();
                cursor = db.query(CWDBHelper.TABLE_NAME_FIRM,
                        new String[]{"_id", CWDBHelper.T_FIRM_C_DESCRIPTION},
                        "_id = ?", new String[] {"1"}, null, null, null);
                if(cursor.moveToFirst()) {
                    setFirmId(cursor.getInt(0));
                    firmString = cursor.getString(1);
                } else ok = false;
            } catch (SQLiteException e) {
                Log.e(LOAD_ITEMS_TAG, "не получилось прочесть данные из таблицы " + CWDBHelper.TABLE_NAME_FIRM);
            } finally {
                cwdbHelper.close();
                if(cursor != null) cursor.close();
                if(db != null) db.close();
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
            SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
            SQLiteDatabase db = null;
            Cursor cursor = null;
            try {
                db = cwdbHelper.getReadableDatabase();
                cursor = db.query(CWDBHelper.TABLE_NAME_PLACE_OF_WORK,
                        new String[]{"_id", CWDBHelper.T_PLACE_OF_WORK_C_DESCRIPTION},
                        "_id = ?", new String[] {"1"}, null, null, null);
                if(cursor.moveToFirst()) {
                    setPowId(cursor.getInt(0));
                    powString = cursor.getString(1);
                } else ok = false;
            } catch (SQLiteException e) {
                Log.e(LOAD_ITEMS_TAG, "не получилось прочесть данные из таблицы " + CWDBHelper.TABLE_NAME_PLACE_OF_WORK);
            } finally {
                cwdbHelper.close();
                if(cursor != null) cursor.close();
                if(db != null) db.close();
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
            SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
            SQLiteDatabase db = null;
            Cursor cursor = null;
            try {
                db = cwdbHelper.getReadableDatabase();
                cursor = db.query(CWDBHelper.TABLE_NAME_TYPE_OF_WORK,
                        new String[]{"_id", CWDBHelper.T_TYPE_OF_WORK_C_DESCRIPTION},
                        "_id = ?", new String[] {"1"}, null, null, null);
                if(cursor.moveToFirst()) {
                    setTowId(cursor.getInt(0));
                    towString = cursor.getString(1);
                } else ok = false;
            } catch (SQLiteException e) {
                Log.e(LOAD_ITEMS_TAG, "не получилось прочесть данные из таблицы " + CWDBHelper.TABLE_NAME_TYPE_OF_WORK);
            } finally {
                cwdbHelper.close();
                if(cursor != null) cursor.close();
                if(db != null) db.close();
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
            SQLiteOpenHelper cwdbHelper = null;
            SQLiteDatabase db = null;
            Cursor cursor = null;
            int dateInSeconds = 0;
            boolean isOk = true;
            long resultOfInsert = -1;
            try {
                cwdbHelper = new CWDBHelper(getApplication());
                db = cwdbHelper.getReadableDatabase();
                cursor = db.rawQuery("SELECT strftime('%s'," + dateForSql + ")", null);
                if (cursor.moveToFirst()) dateInSeconds = cursor.getInt(0);
                else isOk = false;
                if (isOk) {
                    ContentValues cv = new ContentValues();
                    cv.put(CWDBHelper.T_RESULT_C_ID_EMPLOYER, employerId);
                    cv.put(CWDBHelper.T_RESULT_C_ID_FIRM, firmId);
                    cv.put(CWDBHelper.T_RESULT_C_ID_POW, powId);
                    cv.put(CWDBHelper.T_RESULT_C_ID_TOW, towId);
                    cv.put(CWDBHelper.T_RESULT_C_DATE, dateInSeconds);
                    cv.put(CWDBHelper.T_RESULT_C_VALUE, resultValue);
                    cv.put(CWDBHelper.T_RESULT_C_NOTE, context.getNote().getText().toString());
                    db = cwdbHelper.getWritableDatabase();
                    resultOfInsert = db.insert(CWDBHelper.TABLE_NAME_RESULT, null, cv);
                }
            } catch (SQLiteException e) {
                Log.e(ADD_DATA_THREAD, "ошибка при попытке конвертировании даты");
            } finally {
                if (db != null) db.close();
                if (cwdbHelper != null) cwdbHelper.close();
                if (cursor != null) cursor.close();
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
            SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplication());
            SQLiteDatabase db = null;
            Cursor cursor = null;
            try {
                db = cwdbHelper.getReadableDatabase();
                cursor = db.query(tableName,
                        new String[]{"_id"},
                        "_id = ?", new String[] {Integer.toString(id)}, null, null, null);
                if(cursor.moveToFirst()) ok = true;
            } catch (SQLiteException e) {
                Log.e(TAG, "не получилось прочесть данные из таблицы " + tableName);
            } finally {
                cwdbHelper.close();
                if(cursor != null) cursor.close();
                if(db != null) db.close();
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
                resultValue = Float.parseFloat(context.getResultValue().getText().toString());
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

    public void setContext(FillNewData_Activity context) {
        this.context = context;
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

    public String convertDateToString(Calendar calendar) {
        String dayOfMonthStr, monthStr, yearStr;
        int month;
        if (calendar.get(Calendar.DAY_OF_MONTH) < 10) dayOfMonthStr = "0" + calendar.get(Calendar.DAY_OF_MONTH);
        else dayOfMonthStr = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        month = calendar.get(Calendar.MONTH) + 1;
        if (month < 10) monthStr = "0" + (month);
        else monthStr = Integer.toString(month);
        yearStr = Integer.toString(calendar.get(Calendar.YEAR));
        return dayOfMonthStr + "." + monthStr + "." + yearStr;
    }

    public boolean isFirstLaunch() { return firstLaunch; }

    public void setFirstLaunchFalse() {
        firstLaunch = false;
    }

    public void setDateForSql(String s) {
        String day = s.substring(0, 2);
        String month = s.substring(3, 5);
        String year = s.substring(6);
        dateForSql = year + "-" + month + "-" + day;
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
            context.getAddButton().setText("Идёт процесс добавления данных");
            addResultData();
        }
        else {
            if (!correctEmployerData) context.changeEmployerEditTextAttributes();
            if (!correctFirmData) context.changeFirmEditTextAttributes();
            if (!correctToWData) context.changeToWEditTextAttributes();
            if (!correctPoWData) context.changePoWEditTextAttributes();
            if (!correctResultValueData) context.changeResultEditTextAttributes();
            context.prepareActivity(false);
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
        context.prepareActivity(true);
        String s;
        if (res) s = "Данные успешно добавлены";
        else s = "Не получилось добавить данные. Сообщи об этом разработчику";
        Toast toast = Toast.makeText(getApplication(), s, Toast.LENGTH_SHORT);
        toast.show();
    }

}
