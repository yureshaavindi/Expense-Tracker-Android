package com.example.expensetracker;

import android.app.AlertDialog;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class ExpenseAdapter
        extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private final ArrayList<DailyRecord> recordList;

    public ExpenseAdapter(ArrayList<DailyRecord> recordList) {
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_daily_record, parent, false);

        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ExpenseViewHolder holder, int position) {

        DailyRecord record = recordList.get(position);

        holder.tvRecordDate.setText(record.getDate());

        holder.tvRecordIncome.setText(
                formatMoney(record.getTotalIncome())
        );

        holder.tvRecordExpenses.setText(
                formatMoney(record.getTotalExpenses())
        );

        holder.tvRecordBalance.setText(
                formatMoney(record.getBalance())
        );

        holder.btnViewRecord.setOnClickListener(v -> {

            Intent intent = new Intent(
                    v.getContext(),
                    DailySummaryActivity.class
            );

            intent.putExtra("record_id", record.getId());

            v.getContext().startActivity(intent);
        });


        holder.btnEditRecord.setOnClickListener(v -> {

            Intent intent = new Intent(
                    v.getContext(),
                    AddExpenseActivity.class
            );

            intent.putExtra("edit_record_id", record.getId());

            v.getContext().startActivity(intent);
        });

        holder.btnDeleteRecord.setOnClickListener(v -> {

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Daily Record")
                    .setMessage(
                            "Are you sure you want to delete this record?"
                    )
                    .setPositiveButton("Delete", (dialog, which) -> {

                        DatabaseHelper databaseHelper =
                                new DatabaseHelper(v.getContext());

                        boolean deleted =
                                databaseHelper.deleteDailyRecord(
                                        record.getId()
                                );

                        databaseHelper.close();

                        if (deleted) {

                            int currentPosition =
                                    holder.getBindingAdapterPosition();

                            if (currentPosition !=
                                    RecyclerView.NO_POSITION) {

                                recordList.remove(currentPosition);

                                notifyItemRemoved(currentPosition);
                            }

                            Toast.makeText(
                                    v.getContext(),
                                    "Record deleted successfully",
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else {

                            Toast.makeText(
                                    v.getContext(),
                                    "Failed to delete record",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });


    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    private String formatMoney(double amount) {
        return String.format(Locale.US, "Rs. %,.2f", amount);
    }

    public static class ExpenseViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvRecordDate, tvRecordIncome;
        TextView tvRecordExpenses, tvRecordBalance;

        Button btnViewRecord, btnEditRecord, btnDeleteRecord;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);

            tvRecordDate = itemView.findViewById(R.id.tvRecordDate);
            tvRecordIncome = itemView.findViewById(R.id.tvRecordIncome);
            tvRecordExpenses = itemView.findViewById(R.id.tvRecordExpenses);
            tvRecordBalance = itemView.findViewById(R.id.tvRecordBalance);

            btnViewRecord = itemView.findViewById(R.id.btnViewRecord);
            btnEditRecord = itemView.findViewById(R.id.btnEditRecord);
            btnDeleteRecord = itemView.findViewById(R.id.btnDeleteRecord);
        }
    }
}
