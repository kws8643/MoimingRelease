package com.example.moimingrelease;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.Toast;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;

public class GroupPaymentDateSelectActivity extends AppCompatActivity {

    private CalendarView calendar;
    private Button btnSelectDate;
    private ImageView btnClose;

    private LocalDate selectedDate;


    private void receiveIntent() {

        Intent receivedIntent = getIntent();

        if (receivedIntent.getExtras() != null) {

            selectedDate = (LocalDate) receivedIntent.getSerializableExtra("cur_date");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_payment_date_select);

        receiveIntent();

        initView();

        initParams();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                changeBtnText();

            }
        });

        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sendDate = new Intent();

                sendDate.putExtra("selected_date", selectedDate);

                setResult(RESULT_OK, sendDate);

                finish();
            }
        });


    }


    private void initView() {

        calendar = findViewById(R.id.payment_calendar);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, selectedDate.getYear());
        cal.set(Calendar.MONTH, selectedDate.getMonthValue() - 1);
        cal.set(Calendar.DAY_OF_MONTH, selectedDate.getDayOfMonth());
        long milliTime = cal.getTimeInMillis();
        calendar.setDate(milliTime, true, true);

        btnSelectDate = findViewById(R.id.btn_select_date);
        changeBtnText();

        btnClose = findViewById(R.id.btn_close_date_select);

    }

    private void initParams() {


    }

    private void changeBtnText() {

        String month = String.valueOf(selectedDate.getMonthValue());
        String dayOfMonth = String.valueOf(selectedDate.getDayOfMonth());

        DayOfWeek dow = selectedDate.getDayOfWeek();
        String textDow = getDow(dow.getValue());

        String text = month + "??? " + dayOfMonth + "??? " + textDow + "?????? ?????? ??????";

        btnSelectDate.setText(text);

    }

    private String getDow(int dayValue) {

        String textDow = "";

        switch (dayValue) {
            case 1:
                textDow = "???";
                break;
            case 2:
                textDow = "???";
                break;
            case 3:
                textDow = "???";
                break;
            case 4:
                textDow = "???";
                break;
            case 5:
                textDow = "???";
                break;
            case 6:
                textDow = "???";
                break;
            case 7:
                textDow = "???";
                break;
        }

        return textDow;

    }
}