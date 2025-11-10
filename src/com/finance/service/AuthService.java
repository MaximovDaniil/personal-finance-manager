package com.finance.service;

import com.finance.model.User;
import com.finance.storage.DataStorage;

public class AuthService {
    private final DataStorage dataStorage;
    private User currentUser;
    
    public AuthService(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }
    
    public boolean login(String login, String password) {
        User user = dataStorage.loadUser(login);
        if (user != null && user.checkPassword(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }
    
    public boolean register(String login, String password) {
        if (dataStorage.loadUser(login) != null) {
            return false;
        }
        
        User newUser = new User(login, password);
        dataStorage.saveUser(newUser);
        currentUser = newUser;
        return true;
    }
    
    public void logout() {
        if (currentUser != null) {
            dataStorage.saveUser(currentUser);
        }
        currentUser = null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}