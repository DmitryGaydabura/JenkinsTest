package com.example.jenkinsspring.service;

import com.example.jenkinsspring.dao.UserDAO;
import com.example.jenkinsspring.model.User;
import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements UserService {
  private UserDAO userDAO;

  public UserServiceImpl(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  @Override
  public void addUser(User user) throws SQLException {
    userDAO.addUser(user);
  }

  @Override
  public void updateUser(User user) throws SQLException {
    userDAO.updateUser(user);
  }

  @Override
  public void deleteUser(Long id) throws SQLException {
    userDAO.deleteUser(id);
  }

  @Override
  public List<User> getAllUsers() throws SQLException {
    return userDAO.getAllUsers();
  }
  @Override
  public User getUserById(Long id) throws SQLException {
    return userDAO.getUserById(id);
  }

  @Override
  public List<User> getUsersByTeam(String team) throws SQLException {
    return userDAO.getUsersByTeam(team);
  }
}
