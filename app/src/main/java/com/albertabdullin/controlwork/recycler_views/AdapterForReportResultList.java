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

    public AdapterForReportResultList(List<ComplexEntityForDB> list, ReportViewModel viewModel,
                                      LifecycleOwner lifecycleOwner, Fragment parentFragment) {

        mListOfRecyclerView = list;
        mViewModel = viewModel;
        mLifeCycleOwner = lifecycleOwner;
        mParentFragment = parentFragment;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private final TextView date;
        private final TextView result;
        private final TextView type_of_result;
        private final TextView place_of_work;
        private final TextView firstBorder;
        private final TextView secondBorder;
        private final TextView thirdBorder;
        private final TextView fourthBorder;
        private final TextView fifthBorder;
        private final TextView sixthBorder;
        private final TextView textViewForToW;
        private View.OnClickListener clickListener = v -> {
            mViewModel.notifyEditTexts(getAbsoluteAdapterPosition());
            mViewModel.changeColorOfPreviousSelectedItem(
                    new PairOfItemPositions(getAbsoluteAdapterPosition()));
        };

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            view.setOnClickListener(clickListener);
            firstBorder = view.findViewById(R.id.first_border);
            secondBorder = view.findViewById(R.id.second_border);
            thirdBorder = view.findViewById(R.id.third_border);
            fourthBorder = view.findViewById(R.id.fourth_border);
            fifthBorder = view.findViewById(R.id.fifth_border);
            sixthBorder = view.findViewById(R.id.sixth_border);
            textViewForToW = view.findViewById(R.id.type_of_work_TextView);
            type_of_result = view.findViewById(R.id.cell_for_type_of_result);
            date = view.findViewById(R.id.cell_for_date);
            result = view.findViewById(R.id.cell_for_result);
            place_of_work = view.findViewById(R.id.cell_for_place_of_work);
        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_of_rv_report_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterForReportResultList.MyViewHolder holder, int position) {
        if (mListOfRecyclerView.get(position).isResultEntity() || mListOfRecyclerView.get(position).isTypeOfWorkEntity()) {
            holder.view.setOnClickListener(null);
            holder.firstBorder.setVisibility(View.GONE);
            holder.secondBorder.setVisibility(View.GONE);
            holder.thirdBorder.setVisibility(View.GONE);
            holder.fourthBorder.setVisibility(View.GONE);
            holder.fifthBorder.setVisibility(View.GONE);
            holder.sixthBorder.setVisibility(View.GONE);
            holder.date.setVisibility(View.GONE);
            holder.type_of_result.setVisibility(View.GONE);
            if (mListOfRecyclerView.get(position).isResultEntity()) {
                holder.place_of_work.setText("Общая сумма:");
                holder.result.setText(mListOfRecyclerView.get(position).getStringViewOfTypeResultSum());
                holder.textViewForToW.setVisibility(View.INVISIBLE);
            }
            else {
                holder.result.setVisibility(View.GONE);
                holder.place_of_work.setVisibility(View.GONE);
                holder.textViewForToW.setVisibility(View.VISIBLE);
                holder.textViewForToW.setText(mListOfRecyclerView.get(position).getTOWDescription());
            }
        } else {
            holder.view.setOnClickListener(holder.clickListener);
            holder.firstBorder.setVisibility(View.VISIBLE);
            holder.secondBorder.setVisibility(View.VISIBLE);
            holder.thirdBorder.setVisibility(View.VISIBLE);
            holder.fourthBorder.setVisibility(View.VISIBLE);
            holder.fifthBorder.setVisibility(View.VISIBLE);
            holder.sixthBorder.setVisibility(View.VISIBLE);
            holder.date.setVisibility(View.VISIBLE);
            holder.type_of_result.setVisibility(View.VISIBLE);
            holder.place_of_work.setVisibility(View.VISIBLE);
            holder.result.setVisibility(View.VISIBLE);
            holder.textViewForToW.setVisibility(View.INVISIBLE);
            holder.place_of_work.setText(mListOfRecyclerView.get(position).getPOWDescription());
            holder.date.setText(mListOfRecyclerView.get(position).getDate());
            holder.result.setText(mListOfRecyclerView.get(position).getResult());
            if (arrayOfViews != null) {
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
