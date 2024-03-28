package com.example.bank_app.model;

public class Bank {

    private int id;
    private String name;
    private String swift;
    private String address;
    private String description;
    public Bank() {

    }

    public Bank(int id, String name, String swift, String address, String description) {
        this.id = id;
        this.name = name;
        this.swift = swift;
        this.address = address;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSwift() {
        return swift;
    }

    public void setSwift(String swift) {
        this.swift = swift;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBank(int id, String name, String swift, String address, String description) {
        this.id = id;
        this.name = name;
        this.swift = swift;
        this.address = address;
        this.description = description;
    }
}
