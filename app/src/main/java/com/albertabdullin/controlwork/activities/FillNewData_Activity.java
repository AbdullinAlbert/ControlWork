package com.albertabdullin.controlwork.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;

public class FillNewData_Activity extends AppCompatActivity implements ActivityResultCaller {
    private EditText addEmpl;
    private int employerId;
    private EditText addFirm;
    private int firmId;
    private EditText addPlaceOfWork;
    private int powId;
    private EditText addTypeOfWork;
    private int towId;
    public static final String ITEM_FROM_DB = "get_item_from_db";
    public static final String LAUNCH_DEFINITELY_DB_TABLE = "launch_definitely_db_table";
    public static final int TABLE_OF_EMPLOYERS = 0;
    public static final int TABLE_OF_FIRMS = 1;
    public static final int TABLE_OF_TYPES_OF_WORK = 2;
    public static final int TABLE_OF_PLACES_OF_WORK = 3;

    ActivityResultLauncher launcerActivityForDB = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    SimpleEntityForDB eDB = intent.getParcelableExtra(ITEM_FROM_DB);
                    switch (intent.getIntExtra(LAUNCH_DEFINITELY_DB_TABLE, -1)) {
                        case TABLE_OF_EMPLOYERS:
                            addEmpl.setText(eDB.getDescription());
                            employerId = eDB.getID();
                            break;
                        case TABLE_OF_FIRMS:
                            addFirm.setText(eDB.getDescription());
                            firmId = eDB.getID();
                            break;
                        case TABLE_OF_TYPES_OF_WORK:
                            addTypeOfWork.setText(eDB.getDescription());
                            towId = eDB.getID();
                            break;
                        case TABLE_OF_PLACES_OF_WORK:
                            addPlaceOfWork.setText(eDB.getDescription());
                            powId = eDB.getID();
                            break;
                    }
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
        addEmpl = findViewById(R.id.add_empl_editText);
        addEmpl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivutyForResult(TABLE_OF_EMPLOYERS);
            }
        });
        addFirm = findViewById(R.id.add_firm_editText);
        addFirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivutyForResult(TABLE_OF_FIRMS);
            }
        });
        addPlaceOfWork = findViewById(R.id.add_placeOfWork_editText);
        addPlaceOfWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivutyForResult(TABLE_OF_PLACES_OF_WORK);
            }
        });
        addTypeOfWork = findViewById(R.id.add_typeOfWork_editText);
        addTypeOfWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivutyForResult(TABLE_OF_TYPES_OF_WORK);
            }
        });
        Button button = findViewById(R.id.add_button);
        button.setClickable(true);
        button.setFocusable(true);
    }

    private void launchActivutyForResult(int i) {
        Intent intent = new Intent(FillNewData_Activity.this, ListOfBDItemsActivity.class);
        intent.putExtra(LAUNCH_DEFINITELY_DB_TABLE, i);
        launcerActivityForDB.launch(intent);
    }

}
