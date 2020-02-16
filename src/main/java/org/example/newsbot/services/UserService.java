package org.example.newsbot.services;

import org.example.newsbot.dao.UserDao;
import org.example.newsbot.models.User;

import java.util.List;

public class UserService {
    private final UserDao userDao = new UserDao();

    public UserService() {
    }

    public User getUser(int id) {
        return userDao.findById(id);
    }

    public void saveUser(User user) {
        userDao.save(user);
    }

    public void deleteUser(User user) {
        userDao.delete(user);
    }

    public void updateUser(User user) {
        userDao.update(user);
    }

    public List<User> findAllUsers() {
        return userDao.findAll();
    }
}
