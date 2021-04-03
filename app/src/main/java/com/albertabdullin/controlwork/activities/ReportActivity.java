package com.albertabdullin.controlwork.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.fragments.CommonAddDataDF;
import com.albertabdullin.controlwork.fragments.CommonAddPairOfNumbersValueDF;
import com.albertabdullin.controlwork.fragments.CommonDeleteDataDF;
import com.albertabdullin.controlwork.fragments.DeleteDataButtonClickExecutor;
import com.albertabdullin.controlwork.fragments.InsertDataButtonClickExecutor;
import com.albertabdullin.controlwork.fragments.InsertDataPairButtonClickExecutor;
import com.albertabdullin.controlwork.fragments.ReportPreViewFragment;
import com.albertabdullin.controlwork.fragments.SearchCriteriaForReportFragment;
import com.albertabdullin.controlwork.fragments.SearchCriteriaFragment;
import com.albertabdullin.controlwork.models.DateConverter;
import com.albertabdullin.controlwork.viewmodels.DialogFragmentStateHolder;
import com.albertabdullin.controlwork.viewmodels.MakerSearchCriteriaReportVM;
import com.albertabdullin.controlwork.viewmodels.MakerSearchCriteriaVM;
import com.albertabdullin.controlwork.viewmodels.ViewModelFactoryMakerSearchCriteriaReport;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.Calendar;

public class ReportActivity extends AppCompatActivity implements ProviderOfHolderFragmentState, DialogFragmentProvider, SearchCriteriaVMProvider {

    private MakerSearchCriteriaVM mViewModel;
    private SearchCriteriaForReportFragment mSearchCriteriaForReportFragment;

