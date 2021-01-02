package com.albertabdullin.controlwork.recycler_views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.models.ComplexEntityForDB;
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;

import java.util.List;

import static com.albertabdullin.controlwork.R.color.colorAccent;
import static com.albertabdullin.controlwork.R.color.colorPrimary;

public class AdapterForResultListFromQuery extends RecyclerView.Adapter<AdapterForResultListFromQuery.MyViewHolder> {
    private List<ComplexEntityForDB> mListOfRecyclerView;
    private EditDeleteDataVM mViewModel;
    private Context mContext;

    public AdapterForResultListFromQuery(List<ComplexEntityForDB> list, EditDeleteDataVM viewModel, Context context) {
        mListOfRecyclerView = list;
        mViewModel = viewModel;
        mContext = context;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private final TextView id;
        private final TextView date;
        private final TextView result;
        private final TextView note;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
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

        public TextView getDate() {
            return date;
        }

        public TextView getResult() {
            return result;
        }

        public TextView getNote() {
            return note;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_of_result_list_for_rv, parent, false);
        return new AdapterForResultListFromQuery.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundColor(mContext.getResources().getColor(colorAccent, null));
                mViewModel.notifyEditTexts(position);
            }
        };
        holder.getView().setOnClickListener(listener);
        holder.getId().setText(mListOfRecyclerView.get(position).getID());
        holder.getDate().setText(mListOfRecyclerView.get(position).getDate());
        holder.getResult().setText(mListOfRecyclerView.get(position).getResult());
        holder.getNote().setText(mListOfRecyclerView.get(position).getNote());
    }

    @Override
    public int getItemCount() {
        return mListOfRecyclerView.size();
    }


}
