package com.example.expensetracker;

import android.database.Cursor;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExpenseTracker.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE daily_records (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date TEXT NOT NULL, " +
                "salary REAL DEFAULT 0, " +
                "freelance REAL DEFAULT 0, " +
                "business REAL DEFAULT 0, " +
                "other_income REAL DEFAULT 0, " +
                "grocery REAL DEFAULT 0, " +
                "transport REAL DEFAULT 0, " +
                "food REAL DEFAULT 0, " +
                "education REAL DEFAULT 0, " +
                "shopping REAL DEFAULT 0, " +
                "bills REAL DEFAULT 0, " +
                "health REAL DEFAULT 0, " +
                "other_expense REAL DEFAULT 0)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS daily_records");
        onCreate(db);
    }


    public long insertDailyRecord(
            String date,
            double salary,
            double freelance,
            double business,
            double otherIncome,
            double grocery,
            double transport,
            double food,
            double education,
            double shopping,
            double bills,
            double health,
            double otherExpense) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("date", date);
        values.put("salary", salary);
        values.put("freelance", freelance);
        values.put("business", business);
        values.put("other_income", otherIncome);

        values.put("grocery", grocery);
        values.put("transport", transport);
        values.put("food", food);
        values.put("education", education);
        values.put("shopping", shopping);
        values.put("bills", bills);
        values.put("health", health);
        values.put("other_expense", otherExpense);

        long result = db.insert("daily_records", null, values);

        return result;
    }


    public Cursor getDailyRecordById(long recordId) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                "daily_records",
                null,
                "id = ?",
                new String[]{String.valueOf(recordId)},
                null,
                null,
                null
        );
    }


    public ArrayList<DailyRecord> getAllDailyRecords() {

        ArrayList<DailyRecord> recordList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM daily_records ORDER BY date DESC, id DESC",
                null
        );

        try {
            while (cursor.moveToNext()) {

                long id = cursor.getLong(
                        cursor.getColumnIndexOrThrow("id")
                );

                String date = cursor.getString(
                        cursor.getColumnIndexOrThrow("date")
                );

                double totalIncome =
                        cursor.getDouble(cursor.getColumnIndexOrThrow("salary")) +
                                cursor.getDouble(cursor.getColumnIndexOrThrow("freelance")) +
                                cursor.getDouble(cursor.getColumnIndexOrThrow("business")) +
                                cursor.getDouble(cursor.getColumnIndexOrThrow("other_income"));

                double totalExpenses =
                        cursor.getDouble(cursor.getColumnIndexOrThrow("grocery")) +
                                cursor.getDouble(cursor.getColumnIndexOrThrow("transport")) +
                                cursor.getDouble(cursor.getColumnIndexOrThrow("food")) +
                                cursor.getDouble(cursor.getColumnIndexOrThrow("education")) +
                                cursor.getDouble(cursor.getColumnIndexOrThrow("shopping")) +
                                cursor.getDouble(cursor.getColumnIndexOrThrow("bills")) +
                                cursor.getDouble(cursor.getColumnIndexOrThrow("health")) +
                                cursor.getDouble(cursor.getColumnIndexOrThrow("other_expense"));

                DailyRecord record = new DailyRecord(
                        id,
                        date,
                        totalIncome,
                        totalExpenses
                );

                recordList.add(record);
            }
        } finally {
            cursor.close();
        }

        return recordList;
    }


    public boolean deleteDailyRecord(long recordId) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                "daily_records",
                "id = ?",
                new String[]{String.valueOf(recordId)}
        );

        return result > 0;
    }


    public boolean updateDailyRecord(
            long recordId,
            String date,
            double salary,
            double freelance,
            double business,
            double otherIncome,
            double grocery,
            double transport,
            double food,
            double education,
            double shopping,
            double bills,
            double health,
            double otherExpense) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("date", date);

        values.put("salary", salary);
        values.put("freelance", freelance);
        values.put("business", business);
        values.put("other_income", otherIncome);

        values.put("grocery", grocery);
        values.put("transport", transport);
        values.put("food", food);
        values.put("education", education);
        values.put("shopping", shopping);
        values.put("bills", bills);
        values.put("health", health);
        values.put("other_expense", otherExpense);

        int result = db.update(
                "daily_records",
                values,
                "id = ?",
                new String[]{String.valueOf(recordId)}
        );

        return result > 0;
    }


    public double[] getFinancialSummary(int period) {

        double totalIncome = 0;
        double totalExpenses = 0;
        int transactionCount = 0;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        String endDate = dateFormat.format(calendar.getTime());

        if (period == 1) {
            // Daily: today only

        } else if (period == 2) {
            // Weekly: Monday to today
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            int daysSinceMonday =
                    (dayOfWeek + 5) % 7;

            calendar.add(
                    Calendar.DAY_OF_MONTH,
                    -daysSinceMonday
            );

        } else if (period == 3) {
            // Monthly: first day of current month
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        }

        String startDate = dateFormat.format(calendar.getTime());

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM daily_records " +
                        "WHERE date >= ? AND date <= ?",
                new String[]{startDate, endDate}
        );

        try {
            while (cursor.moveToNext()) {

                String[] incomeColumns = {
                        "salary", "freelance",
                        "business", "other_income"
                };

                String[] expenseColumns = {
                        "grocery", "transport", "food",
                        "education", "shopping", "bills",
                        "health", "other_expense"
                };

                for (String column : incomeColumns) {

                    double amount = cursor.getDouble(
                            cursor.getColumnIndexOrThrow(column)
                    );

                    totalIncome += amount;

                    if (amount > 0) {
                        transactionCount++;
                    }
                }

                for (String column : expenseColumns) {

                    double amount = cursor.getDouble(
                            cursor.getColumnIndexOrThrow(column)
                    );

                    totalExpenses += amount;

                    if (amount > 0) {
                        transactionCount++;
                    }
                }
            }
        } finally {
            cursor.close();
        }

        double balance = totalIncome - totalExpenses;

        return new double[]{
                totalIncome,
                totalExpenses,
                balance,
                transactionCount
        };
    }


    public long addOrCombineDailyRecord(
            String date,
            double salary,
            double freelance,
            double business,
            double otherIncome,
            double grocery,
            double transport,
            double food,
            double education,
            double shopping,
            double bills,
            double health,
            double otherExpense) {

        SQLiteDatabase db = this.getWritableDatabase();

        String[] columns = {
                "salary", "freelance", "business", "other_income",
                "grocery", "transport", "food", "education",
                "shopping", "bills", "health", "other_expense"
        };

        double[] newAmounts = {
                salary, freelance, business, otherIncome,
                grocery, transport, food, education,
                shopping, bills, health, otherExpense
        };

        long recordId = -1;

        db.beginTransaction();

        try {
            Cursor cursor = db.query(
                    "daily_records",
                    null,
                    "date = ?",
                    new String[]{date},
                    null,
                    null,
                    "id ASC"
            );

            ContentValues values = new ContentValues();
            values.put("date", date);

            try {
                if (cursor.moveToFirst()) {

                    // Use the oldest record for this date
                    recordId = cursor.getLong(
                            cursor.getColumnIndexOrThrow("id")
                    );

                    double[] combined = newAmounts.clone();

                    // Include all previously saved records
                    do {
                        for (int i = 0; i < columns.length; i++) {
                            combined[i] += cursor.getDouble(
                                    cursor.getColumnIndexOrThrow(columns[i])
                            );
                        }
                    } while (cursor.moveToNext());

                    for (int i = 0; i < columns.length; i++) {
                        values.put(columns[i], combined[i]);
                    }

                    int updated = db.update(
                            "daily_records",
                            values,
                            "id = ?",
                            new String[]{String.valueOf(recordId)}
                    );

                    if (updated != 1) {
                        throw new IllegalStateException(
                                "Failed to update daily record"
                        );
                    }

                    // Remove duplicate rows after combining amounts
                    db.delete(
                            "daily_records",
                            "date = ? AND id != ?",
                            new String[]{
                                    date,
                                    String.valueOf(recordId)
                            }
                    );

                } else {

                    // No record for this date: create a new one
                    for (int i = 0; i < columns.length; i++) {
                        values.put(columns[i], newAmounts[i]);
                    }

                    recordId = db.insertOrThrow(
                            "daily_records",
                            null,
                            values
                    );
                }

            } finally {
                cursor.close();
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

        return recordId;
    }

}
