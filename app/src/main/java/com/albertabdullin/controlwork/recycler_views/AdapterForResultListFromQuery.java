package com.albertabdullin.controlwork.recycler_views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.fragments.DeleteDataFragment;
import com.albertabdullin.controlwork.models.ComplexEntityForDB;
import com.albertabdullin.controlwork.models.PairOfItemPositions;
import com.albertabdullin.controlwork.recycler_views.selection_trackers.EntityFromDBResultListItemDetails;
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;

import java.util.Iterator;
import java.util.List;

import static com.albertabdullin.controlwork.R.color.colorAccent;

public class AdapterForResultListFromQuery extends RecyclerView.Adapter<AdapterForResultListFromQuery.MyViewHolder> {
    private final List<ComplexEntityForDB> mListOfRecyclerView;
    private View[] arrayOfViews;
    private final EditDeleteDataVM mViewModel;
    private final LifecycleOwner mLifeCycleOwner;
    private final DeleteDataFragment mParentFragment;
    private SelectionTracker<ComplexEntityForDB> mSelectionTracker;

    public AdapterForResultListFromQuery(List<ComplexEntityForDB> list, EditDeleteDataVM viewModel,
                                         LifecycleOwner lifecycleOwner,
                                         DeleteDataFragment parentFragment) {
        mListOfRecyclerView = list;
        mViewModel = viewModel;
        mLifeCycleOwner = lifecycleOwner;
        mParentFragment = parentFragment;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private final TextView id;
        private final TextView date;
        private final TextView result;
        private final TextView note;
        private final View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.notifyEditTexts(getAbsoluteAdapterPosition());
                mViewModel.changeColorOfPreviousSelectedItem(
                        new PairOfItemPositions(getAbsoluteAdapterPosition()));
            }
        };

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            view.setOnClickListener(clickListener);
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

        public ItemDetailsLookup.ItemDetails<ComplexEntityForDB> getItemDetails() {
            return new EntityFromDBResultListItemDetails(mListOfRecyclerView.get(getBindingAdapterPosition()),
                    getBindingAdapterPosition());
        }

        public void setActivatedState(boolean b) {
            int bindingAdapterPos = getBindingAdapterPosition();
            if (b) {
                view.setOnClickListener(null);
                if (bindingAdapterPos != mViewModel.getPosOfSelectedItem()
                        && mViewModel.getPosOfSelectedItem() != -1) {
                    arrayOfViews[mViewModel.getPosOfSelectedItem()]
                            .setBackgroundColor(mParentFragment.getResources().getColor(R.color.backgroundColorOfActivity, null));
                    mViewModel.setDefaultValueToNewPosOfPair();
                }
                view.setBackgroundColor(mParentFragment.getResources().getColor(R.color.colorPrimaryDark, null));
                if (mViewModel.isItemNotSelected(bindingAdapterPos)) mViewModel.notifyEditTexts(bindingAdapterPos);
                mViewModel.addItemOfST(bindingAdapterPos);
            } else {
                if (mViewModel.getItemsCountOfST() > mSelectionTracker.getSelection().size()) {
                    if (!mViewModel.getValueOfETLD().equals("")) mViewModel.notifyEditTexts(-1);
                    if (mSelectionTracker.getSelection().size() == 0) {
                        Iterator<Integer> iterator = mViewModel.itemsOfSTsIterator();
                        int[] arrayOfIntegers = new int[mViewModel.getItemsCountOfST()];
                        int i = 0;
                        while (iterator.hasNext()) arrayOfIntegers[i++] = iterator.next();
                        for (int j : arrayOfIntegers) {
                            if (arrayOfViews[j] != null) {
                                arrayOfViews[j].setBackgroundColor(mParentFragment.getResources().getColor(R.color.backgroundColorOfActivity, null));
                                arrayOfViews[j].setOnClickListener(clickListener);
                                mViewModel.removeItemOfST(j);
                            }
                        }
                    } else {
                        arrayOfViews[bindingAdapterPos].setBackgroundColor(mParentFragment.getResources().getColor(R.color.backgroundColorOfActivity, null));
                        mViewModel.removeItemOfST(bindingAdapterPos);
                        view.setOnClickListener(clickListener);
                    }
                }
            }
        }
    }

    public void setSelectionTracker(SelectionTracker<ComplexEntityForDB> selectionTracker) {
        mSelectionTracker = selectionTracker;
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
        holder.setActivatedState(mSelectionTracker.isSelected(mListOfRecyclerView.get(position)));
        holder.getId().setText(mListOfRecyclerView.get(position).getID());
        holder.getDate().setText(mListOfRecyclerView.get(position).getDate());
        holder.getResult().setText(mListOfRecyclerView.get(position).getResult());
        holder.getNote().setText(mListOfRecyclerView.get(position).getNote());
        if (arrayOfViews != null) {
            if (arrayOfViews[position] == null)
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
                                setBackgroundColor(mParentFragment.getResources().getColor(R.color.backgroundColorOfActivity, null));
                    }
                    if (pair.getNewPos() != -1) {
                        arrayOfViews[pair.getNewPos()].setBackgroundColor(mParentFragment.getResources().getColor(colorAccent, null));
                    }
                }
            };
            mViewModel.getChangerColorOfViewHolder().observe(mLifeCycleOwner, observerOfChangerColorRVItem);
        }
    }

    public void initializeArrayOfViews() {
        if (arrayOfViews == null)
            arrayOfViews = new View[getItemCount()];
    }

    public ComplexEntityForDB getElement(int position) {
        return mListOfRecyclerView.get(position);
    }

    public int getPosition(ComplexEntityForDB eDB) {
        int i = 0;
        while (i < mListOfRecyclerView.size() && !eDB.getID().equals(mListOfRecyclerView.get(i).getID())) i++;
        return i == mListOfRecyclerView.size() ? -1 : i;
    }

    public Iterator<ComplexEntityForDB> getIterator() {
        return mListOfRecyclerView.iterator();
    }

}
