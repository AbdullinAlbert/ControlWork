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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.albertabdullin.controlwork.R;

public class CommonDeleteDataDF extends DialogFragment {
    private String mHeader;
    private String mMainText;
    private DeleteDataButtonClickExecutor mDeleteDataButtonClickExecutor;
    private final String KEY_FOR_HEADER = "key for header";
    private final String KEY_FOR_MAIN_TEXT = "key for main text";
    private final String KEY_FOR_EXECUTOR = "key for executor";

    public CommonDeleteDataDF() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mHeader = savedInstanceState.getString(KEY_FOR_HEADER);
            mMainText = savedInstanceState.getString(KEY_FOR_MAIN_TEXT);
            mDeleteDataButtonClickExecutor = savedInstanceState.getParcelable(KEY_FOR_EXECUTOR);
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
        return inflater.inflate(R.layout.dialog_fragment_delete_data, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvHeader = view.findViewById(R.id.DeleteDFSelectSize);
        TextView tvMainText = view.findViewById(R.id.DeleteDFQuestion);
        if (mHeader != null) tvHeader.setText(mHeader);
        if (mMainText != null) tvMainText.setText(mMainText);
        Button bYES = view.findViewById(R.id.DeleteDFButtonYES);
        Button bNO = view.findViewById(R.id.DeleteDFButtonNO);
        bYES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeleteDataButtonClickExecutor.executeYesButtonClick((AppCompatActivity) requireActivity());
                requireDialog().dismiss();
            }
        });
        bNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeleteDataButtonClickExecutor.executeNoButtonClick((AppCompatActivity) requireActivity());
                requireDialog().dismiss();
            }
        });
    }

    public void setHeader(String mHeader) {
        this.mHeader = mHeader;
    }

    public void setMainText(String mMainText) {
        this.mMainText = mMainText;
    }

    public void setExecutor(DeleteDataButtonClickExecutor executor) {
        mDeleteDataButtonClickExecutor = executor;
    }

    @Override
    public void onResume() {
        Window window = requireDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.95), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_FOR_EXECUTOR, mDeleteDataButtonClickExecutor);
        outState.putString(KEY_FOR_HEADER, mHeader);
        outState.putString(KEY_FOR_MAIN_TEXT, mMainText);
    }
}

