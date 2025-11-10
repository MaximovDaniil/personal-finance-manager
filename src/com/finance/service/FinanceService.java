package com.finance.service;

import com.finance.model.TransactionType;
import com.finance.model.User;
import com.finance.storage.DataStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FinanceService {
    private final AuthService authService;
    private final DataStorage dataStorage;
    
    public FinanceService(AuthService authService, DataStorage dataStorage) {
        this.authService = authService;
        this.dataStorage = dataStorage;
    }
    
    public boolean addIncome(double amount, String category, String description) {
        if (!authService.isLoggedIn() || amount <= 0) {
            return false;
        }
        
        User user = authService.getCurrentUser();
        user.addIncome(amount, category, description);
        return true;
    }
    
    public boolean addExpense(double amount, String category, String description) {
        if (!authService.isLoggedIn() || amount <= 0) {
            return false;
        }
        
        User user = authService.getCurrentUser();
        user.addExpense(amount, category, description);
        
        // Проверяем бюджет сразу после добавления расхода
        checkBudgetAfterExpense(category);
        return true;
    }
    
    public boolean setBudget(String category, double limit) {
        if (!authService.isLoggedIn() || limit < 0) {
            return false;
        }
        
        User user = authService.getCurrentUser();
        user.setBudget(category, limit);
        return true;
    }
    
    public boolean updateBudget(String category, double newLimit) {
        if (!authService.isLoggedIn() || newLimit < 0) {
            return false;
        }
        
        User user = authService.getCurrentUser();
        return user.updateBudget(category, newLimit);
    }
    
    public void showStatistics() {
        if (!authService.isLoggedIn()) {
            System.out.println("❌ Необходимо войти в систему.");
            return;
        }
        
        User user = authService.getCurrentUser();
        
        System.out.println("\n=== ФИНАНСОВЫЙ ОТЧЕТ ===");
        System.out.printf("Общий доход: %,10.2f₽%n", user.getTotalIncome());
        System.out.printf("Общие расходы: %,10.2f₽%n", user.getTotalExpenses());
        System.out.printf("Текущий баланс: %,10.2f₽%n", user.getBalance());
        
        // Доходы по категориям
        Map<String, Double> incomeByCategory = user.getIncomeByCategory();
        if (!incomeByCategory.isEmpty()) {
            System.out.println("\n--- Доходы по категориям ---");
            incomeByCategory.forEach((category, amount) -> 
                System.out.printf("  %-20s: %,10.2f₽%n", category, amount));
        }
        
        // Расходы по категориям
        Map<String, Double> expensesByCategory = user.getExpensesByCategory();
        if (!expensesByCategory.isEmpty()) {
            System.out.println("\n--- Расходы по категориям ---");
            expensesByCategory.forEach((category, amount) -> 
                System.out.printf("  %-20s: %,10.2f₽%n", category, amount));
        }
        
        // Статус бюджетов
        Map<String, Double> budgets = user.getBudgets();
        if (!budgets.isEmpty()) {
            System.out.println("\n--- Статус бюджетов ---");
            budgets.forEach((category, limit) -> {
                double spent = user.getExpensesByCategory(category);
                double remaining = limit - spent;
                String statusIcon = spent > limit ? "🚨" : (remaining < limit * 0.2 ? "⚠️" : "✅");
                System.out.printf("%s %-20s: Лимит: %,8.2f₽ | Потрачено: %,8.2f₽ | Осталось: %,8.2f₽%n",
                        statusIcon, category, limit, spent, remaining);
            });
        }
    }
    
    public List<String> checkAlerts() {
        List<String> alerts = new ArrayList<>();
        
        if (!authService.isLoggedIn()) {
            return alerts;
        }
        
        User user = authService.getCurrentUser();
        
        // Проверка отрицательного баланса
        if (user.getBalance() < 0) {
            alerts.add("🚨 ВНИМАНИЕ: Ваш баланс отрицательный! Расходы превысили доходы.");
        }
        
        // Проверка нулевого баланса
        if (user.getBalance() == 0) {
            alerts.add("⚠️ Внимание: Ваш баланс равен нулю.");
        }
        
        // Проверка бюджетов с разными уровнями предупреждений
        Map<String, Double> budgets = user.getBudgets();
        for (Map.Entry<String, Double> entry : budgets.entrySet()) {
            String category = entry.getKey();
            double limit = entry.getValue();
            double spent = user.getExpensesByCategory(category);
            double percentage = (spent / limit) * 100;
            
            if (spent > limit) {
                alerts.add("🚨 ПРЕВЫШЕНИЕ БЮДЖЕТА '" + category + "': " + 
                          String.format("%.0f%%", percentage) + " (" + 
                          String.format("%.2f", spent) + " из " + String.format("%.2f", limit) + ")");
            } else if (percentage >= 90) {
                alerts.add("🔴 КРИТИЧЕСКИЙ УРОВЕНЬ '" + category + "': " + 
                          String.format("%.0f%%", percentage) + " от лимита");
            } else if (percentage >= 80) {
                alerts.add("🟡 ВЫСОКИЙ УРОВЕНЬ '" + category + "': " + 
                          String.format("%.0f%%", percentage) + " от лимита");
            } else if (percentage >= 50) {
                alerts.add("🔵 СРЕДНИЙ УРОВЕНЬ '" + category + "': " + 
                          String.format("%.0f%%", percentage) + " от лимита");
            }
        }
        
        // Проверка больших расходов
        double totalIncome = user.getTotalIncome();
        if (totalIncome > 0) {
            double expenseRatio = user.getTotalExpenses() / totalIncome;
            if (expenseRatio > 0.9) {
                alerts.add("⚠️ Вы тратите " + String.format("%.0f%%", expenseRatio * 100) + 
                          " от доходов. Рекомендуется экономить.");
            }
        }
        
        return alerts;
    }
    
    public boolean transfer(String toUserLogin, double amount) {
        if (!authService.isLoggedIn() || amount <= 0) {
            return false;
        }
        
        User fromUser = authService.getCurrentUser();
        User toUser = dataStorage.loadUser(toUserLogin);
        
        if (toUser == null || fromUser.getBalance() < amount) {
            return false;
        }
        
        // Списание у отправителя
        fromUser.addExpense(amount, "перевод", "Перевод пользователю " + toUserLogin);
        
        // Зачисление получателю
        toUser.addIncome(amount, "перевод", "Перевод от пользователя " + fromUser.getLogin());
        
        dataStorage.saveUser(toUser);
        return true;
    }
    
    public void showFilteredTransactions(List<String> categories, String typeInput) {
        if (!authService.isLoggedIn()) {
            System.out.println("❌ Необходимо войти в систему.");
            return;
        }
        
        User user = authService.getCurrentUser();
        
        // Простая реализация фильтрации
        System.out.println("\n=== ОТФИЛЬТРОВАННЫЕ ТРАНЗАКЦИИ ===");
        
        user.getTransactions().stream()
            .filter(t -> categories == null || categories.isEmpty() || 
                        categories.contains(t.getCategory().toLowerCase()))
            .filter(t -> {
                if ("1".equals(typeInput)) return t.getType() == TransactionType.INCOME;
                if ("2".equals(typeInput)) return t.getType() == TransactionType.EXPENSE;
                return true; // "0" или любое другое значение - показываем все
            })
            .forEach(t -> System.out.printf("  %s %10.2f %-15s %s%n",
                t.getDate().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                t.getAmount(),
                "(" + t.getCategory() + ")",
                t.getDescription()));
    }
    
    private void checkBudgetAfterExpense(String category) {
        User user = authService.getCurrentUser();
        Double budget = user.getBudgets().get(category.toLowerCase());
        
        if (budget != null) {
            double spent = user.getExpensesByCategory(category);
            if (spent > budget) {
                System.out.println("🚨 ПРЕДУПРЕЖДЕНИЕ: Превышен бюджет по категории '" + category + "'!");
            } else if (spent > budget * 0.8) {
                System.out.println("⚠️ Внимание: Близко к превышению бюджета по категории '" + category + "'");
            }
        }
    }
}