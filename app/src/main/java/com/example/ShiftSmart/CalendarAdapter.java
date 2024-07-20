package com.example.ShiftSmart;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ShiftSmart.ui.reflow.ReflowFragment;

import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>
{
    private final ArrayList<String> daysOfMonth;
    private final OnItemListener onItemListener;
    private final Context context;
    //private int selectedPos = RecyclerView.NO_POSITION;
    private String currentDay;
    private ReflowFragment fragment;

    public CalendarAdapter(ArrayList<String> daysOfMonth, OnItemListener onItemListener,
                           Context context, ReflowFragment fragment)
    {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        int screenWidth = parent.getResources().getDisplayMetrics().widthPixels;
        int cellWidth = screenWidth / 7; // 7 columns for the days of the week

        // Set the LayoutParams to force each cell to be square
        view.setLayoutParams(new RecyclerView.LayoutParams(cellWidth - 10, cellWidth - 10));
        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        holder.dayOfMonth.setText(daysOfMonth.get(position));
        Database db = new Database(context);
        String day = daysOfMonth.get(position);
        setDay(day); // For use in Calendar fragment
        String currentDate = fragment.getFormattedDate(day);
        // if we had busy day implemented, we'd check the shift's isBusy value
        // then set maxEmployees to 2 or 3 accordingly
        int minEmployees = 2;

        // Calendar shift indicators
        if (currentDate.length() > 8) { // Empty days will not be considered for shift indicators
            // Weekday Shifts
            if (db.getDayOfWeek(currentDate) < 6) {
                ArrayList<EmployeeProfile> openingEmployees = db.getShifts(currentDate, "1");
                ArrayList<EmployeeProfile> closingEmployees = db.getShifts(currentDate, "2");

                // Check employee training
                if (openingEmployees.size() == minEmployees && closingEmployees.size() == minEmployees) {
                    holder.greenShiftIndicator.setVisibility(View.VISIBLE);
                    // Check if either employee is trained for opening
                    if (openingEmployees.get(0).isTrainedOpening() == 0 && openingEmployees.get(1).isTrainedOpening() == 0) {
                        holder.greenShiftIndicator.setVisibility(View.INVISIBLE);
                        holder.redShiftIndicator.setVisibility(View.VISIBLE);
                    }
                    // Check if either employee is trained for closing
                    if (closingEmployees.get(0).isTrainedClosing() == 0 && closingEmployees.get(1).isTrainedClosing() == 0) {
                        holder.greenShiftIndicator.setVisibility(View.INVISIBLE);
                        holder.redShiftIndicator.setVisibility(View.VISIBLE);
                    }
                }
                // There are employees on shift, but not enough
                else if (openingEmployees.size() > 0 || closingEmployees.size() > 0) {
                    // Not enough employees
                    holder.greenShiftIndicator.setVisibility(View.INVISIBLE);
                    holder.redShiftIndicator.setVisibility(View.VISIBLE);
                }
                // No employees on shift
                else {
                    holder.greenShiftIndicator.setVisibility(View.INVISIBLE);
                }
            }

            // Weekend shifts
            else {
                ArrayList<EmployeeProfile> fullDayEmployees = db.getShifts(currentDate, "3");
                // Check if one employee is trained for opening and the other is trained for closing
                if (fullDayEmployees.size() == minEmployees) {
                    EmployeeProfile employee1 = fullDayEmployees.get(0);
                    EmployeeProfile employee2 = fullDayEmployees.get(1);

                    // Check if employee1 is trained for opening and employee2 is trained for closing
                    if (employee1.isTrainedOpening() == 1 && employee2.isTrainedClosing() == 1){
                        holder.greenShiftIndicator.setVisibility(View.VISIBLE);
                    }
                    // Check if employee1 is trained for closing and employee2 is trained for opening
                    else if (employee1.isTrainedClosing() == 1 && employee2.isTrainedOpening() == 1){
                        holder.greenShiftIndicator.setVisibility(View.VISIBLE);
                    }
                    // Insufficient training on employee1 or employee2
                    else {
                        holder.redShiftIndicator.setVisibility(View.VISIBLE);
                    }
                }
                // There are employees on shift, but not enough
                else if (fullDayEmployees.size() > 0) {
                    holder.redShiftIndicator.setVisibility(View.VISIBLE);
                }
                // No employees on shift
                else {
                    holder.greenShiftIndicator.setVisibility(View.INVISIBLE);
                }
            }
        }
        db.close();
    }

    @Override
    public int getItemCount()
    {
        return daysOfMonth.size();
    }

    public void setDay(String day){
        this.currentDay = day;
    }

    public String getDay(){
        return currentDay;
    }

    public interface  OnItemListener
    {
        void onItemClick(int position, String dayText);

    }


}