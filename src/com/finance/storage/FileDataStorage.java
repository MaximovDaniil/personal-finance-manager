package com.finance.storage;

import com.finance.domain.*;
import com.finance.model.User;

import java.io.*;
import java.util.*;

public class FileDataStorage implements DataStorage {
    private final String dataDir = "data";
    private final Map<String, User> users;
    
    public FileDataStorage() {
        this.users = new HashMap<>();
        loadAllUsers();
    }
    
    @Override
    public void saveUser(User user) {
        users.put(user.getLogin(), user);
        saveUserToFile(user);
    }
    
    @Override
    public User loadUser(String login) {
        // Сначала проверяем в памяти
        User user = users.get(login);
        if (user != null) {
            return user;
        }
        
        // Если нет в памяти, загружаем из файла
        return loadUserFromFile(login);
    }
    
    @Override
    public Map<String, User> getAllUsers() {
        return new HashMap<>(users);
    }
    
    @Override
    public void saveAllData() {
        for (User user : users.values()) {
            saveUserToFile(user);
        }
    }
    
    private void loadAllUsers() {
        File dir = new File(dataDir);
        if (!dir.exists()) {
            dir.mkdirs();
            return;
        }
        
        File[] files = dir.listFiles((d, name) -> name.endsWith(".dat"));
        if (files != null) {
            for (File file : files) {
                String login = file.getName().replace(".dat", "");
                loadUserFromFile(login);
            }
        }
    }
    
    private User loadUserFromFile(String login) {
        File file = new File(dataDir, login + ".dat");
        if (!file.exists()) {
            return null;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            User user = (User) ois.readObject();
            users.put(login, user);
            return user;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка загрузки пользователя " + login + ": " + e.getMessage());
            return null;
        }
    }
    
    private void saveUserToFile(User user) {
        File dir = new File(dataDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        File file = new File(dir, user.getLogin() + ".dat");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(user);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения пользователя " + user.getLogin() + ": " + e.getMessage());
        }
    }
}