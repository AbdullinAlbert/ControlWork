package com.albertabdullin.controlwork.workManager.creating_list_of_report_data_tool;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.db_of_app.CWDBHelper;
import com.albertabdullin.controlwork.models.ComplexEntityForDB;
import com.albertabdullin.controlwork.models.ResultTypeInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.albertabdullin.controlwork.workManager.creating_list_of_report_data_tool.PDFReportCreator.heightBetweenNameAndPeriod;
import static com.albertabdullin.controlwork.workManager.creating_list_of_report_data_tool.PDFReportCreator.heightBetweenReportDescriptionAndTable;
import static com.albertabdullin.controlwork.workManager.creating_list_of_report_data_tool.PDFReportCreator.heightOfTableHeader;
import static com.albertabdullin.controlwork.workManager.creating_list_of_report_data_tool.PDFReportCreator.heightOfTableRow;
import static com.albertabdullin.controlwork.workManager.creating_list_of_report_data_tool.PDFReportCreator.pageHeight;
import static com.albertabdullin.controlwork.workManager.creating_list_of_report_data_tool.PDFReportCreator.reportDescriptionPaddingY;
import static com.albertabdullin.controlwork.workManager.creating_list_of_report_data_tool.PDFReportCreator.tableBottomPaddingY;
import static com.albertabdullin.controlwork.workManager.creating_list_of_report_data_tool.PDFReportCreator.textSizeForReportDescription;

public class ReportDataCreator {

    private final Context mContext;
    private final String mQuery;
    private List<ComplexEntityForDB> mResultList;
    private int employeeID = -1;
    private int typeOfWorkID = -1;
    private int placeOfWorkID = -1;
    private int resultTypeID = -1;
    private String stringViewOfResultType = "";
    private float resultSumOfToW = 0;
    private final Map<ResultTypeInfo, Float> totalResultOfResultType = new LinkedHashMap<>();
    private final Map<ResultTypeInfo, Float> commonResultOfResultType = new LinkedHashMap<>();
    private final Map<ResultTypeInfo, Float> resultTypeOfPoW = new LinkedHashMap<>();
    private final Map<ResultTypeInfo, Float> totalResultOfToW = new LinkedHashMap<>();

    public ReportDataCreator(Context context, String query) {
        mContext = context;
        mQuery = query;
    }

    private void setID(ComplexEntityForDB entityForDB) {
        employeeID = entityForDB.getEmployerID();
        typeOfWorkID = entityForDB.getTypeOfWorkID();
        placeOfWorkID = entityForDB.getPlaceOfWorkID();
        resultTypeID = entityForDB.getResultTypeID();
        stringViewOfResultType = entityForDB.getStringViewOfResultType();
    }

    private void addResultTypeToCommonResultOfResultType() {
        commonResultOfResultType.put(new ResultTypeInfo(resultTypeID, stringViewOfResultType), resultSumOfToW);
    }

    private void transferDataFromPoWResultTypeToToWResultType(ComplexEntityForDB entityForDB) {
        transferDataToCommonResultType(commonResultOfResultType, resultTypeOfPoW);
        addResRows(resultTypeOfPoW, entityForDB, mContext.getString(R.string.common_sum_on_place_of_work));
        transferDataToCommonResultType(resultTypeOfPoW, totalResultOfToW);
    }

    private void transferDataToCommonResultType(Map<ResultTypeInfo, Float> fromTotalResultType, Map<ResultTypeInfo, Float> toTotalResultType) {
        for (Map.Entry<ResultTypeInfo, Float> outerEntry: fromTotalResultType.entrySet()) {
            boolean isNeedAdd = true;
            for (Map.Entry<ResultTypeInfo, Float> innerEntry: toTotalResultType.entrySet()) {
                if (outerEntry.getKey().getResultTypeID() == innerEntry.getKey().getResultTypeID()) {
                    float sum = outerEntry.getValue() + innerEntry.getValue();
                    toTotalResultType.put(innerEntry.getKey(), sum);
                    isNeedAdd = false;
                }
            }
            if (isNeedAdd) toTotalResultType.put(outerEntry.getKey(), outerEntry.getValue());
        }
        fromTotalResultType.clear();
    }

