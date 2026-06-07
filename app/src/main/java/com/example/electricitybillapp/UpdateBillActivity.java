package com.example.electricitybillapp;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.text.DecimalFormat;

public class UpdateBillActivity extends AppCompatActivity{
    private Spinner spinnerMonth;
    private EditText editTextUnit, editTextRebate;
    private TextView textViewTotalCharges, textViewFinalCost;
    private Button buttonCalculate, buttonUpdate, buttonCancel;
    private DataHelper dbHelper;
    private final DecimalFormat df = new DecimalFormat("#0.00");
    private int billId;

    private final String[] months = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bill);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Update Bill");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        billId = getIntent().getIntExtra("bill_id", 0);
        dbHelper = new DataHelper(this);

        spinnerMonth = findViewById(R.id.spinnerMonth);
        editTextUnit = findViewById(R.id.editTextUnit);
        editTextRebate = findViewById(R.id.editTextRebate);
        textViewTotalCharges = findViewById(R.id.textViewTotalCharges);
        textViewFinalCost = findViewById(R.id.textViewFinalCost);
        buttonCalculate = findViewById(R.id.buttonCalculate);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonCancel = findViewById(R.id.buttonCancel);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, months);
        spinnerMonth.setAdapter(adapter);

        loadBillData();

        // Recalculate when the user leaves the unit or rebate field
        editTextUnit.setOnFocusChangeListener((v, hasFocus) -> { if (!hasFocus) recalculate(); });
        editTextRebate.setOnFocusChangeListener((v, hasFocus) -> { if (!hasFocus) recalculate(); });

        // --- ADDED: Calculate button explicitly triggers recalc ---
        buttonCalculate.setOnClickListener(v -> recalculate());

        buttonUpdate.setOnClickListener(v -> updateBill());
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void loadBillData() {
        Cursor cursor = dbHelper.getBillById(billId);
        if (cursor != null && cursor.moveToFirst()) {
            String month = cursor.getString(cursor.getColumnIndexOrThrow(DataHelper.COL_MONTH));
            int unit = cursor.getInt(cursor.getColumnIndexOrThrow(DataHelper.COL_UNIT));
            double rebate = cursor.getDouble(cursor.getColumnIndexOrThrow(DataHelper.COL_REBATE));

            for (int i = 0; i < months.length; i++) {
                if (months[i].equals(month)) {
                    spinnerMonth.setSelection(i);
                    break;
                }
            }
            editTextUnit.setText(String.valueOf(unit));
            editTextRebate.setText(String.valueOf(rebate));
            recalculate();
            cursor.close();
        }
    }

    @SuppressLint("SetTextI18n")
    private void recalculate() {
        String unitStr = editTextUnit.getText().toString().trim();
        String rebateStr = editTextRebate.getText().toString().trim();
        if (!unitStr.isEmpty()) {
            int unit = Integer.parseInt(unitStr);
            double rebate = rebateStr.isEmpty() ? 0 : Double.parseDouble(rebateStr);
            double totalCharges = calculateTotalCharges(unit);
            double finalCost = totalCharges - (totalCharges * rebate / 100);
            textViewTotalCharges.setText("RM " + df.format(totalCharges));
            textViewFinalCost.setText("RM " + df.format(finalCost));
        }
    }

    private double calculateTotalCharges(int unit) {
        double total = 0;
        int remaining = unit;
        if (remaining > 0) {
            int b = Math.min(remaining, 200);
            total += b * 0.218;
            remaining -= b;
        }
        if (remaining > 0) {
            int b = Math.min(remaining, 100);
            total += b * 0.334;
            remaining -= b;
        }
        if (remaining > 0) {
            int b = Math.min(remaining, 300);
            total += b * 0.516;
            remaining -= b;
        }
        if (remaining > 0) total += remaining * 0.546;
        return total;
    }

    private void updateBill() {
        String month = months[spinnerMonth.getSelectedItemPosition()];
        int unit = Integer.parseInt(editTextUnit.getText().toString());
        double totalCharges = Double.parseDouble(
                textViewTotalCharges.getText().toString().replace("RM ", ""));
        double rebate = editTextRebate.getText().toString().isEmpty() ? 0 :
                Double.parseDouble(editTextRebate.getText().toString());
        double finalCost = Double.parseDouble(
                textViewFinalCost.getText().toString().replace("RM ", ""));

        int result = dbHelper.updateBill(billId, month, unit, totalCharges, rebate, finalCost);
        if (result > 0) {
            Toast.makeText(this, "Bill updated successfully!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
