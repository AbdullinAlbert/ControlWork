package com.albertabdullin.controlwork.customView;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import androidx.appcompat.view.CollapsibleActionView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.albertabdullin.controlwork.R;

public class SearchEditText extends CoordinatorLayout implements CollapsibleActionView {
    private View mCoordinatorLayout;
    private EditText mTextView;
    private ImageView mImageView;

    ImageView.OnClickListener onClickImageViewListener = new ImageView.OnClickListener() {

        @Override
        public void onClick(View v) {
            mTextView.setText("");
            mImageView.setVisibility(INVISIBLE);
        }
    };

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (count != 0) mImageView.setVisibility(VISIBLE);
            else mImageView.setVisibility(INVISIBLE);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private void localInit(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCoordinatorLayout = inflater.inflate(R.layout.search_edit_text, this);
        mTextView = mCoordinatorLayout.findViewById(R.id.search_string);
        mTextView.addTextChangedListener(textWatcher);
        mImageView = mCoordinatorLayout.findViewById(R.id.clear_search_string);
        mImageView.setOnClickListener(onClickImageViewListener);
        mImageView.setVisibility(INVISIBLE);
    }

    public SearchEditText(@NonNull Context context) {
        super(context);
        localInit(context);
    }

    public SearchEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        localInit(context);
    }

    public SearchEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        localInit(context);
    }

    public EditText getTextView() {
        return mTextView;
    }

    @Override
    public void onActionViewExpanded() {
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        setLayoutParams(params);
        requestLayout();
    }

    @Override
    public void onActionViewCollapsed() {

    }


}