    private void addResRows(Map<ResultTypeInfo, Float> fromTotalResultType, ComplexEntityForDB entityForDB, String resDescription) {
        for (Map.Entry<ResultTypeInfo, Float> outerEntry : fromTotalResultType.entrySet()) {
            ComplexEntityForDB helperEntity = new ComplexEntityForDB();
            helperEntity.setEmployerID(entityForDB.getEmployerID());
            helperEntity.setResultEntity(true);
            helperEntity.setTypeOfWorkID(entityForDB.getTypeOfWorkID());
            helperEntity.setTOWDescription(entityForDB.getTOWDescription());
            helperEntity.setPOWDescription(entityForDB.getPOWDescription());
            helperEntity.setTypeResultSum(outerEntry.getValue());
            helperEntity.setStringViewOfResultType(outerEntry.getKey().getStringViewOfRT());
            helperEntity.setResultDescription(resDescription);
            mResultList.add(helperEntity);
        }
    }

    private void addToWRow(ComplexEntityForDB eDB) {
        ComplexEntityForDB helperEntity = new ComplexEntityForDB();
        helperEntity.setTypeOfWorkID(eDB.getTypeOfWorkID());
        helperEntity.setEmployerID(eDB.getEmployerID());
        helperEntity.setEmployerDescription(eDB.getEmployerDescription());
        helperEntity.setTypeOfWorkEntity(true);
        helperEntity.setTOWDescription(eDB.getTOWDescription());
        mResultList.add(helperEntity);
    }
    public List<ComplexEntityForDB> getResultList() {
        mResultList = new ArrayList<>();
        SQLiteOpenHelper cwdbHelper = new CWDBHelper(mContext);
        try (SQLiteDatabase db = cwdbHelper.getReadableDatabase();
             Cursor cursor = db.rawQuery(mQuery, null, null)) {
            if (cursor.moveToFirst()) {
                ComplexEntityForDB eDB;
                int countOfPoW = 0;
                int countOfToW = 0;
                boolean isNeedAdd;
                do {
                    byte b = 0;
                    eDB = new ComplexEntityForDB();
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
                    if (eDB.getEmployerID() == employeeID && eDB.getTypeOfWorkID() == typeOfWorkID
                            && eDB.getPlaceOfWorkID() == placeOfWorkID && eDB.getResultTypeID() == resultTypeID || mResultList.size() == 0) {
                        if (mResultList.size() == 0) addToWRow(eDB);
                        resultSumOfToW += cursor.getFloat(10);
                    } else if (eDB.getEmployerID() == employeeID && eDB.getTypeOfWorkID() == typeOfWorkID && eDB.getPlaceOfWorkID() == placeOfWorkID) {
                        addResultTypeToCommonResultOfResultType();
                        resultSumOfToW = cursor.getFloat(10);
                    } else if (eDB.getEmployerID() == employeeID && eDB.getTypeOfWorkID() == typeOfWorkID) {
                        addResultTypeToCommonResultOfResultType();
                        transferDataToCommonResultType(commonResultOfResultType, resultTypeOfPoW);
                        addResRows(resultTypeOfPoW, mResultList.get(mResultList.size() -1), mContext.getString(R.string.common_sum_on_place_of_work));
                        transferDataToCommonResultType(resultTypeOfPoW, totalResultOfToW);
                        resultSumOfToW = cursor.getFloat(10);
                        countOfPoW++;
                    } else if (eDB.getEmployerID() == employeeID) {
                        addResultTypeToCommonResultOfResultType();
                        if (countOfPoW > 0) {
                            transferDataFromPoWResultTypeToToWResultType(mResultList.get(mResultList.size() - 1));
                            countOfPoW = 0;
                        } else transferDataToCommonResultType(commonResultOfResultType, totalResultOfToW);
                        addResRows(totalResultOfToW, mResultList.get(mResultList.size() -1), mContext.getString(R.string.common_sum_on_type_of_work));
                        transferDataToCommonResultType(totalResultOfToW, totalResultOfResultType);
                        addToWRow(eDB);
                        resultSumOfToW = cursor.getFloat(10);
                        countOfToW++;
                    } else {
                        addResultTypeToCommonResultOfResultType();
                        if (countOfPoW > 0) transferDataFromPoWResultTypeToToWResultType(mResultList.get(mResultList.size() - 1));
                        else transferDataToCommonResultType(commonResultOfResultType, totalResultOfToW);
                        if (countOfToW > 0) addResRows(totalResultOfToW, mResultList.get(mResultList.size() - 1), mContext.getString(R.string.common_sum_on_type_of_work));
                        transferDataToCommonResultType(totalResultOfToW, totalResultOfResultType);
                        addResRows(totalResultOfResultType, mResultList.get(mResultList.size() - 1), mContext.getString(R.string.total_sum_on_type_of_result_short));
                        countOfPoW = 0;
                        countOfToW = 0;
                        totalResultOfResultType.clear();
                        resultTypeOfPoW.clear();
                        totalResultOfToW.clear();
                        commonResultOfResultType.clear();
                        resultSumOfToW = cursor.getFloat(10);
                        addToWRow(eDB);
                    }
                    mResultList.add(eDB);
                    setID(eDB);
                } while (cursor.moveToNext());
                isNeedAdd = true;
                for (Map.Entry<ResultTypeInfo, Float> totalResultEntry : commonResultOfResultType.entrySet()) {
                    if (totalResultEntry.getKey().getResultTypeID() == resultTypeID) isNeedAdd = false;
                }
                if (isNeedAdd) addResultTypeToCommonResultOfResultType();
                if (countOfPoW > 0) transferDataFromPoWResultTypeToToWResultType(mResultList.get(mResultList.size() - 1));
                else transferDataToCommonResultType(commonResultOfResultType, totalResultOfToW);
                if (countOfToW > 0) addResRows(totalResultOfToW, mResultList.get(mResultList.size() - 1), mContext.getString(R.string.common_sum_on_type_of_work));
                transferDataToCommonResultType(totalResultOfToW, totalResultOfResultType);
                addResRows(totalResultOfResultType, mResultList.get(mResultList.size() - 1), mContext.getString(R.string.total_sum_on_type_of_result_short));
            }
        } catch (SQLiteException e) {
            throw new RuntimeException("Исключене при получении данных из БД: " + e.getMessage());
        }
        return mResultList;
    }

