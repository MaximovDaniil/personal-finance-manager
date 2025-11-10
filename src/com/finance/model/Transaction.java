package com.finance.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private TransactionType type;
    private double amount;
    private String category;
    private String description;
    private LocalDateTime date;
    
    public Transaction(TransactionType type, double amount, String category, String description) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = LocalDateTime.now();
    }
    
    public String getId() { return id; }
    public TransactionType getType() { return type; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public LocalDateTime getDate() { return date; }
    
    @Override
    public String toString() {
        return String.format("%s: %.2f (%s) - %s", type, amount, category, description);
    }
}