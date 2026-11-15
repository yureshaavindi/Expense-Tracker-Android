package com.example.expensetracker;

import android.database.Cursor;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {

    EditText etDate;
    EditText etSalary, etFreelance, etBusiness, etOtherIncome;
    EditText etGrocery, etTransport, etFood, etEducation;
    EditText etShopping, etBills, etHealth, etOtherExpense;

    Button btnSaveExpense;
    DatabaseHelper databaseHelper;

    long editRecordId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Connect XML fields
        etDate = findViewById(R.id.etDate);

        etSalary = findViewById(R.id.etSalary);
        etFreelance = findViewById(R.id.etFreelance);
        etBusiness = findViewById(R.id.etBusiness);
        etOtherIncome = findViewById(R.id.etOtherIncome);

        etGrocery = findViewById(R.id.etGrocery);
        etTransport = findViewById(R.id.etTransport);
        etFood = findViewById(R.id.etFood);
        etEducation = findViewById(R.id.etEducation);
        etShopping = findViewById(R.id.etShopping);
        etBills = findViewById(R.id.etBills);
        etHealth = findViewById(R.id.etHealth);
        etOtherExpense = findViewById(R.id.etOtherExpense);

        btnSaveExpense = findViewById(R.id.btnSaveExpense);

        // Initialize SQLite database
        databaseHelper = new DatabaseHelper(this);

        editRecordId = getIntent().getLongExtra("edit_record_id", -1);

        // Set today's date
        Calendar calendar = Calendar.getInstance();

        etDate.setText(String.format(
                Locale.US,
                "%04d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
        ));

        // Date picker
        etDate.setOnClickListener(v -> {

            DatePickerDialog datePicker = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {

                        String selectedDate = String.format(
                                Locale.US,
                                "%04d-%02d-%02d",
                                year,
                                month + 1,
                                dayOfMonth
                        );

                        etDate.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePicker.show();
        });

        // Check if user is editing an existing record
        if (editRecordId != -1) {
            loadRecordForEditing();
        }

        // Save button
        btnSaveExpense.setOnClickListener(v -> saveDailyRecord());


    }

    // Convert amount text into a number
    private double getAmount(EditText field) {

        String text = field.getText().toString().trim();

        if (text.isEmpty()) {
            return 0.0;
        }

        return Double.parseDouble(text);
    }

    // Save daily record to SQLite
    private void saveDailyRecord() {

        String date = etDate.getText().toString().trim();

        if (date.isEmpty()) {
            Toast.makeText(this,
                    "Please select a date",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double salary = getAmount(etSalary);
            double freelance = getAmount(etFreelance);
            double business = getAmount(etBusiness);
            double otherIncome = getAmount(etOtherIncome);

            double grocery = getAmount(etGrocery);
            double transport = getAmount(etTransport);
            double food = getAmount(etFood);
            double education = getAmount(etEducation);
            double shopping = getAmount(etShopping);
            double bills = getAmount(etBills);
            double health = getAmount(etHealth);
            double otherExpense = getAmount(etOtherExpense);

            double[] amounts = {
                    salary, freelance, business, otherIncome,
                    grocery, transport, food, education,
                    shopping, bills, health, otherExpense
            };

            boolean hasAmount = false;

            for (double amount : amounts) {

                if (Double.isNaN(amount)
                        || Double.isInfinite(amount)
                        || amount < 0) {

                    Toast.makeText(this,
                            "Enter valid non-negative amounts",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (amount > 0) {
                    hasAmount = true;
                }
            }

            if (!hasAmount) {
                Toast.makeText(this,
                        "Enter at least one amount",
                        Toast.LENGTH_SHORT).show();
                return;
            }


            long recordId;

            if (editRecordId != -1) {

                // UPDATE existing record
                boolean updated = databaseHelper.updateDailyRecord(
                        editRecordId,
                        date,
                        salary,
                        freelance,
                        business,
                        otherIncome,
                        grocery,
                        transport,
                        food,
                        education,
                        shopping,
                        bills,
                        health,
                        otherExpense
                );

                if (updated) {
                    recordId = editRecordId;
                } else {
                    recordId = -1;
                }

            } else {

                // INSERT new record
                recordId = databaseHelper.addOrCombineDailyRecord(
                        date,
                        salary,
                        freelance,
                        business,
                        otherIncome,
                        grocery,
                        transport,
                        food,
                        education,
                        shopping,
                        bills,
                        health,
                        otherExpense
                );
            }

// Open Daily Summary after saving or updating
            if (recordId != -1) {

                String message = (editRecordId != -1)
                        ? "Daily record updated successfully!"
                        : "Daily record saved successfully!";

                Toast.makeText(
                        this,
                        message,
                        Toast.LENGTH_SHORT
                ).show();

                Intent intent = new Intent(
                        AddExpenseActivity.this,
                        DailySummaryActivity.class
                );

                intent.putExtra("record_id", recordId);

                startActivity(intent);
                finish();

            } else {

                Toast.makeText(
                        this,
                        "Failed to save or update record",
                        Toast.LENGTH_SHORT
                ).show();
            }


        } catch (NumberFormatException e) {

            Toast.makeText(this,
                    "Please enter valid numbers",
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void loadRecordForEditing() {

        try (Cursor cursor =
                     databaseHelper.getDailyRecordById(editRecordId)) {

            if (cursor == null || !cursor.moveToFirst()) {
                Toast.makeText(this,
                        "Record not found",
                        Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            etDate.setText(cursor.getString(
                    cursor.getColumnIndexOrThrow("date")
            ));

            setAmount(etSalary, cursor, "salary");
            setAmount(etFreelance, cursor, "freelance");
            setAmount(etBusiness, cursor, "business");
            setAmount(etOtherIncome, cursor, "other_income");

            setAmount(etGrocery, cursor, "grocery");
            setAmount(etTransport, cursor, "transport");
            setAmount(etFood, cursor, "food");
            setAmount(etEducation, cursor, "education");
            setAmount(etShopping, cursor, "shopping");
            setAmount(etBills, cursor, "bills");
            setAmount(etHealth, cursor, "health");
            setAmount(etOtherExpense, cursor, "other_expense");

            btnSaveExpense.setText("Update Daily Record");
        }
    }

    private void setAmount(
            EditText field,
            Cursor cursor,
            String column) {

        double amount = cursor.getDouble(
                cursor.getColumnIndexOrThrow(column)
        );

        if (amount == 0) {
            field.setText("");
        } else {
            field.setText(String.valueOf(amount));
        }
    }

}
