package com.finance.service;

import com.finance.model.Transaction;
import com.finance.model.TransactionType;
import com.finance.model.User;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExportService {
    
    public boolean exportToCsv(User user, String filePath) {

        if (!filePath.toLowerCase().endsWith(".csv")) {
            filePath += ".csv";
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {

            writer.println("Date,Type,Category,Amount,Description");
            
            for (Transaction transaction : user.getTransactions()) {
                writer.printf("%s,%s,%s,%.2f,%s%n",
                        transaction.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        transaction.getType(),
                        transaction.getCategory(),
                        transaction.getAmount(),
                        transaction.getDescription().replace(",", ";"));
            }
            System.out.println("✅ Файл создан: " + new File(filePath).getAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("❌ Ошибка экспорта в CSV: " + e.getMessage());
            return false;
        }
    }
    
    public boolean exportToJson(User user, String filePath) {

        if (!filePath.toLowerCase().endsWith(".json")) {
            filePath += ".json";
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("[");
            
            List<Transaction> transactions = user.getTransactions();
            for (int i = 0; i < transactions.size(); i++) {
                Transaction transaction = transactions.get(i);
                writer.println("  {");
                writer.printf("    \"id\": \"%s\",%n", transaction.getId());
                writer.printf("    \"type\": \"%s\",%n", transaction.getType());
                writer.printf("    \"amount\": %.2f,%n", transaction.getAmount());
                writer.printf("    \"category\": \"%s\",%n", transaction.getCategory());
                writer.printf("    \"description\": \"%s\",%n", 
                    transaction.getDescription().replace("\"", "\\\""));
                writer.printf("    \"date\": \"%s\"%n", 
                    transaction.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                
                if (i < transactions.size() - 1) {
                    writer.println("  },");
                } else {
                    writer.println("  }");
                }
            }
            
            writer.println("]");
            System.out.println("✅ Файл создан: " + new File(filePath).getAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("❌ Ошибка экспорта в JSON: " + e.getMessage());
            return false;
        }
    }
    
    public boolean importFromCsv(User user, String filePath) {

        if (!filePath.toLowerCase().endsWith(".csv")) {
            filePath += ".csv";
        }
        
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("❌ Файл не найден: " + file.getAbsolutePath());
            return false;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Пропускаем заголовок
            
            if (line == null || !line.contains("Date,Type,Category,Amount,Description")) {
                System.err.println("❌ Неверный формат CSV файла");
                return false;
            }
            
            int importedCount = 0;
            int errorCount = 0;
            
            while ((line = reader.readLine()) != null) {
                try {

                    String[] parts = parseCsvLine(line);
                    if (parts.length >= 5) {
                        TransactionType type = TransactionType.valueOf(parts[1].toUpperCase());
                        double amount = Double.parseDouble(parts[3]);
                        String category = parts[2];
                        String description = parts[4];
                        
                        if (type == TransactionType.INCOME) {
                            user.addIncome(amount, category, description);
                        } else {
                            user.addExpense(amount, category, description);
                        }
                        importedCount++;
                    }
                } catch (Exception e) {
                    errorCount++;
                    System.err.println("❌ Ошибка в строке: " + line);
                }
            }
            
            System.out.printf("✅ Импортировано транзакций: %d%n", importedCount);
            if (errorCount > 0) {
                System.out.printf("⚠️  Ошибок при импорте: %d%n", errorCount);
            }
            return importedCount > 0;
            
        } catch (IOException e) {
            System.err.println("❌ Ошибка импорта из CSV: " + e.getMessage());
            return false;
        }
    }
    
    public boolean importFromJson(User user, String filePath) {

        if (!filePath.toLowerCase().endsWith(".json")) {
            filePath += ".json";
        }
        
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("❌ Файл не найден: " + file.getAbsolutePath());
            return false;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            
            String json = jsonContent.toString().trim();
            List<Transaction> transactions = parseJsonTransactions(json);
            
            int importedCount = 0;
            int errorCount = 0;
            
            for (Transaction transaction : transactions) {
                try {
                    if (transaction.getType() == TransactionType.INCOME) {
                        user.addIncome(transaction.getAmount(), transaction.getCategory(), transaction.getDescription());
                    } else {
                        user.addExpense(transaction.getAmount(), transaction.getCategory(), transaction.getDescription());
                    }
                    importedCount++;
                } catch (Exception e) {
                    errorCount++;
                    System.err.println("❌ Ошибка при импорте транзакции: " + transaction);
                }
            }
            
            System.out.printf("✅ Импортировано транзакций из JSON: %d%n", importedCount);
            if (errorCount > 0) {
                System.out.printf("⚠️  Ошибок при импорте: %d%n", errorCount);
            }
            return importedCount > 0;
            
        } catch (IOException e) {
            System.err.println("❌ Ошибка импорта из JSON: " + e.getMessage());
            return false;
        }
    }
    
    private List<Transaction> parseJsonTransactions(String json) {
        List<Transaction> transactions = new ArrayList<>();
        
        try {

            String cleanJson = json.replaceAll("\\s+", " ").trim();
            
            if (!cleanJson.startsWith("[") || !cleanJson.endsWith("]")) {
                System.err.println("❌ Неверный формат JSON: ожидается массив");
                return transactions;
            }
            
            String arrayContent = cleanJson.substring(1, cleanJson.length() - 1).trim();
            
            String[] objects = splitJsonObjects(arrayContent);
            
            for (String obj : objects) {
                if (obj.trim().isEmpty()) continue;
                
                try {
                    Transaction transaction = parseJsonObject(obj.trim());
                    if (transaction != null) {
                        transactions.add(transaction);
                    }
                } catch (Exception e) {
                    System.err.println("❌ Ошибка парсинга объекта: " + obj);
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Ошибка парсинга JSON: " + e.getMessage());
        }
        
        return transactions;
    }
    
    private Transaction parseJsonObject(String jsonObject) {
        try {
            String typeStr = extractJsonValue(jsonObject, "type");
            String amountStr = extractJsonValue(jsonObject, "amount");
            String category = extractJsonValue(jsonObject, "category");
            String description = extractJsonValue(jsonObject, "description");
            
            if (typeStr == null || amountStr == null || category == null) {
                return null;
            }
            
            TransactionType type = TransactionType.valueOf(typeStr.toUpperCase());
            double amount = Double.parseDouble(amountStr);
            
            return new Transaction(type, amount, category, description);
            
        } catch (Exception e) {
            System.err.println("❌ Ошибка создания транзакции из JSON: " + e.getMessage());
            return null;
        }
    }
    
    private String extractJsonValue(String jsonObject, String key) {
        String searchKey = "\"" + key + "\":";
        int keyIndex = jsonObject.indexOf(searchKey);
        if (keyIndex == -1) return null;
        
        int valueStart = keyIndex + searchKey.length();
        int valueEnd = jsonObject.indexOf(",", valueStart);
        if (valueEnd == -1) {
            valueEnd = jsonObject.indexOf("}", valueStart);
        }
        if (valueEnd == -1) return null;
        
        String value = jsonObject.substring(valueStart, valueEnd).trim();
        
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        
        return value;
    }
    
    private String[] splitJsonObjects(String arrayContent) {
        List<String> objects = new ArrayList<>();
        int depth = 0;
        StringBuilder currentObject = new StringBuilder();
        
        for (char c : arrayContent.toCharArray()) {
            if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth--;
            }
            
            currentObject.append(c);
            
            if (depth == 0 && currentObject.length() > 0) {
                String obj = currentObject.toString().trim();
                if (!obj.isEmpty() && obj.startsWith("{") && obj.endsWith("}")) {
                    objects.add(obj);
                }
                currentObject = new StringBuilder();
                
                int nextChar = arrayContent.indexOf(obj) + obj.length();
                if (nextChar < arrayContent.length() && arrayContent.charAt(nextChar) == ',') {
                    nextChar++;
                }
            }
        }
        
        return objects.toArray(new String[0]);
    }
    
    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        
        return result.toArray(new String[0]);
    }
}