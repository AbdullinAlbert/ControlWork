package com.albertabdullin.controlwork.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.viewmodels.MakerSearchCriteriaReportVM;

public class SearchCriteriaForReportFragment extends SearchCriteriaFragment {

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        ((MakerSearchCriteriaReportVM) mViewModel).setHasAppPermission(isGranted);
        ((MakerSearchCriteriaReportVM) mViewModel).launchCreatingReport();
    });


    @Override
    protected void setTitleForToolBar(Toolbar toolbar) {
        toolbar.setTitle(getString(R.string.search_criteria));
        toolbar.setSubtitle(getString(R.string.for_report));
        toolbar.inflateMenu(R.menu.show_report_preview_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.show_report_preview) {
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                ReportPreViewFragment reportPreViewFragment = new ReportPreViewFragment(mViewModel.createQuery());
                transaction.replace(R.id.container_for_monthly_report_fragments, reportPreViewFragment,
                        getResources().getString(R.string.tag_report_preview_fragment)).
                        addToBackStack(null).commit();
                return true;
            }
            return false;
        });
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
