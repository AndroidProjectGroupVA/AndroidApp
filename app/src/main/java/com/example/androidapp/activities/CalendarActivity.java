package com.example.androidapp.activities;

import static com.example.androidapp.utilities.CalendarUtils.daysInMonthArray;
import static com.example.androidapp.utilities.CalendarUtils.monthYearFromDate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.adapters.CalendarAdapter;
import com.example.androidapp.listener.OnItemListener;
import com.example.androidapp.utilities.CalendarUtils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity implements OnItemListener {
    private TextView month_year;
    private RecyclerView calendarRecylerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.calendar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        calendarRecylerView = findViewById(R.id.calendarRecylerView);
        month_year = findViewById(R.id.month_year);

        CalendarUtils.selectedDate = LocalDate.now();
        setMonthView();
    }

    private void setMonthView() {
        month_year.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInmonth = daysInMonthArray();

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInmonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecylerView.setLayoutManager(layoutManager);
        calendarRecylerView.setAdapter(calendarAdapter);
    }

    public void prevMonth(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonth(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        setMonthView();
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        if (date != null) {
            CalendarUtils.selectedDate = date;
            setMonthView();
        }
    }

    public void weekAction(View view) {
        startActivity(new Intent(this, CalendarWeekActivity.class));
    }
}
