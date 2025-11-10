package com.finance;

import com.finance.model.TransactionType;
import com.finance.model.User;
import com.finance.service.AuthService;
import com.finance.service.FinanceService;
import com.finance.storage.DataStorage;

import java.util.List;

public class SimpleTest {
    public static void main(String[] args) {
        System.out.println("=== ЗАПУСК ВСЕХ ТЕСТОВ ===");
        System.out.println("=" .repeat(50));
        
        int passed = 0;
        int failed = 0;
        
        System.out.println("\n--- ТЕСТЫ USER ---");
        passed += runUserTests();
        
        System.out.println("\n--- ТЕСТЫ AUTH SERVICE ---");
        passed += runAuthServiceTests();
        
        System.out.println("\n--- ТЕСТЫ FINANCE SERVICE ---");
        passed += runFinanceServiceTests();
        
        System.out.println("\n" + "=" .repeat(50));
        System.out.println("=== ФИНАЛЬНЫЕ РЕЗУЛЬТАТЫ ===");
        System.out.println("Всего тестов: " + (passed + failed));
        System.out.println("Пройдено: " + passed);
        System.out.println("Провалено: " + failed);
        
        if (failed == 0) {
            System.out.println("ВСЕ ТЕСТЫ УСПЕШНО ПРОЙДЕНЫ!");
        } else {
            System.out.println("Некоторые тесты провалились");
        }
    }
    
