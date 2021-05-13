package com.albertabdullin.controlwork.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.fragments.PickerSignsDF;
import com.albertabdullin.controlwork.fragments.SearchCriteriaForReportFragment;
import com.albertabdullin.controlwork.fragments.SearchCriteriaFragment;
import com.albertabdullin.controlwork.models.ComplexEntityForDB;
import com.albertabdullin.controlwork.workManager.CreateReportWorker;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Calendar.DAY_OF_MONTH;

public class MakerSearchCriteriaReportVM extends MakerSearchCriteriaVM {

    private final WorkManager mWorkManager;

    private boolean mHasAppPermission = false;

    public MakerSearchCriteriaReportVM(@NonNull Application application) {
        super(application);
        mWorkManager = WorkManager.getInstance(application);
        selectedEqualSignForDate = "\u2a7e" + " " + "\u2a7d";
        notifyAboutTapAddButton(SearchCriteriaFragment.DATES_VALUE, PickerSignsDF.ADD_ITEM, -1);
        Calendar calendar = Calendar.getInstance();
        String stringDate1 = getBeginOfMonth(calendar);
        String stringDate2 = getEndOfMonth(calendar);
        addItemToDateList(selectedEqualSignForDate, stringDate1, stringDate2);
        Long longDate1 = getLongDateBegin(calendar);
        Long longDate2 = getLongDateEnd(calendar);
        addSearchCriteria(SearchCriteriaFragment.DATES_VALUE, null, longDate1, longDate2);
        setSelectedSignAndStringViewOfDate(selectedEqualSignForDate, createStringViewOfDate(selectedEqualSignForDate));
    }

    private Long getLongDateBegin(Calendar calendar) {
        calendar.set(DAY_OF_MONTH, 1);
        long fractionalPart = (calendar.getTimeInMillis()) % (86400L);
        return (calendar.getTimeInMillis() - fractionalPart);
    }

    private long getLongDateEnd(Calendar calendar) {
        calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(DAY_OF_MONTH));
        long fractionalPart = (calendar.getTimeInMillis()) % (86400L);
        return (calendar.getTimeInMillis() - fractionalPart);
    }

    private String getMonthAndYear(Calendar calendar) {
        String monthAndYear = (calendar.get(Calendar.MONTH) + 1) + ".";
        if (monthAndYear.length() == 2) monthAndYear = "0" + monthAndYear;
        monthAndYear += calendar.get(Calendar.YEAR);
        return monthAndYear;
    }

    private String getBeginOfMonth(Calendar calendar) {
        String mStringDateBegin = calendar.getActualMinimum(DAY_OF_MONTH) + ".";
        if (mStringDateBegin.length() == 2) mStringDateBegin = "0" + mStringDateBegin;
        mStringDateBegin += getMonthAndYear(calendar);
        return mStringDateBegin;
    }

    private String getEndOfMonth(Calendar calendar) {
        String mStringDateBegin = calendar.getActualMaximum(DAY_OF_MONTH) + ".";
        if (mStringDateBegin.length() == 2) mStringDateBegin = "0" + mStringDateBegin;
        mStringDateBegin += getMonthAndYear(calendar);
        return mStringDateBegin;
    }

    @Override
    protected String getOrderBy() {
        return " " + getApplication().getString(R.string.order_by_for_report);
    }

    private String getDateRangesString() {
        StringBuilder sb = new StringBuilder();
        if (searchCriteriaForDate == null) return "";
        if (searchCriteriaForDate.containsKey("="))
            sb.append(createStringViewOfDate("=")).append(", ");
        if (searchCriteriaForDate.containsKey("\u2a7e" + " " + "\u2a7d"))
            sb.append(createStringViewOfDate("\u2a7e" + " " + "\u2a7d")).append(", ");
        if (searchCriteriaForDate.containsKey("\u2260"))
            sb.append("\u2260").append(": [").append(createStringViewOfDate("\u2260")).append("], ");
        if (searchCriteriaForDate.containsKey("\u2a7e"))
            sb.append("\u2a7e").append(" ").append(stringViewOfDateMoreSignLD.getValue()).append(", ");
        if (searchCriteriaForDate.containsKey("\u2a7d"))
            sb.append("\u2a7d").append(" ").append(stringViewOfDateLessSignLD.getValue()).append(", ");
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }

    public void setHasAppPermission(boolean b) {
        mHasAppPermission = b;
    }

    private Data getDataForWorker() {
        Data.Builder builder = new Data.Builder();
        return builder.putString(CreateReportWorker.KEY_FOR_DATE_RANGES, getDateRangesString())
            .putBoolean(CreateReportWorker.KEY_FOR_PERMISSION, mHasAppPermission)
            .putString(CreateReportWorker.KEY_FOR_QUERY, getQueryForSearch()).build();
    }

    public void launchCreatingReport() {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(CreateReportWorker.class)
                .setInputData(getDataForWorker())
                .build();
        mWorkManager.enqueue(workRequest);
    }

}
