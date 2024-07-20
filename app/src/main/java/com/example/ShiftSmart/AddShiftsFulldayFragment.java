package com.example.ShiftSmart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class AddShiftsFulldayFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ImageButton addShiftsBackButton;
    Button saveShiftButton;
    Button clearShiftButton;

    Button fulldayShift1;
    Button fulldayShift2;

    ImageButton removeFShift1;
    ImageButton removeFShift2;

    TextView FOTrained1;
    TextView FCTrained1;
    TextView FOTrained2;
    TextView FCTrained2;

    Button selectedButton;


    public AddShiftsFulldayFragment() {
        // Required empty public constructor
    }

    public static AddShiftsFulldayFragment newInstance(String param1, String param2) {
        AddShiftsFulldayFragment fragment = new AddShiftsFulldayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_add_shift_fullday, container, false);

        if (getArguments() != null && getArguments().containsKey("Date")) {
            String date = getArguments().getString("Date");
            TextView selectedDateText = root.findViewById(R.id.selectedDate);
            selectedDateText.setText(date);
        }

        // Passing it outside of the above if statement to bring it into scope
        String date = getArguments().getString("Date");
        // New bundle to pass to employee selection screen
        final Bundle bundle = new Bundle();
        bundle.putString("Date", date);

        // Hide the tabs
        TabLayout tabLayout = requireActivity().findViewById(R.id.tab_layout_view);
        if (tabLayout != null) {
            tabLayout.setVisibility(View.GONE);
        }


        // Following code allows navigation to the 'Choose Employee' screen from 'Add Employee' button
        fulldayShift1 = root.findViewById(R.id.fulldayShift1);
        fulldayShift2 = root.findViewById(R.id.fulldayShift2);

        removeFShift1 = root.findViewById(R.id.fulldayshift1_remove_button);
        removeFShift2 = root.findViewById(R.id.fulldayshift2_remove_button);

        FOTrained1 = root.findViewById(R.id.Ftrained_opening1);
        FCTrained1 = root.findViewById(R.id.Ftrained_closing1);
        FOTrained2 = root.findViewById(R.id.Ftrained_opening2);
        FCTrained2 = root.findViewById(R.id.Ftrained_closing2);

        removeFShift1.setVisibility(View.GONE);
        removeFShift2.setVisibility(View.GONE);

        FOTrained1.setVisibility(View.GONE);
        FCTrained1.setVisibility(View.GONE);
        FOTrained2.setVisibility(View.GONE);
        FCTrained2.setVisibility(View.GONE);

        // Update the button text based on the scheduled employee
        updateButtonText(fulldayShift1);
        updateButtonText(fulldayShift2);

        // Makes the remove button visible if there is an employee scheduled for that shift
        //updateRemoveButton();

        fulldayShift1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigates to choose employee screen
                //selectedButton = morningShift1;
                bundle.putString("ShiftType", "3");
                bundle.putString("ShiftSlot", "1");
                Navigation.findNavController(v).navigate(R.id.ChooseEmployee, bundle);
            }
        });

        fulldayShift2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigates to choose employee screen
                //selectedButton = morningShift2;
                bundle.putString("ShiftType", "3");
                bundle.putString("ShiftSlot", "2");
                Navigation.findNavController(v).navigate(R.id.ChooseEmployee, bundle);
            }
        });

//        NavController navController = NavHostFragment.findNavController(this);
//        MutableLiveData<String> liveData = navController.getCurrentBackStackEntry().getSavedStateHandle().getLiveData("selectedEmployee");
//        liveData.observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(String selectedEmployee) {
//                morningShift1.setText(selectedEmployee);
//            }
//        });

        addShiftsBackButton = root.findViewById(R.id.addShifts_back_button);

        // Back button click
        addShiftsBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Check if the form has been modified

                Database db = new Database(getContext());

                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setMessage("Are you sure you want to go back? Your changes will not be saved.")
                            .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    db.resetTempShiftTable();
                                    db.close();
                                    Navigation.findNavController(view).popBackStack();
                                }
                            })
                            .setNegativeButton("Stay", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    db.close();
                                }
                            });
                    builder.create().show();
                }
        });

        clearShiftButton = root.findViewById(R.id.delete_button);

        // Removes all employees from shifts - may not refresh on click
        clearShiftButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setMessage("Are you sure you want to reset this shift?")
                        .setPositiveButton("Clear All", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Database db = new Database(getContext());
                                db.resetShifts(date);
                                db.resetTempShiftTable();
                                db.close();
                                Toast.makeText(getContext(), "Shift removed", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(view).popBackStack();
                            }
                        })
                        .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            }
        });

        saveShiftButton = root.findViewById(R.id.save_button);

        // Saves the current state of the shift
        saveShiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Database db = new Database(getContext());
                String[] data = db.getTempShiftData("weekend");
