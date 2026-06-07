package com.example.electricitybillapp;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.text.DecimalFormat;

public class ViewBillActivity extends AppCompatActivity{

    private TextView textViewMonth, textViewUnit, textViewTotalCharges, textViewRebate, textViewFinalCost;
    private DataHelper dbHelper;
    private final DecimalFormat df = new DecimalFormat("#0.00");

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bill);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Bill Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        int billId = getIntent().getIntExtra("bill_id", 0);

        textViewMonth = findViewById(R.id.textViewMonth);
        textViewUnit = findViewById(R.id.textViewUnit);
        textViewTotalCharges = findViewById(R.id.textViewTotalCharges);
        textViewRebate = findViewById(R.id.textViewRebate);
        textViewFinalCost = findViewById(R.id.textViewFinalCost);

        dbHelper = new DataHelper(this);
        Cursor cursor = dbHelper.getBillById(billId);
        if (cursor != null && cursor.moveToFirst()) {
            String month = cursor.getString(cursor.getColumnIndexOrThrow(DataHelper.COL_MONTH));
            int unit = cursor.getInt(cursor.getColumnIndexOrThrow(DataHelper.COL_UNIT));
            double totalCharges = cursor.getDouble(cursor.getColumnIndexOrThrow(DataHelper.COL_TOTAL_CHARGES));
            double rebate = cursor.getDouble(cursor.getColumnIndexOrThrow(DataHelper.COL_REBATE));
            double finalCost = cursor.getDouble(cursor.getColumnIndexOrThrow(DataHelper.COL_FINAL_COST));

            textViewMonth.setText(month);
            textViewUnit.setText(unit + " kWh");
            textViewTotalCharges.setText("RM " + df.format(totalCharges));
            textViewRebate.setText(rebate + "%");
            textViewFinalCost.setText("RM " + df.format(finalCost));
            cursor.close();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
