package com.albertabdullin.controlwork.activities;

import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.models.DateConverter;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;
import com.albertabdullin.controlwork.viewmodels.AddNewDataVM;
import com.albertabdullin.controlwork.viewmodels.ViewModelFactoryAddNewData;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.Calendar;

public class FillNewData_Activity extends AppCompatActivity implements ActivityResultCaller {
    private EditText addEmpl;
    private EditText addFirm;
    private EditText addPlaceOfWork;
    private EditText addTypeOfWork;
    private EditText date;
    private EditText resultValue;
    private EditText addTypeOfResult;
    private Button addButton;
    private EditText note;
    private AddNewDataVM mViewModel;
    public static final String ITEM_FROM_DB = "get_item_from_db";
    public static final String LAUNCH_DEFINITELY_DB_TABLE = "launch_definitely_db_table";
    public static final int TABLE_OF_EMPLOYERS = 0;
    public static final int TABLE_OF_FIRMS = 1;
    public static final int TABLE_OF_TYPES_OF_WORK = 2;
    public static final int TABLE_OF_PLACES_OF_WORK = 3;
    public static final int TABLE_OF_RESULT_TYPE = 4;
    public static final int DISABLED_STATE = 0;
    public static final int ENABLED_STATE = 1;
    public static final int DATA_ADDING_STATE = 2;

    public static Handler handler = new Handler(Looper.getMainLooper());

