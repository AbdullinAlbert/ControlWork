package com.albertabdullin.controlwork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private View.OnClickListener buttonListener = new View.OnClickListener() {
        public void onClick(View v) {
            fillNewData(v);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button add_button = (Button) findViewById(R.id.add_data_button);
        add_button.setOnClickListener(buttonListener);
    }

    public void fillNewData(View view) {
        Intent intent = new Intent(this, FillNewData.class);
        startActivity(intent);
    }
}
