package com.example.bank_app.model;

import java.util.Date;

public class Transaction {

    private int id;
    private int bank_account_id;

    private String bank_account_iban;
    private String to_bank_account_name;
    private String to_bank_account_iban;
    private Number to_bank_account_cash;
    private String to_bank_account_note;
    private String to_bank_account_secondary_note;
    private String process;
    private int status;
    private Date date;

    public Transaction() {

    }

    public Transaction(int id, int bank_account_id, String bank_account_iban, String to_bank_account_name, String to_bank_account_iban, Number to_bank_account_cash, String to_bank_account_note, String to_bank_account_secondary_note, String process, int status, Date date) {
        this.id = id;
        this.bank_account_id = bank_account_id;
        this.bank_account_iban = bank_account_iban;
        this.to_bank_account_name = to_bank_account_name;
        this.to_bank_account_iban = to_bank_account_iban;
        this.to_bank_account_cash = to_bank_account_cash;
        this.to_bank_account_note = to_bank_account_note;
        this.to_bank_account_secondary_note = to_bank_account_secondary_note;
        this.process = process;
        this.status = status;
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

    public String getBank_account_iban() {
        return bank_account_iban;
    }

    public void setBank_account_iban(String bank_account_iban) {
        this.bank_account_iban = bank_account_iban;
    }

    public String getTo_bank_account_name() {
        return to_bank_account_name;
    }

    public void setTo_bank_account_name(String to_bank_account_name) {
        this.to_bank_account_name = to_bank_account_name;
    }

    public String getTo_bank_account_iban() {
        return to_bank_account_iban;
    }

    public void setTo_bank_account_iban(String to_bank_account_iban) {
        this.to_bank_account_iban = to_bank_account_iban;
    }

    public Number getTo_bank_account_cash() {
        return to_bank_account_cash;
    }

    public void setTo_bank_account_cash(Number to_bank_account_cash) {
        this.to_bank_account_cash = to_bank_account_cash;
    }

    public String getTo_bank_account_note() {
        return to_bank_account_note;
    }

    public void setTo_bank_account_note(String to_bank_account_note) {
        this.to_bank_account_note = to_bank_account_note;
    }

    public String getTo_bank_account_secondary_note() {
        return to_bank_account_secondary_note;
    }

    public void setTo_bank_account_secondary_note(String to_bank_account_secondary_note) {
        this.to_bank_account_secondary_note = to_bank_account_secondary_note;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTransaction(int id, int bank_account_id, String bank_account_iban, String to_bank_account_name, String to_bank_account_iban, Number to_bank_account_cash, String to_bank_account_note, String to_bank_account_secondary_note, String process, int status, Date date) {
        this.id = id;
        this.bank_account_id = bank_account_id;
        this.bank_account_iban = bank_account_iban;
        this.to_bank_account_name = to_bank_account_name;
        this.to_bank_account_iban = to_bank_account_iban;
        this.to_bank_account_cash = to_bank_account_cash;
        this.to_bank_account_note = to_bank_account_note;
        this.to_bank_account_secondary_note = to_bank_account_secondary_note;
        this.process = process;
        this.status = status;
        this.date = date;
    }
}
