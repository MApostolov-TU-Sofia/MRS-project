package com.example.bank_app.model;

import java.util.Date;

public class Transaction {

    private int id;
    private int bank_account_id;
    private String process;
    private Date date;

    public Transaction() {

    }

    public Transaction(int id, int bank_account_id, String process, Date date) {
        this.id = id;
        this.bank_account_id = bank_account_id;
        this.process = process;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBank_account_id() {
        return bank_account_id;
    }

    public void setBank_account_id(int bank_account_id) {
        this.bank_account_id = bank_account_id;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTransaction(int id, int bank_account_id, String process, Date date) {
        this.id = id;
        this.bank_account_id = bank_account_id;
        this.process = process;
        this.date = date;
    }
}
