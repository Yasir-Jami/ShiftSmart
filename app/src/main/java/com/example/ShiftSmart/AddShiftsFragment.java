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


public class AddShiftsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ImageButton addShiftsBackButton;
    Button saveShiftButton;

    Button clearShiftButton;

    Button morningShift1;
    Button morningShift2;
    Button afternoonShift1;
    Button afternoonShift2;

    ImageButton removeMShift1;
    ImageButton removeMShift2;
    ImageButton removeAShift1;
    ImageButton removeAShift2;

    TextView MOTrained1;
    TextView MCTrained1;
    TextView MOTrained2;
    TextView MCTrained2;
    TextView AOTrained1;
    TextView ACTrained1;
    TextView AOTrained2;
    TextView ACTrained2;

    Button selectedButton;


    public AddShiftsFragment() {
        // Required empty public constructor
    }

    public static AddShiftsFragment newInstance(String param1, String param2) {
        AddShiftsFragment fragment = new AddShiftsFragment();
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
        View root = inflater.inflate(R.layout.fragment_add_shifts, container, false);


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

        String shiftType = getArguments().getString("ShiftType");


        // Following code allows navigation to the 'Choose Employee' screen from 'Add Employee' button
        morningShift1 = root.findViewById(R.id.morningShift1);
        morningShift2 = root.findViewById(R.id.morningShift2);
        afternoonShift1 = root.findViewById(R.id.afternoonShift1);
        afternoonShift2 = root.findViewById(R.id.afternoonShift2);

        removeMShift1 = root.findViewById(R.id.morningshift1_remove_button);
        removeMShift2 = root.findViewById(R.id.morningshift2_remove_button);
        removeAShift1 = root.findViewById(R.id.afternoonshift1_remove_button);
        removeAShift2 = root.findViewById(R.id.afternoonshift2_remove_button);

        MOTrained1 = root.findViewById(R.id.Mtrained_opening1);
        MCTrained1 = root.findViewById(R.id.Mtrained_closing1);
        MOTrained2 = root.findViewById(R.id.Mtrained_opening2);
        MCTrained2 = root.findViewById(R.id.Mtrained_closing2);
        AOTrained1 = root.findViewById(R.id.Atrained_opening1);
        ACTrained1 = root.findViewById(R.id.Atrained_closing1);
        AOTrained2 = root.findViewById(R.id.Atrained_opening2);
        ACTrained2 = root.findViewById(R.id.Atrained_closing2);

        removeMShift1.setVisibility(View.GONE);
        removeMShift2.setVisibility(View.GONE);
        removeAShift1.setVisibility(View.GONE);
        removeAShift2.setVisibility(View.GONE);

        MOTrained1.setVisibility(View.GONE);
        MCTrained1.setVisibility(View.GONE);
        MOTrained2.setVisibility(View.GONE);
        MCTrained2.setVisibility(View.GONE);
        AOTrained1.setVisibility(View.GONE);
        ACTrained1.setVisibility(View.GONE);
        AOTrained2.setVisibility(View.GONE);
        ACTrained2.setVisibility(View.GONE);

        // Update the button text based on the scheduled employee
        updateButtonText(morningShift1);
        updateButtonText(morningShift2);
        updateButtonText(afternoonShift1);
        updateButtonText(afternoonShift2);

        // Makes the remove button visible if there is an employee scheduled for that shift
        //updateRemoveButton();

//        removeMShift1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Database db = new Database(getContext());
//
//                db.addOrUpdateShift(0, date, shiftType, -2, -1, -1, 0);
//                updateButtonText(morningShift1);
//                updateRemoveButton();
//                db.close();
//            }
//        });

        morningShift1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedButton = morningShift1;
                bundle.putString("ShiftType", "1");
                bundle.putString("ShiftSlot", "1");
                Navigation.findNavController(v).navigate(R.id.ChooseEmployee, bundle);
            }
        });

        morningShift2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedButton = morningShift2;
                bundle.putString("ShiftType", "1");
                bundle.putString("ShiftSlot", "2");
                Navigation.findNavController(v).navigate(R.id.ChooseEmployee, bundle);
            }
        });

        afternoonShift1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedButton = afternoonShift1;
                bundle.putString("ShiftType", "2");
                bundle.putString("ShiftSlot", "1");
                Navigation.findNavController(v).navigate(R.id.ChooseEmployee, bundle);
            }
        });

        afternoonShift2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedButton = afternoonShift2;
                bundle.putString("ShiftType", "2");
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

                // Check if the form has been modified
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
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
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
                String[] data = db.getTempShiftData("weekday");
