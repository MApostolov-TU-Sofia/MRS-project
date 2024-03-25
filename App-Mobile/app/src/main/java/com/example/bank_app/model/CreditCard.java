package com.example.bank_app.model;

import java.util.Date;

public class CreditCard {

    private int id;
    private int bank_account_id;
    private String credit_card_nbr;
    private Number limit;
    private Date valid_to;

    public CreditCard() {

    }

    public CreditCard(int id, int bank_account_id, String credit_card_nbr, Number limit, Date valid_to) {
        this.id = id;
        this.bank_account_id = bank_account_id;
        this.credit_card_nbr = credit_card_nbr;
        this.limit = limit;
        this.valid_to = valid_to;
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

    public String getCredit_card_nbr() {
        return credit_card_nbr;
    }

    public void setCredit_card_nbr(String credit_card_nbr) {
        this.credit_card_nbr = credit_card_nbr;
    }

    public Number getLimit() {
        return limit;
    }

    public void setLimit(Number limit) {
        this.limit = limit;
    }

    public Date getValid_to() {
        return valid_to;
    }

    public void setValid_to(Date valid_to) {
        this.valid_to = valid_to;
    }

    public void setCreditCard(int id, int bank_account_id, String credit_card_nbr, Number limit, Date valid_to) {
        this.id = id;
        this.bank_account_id = bank_account_id;
        this.credit_card_nbr = credit_card_nbr;
        this.limit = limit;
        this.valid_to = valid_to;
    }
}
