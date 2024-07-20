package com.example.ShiftSmart.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.ShiftSmart.Database;
import com.example.ShiftSmart.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

public class AddAvailabilityFragment extends Fragment {

    ImageButton availabilityBackButton;
    Button confirmButton;
    ArrayList<ImageButton> buttonsList = new ArrayList<>();
    int[] availabilities = new int[7];

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_availability, container, false);

        TabLayout tabLayout = requireActivity().findViewById(R.id.tab_layout_view);
        if (tabLayout != null) {
            tabLayout.setVisibility(View.GONE);
        }

        // Buttons and availabilities
        availabilityBackButton = root.findViewById(R.id.availability_back_button);
        confirmButton = root.findViewById(R.id.confirm_button);

        /*
        Editing a profile and navigating to the AddAvailabilityFragment
        loads that employee's data into the availability table.
        Adding a profile and navigating to the AddAvailabilityFragment
        sets all buttons to Unavailable for the first time.
        The state is saved until the employee is added or edited.
        */
        // Get all availabilities from the availability table
        Database db = new Database(getContext());
        availabilities = db.getAvailabilities();

        ImageButton mondayOpen = root.findViewById(R.id.mon_open);
        mondayOpen.setTag(R.id.shift_type, "opening");
        mondayOpen.setOnClickListener(this::onShiftClick);

        ImageButton tuesdayOpen = root.findViewById(R.id.tues_open);
        tuesdayOpen.setTag(R.id.shift_type, "opening");
        tuesdayOpen.setOnClickListener(this::onShiftClick);

        ImageButton wednesdayOpen = root.findViewById(R.id.wed_open);
        wednesdayOpen.setTag(R.id.shift_type, "opening");
        wednesdayOpen.setOnClickListener(this::onShiftClick);

        ImageButton thursdayOpen = root.findViewById(R.id.thurs_open);
        thursdayOpen.setTag(R.id.shift_type, "opening");
        thursdayOpen.setOnClickListener(this::onShiftClick);

        ImageButton fridayOpen = root.findViewById(R.id.fri_open);
        fridayOpen.setTag(R.id.shift_type, "opening");
        fridayOpen.setOnClickListener(this::onShiftClick);

        ImageButton mondayClose = root.findViewById(R.id.mon_close);
        mondayClose.setTag(R.id.shift_type, "closing");
        mondayClose.setOnClickListener(this::onShiftClick);

        ImageButton tuesdayClose = root.findViewById(R.id.tues_close);
        tuesdayClose.setTag(R.id.shift_type, "closing");
        tuesdayClose.setOnClickListener(this::onShiftClick);

        ImageButton wednesdayClose = root.findViewById(R.id.wed_close);
        wednesdayClose.setTag(R.id.shift_type, "closing");
        wednesdayClose.setOnClickListener(this::onShiftClick);

        ImageButton thursdayClose = root.findViewById(R.id.thurs_close);
        thursdayClose.setTag(R.id.shift_type, "closing");
        thursdayClose.setOnClickListener(this::onShiftClick);

        ImageButton fridayClose = root.findViewById(R.id.fri_close);
        fridayClose.setTag(R.id.shift_type, "closing");
        fridayClose.setOnClickListener(this::onShiftClick);

        ImageButton saturdayFullDay = root.findViewById(R.id.sat_full_day);
        saturdayFullDay.setTag(R.id.shift_type, "fullDay");
        saturdayFullDay.setOnClickListener(this::onShiftClick);

        ImageButton sundayFullDay = root.findViewById(R.id.sun_full_day);
        sundayFullDay.setTag(R.id.shift_type, "fullDay");
        sundayFullDay.setOnClickListener(this::onShiftClick);

        // Set button states according to availabilities
        buttonsList.add(mondayOpen);
        buttonsList.add(mondayClose);
        buttonsList.add(tuesdayOpen);
        buttonsList.add(tuesdayClose);
        buttonsList.add(wednesdayOpen);
        buttonsList.add(wednesdayClose);
        buttonsList.add(thursdayOpen);
        buttonsList.add(thursdayClose);
        buttonsList.add(fridayOpen);
        buttonsList.add(fridayClose);
        buttonsList.add(saturdayFullDay);
        buttonsList.add(sundayFullDay);
        setButtonStates(availabilities);

        availabilityBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Navigation.findNavController(view).popBackStack();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Get states of all the buttons and uses it to determine an employee's availability
             * @param view Confirm button which sets employee availabilities.
             */
            @Override
            public void onClick(View view) {
                // Get the state of the buttons and set the availability values accordingly
                int j;
                // Set availabilities according to the buttons clicked
                // Weekdays availability: 0 - Unavailable, 1 - Opening, 2 - Closing, 3 - Opening and Closing
                // Weekends availability: 0 - Unavailable, 2 - Full Day

                // Determines availabilities based on button states
                for (int i = 0; i < buttonsList.size() - 2; i++) {
                    j = (i / 2);
                    // If available for opening and closing shift
                    if ("available".equals(buttonsList.get(i).getTag()) && "available".equals(buttonsList.get(i + 1).getTag())) {
                        availabilities[j] = 3;
                    }
                    // If available for opening shift
                    else if ("available".equals(buttonsList.get(i).getTag()) && !("available".equals(buttonsList.get(i + 1).getTag()))) {
                        availabilities[j] = 1;
                    }
                    // If available for closing shift
                    else if ("available".equals(buttonsList.get(i + 1).getTag()) && !("available".equals(buttonsList.get(i).getTag()))) {
                        availabilities[j] = 2;
                    } else {
                        availabilities[j] = 0;
                    }

                    i++; // Move to next day's opening and closing shifts
                }

                // Saturday availability
                if ("available".equals(buttonsList.get(buttonsList.size() - 2).getTag())) {
                    availabilities[5] = 1;
                } else {
                    availabilities[5] = 0;
                }
                // Sunday availability
                if ("available".equals(buttonsList.get(buttonsList.size() - 1).getTag())) {
                    availabilities[6] = 1;
                } else {
                    availabilities[6] = 0;
                }

                db.updateAvailabilities(availabilities);
                db.close();
                Navigation.findNavController(view).popBackStack();
            }
        });

        return root;
    }

    public void onShiftClick(View view) {
        ImageButton clickedImageButton = (ImageButton) view;
        boolean isAvailable = "available".equals(clickedImageButton.getTag());
        int colorShift;
        String shiftType = (String) clickedImageButton.getTag(R.id.shift_type);

        // determine shift type based on tag (opening, closing, fullDay)
        if (isAvailable) {
            // revert to unclicked state
            if (Objects.equals(shiftType, "fullDay")) {
                colorShift = R.drawable.rectangle_fullday_unavailable;
                clickedImageButton.setTag(null);
            } else {
                colorShift = R.drawable.rectangle_unavailable;
                clickedImageButton.setTag(null);
            }

        } else {
            // set to available based on shift type
            clickedImageButton.setTag("available");

            switch (shiftType) {
                case "opening":
                    colorShift = R.drawable.rectangle_open;
                    break;
                case "closing":
                    colorShift = R.drawable.rectangle_close;
                    break;
                case "fullDay":
                    colorShift = R.drawable.rectangle_fullday;
                    break;
                default:
                    colorShift = R.drawable.rectangle_unavailable;
                    break;
            }
        }
        clickedImageButton.setImageResource(colorShift);
    }

    /**
     * Sets the button states based on the values stored in the availability table
     */
    private void setButtonStates(int[] availabilities) {
        int size = buttonsList.size();
        ImageButton imageButtonOpen;
        ImageButton imageButtonClose;
        ImageButton imageButtonSaturday;
        ImageButton imageButtonSunday;
        int colorShift;
        int j;

        // Set button states for weekdays
        for (int i = 0; i < size - 2; i++) {
            imageButtonOpen = buttonsList.get(i);
            imageButtonClose = buttonsList.get(i + 1);
            j = (i / 2);

            // Unavailable for both shifts
            if (availabilities[j] == 0) {
                colorShift = R.drawable.rectangle_unavailable;
                imageButtonOpen.setTag(null);
                imageButtonOpen.setImageResource(colorShift);
                imageButtonClose.setTag(null);
                imageButtonClose.setImageResource(colorShift);
            }
            // Available for Opening Shift
            else if (availabilities[j] == 1) {
                colorShift = R.drawable.rectangle_open;
                imageButtonOpen.setTag("available");
                imageButtonOpen.setImageResource(colorShift);
                colorShift = R.drawable.rectangle_unavailable;
                imageButtonClose.setTag(null);
                imageButtonClose.setImageResource(colorShift);
            }
            // Available for Closing Shift
            else if (availabilities[j] == 2) {
                colorShift = R.drawable.rectangle_unavailable;
                imageButtonOpen.setTag(null);
                imageButtonOpen.setImageResource(colorShift);
                colorShift = R.drawable.rectangle_close;
                imageButtonClose.setTag("available");
                imageButtonClose.setImageResource(colorShift);
            }
            // Available for Opening and Closing Shift
            else if (availabilities[j] == 3) {
                colorShift = R.drawable.rectangle_open;
                imageButtonOpen.setTag("available");
                imageButtonOpen.setImageResource(colorShift);
                colorShift = R.drawable.rectangle_close;
                imageButtonClose.setTag("available");
                imageButtonClose.setImageResource(colorShift);
            }
            i++;
        }

        // Set button states for weekends
        imageButtonSaturday = buttonsList.get(size - 2);
        imageButtonSunday = buttonsList.get(size - 1);

        // Unavailable for Saturday
        if (availabilities[5] == 0) {
            colorShift = R.drawable.rectangle_fullday_unavailable;
            imageButtonSaturday.setTag(null);
            imageButtonSaturday.setImageResource(colorShift);
        }
        // Available for Saturday
        else if (availabilities[5] == 1) {
            colorShift = R.drawable.rectangle_fullday;
            imageButtonSaturday.setTag("available");
            imageButtonSaturday.setImageResource(colorShift);
        }
        // Unavailable for Sunday
        if (availabilities[6] == 0) {
            colorShift = R.drawable.rectangle_fullday_unavailable;
            imageButtonSunday.setTag(null);
            imageButtonSunday.setImageResource(colorShift);
        }
        // Available for Sunday
        else if (availabilities[6] == 1) {
            colorShift = R.drawable.rectangle_fullday;
            imageButtonSunday.setTag("available");
            imageButtonSunday.setImageResource(colorShift);
        }
    }
}