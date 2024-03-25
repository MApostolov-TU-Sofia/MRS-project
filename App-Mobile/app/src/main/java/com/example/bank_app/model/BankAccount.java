package com.example.bank_app.model;

public class BankAccount {

    private int id;
    private int bank_id;
    private int user_id;
    private String account_nbr;
    private Number cash;

    public BankAccount() {

    }

    public BankAccount(int id, int bank_id, int user_id, String account_nbr, Number cash) {
        this.id = id;
        this.bank_id = bank_id;
        this.user_id = user_id;
        this.account_nbr = account_nbr;
        this.cash = cash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBank_id() {
        return bank_id;
    }

    public void setBank_id(int bank_id) {
        this.bank_id = bank_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getAccount_nbr() {
        return account_nbr;
    }

    public void setAccount_nbr(String account_nbr) {
        this.account_nbr = account_nbr;
    }

    public Number getCash() {
        return cash;
    }

    public void setCash(Number cash) {
        this.cash = cash;
    }

    public void setBankAccount(int id, int bank_id, int user_id, String account_nbr, Number cash) {
        this.id = id;
        this.bank_id = bank_id;
        this.user_id = user_id;
        this.account_nbr = account_nbr;
        this.cash = cash;
    }
}
