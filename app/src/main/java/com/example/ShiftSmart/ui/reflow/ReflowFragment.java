package com.example.ShiftSmart.ui.reflow;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ShiftSmart.CalendarAdapter;
import com.example.ShiftSmart.Database;
import com.example.ShiftSmart.EmployeeProfile;
import com.example.ShiftSmart.R;
import com.example.ShiftSmart.databinding.FragmentReflowBinding;
import com.google.android.material.tabs.TabLayout;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ReflowFragment extends Fragment implements CalendarAdapter.OnItemListener  {

    private FragmentReflowBinding binding;
    private TextView monthYearText;
    private TextView noShiftsScheduledTextView;
    private TextView dateScheduledTV;
    private TextView getDateScheduledTVWeekend;
    private TextView openingEmployee1TV, openingEmployee2TV, openingEmployee3TV;
    private TextView closingEmployee1TV, closingEmployee2TV, closingEmployee3TV;
    private TextView fullDayEmployee1TV, fullDayEmployee2TV, fullDayEmployee3TV;
    private LinearLayout noDateSelected;
    private LinearLayout shiftContainerShifts;

    private LinearLayout shiftsContainerShiftsWeekend;
    private LinearLayout noShiftsScheduled;

    private RecyclerView calendarRecyclerView;

    private LocalDate selectedDate;

    private ImageButton previousMonthButton;
    private ImageButton nextMonthButton;
    //CalendarAdapter adapter;
    Button addShiftButton;
    Button editShiftButton;
    String currentSelectedDate;
    String currentMonthYear;
    String selectedDay="";
    String employeeName;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ReflowViewModel reflowViewModel =
                new ViewModelProvider(this).get(ReflowViewModel.class);


        binding = FragmentReflowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        calendarRecyclerView = root.findViewById(R.id.calendarRecyclerView);
        monthYearText = root.findViewById(R.id.monthYearTV);

        selectedDate = LocalDate.now();
        SharedPreferences prefs = getActivity().getSharedPreferences("CalendarPrefs", Context.MODE_PRIVATE);
        String currentMonth = prefs.getString("CurrentMonth", null);
        if (currentMonth != null) {
            selectedDate = LocalDate.parse(currentMonth);
        } else {
            selectedDate = LocalDate.now();
        }
        setMonthView();

        previousMonthButton = root.findViewById(R.id.previousMonthButton);
        nextMonthButton = root.findViewById(R.id.nextMonthButton);
        //adapter = new CalendarAdapter(daysInMonthArray(selectedDate), this);


        noDateSelected = root.findViewById(R.id.noDateSelected);
        shiftContainerShifts = root.findViewById(R.id.shiftsContainerShifts);
        shiftsContainerShiftsWeekend = root.findViewById(R.id.shiftsContainerShiftsWeekend);
        noShiftsScheduled = root.findViewById(R.id.noShiftsScheduled);
        noShiftsScheduledTextView = root.findViewById(R.id.noShiftsScheduledTextView);
        dateScheduledTV = root.findViewById(R.id.dateScheduledTV);
        openingEmployee1TV = root.findViewById(R.id.openingEmployee1TV);
        openingEmployee2TV = root.findViewById(R.id.openingEmployee2TV);
        openingEmployee3TV = root.findViewById(R.id.openingEmployee3TV);
        closingEmployee1TV = root.findViewById(R.id.closingEmployee1TV);
        closingEmployee2TV = root.findViewById(R.id.closingEmployee2TV);
        closingEmployee3TV = root.findViewById(R.id.closingEmployee3TV);
        fullDayEmployee1TV = root.findViewById(R.id.fullDayEmployee1TV);
        fullDayEmployee2TV = root.findViewById(R.id.fullDayEmployee2TV);
        fullDayEmployee3TV = root.findViewById(R.id.fullDayEmployee3TV);
        getDateScheduledTVWeekend = root.findViewById(R.id.dateScheduledTVWeekend);
        editShiftButton = root.findViewById(R.id.editShiftButton);
        addShiftButton = root.findViewById(R.id.addShiftButton);

        previousMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousMonthAction();
            }
        });

        nextMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonthAction();
            }
        });

