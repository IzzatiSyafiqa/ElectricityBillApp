package com.example.electricitybillapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class BillListActivity extends AppCompatActivity {
    private ListView listViewBills;
    private DataHelper dbHelper;
    private Cursor cursor;
    private static final String TAG = "BillListActivity";

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_bill_list);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Bill History");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            dbHelper = new DataHelper(this);
            listViewBills = findViewById(R.id.listViewBills);

            refreshList();

            listViewBills.setOnItemClickListener((parent, view, position, id) -> {
                if (cursor == null || cursor.isClosed() || cursor.getCount() == 0) {
                    refreshList();
                    return;
                }
                cursor.moveToPosition(position);
                final int billId = cursor.getInt(cursor.getColumnIndexOrThrow(DataHelper.COL_ID));
                final String month = cursor.getString(cursor.getColumnIndexOrThrow(DataHelper.COL_MONTH));

                final CharSequence[] options = {"View Details", "Update", "Delete"};
                AlertDialog.Builder builder = new AlertDialog.Builder(BillListActivity.this);
                builder.setTitle("Select Option for " + month);
                builder.setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Intent viewIntent = new Intent(BillListActivity.this, ViewBillActivity.class);
                            viewIntent.putExtra("bill_id", billId);
                            startActivity(viewIntent);
                            break;
                        case 1:
                            Intent updateIntent = new Intent(BillListActivity.this, UpdateBillActivity.class);
                            updateIntent.putExtra("bill_id", billId);
                            startActivityForResult(updateIntent, 1);
                            break;
                        case 2:
                            confirmDelete(billId);
                            break;
                    }
                });
                builder.show();
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error in onCreate", e);
            finish();
        }
    }

    private void refreshList() {
        // Close previous cursor to avoid leak
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        cursor = dbHelper.getAllBills();
        if (cursor != null && cursor.getCount() > 0) {
            BillAdapter adapter = new BillAdapter(this, cursor);
            listViewBills.setAdapter(adapter);
        } else {
            listViewBills.setAdapter(null);
            Toast.makeText(this, "No bills found. Please add bills from main page.", Toast.LENGTH_LONG).show();
        }
    }

    private void confirmDelete(final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Bill");
        builder.setMessage("Are you sure you want to delete this bill?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            dbHelper.deleteBill(id);
            Toast.makeText(BillListActivity.this, "Bill deleted", Toast.LENGTH_SHORT).show();
            refreshList();
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) refreshList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}