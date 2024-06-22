package com.example.androidapp.activities;

import static com.example.androidapp.utilities.CalendarUtils.daysInMonthArray;
import static com.example.androidapp.utilities.CalendarUtils.daysInWeekArray;
import static com.example.androidapp.utilities.CalendarUtils.monthYearFromDate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
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
import com.example.androidapp.adapters.EventAdapter;
import com.example.androidapp.listener.OnItemListener;
import com.example.androidapp.models.Event;
import com.example.androidapp.utilities.CalendarUtils;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarWeekActivity extends AppCompatActivity implements OnItemListener {

    private TextView month_year;
    private RecyclerView calendarRecylerView;
    private ListView eventListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar_week);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.calendar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        calendarRecylerView = findViewById(R.id.calendarRecylerView);
        month_year = findViewById(R.id.month_year);
        eventListView = findViewById(R.id.eventListView);

        setWeekView();
    }

    private void setWeekView() {
        month_year.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecylerView.setLayoutManager(layoutManager);
        calendarRecylerView.setAdapter(calendarAdapter);
        setEventAdapter();
    }

    public void prevWeek(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    public void nextWeek(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.selectedDate = date;
        setWeekView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setEventAdapter();
    }

    private void setEventAdapter() {
        ArrayList<Event> dailyEvents = Event.eventsForDate(CalendarUtils.selectedDate);
        EventAdapter eventAdapter = new EventAdapter(getApplicationContext(), dailyEvents);
        eventListView.setAdapter(eventAdapter);
    }

    public void newEvent(View view) {
        startActivity(new Intent(this, CalendarEventActivity.class));
    }
}