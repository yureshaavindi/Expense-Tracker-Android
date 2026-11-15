package com.example.expensetracker;

import android.content.Intent;
import android.widget.Button;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExpenseListActivity extends AppCompatActivity {

    RecyclerView recyclerViewRecords;
    DatabaseHelper databaseHelper;
    ExpenseAdapter expenseAdapter;

    ArrayList<DailyRecord> recordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_expense_list);


        Button btnBackToHome = findViewById(R.id.btnBackToHome);

        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ExpenseListActivity.this,
                    MainActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);
            finish();
        });


        recyclerViewRecords =
                findViewById(R.id.recyclerViewRecords);

        recyclerViewRecords.setLayoutManager(
                new LinearLayoutManager(this)
        );

        databaseHelper = new DatabaseHelper(this);

        loadDailyRecords();
    }

    private void loadDailyRecords() {

        recordList = databaseHelper.getAllDailyRecords();

        expenseAdapter = new ExpenseAdapter(recordList);

        recyclerViewRecords.setAdapter(expenseAdapter);

        if (recordList.isEmpty()) {
            Toast.makeText(
                    this,
                    "No daily records found",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            loadDailyRecords();
        }
    }
}
