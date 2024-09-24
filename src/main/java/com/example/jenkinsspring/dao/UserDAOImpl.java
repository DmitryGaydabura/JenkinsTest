package com.example.jenkinsspring.dao;

import com.example.jenkinsspring.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация интерфейса UserDAO для взаимодействия с базой данных.
 */
public class UserDAOImpl implements UserDAO {
  private final Connection connection;

  /**
   * Конструктор, принимающий соединение с базой данных.
   *
   * @param connection Соединение с базой данных.
   */
  public UserDAOImpl(Connection connection) {
    this.connection = connection;
  }

  @Override
  public void addUser(User user) throws SQLException {
    String sql = "INSERT INTO users (first_name, last_name, age, team, deleted) VALUES (?, ?, ?, ?, false)";
    try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, user.getFirstName());
      stmt.setString(2, user.getLastName());
      stmt.setInt(3, user.getAge());
      stmt.setString(4, user.getTeam());
      stmt.executeUpdate();

      try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          user.setId(generatedKeys.getLong(1));
        } else {
          throw new SQLException("Добавление пользователя не удалось, не получен ID.");
        }
      }
    }
  }

  @Override
  public void updateUser(User user) throws SQLException {
    String sql = "UPDATE users SET first_name = ?, last_name = ?, age = ?, team = ? WHERE id = ? AND deleted = false";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, user.getFirstName());
      stmt.setString(2, user.getLastName());
      stmt.setInt(3, user.getAge());
      stmt.setString(4, user.getTeam());
      stmt.setLong(5, user.getId());
      int affectedRows = stmt.executeUpdate();
      if (affectedRows == 0) {
        throw new SQLException("Обновление пользователя не удалось, пользователь не найден или удален.");
      }
    }
  }

  @Override
  public void softDeleteUser(Long userId) throws SQLException {
    String sql = "UPDATE users SET deleted = true WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setLong(1, userId);
      int affectedRows = stmt.executeUpdate();
      if (affectedRows == 0) {
        throw new SQLException("Мягкое удаление пользователя не удалось, пользователь не найден.");
      }
    }
  }

  @Override
  public List<User> getAllUsers() throws SQLException {
    String sql = "SELECT id, first_name, last_name, age, team FROM users WHERE deleted = false";
    List<User> users = new ArrayList<>();
    try (PreparedStatement stmt = connection.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        User user = mapRowToUser(rs);
        users.add(user);
      }
    }
    return users;
  }

  @Override
  public User getUserById(Long userId) throws SQLException {
    String sql = "SELECT id, first_name, last_name, age, team FROM users WHERE id = ? AND deleted = false";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setLong(1, userId);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return mapRowToUser(rs);
        }
      }
    }
    return null;
  }

  @Override
  public List<User> getUsersByTeam(String team) throws SQLException {
    String sql = "SELECT id, first_name, last_name, age, team FROM users WHERE team = ? AND deleted = false";
    List<User> users = new ArrayList<>();
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, team);
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          User user = mapRowToUser(rs);
          users.add(user);
        }
      }
    }
    return users;
  }

  /**
   * Преобразует строку результата запроса в объект User.
   *
   * @param rs Результат запроса.
   * @return Объект User.
   * @throws SQLException Если возникает ошибка при доступе к данным.
   */
  private User mapRowToUser(ResultSet rs) throws SQLException {
    User user = new User();
    user.setId(rs.getLong("id"));
    user.setFirstName(rs.getString("first_name"));
    user.setLastName(rs.getString("last_name"));
    user.setAge(rs.getInt("age"));
    user.setTeam(rs.getString("team"));
    return user;
  }
}
