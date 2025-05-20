package com.example.banking.model;

import java.math.BigDecimal;

public class Account {
    private Long id;
    private BigDecimal balance;
    private String firstName;
    private String lastName;

    public Account() {}

    public Account(Long id, BigDecimal balance, String firstName, String lastName) {
        this.id = id;
        this.balance = balance;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}