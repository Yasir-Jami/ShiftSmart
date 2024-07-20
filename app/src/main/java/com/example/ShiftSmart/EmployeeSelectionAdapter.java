package com.example.ShiftSmart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class EmployeeSelectionAdapter extends RecyclerView.Adapter<EmployeeSelectionAdapter.VersionVH> {
    ArrayList<EmployeeProfile> employeeList;
    String date;
    String shiftType;
    String shiftSlot;

    public clickListener clickListener;

    public EmployeeSelectionAdapter(ArrayList<EmployeeProfile> employeeList, clickListener clickListener) {
        this.employeeList = employeeList;
        this.clickListener = clickListener;
    }

    public interface clickListener{
        void selectedUser(EmployeeProfile employee, View v);
    }
    @NonNull
    @Override
    public VersionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selectionrows, parent, false);
        return new VersionVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VersionVH holder, int position) {
        EmployeeProfile employee = employeeList.get(position);

        // Update indicators based on training status
        if (employee.isTrainedOpening() == 1) {
            holder.trainedOpening.setVisibility(View.VISIBLE);
        } else {
            holder.trainedOpening.setVisibility(View.GONE);
        }

        if (employee.isTrainedClosing() == 1) {
            holder.trainedClosing.setVisibility(View.VISIBLE);
        } else {
            holder.trainedClosing.setVisibility(View.GONE);
        }

        if (employee.getNickname().trim().isEmpty()) {
            holder.fullNameText.setText(employee.getFullName());
        }
        else {
            holder.fullNameText.setText(employee.getNickname());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database db = new Database(v.getContext());

                String date = getDate();
                String shiftType = getShiftType();
                // Shift slot determines which employee (employee1, employee2, employee3) is updated
                String shiftSlot = getShiftSlot();

                if (shiftSlot.equals("1")){
                    db.addOrUpdateShift(0, date, shiftType, Integer.parseInt(employee.getId()), -1, -1, 0);
                }
                else if (shiftSlot.equals("2")){
                    db.addOrUpdateShift(0, date, shiftType, -1, Integer.parseInt(employee.getId()), -1, 0);
                }
                else if (shiftSlot.equals("3")){
                    db.addOrUpdateShift(0, date, shiftType, -1, -1, Integer.parseInt(employee.getId()), 0);
                }

                db.close();
                clickListener.selectedUser(employee, v);
            }
        });

    }

    public String getDate(){
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getShiftType(){
        return shiftType;
    }

    public void setShiftType(String shiftType){
        this.shiftType = shiftType;
    }

    public String getShiftSlot(){
        return shiftSlot;
    }

    public void setShiftSlot(String shiftSlot){
        this.shiftSlot = shiftSlot;
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public class VersionVH extends RecyclerView.ViewHolder{
        TextView fullNameText;
        RelativeLayout relativeLayout;

        TextView trainedOpening;

        TextView trainedClosing;

        public VersionVH(@NonNull View itemView) {
            super(itemView);

            fullNameText = itemView.findViewById(R.id.full_name);
            trainedOpening = itemView.findViewById(R.id.trained_opening);
            trainedClosing = itemView.findViewById(R.id.trained_closing);
            relativeLayout = itemView.findViewById(R.id.relative_layout);



        }
    }
}