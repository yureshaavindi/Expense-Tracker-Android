package com.example.expensetracker;

import android.widget.Toast;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class DailySummaryActivity extends AppCompatActivity {

    TextView tvSummaryDate, tvTotalIncome;
    TextView tvTotalExpenses, tvDailyBalance;
    TextView tvTransactionCount;

    LinearLayout layoutCategoryDetails;
    Button btnBackHome, btnViewExpenses;

    DatabaseHelper databaseHelper;

    String[] incomeColumns = {
            "salary", "freelance",
            "business", "other_income"
    };

    String[] expenseColumns = {
            "grocery", "transport", "food",
            "education", "shopping", "bills",
            "health", "other_expense"
    };

    String[] incomeNames = {
            "Salary", "Freelance",
            "Business", "Other Income"
    };

    String[] expenseNames = {
            "Grocery", "Transport", "Food",
            "Education", "Shopping", "Bills",
            "Health", "Other Expense"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_daily_summary);

        tvSummaryDate = findViewById(R.id.tvSummaryDate);
        tvTotalIncome = findViewById(R.id.tvTotalIncome);
        tvTotalExpenses = findViewById(R.id.tvTotalExpenses);
        tvDailyBalance = findViewById(R.id.tvDailyBalance);
        tvTransactionCount = findViewById(R.id.tvTransactionCount);

        layoutCategoryDetails =
                findViewById(R.id.layoutCategoryDetails);


        btnBackHome = findViewById(R.id.btnBackHome);
        btnViewExpenses = findViewById(R.id.btnViewExpenses);

        databaseHelper = new DatabaseHelper(this);

        long recordId = getIntent().getLongExtra(
                "record_id", -1
        );

        if (recordId == -1) {

            Toast.makeText(
                    this,
                    "Error: Record ID not received",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }
        displayRecord(recordId);


        btnBackHome.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DailySummaryActivity.this,
                    MainActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);
            finish();
        });

        btnViewExpenses.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DailySummaryActivity.this,
                    ExpenseListActivity.class
            );

            startActivity(intent);
        });

    }

    private void displayRecord(long recordId) {

        try (Cursor cursor =
                     databaseHelper.getDailyRecordById(recordId)) {

            if (cursor == null || !cursor.moveToFirst()) {
                finish();
                return;
            }

            String date = cursor.getString(
                    cursor.getColumnIndexOrThrow("date")
            );

            tvSummaryDate.setText(date);

            double totalIncome = 0;
            double totalExpenses = 0;
            int transactionCount = 0;

            layoutCategoryDetails.removeAllViews();

            for (int i = 0; i < incomeColumns.length; i++) {

                double amount = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                incomeColumns[i]
                        )
                );

                totalIncome += amount;

                if (amount > 0) {
                    transactionCount++;
                    addCategoryRow(incomeNames[i], amount, true);
                }
            }

            for (int i = 0; i < expenseColumns.length; i++) {

                double amount = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                expenseColumns[i]
                        )
                );

                totalExpenses += amount;

                if (amount > 0) {
                    transactionCount++;
                    addCategoryRow(expenseNames[i], amount, false);
                }
            }

            double balance = totalIncome - totalExpenses;

            tvTotalIncome.setText(formatMoney(totalIncome));
            tvTotalExpenses.setText(formatMoney(totalExpenses));
            tvDailyBalance.setText(formatMoney(balance));
            tvTransactionCount.setText(
                    String.valueOf(transactionCount)
            );
        }
    }

    private void addCategoryRow(
            String category,
            double amount,
            boolean isIncome) {

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        row.setPadding(0, 12, 0, 12);

        TextView nameText = new TextView(this);
        nameText.setText(category);
        nameText.setTextSize(15);
        nameText.setTextColor(0xFF334155);

        LinearLayout.LayoutParams nameParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        row.addView(nameText, nameParams);

        TextView amountText = new TextView(this);
        amountText.setText(formatMoney(amount));
        amountText.setTextSize(15);

        amountText.setTextColor(
                isIncome ? 0xFF15803D : 0xFFDC2626
        );

        row.addView(amountText);

        layoutCategoryDetails.addView(row);
    }

    private String formatMoney(double amount) {
        return String.format(
                Locale.US,
                "Rs. %,.2f",
                amount
        );
    }
}
