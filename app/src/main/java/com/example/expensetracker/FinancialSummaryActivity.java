package com.example.expensetracker;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class FinancialSummaryActivity extends AppCompatActivity {

    RadioGroup rgSummaryPeriod;

    TextView tvPeriodLabel, tvSummaryDateRange;
    TextView tvFinancialIncome;
    TextView tvFinancialExpenses;
    TextView tvFinancialBalance;
    TextView tvFinancialTransactions;

    Button btnFinancialBackHome;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_financial_summary);

        tvSummaryDateRange = findViewById(R.id.tvSummaryDateRange);

        rgSummaryPeriod = findViewById(R.id.rgSummaryPeriod);

        tvPeriodLabel = findViewById(R.id.tvPeriodLabel);
        tvFinancialIncome = findViewById(R.id.tvFinancialIncome);
        tvFinancialExpenses = findViewById(R.id.tvFinancialExpenses);
        tvFinancialBalance = findViewById(R.id.tvFinancialBalance);
        tvFinancialTransactions =
                findViewById(R.id.tvFinancialTransactions);

        btnFinancialBackHome =
                findViewById(R.id.btnFinancialBackHome);

        databaseHelper = new DatabaseHelper(this);

        // Display today's financial summary
        displayFinancialSummary(1);

        // Change summary when a period is selected
        rgSummaryPeriod.setOnCheckedChangeListener(
                (group, checkedId) -> {

                    if (checkedId == R.id.rbDaily) {

                        tvPeriodLabel.setText("Today's Summary");
                        displayFinancialSummary(1);

                    } else if (checkedId == R.id.rbWeekly) {

                        tvPeriodLabel.setText("This Week's Summary");
                        displayFinancialSummary(2);

                    } else if (checkedId == R.id.rbMonthly) {

                        tvPeriodLabel.setText("This Month's Summary");
                        displayFinancialSummary(3);
                    }
                }
        );

        // Back to Home
        btnFinancialBackHome.setOnClickListener(v -> {

            Intent intent = new Intent(
                    FinancialSummaryActivity.this,
                    MainActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);
            finish();
        });
    }

    private void displayFinancialSummary(int period) {

        displayDateRange(period);

        double[] summary =
                databaseHelper.getFinancialSummary(period);

        double totalIncome = summary[0];
        double totalExpenses = summary[1];
        double balance = summary[2];
        int transactions = (int) summary[3];

        tvFinancialIncome.setText(formatMoney(totalIncome));

        tvFinancialExpenses.setText(
                formatMoney(totalExpenses)
        );

        tvFinancialBalance.setText(formatMoney(balance));

        tvFinancialTransactions.setText(
                String.valueOf(transactions)
        );
    }

    private String formatMoney(double amount) {

        return String.format(
                Locale.US,
                "Rs. %,.2f",
                amount
        );
    }


    private void displayDateRange(int period) {

        Calendar today = Calendar.getInstance();
        Calendar start = (Calendar) today.clone();

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd MMMM yyyy", Locale.US);

        if (period == 1) {

            // Daily
            tvSummaryDateRange.setText(
                    dateFormat.format(today.getTime())
            );

        } else if (period == 2) {

            // Weekly: Monday to today
            int dayOfWeek = start.get(Calendar.DAY_OF_WEEK);
            int daysSinceMonday = (dayOfWeek + 5) % 7;

            start.add(
                    Calendar.DAY_OF_MONTH,
                    -daysSinceMonday
            );

            String dateRange =
                    dateFormat.format(start.getTime()) +
                            " - " +
                            dateFormat.format(today.getTime());

            tvSummaryDateRange.setText(dateRange);

        } else if (period == 3) {

            // Monthly: first day to today
            start.set(Calendar.DAY_OF_MONTH, 1);

            String dateRange =
                    dateFormat.format(start.getTime()) +
                            " - " +
                            dateFormat.format(today.getTime());

            tvSummaryDateRange.setText(dateRange);
        }
    }

}