//                String day = daysOfMonth.get(position);
//                setDay(day); // For use in Calendar fragment
//                String currentDate = fragment.getFormattedDate(day);
                // if we had busy day implemented, we'd check the shift's isBusy value
                // then set maxEmployees to 2 or 3 accordingly
                int minEmployees = 2;
                int trainedCheckM = 0;
                int trainedCheckA = 0;
                String checkMessage = "";

                List<String> openingEmployees = new ArrayList<>();
                List<String> closingEmployees = new ArrayList<>();

                for (int i = 0; i < data.length; i++) {
                    if (i < 3 && data[i] != null) {
                        openingEmployees.add(data[i]);
                    }
                    else if (i >= 3 && i < 6 && data[i] != null) {
                        closingEmployees.add(data[i]);
                    }
                }

                        // Check employee training
                if (openingEmployees.size() == minEmployees && closingEmployees.size() == minEmployees) {
                    EmployeeProfile validCheck = db.getEmployeeOnShift(date, "1", 1);

                    if (validCheck.isTrainedOpening() != 0) {
                        trainedCheckM = 1;
                    }

                    validCheck = db.getEmployeeOnShift(date, "1", 2);
                    if (validCheck.isTrainedOpening() != 0) {
                        trainedCheckM = 1;
                    }

                    validCheck = db.getEmployeeOnShift(date, "2", 1);
                    if (validCheck.isTrainedClosing() != 0) {
                        trainedCheckA = 1;
                    }

                    validCheck = db.getEmployeeOnShift(date, "2", 2);
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
//        if (morningShift1.getText().toString() != "CLICK TO ADD EMPLOYEE") {
//            removeMShift1.setVisibility(View.VISIBLE);
//        }
//
//        if (morningShift2.getText().toString() != "CLICK TO ADD EMPLOYEE") {
//            removeMShift2.setVisibility(View.VISIBLE);
//        }
//
//        if (afternoonShift1.getText().toString() != "CLICK TO ADD EMPLOYEE") {
//            removeAShift1.setVisibility(View.VISIBLE);
//        }
//
//        if (afternoonShift2.getText().toString() != "CLICK TO ADD EMPLOYEE") {
//            removeAShift2.setVisibility(View.VISIBLE);
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

        // Opening Employee 1
        if (buttonId == R.id.morningShift1){
            EmployeeProfile openingEmployee1 = db.getEmployeeOnShift(date, "1", 1);
            if (openingEmployee1 != null) {
                if (openingEmployee1.getNickname().trim().isEmpty()) {
                    employeeText = openingEmployee1.getFullName();
                }
                else {
                    employeeText = openingEmployee1.getNickname();
                } // Opening Employee 1
                if (openingEmployee1.isTrainedOpening() == 1) {
                   // employeeText = employeeText + trained;
                    MOTrained1.setVisibility(View.VISIBLE);
                }
                if (openingEmployee1.isTrainedClosing() == 1) {
                    // employeeText = employeeText + trained;
                    MCTrained1.setVisibility(View.VISIBLE);
                }
            }
        }
        // Opening Employee 2
        else if (buttonId == R.id.morningShift2){
            EmployeeProfile openingEmployee2 = db.getEmployeeOnShift(date, "1", 2);
            if (openingEmployee2 != null) {
                if (openingEmployee2.getNickname().trim().isEmpty()) {
                    employeeText = openingEmployee2.getFullName();
                }
                else {
                    employeeText = openingEmployee2.getNickname();
                }// Opening Employee 2
                if (openingEmployee2.isTrainedOpening() == 1) {
                   // employeeText = employeeText + trained;
                    MOTrained2.setVisibility(View.VISIBLE);
                }
                if (openingEmployee2.isTrainedClosing() == 1) {
                    // employeeText = employeeText + trained;
                    MCTrained2.setVisibility(View.VISIBLE);
                }
            }
        }
        // Closing Employee 1
        else if (buttonId == R.id.afternoonShift1){
            EmployeeProfile closingEmployee1 = db.getEmployeeOnShift(date, "2", 1);
            if (closingEmployee1 != null) {
                if (closingEmployee1.getNickname().trim().isEmpty()) {
                    employeeText = closingEmployee1.getFullName();
                }
                else {
                    employeeText = closingEmployee1.getNickname();
                } // Closing Employee 1
                if (closingEmployee1.isTrainedClosing() == 1) {
                    //employeeText = employeeText + trained;
                    ACTrained1.setVisibility(View.VISIBLE);
                }
                if (closingEmployee1.isTrainedOpening() == 1) {
                    // employeeText = employeeText + trained;
                    AOTrained1.setVisibility(View.VISIBLE);
                }
            }
        }
        // Closing Employee 2
        else if (buttonId == R.id.afternoonShift2){
            EmployeeProfile closingEmployee2 = db.getEmployeeOnShift(date, "2", 2);
            if (closingEmployee2 != null) {
                if (closingEmployee2.getNickname().trim().isEmpty()) {
                    employeeText = closingEmployee2.getFullName();
                }
                else {
                    employeeText = closingEmployee2.getNickname();
                } // Closing Employee 2
                if (closingEmployee2.isTrainedClosing() == 1) {
                   // employeeText = employeeText + trained;
                    ACTrained2.setVisibility(View.VISIBLE);
                }
                if (closingEmployee2.isTrainedOpening() == 1) {
                    // employeeText = employeeText + trained;
                    AOTrained2.setVisibility(View.VISIBLE);
                }
            }
        }
        db.close();
        return employeeText;
    }

    private void saveShiftData(String date, View view) {

        // Loads temp table contents into shift table
        Database db = new Database(getContext());
        String[] data = db.getTempShiftData("weekday");
        for (int i = 0; i < data.length; i++){
            if (data[i] == null){
                data[i] = "-2";
            }
        }
        int mEmployee1 = Integer.parseInt(data[0]);
        int mEmployee2 = Integer.parseInt(data[1]);
        int mEmployee3 = Integer.parseInt(data[2]);
        int cEmployee1 = Integer.parseInt(data[3]);
        int cEmployee2 = Integer.parseInt(data[4]);
        int cEmployee3 = Integer.parseInt(data[5]);

        db.addOrUpdateShift(1, date, "1", mEmployee1, mEmployee2, mEmployee3, 0);
        db.addOrUpdateShift(1, date, "2", cEmployee1, cEmployee2, cEmployee3, 0);
        db.resetTempShiftTable();
        db.close();
        Toast.makeText(getContext(), "Shift saved", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(view).popBackStack();

    }
}