//                String day = daysOfMonth.get(position);
//                setDay(day); // For use in Calendar fragment
//                String currentDate = fragment.getFormattedDate(day);
                // if we had busy day implemented, we'd check the shift's isBusy value
                // then set maxEmployees to 2 or 3 accordingly
                int minEmployees = 2;
                int trainedCheckM = 0;
                int trainedCheckA = 0;
                String checkMessage = "";

                List<String> fulldayEmployees = new ArrayList<>();

                for (int i = 0; i < data.length; i++) {
                    if (data[i] != null) {
                        fulldayEmployees.add(data[i]);
                    }
                }

                // Check employee training
                if (fulldayEmployees.size() == minEmployees) {
                    // Check if either employee is trained for opening
                    EmployeeProfile validCheck = db.getEmployeeOnShift(date, "3", 1);

                    if (validCheck.isTrainedOpening() != 0) {
                        trainedCheckM = 1;
                    }
                    if (validCheck.isTrainedClosing() != 0) {
                        trainedCheckA = 1;
                    }

                    validCheck = db.getEmployeeOnShift(date, "3", 2);
                    if (validCheck.isTrainedOpening() != 0) {
                        trainedCheckM = 1;
                    }
                    if (validCheck.isTrainedClosing() != 0) {
                        trainedCheckA = 1;
                    }

                    // Check if either employee is trained for closing
                    if (trainedCheckM == 0 || trainedCheckA == 0) {
                        checkMessage = "Warning! Missing trained employee(s) on shift. ";
                    }
                }
                // Shift is incomplete - either there are no employees or not enough
                else {
                    // Not enough employees
                    checkMessage = "Warning! Shift is not complete. ";
                }
                db.close();

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setMessage(checkMessage + "Are you sure you want to save?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Save the shift data
                                saveShiftData(date, view);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        return root;
    }

//    private void updateRemoveButton() {
//
//        if (fulldayShift1.getText().toString().equals("CLICK TO ADD EMPLOYEE")) {
//            removeFShift1.setVisibility(View.VISIBLE);
//        }
//
//        if (fulldayShift2.getText().toString().equals("CLICK TO ADD EMPLOYEE")) {
//            removeFShift2.setVisibility(View.VISIBLE);
//        }
//    }

    private void updateButtonText(Button button) {
        String scheduledEmployee = getScheduledEmployeeNameForButton(button.getId());
        if (scheduledEmployee != null && !scheduledEmployee.equals("CLICK TO ADD EMPLOYEE")) {
            // Set button background to white and text color to black
            button.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            button.setTextColor(Color.BLACK);
            button.setText(scheduledEmployee);
        } else {
            // Set button background to grey and text color to white
            button.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            button.setTextColor(Color.WHITE);
            button.setText("CLICK TO ADD EMPLOYEE");
        }
    }

    /**
     * Returns the name of the employee associated with a shift slot
     * @param buttonId id of the button/shift slot
     * @return employee's name
     */
    private String getScheduledEmployeeNameForButton(int buttonId) {
        Database db = new Database(getContext());
        String date = getArguments().getString("Date");
        String employeeText = "CLICK TO ADD EMPLOYEE"; // Default text

        if (buttonId == R.id.fulldayShift1){
            EmployeeProfile fullDayEmployee1 = db.getEmployeeOnShift(date, "3", 1);
            if (fullDayEmployee1 != null) {
                if (fullDayEmployee1.getNickname().trim().isEmpty()) {
                    employeeText = fullDayEmployee1.getFullName();
                }
                else {
                    employeeText = fullDayEmployee1.getNickname();
                }
                //employeeText = fullDayEmployee1.getFullName(); // Opening Employee 1
                if (fullDayEmployee1.isTrainedOpening() == 1) {
                    // employeeText = employeeText + trained;
                    FOTrained1.setVisibility(View.VISIBLE);
                }
                if (fullDayEmployee1.isTrainedClosing() == 1) {
                    // employeeText = employeeText + trained;
                    FCTrained1.setVisibility(View.VISIBLE);
                }
            }
        }
        else if (buttonId == R.id.fulldayShift2){
            EmployeeProfile fullDayEmployee2 = db.getEmployeeOnShift(date, "3", 2);
            if (fullDayEmployee2 != null) {
                if (fullDayEmployee2.getNickname().trim().isEmpty()) {
                    employeeText = fullDayEmployee2.getFullName();
                }
                else {
                    employeeText = fullDayEmployee2.getNickname();
                }
                //employeeText = fullDayEmployee2.getFullName(); // Opening Employee 2
                if (fullDayEmployee2.isTrainedOpening() == 1) {
                    // employeeText = employeeText + trained;
                    FOTrained2.setVisibility(View.VISIBLE);
                }
                if (fullDayEmployee2.isTrainedClosing() == 1) {
                    // employeeText = employeeText + trained;
                    FCTrained2.setVisibility(View.VISIBLE);
                }
            }
        }
        db.close();
        return employeeText;
    }

    private void saveShiftData(String date, View view) {
        // Loads temp table contents into shift table
        Database db = new Database(getContext());
        String[] data = db.getTempShiftData("weekend");
        for (int i = 0; i < (data.length)/2; i++){
            if (data[i] == null){
                data[i] = "-2";
            }
        }
        int fEmployee1 = Integer.parseInt(data[0]);
        int fEmployee2 = Integer.parseInt(data[1]);
        int fEmployee3 = Integer.parseInt(data[2]);

        db.addOrUpdateShift(1, date, "3", fEmployee1, fEmployee2, fEmployee3, 0);
        db.resetTempShiftTable();
        db.close();
        Toast.makeText(getContext(), "Shift saved", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(view).popBackStack();
    }
}