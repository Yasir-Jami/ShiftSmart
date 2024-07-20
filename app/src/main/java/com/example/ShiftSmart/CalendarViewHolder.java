package com.example.ShiftSmart;




import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public final TextView dayOfMonth;
    public View redShiftIndicator, greenShiftIndicator;
    private final CalendarAdapter.OnItemListener onItemListener;

    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener)
    {
        super(itemView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        redShiftIndicator = itemView.findViewById(R.id.redShiftIndicator);
        greenShiftIndicator = itemView.findViewById(R.id.greenShiftIndicator);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        Log.d("CalendarViewHolder", "Item clicked: " + getAdapterPosition());
        onItemListener.onItemClick(getAdapterPosition(), (String) dayOfMonth.getText());
    }
}
