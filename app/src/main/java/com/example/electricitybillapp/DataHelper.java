package com.example.electricitybillapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "electricity_bill.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "bills";
    public static final String COL_ID = "id";
    public static final String COL_MONTH = "month";
    public static final String COL_UNIT = "unit";
    public static final String COL_TOTAL_CHARGES = "total_charges";
    public static final String COL_REBATE = "rebate";
    public static final String COL_FINAL_COST = "final_cost";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_MONTH + " TEXT NOT NULL, " +
                    COL_UNIT + " INTEGER NOT NULL, " +
                    COL_TOTAL_CHARGES + " REAL NOT NULL, " +
                    COL_REBATE + " REAL NOT NULL, " +
                    COL_FINAL_COST + " REAL NOT NULL" +
                    ")";

    public DataHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertBill(String month, int unit, double totalCharges, double rebate, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MONTH, month);
        values.put(COL_UNIT, unit);
        values.put(COL_TOTAL_CHARGES, totalCharges);
        values.put(COL_REBATE, rebate);
        values.put(COL_FINAL_COST, finalCost);
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public Cursor getAllBills() {
        SQLiteDatabase db = this.getReadableDatabase();
        // Return both 'id' (for your own use) and '_id' (for CursorAdapter)
        return db.rawQuery("SELECT " + COL_ID + ", " + COL_ID + " as _id, " +
                COL_MONTH + ", " + COL_FINAL_COST + " FROM " + TABLE_NAME, null);
    }

    public Cursor getBillById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_ID + " = " + id, null);
    }

    public int updateBill(int id, String month, int unit, double totalCharges, double rebate, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MONTH, month);
        values.put(COL_UNIT, unit);
        values.put(COL_TOTAL_CHARGES, totalCharges);
        values.put(COL_REBATE, rebate);
        values.put(COL_FINAL_COST, finalCost);
        return db.update(TABLE_NAME, values, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deleteBill(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}
