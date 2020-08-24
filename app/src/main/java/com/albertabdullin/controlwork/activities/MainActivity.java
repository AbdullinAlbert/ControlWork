package com.albertabdullin.controlwork.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.albertabdullin.controlwork.*;
import com.albertabdullin.controlwork.activities.FillNewData_Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    private AdapterView.OnItemClickListener ItemOfListView = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            if (id==0) fillNewData();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_list_of_emp);
        setSupportActionBar(toolbar);
        final String[] listOfFunction = new String[] {"Добавить данные", "Редактировать/удалить данные", "Отчет за текущий день",
            "Отчет за текущую неделю", "Отчет за текущий месяц", "Отчет за текущий год",
            "Отчет за указанный период", "Устаревшие данные", "Настройки", "Как работает программа"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listOfFunction);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(ItemOfListView);
    }

    private void fillNewData() {
        Intent intent = new Intent(this, FillNewData_Activity.class);
        startActivity(intent);
    }
}

