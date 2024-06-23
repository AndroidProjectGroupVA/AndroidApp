package com.example.androidapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.androidapp.R;
import com.example.androidapp.models.Event;
import com.example.androidapp.utilities.CalendarUtils;

import java.time.LocalTime;

public class CalendarEventActivity extends AppCompatActivity {
    private EditText eventName;
    private TextView tv_eventDate, tv_eventTime;
    private LocalTime time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.calendarEvent), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        eventName = findViewById(R.id.eventName);
        tv_eventDate = findViewById(R.id.tv_eventDate);
        tv_eventTime = findViewById(R.id.tv_eventTime);
        time = LocalTime.now();
        tv_eventDate.setText("Ngày: " + CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        tv_eventTime.setText("Thời gian: " + CalendarUtils.formattedTime(time));

    }

    public void saveEvent(View view) {
        String eventName1 = eventName.getText().toString();
        Event newEvent = new Event(eventName1, CalendarUtils.selectedDate, time);
        Event.eventList.add(newEvent);
        finish();
    }
}
