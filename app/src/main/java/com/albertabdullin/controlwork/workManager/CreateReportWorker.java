package com.albertabdullin.controlwork.workManager;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.activities.ReportActivity;
import com.albertabdullin.controlwork.workManager.creating_list_of_report_data_tool.PDFReportCreator;
import com.albertabdullin.controlwork.workManager.creating_list_of_report_data_tool.ReportDataCreator;

public class CreateReportWorker extends Worker {

    public static String KEY_FOR_DATE_RANGES = "key for date ranges";
    public static String KEY_FOR_QUERY = "key for query";
    public static String KEY_FOR_PERMISSION = "key for permission";

    public CreateReportWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    @NonNull
    @Override
    public Result doWork() {
        ReportDataCreator reportDataCreator = new ReportDataCreator(getApplicationContext(), getInputData().getString(KEY_FOR_QUERY));
        PDFReportCreator pdfReportCreator;
        try {
            pdfReportCreator = new PDFReportCreator(
                    getApplicationContext(),
                    reportDataCreator.getResultList(),
                    getInputData().getString(KEY_FOR_DATE_RANGES),
                    getInputData().getBoolean(KEY_FOR_PERMISSION, false),
                    reportDataCreator.getPageCountList(),
                    reportDataCreator.getListName()
            );
        } catch (RuntimeException e) {
            ReportActivity.handler.post(() ->
                Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.fail_attempt_about_delete_data_from_db) + ": "
                + e.getMessage(), Toast.LENGTH_SHORT).show());
                return Result.failure();
        }
        if (pdfReportCreator.createPDFDocument().equals(Result.success())) {
            ReportActivity.handler.post(() -> Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.the_report_has_been_created_successfully), Toast.LENGTH_LONG).show());
            return Result.success();
        } else {
            ReportActivity.handler.post(() ->
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.the_report_has_not_been_created), Toast.LENGTH_LONG).show());
            return Result.failure();
        }
    }

}
