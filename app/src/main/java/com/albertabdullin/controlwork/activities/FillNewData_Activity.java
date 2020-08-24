package com.albertabdullin.controlwork.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.albertabdullin.controlwork.R;

public class FillNewData_Activity extends AppCompatActivity implements ActivityResultCaller {
    private EditText addNewEmpl;
    private int employerId;
    public static final String ITEM_FROM_DB = "get_item_from_db";
    public static final String ID_FROM_DB = "get_id";
    public static final String LAUNCH_DEFINITELY_DB = "launch_definitely_db";

    ActivityResultLauncher launcerActivityForDB = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    addNewEmpl.setText(intent.getStringExtra(ITEM_FROM_DB));
                    employerId = intent.getIntExtra(ID_FROM_DB, -1);
                }
            }
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_data);
        Toolbar toolbar = findViewById(R.id.toolbar_list_of_emp);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        addNewEmpl = findViewById(R.id.add_empl_editText);
        addNewEmpl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivutyForResult(0);
            }
        });
    }

    private void launchActivutyForResult(int i) {
        Intent intent = new Intent(FillNewData_Activity.this, ListOfBDItemsActivity.class);
        intent.putExtra(LAUNCH_DEFINITELY_DB, i);
        launcerActivityForDB.launch(intent);
    }

}
