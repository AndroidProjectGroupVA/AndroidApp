package com.example.androidapp.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.listener.OnItemListener;
import com.example.androidapp.utilities.CalendarUtils;
import com.example.androidapp.utilities.CalendarViewHolder;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;
    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener) {
        this.days = days;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (days.size() > 15) {
            layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        }
        else {
            layoutParams.height = (int) parent.getHeight();
        }
        return new CalendarViewHolder(view, onItemListener, days);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        final LocalDate date = days.get(position);

        holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
        if (date.equals(CalendarUtils.selectedDate)) {
            holder.parentCellView.setBackgroundColor(Color.LTGRAY);
        }
        if (date.getMonth().equals(CalendarUtils.selectedDate.getMonth())) {
            holder.dayOfMonth.setTextColor(Color.BLACK);
        }
        else {
            holder.dayOfMonth.setTextColor(Color.LTGRAY);
        }
    }

    @Override
    public int getItemCount() {
        return days.size();
    }
}
