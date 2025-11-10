package com.finance;

import com.finance.service.AuthService;
import com.finance.service.FinanceService;
import com.finance.service.ExportService;
import com.finance.storage.DataStorage;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static AuthService authService;
    private static FinanceService financeService;
    private static ExportService exportService;
    private static DataStorage dataStorage;
    private static Scanner scanner;
    private static boolean running = true;
    
    public static void main(String[] args) {
        initializeServices();
        System.out.println("=== Personal Finance Manager ===");
        System.out.println("Добро пожаловать в систему управления личными финансами!");
        
        while (running) {
            if (!authService.isLoggedIn()) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
        
        scanner.close();
        dataStorage.saveAllData();
        System.out.println("Спасибо за использование Personal Finance Manager!");
    }
    
    private static void initializeServices() {
        dataStorage = new DataStorage();
        authService = new AuthService(dataStorage);
        financeService = new FinanceService(authService, dataStorage);
        exportService = new ExportService();
        scanner = new Scanner(System.in);
    }
    
    private static void showAuthMenu() {
        System.out.println("\n=== Аутентификация ===");
        System.out.println("1. Вход");
        System.out.println("2. Регистрация");
        System.out.println("3. Выход");
        System.out.print("Выберите действие: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                login();
                break;
            case "2":
                register();
                break;
            case "3":
                running = false;
                break;
            default:
                System.out.println("❌ Неверный выбор. Попробуйте снова.");
        }
    }
    
    private static void showMainMenu() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("=== ГЛАВНОЕ МЕНЮ ===");
        System.out.println("Текущий пользователь: " + authService.getCurrentUser().getLogin());
        System.out.println("=".repeat(40));
        System.out.println("1.  Добавить доход");
        System.out.println("2.  Добавить расход");
        System.out.println("3.  Установить бюджет");
        System.out.println("4.  Редактировать бюджет");
        System.out.println("5.  Показать статистику");
        System.out.println("6.  Показать баланс");
        System.out.println("7.  Фильтр транзакций");
        System.out.println("8.  Перевод другому пользователю");
        System.out.println("9.  Проверить оповещения");
        System.out.println("10. Экспорт данных (CSV/JSON)");
        System.out.println("11. Импорт данных (CSV/JSON)"); // Обновили текст
        System.out.println("12. Справка (help)");
        System.out.println("13. Выход");
        System.out.println("=".repeat(40));
        System.out.print("Выберите действие: ");

        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                addIncome();
                break;
            case "2":
                addExpense();
                break;
            case "3":
                setBudget();
                break;
            case "4":
                editBudget();
                break;
            case "5":
                financeService.showStatistics();
                break;
            case "6":
                showBalance();
                break;
            case "7":
                filterTransactions();
                break;
            case "8":
                transferMoney();
                break;
            case "9":
                checkAlerts();
                break;
            case "10":
                exportData();
                break;
            case "11":
                importData();
                break;
            case "12":
                showHelp();
                break;
            case "13":
                logout();
                break;
            default:
                System.out.println("❌ Неверный выбор. Попробуйте снова.");
        }
    }
    
    private static void login() {
        System.out.print("Логин: ");
        String login = scanner.nextLine().trim();
        System.out.print("Пароль: ");
        String password = scanner.nextLine().trim();
        
        if (authService.login(login, password)) {
            System.out.println("✅ Успешный вход! Добро пожаловать, " + login + "!");
        } else {
            System.out.println("❌ Неверный логин или пароль.");
        }
    }
    
    private static void register() {
        System.out.print("Придумайте логин: ");
        String login = scanner.nextLine().trim();
        System.out.print("Придумайте пароль: ");
        String password = scanner.nextLine().trim();
        
        if (authService.register(login, password)) {
            System.out.println("✅ Регистрация успешна! Добро пожаловать, " + login + "!");
        } else {
            System.out.println("❌ Пользователь с таким логином уже существует.");
        }
    }
    
    private static void addIncome() {
        try {
            System.out.print("Сумма дохода: ");
            double amount = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Категория: ");
            String category = scanner.nextLine().trim();
            System.out.print("Описание: ");
            String description = scanner.nextLine().trim();
            
            if (financeService.addIncome(amount, category, description)) {
                System.out.println("✅ Доход успешно добавлен!");
            } else {
                System.out.println("❌ Ошибка при добавлении дохода.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Неверный формат суммы.");
        }
    }
    
    private static void addExpense() {
        try {
            System.out.print("Сумма расхода: ");
            double amount = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Категория: ");
            String category = scanner.nextLine().trim();
            System.out.print("Описание: ");
            String description = scanner.nextLine().trim();
            
            if (financeService.addExpense(amount, category, description)) {
                System.out.println("✅ Расход успешно добавлен!");
            } else {
                System.out.println("❌ Ошибка при добавлении расхода.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Неверный формат суммы.");
        }
    }
    
    private static void setBudget() {
        try {
            System.out.print("Категория: ");
            String category = scanner.nextLine().trim();
            System.out.print("Лимит бюджета: ");
            double limit = Double.parseDouble(scanner.nextLine().trim());
            
            if (financeService.setBudget(category, limit)) {
                System.out.println("✅ Бюджет успешно установлен!");
            } else {
                System.out.println("❌ Ошибка при установке бюджета.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Неверный формат суммы.");
        }
    }
    
    private static void editBudget() {
        try {
            System.out.print("Категория для редактирования: ");
            String category = scanner.nextLine().trim();
            System.out.print("Новый лимит бюджета: ");
            double newLimit = Double.parseDouble(scanner.nextLine().trim());
            
            if (financeService.updateBudget(category, newLimit)) {
                System.out.println("✅ Бюджет успешно обновлен!");
            } else {
                System.out.println("❌ Ошибка при обновлении бюджета. Проверьте название категории.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Неверный формат суммы.");
        }
    }
    
    private static void showBalance() {
        if (!authService.isLoggedIn()) {
            System.out.println("❌ Необходимо войти в систему.");
            return;
        }
        
        double balance = authService.getCurrentUser().getBalance();
        System.out.printf("💰 Ваш текущий баланс: %,10.2f₽%n", balance);
        
        if (balance < 0) {
            System.out.println("🚨 ВНИМАНИЕ: Ваш баланс отрицательный!");
        } else if (balance == 0) {
            System.out.println("⚠️ Внимание: Ваш баланс равен нулю.");
        }
    }
    
    private static void filterTransactions() {
        if (!authService.isLoggedIn()) {
            System.out.println("❌ Необходимо войти в систему.");
            return;
        }
        
        System.out.println("\n=== ФИЛЬТР ТРАНЗАКЦИЙ ===");
        System.out.println("Введите категории через запятую (или оставьте пустым для всех):");
        String categoriesInput = scanner.nextLine().trim();
        
        System.out.println("Тип транзакций (1-доходы, 2-расходы, 0-все):");
        String typeInput = scanner.nextLine().trim();
        
        List<String> categories = null;
        if (!categoriesInput.isEmpty()) {
            categories = List.of(categoriesInput.split(","));
        }
        
        financeService.showFilteredTransactions(categories, typeInput);
    }
    
    private static void transferMoney() {
        try {
            System.out.print("Логин получателя: ");
            String toUser = scanner.nextLine().trim();
            System.out.print("Сумма перевода: ");
            double amount = Double.parseDouble(scanner.nextLine().trim());
            
            if (financeService.transfer(toUser, amount)) {
                System.out.println("✅ Перевод успешно выполнен!");
            } else {
                System.out.println("❌ Ошибка при переводе. Проверьте логин получателя и баланс.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Неверный формат суммы.");
        }
    }
    
    private static void checkAlerts() {
        List<String> alerts = financeService.checkAlerts();
        if (alerts.isEmpty()) {
            System.out.println("✅ Все в порядке! Нет активных оповещений.");
        } else {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("=== АКТИВНЫЕ ОПОВЕЩЕНИЯ ===");
            System.out.println("=".repeat(50));
            alerts.forEach(alert -> {
                if (alert.contains("🚨") || alert.contains("ПРЕВЫШЕНИЕ")) {
                    System.out.println("🔴 " + alert);
                } else if (alert.contains("🟡") || alert.contains("ВЫСОКИЙ")) {
                    System.out.println("🟡 " + alert);
                } else if (alert.contains("🔵") || alert.contains("СРЕДНИЙ")) {
                    System.out.println("🔵 " + alert);
                } else {
                    System.out.println("ℹ️  " + alert);
                }
            });
            System.out.println("=".repeat(50));
        }
    }
    
    private static void exportData() {
        if (!authService.isLoggedIn()) {
            System.out.println("❌ Необходимо войти в систему.");
            return;
        }
        
        System.out.println("\n=== ЭКСПОРТ ДАННЫХ ===");
        System.out.println("1. Экспорт в CSV");
        System.out.println("2. Экспорт в JSON");
        System.out.print("Выберите формат: ");
        
        String choice = scanner.nextLine().trim();
        System.out.print("Введите имя файла: ");
        String filename = scanner.nextLine().trim();
        
        boolean success = false;
        if ("1".equals(choice)) {
            success = exportService.exportToCsv(authService.getCurrentUser(), filename);
        } else if ("2".equals(choice)) {
            success = exportService.exportToJson(authService.getCurrentUser(), filename);
        } else {
            System.out.println("❌ Неверный выбор формата.");
            return;
        }
        
        if (success) {
            System.out.println("✅ Данные успешно экспортированы в файл: " + filename);
        } else {
            System.out.println("❌ Ошибка при экспорте данных.");
        }
    }
    
    private static void importData() {
        if (!authService.isLoggedIn()) {
            System.out.println("❌ Необходимо войти в систему.");
            return;
        }
        
        System.out.println("\n=== ИМПОРТ ДАННЫХ ===");
        System.out.println("1. Импорт из CSV");
        System.out.println("2. Импорт из JSON");
        System.out.print("Выберите формат: ");
        
        String choice = scanner.nextLine().trim();
        System.out.print("Введите имя файла: ");
        String filename = scanner.nextLine().trim();
        
        boolean success = false;
        if ("1".equals(choice)) {
            success = exportService.importFromCsv(authService.getCurrentUser(), filename);
        } else if ("2".equals(choice)) {
            success = exportService.importFromJson(authService.getCurrentUser(), filename);
        } else {
            System.out.println("❌ Неверный выбор формата.");
            return;
        }
        
        if (success) {
            System.out.println("✅ Данные успешно импортированы из файла: " + filename);
        } else {
            System.out.println("❌ Ошибка при импорте данных.");
        }
    }
    
    private static void showHelp() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("=== СПРАВКА ПО КОМАНДАМ ===");
        System.out.println("=".repeat(60));
        
        System.out.println("\n📋 ОСНОВНЫЕ КОМАНДЫ:");
        showFormattedTable(
            List.of("№", "Команда", "Описание"),
            List.of(
                List.of("1", "Добавить доход", "Добавление дохода с указанием категории"),
                List.of("2", "Добавить расход", "Добавление расхода с указанием категории"),
                List.of("3", "Установить бюджет", "Установка лимита для категории расходов"),
                List.of("4", "Редактировать бюджет", "Изменение существующего бюджета"),
                List.of("5", "Статистика", "Подробный финансовый отчет"),
                List.of("6", "Баланс", "Текущее состояние счета"),
                List.of("7", "Фильтр", "Фильтрация транзакций по категориям")
            )
        );
        
        System.out.println("\n🎯 ДОПОЛНИТЕЛЬНЫЕ КОМАНДЫ:");
        showFormattedTable(
            List.of("№", "Команда", "Описание"),
            List.of(
                List.of("8", "Перевод", "Перевод денег другому пользователю"),
                List.of("9", "Оповещения", "Проверка финансовых предупреждений"),
                List.of("10", "Экспорт", "Экспорт данных в CSV/JSON"),
                List.of("11", "Импорт", "Импорт данных из CSV/JSON"),
                List.of("12", "Справка", "Показать эту справку"),
                List.of("13", "Выход", "Выйти из системы")
            )
        );
        
        System.out.println("\n💡 ПРИМЕРЫ ИСПОЛЬЗОВАНИЯ:");
        System.out.println("  • Добавление дохода: 50000 зарплата \"Зарплата за октябрь\"");
        System.out.println("  • Установка бюджета: еда 15000");
        System.out.println("  • Редактирование бюджета: еда 20000");
        System.out.println("  • Перевод средств: user2 5000");
        System.out.println("  • Экспорт данных: finances.csv");
        
        System.out.println("\n🔔 СИСТЕМА ОПОВЕЩЕНИЙ:");
        System.out.println("  🚨 КРАСНЫЙ - Превышение бюджета (>100%)");
        System.out.println("  🟡 ЖЕЛТЫЙ - Высокий уровень (>80%)");
        System.out.println("  🔵 СИНИЙ - Средний уровень (>50%)");
        System.out.println("  ⚠️  ВНИМАНИЕ - Отрицательный/нулевой баланс");
        
        System.out.println("=".repeat(60));
    }
    
    private static void logout() {
        authService.logout();
        System.out.println("Вы вышли из системы.");
    }

    private static void showFormattedTable(List<String> headers, List<List<String>> rows) {
        if (rows.isEmpty()) return;
        
        int columns = headers.size();
        int[] maxLengths = new int[columns];
        
        for (int i = 0; i < columns; i++) {
            maxLengths[i] = headers.get(i).length();
            for (List<String> row : rows) {
                if (i < row.size()) {
                    maxLengths[i] = Math.max(maxLengths[i], row.get(i).length());
                }
            }
        }
        
        StringBuilder topBorder = new StringBuilder("┌");
        for (int i = 0; i < columns; i++) {
            topBorder.append("─".repeat(maxLengths[i] + 2));
            topBorder.append(i < columns - 1 ? "┬" : "┐");
        }
        System.out.println(topBorder);
        
        StringBuilder headerLine = new StringBuilder("│");
        for (int i = 0; i < columns; i++) {
            headerLine.append(" ").append(String.format("%-" + maxLengths[i] + "s", headers.get(i))).append(" │");
        }
        System.out.println(headerLine);
        
        StringBuilder separator = new StringBuilder("├");
        for (int i = 0; i < columns; i++) {
            separator.append("─".repeat(maxLengths[i] + 2));
            separator.append(i < columns - 1 ? "┼" : "┤");
        }
        System.out.println(separator);
        
        for (List<String> row : rows) {
            StringBuilder rowLine = new StringBuilder("│");
            for (int i = 0; i < columns; i++) {
                String cell = i < row.size() ? row.get(i) : "";
                rowLine.append(" ").append(String.format("%-" + maxLengths[i] + "s", cell)).append(" │");
            }
            System.out.println(rowLine);
        }
        
        StringBuilder bottomBorder = new StringBuilder("└");
        for (int i = 0; i < columns; i++) {
            bottomBorder.append("─".repeat(maxLengths[i] + 2));
            bottomBorder.append(i < columns - 1 ? "┴" : "┘");
        }
        System.out.println(bottomBorder);
    }
}