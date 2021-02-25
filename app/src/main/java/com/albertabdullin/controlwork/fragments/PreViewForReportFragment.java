package com.albertabdullin.controlwork.fragments;

import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.activities.ReportActivity;

public class PreViewForReportFragment extends DeleteDataFragment {

    public PreViewForReportFragment() { }

    public PreViewForReportFragment(String query) {
        super(query);
    }

    @Override
    protected void initializeSelectionTracker() { }

    @Override
    protected void addingListenerToBackPressedNotifier() { }

    @Override
    protected void inflateToolbarMenu(Toolbar toolbar) {
        toolbar.inflateMenu(R.menu.create_report);
        MenuItem menuItem = toolbar.getMenu().getItem(0);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_create_report_item) {
                    ((ReportActivity)requireActivity()).setResultList(mViewModel.getResultList());
                    ((ReportActivity)requireActivity()).launchCreatingReport();
                    requireActivity().onBackPressed();
                    return true;
                }
                return false;
            }
        });
    }

}