package com.albertabdullin.controlwork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    private AdapterView.OnItemClickListener ItemOfListView = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            if (id==0) fillNewData(v);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] listOfFunction = new String[] {"Добавить данные", "Отчет за текущий день",
            "Отчет за текущую неделю", "Отчет за текущий месяц", "Отчет за текущий год",
            "Отчет за указанный период", "Устаревшие данные"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listOfFunction);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(ItemOfListView);
    }

    private void fillNewData(View view) {
        Intent intent = new Intent(this, FillNewData_Activity.class);
        startActivity(intent);
    }
}

