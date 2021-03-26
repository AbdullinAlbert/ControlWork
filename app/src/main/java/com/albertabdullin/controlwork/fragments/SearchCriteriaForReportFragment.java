package com.albertabdullin.controlwork.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.view.MenuItem;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.models.ComplexEntityForDB;
import com.albertabdullin.controlwork.models.DateConverter;
import com.albertabdullin.controlwork.viewmodels.MakerSearchCriteriaReportVM;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import static java.util.Calendar.DAY_OF_MONTH;


public class SearchCriteriaForReportFragment extends SearchCriteriaFragment {

    public enum DateRange {
        MONTH, YEAR, CERTAIN_PERIOD;
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        ((MakerSearchCriteriaReportVM) mViewModel).setHasAppPermission(isGranted);
        ((MakerSearchCriteriaReportVM) mViewModel).launchCreatingReport();
    });

    private class DateProvider {

        String mStringDateBegin;
        String mStringDateEnd;

        DateProvider(DateRange dateRange) {
            Calendar calendar = Calendar.getInstance();
            switch (dateRange) {
                case MONTH:
                    mStringDateBegin = calendar.getActualMinimum(DAY_OF_MONTH) + ".";
                    if (mStringDateBegin.length() == 2) mStringDateBegin = "0" + mStringDateBegin;
                    mStringDateEnd = calendar.getActualMaximum(DAY_OF_MONTH) + ".";
                    String monthAndYear = (calendar.get(Calendar.MONTH) + 1) + ".";
                    if (monthAndYear.length() == 2) monthAndYear = "0" + monthAndYear;
                    monthAndYear += calendar.get(Calendar.YEAR);
                    mStringDateBegin += monthAndYear;
                    mStringDateEnd += monthAndYear;
                    break;
                case YEAR:
                    mStringDateBegin = "01.01." + calendar.get(Calendar.YEAR);
                    mStringDateEnd = "31.12." + calendar.get(Calendar.YEAR);
                    break;
            }
        }

        long getLongDateBegin() {
            long date;
            try {
               date = DateConverter.convertStringDateToLong(mStringDateBegin);
            } catch (ParseException parseException) {
                throw new RuntimeException(getString(R.string.cannot_convert_string_to_date) + ": " + parseException.getMessage());
            }
            return date;
        }

        long getLongDateEnd() {
            long date;
            try {
                date = DateConverter.convertStringDateToLong(mStringDateEnd);
            } catch (ParseException parseException) {
                throw new RuntimeException(getString(R.string.cannot_convert_string_to_date) + ": " + parseException.getMessage());
            }
            return date;
        }
    }

    @Override
    protected void setTitleForToolBar(Toolbar toolbar) {
        toolbar.setTitle(R.string.search_criteria_for_report);
        toolbar.setSubtitle(getSubTitleText());
        toolbar.inflateMenu(R.menu.show_report_preview_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.show_report_preview) {
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                PreViewForReportFragment preViewForReportFragment = new PreViewForReportFragment(mViewModel.createQuery());
                transaction.replace(R.id.container_for_monthly_report_fragments, preViewForReportFragment,
                        getResources().getString(R.string.tag_for_preview_for_report_fragment)).
                        addToBackStack(null).commit();
                return true;
            }
            return false;
        });
    }

    private String getSubTitleText() {
        switch (((MakerSearchCriteriaReportVM)mViewModel).getSelectedPeriod()) {
            case MONTH: return getString(R.string.current_month);
            case YEAR: return getString(R.string.current_year);
            default: return getString(R.string.certain_period);
        }
    }

    @Override
    protected void setDateSearchCriteria() {
        switch (((MakerSearchCriteriaReportVM)mViewModel).getSelectedPeriod()) {
            case MONTH:
                setDateSearchCriteriaForDefaultPeriod(DateRange.MONTH);
                break;
            case YEAR:
                setDateSearchCriteriaForDefaultPeriod(DateRange.YEAR);
                break;
            case CERTAIN_PERIOD:
                super.setDateSearchCriteria();
        }

    }

    private void setDateSearchCriteriaForDefaultPeriod(DateRange dateRange) {
        selectedDateEditText.setClickable(false);
        selectedDateEditText.setFocusable(false);
        if (mViewModel.isSearchCriteriaForDateNull()) {
            DateProvider dateProvider = new DateProvider(dateRange);
            mViewModel.addSearchCriteria(DATES_VALUE, null ,dateProvider.getLongDateBegin(), dateProvider.getLongDateEnd());
            mViewModel.addItemToDateList(("\u2a7e" + " " + "\u2a7d"), dateProvider.mStringDateBegin, dateProvider.mStringDateEnd);
        }
        selectedDateEditText.setText(mViewModel.createStringViewOfDate("\u2a7e" + " " + "\u2a7d"));
    }

    @Override
    protected void setTextToSearchButton(Button button) {
        button.setText(R.string.create_report);
    }

    @Override
    protected void startViewForResult(String query) {
        launchCreatingReport();
    }

    public void launchCreatingReport() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            ((MakerSearchCriteriaReportVM) mViewModel).setHasAppPermission(true);
            ((MakerSearchCriteriaReportVM) mViewModel).launchCreatingReport();
        } else requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

}
