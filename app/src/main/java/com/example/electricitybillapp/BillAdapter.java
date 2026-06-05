package com.example.electricitybillapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import java.text.DecimalFormat;
public class BillAdapter extends CursorAdapter{
    private final DecimalFormat df = new DecimalFormat("#0.00");

    public BillAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_bill, parent, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textViewMonth = view.findViewById(R.id.textViewMonth);
        TextView textViewFinalCost = view.findViewById(R.id.textViewFinalCost);

        String month = cursor.getString(cursor.getColumnIndexOrThrow(DataHelper.COL_MONTH));
        double finalCost = cursor.getDouble(cursor.getColumnIndexOrThrow(DataHelper.COL_FINAL_COST));

        textViewMonth.setText(month);
        textViewFinalCost.setText("RM " + df.format(finalCost));
    }
}
