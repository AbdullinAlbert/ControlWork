package com.albertabdullin.controlwork.recycler_views;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.models.ComplexEntityForDB;
import com.albertabdullin.controlwork.models.PairOfItemPositions;
import com.albertabdullin.controlwork.viewmodels.ReportViewModel;

import java.util.List;

import static com.albertabdullin.controlwork.R.color.colorAccent;

public class AdapterForReportResultList extends RecyclerView.Adapter<AdapterForReportResultList.MyViewHolder> {

    protected final List<ComplexEntityForDB> mListOfRecyclerView;
    protected View[] arrayOfViews;
    protected final ReportViewModel mViewModel;
    protected final LifecycleOwner mLifeCycleOwner;
    protected final Fragment mParentFragment;
    private final int dataItemFromDB = 0;
    private final int towItem = 1;
    private final int resultItem = 2;

    public AdapterForReportResultList(List<ComplexEntityForDB> list, ReportViewModel viewModel,
                                      LifecycleOwner lifecycleOwner, Fragment parentFragment) {

        mListOfRecyclerView = list;
        mViewModel = viewModel;
        mLifeCycleOwner = lifecycleOwner;
        mParentFragment = parentFragment;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final View view;
        private TextView date;
        private TextView dbResult;
        private TextView totalResult;
        private TextView type_of_totalResult;
        private TextView dbType_of_result;
        private TextView place_of_work;
        private TextView typeOfWork;
        private TextView resultDescription;
        private final View.OnClickListener clickListener = v -> {
            mViewModel.notifyEditTexts(getAbsoluteAdapterPosition());
            mViewModel.changeColorOfPreviousSelectedItem(
                    new PairOfItemPositions(getAbsoluteAdapterPosition()));
        };

        public MyViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            view = itemView;

            switch (viewType) {
                case dataItemFromDB:
                    date = view.findViewById(R.id.cell_for_date_from_db);
                    place_of_work = view.findViewById(R.id.cell_for_place_of_work_from_db);
                    dbResult = view.findViewById(R.id.cell_for_result_from_db);
                    dbType_of_result = view.findViewById(R.id.cell_for_type_of_result_from_db);
                    break;
                case resultItem:
                    resultDescription = view.findViewById(R.id.resultDescription_TextView);
                    totalResult = view.findViewById(R.id.cell_for_result);
                    type_of_totalResult = view.findViewById(R.id.cell_for_type_of_result);
                    break;
                default:
                    typeOfWork = view.findViewById(R.id.tow_description_textView);
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (mListOfRecyclerView.get(position).isResultEntity()) return resultItem;
        if (mListOfRecyclerView.get(position).isTypeOfWorkEntity()) return towItem;
        return dataItemFromDB;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout;
        switch(viewType) {
            case dataItemFromDB:
                layout = R.layout.item_of_db_of_rv_report_list;
                break;
            case towItem:
                layout = R.layout.tow_item_of_rv_report_list;
                break;
            default:
                layout = R.layout.result_item_of_rv_report_list;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MyViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterForReportResultList.MyViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case dataItemFromDB:
                holder.view.setOnClickListener(holder.clickListener);
                holder.place_of_work.setText(mListOfRecyclerView.get(position).getPOWDescription());
                holder.date.setText(mListOfRecyclerView.get(position).getDate());
                holder.dbResult.setText(mListOfRecyclerView.get(position).getResult());
                holder.dbType_of_result.setText(mListOfRecyclerView.get(position).getStringViewOfResultType());
                if (arrayOfViews != null && arrayOfViews.length != 0) {
                    arrayOfViews[position] = holder.view;
                    if (holder.getBindingAdapterPosition() == mViewModel.getPosOfSelectedItem() || mViewModel.getPosOfSelectedItem() == -1)
                        changeColorOfItems();
                }
                if (position == mViewModel.getPosOfSelectedItem())
                    holder.view.setBackgroundColor(mParentFragment.getResources().getColor(colorAccent, null));
                if (position != mViewModel.getPosOfSelectedItem()) {
                    Drawable drawable = holder.view.getBackground();
                    if (drawable instanceof ColorDrawable && ((ColorDrawable) drawable).getColor() == mParentFragment.getResources().getColor(colorAccent, null))
                        holder.view.setBackgroundColor(mParentFragment.getResources().getColor(R.color.backgroundColorOfActivity, null));
                }
                break;
            case resultItem:
                holder.view.setOnClickListener(null);
                holder.totalResult.setText(mListOfRecyclerView.get(position).getStringViewOfTypeResultSum());
                holder.type_of_totalResult.setText(mListOfRecyclerView.get(position).getStringViewOfResultType());
                holder.resultDescription.setText(mListOfRecyclerView.get(position).getResultDescription());
                break;
            default:
                holder.view.setOnClickListener(null);
                holder.typeOfWork.setText(mListOfRecyclerView.get(position).getTOWDescription());
        }
    }

    @Override
    public int getItemCount() {
        return mListOfRecyclerView.size();
    }

    private void changeColorOfItems() {
        if (!mViewModel.getChangerColorOfViewHolder().hasObservers()) {
            Observer<PairOfItemPositions> observerOfChangerColorRVItem = pair -> {
                if (pair.getOldPos() != -1 && arrayOfViews[pair.getOldPos()] != null ) {
                    arrayOfViews[pair.getOldPos()].
                            setBackgroundColor(mParentFragment.getResources().getColor(R.color.backgroundColorOfActivity, null));
                }
                if (pair.getNewPos() != -1 && arrayOfViews[pair.getNewPos()] != null) {
                    arrayOfViews[pair.getNewPos()].setBackgroundColor(mParentFragment.getResources().getColor(colorAccent, null));
                }
            };
            mViewModel.getChangerColorOfViewHolder().observe(mLifeCycleOwner, observerOfChangerColorRVItem);
        }
    }

    public void initializeArrayOfViews() {
        if (arrayOfViews == null) arrayOfViews = new View[getItemCount()];
    }

}
