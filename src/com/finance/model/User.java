package com.finance.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String login;
    private String password;
    private double balance;
    private List<Transaction> transactions;
    private Map<String, Double> budgets;
    
    public User(String login, String password) {
        this.login = login;
        this.password = password;
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
        this.budgets = new HashMap<>();
    }
    
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
    
    public void addIncome(double amount, String category, String description) {
        Transaction transaction = new Transaction(TransactionType.INCOME, amount, category, description);
        transactions.add(transaction);
        balance += amount;
    }
    
    public void addExpense(double amount, String category, String description) {
        Transaction transaction = new Transaction(TransactionType.EXPENSE, amount, category, description);
        transactions.add(transaction);
        balance -= amount;
    }
    
    public void setBudget(String category, double limit) {
        budgets.put(category.toLowerCase(), limit);
    }
    
    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public double getBalance() { return balance; }
    public List<Transaction> getTransactions() { return new ArrayList<>(transactions); }
    public Map<String, Double> getBudgets() { return new HashMap<>(budgets); }
    
    public double getTotalIncome() {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
    
    public double getTotalExpenses() {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
    
    public double getExpensesByCategory(String category) {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .filter(t -> t.getCategory().equalsIgnoreCase(category))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
    
    public Map<String, Double> getIncomeByCategory() {
        Map<String, Double> result = new HashMap<>();
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.INCOME) {
                result.put(t.getCategory(), result.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
            }
        }
        return result;
    }
    
    public Map<String, Double> getExpensesByCategory() {
        Map<String, Double> result = new HashMap<>();
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.EXPENSE) {
                result.put(t.getCategory(), result.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
            }
        }
        return result;
    }

    public double getExpensesByCategories(List<String> categories, LocalDate startDate, LocalDate endDate) {
    return transactions.stream()
            .filter(t -> t.getType() == TransactionType.EXPENSE)
            .filter(t -> categories.contains(t.getCategory().toLowerCase()))
            .filter(t -> {
                LocalDate transactionDate = t.getDate().toLocalDate();
                return (startDate == null || !transactionDate.isBefore(startDate)) &&
                       (endDate == null || !transactionDate.isAfter(endDate));
            })
            .mapToDouble(Transaction::getAmount)
            .sum();
    }

    public double getIncomeByCategories(List<String> categories, LocalDate startDate, LocalDate endDate) {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .filter(t -> categories.contains(t.getCategory().toLowerCase()))
                .filter(t -> {
                    LocalDate transactionDate = t.getDate().toLocalDate();
                    return (startDate == null || !transactionDate.isBefore(startDate)) &&
                        (endDate == null || !transactionDate.isAfter(endDate));
                })
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public List<Transaction> getFilteredTransactions(List<String> categories, TransactionType type, LocalDate startDate, LocalDate endDate) {
        return transactions.stream()
                .filter(t -> type == null || t.getType() == type)
                .filter(t -> categories == null || categories.isEmpty() || 
                            categories.contains(t.getCategory().toLowerCase()))
                .filter(t -> {
                    if (startDate == null && endDate == null) return true;
                    LocalDate transactionDate = t.getDate().toLocalDate();
                    return (startDate == null || !transactionDate.isBefore(startDate)) &&
                        (endDate == null || !transactionDate.isAfter(endDate));
                })
                .collect(Collectors.toList());
    }

    public boolean updateBudget(String category, double newLimit) {
    if (budgets.containsKey(category.toLowerCase()) && newLimit >= 0) {
        budgets.put(category.toLowerCase(), newLimit);
        return true;
    }
    return false;
    }

    public boolean removeBudget(String category) {
        return budgets.remove(category.toLowerCase()) != null;
    }

    public boolean updateCategory(String oldCategory, String newCategory, TransactionType type) {
        boolean updated = false;
        for (Transaction transaction : transactions) {
            if (transaction.getCategory().equalsIgnoreCase(oldCategory)) {
                transaction = new Transaction(transaction.getType(), transaction.getAmount(), 
                                            newCategory, transaction.getDescription());
                updated = true;
            }
        }
        
        Double budget = budgets.remove(oldCategory.toLowerCase());
        if (budget != null) {
            budgets.put(newCategory.toLowerCase(), budget);
            updated = true;
        }
        
        return updated;
    }
        public boolean hasCategory(String categoryName) {
        return transactions.stream()
                .anyMatch(t -> t.getCategory().equalsIgnoreCase(categoryName));
    }
}