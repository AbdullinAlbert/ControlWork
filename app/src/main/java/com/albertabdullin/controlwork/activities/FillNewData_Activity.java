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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;

import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.models.DateConverter;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;
import com.albertabdullin.controlwork.viewmodels.AddNewDataVM;
import com.albertabdullin.controlwork.viewmodels.ViewModelFactoryAddNewData;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.Calendar;

public class FillNewData_Activity extends AppCompatActivity implements ActivityResultCaller {
    private EditText addEmpl;
    private EditText addFirm;
    private EditText addPlaceOfWork;
    private EditText addTypeOfWork;
    private EditText date;
    private EditText resultValue;
    private Button addButton;
    private EditText note;
    private AddNewDataVM mViewModel;
    public static final String ITEM_FROM_DB = "get_item_from_db";
    public static final String LAUNCH_DEFINITELY_DB_TABLE = "launch_definitely_db_table";
    public static final int TABLE_OF_EMPLOYERS = 0;
    public static final int TABLE_OF_FIRMS = 1;
    public static final int TABLE_OF_TYPES_OF_WORK = 2;
    public static final int TABLE_OF_PLACES_OF_WORK = 3;

    public static Handler handler = new Handler(Looper.getMainLooper());

    ActivityResultLauncher launcherActivityForDB = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
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
                    }
                }
        });

    View.OnClickListener pickDate = (v) -> {
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

    View.OnClickListener addDataListener = (v) -> {
        mViewModel.startToCheckCorrectData();
        addButton.setText(getString(R.string.correct_data_check));
        addButton.setClickable(false);
        addButton.setForeground(null);
    };

    TextWatcher twResultValue = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!mViewModel.isCorrectResultValueData()) {
                resultValue.setBackgroundTintList(null);
                resultValue.setTextColor(getResources().getColor(R.color.standardBlack, null));
                TextView tv = findViewById(R.id.incorrectResultValueFocus);
                tv.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                mViewModel.setCorrectResultValueDataTrue();
            }
            mViewModel.setResultValueString(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

    TextWatcher twNote = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mViewModel.setNote(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {   }
    };

    Observer<Boolean> observerIncorrectEmployeeET = (b) -> {
        if (b) emphasizeEditText(addEmpl);
        else deleteEmphasizeFromEditText(addEmpl);
    };

    Observer<Boolean> observerIncorrectEmployeeTV = b -> {
        if (b) emphasizeTextView(findViewById(R.id.incorrectEmployerFocus));
        else deleteEmphasizeFromTextView(findViewById(R.id.incorrectEmployerFocus));
    };

    Observer<Boolean> observerIncorrectFirmET = (b) -> {
        if (b) emphasizeEditText(addFirm);
        else deleteEmphasizeFromEditText(addFirm);
    };

    Observer<Boolean> observerIncorrectFirmTV = b -> {
        if (b) emphasizeTextView(findViewById(R.id.incorrectFirmFocus));
        else deleteEmphasizeFromTextView(findViewById(R.id.incorrectFirmFocus));
    };

    Observer<Boolean> observerIncorrectToWET = (b) -> {
        if (b) emphasizeEditText(addTypeOfWork);
        else deleteEmphasizeFromEditText(addTypeOfWork);

    };

    Observer<Boolean> observerIncorrectToWTV = b -> {
        if (b) emphasizeTextView(findViewById(R.id.incorrectToWFocus));
        else deleteEmphasizeFromTextView(findViewById(R.id.incorrectToWFocus));
    };

    Observer<Boolean> observerIncorrectPoWET = (b) -> {
        if (b) emphasizeEditText(addPlaceOfWork);
        else deleteEmphasizeFromEditText(addPlaceOfWork);
    };

    Observer<Boolean> observerIncorrectPoWTV = b -> {
        if (b) emphasizeTextView(findViewById(R.id.incorrectPoWFocus));
        else deleteEmphasizeFromTextView(findViewById(R.id.incorrectPoWFocus));
    };

    Observer<Boolean> observerIncorrectResultET = (b) -> {
        if (b) emphasizeEditText(findViewById(R.id.add_result_editText));
        else deleteEmphasizeFromEditText(findViewById(R.id.add_result_editText));
    };

    Observer<Boolean> observerIncorrectResultTV = b -> {
        if (b) emphasizeTextView(findViewById(R.id.incorrectResultValueFocus));
        else deleteEmphasizeFromTextView(findViewById(R.id.incorrectResultValueFocus));
    };

    private void deleteEmphasizeFromEditText(EditText editText) {
        editText.setBackgroundTintList(null);
        editText.setTextColor(getResources().getColor(R.color.standardBlack, null));
    }

    private void deleteEmphasizeFromTextView(TextView textView) {
        textView.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
    }

    private void emphasizeEditText(EditText editText) {
        editText.setBackgroundTintList(getColorStateList(R.color.highlightBorder));
        editText.setTextColor(getResources().getColor(R.color.highlightBorder, null));
    }

    private void emphasizeTextView(TextView tv) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMarginStart(12);
        tv.setLayoutParams(lp);
    }

    Observer<Boolean> observerAddButtonForPrepare = (b) -> {
            addButton.setClickable(true);
            addButton.setText("Добавить");
            int[] attr = new int[] {android.R.attr.selectableItemBackground};
            TypedArray typedArray = obtainStyledAttributes(attr);
            Drawable drawableAttr = typedArray.getDrawable(0);
            typedArray.recycle();
            addButton.setForeground(drawableAttr);
            if (b) {
                resultValue.setText("");
                note.setText("");
            }
    };

    Observer<String> observerAddButtonChangeText = (s) -> addButton.setText(s);

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
        TextView tvEmp = findViewById(R.id.incorrectEmployerFocus);
        tvEmp.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        TextView tvFirm = findViewById(R.id.incorrectFirmFocus);
        tvFirm.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        TextView tvToW = findViewById(R.id.incorrectToWFocus);
        tvToW.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        TextView tvPoW = findViewById(R.id.incorrectPoWFocus);
        tvPoW.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        TextView tvRes = findViewById(R.id.incorrectResultValueFocus);
        tvRes.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        addEmpl = findViewById(R.id.add_empl_editText);
        addEmpl.setOnClickListener(v -> launchActivityForResult(TABLE_OF_EMPLOYERS));
        Observer<String> employerEditTextObserver = s -> addEmpl.setText(s);
        mViewModel.getLiveDataEmployerText().observe(this, employerEditTextObserver);
        addFirm = findViewById(R.id.add_firm_editText);
        addFirm.setOnClickListener(v -> launchActivityForResult(TABLE_OF_FIRMS));
        Observer<String> firmEditTextObserver = s -> addFirm.setText(s);
        mViewModel.getLiveDataFirmText().observe(this, firmEditTextObserver);
        addPlaceOfWork = findViewById(R.id.add_placeOfWork_editText);
        addPlaceOfWork.setOnClickListener(v -> launchActivityForResult(TABLE_OF_PLACES_OF_WORK));
        Observer<String> pOWEditTextObserver = s -> addPlaceOfWork.setText(s);
        mViewModel.getLiveDataPoWText().observe(this, pOWEditTextObserver);
        addTypeOfWork = findViewById(R.id.add_typeOfWork_editText);
        addTypeOfWork.setOnClickListener(v -> launchActivityForResult(TABLE_OF_TYPES_OF_WORK));
        Observer<String> tOWEditTextObserver = s -> addTypeOfWork.setText(s);
        mViewModel.getLiveDataToWText().observe(this, tOWEditTextObserver);
        date = findViewById(R.id.add_date_editText);
        date.setOnClickListener(pickDate);
        resultValue = findViewById(R.id.add_result_editText);
        resultValue.addTextChangedListener(twResultValue);
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
        mViewModel.getPrepareAddButton().observe(this, observerAddButtonForPrepare);
        mViewModel.getChangeTextAddButton().observe(this, observerAddButtonChangeText);
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

}
