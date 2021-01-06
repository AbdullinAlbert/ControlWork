package com.albertabdullin.controlwork.recycler_views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.models.ComplexEntityForDB;
import com.albertabdullin.controlwork.models.PairOfItemPositions;
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;

import java.util.List;

import static com.albertabdullin.controlwork.R.color.colorAccent;

public class AdapterForResultListFromQuery extends RecyclerView.Adapter<AdapterForResultListFromQuery.MyViewHolder> {
    private final List<ComplexEntityForDB> mListOfRecyclerView;
    private View[] arrayOfViews;
    private final EditDeleteDataVM mViewModel;
    private final LifecycleOwner mLifeCycleOwner;
    private final Context mContext;

    public AdapterForResultListFromQuery(List<ComplexEntityForDB> list, EditDeleteDataVM viewModel,
                                         LifecycleOwner lifecycleOwner,
                                         Context context) {
        mListOfRecyclerView = list;
        mViewModel = viewModel;
        mLifeCycleOwner = lifecycleOwner;
        mContext = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private final TextView id;
        private final TextView date;
        private final TextView result;
        private final TextView note;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewModel.notifyEditTexts(getAbsoluteAdapterPosition());
                    mViewModel.changeColorOfPreviousSelectedItem(
                            new PairOfItemPositions(getAbsoluteAdapterPosition()));
                }
            });
            id = view.findViewById(R.id.cell_for_number);
            date = view.findViewById(R.id.cell_for_date);
            result = view.findViewById(R.id.cell_for_result);
            note = view.findViewById(R.id.cell_for_note);
        }

        public View getView() {
            return view;
        }

        public TextView getId() {
            return id;
        }

        public TextView getDate() { return date; }

        public TextView getResult() {
            return result;
        }

        public TextView getNote() { return note; }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_of_result_list_for_rv, parent, false);
        return new AdapterForResultListFromQuery.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.getId().setText(mListOfRecyclerView.get(position).getID());
        holder.getDate().setText(mListOfRecyclerView.get(position).getDate());
        holder.getResult().setText(mListOfRecyclerView.get(position).getResult());
        holder.getNote().setText(mListOfRecyclerView.get(position).getNote());
        if (arrayOfViews != null) {
            arrayOfViews[position] = holder.getView();
            if (holder.getBindingAdapterPosition() == mViewModel.getPosOfSelectedItem()
            || mViewModel.getPosOfSelectedItem() == -1)
                changeColorOfItems();
        }
    }

    @Override
    public int getItemCount() {
        return mListOfRecyclerView.size();
    }

    private void changeColorOfItems() {
        if (!mViewModel.getChangerColorOfViewHolder().hasObservers()) {
            Observer<PairOfItemPositions> observerOfChangerColorRVItem = new Observer<PairOfItemPositions>() {
                @Override
                public void onChanged(PairOfItemPositions pair) {
                    if (pair.getOldPos() != -1) {
                        arrayOfViews[pair.getOldPos()].
                                setBackgroundColor(mContext.getResources().getColor(R.color.backgroundColorOfActivity, null));
                    }
                    arrayOfViews[pair.getNewPos()].setBackgroundColor(mContext.getResources().getColor(colorAccent, null));
                }
            };
            mViewModel.getChangerColorOfViewHolder().observe(mLifeCycleOwner, observerOfChangerColorRVItem);
        }
    }

    public void initializeArrayOfViews() { arrayOfViews = new View[getItemCount()]; }


}
