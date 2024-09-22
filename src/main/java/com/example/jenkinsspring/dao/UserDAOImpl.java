package com.example.jenkinsspring.dao;

import com.example.jenkinsspring.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {
  private Connection connection;

  public UserDAOImpl(Connection connection) {
    this.connection = connection;
  }

  public Connection getConnection() {
    return this.connection;
  }

  @Override
  public void addUser(User user) throws SQLException {
    String sql = "INSERT INTO users (first_name, last_name, age, team) VALUES (?, ?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, user.getFirstName());
      stmt.setString(2, user.getLastName());
      stmt.setInt(3, user.getAge());
      stmt.setString(4, user.getTeam()); // Устанавливаем команду
      stmt.executeUpdate();

      // Получаем сгенерированный ID пользователя
      try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          user.setId(generatedKeys.getLong(1));
        }
      }
    }
  }

  @Override
  public void updateUser(User user) throws SQLException {
    String sql = "UPDATE users SET first_name = ?, last_name = ?, age = ?, team = ? WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, user.getFirstName());
      stmt.setString(2, user.getLastName());
      stmt.setInt(3, user.getAge());
      stmt.setString(4, user.getTeam());
      stmt.setLong(5, user.getId());
      stmt.executeUpdate();
    }
  }

  @Override
  public List<User> getUsersByTeam(String team) throws SQLException {
    List<User> userList = new ArrayList<>();
    String sql = "SELECT id, first_name, last_name, age, team FROM users WHERE team = ? ORDER BY id";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, team);
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          User user = new User();
          user.setId(rs.getLong("id"));
          user.setFirstName(rs.getString("first_name"));
          user.setLastName(rs.getString("last_name"));
          user.setAge(rs.getInt("age"));
          user.setTeam(rs.getString("team"));
          userList.add(user);
        }
      }
    }
    return userList;
  }

  @Override
  public void deleteUser(Long id) throws SQLException {
    String sql = "DELETE FROM users WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setLong(1, id);
      stmt.executeUpdate();
    }
  }

  @Override
  public List<User> getAllUsers() throws SQLException {
    List<User> userList = new ArrayList<>();

    String sql = "SELECT id, first_name, last_name, age FROM users ORDER BY id";
    try (PreparedStatement stmt = connection.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setAge(rs.getInt("age"));
        userList.add(user);
      }
    }
    return userList;
  }

  @Override
  public User getUserById(Long id) throws SQLException {
    String sql = "SELECT id, first_name, last_name, age, team FROM users WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setLong(1, id);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          User user = new User();
          user.setId(rs.getLong("id"));
          user.setFirstName(rs.getString("first_name"));
          user.setLastName(rs.getString("last_name"));
          user.setAge(rs.getInt("age"));
          user.setTeam(rs.getString("team"));
          return user;
        }
      }
    }
    return null;
  }

  public void closeConnection() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }
}