    public static Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_report);
        mViewModel = new ViewModelProvider(this,
                new ViewModelFactoryMakerSearchCriteriaReport(this.getApplication())).get(MakerSearchCriteriaReportVM.class);
        ((MakerSearchCriteriaReportVM)mViewModel).setSelectedPeriod(
                (SearchCriteriaForReportFragment.DateRange) getIntent().getSerializableExtra(MainActivity.KEY_FOR_REPORT_PERIOD));
        ReportPreViewFragment reportPreViewFragment = (ReportPreViewFragment) getSupportFragmentManager()
                .findFragmentByTag(getString(R.string.tag_report_preview_fragment));
        mSearchCriteriaForReportFragment = (SearchCriteriaForReportFragment) getSupportFragmentManager()
                .findFragmentByTag(getString(R.string.search_criteria_for_monthly_report_fragment));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (reportPreViewFragment != null) {
            transaction.replace(R.id.container_for_monthly_report_fragments, reportPreViewFragment,
                    getString(R.string.tag_report_preview_fragment));
            return;
        }
        if (mSearchCriteriaForReportFragment != null) {
            transaction.replace(R.id.container_for_monthly_report_fragments, mSearchCriteriaForReportFragment,
                    getString(R.string.search_criteria_for_monthly_report_fragment));
        } else {
            mSearchCriteriaForReportFragment = new SearchCriteriaForReportFragment();
            transaction.add(R.id.container_for_monthly_report_fragments, mSearchCriteriaForReportFragment,
                    getString(R.string.search_criteria_for_monthly_report_fragment));
        }
        transaction.commit();
    }

    @Override
    public DialogFragmentStateHolder getHolder() {
        return mViewModel;
    }

    public void launchCreatingReport() {
        mSearchCriteriaForReportFragment.launchCreatingReport();
    }

    @Override
    public CommonAddDataDF getAddStringDataDialogFragment() {
        return new CommonAddDataDF()
                .setHint(getString(R.string.hint_for_insert_number_for_search_criteria))
                .setInputType(CommonAddDataDF.EditTextInputType.TEXT_PERSON_NAME)
                .setLengthOfText(getResources().getInteger(R.integer.max_length_of_string_value))
                .setExecutor(new InsertDataButtonClickExecutor() {
                    @Override
                    public void executeYesButtonClick(AppCompatActivity activity, String text) {
                        if (text.length() != 0) {
                            mViewModel.addItemToNoteList(mViewModel.getCommonSelectedSign(), text);
                            mViewModel.addSearchCriteria(SearchCriteriaFragment.NOTES_VALUE,
                                    mViewModel.getPositionOfSign(SearchCriteriaFragment.NOTES_VALUE, mViewModel.getCommonSelectedSign()),
                                    text, null);
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Нельзя добавлять пустые строки", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                    @Override
                    public void executeNoButtonClick() {
                    }
                });
    }

    @Override
    public CommonAddDataDF getAddStringDataDialogFragment(int selectedPosition) {
        return new CommonAddDataDF()
                .setHint(getString(R.string.hint_for_insert_number_for_search_criteria))
                .setInputType(CommonAddDataDF.EditTextInputType.TEXT_PERSON_NAME)
                .setLengthOfText(getResources().getInteger(R.integer.max_length_of_string_value))
                .setTextForEditText(mViewModel.getValueOfNote(mViewModel.getCommonSelectedSign(), selectedPosition))
                .setExecutor(new InsertDataButtonClickExecutor() {
                    @Override
                    public void executeYesButtonClick(AppCompatActivity activity, String text) {
                        if (text.length() != 0) {
                            mViewModel.changeItemToNoteList(mViewModel.getCommonSelectedSign(), selectedPosition, text);
                            mViewModel.changeSearchCriteria(SearchCriteriaFragment.NOTES_VALUE, mViewModel.getCommonSelectedSign(),
                                    selectedPosition, text, null);
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Нельзя добавлять пустые строки", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                    @Override
                    public void executeNoButtonClick() { }
                });
    }

    @Override
    public CommonDeleteDataDF getDeleteDataDialogFragment() {
        CommonDeleteDataDF commonDeleteDataDF = new CommonDeleteDataDF();
        int count = mViewModel.getListOfSelectedPositionForDeleteSign(mViewModel.getSelectedTypeOfValue(),
                mViewModel.getCommonSelectedSign()).size();
        String header = getResources().getString(R.string.selected_records_with_colon) + " " + count;
        commonDeleteDataDF.setHeader(header);
        if(count == 1) {
            String mainText = "Вы действительно хотите удалить ";
            int pos = mViewModel.getListOfSelectedPositionForDeleteSign(mViewModel.getSelectedTypeOfValue(),
                    mViewModel.getCommonSelectedSign()).get(0);
            switch (mViewModel.getSelectedTypeOfValue()) {
                case SearchCriteriaFragment.DATES_VALUE:
                    mainText += mViewModel.getAdapterListOfCurrentSignForDate(mViewModel.getCommonSelectedSign()).get(pos);
                    break;
                case SearchCriteriaFragment.NUMBERS_VALUE:
                    mainText += mViewModel.getAdapterListOfCurrentSignForNumber(mViewModel.getCommonSelectedSign()).get(pos);
                    break;
                case SearchCriteriaFragment.NOTES_VALUE:
                    mainText += mViewModel.getAdapterListOfCurrentSignForNote(mViewModel.getCommonSelectedSign()).get(pos);
                    break;
                default:
                    throw new RuntimeException("Опечатка в константах. Вызов удаления элементов списка. mTypeOfValue =" +
                            mViewModel.getSelectedTypeOfValue());
            }
            commonDeleteDataDF.setMainText(mainText);
        }
        commonDeleteDataDF.setExecutor(new DeleteDataButtonClickExecutor() {
            @Override
            public void executeYesButtonClick(AppCompatActivity appCompatActivity) {
                switch (mViewModel.getSelectedTypeOfValue()) {
                    case SearchCriteriaFragment.DATES_VALUE:
                        ((MakerSearchCriteriaVM)((ProviderOfHolderFragmentState)appCompatActivity).getHolder())
                                .deleteSearchCriteriaValueForDate(mViewModel.getCommonSelectedSign());
                        break;
                    case SearchCriteriaFragment.NUMBERS_VALUE:
                        ((MakerSearchCriteriaVM)((ProviderOfHolderFragmentState)appCompatActivity).getHolder())
                                .deleteSearchCriteriaValueForNumber(mViewModel.getCommonSelectedSign());
                        break;
                    case SearchCriteriaFragment.NOTES_VALUE:
                        ((MakerSearchCriteriaVM)((ProviderOfHolderFragmentState)appCompatActivity).getHolder())
                                .deleteSearchCriteriaValueForNote(mViewModel.getCommonSelectedSign());
                        break;
                    default:
                        throw new RuntimeException("Опечатка в константах. Метод void executeYesButtonClick. mTypeOfValue =" +
                                mViewModel.getSelectedTypeOfValue());
                }
            }

            @Override
            public void executeNoButtonClick(AppCompatActivity appCompatActivity) {
            }
        });
        return commonDeleteDataDF;
    }

    @Override
    public CommonAddPairOfNumbersValueDF getAddDataPairDialogFragment() {
        CommonAddPairOfNumbersValueDF commonAddPairOfNumbersValueDF = new CommonAddPairOfNumbersValueDF();
        commonAddPairOfNumbersValueDF.setExecutor(new InsertDataPairButtonClickExecutor() {
            @Override
            public void executeYesButtonClick(float firstNumber, float secondNumber) {
                mViewModel.addItemToNumberList(mViewModel.getCommonSelectedSign(), Float.toString(firstNumber),
                        Float.toString(secondNumber));
                mViewModel.addSearchCriteria(SearchCriteriaFragment.NUMBERS_VALUE,
                        mViewModel.getPositionOfSign(SearchCriteriaFragment.NUMBERS_VALUE, mViewModel.getCommonSelectedSign()), firstNumber, secondNumber);
            }

            @Override
            public void executeNoButtonClick() { }
        });
        return commonAddPairOfNumbersValueDF;
    }

    @Override
    public CommonAddPairOfNumbersValueDF getAddDataPairDialogFragment(int selectedPosition) {
        CommonAddPairOfNumbersValueDF commonAddPairOfNumbersValueDF = new CommonAddPairOfNumbersValueDF();
        commonAddPairOfNumbersValueDF.setTextForFirstField(mViewModel.getValueOfNumber(mViewModel.getCommonSelectedSign(), selectedPosition * 2));
        commonAddPairOfNumbersValueDF.setTextForSecondField(mViewModel.getValueOfNumber(mViewModel.getCommonSelectedSign(), selectedPosition * 2 + 1));
        commonAddPairOfNumbersValueDF.setExecutor(new InsertDataPairButtonClickExecutor() {
            @Override
            public void executeYesButtonClick(float firstNumber, float secondNumber) {
                mViewModel.changeItemToOneNumberList(mViewModel.getCommonSelectedSign(), selectedPosition, Float.toString(firstNumber), Float.toString(secondNumber));
                mViewModel.changeSearchCriteria(SearchCriteriaFragment.NUMBERS_VALUE,
                        mViewModel.getCommonSelectedSign(), selectedPosition * 2, firstNumber, secondNumber);
            }

            @Override
            public void executeNoButtonClick() { }
        });
        return commonAddPairOfNumbersValueDF;
    }

    @Override
    public CommonAddDataDF getAddNumberDataDialogFragment() {
        return new CommonAddDataDF()
                .setHint(getResources().getString(R.string.hint_for_insert_number_for_search_criteria))
                .setInputType(CommonAddDataDF.EditTextInputType.NUMBER_DECIMAL)
                .setLengthOfText(getResources().getInteger(R.integer.max_digit_length_of_value))
                .setExecutor(new InsertDataButtonClickExecutor() {
                    @Override
                    public void executeYesButtonClick(AppCompatActivity activity, String text) {
                        if (text.length() != 0) {
                            mViewModel.addItemToNumberList(mViewModel.getCommonSelectedSign(), text, null);
                            mViewModel.addSearchCriteria(SearchCriteriaFragment.NUMBERS_VALUE,
                                    mViewModel.getPositionOfSign(SearchCriteriaFragment.NUMBERS_VALUE, mViewModel.getCommonSelectedSign()),
                                    Float.parseFloat(text), null);
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Нельзя добавлять пустые строки", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                    @Override
                    public void executeNoButtonClick() {
                    }
                });
    }

    @Override
    public CommonAddDataDF getAddNumberDataDialogFragment(int selectedPosition) {
        return new CommonAddDataDF()
                .setHint(getString(R.string.hint_for_insert_number_for_search_criteria))
                .setInputType(CommonAddDataDF.EditTextInputType.NUMBER_DECIMAL)
                .setLengthOfText(getResources().getInteger(R.integer.max_digit_length_of_value))
                .setTextForEditText(mViewModel.getValueOfNumber(mViewModel.getCommonSelectedSign(), selectedPosition))
                .setExecutor(new InsertDataButtonClickExecutor() {
                    @Override
                    public void executeYesButtonClick(AppCompatActivity activity, String text) {
                        if (text.length() != 0) {
                            MakerSearchCriteriaVM localVM =
                                    (MakerSearchCriteriaVM)((ProviderOfHolderFragmentState)activity).getHolder();
                            localVM.changeItemToOneNumberList(mViewModel.getCommonSelectedSign(), selectedPosition, text, null);
                            localVM.changeSearchCriteria(SearchCriteriaFragment.NUMBERS_VALUE, mViewModel.getCommonSelectedSign(),
                                    selectedPosition, text, null);
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Нельзя добавлять пустые строки", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                    @Override
                    public void executeNoButtonClick() {
                    }
                });
    }

    @Override
    public MaterialDatePicker<Pair<Long, Long>> getDateRangeDialogFragment() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection.first);
            String beginOfRangeDate = DateConverter.getStringViewOfDate(calendar);
            calendar.setTimeInMillis(selection.second);
            String endOfRangeDate = DateConverter.getStringViewOfDate(calendar);
            mViewModel.addItemToDateList(mViewModel.getCommonSelectedSign(), beginOfRangeDate, endOfRangeDate);
            mViewModel.addSearchCriteria(SearchCriteriaFragment.DATES_VALUE,
                    mViewModel.getPositionOfSign(SearchCriteriaFragment.DATES_VALUE, mViewModel.getCommonSelectedSign()),
                    selection.first, selection.second);
        });
        return materialDatePicker;
    }

    @Override
    public MaterialDatePicker<Pair<Long, Long>> getDateRangeDialogFragment(int posOfDateRangeBegin, int posOfDateRangeEnd) {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        Long firstSelect = mViewModel.getSelection(mViewModel.getCommonSelectedSign(), posOfDateRangeBegin);
        Long secondSelect = mViewModel.getSelection(mViewModel.getCommonSelectedSign(), posOfDateRangeEnd);
        builder.setSelection(new Pair<>(firstSelect, secondSelect));
        MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection.first);
            String beginOfRangeDate = DateConverter.getStringViewOfDate(calendar);
            calendar.setTimeInMillis(selection.second);
            String endOfRangeDate = DateConverter.getStringViewOfDate(calendar);
            mViewModel.changeItemInDateList(mViewModel.getCommonSelectedSign(), posOfDateRangeBegin / 2,
                    beginOfRangeDate, endOfRangeDate);
            mViewModel.changeSearchCriteria(SearchCriteriaFragment.DATES_VALUE,
                    mViewModel.getCommonSelectedSign(), posOfDateRangeBegin, selection.first, selection.second);
        });
        return materialDatePicker;
    }

    @Override
    public MaterialDatePicker<Long> getDateDialogFragment() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        MaterialDatePicker<Long> materialDatePicker = builder.build();
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            String date = DateConverter.getStringViewOfDate(calendar);
            mViewModel.addItemToDateList(mViewModel.getCommonSelectedSign(), date, null);
            mViewModel.addSearchCriteria(SearchCriteriaFragment.DATES_VALUE,
                    mViewModel.getPositionOfSign(SearchCriteriaFragment.DATES_VALUE, mViewModel.getCommonSelectedSign()), selection, null);
        });
        return materialDatePicker;
    }

    @Override
    public MaterialDatePicker<Long> getDateDialogFragment(int selectedDate) {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setSelection(mViewModel.getSelection(mViewModel.getCommonSelectedSign(), selectedDate));
        MaterialDatePicker<Long> materialDatePicker = builder.build();
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            String date = DateConverter.getStringViewOfDate(calendar);
            mViewModel.changeItemInDateList(mViewModel.getCommonSelectedSign(), selectedDate, date, null);
            mViewModel.changeSearchCriteria(SearchCriteriaFragment.DATES_VALUE,
                    mViewModel.getCommonSelectedSign(), selectedDate, selection, null);
        });
        return materialDatePicker;
    }

    @Override
    public MakerSearchCriteriaVM getMakerSearchCriteriaVM() {
        return mViewModel;
    }
}
