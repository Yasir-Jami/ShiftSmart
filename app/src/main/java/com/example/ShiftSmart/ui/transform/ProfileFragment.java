package com.example.ShiftSmart.ui.transform;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ShiftSmart.Database;
import com.example.ShiftSmart.R;
import com.example.ShiftSmart.EmployeeProfile;
import com.example.ShiftSmart.EmployeeAdapter;
import com.example.ShiftSmart.databinding.FragmentTransformBinding;
import android.widget.Button;

import java.util.ArrayList;

import androidx.navigation.Navigation;

// Add this import for FloatingActionButton
import com.google.android.material.tabs.TabLayout;

public class ProfileFragment extends Fragment {

    private FragmentTransformBinding binding;
    private RecyclerView recyclerView;
    private ArrayList<EmployeeProfile> employeeList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTransformBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recyclerviewTransform;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        initEmployeeData();
        setRecyclerView();

        // Find your FloatingActionButton
        Button employeeAddButton = root.findViewById(R.id.employee_add_button);

        // Set OnClickListener to open the new screen when the FAB is clicked
        employeeAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open your new screen here
                Database db = new Database(getActivity());
                db.resetAvailabilityTable();
                Navigation.findNavController(v).navigate(R.id.addProfileFragment);
            }
        });

        //show tabs
        TabLayout tabLayout = getActivity().findViewById(R.id.tab_layout_view);
        if (tabLayout != null) {
            tabLayout.setVisibility(View.VISIBLE);
        }


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setRecyclerView() {
        EmployeeAdapter employeeAdapter = new EmployeeAdapter(employeeList);
        recyclerView.setAdapter(employeeAdapter);
        recyclerView.setHasFixedSize(true);
    }

    /**
     * Gets employee data from database to be added to the profiles screen.
     */
    private void initEmployeeData() {
        Database db = new Database(getActivity());
        // Add data from database
        employeeList = db.getEmployeeDataFromTable();
        db.close();
    }
}