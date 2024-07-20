package com.example.ShiftSmart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ShiftSmart.databinding.FragmentEmployeeSelectionBinding;

import java.util.ArrayList;


public class EmployeeSelection extends Fragment implements EmployeeSelectionAdapter.clickListener {

    private FragmentEmployeeSelectionBinding binding1;
    private RecyclerView recyclerView1;
    private ArrayList<EmployeeProfile> employeeList;


    Button selectNoneButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding1 = FragmentEmployeeSelectionBinding.inflate(inflater, container, false);
        View root = binding1.getRoot();
        recyclerView1 = binding1.recyclerviewSelection;
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));

        String date = getArguments().getString("Date");
        String shiftType = getArguments().getString("ShiftType");
        String shiftSlot = getArguments().getString("ShiftSlot");
        Database db = new Database(getContext());
        initEmployeeData(date, shiftType);
        setRecyclerView();
        db.close();

        selectNoneButton = root.findViewById(R.id.select_none_button);

        // If "select none" is clicked, remove employee from shift - we can replace with a delete button
        selectNoneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Database db = new Database(getContext());
                if (shiftType.equals("2")){
                    System.out.println();
                }
                switch (shiftSlot) {
                    case "1":
                        db.addOrUpdateShift(0, date, shiftType, -2, -1, -1, 0);
                        break;
                    case "2":
                        db.addOrUpdateShift(0, date, shiftType, -1, -2, -1, 0);
                        break;
                    case "3":
                        db.addOrUpdateShift(0, date, shiftType, -1, -1, -2, 0);
                        break;
                }
                db.close();
                Navigation.findNavController(view).getPreviousBackStackEntry().getSavedStateHandle().set("selectedEmployee", "CLICK TO ADD EMPLOYEE");
                Navigation.findNavController(view).popBackStack();
            }

        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding1 = null;
    }

    private void setRecyclerView() {
        EmployeeSelectionAdapter employeeSelectionAdapter = new EmployeeSelectionAdapter(employeeList, this::selectedUser);
        String date = getArguments().getString("Date");
        String shiftType = getArguments().getString("ShiftType");
        String shiftSlot = getArguments().getString("ShiftSlot");
        employeeSelectionAdapter.setDate(date);
        employeeSelectionAdapter.setShiftType(shiftType);
        employeeSelectionAdapter.setShiftSlot(shiftSlot);
        recyclerView1.setAdapter(employeeSelectionAdapter);
        recyclerView1.setHasFixedSize(true);
    }

    /**
     * Gets employee data from database to be added to the profiles screen.
     */
    private void initEmployeeData(String date, String shiftType) {
        Database db = new Database(getActivity());
        // Add data from database
        employeeList = db.getAvailableEmployees(date, shiftType);
        db.close();
    }

    @Override
    public void selectedUser(EmployeeProfile employee, View v) {

        NavController navController = NavHostFragment.findNavController(this);
        navController.getPreviousBackStackEntry().getSavedStateHandle().set("selectedEmployee", employee.getFullName());
        navController.popBackStack();

    }
}