    private static int runUserTests() {
        int passed = 0;
        
        try {
            testUserCreation();
            System.out.println("testUserCreation - PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("testUserCreation - FAILED: " + e.getMessage());
        }
        
        try {
            testAddIncome();
            System.out.println("testAddIncome - PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("testAddIncome - FAILED: " + e.getMessage());
        }
        
        try {
            testAddExpense();
            System.out.println("testAddExpense - PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("testAddExpense - FAILED: " + e.getMessage());
        }
        
        try {
            testSetBudget();
            System.out.println("testSetBudget - PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("testSetBudget - FAILED: " + e.getMessage());
        }
        
        try {
            testUpdateBudget();
            System.out.println("testUpdateBudget - PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("testUpdateBudget - FAILED: " + e.getMessage());
        }
        
        try {
            testRemoveBudget();
            System.out.println("testRemoveBudget - PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("testRemoveBudget - FAILED: " + e.getMessage());
        }
        
        try {
            testExpensesByCategory();
            System.out.println("testExpensesByCategory - PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("testExpensesByCategory - FAILED: " + e.getMessage());
        }
        
        try {
            testIncomeByCategory();
            System.out.println("testIncomeByCategory - PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("testIncomeByCategory - FAILED: " + e.getMessage());
        }
        
        try {
            testPasswordCheck();
            System.out.println("testPasswordCheck - PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("testPasswordCheck - FAILED: " + e.getMessage());
        }
        
        return passed;
    }
    
    private static int runAuthServiceTests() {
        int passed = 0;
        
        try {
            testRegisterAndLogin();
            System.out.println("testRegisterAndLogin - PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("testRegisterAndLogin - FAILED: " + e.getMessage());
        }
        
        try {
            testDuplicateRegistration();
            System.out.println("testDuplicateRegistration - PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("testDuplicateRegistration - FAILED: " + e.getMessage());
        }
        
        try {
            testFailedLogin();
            System.out.println("testFailedLogin - PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("testFailedLogin - FAILED: " + e.getMessage());
        }
        
        try {
            testLogout();
            System.out.println("testLogout - PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("testLogout - FAILED: " + e.getMessage());
        }
        
        return passed;
    }
    
    private static int runFinanceServiceTests() {
        int passed = 0;
        
        try {
            testFinanceAddIncome();
            System.out.println("testFinanceAddIncome - PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("testFinanceAddIncome - FAILED: " + e.getMessage());
        }
        
        try {
            testFinanceAddExpense();
            System.out.println("testFinanceAddExpense - PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("testFinanceAddExpense - FAILED: " + e.getMessage());
        }
        
        try {
            testFinanceSetBudget();
            System.out.println("testFinanceSetBudget - PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("testFinanceSetBudget - FAILED: " + e.getMessage());
        }
        
        try {
            testFinanceUpdateBudget();
            System.out.println("testFinanceUpdateBudget - PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("testFinanceUpdateBudget - FAILED: " + e.getMessage());
        }
        
        try {
            testFinanceTransfer();
            System.out.println("testFinanceTransfer - PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("testFinanceTransfer - FAILED: " + e.getMessage());
        }
        
        try {
            testFinanceCheckAlerts();
            System.out.println("testFinanceCheckAlerts - PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("testFinanceCheckAlerts - FAILED: " + e.getMessage());
        }
        
        return passed;
    }
    
    // User Tests
    private static void testUserCreation() {
        User user = new User("testuser", "password123");
        if (!"testuser".equals(user.getLogin())) throw new RuntimeException("Неверный логин");
        if (user.getBalance() != 0.0) throw new RuntimeException("Баланс не нулевой");
        if (!user.getTransactions().isEmpty()) throw new RuntimeException("Транзакции не пустые");
        if (!user.getBudgets().isEmpty()) throw new RuntimeException("Бюджеты не пустые");
    }
    
    private static void testAddIncome() {
        User user = new User("testuser", "password123");
        user.addIncome(1000.0, "salary", "Monthly salary");
        if (user.getBalance() != 1000.0) throw new RuntimeException("Баланс не обновился");
        if (user.getTotalIncome() != 1000.0) throw new RuntimeException("Доходы не совпадают");
        if (user.getTransactions().size() != 1) throw new RuntimeException("Неверное количество транзакций");
    }
    
    private static void testAddExpense() {
        User user = new User("testuser", "password123");
        user.addIncome(2000.0, "salary", "Monthly salary");
        user.addExpense(500.0, "food", "Groceries");
        if (user.getBalance() != 1500.0) throw new RuntimeException("Баланс не обновился");
        if (user.getTotalExpenses() != 500.0) throw new RuntimeException("Расходы не совпадают");
    }
    
    private static void testSetBudget() {
        User user = new User("testuser", "password123");
        user.setBudget("food", 1000.0);
        if (user.getBudgets().get("food") != 1000.0) throw new RuntimeException("Бюджет не установлен");
    }
    
    private static void testUpdateBudget() {
        User user = new User("testuser", "password123");
        user.setBudget("food", 1000.0);
        if (!user.updateBudget("food", 1500.0)) throw new RuntimeException("Бюджет не обновился");
        if (user.getBudgets().get("food") != 1500.0) throw new RuntimeException("Новый бюджет не установлен");
    }
    
    private static void testRemoveBudget() {
        User user = new User("testuser", "password123");
        user.setBudget("food", 1000.0);
        if (!user.removeBudget("food")) throw new RuntimeException("Бюджет не удалился");
        if (user.getBudgets().containsKey("food")) throw new RuntimeException("Бюджет все еще существует");
    }
    
    private static void testExpensesByCategory() {
        User user = new User("testuser", "password123");
        user.addExpense(100.0, "food", "Lunch");
        user.addExpense(200.0, "food", "Dinner");
        user.addExpense(50.0, "transport", "Bus");
        
        if (user.getExpensesByCategory("food") != 300.0) throw new RuntimeException("Расходы по категории food неверны");
        if (user.getExpensesByCategory("transport") != 50.0) throw new RuntimeException("Расходы по категории transport неверны");
    }
    
    private static void testIncomeByCategory() {
        User user = new User("testuser", "password123");
        user.addIncome(1000.0, "salary", "Work");
        user.addIncome(500.0, "bonus", "Bonus");
        
        var incomeByCategory = user.getIncomeByCategory();
        if (incomeByCategory.get("salary") != 1000.0) throw new RuntimeException("Доходы salary неверны");
        if (incomeByCategory.get("bonus") != 500.0) throw new RuntimeException("Доходы bonus неверны");
    }
    
    private static void testPasswordCheck() {
        User user = new User("testuser", "password123");
        if (!user.checkPassword("password123")) throw new RuntimeException("Правильный пароль не принят");
        if (user.checkPassword("wrongpassword")) throw new RuntimeException("Неправильный пароль принят");
    }
    
    // AuthService Tests
    private static void testRegisterAndLogin() {
        DataStorage dataStorage = new DataStorage();
        AuthService authService = new AuthService(dataStorage);
        
        if (!authService.register("newuser", "password")) throw new RuntimeException("Регистрация не удалась");
        if (!authService.login("newuser", "password")) throw new RuntimeException("Логин не удался");
        if (authService.getCurrentUser() == null) throw new RuntimeException("Пользователь не установлен");
        if (!"newuser".equals(authService.getCurrentUser().getLogin())) throw new RuntimeException("Неверный пользователь");
    }
    
    private static void testDuplicateRegistration() {
        DataStorage dataStorage = new DataStorage();
        AuthService authService = new AuthService(dataStorage);
        
        if (!authService.register("user1", "password")) throw new RuntimeException("Первая регистрация не удалась");
        if (authService.register("user1", "password")) throw new RuntimeException("Дублирующая регистрация прошла успешно");
    }
    
    private static void testFailedLogin() {
        DataStorage dataStorage = new DataStorage();
        AuthService authService = new AuthService(dataStorage);
        
        authService.register("user1", "password");
        
        if (authService.login("user1", "wrongpassword")) throw new RuntimeException("Логин с неправильным паролем прошел");
        if (authService.login("nonexistent", "password")) throw new RuntimeException("Логин несуществующего пользователя прошел");
    }
    
    private static void testLogout() {
        DataStorage dataStorage = new DataStorage();
        AuthService authService = new AuthService(dataStorage);
        
        authService.register("user1", "password");
        authService.login("user1", "password");
        
        if (!authService.isLoggedIn()) throw new RuntimeException("Пользователь не вошел в систему");
        
        authService.logout();
        
        if (authService.isLoggedIn()) throw new RuntimeException("Пользователь не вышел из системы");
        if (authService.getCurrentUser() != null) throw new RuntimeException("Текущий пользователь не сброшен");
    }
    
    // FinanceService Tests
    private static void testFinanceAddIncome() {
        DataStorage dataStorage = new DataStorage();
        AuthService authService = new AuthService(dataStorage);
        FinanceService financeService = new FinanceService(authService, dataStorage);
        
        authService.register("testuser", "password");
        authService.login("testuser", "password");
        
        if (!financeService.addIncome(1000.0, "salary", "Test income")) throw new RuntimeException("Добавление дохода не удалось");
        if (authService.getCurrentUser().getBalance() != 1000.0) throw new RuntimeException("Баланс не обновился");
    }
    
    private static void testFinanceAddExpense() {
        DataStorage dataStorage = new DataStorage();
        AuthService authService = new AuthService(dataStorage);
        FinanceService financeService = new FinanceService(authService, dataStorage);
        
        authService.register("testuser", "password");
        authService.login("testuser", "password");
        financeService.addIncome(2000.0, "salary", "Test income");
        
        if (!financeService.addExpense(500.0, "food", "Test expense")) throw new RuntimeException("Добавление расхода не удалось");
        if (authService.getCurrentUser().getBalance() != 1500.0) throw new RuntimeException("Баланс не обновился");
    }
    
    private static void testFinanceSetBudget() {
        DataStorage dataStorage = new DataStorage();
        AuthService authService = new AuthService(dataStorage);
        FinanceService financeService = new FinanceService(authService, dataStorage);
        
        authService.register("testuser", "password");
        authService.login("testuser", "password");
        
        if (!financeService.setBudget("food", 1000.0)) throw new RuntimeException("Установка бюджета не удалась");
        if (authService.getCurrentUser().getBudgets().get("food") != 1000.0) throw new RuntimeException("Бюджет не установлен");
    }
    
    private static void testFinanceUpdateBudget() {
        DataStorage dataStorage = new DataStorage();
        AuthService authService = new AuthService(dataStorage);
        FinanceService financeService = new FinanceService(authService, dataStorage);
        
        authService.register("testuser", "password");
        authService.login("testuser", "password");
        financeService.setBudget("food", 1000.0);
        
        if (!financeService.updateBudget("food", 1500.0)) throw new RuntimeException("Обновление бюджета не удалось");
        if (authService.getCurrentUser().getBudgets().get("food") != 1500.0) throw new RuntimeException("Бюджет не обновился");
    }
    
    private static void testFinanceTransfer() {
        DataStorage dataStorage = new DataStorage();
        AuthService authService = new AuthService(dataStorage);
        FinanceService financeService = new FinanceService(authService, dataStorage);
        
        authService.register("user2", "password");
        authService.register("testuser", "password");
        authService.login("testuser", "password");
        financeService.addIncome(1000.0, "salary", "Test income");
        
        if (!financeService.transfer("user2", 500.0)) throw new RuntimeException("Перевод не удался");
        if (authService.getCurrentUser().getBalance() != 500.0) throw new RuntimeException("Баланс отправителя неверен");
    }
    
    private static void testFinanceCheckAlerts() {
        DataStorage dataStorage = new DataStorage();
        AuthService authService = new AuthService(dataStorage);
        FinanceService financeService = new FinanceService(authService, dataStorage);
        
        authService.register("testuser", "password");
        authService.login("testuser", "password");
        financeService.setBudget("food", 1000.0);
        financeService.addExpense(900.0, "food", "High expense");
        
        List<String> alerts = financeService.checkAlerts();
        if (alerts.isEmpty()) throw new RuntimeException("Оповещения не сгенерированы");
    }
}