    //количество элементов в списке - количество сотрудников, для которых нужно создать отчёты.
    // Значение map - list. Через size() каждого List будем решать сколько страниц отчёта нужно
    //для каждого сотрудника
    public List<Integer> getPageCountList() {
        Map<Integer, List<ComplexEntityForDB>> employeeToResultWorkMap = new TreeMap<>(mResultList.stream()
                .collect(Collectors.groupingBy(ComplexEntityForDB::getEmployerID)));
        int availableHeightForFirstPageTable = pageHeight - (reportDescriptionPaddingY + textSizeForReportDescription * 2 + heightBetweenNameAndPeriod +
                heightBetweenReportDescriptionAndTable + heightOfTableHeader + tableBottomPaddingY);
        int availableHeightForSecondAndOtherPagesTable = pageHeight - (reportDescriptionPaddingY + heightOfTableHeader + tableBottomPaddingY);
        return employeeToResultWorkMap.values().stream()
                .map(complexEntityForDBS -> {
                    int neededScope = complexEntityForDBS.size() * heightOfTableRow;
                    if (neededScope <= availableHeightForFirstPageTable) return 1;
                    else {
                        int additionalPageCount = 1;
                        neededScope -= availableHeightForFirstPageTable;
                        do {
                            additionalPageCount++;
                            neededScope -= availableHeightForSecondAndOtherPagesTable;
                        } while (neededScope > availableHeightForSecondAndOtherPagesTable);
                        return additionalPageCount;
                    }
                }).collect(Collectors.toList());
    }

    public List<String> getListName() {
        ArrayList<String> mListName = new ArrayList<>();
        int currentID = -1;
        for (ComplexEntityForDB entity : mResultList) {
            if (entity.getEmployerID() != currentID) {
                mListName.add(entity.getEmployerDescription());
                currentID = entity.getEmployerID();
            }
        }
        return mListName;
    }

}
