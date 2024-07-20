package com.example.ShiftSmart;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.Gravity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import android.widget.Button;
import androidx.navigation.Navigation;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.VersionVH> {
    ArrayList<EmployeeProfile> employeeList;

    public EmployeeAdapter(ArrayList<EmployeeProfile> employeeList) {
        this.employeeList = employeeList;
    }

    @NonNull
    @Override
    public VersionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new VersionVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VersionVH holder, int position) {
        EmployeeProfile employee = employeeList.get(position);

        // Update indicators based on training status
        if (employee.isTrainedOpening() == 1) {
            holder.openingShiftIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.openingShiftIndicator.setVisibility(View.GONE);
        }

        if (employee.isTrainedClosing() == 1) {
            holder.closingShiftIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.closingShiftIndicator.setVisibility(View.GONE);
        }

        // Use regex here in the future
        StringBuilder builder;
        builder = new StringBuilder(15);
        builder.append('(');
        builder.append(employee.getPhoneNumber().substring(0,3));
        builder.append(')');
        builder.append('-');
        builder.append(employee.getPhoneNumber().substring(3,6));
        builder.append('-');
        builder.append(employee.getPhoneNumber().substring(6,10));

        // Additional hyphens crashes program
        String formattedPhoneNumber = builder.toString(); // (780)-123-456

        // Use this to fill out each employee's schedule
        int[] availabilities = employee.getAvailabilities();
//        holder.idText.setText(employee.getId());
        if (employee.getNickname().trim().isEmpty()) {
            holder.fullNameText.setText(employee.getFullName());
        }
        else {
            holder.fullNameText.setText(employee.getNickname());
        }

        holder.phoneNumberText.setText(formattedPhoneNumber);
//        holder.emailText.setText(employee.getEmail());

        // Availability Legend: 0 - Unavailable, 1 - Opening, 2 - Closing, 3 - Opening and Closing

        // Employee's individual availability schedule goes here
        int[] shiftedAvailabilities = new int[availabilities.length];
        shiftedAvailabilities[0] = availabilities[availabilities.length - 1]; // Place the last element at index 0

        for (int i = 1; i < availabilities.length; i++) {
            shiftedAvailabilities[i] = availabilities[i - 1];
        }
        // Get the availability table layout
        TableLayout availabilityTable = holder.itemView.findViewById(R.id.availability_table);

        availabilityTable.removeAllViews();

        TableRow headerRow = new TableRow(holder.itemView.getContext());
        String[] weekdays = {"", "S", "M", "T", "W", "T", "F", "S"};
        for (String day : weekdays) {
            TextView textView = new TextView(holder.itemView.getContext());
            textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            textView.setText(day);
            textView.setGravity(Gravity.CENTER);
            headerRow.addView(textView);
        }
        availabilityTable.addView(headerRow);

        Context context = holder.itemView.getContext();
        int openingColor = ContextCompat.getColor(context, R.color.open_color_yellow);
        int closingColor = ContextCompat.getColor(context, R.color.close_color_purple);
        int openingClosingColor = ContextCompat.getColor(context, R.color.fullday_teal);

        // Fill the availability table
        for (int i = 0; i < 2; i++) { // Two rows: one for opening, one for closing
            // Create a new table row
            TableRow tableRow = new TableRow(holder.itemView.getContext());

            // Add labels for open and close rows in the first column
            TextView label = new TextView(holder.itemView.getContext());
            label.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            if (i == 0) {
                label.setText("Open");
            } else {
                label.setText("Close");
            }
            tableRow.addView(label);

            // Create image views for each day
            for (int j = 0; j < shiftedAvailabilities.length; j++) {
                ImageView imageView = new ImageView(holder.itemView.getContext());
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

        final Bundle bundle = new Bundle();
        bundle.putInt("employeeID", Integer.parseInt(employee.getId()));

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the EditProfileFragment
                Database db = new Database(v.getContext());
                db.loadEmployeeAvailabilities(Integer.parseInt(employee.getId()));
                db.close();
                Navigation.findNavController(v).navigate(R.id.editProfileFragment, bundle);
            }
        });



        boolean isExpandable = employeeList.get(position).isExpandable();
        holder.expandableLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public class VersionVH extends RecyclerView.ViewHolder{
        TextView idText, fullNameText, phoneNumberText, emailText;
        Button editButton;
        LinearLayout linearLayout;
        RelativeLayout expandableLayout;

        TextView openingShiftIndicator;
        TextView closingShiftIndicator;

        public VersionVH(@NonNull View itemView) {
            super(itemView);

//            idText = itemView.findViewById(R.id.profile_employee_id);
            fullNameText = itemView.findViewById(R.id.profile_full_name);
            phoneNumberText = itemView.findViewById(R.id.profile_phone_number);
//            emailText = itemView.findViewById(R.id.profile_email);
            editButton = itemView.findViewById(R.id.edit_button);

            linearLayout = itemView.findViewById(R.id.linear_layout);
            expandableLayout = itemView.findViewById(R.id.expandable_layout);
            openingShiftIndicator = itemView.findViewById(R.id.opening_shift_indicator);
            closingShiftIndicator = itemView.findViewById(R.id.closing_shift_indicator);


            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EmployeeProfile employees = employeeList.get(getAdapterPosition());
                    employees.setExpandable(!employees.isExpandable());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}