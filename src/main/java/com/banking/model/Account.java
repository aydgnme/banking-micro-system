package com.banking.model;

public class Account {
    private String accountNumber;
    private String pin;
    private double balance;
    private String ownerName;

    public Account() {}

    public Account(String accountNumber, String pin, double balance, String ownerName) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = balance;
        this.ownerName = ownerName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
} 