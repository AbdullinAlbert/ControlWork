package com.albertabdullin.controlwork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class FillNewData_Activity extends AppCompatActivity {
    private View.OnClickListener empClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showTableOfEmployers(v);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_new_data);
        EditText addNewEmpl = (EditText)findViewById(R.id.add_empl_editText);
        addNewEmpl.setOnClickListener(empClickListner);
    }
    private void showTableOfEmployers(View view) {
        Intent intent = new Intent(this, ListOfEmployers_Activity.class);
        startActivity(intent);
    }
}
