package com.finance.repository;

import com.finance.model.User;
import java.util.Map;

public interface UserRepository {
    void save(User user);
    User findByLogin(String login);
    Map<String, User> findAll();
    void saveAll();
}