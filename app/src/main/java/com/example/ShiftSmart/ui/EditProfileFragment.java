package com.example.ShiftSmart.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.ShiftSmart.Database;
import com.example.ShiftSmart.EmployeeProfile;
import com.example.ShiftSmart.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class EditProfileFragment extends Fragment {

    EditText employeeIdInput,
            employeeFirstNameInput,
            employeeLastNameInput,
            employeeEmailInput,
            employeePhoneNumberInput,
            employeeNicknameInput;

    CheckBox employeeTrainedOpening, employeeTrainedClosing;

    Button saveEmployeeButton, deleteEmployeeButton, editAvailabilityButton;
    ImageButton editEmployeeFormBackButton;
    private boolean formModified = false;
    private TextWatcher textWatcher;
    ArrayList<EmployeeProfile> employeeList;

    int[] availabilities = new int[7];

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Hide the tabs
        TabLayout tabLayout = requireActivity().findViewById(R.id.tab_layout_view);
        if (tabLayout != null) {
            tabLayout.setVisibility(View.GONE);
        }

        // Text
        employeeIdInput = root.findViewById(R.id.EmployeeIDInput_edit);
        employeeFirstNameInput = root.findViewById(R.id.FirstNameInput_edit);
        employeeLastNameInput = root.findViewById(R.id.LastNameInput_edit);
        employeeNicknameInput =  root.findViewById(R.id.NicknameInput_edit);
        employeeEmailInput = root.findViewById(R.id.EmailAddressInput_edit);
        employeePhoneNumberInput = root.findViewById(R.id.PhoneNumberInput_edit);
        // Check boxes
        employeeTrainedOpening = root.findViewById(R.id.OpeningShift_edit);
        employeeTrainedClosing = root.findViewById(R.id.ClosingShift_edit);
        // Buttons
        saveEmployeeButton = root.findViewById(R.id.save_button);
        deleteEmployeeButton = root.findViewById(R.id.delete_button);
        editEmployeeFormBackButton = root.findViewById(R.id.back_button);
        editAvailabilityButton = root.findViewById(R.id.add_availability_button_edit);

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

        final int employeeID = getArguments().getInt("employeeID");

        Database db = new Database(getActivity());
        String[] employeeData = db.getEmployeeProfile(employeeID);
        availabilities = db.getAvailabilities();
        employeeList = db.getEmployeeDataFromTable();
        // Fill edit profile screen with employee profile's data
        employeeIdInput.setText(employeeData[0]);
        employeeFirstNameInput.setText(employeeData[1]);
        employeeLastNameInput.setText(employeeData[2]);
        employeeEmailInput.setText(employeeData[3]);
        employeePhoneNumberInput.setText(employeeData[4]);
        employeeNicknameInput.setText(employeeData[7]);

        if (employeeData[5].equals("1")){
            employeeTrainedOpening.setChecked(true);
        }
        if (employeeData[6].equals("1")){
            employeeTrainedClosing.setChecked(true);
        }

        int[] shiftedAvailabilities = new int[availabilities.length];
        shiftedAvailabilities[0] = availabilities[availabilities.length - 1]; // Place the last element at index 0

        for (int i = 1; i < availabilities.length; i++) {
            shiftedAvailabilities[i] = availabilities[i - 1];
        }
        // Get the availability table layout
        TableLayout availabilityTable = root.findViewById(R.id.availability_table_edit);

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
                } else if (shiftedAvailabilities[j] == 1) {
                    if (j != 0 && j != 6 && i == 0) { // For opening shift
                        backgroundColor = openingColor;
                    } else if ((j == 0) || (j == 6)) { // For Sunday and Saturday
                        backgroundColor = openingClosingColor;
                    }
                } else if (shiftedAvailabilities[j] == 2 && i == 1) {
                        backgroundColor = closingColor; // For closing shift
                } else if (shiftedAvailabilities[j] == 3) {
                    if (i == 1) { // For closing shift
                        backgroundColor = closingColor;
                    } else if (i == 0) { // For opening shift
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


        // Save employee data
        saveEmployeeButton.setOnClickListener(new View.OnClickListener() {
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
                            && employeeLastNameInput.getText().toString().trim().equals(employeeList.get(i).getLastName()) && !employeeIdInput.getText().toString().trim().equals(employeeList.get(i).getId())) {
                        if (TextUtils.isEmpty(employeeNicknameInput.getText().toString().trim()) && employeeList.get(i).getNickname().isEmpty()) {
                            employeeNicknameInput.setError("Matching name found-Please enter a Nickname.");
                            errorFlag = 1;
                        }
                    }
                    if (!TextUtils.isEmpty(employeeNicknameInput.getText().toString().trim()) && employeeNicknameInput.getText().toString().trim().equals(employeeList.get(i).getNickname()) && !employeeIdInput.getText().toString().trim().equals(employeeList.get(i).getId())) {
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
                int isTrainedOpening = 0;
                int isTrainedClosing = 0;

                if (employeeTrainedOpening.isChecked()){
                    isTrainedOpening = 1;
                }

                if (employeeTrainedClosing.isChecked()){
                    isTrainedClosing = 1;
                }

                db.updateEmployee(
                        employeeID,
                        Integer.parseInt(employeeIdInput.getText().toString().trim()),
                        employeeFirstNameInput.getText().toString().trim(),
                        employeeLastNameInput.getText().toString().trim(),
                        employeeNicknameInput.getText().toString().trim(),
                        employeeEmailInput.getText().toString().trim(),
                        employeePhoneNumberInput.getText().toString().trim(),
                        isTrainedOpening, isTrainedClosing, availabilities);

                db.close();
                // Navigate back to the profiles screen
                Navigation.findNavController(view).popBackStack();
            }
        });

        // Show a popup if the information is different
        // Saving the text after initially setting it might be the best way
        editEmployeeFormBackButton.setOnClickListener(new View.OnClickListener() {
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

        deleteEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialogDelete(v, employeeID);
            }
        });

        editAvailabilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open availability screen
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

    @SuppressLint("ResourceAsColor")
    private void showConfirmationDialogDelete(final View view, int employeeIDToDelete) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Are you sure you want to delete this profile? You can't undo this action.")

                .setPositiveButton("Delete", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        // Delete the profile
                        Database db = new Database(getActivity());
                        db.removeEmployee(employeeIDToDelete);
                        Navigation.findNavController(view).popBackStack();
                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
    }

}