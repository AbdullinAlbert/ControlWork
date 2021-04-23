package com.albertabdullin.controlwork.workManager.creating_list_of_report_data_tool;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;

import androidx.work.ListenableWorker;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.activities.ReportActivity;
import com.albertabdullin.controlwork.models.ComplexEntityForDB;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.stream.IntStream;

public class PDFReportCreator {

    private int currentYPos;
    public static int reportDescriptionPaddingY = 30;
    public static int textSizeForReportDescription = 12;
    public static final int heightBetweenNameAndPeriod = 20;
    public static final int heightBetweenReportDescriptionAndTable = 10;
    private final int tablePaddingX = 40;
    public static final int tableBottomPaddingY = 65;
    public static final int pageWidth = 594;
    public static final int pageHeight = 846;
    public static int heightOfTableRow = 20;
    public static final int heightOfTableHeader = heightOfTableRow * 2;
    private final int idColumnWidth = 30;
    private final int commonColumnWidth = 120;
    private final int dateAndResultColumnWidth = 62;
    private final int typeOfResultColumnWidth = 35;
    private final int firstVerticalLine = tablePaddingX + idColumnWidth;
    private final int secondVerticalLine = firstVerticalLine + commonColumnWidth;
    private final int thirdVerticalLine = secondVerticalLine + commonColumnWidth;
    private final int fourthVerticalLine = thirdVerticalLine + dateAndResultColumnWidth;
    private final int fifthVerticalLine = fourthVerticalLine + dateAndResultColumnWidth;
    private final int sixthVerticalLine = fifthVerticalLine + typeOfResultColumnWidth;

    private final Context mContext;
    private final List<ComplexEntityForDB> mResultList;
    private final List<Integer> mPageCountList;
    private Paint mPaintForReportHeaderText;
    private Paint mPaintForTableHeaderText;
    private Paint mPaintForTableRowText;
    private int mCurrentPosInResultList = 0;
    private final String mDateRanges;
    private boolean mNeededNextPage;
    private int rowNumber;
    private final boolean mHasPermission;
    private final int mPageCount;
    private final List<String> mListName;

    public PDFReportCreator(Context context, List<ComplexEntityForDB> resultList, String dateRanges,
                            boolean hasPermission, List<Integer> pageCountList, List<String> listName) {
        mContext = context;
        mResultList = resultList;
        mDateRanges = dateRanges;
        mHasPermission = hasPermission;
        mPageCountList = pageCountList;
        mPageCount = mPageCountList.stream().reduce(0, (sum, e) -> sum += e);
        mListName = listName;
        initPaintForReportHeader();
        initPaintForTableHeader();
        initPaintForTableRow();
    }

