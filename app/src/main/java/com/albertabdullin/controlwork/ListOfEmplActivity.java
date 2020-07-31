package com.albertabdullin.controlwork;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Map;

public class ListOfEmplActivity extends AppCompatActivity {
    private EmployersAdapter employersAdapter;
    private ListOfEmplVM model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_employers);
        Toolbar toolbar = findViewById(R.id.toolbar_list_of_emp);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        model = new ViewModelProvider(this, new ViewModelFactory(this.getApplication())).get(ListOfEmplVM.class);
        RecyclerView recyclerView = findViewById(R.id.list_of_emp);
        Observer<Map<Integer, Map<Integer, String>>> observerRV = new Observer<Map<Integer, Map<Integer, String>>>() {
            @Override
            public void onChanged(Map<Integer, Map<Integer, String>> changedList) {
                if(model.getHelperListOfEmp().size() == 0) {
                    model.getHelperListOfEmp().putAll(changedList);
                    employersAdapter.notifyDataSetChanged();
                }else employersAdapter.notifyItemChanged(model.getHelperListOfEmp().size() - 1);
            }
        };
        employersAdapter = new EmployersAdapter(model.getHelperListOfEmp());
        model.getLiveDataEmp().observe(this, observerRV);
        recyclerView.setAdapter(employersAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddDataDF dialogFragment = new AddDataDF();
                dialogFragment.show(getSupportFragmentManager(), "newData");
            }
        });
    }



}