package com.example.electricitybillapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.text.DecimalFormat;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerMonth;
    private EditText editTextUnit, editTextRebate;
    private TextView textViewTotalCharges, textViewFinalCost;
    private Button buttonCalculate, buttonSave, buttonViewList, buttonAbout;
    private DataHelper dbHelper;
    private final DecimalFormat df = new DecimalFormat("#0.00");

    private final String[] months = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Electricity Bill Estimator");
        }

        dbHelper = new DataHelper(this);

        spinnerMonth = findViewById(R.id.spinnerMonth);
        editTextUnit = findViewById(R.id.editTextUnit);
        editTextRebate = findViewById(R.id.editTextRebate);
        textViewTotalCharges = findViewById(R.id.textViewTotalCharges);
        textViewFinalCost = findViewById(R.id.textViewFinalCost);
        buttonCalculate = findViewById(R.id.buttonCalculate);
        buttonSave = findViewById(R.id.buttonSave);
        buttonViewList = findViewById(R.id.buttonViewList);
        buttonAbout = findViewById(R.id.buttonAbout);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, months);
        spinnerMonth.setAdapter(adapter);

        buttonCalculate.setOnClickListener(v -> calculateBill());
        buttonSave.setOnClickListener(v -> saveBillToDatabase());
        buttonViewList.setOnClickListener(v -> {
            android.content.Intent intent = new Intent(MainActivity.this, BillListActivity.class);
            startActivity(intent);
        });

        buttonAbout.setOnClickListener(v -> {
            android.content.Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });
    }

    @SuppressLint("SetTextI18n")
    private void calculateBill() {
        String unitStr = editTextUnit.getText().toString().trim();
        String rebateStr = editTextRebate.getText().toString().trim();

        if (unitStr.isEmpty()) {
            Toast.makeText(this, "Please enter electricity unit (kWh)", Toast.LENGTH_SHORT).show();
            editTextUnit.requestFocus();
            return;
        }

        int unit = Integer.parseInt(unitStr);
        if (unit < 1 || unit > 1000) {
            Toast.makeText(this, "Unit must be between 1 and 1000 kWh", Toast.LENGTH_SHORT).show();
            return;
        }

        double rebate = 0;
        if (!rebateStr.isEmpty()) {
            rebate = Double.parseDouble(rebateStr);
            if (rebate < 0 || rebate > 5) {
                Toast.makeText(this, "Rebate must be between 0% and 5%", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        double totalCharges = calculateTotalCharges(unit);
        double finalCost = totalCharges - (totalCharges * rebate / 100);

        textViewTotalCharges.setText("RM " + df.format(totalCharges));
        textViewFinalCost.setText("RM " + df.format(finalCost));
        buttonSave.setEnabled(true);
    }

    private double calculateTotalCharges(int unit) {
        double total = 0;
        int remaining = unit;

        if (remaining > 0) {
            int block1 = Math.min(remaining, 200);
            total += block1 * 0.218;
            remaining -= block1;
        }
        if (remaining > 0) {
            int block2 = Math.min(remaining, 100);
            total += block2 * 0.334;
            remaining -= block2;
        }
        if (remaining > 0) {
            int block3 = Math.min(remaining, 300);
            total += block3 * 0.516;
            remaining -= block3;
        }
        if (remaining > 0) {
            total += remaining * 0.546;
        }
        return total;
    }

    @SuppressLint("SetTextI18n")
    private void saveBillToDatabase() {
        String month = months[spinnerMonth.getSelectedItemPosition()];
        int unit = Integer.parseInt(editTextUnit.getText().toString());
        double totalCharges = Double.parseDouble(
                textViewTotalCharges.getText().toString().replace("RM ", ""));
        double rebate = editTextRebate.getText().toString().isEmpty() ? 0 :
                Double.parseDouble(editTextRebate.getText().toString());
        double finalCost = Double.parseDouble(
                textViewFinalCost.getText().toString().replace("RM ", ""));

        long id = dbHelper.insertBill(month, unit, totalCharges, rebate, finalCost);

        if (id > 0) {
            Toast.makeText(this, "Bill saved successfully!", Toast.LENGTH_SHORT).show();
            buttonSave.setEnabled(false);
            editTextUnit.setText("");
            editTextRebate.setText("");
            textViewTotalCharges.setText("RM 0.00");
            textViewFinalCost.setText("RM 0.00");
        } else {
            Toast.makeText(this, "Failed to save bill", Toast.LENGTH_SHORT).show();
        }
    }
}