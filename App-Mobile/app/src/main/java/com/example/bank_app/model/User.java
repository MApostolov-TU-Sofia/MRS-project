package com.example.bank_app.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String salt;
    private int bank_id;
    private int role_id;
    private String name;
    private String address;
    private String phone_nbr;
    private String job;

    public User() {

    }

    public User(int id, String username, String password, String salt, int bank_id, int role_id, String name, String address, String phone_nbr, String job) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.bank_id = bank_id;
        this.role_id = role_id;
        this.name = name;
        this.address = address;
        this.phone_nbr = phone_nbr;
        this.job = job;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public int getBank_id() {
        return bank_id;
    }

    public void setBank_id(int bank_id) {
        this.bank_id = bank_id;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone_nbr() {
        return phone_nbr;
    }

    public void setPhone_nbr(String phone_nbr) {
        this.phone_nbr = phone_nbr;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public void setUser(int id, String username, String password, String salt, int bank_id, int role_id, String name, String address, String phone_nbr, String job) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.bank_id = bank_id;
        this.role_id = role_id;
        this.name = name;
        this.address = address;
        this.phone_nbr = phone_nbr;
        this.job = job;
    }
}
