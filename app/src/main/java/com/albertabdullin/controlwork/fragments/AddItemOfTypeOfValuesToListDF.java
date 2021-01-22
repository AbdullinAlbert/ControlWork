package com.albertabdullin.controlwork.fragments;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.activities.ProviderOfHolderFragmentState;
import com.albertabdullin.controlwork.recycler_views.AdapterForListOfTypeOfValues;
import com.albertabdullin.controlwork.viewmodels.MakerSearchCriteriaVM;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;


public class AddItemOfTypeOfValuesToListDF extends DialogFragment {
    private MakerSearchCriteriaVM model;
    private String mSign;
    private int mTypeOfValue;
    private AdapterForListOfTypeOfValues adapter;
    private static final String KEY_FOR_SIGN = "key_for_sign";
    private static final String KEY_FOR_TYPE_OF_VALUE = "key_for_type_of_value";
    public static final int EMPTY_LIST = 0;
    public static final int ADD_ITEM_TO_LIST = 1;
    public static final int DELETE_ITEM_FROM_LIST = 2;
    public static final int UPDATE_ITEM_FROM_LIST = 3;

    public AddItemOfTypeOfValuesToListDF() {}

    public AddItemOfTypeOfValuesToListDF(int typeOfValue, String sign) {
        mSign = sign;
        mTypeOfValue = typeOfValue;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(MakerSearchCriteriaVM.class);
        if (savedInstanceState != null) {
            mSign = savedInstanceState.getString(KEY_FOR_SIGN);
            mTypeOfValue = savedInstanceState.getInt(KEY_FOR_TYPE_OF_VALUE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_to_add_search_criteria_values_items, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (mTypeOfValue == SearchCriteriaFragment.DATES_VALUE) toolbar.setTitle("Добавь нужные даты");
        else toolbar.setTitle("Добавь нужные значения");
        final ImageView deleteImage = view.findViewById(R.id.imageView_to_delete_item);
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonDeleteDataDF commonDeleteDataDF = new CommonDeleteDataDF();
                int count = model.getListOfSelectedPositionForDeleteSign(mTypeOfValue, mSign).size();
                String header = getResources().getString(R.string.header_of_delete_dialog_fragment) + " " + count;
                commonDeleteDataDF.setHeader(header);
                if(count == 1) {
                    String mainText = "Вы действительно хотите удалить ";
                    int pos = model.getListOfSelectedPositionForDeleteSign(mTypeOfValue, mSign).get(0);
                    switch (mTypeOfValue) {
                        case SearchCriteriaFragment.DATES_VALUE:
                            mainText += model.getAdapterListOfCurrentSignForDate(mSign).get(pos);
                            break;
                        case SearchCriteriaFragment.NUMBERS_VALUE:
                            mainText += model.getAdapterListOfCurrentSignForNumber(mSign).get(pos);
                            break;
                        case SearchCriteriaFragment.NOTES_VALUE:
                            mainText += model.getAdapterListOfCurrentSignForNote(mSign).get(pos);
                            break;
                        default:
                            throw new RuntimeException("Опечатка в константах. Вызов удаления элементов списка. mTypeOfValue =" + mTypeOfValue);
                    }
                    commonDeleteDataDF.setMainText(mainText);
                }
                commonDeleteDataDF.setExecutor(new DeleteDataButtonClickExecutor() {
                    @Override
                    public void executeYesButtonClick(AppCompatActivity appCompatActivity) {
                        switch (mTypeOfValue) {
                            case SearchCriteriaFragment.DATES_VALUE:
                                ((MakerSearchCriteriaVM)((ProviderOfHolderFragmentState)appCompatActivity).getHolder())
                                        .deleteSearchCriteriaValueForDate(mSign);
                                break;
                            case SearchCriteriaFragment.NUMBERS_VALUE:
                                ((MakerSearchCriteriaVM)((ProviderOfHolderFragmentState)appCompatActivity).getHolder())
                                        .deleteSearchCriteriaValueForNumber(mSign);
                                break;
                            case SearchCriteriaFragment.NOTES_VALUE:
                                ((MakerSearchCriteriaVM)((ProviderOfHolderFragmentState)appCompatActivity).getHolder())
                                        .deleteSearchCriteriaValueForNote(mSign);
                                break;
                            default:
                                throw new RuntimeException("Опечатка в константах. Метод void executeYesButtonClick. mTypeOfValue =" + mTypeOfValue);
                        }
                    }

                    @Override
                    public void executeNoButtonClick(AppCompatActivity appCompatActivity) {
                    }
                });
                commonDeleteDataDF.show(requireActivity().getSupportFragmentManager(), "delete_item_df");
            }
        });
        Observer<Boolean> observerOfDeleteImage = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) deleteImage.setVisibility(View.VISIBLE);
                else deleteImage.setVisibility(View.INVISIBLE);
                deleteImage.setClickable(aBoolean);
            }
        };
        final List<String> hList;
        if (mTypeOfValue == SearchCriteriaFragment.DATES_VALUE) hList = model.getAdapterListOfCurrentSignForDate(mSign);
        else if (mTypeOfValue == SearchCriteriaFragment.NUMBERS_VALUE) hList = model.getAdapterListOfCurrentSignForNumber(mSign);
        else hList = model.getAdapterListOfCurrentSignForNote(mSign);
        adapter = new AdapterForListOfTypeOfValues(mTypeOfValue, hList, model, mSign, requireActivity());
        RecyclerView rv = view.findViewById(R.id.recyclerView);
        rv.setAdapter(adapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(requireActivity());
        rv.setLayoutManager(mLayoutManager);
        DividerItemDecoration divider = new DividerItemDecoration
                (new android.view.ContextThemeWrapper(requireActivity(), R.style.AppTheme), mLayoutManager.getOrientation());
        rv.addItemDecoration(divider);
        Observer<Integer> adapterListObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer) {
                    case EMPTY_LIST:
                        adapter.notifyDataSetChanged();
                        break;
                    case ADD_ITEM_TO_LIST:
                        adapter.notifyItemInserted(hList.size() - 1);
                        break;
                    case DELETE_ITEM_FROM_LIST:
                        List<Integer> hList = model.getListOfSelectedPositionForDeleteSign(mTypeOfValue, mSign);
                        if (hList != null) {
                            for (int i = hList.size() - 1; i > -1; i--) {
                                adapter.notifyItemRemoved(hList.get(i));
                            }
                            hList.clear();
                        }
                        break;
                    case UPDATE_ITEM_FROM_LIST:
                        adapter.notifyItemChanged(model.getPositionOfUpdatedItem(mTypeOfValue));
                        break;
                }
            }
        };
        switch (mTypeOfValue) {
            case SearchCriteriaFragment.DATES_VALUE:
                switch (mSign) {
                    case "=":
                        model.getDeleteImageViewOnDialogEqualitySignDateLD().observe(getViewLifecycleOwner(), observerOfDeleteImage);
                        model.getAdapterListOfOneDateForEqualitySignLD().observe(getViewLifecycleOwner(), adapterListObserver);
                        break;
                    case "\u2260":
                        model.getDeleteImageViewOnDialogInequalitySignDateLD().observe(getViewLifecycleOwner(), observerOfDeleteImage);
                        model.getAdapterListOfOneDateForInequalitySignLD().observe(getViewLifecycleOwner(), adapterListObserver);
                        break;
                    case ("\u2a7e" + " " + "\u2a7d"):
                        model.getDeleteImageViewOnDialogMoreAndLessSignsDateLD().observe(getViewLifecycleOwner(), observerOfDeleteImage);
                        model.getAdapterListOfRangeOfDatesForMoreAndLessSignsLD().observe(getViewLifecycleOwner(), adapterListObserver);
                        break;
                }
                break;
            case SearchCriteriaFragment.NUMBERS_VALUE:
                switch (mSign) {
                    case "=":
                        model.getDeleteImageViewOnDialogEqualitySignNumberLD().observe(getViewLifecycleOwner(), observerOfDeleteImage);
                        model.getAdapterListOfOneNumberForEqualitySignLD().observe(getViewLifecycleOwner(), adapterListObserver);
                        break;
                    case "\u2260":
                        model.getDeleteImageViewOnDialogInequalitySignNumberLD().observe(getViewLifecycleOwner(), observerOfDeleteImage);
                        model.getAdapterListOfOneNumberForInequalitySignLD().observe(getViewLifecycleOwner(), adapterListObserver);
                        break;
                    case ("\u2a7e" + " " + "\u2a7d"):
                        model.getDeleteImageViewOnDialogMoreAndLessSignsNumberLD().observe(getViewLifecycleOwner(), observerOfDeleteImage);
                        model.getAdapterListOfRangeOfNumbersForMoreAndLessSignsLD().observe(getViewLifecycleOwner(), adapterListObserver);
                        break;
                }
                break;
            case SearchCriteriaFragment.NOTES_VALUE:
                switch (mSign) {
                    case "=":
                        model.getDeleteImageViewOnDialogEqualitySignNoteLD().observe(getViewLifecycleOwner(), observerOfDeleteImage);
                        model.getAdapterListOfNoteForEqualitySignLD().observe(getViewLifecycleOwner(), adapterListObserver);
                        break;
                    case "\u2260":
                        model.getDeleteImageViewOnDialogInequalitySignNoteLD().observe(getViewLifecycleOwner(), observerOfDeleteImage);
                        model.getAdapterListOfNoteForInequalitySignLD().observe(getViewLifecycleOwner(), adapterListObserver);
                        break;
                }
                break;

        }
        FloatingActionButton fab = view.findViewById(R.id.fab_to_add_one_date);
        switch (mTypeOfValue) {
            case SearchCriteriaFragment.DATES_VALUE:
                if (("\u2a7e" + " " + "\u2a7d").equals(mSign))
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
                            MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();
                            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                                @Override
                                public void onPositiveButtonClick(Pair<Long, Long> selection) {
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTimeInMillis(selection.first);
                                    String beginOfRangeDate = SearchCriteriaFragment.getStringViewOfDate(calendar);
                                    calendar.setTimeInMillis(selection.second);
                                    String endOfRangeDate = SearchCriteriaFragment.getStringViewOfDate(calendar);
                                    model.addItemToDateList(mSign, beginOfRangeDate, endOfRangeDate);
                                    model.addSearchCriteria(SearchCriteriaFragment.DATES_VALUE,
                                            model.getPositionOfSign(mTypeOfValue, mSign), selection.first, selection.second);
                                }
                            });
                            materialDatePicker.show(requireActivity().getSupportFragmentManager(), "date_picker");
                        }
                    });
                else fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                        MaterialDatePicker<Long> materialDatePicker = builder.build();
                        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                            @Override
                            public void onPositiveButtonClick(Long selection) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(selection);
                                String date = SearchCriteriaFragment.getStringViewOfDate(calendar);
                                model.addItemToDateList(mSign, date, null);
                                model.addSearchCriteria(SearchCriteriaFragment.DATES_VALUE,
                                        model.getPositionOfSign(mTypeOfValue, mSign), selection, null);
                            }
                        });
                        materialDatePicker.show(requireActivity().getSupportFragmentManager(), "date_picker");
                    }
                });
                break;
            case SearchCriteriaFragment.NUMBERS_VALUE:
                if (("\u2a7e" + " " + "\u2a7d").equals(mSign))
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AddItemOfPairOfNumbersValueDF addItemOfPairOfNumbersValueDF = new AddItemOfPairOfNumbersValueDF(mSign, null);
                            addItemOfPairOfNumbersValueDF.show(requireActivity().getSupportFragmentManager(), "add number value");
                        }
                    });
                else fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonAddDataDF commonAddDataDF = new CommonAddDataDF()
                                .setHint(requireContext().getResources().getString(R.string.hint_for_insert_number_for_search_criteria))
                                .setInputType(CommonAddDataDF.EditTextInputType.NUMBER_DECIMAL)
                                .setLengthOfText(getResources().getInteger(R.integer.max_digit_length_of_value))
                                .setExecutor(new InsertDataButtonClickExecutor() {
                                    @Override
                                    public void executeYesButtonClick(AppCompatActivity activity, String text) {
                                        if (text.length() != 0) {
                                            MakerSearchCriteriaVM localVM =
                                                    (MakerSearchCriteriaVM)((ProviderOfHolderFragmentState)activity).getHolder();
                                            localVM.addItemToNumberList(mSign, text, null);
                                            localVM.addSearchCriteria(SearchCriteriaFragment.NUMBERS_VALUE,
                                                    localVM.getPositionOfSign(SearchCriteriaFragment.NUMBERS_VALUE, mSign),
                                                    Float.parseFloat(text), null);
                                        } else {
                                            Toast toast = Toast.makeText(requireContext(),
                                                    "Нельзя добавлять пустые строки", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    }
                                    @Override
                                    public void executeNoButtonClick() {
                                    }
                                });
                        commonAddDataDF.show(requireActivity().getSupportFragmentManager(), "newData");
                    }
                });
                break;
            case SearchCriteriaFragment.NOTES_VALUE:
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonAddDataDF commonAddDataDF = new CommonAddDataDF()
                                .setHint(requireContext().getResources().getString(R.string.hint_for_insert_number_for_search_criteria))
                                .setInputType(CommonAddDataDF.EditTextInputType.TEXT_PERSON_NAME)
                                .setLengthOfText(30)
                                .setExecutor(new InsertDataButtonClickExecutor() {
                                    @Override
                                    public void executeYesButtonClick(AppCompatActivity activity, String text) {
                                        if (text.length() != 0) {
                                            MakerSearchCriteriaVM localVM =
                                                    (MakerSearchCriteriaVM)((ProviderOfHolderFragmentState)activity).getHolder();
                                            localVM.addItemToNoteList(mSign, text);
                                            localVM.addSearchCriteria(SearchCriteriaFragment.NOTES_VALUE,
                                                    localVM.getPositionOfSign(SearchCriteriaFragment.NOTES_VALUE, mSign),
                                                    text, null);
                                        } else {
                                            Toast toast = Toast.makeText(requireContext(),
                                                    "Нельзя добавлять пустые строки", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    }
                                    @Override
                                    public void executeNoButtonClick() {
                                    }
                                });
                        commonAddDataDF.show(requireActivity().getSupportFragmentManager(), "newData");
                    }
                });
                break;
        }

        Button oKButton = view.findViewById(R.id.ok_button);
        oKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireDialog().dismiss();
                switch (mTypeOfValue) {
                    case SearchCriteriaFragment.DATES_VALUE:
                        String stringViewOfDate = model.createStringViewOfDate(mSign);
                        model.setSelectedSignAndStringViewOfDate(mSign, stringViewOfDate);
                        break;
                    case SearchCriteriaFragment.NUMBERS_VALUE:
                        String stringViewOfNumber = model.createStringViewOfNumber(mSign);
                        model.setSelectedSignAndStringViewOfNumber(mSign, stringViewOfNumber);
                        break;
                    case SearchCriteriaFragment.NOTES_VALUE:
                        String stringViewOfNote = model.createStringViewOfNote(mSign);
                        model.setSelectedSignAndStringViewOfNote(mSign, stringViewOfNote);
                        break;
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_FOR_SIGN, mSign);
        outState.putInt(KEY_FOR_TYPE_OF_VALUE, mTypeOfValue);
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.95), (int) (size.y * 0.80));
        window.setGravity(Gravity.CENTER);
        super.onResume();
    }

}
