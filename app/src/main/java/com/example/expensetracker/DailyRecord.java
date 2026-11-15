
package com.example.expensetracker;

public class DailyRecord {

    private long id;
    private String date;
    private double totalIncome;
    private double totalExpenses;

    public DailyRecord(long id, String date,
                       double totalIncome, double totalExpenses) {

        this.id = id;
        this.date = date;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
    }

    public long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public double getTotalExpenses() {
        return totalExpenses;
    }

    public double getBalance() {
        return totalIncome - totalExpenses;
    }
}
