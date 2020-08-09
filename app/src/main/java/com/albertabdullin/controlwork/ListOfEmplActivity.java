package com.albertabdullin.controlwork;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ListOfEmplActivity extends AppCompatActivity implements RecyclerViewObserver {
    private EmployersAdapter employersAdapter;
    private ListOfEmplVM model;

    @Override
    public void onClick(EntityForDB eDB) {
        Intent resultIntent = new Intent();
        resultIntent
            .putExtra(FillNewData_Activity.ID_FROM_DB, eDB.getID())
            .putExtra(FillNewData_Activity.ITEM_FROM_DB, eDB.getDescription());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

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
        Observer<List<EntityForDB>> observerRV = new Observer<List<EntityForDB>>() {
            @Override
            public void onChanged(List<EntityForDB> changedList) {
                if(model.getHelperListOfEntities().size() == 0) {
                    model.getHelperListOfEntities().addAll(changedList);
                    employersAdapter.notifyDataSetChanged();
                }else employersAdapter.notifyItemChanged(model.getHelperListOfEntities().size() - 1);
            }
        };
        employersAdapter = new EmployersAdapter(model.getHelperListOfEntities(), this);
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