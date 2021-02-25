package com.albertabdullin.controlwork.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.models.ComplexEntityForDB;
import com.albertabdullin.controlwork.workManager.CreateReportWorker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MakerSearchCriteriaReportVM extends MakerSearchCriteriaVM {

    private final WorkManager mWorkManager;
    private List<ComplexEntityForDB> mResultList;

    private boolean needPreView = false;

    public MakerSearchCriteriaReportVM(@NonNull Application application) {
        super(application);
        mWorkManager = WorkManager.getInstance(application);
    }

    public boolean isNeedPreView() {
        return needPreView;
    }

    public void setNeedPreView(boolean b) { needPreView = b; }

    public void setResultList(List<ComplexEntityForDB> resultList) {
        mResultList = resultList;
    }

    @Override
    protected String getOrderBy() {
        return " " + getApplication().getString(R.string.order_by_for_report);
    }

    private String getDateRangesString() {
        StringBuilder sb = new StringBuilder();
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

    private Data getDataForWorker() {
        Data.Builder builder = new Data.Builder();
        Map<String, Object> helperMap = new HashMap<>();
        helperMap.put(CreateReportWorker.KEY_FOR_RESULT_LIST, mResultList);
        return builder.putAll(helperMap)
            .putString(CreateReportWorker.KEY_FOR_DATE_RANGES, getDateRangesString())
            .putString(CreateReportWorker.KEY_FOR_QUERY, getQueryForSearch()).build();
    }

    public void launchCreatingReport() {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(CreateReportWorker.class)
                .setInputData(getDataForWorker())
                .build();
        mWorkManager.enqueue(workRequest);
    }

}
