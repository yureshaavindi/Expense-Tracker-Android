package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnAddExpense, btnViewExpenses, btnFinancialSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btnAddExpense = findViewById(R.id.btnAddExpense);
        btnViewExpenses = findViewById(R.id.btnViewExpenses);

        btnAddExpense.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    AddExpenseActivity.class
            );

            startActivity(intent);
        });

        btnViewExpenses.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    ExpenseListActivity.class
            );

            startActivity(intent);
        });

        btnFinancialSummary = findViewById(R.id.btnFinancialSummary);


        btnFinancialSummary.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    FinancialSummaryActivity.class
            );

            startActivity(intent);
        });


    }
}
