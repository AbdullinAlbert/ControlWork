package com.albertabdullin.controlwork;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class FillNewData_Activity extends AppCompatActivity {
    static final int PICK_EMPLOYER = 0;
    private View.OnClickListener empClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showTableOfEmployers(v);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_list_of_emp);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        EditText addNewEmpl = (EditText)findViewById(R.id.add_empl_editText);
        addNewEmpl.setOnClickListener(empClickListner);
    }
    private void showTableOfEmployers(View view) {
        Intent intent = new Intent(this, ListOfEmplActivity.class);
        startActivityForResult(intent, PICK_EMPLOYER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
