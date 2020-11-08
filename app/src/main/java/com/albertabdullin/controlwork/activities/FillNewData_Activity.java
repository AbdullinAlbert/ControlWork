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
    private static AddNewDataVM model;
    public static final String ITEM_FROM_DB = "get_item_from_db";
    public static final String LAUNCH_DEFINITELY_DB_TABLE = "launch_definitely_db_table";
    public static final int TABLE_OF_EMPLOYERS = 0;
    public static final int TABLE_OF_FIRMS = 1;
    public static final int TABLE_OF_TYPES_OF_WORK = 2;
    public static final int TABLE_OF_PLACES_OF_WORK = 3;
    public static final int GET_EMPLOYER_MESSAGE = 4;
    public static final int GET_FIRM_MESSAGE = 5;
    public static final int GET_POW_MESSAGE = 6;
    public static final int GET_TOW_MESSAGE = 7;
    public static final int ADD_DATA_TO_BD = 8;
    public static final int NOTIFY_ABOUT_CHECK_DATA_FROM_DB = 9;

    public static Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case GET_EMPLOYER_MESSAGE:
                    model.setEmployerEditText();
                    break;
                case GET_FIRM_MESSAGE:
                    model.setFirmEditText();
                    break;
                case GET_POW_MESSAGE:
                    model.setPlaceOfWorkEditText();
                    break;
                case GET_TOW_MESSAGE:
                    model.setTypeOfWorkEditText();
                    break;
                case ADD_DATA_TO_BD:
                    if (msg.arg1 == 1) model.notifyAboutCompleteOperation(true);
                    else model.notifyAboutCompleteOperation(false);
                    break;
                case NOTIFY_ABOUT_CHECK_DATA_FROM_DB:
                    model.readResultOfCheck();
                    break;
            }
        }
    };


    ActivityResultLauncher launcherActivityForDB = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    assert intent != null;
                    SimpleEntityForDB eDB = intent.getParcelableExtra(ITEM_FROM_DB);
                    switch (intent.getIntExtra(LAUNCH_DEFINITELY_DB_TABLE, -1)) {
                        case TABLE_OF_EMPLOYERS:
                            if (!model.isCorrectEmployerData()) {
                                addEmpl.setBackgroundTintList(null);
                                addEmpl.setTextColor(getResources().getColor(R.color.standardBlack, null));
                                TextView tv = findViewById(R.id.incorrectEmployerFocus);
                                tv.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                            }
                            assert eDB != null;
                            addEmpl.setText(eDB.getDescription());
                            model.setEmployerId(eDB.getID());
                            break;
                        case TABLE_OF_FIRMS:
                            if (!model.isCorrectFirmData()) {
                                addFirm.setBackgroundTintList(null);
                                addFirm.setTextColor(getResources().getColor(R.color.standardBlack, null));
                                TextView tv = findViewById(R.id.incorrectFirmFocus);
                                tv.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                            }
                            assert eDB != null;
                            addFirm.setText(eDB.getDescription());
                            model.setFirmId(eDB.getID());
                            break;
                        case TABLE_OF_TYPES_OF_WORK:
                            if (!model.isCorrectToWData()) {
                                addTypeOfWork.setBackgroundTintList(null);
                                addTypeOfWork.setTextColor(getResources().getColor(R.color.standardBlack, null));
                                TextView tv = findViewById(R.id.incorrectToWFocus);
                                tv.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                            }
                            assert eDB != null;
                            addTypeOfWork.setText(eDB.getDescription());
                            model.setTowId(eDB.getID());
                            break;
                        case TABLE_OF_PLACES_OF_WORK:
                            if (!model.isCorrectPoWData()) {
                                addPlaceOfWork.setBackgroundTintList(null);
                                addPlaceOfWork.setTextColor(getResources().getColor(R.color.standardBlack, null));
                                TextView tv = findViewById(R.id.incorrectPoWFocus);
                                tv.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                            }
                            assert eDB != null;
                            addPlaceOfWork.setText(eDB.getDescription());
                            model.setPowId(eDB.getID());
                            break;
                    }
                }
            }
        });

    View.OnClickListener pickDate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            MaterialDatePicker<Long> materialDatePicker = builder.build();
            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                @Override
                public void onPositiveButtonClick(Long selection) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(selection);
                    String date = convertLongToStringDate(calendar);
                    FillNewData_Activity.this.date.setText(date);
                    model.setDateForSql(selection);
                }
            });
            materialDatePicker.show(getSupportFragmentManager(), "date_picker");
        }
    };

    View.OnClickListener addDataListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            model.startToCheckCorrectData();
            addButton.setText("Проверка корректности данных");
            addButton.setClickable(false);
            addButton.setForeground(null);
        }
    };

    TextWatcher twResultValue = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!model.isCorrectResultValueData()) {
                resultValue.setBackgroundTintList(null);
                resultValue.setTextColor(getResources().getColor(R.color.standardBlack, null));
                TextView tv = findViewById(R.id.incorrectResultValueFocus);
                tv.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                model.setCorrectResultValueDataTrue();
            }
            model.setResultValueString(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    TextWatcher twNote = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            model.setNote(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    Observer<Integer> observerIncorrectET = new Observer<Integer>() {
        @Override
        public void onChanged(Integer i) {
            EditText editText;
            switch (i) {
                case AddNewDataVM.INCORRECT_EMPLOYEE_ET:
                    editText = addEmpl;
                    break;
                case AddNewDataVM.INCORRECT_FIRM_ET:
                    editText = addFirm;
                    break;
                case AddNewDataVM.INCORRECT_TYPE_ET:
                    editText = addTypeOfWork;
                    break;
                case AddNewDataVM.INCORRECT_PLACE_ET:
                    editText = addPlaceOfWork;
                    break;
                case AddNewDataVM.INCORRECT_RES_VALUE_ET:
                    editText = resultValue;
                    break;
                default:
                    throw new RuntimeException("Опечатка в константах некорректно заполненных полей");
            }
            editText.setBackgroundTintList(getColorStateList(R.color.highliteBorder));
            editText.setTextColor(getResources().getColor(R.color.highliteBorder, null));
        }
    };

    Observer<Integer> observerIncorrectTV = new Observer<Integer>() {
        @Override
        public void onChanged(Integer i) {
            TextView tv;
            switch (i) {
                case AddNewDataVM.INCORRECT_EMPLOYEE_ET:
                    tv = findViewById(R.id.incorrectEmployerFocus);
                    break;
                case AddNewDataVM.INCORRECT_FIRM_ET:
                    tv = findViewById(R.id.incorrectFirmFocus);
                    break;
                case AddNewDataVM.INCORRECT_TYPE_ET:
                    tv = findViewById(R.id.incorrectToWFocus);
                    break;
                case AddNewDataVM.INCORRECT_PLACE_ET:
                    tv = findViewById(R.id.select_placeOfWork_editText);
                    break;
                case AddNewDataVM.INCORRECT_RES_VALUE_ET:
                    tv = findViewById(R.id.incorrectResultValueFocus);
                    break;
                default:
                    throw new RuntimeException("Опечатка в константах некорректно заполненных полей");
            }
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMarginStart(12);
            tv.setLayoutParams(lp);
        }
    };

    Observer<Boolean> observerAddButtonForPrepare = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean b) {
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
        }
    };

    Observer<String> observerAddButtonChangeText = new Observer<String>() {
        @Override
        public void onChanged(String s) {
            addButton.setText(s);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_data);
        Toolbar toolbar = findViewById(R.id.toolbar_list_of_emp);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setDisplayHomeAsUpEnabled(true);
        else throw new RuntimeException("Support ActionBar равен Null");
        model = new ViewModelProvider(this, new ViewModelFactoryAddNewData(this.getApplication())).get(AddNewDataVM.class);
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
        addEmpl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivityForResult(TABLE_OF_EMPLOYERS);
            }
        });
        Observer<String> employerEditTextObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                addEmpl.setText(s);
            }
        };
        model.getLiveDataEmployerText().observe(this, employerEditTextObserver);
        addFirm = findViewById(R.id.add_firm_editText);
        addFirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivityForResult(TABLE_OF_FIRMS);
            }
        });
        Observer<String> firmEditTextObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                addFirm.setText(s);
            }
        };
        model.getLiveDataFirmText().observe(this, firmEditTextObserver);
        addPlaceOfWork = findViewById(R.id.add_placeOfWork_editText);
        addPlaceOfWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivityForResult(TABLE_OF_PLACES_OF_WORK);
            }
        });
        Observer<String> pOWEditTextObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                addPlaceOfWork.setText(s);
            }
        };
        model.getLiveDataPoWText().observe(this, pOWEditTextObserver);
        addTypeOfWork = findViewById(R.id.add_typeOfWork_editText);
        addTypeOfWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivityForResult(TABLE_OF_TYPES_OF_WORK);
            }
        });
        Observer<String> tOWEditTextObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                addTypeOfWork.setText(s);
            }
        };
        model.getLiveDataToWText().observe(this, tOWEditTextObserver);
        date = findViewById(R.id.add_date_editText);
        date.setOnClickListener(pickDate);
        resultValue = findViewById(R.id.add_result_editText);
        resultValue.addTextChangedListener(twResultValue);
        note = findViewById(R.id.add_note_editText);
        note.addTextChangedListener(twNote);
        addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(addDataListener);
        model.getWarningEmployerETLD().observe(this, observerIncorrectET);
        model.getWarningEmployerTVLD().observe(this, observerIncorrectTV);
        model.getPrepareAddButton().observe(this, observerAddButtonForPrepare);
        model.getChangeTextAddButton().observe(this, observerAddButtonChangeText);
        if (model.isFirstLaunch()) {
            date.setText(convertLongToStringDate(Calendar.getInstance()));
            model.setDateForSql(Calendar.getInstance().getTimeInMillis());
            model.startGetEmployerThread();
            model.startGetFirmThread();
            model.startGetToWThread();
            model.startGetPoWThread();
            model.setFirstLaunchFalse();
        }
    }

    private void launchActivityForResult(int i) {
        Intent intent = new Intent(FillNewData_Activity.this, ListOfBDItemsActivity.class);
        intent.putExtra(LAUNCH_DEFINITELY_DB_TABLE, i);
        launcherActivityForDB.launch(intent);
    }

    private String convertLongToStringDate(Calendar c) {
        int dayOfMonth, month = 1;
        dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        month += c.get(Calendar.MONTH);
        StringBuilder sb = new StringBuilder();
        if (dayOfMonth < 10) sb.append("0");
        sb.append(dayOfMonth).append(".");
        if (month < 10) sb.append("0");
        sb.append(month).append(".");
        sb.append(c.get(Calendar.YEAR));
        return sb.toString();
    }

}
