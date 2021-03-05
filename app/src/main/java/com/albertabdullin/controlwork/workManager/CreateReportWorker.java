package com.albertabdullin.controlwork.workManager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.activities.ReportActivity;
import com.albertabdullin.controlwork.db_of_app.CWDBHelper;
import com.albertabdullin.controlwork.models.ComplexEntityForDB;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CreateReportWorker extends Worker {

    public static String KEY_FOR_DATE_RANGES = "key for date ranges";
    public static String KEY_FOR_RESULT_LIST = "key for result list";
    public static String KEY_FOR_QUERY = "key for query";
    public static String KEY_FOR_PERMISSION = "key for permission";

    private final String TAG = CreateReportWorker.class.getSimpleName();

    private int currentYPos;
    private final int reportDescriptionPaddingX = 30;
    private final int reportDescriptionPaddingY = 30;
    private final int textSizeForReportDescription = 12;
    private final int textSizeForTableHeaderText = 11;
    private final int textSizeForTableRow = 10;
    private final int heightBetweenNameAndPeriod = 20;
    private final int heightBetweenReportDescriptionAndTable = 10;
    private final int tablePaddingX = 40;
    private final int tableRowPaddingX = 2;
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

    private List<Integer> mPageCountList;
    private int mPageCount;
    private String mDateRanges;
    private List<ComplexEntityForDB> mResultList;
    private Paint mPaintForReportHeaderText;
    private Paint mPaintForTableHeaderText;
    private Paint mPaintForTableRowText;
    private List<String> mListName;
    private int mCurrentPosInResultList = 0;
    private int rowNumber;
    private float resultSum = 0;
    private boolean mNeededNextPage;
    private boolean mHasPermission;

    public CreateReportWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    private List<ComplexEntityForDB> getResultList(String mQuery) {
        mResultList = new ArrayList<>();
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
                   mResultList.add(eDB);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            throw new RuntimeException("Исключене при получении данных из БД: " + e.getMessage());
        }
        return mResultList;
    }
    //количество элементов в списке - количество сотрудников, для которых нужно создать отчёты.
    // Значение map - list. Через size() каждого List будем решать сколько страниц отчёта нужно
    //для каждого сотрудника
    private void initPageCountList(Map<Integer, List<ComplexEntityForDB>> map) {
        int availableHeightForFirstPageTable = pageHeight - (reportDescriptionPaddingY + textSizeForReportDescription * 2 + heightBetweenNameAndPeriod +
                heightBetweenReportDescriptionAndTable + heightOfTableHeader + tableBottomPaddingY);
        int availableHeightForSecondAndOtherPagesTable = pageHeight - (reportDescriptionPaddingY + heightOfTableHeader + tableBottomPaddingY);
        mPageCountList = map.values().stream()
                .map(complexEntityForDBS -> {
                    int countOfDifferentToW = (int) complexEntityForDBS.stream()
                            .mapToInt(ComplexEntityForDB::getTypeOfWorkID)
                            .distinct()
                            .count();
                    int neededScope = (complexEntityForDBS.size()  + countOfDifferentToW * 2) * heightOfTableRow;
                    if (neededScope <= availableHeightForFirstPageTable) return 1;
                    else {
                        int additionalPageCount = 1;
                        neededScope -= availableHeightForFirstPageTable;
                        do {
                            additionalPageCount++;
                            neededScope -= availableHeightForSecondAndOtherPagesTable + heightOfTableRow;
                        } while (neededScope > availableHeightForSecondAndOtherPagesTable);
                        return additionalPageCount;
                    }
                }).collect(Collectors.toList());
    }

    private void writeReportHeader(Canvas canvas, String employeeName) {
        String stringForFirstLastNames = "ФИО сотрудника: " + employeeName;
        canvas.drawText(stringForFirstLastNames, reportDescriptionPaddingX, currentYPos += reportDescriptionPaddingY, mPaintForReportHeaderText);
        String stringForDate = "Отчетный период: " + mDateRanges;
        canvas.drawText(stringForDate, reportDescriptionPaddingX, currentYPos += heightBetweenNameAndPeriod, mPaintForReportHeaderText);
        currentYPos += heightBetweenReportDescriptionAndTable;
    }

    private void setNeededNextPage(boolean b) {
        mNeededNextPage = b;
    }

    private boolean isNextPageNeed() {
        return mNeededNextPage;
    }

    private void addTextToCellCenter(Canvas canvas, int beginXPos, int endXPos, int yPos, String text,
                                     Paint paintText, Rect boundRect) {
        paintText.getTextBounds(text, 0, text.length(), boundRect);
        int x = ((endXPos - beginXPos) / 2) - boundRect.centerX();
        canvas.drawText(text, beginXPos + x, yPos, paintText);
    }

    private void drawTableHeader(Canvas canvas, Paint paint) {
        if (currentYPos == 0) currentYPos += reportDescriptionPaddingY;
        Rect boundRect = new Rect();
        mPaintForTableHeaderText.getTextBounds(getApplicationContext().getString(R.string.number), 0, getApplicationContext().getString(R.string.number).length(), boundRect);
        int posYForText = ((heightOfTableHeader / 2) - boundRect.centerY()) + currentYPos;
        addTextToCellCenter(canvas, tablePaddingX, firstVerticalLine, posYForText, getApplicationContext().getString(R.string.number), mPaintForTableHeaderText, boundRect);
        addTextToCellCenter(canvas, firstVerticalLine, secondVerticalLine, posYForText, getApplicationContext().getString(R.string.firm_name), mPaintForTableHeaderText, boundRect);
        addTextToCellCenter(canvas, secondVerticalLine, thirdVerticalLine, posYForText, getApplicationContext().getString(R.string.place_of_work), mPaintForTableHeaderText, boundRect);
        addTextToCellCenter(canvas, thirdVerticalLine, fourthVerticalLine, posYForText, getApplicationContext().getString(R.string.date), mPaintForTableHeaderText, boundRect);
        addTextToCellCenter(canvas, fourthVerticalLine, fifthVerticalLine, posYForText, getApplicationContext().getString(R.string.result), mPaintForTableHeaderText, boundRect);
        addTextToCellCenter(canvas, fifthVerticalLine, pageWidth - tablePaddingX, posYForText, getApplicationContext().getString(R.string.note), mPaintForTableHeaderText, boundRect);
        canvas.drawLine(tablePaddingX, currentYPos + heightOfTableHeader, pageWidth - tablePaddingX, currentYPos + heightOfTableHeader, paint);
        drawVerticalLines(canvas, currentYPos, currentYPos + heightOfTableHeader, paint);
    }

    private void drawVerticalLines(Canvas canvas, int yPosBegin, int yPosEnd, Paint tablePaint) {
        canvas.drawLine(firstVerticalLine, yPosBegin, firstVerticalLine, yPosEnd, tablePaint);
        canvas.drawLine(secondVerticalLine, yPosBegin, secondVerticalLine, yPosEnd, tablePaint);
        canvas.drawLine(thirdVerticalLine, yPosBegin, thirdVerticalLine, yPosEnd, tablePaint);
        canvas.drawLine(fourthVerticalLine, yPosBegin, fourthVerticalLine, yPosEnd, tablePaint);
        canvas.drawLine(fifthVerticalLine, yPosBegin, fifthVerticalLine, yPosEnd, tablePaint);
    }

    private void addRowOfResult(Canvas canvas, int yPosBegin, int posYForText, float resultSum, Rect boundRect) {
        String result = getApplicationContext().getString(R.string.result_with_colon);
        canvas.drawText(result, fourthVerticalLine - mPaintForTableRowText.measureText(result) - tableRowPaddingX, posYForText, mPaintForTableRowText);
        addTextToCellCenter(canvas, fourthVerticalLine, fifthVerticalLine, posYForText, Float.toString(resultSum), mPaintForTableRowText, boundRect);
    }

    private void drawTable(Canvas canvas, Paint tablePaint) {
        drawTableHeader(canvas, tablePaint);
        int beginTable = currentYPos;
        currentYPos += heightOfTableHeader;
        int employeeID = mResultList.get(mCurrentPosInResultList).getEmployerID();
        int towID = mResultList.get(mCurrentPosInResultList).getTypeOfWorkID();
        boolean isNeedResRow = true;
        Rect boundRect = new Rect();
        mPaintForTableRowText.getTextBounds(mResultList.get(mCurrentPosInResultList).getFirmDescription(), 0, mResultList.get(mCurrentPosInResultList).getFirmDescription().length(), boundRect);
        int preliminaryPosYForText = (heightOfTableRow / 2) - boundRect.centerY();
        int posYForText = preliminaryPosYForText + currentYPos;
        canvas.drawText(mResultList.get(mCurrentPosInResultList).getTOWDescription(), tablePaddingX + ((float)idColumnWeight / 3), posYForText, mPaintForTableRowText);
        int currentYPosForDrawVerticalLine = (currentYPos += heightOfTableRow);
        canvas.drawLine(tablePaddingX, currentYPos, pageWidth - tablePaddingX, currentYPos, tablePaint);
        posYForText = preliminaryPosYForText + currentYPos;
        while (mCurrentPosInResultList < mResultList.size()) {
            if (employeeID == mResultList.get(mCurrentPosInResultList).getEmployerID() && towID == mResultList.get(mCurrentPosInResultList).getTypeOfWorkID()) {
                addTextToCellCenter(canvas, tablePaddingX, firstVerticalLine, posYForText, Integer.toString(rowNumber++), mPaintForTableRowText, boundRect);
                addTextToCellCenter(canvas, firstVerticalLine, secondVerticalLine, posYForText, mResultList.get(mCurrentPosInResultList).getFirmDescription(), mPaintForTableRowText, boundRect);
                addTextToCellCenter(canvas, secondVerticalLine, thirdVerticalLine, posYForText, mResultList.get(mCurrentPosInResultList).getPOWDescription(), mPaintForTableRowText, boundRect);
                addTextToCellCenter(canvas, thirdVerticalLine, fourthVerticalLine, posYForText, mResultList.get(mCurrentPosInResultList).getDate(), mPaintForTableRowText, boundRect);
                addTextToCellCenter(canvas, fourthVerticalLine, fifthVerticalLine, posYForText, mResultList.get(mCurrentPosInResultList).getResult(), mPaintForTableRowText, boundRect);
                addTextToCellCenter(canvas, fifthVerticalLine, pageWidth - tablePaddingX, posYForText, mResultList.get(mCurrentPosInResultList).getNote(), mPaintForTableRowText, boundRect);
                resultSum += Float.parseFloat(mResultList.get(mCurrentPosInResultList).getResult());
                currentYPos += heightOfTableRow;
                canvas.drawLine(tablePaddingX, currentYPos , pageWidth - tablePaddingX, currentYPos, tablePaint);
                posYForText = preliminaryPosYForText + currentYPos;
                if (currentYPos >= pageHeight - tableBottomPaddingY) {
                    if (mCurrentPosInResultList == mResultList.size() - 1 ||
                            (mResultList.get(mCurrentPosInResultList + 1).getEmployerID() != employeeID && mCurrentPosInResultList != mResultList.size() - 1 )) setNeededNextPage(false);
                    else if (mResultList.get(mCurrentPosInResultList + 1).getEmployerID() == employeeID && towID == mResultList.get(mCurrentPosInResultList + 1).getTypeOfWorkID()) isNeedResRow = false;
                    mCurrentPosInResultList++;
                    break;
                }
                mCurrentPosInResultList++;
            } else if (employeeID == mResultList.get(mCurrentPosInResultList).getEmployerID() && towID != mResultList.get(mCurrentPosInResultList).getTypeOfWorkID()) {
                drawVerticalLines(canvas, currentYPosForDrawVerticalLine, currentYPos, tablePaint);
                addRowOfResult(canvas, currentYPosForDrawVerticalLine, posYForText, resultSum, boundRect);
                currentYPos += heightOfTableRow;
                posYForText = preliminaryPosYForText + currentYPos;
                towID = mResultList.get(mCurrentPosInResultList).getTypeOfWorkID();
                resultSum = 0;
                canvas.drawLine(tablePaddingX, currentYPos, pageWidth - tablePaddingX, currentYPos, tablePaint);
                canvas.drawText(mResultList.get(mCurrentPosInResultList).getTOWDescription(), tablePaddingX + ((float)idColumnWeight / 3), posYForText, mPaintForTableRowText);
                currentYPos += heightOfTableRow;
                canvas.drawLine(tablePaddingX, currentYPos, pageWidth - tablePaddingX, currentYPos, tablePaint);
                currentYPosForDrawVerticalLine = currentYPos;
                posYForText = preliminaryPosYForText + currentYPos;
            } else break;
        }
        drawVerticalLines(canvas, currentYPosForDrawVerticalLine, currentYPos, tablePaint);
        if (isNeedResRow) {
            addRowOfResult(canvas, currentYPosForDrawVerticalLine, posYForText, resultSum, boundRect);
            resultSum = 0;
            currentYPos += heightOfTableRow;
        }
        canvas.drawRect(tablePaddingX, beginTable, pageWidth - tablePaddingX, currentYPos, tablePaint);
    }

    private Result createPDFDocument() {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, mPageCount).create();
        Paint tablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tablePaint.setStyle(Paint.Style.STROKE);
        tablePaint.setColor(getApplicationContext().getColor(R.color.standardBlack));
        IntStream.range(0, mPageCountList.size())
                .forEach(index -> {
                    CreateReportWorker.this.setNeededNextPage(true);
                    rowNumber = 1;
                    IntStream.range(0, mPageCountList.get(index)).forEach(pageNumber -> {
                        if (CreateReportWorker.this.isNextPageNeed()) {
                            currentYPos = 0;
                            PdfDocument.Page page = document.startPage(pageInfo);
                            if (pageNumber == 0)
                                writeReportHeader(page.getCanvas(), mListName.get(index));
                            drawTable(page.getCanvas(), tablePaint);
                            document.finishPage(page);
                        }
                    });
                });
        FileOutputStream fos;
        String fileName = Calendar.getInstance().getTimeInMillis() + ".pdf";
        try {
            File output;
            if (mHasPermission) output = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            else output = new File(getApplicationContext().getExternalFilesDir(""), fileName);
            fos = new FileOutputStream(output);
        } catch (FileNotFoundException e) {
            ReportActivity.handler.getLooper().prepare();
            ReportActivity.handler.post(() ->
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.creating_file_error) + e.getMessage(), Toast.LENGTH_SHORT).show());
            return Result.failure();
        }
        try {
            document.writeTo(fos);
        } catch (IOException e) {
            ReportActivity.handler.getLooper().prepare();
            ReportActivity.handler.post(() ->
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.writing_file_error) + e.getMessage(), Toast.LENGTH_SHORT).show());
            return Result.failure();
        }
        document.close();
        return Result.success();
    }

    private void initPaintForReportHeader() {
        mPaintForReportHeaderText = new Paint();
        mPaintForReportHeaderText.setTextSize(textSizeForReportDescription);
        mPaintForReportHeaderText.setColor(getApplicationContext().getResources().getColor(R.color.standardBlack, null));
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/robotoregular.ttf");
        mPaintForReportHeaderText.setTypeface(font);
    }

    private void initListName() {
        mListName = new ArrayList<>();
        int currentID = -1;
        for (ComplexEntityForDB entity : mResultList) {
            if (entity.getEmployerID() != currentID) {
                mListName.add(entity.getEmployerDescription());
                currentID = entity.getEmployerID();
            }
        }
    }

    private void initPaintForTableHeader() {
        mPaintForTableHeaderText = new Paint(mPaintForReportHeaderText);
        mPaintForTableHeaderText.setTextSize(textSizeForTableHeaderText);
        mPaintForTableHeaderText.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/robotobold.ttf"));
    }

    private void initPaintForTableRow() {
        mPaintForTableRowText = new Paint(mPaintForReportHeaderText);
        mPaintForTableRowText.setTextSize(textSizeForTableRow);
    }



    @NonNull
    @Override
    public Result doWork() {
        mDateRanges = getInputData().getString(KEY_FOR_DATE_RANGES);
        mHasPermission = getInputData().getBoolean(KEY_FOR_PERMISSION, false);
        mResultList = (List<ComplexEntityForDB>) getInputData().getKeyValueMap().get(KEY_FOR_RESULT_LIST);
        if (mResultList == null) {
            try {
                mResultList = getResultList(getInputData().getString(KEY_FOR_QUERY));
            } catch (RuntimeException e) {
                Log.e(TAG, "failure when get data from db", e);
                Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.fail_attempt_about_delete_data_from_db) + ": "
                + e.getMessage(), Toast.LENGTH_SHORT).show();
                return Result.failure();
            }
        }
        Map<Integer, List<ComplexEntityForDB>> employeeToResultWorkMap = new TreeMap<>(mResultList.stream()
                .collect(Collectors.groupingBy(ComplexEntityForDB::getEmployerID)));
        initPageCountList(employeeToResultWorkMap);
        mPageCount = mPageCountList.stream().reduce(0, (sum, e) -> sum += e);
        initListName();
        initPaintForReportHeader();
        initPaintForTableHeader();
        initPaintForTableRow();
        if (createPDFDocument().equals(Result.success())) {
            ReportActivity.handler.post(() -> {
                Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.the_report_has_been_created_successfully), Toast.LENGTH_LONG).show();
            });
            return Result.success();
        } else {
            ReportActivity.handler.post(() ->
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.the_report_has_not_been_created), Toast.LENGTH_LONG).show());
            return Result.failure();
        }
    }

}
