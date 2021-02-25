package com.albertabdullin.controlwork.workManager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.albertabdullin.controlwork.db_of_app.CWDBHelper;
import com.albertabdullin.controlwork.models.ComplexEntityForDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CreateReportWorker extends Worker {

    public static String KEY_FOR_DATE_RANGES = "key for date ranges";
    public static String KEY_FOR_RESULT_LIST = "key for result list";
    public static String KEY_FOR_QUERY = "key for query";

    private final String TAG = CreateReportWorker.class.getSimpleName();

    private List<Integer> pageInfo;
    private String dateRanges;

    private final int reportDescriptionPaddingX = 30;
    private final int reportDescriptionPaddingY = 30;
    private final int textSizeForReportDescription = 20;
    private final int heightBetweenNameAndPeriod = 20;
    private final int heightBetweenReportDescriptionAndTable = 10;
    private final int tablePaddingX = 40;
    private final int tableBottomPaddingY = 65;
    private final int pageWidth = 594;
    private final int pageHeight = 846;
    private final int heightOfTableRow = 20;
    private final int heightOfTableHeader = heightOfTableRow * 2;
    private final int idColumnWeight = 30;
    private final int commonColumnWeight = 125;
    private final int dateAndResultColumnWeight = 62;
    private final int firstVerticalLine = tablePaddingX + idColumnWeight;
    private final int secondVerticalLine = firstVerticalLine + commonColumnWeight;
    private final int thirdVerticalLine = secondVerticalLine + commonColumnWeight;
    private final int fourthVerticalLine = thirdVerticalLine + dateAndResultColumnWeight;
    private final int fifthVerticalLine = fourthVerticalLine + dateAndResultColumnWeight;


    private List<ComplexEntityForDB> resultList;

    public CreateReportWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    private List<ComplexEntityForDB> getResultList(String mQuery) {
        resultList = new ArrayList<>();
        SQLiteOpenHelper cwdbHelper = new CWDBHelper(getApplicationContext());
        try (SQLiteDatabase db = cwdbHelper.getReadableDatabase();
             Cursor cursor = db.rawQuery(mQuery, null, null)) {
            if (cursor.moveToFirst()) {
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
                    eDB.setNote(cursor.getString(b));
                    resultList.add(eDB);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            throw new RuntimeException("Исключене при получении данных из БД: " + e.getMessage());
        }
        return resultList;
    }
    //количество элементов в списке - количество сотрудников, для которых нужно создать отчёты.
    // Значение индекса - list. Через size() каждого List будем решать сколько страниц отчёта нужно
    //для каждого сотрудника
    private void initPageInfo(Map<Integer, List<ComplexEntityForDB>> map) {
        pageInfo = new ArrayList<>();
        int availableHeightForFirstPageTable = pageHeight - (reportDescriptionPaddingY + textSizeForReportDescription * 2 + heightBetweenNameAndPeriod +
                heightBetweenReportDescriptionAndTable + heightOfTableHeader + tableBottomPaddingY);
        for (Map.Entry<Integer, List<ComplexEntityForDB>> entry : map.entrySet()) {
            if (availableHeightForFirstPageTable > entry.getValue().size() * heightOfTableRow) pageInfo.add(1);
            else {

            }
        }
    }

    @NonNull
    @Override
    public Result doWork() {
        resultList = (List<ComplexEntityForDB>) getInputData().getKeyValueMap().get(KEY_FOR_RESULT_LIST);
        if (resultList == null) {
            try {
                resultList = getResultList(getInputData().getString(KEY_FOR_QUERY));
            } catch (RuntimeException e) {
                Log.e(TAG, "failure when get data from db", e);
                return Result.failure();
            }
        }
        Map<Integer, List<ComplexEntityForDB>> helperMap = resultList.stream()
                .collect(Collectors.groupingBy(ComplexEntityForDB::getEmployerID));
        initPageInfo(helperMap);
        return Result.success();
    }

}
