package com.example.ShiftSmart.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.graphics.Color;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.ShiftSmart.Database;
import com.example.ShiftSmart.EmployeeProfile;
import com.example.ShiftSmart.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class AddProfileFragment extends Fragment {
    EditText employeeIdInput,
            employeeFirstNameInput,
            employeeLastNameInput,
            employeeEmailInput,
            employeePhoneNumberInput,
            employeeNicknameInput;
    CheckBox employeeTrainedOpening, employeeTrainedClosing;
    Button addEmployeeButton;
    ImageButton addEmployeeFormBackButton;
    ArrayList<EmployeeProfile> employeeList;
    int[] availabilities = new int[7];

    private boolean formModified = false;
    private TextWatcher textWatcher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_profile, container, false);

        // Hide the tabs
        TabLayout tabLayout = requireActivity().findViewById(R.id.tab_layout_view);
        if (tabLayout != null) {
            tabLayout.setVisibility(View.GONE);
        }

        // Attach employee inputs and buttons
        employeeIdInput = root.findViewById(R.id.EmployeeIDInput);
        employeeFirstNameInput = root.findViewById(R.id.FirstNameInput);
        employeeLastNameInput = root.findViewById(R.id.LastNameInput);
        employeeNicknameInput = root.findViewById(R.id.NicknameInput);
        employeeEmailInput = root.findViewById(R.id.EmailAddressInput);
        employeePhoneNumberInput = root.findViewById(R.id.PhoneNumberInput);
        employeeTrainedOpening = root.findViewById(R.id.OpeningShift);
        employeeTrainedClosing = root.findViewById(R.id.ClosingShift);
        addEmployeeButton = root.findViewById(R.id.confirm_button);
        addEmployeeFormBackButton = root.findViewById(R.id.back_button);

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                formModified = true;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        employeeIdInput.addTextChangedListener(textWatcher);
        employeeFirstNameInput.addTextChangedListener(textWatcher);
        employeeLastNameInput.addTextChangedListener(textWatcher);
        employeeNicknameInput.addTextChangedListener(textWatcher);
        employeeEmailInput.addTextChangedListener(textWatcher);
        employeePhoneNumberInput.addTextChangedListener(textWatcher);
        // Get the current availabilities if they are changed
        Database db = new Database(getActivity());
        availabilities = db.getAvailabilities();
        employeeList = db.getEmployeeDataFromTable();

        int[] shiftedAvailabilities = new int[availabilities.length];
        shiftedAvailabilities[0] = availabilities[availabilities.length - 1]; // Place the last element at index 0

        for (int i = 1; i < availabilities.length; i++) {
            shiftedAvailabilities[i] = availabilities[i - 1];
        }
        // Get the availability table layout
        TableLayout availabilityTable = root.findViewById(R.id.availability_table);

        int openingColor = getResources().getColor(R.color.open_color_yellow);
        int closingColor = getResources().getColor(R.color.close_color_purple);
        int openingClosingColor = getResources().getColor(R.color.fullday_teal);

        // Fill the availability table
        for (int i = 0; i < 2; i++) { // Two rows: one for opening, one for closing
            // Create a new table row
            TableRow tableRow = new TableRow(requireContext());

            // Add labels for open and close rows in the first column
            TextView label = new TextView(requireContext());
            label.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            if (i == 0) {
                label.setText("Open");
            } else {
                label.setText("Close");
            }
            tableRow.addView(label);

            // Create image views for each day
            for (int j = 0; j < shiftedAvailabilities.length; j++) {
                ImageView imageView = new ImageView(requireContext());
                TableRow.LayoutParams params = new TableRow.LayoutParams(50, 50);
                params.setMargins(5, 0, 5, 0);
                int backgroundColor = Color.WHITE;

                // Set background color based on availability
                if (shiftedAvailabilities[j] == 0) {
                    backgroundColor = Color.WHITE;
                }
                else if (shiftedAvailabilities[j] == 1) {
                    if (j != 0 && j != 6 && i == 0) { // For opening shift
                        backgroundColor = openingColor;
                    }
                    else if ((j == 0) || (j == 6)) { // For Sunday and Saturday
                        backgroundColor = openingClosingColor;
                    }
                }
                else if (shiftedAvailabilities[j] == 2 && i == 1) {
                        backgroundColor = closingColor;
                }
                else if (shiftedAvailabilities[j] == 3) {
                    if (i == 1) { // For closing shift
                        backgroundColor = closingColor;
                    }
                    else if (i == 0) { // For opening shift
                        backgroundColor = openingColor;
                    }
                }

                imageView.setLayoutParams(params);
                imageView.setBackgroundColor(backgroundColor);
                tableRow.addView(imageView);
            }

            // Add the row to the table layout
            availabilityTable.addView(tableRow);
        }

        // Confirm button
        addEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if any of the fields are empty
                int errorFlag = 0;

                // Raise a flag and do not continue if there at least one error
                if (TextUtils.isEmpty(employeeFirstNameInput.getText().toString().trim())){
                    employeeFirstNameInput.setError("Please enter a first name.");
                    errorFlag = 1;
                }
                if (TextUtils.isEmpty(employeeLastNameInput.getText().toString().trim())){
                    employeeLastNameInput.setError("Please enter a last name.");
                    errorFlag = 1;
                }
                if (TextUtils.isEmpty(employeeIdInput.getText().toString().trim())){
                    employeeIdInput.setError("Please enter an ID.");
                    errorFlag = 1;
                }
                if (TextUtils.isEmpty(employeeEmailInput.getText().toString().trim())){
                    employeeEmailInput.setError("Please enter an email.");
                    errorFlag = 1;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(employeeEmailInput.getText().toString().trim()).matches()){
                    employeeEmailInput.setError("Invalid email address.");
                    errorFlag = 1;
                }
                if (TextUtils.isEmpty(employeePhoneNumberInput.getText().toString().trim())){
                    employeePhoneNumberInput.setError("Please enter a phone number.");
                    errorFlag = 1;
                }
                else if (employeePhoneNumberInput.getText().toString().trim().length() != 10){
                    employeePhoneNumberInput.setError("Phone number must contain exactly 10 digits.");
                    errorFlag = 1;
                }

                for (int i = 0; i < employeeList.size(); i++) {
                    if (employeeFirstNameInput.getText().toString().trim().equals(employeeList.get(i).getFirstName())
                            && employeeLastNameInput.getText().toString().trim().equals(employeeList.get(i).getLastName())) {
                        if (TextUtils.isEmpty(employeeNicknameInput.getText().toString().trim()) && employeeList.get(i).getNickname().isEmpty()) {
                            employeeNicknameInput.setError("Matching name found-Please enter a Nickname.");
                            errorFlag = 1;
                        }
                    }
                    if (!TextUtils.isEmpty(employeeNicknameInput.getText().toString().trim()) && employeeNicknameInput.getText().toString().trim().equals(employeeList.get(i).getNickname())) {
                        employeeNicknameInput.setError("Nickname taken.");
                        errorFlag = 1;
                    }
                    if (!TextUtils.isEmpty(employeeNicknameInput.getText().toString().trim()) && employeeNicknameInput.getText().toString().trim().equals(employeeList.get(i).getFullName())) {
                        employeeNicknameInput.setError("Matching name found-Please enter a new Nickname.");
                        errorFlag = 1;
                    }
                    if (employeeFirstNameInput.getText().toString().trim().length() + employeeLastNameInput.getText().toString().trim().length() > 10 && TextUtils.isEmpty(employeeNicknameInput.getText().toString().trim())) {
                        employeeNicknameInput.setError("Name exceeds length-Please enter a Nickname.");
                        errorFlag = 1;
                    }
                }

                // Don't save changes
                if (errorFlag == 1){
                    return;
                }

                // Check state of checkboxes
                int isTrainedOpening = employeeTrainedOpening.isChecked() ? 1 : 0;
                int isTrainedClosing = employeeTrainedClosing.isChecked() ? 1 : 0;

                availabilities = db.getAvailabilities();

                db.addEmployee(
                        Integer.parseInt(employeeIdInput.getText().toString().trim()),
                        employeeFirstNameInput.getText().toString().trim(),
                        employeeLastNameInput.getText().toString().trim(),
                        employeeNicknameInput.getText().toString().trim(),
                        employeeEmailInput.getText().toString().trim(),
                        employeePhoneNumberInput.getText().toString().trim(),
                        isTrainedOpening, isTrainedClosing, availabilities);

                db.close();
                // Navigate back to the profiles screen with tabs
                Navigation.findNavController(view).popBackStack();
            }
        });


        // Back button click
        addEmployeeFormBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if the form has been modified
                if (formModified) {
                    showConfirmationDialog(view);
                } else {
                    Navigation.findNavController(view).popBackStack();
                }
            }
        });

        Button addAvailabilityButton = root.findViewById(R.id.add_availability_button);
        addAvailabilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.addAvailabilityFragment);
            }
        });

        return root;
    }

    private void showConfirmationDialog(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Are you sure you want to go back? Your changes will not be saved.")
                .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Navigation.findNavController(view).popBackStack();
                    }
                })
                .setNegativeButton("Stay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
}