    private void writeReportHeader(Canvas canvas, String employeeName) {
        String stringForFirstLastNames = "ФИО сотрудника: " + employeeName;
        int reportDescriptionPaddingX = 30;
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

    private void drawTextToCellCenter(Canvas canvas, int beginXPos, int endXPos, int yPos, String text,
                                      Paint paintText, Rect boundRect) {
        paintText.getTextBounds(text, 0, text.length(), boundRect);
        int x = ((endXPos - beginXPos) / 2) - boundRect.centerX();
        canvas.drawText(text, beginXPos + x, yPos, paintText);
    }

    private void drawTableHeader(Canvas canvas, Paint paint) {
        if (currentYPos == 0) currentYPos += reportDescriptionPaddingY;
        Rect boundRect = new Rect();
        mPaintForTableHeaderText.getTextBounds(mContext.getString(R.string.number), 0, mContext.getString(R.string.number).length(), boundRect);
        int posYForText = ((heightOfTableHeader / 2) - boundRect.centerY()) + currentYPos;
        drawTextToCellCenter(canvas, tablePaddingX, firstVerticalLine, posYForText, mContext.getString(R.string.number), mPaintForTableHeaderText, boundRect);
        drawTextToCellCenter(canvas, firstVerticalLine, secondVerticalLine, posYForText, mContext.getString(R.string.firm_name), mPaintForTableHeaderText, boundRect);
        drawTextToCellCenter(canvas, secondVerticalLine, thirdVerticalLine, posYForText, mContext.getString(R.string.place_of_work), mPaintForTableHeaderText, boundRect);
        drawTextToCellCenter(canvas, thirdVerticalLine, fourthVerticalLine, posYForText, mContext.getString(R.string.date), mPaintForTableHeaderText, boundRect);
        drawTextToCellCenter(canvas, fourthVerticalLine, fifthVerticalLine, posYForText, mContext.getString(R.string.result), mPaintForTableHeaderText, boundRect);
        drawTextToCellCenter(canvas, fifthVerticalLine, sixthVerticalLine, posYForText, mContext.getString(R.string.type), mPaintForTableHeaderText, boundRect);
        drawTextToCellCenter(canvas, sixthVerticalLine, pageWidth - tablePaddingX, posYForText, mContext.getString(R.string.note), mPaintForTableHeaderText, boundRect);
        canvas.drawLine(tablePaddingX, currentYPos + heightOfTableHeader, pageWidth - tablePaddingX, currentYPos + heightOfTableHeader, paint);
        drawVerticalLines(canvas, currentYPos, currentYPos + heightOfTableHeader, paint);
    }

    private void drawVerticalLines(Canvas canvas, int yPosBegin, int yPosEnd, Paint tablePaint) {
        canvas.drawLine(firstVerticalLine, yPosBegin, firstVerticalLine, yPosEnd, tablePaint);
        canvas.drawLine(secondVerticalLine, yPosBegin, secondVerticalLine, yPosEnd, tablePaint);
        canvas.drawLine(thirdVerticalLine, yPosBegin, thirdVerticalLine, yPosEnd, tablePaint);
        canvas.drawLine(fourthVerticalLine, yPosBegin, fourthVerticalLine, yPosEnd, tablePaint);
        canvas.drawLine(fifthVerticalLine, yPosBegin, fifthVerticalLine, yPosEnd, tablePaint);
        canvas.drawLine(sixthVerticalLine, yPosBegin, sixthVerticalLine, yPosEnd, tablePaint);
    }

    private void drawRowOfResult(Canvas canvas, int posYForText, Rect boundRect, ComplexEntityForDB entityForDB, Paint tablePaint) {
        String totalSumOnTypeOfResultShort = mContext.getString(R.string.total_sum_on_type_of_result_short);
        String totalSumOnTypeOfResultFull = mContext.getString(R.string.total_sum_on_type_of_result_full);
        String result = entityForDB.getResultDescription().equals(totalSumOnTypeOfResultShort) ? totalSumOnTypeOfResultFull : entityForDB.getResultDescription();
        int tableRowPaddingX = 2;
        if (result.equals(mContext.getString(R.string.common_sum_on_place_of_work))) {
            result = result.substring(0, result.length() -1) + " " + entityForDB.getPOWDescription() + ": ";
        } else if (result.equals(mContext.getString(R.string.common_sum_on_type_of_work))) {
            result = result.substring(0, result.length() -1) + " " + entityForDB.getTOWDescription() + ": ";
        }
        canvas.drawText(result, fourthVerticalLine - mPaintForTableRowText.measureText(result) - tableRowPaddingX, posYForText, mPaintForTableRowText);
        drawTextToCellCenter(canvas, fourthVerticalLine, fifthVerticalLine, posYForText, entityForDB.getStringViewOfTypeResultSum(), mPaintForTableRowText, boundRect);
        drawTextToCellCenter(canvas, fifthVerticalLine, sixthVerticalLine, posYForText, entityForDB.getStringViewOfResultType(), mPaintForTableRowText, boundRect);
        canvas.drawLine(tablePaddingX, currentYPos, pageWidth - tablePaddingX, currentYPos, tablePaint);
    }

    private void drawTable(Canvas canvas, Paint tablePaint) {
        drawTableHeader(canvas, tablePaint);
        int beginTable = currentYPos;
        currentYPos += heightOfTableHeader;
        int employeeID = mResultList.get(mCurrentPosInResultList).getEmployerID();
        Rect boundRect = new Rect();
        mPaintForTableRowText.getTextBounds(mResultList.get(mCurrentPosInResultList).getTOWDescription(), 0, mResultList.get(mCurrentPosInResultList).getTOWDescription().length(), boundRect);
        int preliminaryPosYForText = (heightOfTableRow / 2) - boundRect.centerY();
        int currentYPosForDrawVerticalLine = currentYPos;
        int posYForText = preliminaryPosYForText + currentYPos;
        while (mCurrentPosInResultList < mResultList.size() && mResultList.get(mCurrentPosInResultList).getEmployerID() == employeeID) {
            if (mResultList.get(mCurrentPosInResultList).isTypeOfWorkEntity()) {
                canvas.drawText(mResultList.get(mCurrentPosInResultList).getTOWDescription(), tablePaddingX + ((float) idColumnWidth / 3), posYForText, mPaintForTableRowText);
                canvas.drawLine(tablePaddingX, currentYPos, pageWidth - tablePaddingX, currentYPos, tablePaint);
                currentYPos += heightOfTableRow;
                posYForText = preliminaryPosYForText + currentYPos;
                currentYPosForDrawVerticalLine = currentYPos;
            } else if (mResultList.get(mCurrentPosInResultList).isResultEntity()) {
                if (!mResultList.get(mCurrentPosInResultList - 1).isResultEntity())
                    drawVerticalLines(canvas, currentYPosForDrawVerticalLine, currentYPos, tablePaint);
                drawRowOfResult(canvas, posYForText, boundRect, mResultList.get(mCurrentPosInResultList), tablePaint);
                currentYPos += heightOfTableRow;
                posYForText = preliminaryPosYForText + currentYPos;
                currentYPosForDrawVerticalLine = currentYPos;
            } else {
                drawTextToCellCenter(canvas, tablePaddingX, firstVerticalLine, posYForText, Integer.toString(rowNumber++), mPaintForTableRowText, boundRect);
                drawTextToCellCenter(canvas, firstVerticalLine, secondVerticalLine, posYForText, mResultList.get(mCurrentPosInResultList).getFirmDescription(), mPaintForTableRowText, boundRect);
                drawTextToCellCenter(canvas, secondVerticalLine, thirdVerticalLine, posYForText, mResultList.get(mCurrentPosInResultList).getPOWDescription(), mPaintForTableRowText, boundRect);
                drawTextToCellCenter(canvas, thirdVerticalLine, fourthVerticalLine, posYForText, mResultList.get(mCurrentPosInResultList).getDate(), mPaintForTableRowText, boundRect);
                drawTextToCellCenter(canvas, fourthVerticalLine, fifthVerticalLine, posYForText, mResultList.get(mCurrentPosInResultList).getResult(), mPaintForTableRowText, boundRect);
                drawTextToCellCenter(canvas, fifthVerticalLine, sixthVerticalLine, posYForText, mResultList.get(mCurrentPosInResultList).getStringViewOfResultType(), mPaintForTableRowText, boundRect);
                drawTextToCellCenter(canvas, sixthVerticalLine, pageWidth - tablePaddingX, posYForText, mResultList.get(mCurrentPosInResultList).getNote(), mPaintForTableRowText, boundRect);
                canvas.drawLine(tablePaddingX, currentYPos, pageWidth - tablePaddingX, currentYPos, tablePaint);
                currentYPos += heightOfTableRow;
                posYForText = preliminaryPosYForText + currentYPos;
            }
            mCurrentPosInResultList++;
            if (currentYPos >= pageHeight - tableBottomPaddingY) break;
        }
        if (mCurrentPosInResultList == mResultList.size() || (mCurrentPosInResultList < mResultList.size() &&
                mResultList.get(mCurrentPosInResultList).getEmployerID() != employeeID)) setNeededNextPage(false);
        canvas.drawRect(tablePaddingX, beginTable, pageWidth - tablePaddingX, currentYPos, tablePaint);
    }

    public ListenableWorker.Result createPDFDocument() {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, mPageCount).create();
        Paint tablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tablePaint.setStyle(Paint.Style.STROKE);
        tablePaint.setColor(mContext.getColor(R.color.standardBlack));
        try {
            IntStream.range(0, mPageCountList.size())
                    .forEach(index -> {
                        setNeededNextPage(true);
                        rowNumber = 1;
                        IntStream.range(0, mPageCountList.get(index)).forEach(pageNumber -> {
                            if (isNextPageNeed()) {
                                currentYPos = 0;
                                PdfDocument.Page page = document.startPage(pageInfo);
                                if (pageNumber == 0)
                                    writeReportHeader(page.getCanvas(), mListName.get(index));
                                drawTable(page.getCanvas(), tablePaint);
                                document.finishPage(page);
                            }
                        });
                    });
        } catch (Exception e) {
            Looper.prepare();
            ReportActivity.handler.post(() ->
                    Toast.makeText(mContext, mContext.getString(R.string.creating_file_error) + e.getMessage(), Toast.LENGTH_SHORT).show());
            return ListenableWorker.Result.failure();
        }
        FileOutputStream fos;
        String fileName = Calendar.getInstance().getTimeInMillis() + ".pdf";
        try {
            File output;
            if (mHasPermission) output = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            else output = new File(mContext.getExternalFilesDir(""), fileName);
            fos = new FileOutputStream(output);
        } catch (FileNotFoundException e) {
            Looper.prepare();
            ReportActivity.handler.post(() ->
                    Toast.makeText(mContext, mContext.getString(R.string.creating_file_error) + e.getMessage(), Toast.LENGTH_SHORT).show());
            return ListenableWorker.Result.failure();
        }
        try {
            document.writeTo(fos);
        } catch (IOException e) {
            Looper.prepare();
            ReportActivity.handler.post(() ->
                    Toast.makeText(mContext, mContext.getString(R.string.writing_file_error) + e.getMessage(), Toast.LENGTH_SHORT).show());
            return ListenableWorker.Result.failure();
        }
        document.close();
        return ListenableWorker.Result.success();
    }


    private void initPaintForTableHeader() {
        mPaintForTableHeaderText = new Paint(mPaintForReportHeaderText);
        int textSizeForTableHeaderText = 11;
        mPaintForTableHeaderText.setTextSize(textSizeForTableHeaderText);
        mPaintForTableHeaderText.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/robotobold.ttf"));
    }

    private void initPaintForTableRow() {
        mPaintForTableRowText = new Paint(mPaintForReportHeaderText);
        int textSizeForTableRow = 10;
        mPaintForTableRowText.setTextSize(textSizeForTableRow);
    }

    private void initPaintForReportHeader() {
        mPaintForReportHeaderText = new Paint();
        mPaintForReportHeaderText.setTextSize(textSizeForReportDescription);
        mPaintForReportHeaderText.setColor(mContext.getResources().getColor(R.color.standardBlack, null));
        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/robotoregular.ttf");
        mPaintForReportHeaderText.setTypeface(font);
    }

}


