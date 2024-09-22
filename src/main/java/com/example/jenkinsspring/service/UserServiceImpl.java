package com.example.jenkinsspring.service;

import com.example.jenkinsspring.dao.UserDAO;
import com.example.jenkinsspring.model.User;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements UserService {
  private UserDAO userDAO;
  private Connection connection;

  public UserServiceImpl(UserDAO userDAO) {
    this.userDAO = userDAO;
    if (userDAO instanceof com.example.jenkinsspring.dao.UserDAOImpl) {
      this.connection = ((com.example.jenkinsspring.dao.UserDAOImpl) userDAO).getConnection();
    }
  }

  @Override
  public void addUser(User user) throws SQLException {
    try {
      userDAO.addUser(user);
      connection.commit();
    } catch (SQLException e) {
      connection.rollback();
      throw e;
    }
  }

  @Override
  public void updateUser(User user) throws SQLException {
    try {
      userDAO.updateUser(user);
      connection.commit();
    } catch (SQLException e) {
      connection.rollback();
      throw e;
    }
  }

  @Override
  public void deleteUser(Long id) throws SQLException {
    try {
      userDAO.deleteUser(id);
      connection.commit();
    } catch (SQLException e) {
      connection.rollback();
      throw e;
    }
  }

  @Override
  public List<User> getAllUsers() throws SQLException {
    try {
      List<User> users = userDAO.getAllUsers();
      connection.commit();
      return users;
    } catch (SQLException e) {
      connection.rollback();
      throw e;
    }
  }

  @Override
  public User getUserById(Long id) throws SQLException {
    try {
      User user = userDAO.getUserById(id);
      connection.commit();
      return user;
    } catch (SQLException e) {
      connection.rollback();
      throw e;
    }
  }

  @Override
  public List<User> getUsersByTeam(String team) throws SQLException {
    try {
      List<User> users = userDAO.getUsersByTeam(team);
      connection.commit();
      return users;
    } catch (SQLException e) {
      connection.rollback();
      throw e;
    }
  }

  // Метод для коммита транзакций (если необходим)
  public void commitConnection() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.commit();
    }
  }

  // Метод для закрытия соединения
  public void closeConnection() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }
}
