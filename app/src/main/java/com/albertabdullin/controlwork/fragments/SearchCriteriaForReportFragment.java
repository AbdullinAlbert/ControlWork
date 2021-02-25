package com.albertabdullin.controlwork.fragments;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.models.ComplexEntityForDB;
import com.albertabdullin.controlwork.models.DateConverter;
import com.albertabdullin.controlwork.viewmodels.MakerSearchCriteriaReportVM;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import static java.util.Calendar.DAY_OF_MONTH;

public class SearchCriteriaForReportFragment extends SearchCriteriaFragment {

    @Override
    protected void setTitleForToolBar(Toolbar toolbar) {
        toolbar.setTitle(R.string.search_criteria_for_report);
        toolbar.setSubtitle(getString(R.string.current_month));
        toolbar.inflateMenu(R.menu.show_report_preview_menu);
        MenuItem menuItem = toolbar.getMenu().getItem(0);
        if (((MakerSearchCriteriaReportVM)mViewModel).isNeedPreView()) menuItem.setChecked(true);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.show_report_preview) {
                boolean b = ((MakerSearchCriteriaReportVM)mViewModel).isNeedPreView();
                item.setChecked(!b);
                ((MakerSearchCriteriaReportVM)mViewModel).setNeedPreView(!b);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void setDateSearchCriteria() {
        selectedDateEditText.setClickable(false);
        selectedDateEditText.setFocusable(false);
        if (mViewModel.isSearchCriteriaForDateNull()) {
            String dateWithoutDay;
            String beginOfMonthDate;
            String endOfMonthDate;
            Calendar calendar = Calendar.getInstance();
            beginOfMonthDate = calendar.getActualMinimum(DAY_OF_MONTH) + ".";
            if (beginOfMonthDate.length() == 2) beginOfMonthDate = "0" + beginOfMonthDate;
            endOfMonthDate = calendar.getActualMaximum(DAY_OF_MONTH) + ".";
            dateWithoutDay = (calendar.get(Calendar.MONTH) + 1) + ".";
            if (dateWithoutDay.length() == 2) dateWithoutDay = "0" + dateWithoutDay;
            dateWithoutDay += calendar.get(Calendar.YEAR);
            beginOfMonthDate = beginOfMonthDate + dateWithoutDay;
            endOfMonthDate = endOfMonthDate + dateWithoutDay;
            long beginOfMonth;
            long endOfMonth;
            try {
                beginOfMonth = DateConverter.convertStringDateToLong(beginOfMonthDate);
                endOfMonth = DateConverter.convertStringDateToLong(endOfMonthDate);
            } catch (ParseException e) {
                Toast.makeText(requireContext(), getString(R.string.cannot_convert_string_to_date) +
                        ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            mViewModel.addSearchCriteria(DATES_VALUE, null ,beginOfMonth, endOfMonth);
            mViewModel.addItemToDateList(("\u2a7e" + " " + "\u2a7d"), beginOfMonthDate, endOfMonthDate);
        }
        selectedDateEditText.setText(mViewModel.createStringViewOfDate("\u2a7e" + " " + "\u2a7d"));
    }

    @Override
    protected void setTextToSearchButton(Button button) {
        button.setText(R.string.create_report);
    }

    @Override
    protected void startViewForResult(String query) {
        if (((MakerSearchCriteriaReportVM) mViewModel).isNeedPreView()) {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            PreViewForReportFragment preViewForReportFragment = new PreViewForReportFragment(query);
            transaction.replace(R.id.container_for_monthly_report_fragments, preViewForReportFragment,
                    getResources().getString(R.string.tag_for_preview_for_report_fragment)).
                    addToBackStack(null).commit();
        } else launchCreatingReport();
    }

    public void launchCreatingReport() {
        ((MakerSearchCriteriaReportVM) mViewModel).launchCreatingReport();
    }

    public void setResultList(List<ComplexEntityForDB> resultList) {
        ((MakerSearchCriteriaReportVM) mViewModel).setResultList(resultList);
    }

}