//        addShiftButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                currentSelectedDate = getCurrentDate();
//                Bundle bundle = new Bundle();
//                bundle.putString("Date", currentSelectedDate);
//                Navigation.findNavController(v).navigate(R.id.add_shifts, bundle);
//            }
//        });

        addShiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelectedDate = getCurrentDate();
                Database db = new Database(getContext());
                // Load current day's shifts into temp shift table
                db.loadShiftsIntoTemp(currentSelectedDate);
                // Check if the selected day is a weekend or weekday
                int weekday = getDayOfWeek(currentSelectedDate);

                boolean isWeekend = weekday == 6 || weekday == 7; // Saturday is 6, Sunday is 7

                Bundle bundle = new Bundle();
                bundle.putString("Date", currentSelectedDate);
                if (isWeekend) {
                    // Weekend nav
                    Log.d("DayCheck", "Selected day is a weekend");
                    Navigation.findNavController(v).navigate(R.id.add_shift_fullday, bundle);
                } else {
                    // Weekday nav
                    Log.d("DayCheck", "Selected day is a weekday");
                    Navigation.findNavController(v).navigate(R.id.add_shifts, bundle);
                }
                db.close();
            }
        });
        editShiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelectedDate = getCurrentDate();
                Database db = new Database(getContext());
                // Load current day's shifts into temp shift table
                db.loadShiftsIntoTemp(currentSelectedDate);

                // Check if the selected day is a weekend or weekday
                int weekday = getDayOfWeek(currentSelectedDate);
                boolean isWeekend = weekday == 6 || weekday == 7; // Saturday is 6, Sunday is 7

                Bundle bundle = new Bundle();
                bundle.putString("Date", currentSelectedDate);
                if (isWeekend) {
                    // Weekend nav
                    Log.d("DayCheck", "Selected day is a weekend");
                    Navigation.findNavController(v).navigate(R.id.add_shift_fullday, bundle);
                } else {
                    // Weekday nav
                    Log.d("DayCheck", "Selected day is a weekday");
                    Navigation.findNavController(v).navigate(R.id.add_shifts, bundle);
                }
                db.close();
            }
        });



        //show tabs
        TabLayout tabLayout = getActivity().findViewById(R.id.tab_layout_view);
        if (tabLayout != null) {
            tabLayout.setVisibility(View.VISIBLE);
        }

        return root;
    }

    private void setMonthView()
    {
        monthYearText.setText(monthYearFromDate(selectedDate));
        currentMonthYear = getCurrentMonthAndYear(selectedDate);
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this, getContext(), this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private ArrayList<String> daysInMonthArray(LocalDate date)
    {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i = 1; i <= 42; i++)
        {
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek)
            {
                daysInMonthArray.add("");
            }
            else
            {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return  daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    private String getCurrentMonthAndYear(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return date.format(formatter);
    }

    private void previousMonthAction()
    {
        selectedDate = selectedDate.minusMonths(1);
        saveCurrentMonth(selectedDate);
        setMonthView();
    }

    private void nextMonthAction()
    {
        selectedDate = selectedDate.plusMonths(1);
        saveCurrentMonth(selectedDate);
        setMonthView();
    }

    private void saveCurrentMonth(LocalDate date) {
        SharedPreferences prefs = getActivity().getSharedPreferences("CalendarPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("CurrentMonth", date.toString());
        editor.apply();
    }

    public String getCurrentDate(){
        if (selectedDay.length() == 1){
            selectedDay = "0" + selectedDay;
        }
        currentSelectedDate = currentMonthYear+"-"+selectedDay;
        return currentSelectedDate;
    }

    public String getFormattedDate(String day){
        if (day.length() == 1){
            day = "0" + day;
        }
        currentSelectedDate = currentMonthYear+"-"+day;
        return currentSelectedDate;
    }

    private String formatSelectedDate(String currentSelectedDate){
        LocalDate date = LocalDate.parse(currentSelectedDate);
        DateTimeFormatter formattedDate = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        return date.format(formattedDate);
    }

    @Override
    public void onItemClick(int position, String dayText)

    {
        this.selectedDay = dayText;
        this.currentSelectedDate = getCurrentDate();

        openingEmployee1TV.setText("");
        openingEmployee2TV.setText("");
        openingEmployee3TV.setText("");
        closingEmployee1TV.setText("");
        closingEmployee2TV.setText("");
        closingEmployee3TV.setText("");
        fullDayEmployee1TV.setText("");
        fullDayEmployee2TV.setText("");
        fullDayEmployee3TV.setText("");

        Database db = new Database(getContext());
        // For debugging
        //db.resetAllShifts();
        //db.resetAllTempShifts();

        if(!dayText.equals(""))
        {
            noDateSelected.setVisibility(View.GONE);

            // Monday-Friday Shifts
            if (db.getDayOfWeek(currentSelectedDate) < 6) {
                ArrayList<EmployeeProfile> openingEmployees = db.getShifts(currentSelectedDate, "1");
                ArrayList<EmployeeProfile> closingEmployees = db.getShifts(currentSelectedDate, "2");

                //if shifts are NOT scheduled
                if (openingEmployees.size() == 0 && closingEmployees.size() == 0) {
                    addShiftButton.setVisibility(View.VISIBLE);
                    noShiftsScheduled.setVisibility(View.VISIBLE);
                    noShiftsScheduledTextView.setText("No shifts scheduled for " + formatSelectedDate(currentSelectedDate));

                    editShiftButton.setVisibility(View.GONE);
                    shiftContainerShifts.setVisibility(View.GONE);
                    shiftsContainerShiftsWeekend.setVisibility(View.GONE);

                }
                else{ // if shifts are scheduled (weekday)
                    editShiftButton.setVisibility(View.VISIBLE);
                    shiftContainerShifts.setVisibility(View.VISIBLE);
                    dateScheduledTV.setText(formatSelectedDate(currentSelectedDate));

                    shiftsContainerShiftsWeekend.setVisibility(View.GONE);
                    noShiftsScheduled.setVisibility(View.GONE);
                    addShiftButton.setVisibility(View.GONE);

                    ArrayList<TextView> openingEmployeeTexts = new ArrayList<>();

                    openingEmployeeTexts.add(openingEmployee1TV);
                    openingEmployeeTexts.add(openingEmployee2TV);
                    openingEmployeeTexts.add(openingEmployee3TV);

                    for (int i = 0; i < openingEmployees.size(); i++) {

                        if (openingEmployees.get(i).getNickname().trim().isEmpty()) {
                            employeeName = openingEmployees.get(i).getFullName();
                        }
                        else {
                            employeeName = openingEmployees.get(i).getNickname();
                        }
                        //String employeeName = openingEmployees.get(i).getFullName();
                        openingEmployeeTexts.get(i).setText(employeeName);
                        System.out.println("Morning employee " + i + " " + employeeName);
                    }
                    ArrayList<TextView> closingEmployeeTexts = new ArrayList<>();

                    closingEmployeeTexts.add(closingEmployee1TV);
                    closingEmployeeTexts.add(closingEmployee2TV);
                    closingEmployeeTexts.add(closingEmployee3TV);

                    for (int i = 0; i < closingEmployees.size(); i++) {
                        if (closingEmployees.get(i).getNickname().trim().isEmpty()) {
                            employeeName = closingEmployees.get(i).getFullName();
                        }
                        else {
                            employeeName = closingEmployees.get(i).getNickname();
                        }
                        //String employeeName = closingEmployees.get(i).getFullName();
                        closingEmployeeTexts.get(i).setText(employeeName);
                        System.out.println("Afternoon employee " + i + " " + employeeName);
                    }

                }
            }
            // Saturday and Sunday Shifts
            else {
                ArrayList<EmployeeProfile> fulLDayEmployees = db.getShifts(currentSelectedDate, "3");

                //if shifts have NOT been scheduled
                if (fulLDayEmployees.size() == 0) {
                    addShiftButton.setVisibility(View.VISIBLE);
                    noShiftsScheduled.setVisibility(View.VISIBLE);
                    noShiftsScheduledTextView.setText("No shifts scheduled for " + formatSelectedDate(currentSelectedDate));

                    shiftsContainerShiftsWeekend.setVisibility(View.GONE);
                    shiftContainerShifts.setVisibility(View.GONE);
                    editShiftButton.setVisibility(View.GONE);

                }

                // shifts have been scheduled
                else { // if shifts are scheduled (weekend)
                    editShiftButton.setVisibility(View.VISIBLE);
                    shiftsContainerShiftsWeekend.setVisibility(View.VISIBLE);
                    getDateScheduledTVWeekend.setText(formatSelectedDate(currentSelectedDate));

                    addShiftButton.setVisibility(View.GONE);
                    shiftContainerShifts.setVisibility(View.GONE);
                    noShiftsScheduled.setVisibility(View.GONE);

                    ArrayList<TextView> fullDayEmployeeTexts = new ArrayList<>();

                    fullDayEmployeeTexts.add(fullDayEmployee1TV);
                    fullDayEmployeeTexts.add(fullDayEmployee2TV);
                    fullDayEmployeeTexts.add(fullDayEmployee3TV);

                    for (int i = 0; i < fulLDayEmployees.size(); i++) {
                        if (fulLDayEmployees.get(i).getNickname().trim().isEmpty()) {
                            employeeName = fulLDayEmployees.get(i).getFullName();
                        }
                        else {
                            employeeName = fulLDayEmployees.get(i).getNickname();
                        }
                        //String employeeName = fulLDayEmployees.get(i).getFullName();
                        fullDayEmployeeTexts.get(i).setText(employeeName);
                    }
                }

                /*
                editShiftButton.setVisibility(View.VISIBLE);
                shiftsContainerShiftsWeekend.setVisibility(View.VISIBLE);
                getDateScheduledTVWeekend.setText(formatSelectedDate(currentSelectedDate));

                editShiftButton.setVisibility(View.GONE);
                shiftContainerShifts.setVisibility(View.GONE);

                 */
            }
            db.close();
        }
        else{
            noDateSelected.setVisibility(View.VISIBLE);
            addShiftButton.setVisibility(View.GONE);
            editShiftButton.setVisibility(View.GONE);
            noShiftsScheduled.setVisibility(View.GONE);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public int getDayOfWeek(String date){
        LocalDate selectedDate = LocalDate.parse(date);
        DayOfWeek dayOfWeek = selectedDate.getDayOfWeek(); // 1 is Monday, 7 is Sunday
        return dayOfWeek.getValue();
    }


}