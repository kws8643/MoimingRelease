package com.example.moimingrelease;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MoimingErrorActivity extends AppCompatActivity {


    private Button btnRefresh;
    private TextView btnReport;

    private void receiveIntent() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moiming_error);

        receiveIntent();

        initView();

        initParams();

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void initView() {

        btnRefresh = findViewById(R.id.btn_refresh_error);
        btnReport = findViewById(R.id.btn_report_error);
    }

    private void initParams() {

    }
}