    private final ActivityResultLauncher launcherActivityForDB = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
        (result) -> {
                if(result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    assert intent != null;
                    SimpleEntityForDB eDB = intent.getParcelableExtra(ITEM_FROM_DB);
                    switch (intent.getIntExtra(LAUNCH_DEFINITELY_DB_TABLE, -1)) {
                        case TABLE_OF_EMPLOYERS:
                            if (!mViewModel.isCorrectEmployerData()) mViewModel.deleteEmphasizeFromEditTextAndTextView(TABLE_OF_EMPLOYERS);
                            assert eDB != null;
                            addEmpl.setText(eDB.getDescription());
                            mViewModel.setEmployerId(eDB.getID());
                            break;
                        case TABLE_OF_FIRMS:
                            if (!mViewModel.isCorrectFirmData()) mViewModel.deleteEmphasizeFromEditTextAndTextView(TABLE_OF_FIRMS);
                            assert eDB != null;
                            addFirm.setText(eDB.getDescription());
                            mViewModel.setFirmId(eDB.getID());
                            break;
                        case TABLE_OF_TYPES_OF_WORK:
                            if (!mViewModel.isCorrectToWData()) mViewModel.deleteEmphasizeFromEditTextAndTextView(TABLE_OF_TYPES_OF_WORK);
                            assert eDB != null;
                            addTypeOfWork.setText(eDB.getDescription());
                            mViewModel.setTowId(eDB.getID());
                            break;
                        case TABLE_OF_PLACES_OF_WORK:
                            if (!mViewModel.isCorrectPoWData()) mViewModel.deleteEmphasizeFromEditTextAndTextView(TABLE_OF_PLACES_OF_WORK);
                            assert eDB != null;
                            addPlaceOfWork.setText(eDB.getDescription());
                            mViewModel.setPowId(eDB.getID());
                            break;
                        case TABLE_OF_RESULT_TYPE:
                            if (!mViewModel.isCorrectResultTypeData()) mViewModel.deleteEmphasizeFromEditTextAndTextView(TABLE_OF_RESULT_TYPE);
                            assert eDB != null;
                            addTypeOfResult.setText(eDB.getDescription());
                            mViewModel.setResultTypeID(eDB.getID());
                            break;
                    }
                }
        });

    private final View.OnClickListener pickDate = (v) -> {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            MaterialDatePicker<Long> materialDatePicker = builder.build();
            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selection);
                String date = DateConverter.getStringViewOfDate(calendar);
                FillNewData_Activity.this.date.setText(date);
                mViewModel.setDateForSql(selection);
            });
            materialDatePicker.show(getSupportFragmentManager(), "date_picker");
    };

    private final View.OnClickListener addDataListener = (v) -> {
        hideKeyBoard();
        mViewModel.startToCheckCorrectData();
    };

    private final TextWatcher twResultValue = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!mViewModel.isCorrectResultValueData()) {
                resultValue.setBackgroundTintList(null);
                resultValue.setTextColor(getResources().getColor(R.color.standardBlack, null));
                findViewById(R.id.incorrectResultValueFocus).setVisibility(View.GONE);
                mViewModel.setCorrectResultValueDataTrue();
            }
            mViewModel.setResultValueString(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

    private final TextWatcher twNote = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mViewModel.setNote(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {   }
    };

    private final Observer<Boolean> observerIncorrectEmployeeET = (b) -> {
        if (b) emphasizeEditText(addEmpl);
        else deleteEmphasizeFromEditText(addEmpl);
    };

    private final Observer<Boolean> observerIncorrectEmployeeTV = b -> {
        TextView tv = findViewById(R.id.incorrectEmployerFocus);
        if (b) tv.setVisibility(View.VISIBLE);
        else tv.setVisibility(View.GONE);
    };

    private final Observer<Boolean> observerIncorrectFirmET = (b) -> {
        if (b) emphasizeEditText(addFirm);
        else deleteEmphasizeFromEditText(addFirm);
    };

    private final Observer<Boolean> observerIncorrectFirmTV = b -> {
        TextView tv = findViewById(R.id.incorrectFirmFocus);
        if (b) tv.setVisibility(View.VISIBLE);
        else tv.setVisibility(View.GONE);
    };

    private final Observer<Boolean> observerIncorrectToWET = (b) -> {
        if (b) emphasizeEditText(addTypeOfWork);
        else deleteEmphasizeFromEditText(addTypeOfWork);

    };

    private final Observer<Boolean> observerIncorrectToWTV = b -> {
        TextView tv = findViewById(R.id.incorrectToWFocus);
        if (b) tv.setVisibility(View.VISIBLE);
        else tv.setVisibility(View.GONE);
    };

    private final Observer<Boolean> observerIncorrectPoWET = (b) -> {
        if (b) emphasizeEditText(addPlaceOfWork);
        else deleteEmphasizeFromEditText(addPlaceOfWork);
    };

    private final Observer<Boolean> observerIncorrectPoWTV = b -> {
        TextView tv = findViewById(R.id.incorrectPoWFocus);
        if (b) tv.setVisibility(View.VISIBLE);
        else tv.setVisibility(View.GONE);
    };

    private final Observer<Boolean> observerIncorrectResultET = (b) -> {
        if (b) emphasizeEditText(findViewById(R.id.add_result_editText));
        else deleteEmphasizeFromEditText(findViewById(R.id.add_result_editText));
    };

    private final Observer<Boolean> observerIncorrectResultTV = b -> {
        TextView tv = findViewById(R.id.incorrectResultValueFocus);
        if (b) tv.setVisibility(View.VISIBLE);
        else tv.setVisibility(View.GONE);
    };

    private final Observer<Boolean> observerIncorrectResultTypeET = (b) -> {
        if (b) emphasizeEditText(findViewById(R.id.result_type_editText));
        else deleteEmphasizeFromEditText(findViewById(R.id.result_type_editText));
    };

    private final Observer<Boolean> observerIncorrectResultTypeTV = b -> {
        TextView tv = findViewById(R.id.incorrectResultTypeFocus);
        if (b) tv.setVisibility(View.VISIBLE);
        else tv.setVisibility(View.GONE);
    };

    private void deleteEmphasizeFromEditText(EditText editText) {
        editText.setBackgroundTintList(null);
        editText.setTextColor(getResources().getColor(R.color.standardBlack, null));
    }

    private void emphasizeEditText(EditText editText) {
        editText.setBackgroundTintList(getColorStateList(R.color.highlightBorder));
        editText.setTextColor(getResources().getColor(R.color.highlightBorder, null));
    }

    Observer<Integer> observerAddButtonForPrepare = (i) -> {
        if (i == DISABLED_STATE) {
            addButton.setText(getString(R.string.correct_data_check));
            addButton.setEnabled(false);
        } else if (i == ENABLED_STATE) {
            addButton.setClickable(true);
            addButton.setText("Добавить");
            addButton.setEnabled(true);
        } else if (i == DATA_ADDING_STATE)
            addButton.setText(getString(R.string.adding_data_process_is_active));
    };

    Observer<String> resultValueEditTextObserver = (s) -> resultValue.setText(s);

    Observer<String> noteValueEditTextObserver = (s) -> note.setText(s);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_data);
        Toolbar toolbar = findViewById(R.id.toolbar_list_of_emp);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setDisplayHomeAsUpEnabled(true);
        else throw new RuntimeException("Support ActionBar равен Null");
        mViewModel = new ViewModelProvider(this, new ViewModelFactoryAddNewData(this.getApplication())).get(AddNewDataVM.class);
        addEmpl = findViewById(R.id.add_empl_editText);
        addEmpl.setOnClickListener(v -> {
            hideKeyBoard();
            launchActivityForResult(TABLE_OF_EMPLOYERS);
        });
        Observer<String> employerEditTextObserver = s -> addEmpl.setText(s);
        mViewModel.getLiveDataEmployerText().observe(this, employerEditTextObserver);
        addFirm = findViewById(R.id.add_firm_editText);
        addFirm.setOnClickListener(v -> {
            hideKeyBoard();
            launchActivityForResult(TABLE_OF_FIRMS);
        });
        Observer<String> firmEditTextObserver = s -> addFirm.setText(s);
        mViewModel.getLiveDataFirmText().observe(this, firmEditTextObserver);
        addPlaceOfWork = findViewById(R.id.add_placeOfWork_editText);
        addPlaceOfWork.setOnClickListener(v -> {
            hideKeyBoard();
            launchActivityForResult(TABLE_OF_PLACES_OF_WORK);
        });
        Observer<String> pOWEditTextObserver = s -> addPlaceOfWork.setText(s);
        mViewModel.getLiveDataPoWText().observe(this, pOWEditTextObserver);
        addTypeOfWork = findViewById(R.id.add_typeOfWork_editText);
        addTypeOfWork.setOnClickListener(v -> {
            hideKeyBoard();
            launchActivityForResult(TABLE_OF_TYPES_OF_WORK);
        });
        Observer<String> tOWEditTextObserver = s -> addTypeOfWork.setText(s);
        mViewModel.getLiveDataToWText().observe(this, tOWEditTextObserver);
        date = findViewById(R.id.add_date_editText);
        date.setOnClickListener(pickDate);
        resultValue = findViewById(R.id.add_result_editText);
        resultValue.addTextChangedListener(twResultValue);
        addTypeOfResult = findViewById(R.id.result_type_editText);
        addTypeOfResult.setOnClickListener(v -> {
            hideKeyBoard();
            launchActivityForResult(TABLE_OF_RESULT_TYPE);
        });
        Observer<String> resultTypeObserver = addTypeOfResult::setText;
        mViewModel.getLiveDataResultTypeText().observe(this, resultTypeObserver);
        note = findViewById(R.id.add_note_editText);
        note.addTextChangedListener(twNote);
        addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(addDataListener);
        mViewModel.getWarningEmployerETLD().observe(this, observerIncorrectEmployeeET);
        mViewModel.getWarningEmployerTVLD().observe(this, observerIncorrectEmployeeTV);
        mViewModel.getWarningFirmETLD().observe(this, observerIncorrectFirmET);
        mViewModel.getWarningFirmTVLD().observe(this, observerIncorrectFirmTV);
        mViewModel.getWarningTypeOfWorkETLD().observe(this, observerIncorrectToWET);
        mViewModel.getWarningTypeOfWorkTVLD().observe(this, observerIncorrectToWTV);
        mViewModel.getWarningPlaceOfWorkETLD().observe(this, observerIncorrectPoWET);
        mViewModel.getWarningPlaceOfWorkTVLD().observe(this, observerIncorrectPoWTV);
        mViewModel.getWarningResultETLD().observe(this, observerIncorrectResultET);
        mViewModel.getWarningResultTVLD().observe(this, observerIncorrectResultTV);
        mViewModel.getResultValueEditText().observe(this, resultValueEditTextObserver);
        mViewModel.getNoteValueEditText().observe(this, noteValueEditTextObserver);
        mViewModel.getWarningResultTypeETLD().observe(this, observerIncorrectResultTypeET);
        mViewModel.getWarningResultTypeTVLD().observe(this,  observerIncorrectResultTypeTV);
        mViewModel.getPrepareAddButton().observe(this, observerAddButtonForPrepare);
        if (mViewModel.isFirstLaunch()) {
            date.setText(DateConverter.getStringViewOfDate(Calendar.getInstance()));
            mViewModel.setDateForSql(Calendar.getInstance().getTimeInMillis());
            mViewModel.getFirstItemsFromDBTables();
            mViewModel.setFirstLaunchFalse();
        }
    }

    private void launchActivityForResult(int i) {
        Intent intent = new Intent(FillNewData_Activity.this, ListOfDBItemsActivity.class);
        intent.putExtra(LAUNCH_DEFINITELY_DB_TABLE, i);
        launcherActivityForDB.launch(intent);
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view != null) {
            if (imm.isAcceptingText()) imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        } else {
            if (imm.isActive()) imm.toggleSoftInput(0,0);
        }
    }

}
