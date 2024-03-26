package com.example.bank_app.model;

public class BankAccount {

    private int id;
    private int bank_id;
    private int user_id;
    private String account_nbr;
    private int status;
    private Double cash;

    public BankAccount() {

    }

    public BankAccount(int id, int bank_id, int user_id, String account_nbr, int status, Double cash) {
        this.id = id;
        this.bank_id = bank_id;
        this.user_id = user_id;
        this.account_nbr = account_nbr;
        this.status = status;
        this.cash = cash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBankId() {
        return bank_id;
    }

    public void setBankId(int bank_id) {
        this.bank_id = bank_id;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public String getAccountNbr() {
        return account_nbr;
    }

    public void setAccountNbr(String account_nbr) {
        this.account_nbr = account_nbr;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public Double getCash() {
        return cash;
    }

    public void setCash(Double cash) {
        this.cash = cash;
    }

    public void setBankAccount(int id, int bank_id, int user_id, String account_nbr, int status, Double cash) {
        this.id = id;
        this.bank_id = bank_id;
        this.user_id = user_id;
        this.account_nbr = account_nbr;
        this.status = status;
        this.cash = cash;
    }
}
