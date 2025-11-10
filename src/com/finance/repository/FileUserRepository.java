package com.finance.repository;

import com.finance.model.User;
import com.finance.storage.DataStorage;
import java.util.Map;

public class FileUserRepository implements UserRepository {
    private final DataStorage dataStorage;
    
    public FileUserRepository(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }
    
    @Override
    public void save(User user) {
        dataStorage.saveUser(user);
    }
    
    @Override
    public User findByLogin(String login) {
        return dataStorage.loadUser(login);
    }
    
    @Override
    public Map<String, User> findAll() {
        return dataStorage.getAllUsers();
    }
    
    @Override
    public void saveAll() {
        dataStorage.saveAllData();
    }
}