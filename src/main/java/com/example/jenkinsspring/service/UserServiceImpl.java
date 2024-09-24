package com.example.jenkinsspring.service;

import com.example.jenkinsspring.dao.UserDAO;
import com.example.jenkinsspring.dao.UserDAOImpl;
import com.example.jenkinsspring.model.User;
import com.example.jenkinsspring.exception.UserNotFoundException;
import com.example.jenkinsspring.util.DataSourceManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Реализация интерфейса UserService для управления пользователями.
 */
public class UserServiceImpl implements UserService {
  private UserDAO userDAO;

  /**
   * Конструктор, инициализирующий UserDAO.
   *
   * @throws SQLException если возникает ошибка при подключении к базе данных.
   */
  public UserServiceImpl() throws SQLException {
    try (Connection connection = DataSourceManager.getDataSource().getConnection()) {
      this.userDAO = new UserDAOImpl(connection);
    }
  }

  @Override
  public void addUser(User user) throws SQLException {
    try (Connection connection = DataSourceManager.getDataSource().getConnection()) {
      connection.setAutoCommit(false);
      userDAO = new UserDAOImpl(connection);
      userDAO.addUser(user);
      connection.commit();
    } catch (SQLException e) {
      throw e;
    }
  }

  @Override
  public void updateUser(User user) throws SQLException {
    try (Connection connection = DataSourceManager.getDataSource().getConnection()) {
      connection.setAutoCommit(false);
      userDAO = new UserDAOImpl(connection);
      userDAO.updateUser(user);
      connection.commit();
    } catch (SQLException e) {
      throw e;
    }
  }

  @Override
  public void softDeleteUser(Long userId) throws SQLException, UserNotFoundException {
    try (Connection connection = DataSourceManager.getDataSource().getConnection()) {
      connection.setAutoCommit(false);
      userDAO = new UserDAOImpl(connection);
      User user = userDAO.getUserById(userId);
      if (user == null) {
        throw new UserNotFoundException("Пользователь с ID " + userId + " не найден.");
      }
      userDAO.softDeleteUser(userId);
      connection.commit();
    } catch (SQLException | UserNotFoundException e) {
      throw e;
    }
  }

  @Override
  public List<User> getAllUsers() throws SQLException {
    try (Connection connection = DataSourceManager.getDataSource().getConnection()) {
      userDAO = new UserDAOImpl(connection);
      return userDAO.getAllUsers();
    } catch (SQLException e) {
      throw e;
    }
  }

  @Override
  public User getUserById(Long userId) throws SQLException, UserNotFoundException {
    try (Connection connection = DataSourceManager.getDataSource().getConnection()) {
      userDAO = new UserDAOImpl(connection);
      User user = userDAO.getUserById(userId);
      if (user == null) {
        throw new UserNotFoundException("Пользователь с ID " + userId + " не найден.");
      }
      return user;
    } catch (SQLException e) {
      throw e;
    }
  }

  @Override
  public List<User> getUsersByTeam(String team) throws SQLException {
    try (Connection connection = DataSourceManager.getDataSource().getConnection()) {
      userDAO = new UserDAOImpl(connection);
      return userDAO.getUsersByTeam(team);
    } catch (SQLException e) {
      throw e;
    }
  }
}
