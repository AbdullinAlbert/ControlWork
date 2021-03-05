package com.albertabdullin.controlwork.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.albertabdullin.controlwork.*;
import com.albertabdullin.controlwork.fragments.SearchCriteriaForReportFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_FOR_REPORT_PERIOD = "key for report period";

    private final AdapterView.OnItemClickListener ItemOfListView = (parent, v, position, id) -> {
        switch ((int) id) {
            case 0:
                fillNewData();
                break;
            case 1:
                deleteOrChangeData();
                break;
            case 2:
                monthlyReport();
                break;
            case 3:
                yearReport();
                break;
            case 4:
                certainPeriodReport();
                break;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar_list_of_emp);
        setSupportActionBar(toolbar);
        final String[] listOfFunction = new String[] {"Добавить данные", "Редактировать/удалить данные", "Отчет за текущий месяц", "Отчет за текущий год",
            "Отчет за указанный период", "Настройки", "Как работает программа"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listOfFunction);
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(ItemOfListView);
    }

    private void fillNewData() {
        Intent intent = new Intent(this, FillNewData_Activity.class);
        startActivity(intent);
    }

    private void deleteOrChangeData() {
        Intent intent = new Intent(this, MakerSearchCriteriaActivity.class);
        startActivity(intent);
    }

    private void monthlyReport() {
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra(KEY_FOR_REPORT_PERIOD, SearchCriteriaForReportFragment.DateRange.MONTH);
        startActivity(intent);
    }

    private void yearReport() {
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra(KEY_FOR_REPORT_PERIOD, SearchCriteriaForReportFragment.DateRange.YEAR);
        startActivity(intent);
    }

    private void certainPeriodReport() {
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra(KEY_FOR_REPORT_PERIOD, SearchCriteriaForReportFragment.DateRange.CERTAIN_PERIOD);
        startActivity(intent);
    }

}

