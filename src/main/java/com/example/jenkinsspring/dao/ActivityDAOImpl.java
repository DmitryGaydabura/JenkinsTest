package com.example.jenkinsspring.dao;

import com.example.jenkinsspring.model.Activity;
import com.example.jenkinsspring.exception.UserNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityDAOImpl implements ActivityDAO {
  private Connection connection;

  public ActivityDAOImpl(Connection connection) {
    this.connection = connection;
  }

  @Override
  public void addActivity(Activity activity) throws SQLException {
    // Проверка существования пользователя
    String checkUserSql = "SELECT first_name, last_name FROM users WHERE id = ?";
    try (PreparedStatement checkUserStmt = connection.prepareStatement(checkUserSql)) {
      checkUserStmt.setLong(1, activity.getUserId());
      try (ResultSet rs = checkUserStmt.executeQuery()) {
        if (rs.next()) {
          activity.setFirstName(rs.getString("first_name"));
          activity.setLastName(rs.getString("last_name"));
        } else {
          throw new UserNotFoundException("Пользователь с ID " + activity.getUserId() + " не найден.");
        }
      } catch (UserNotFoundException e) {
        throw new RuntimeException(e);
      }
    }

    // Вставка активности
    String sql = "INSERT INTO activities (user_id, description) VALUES (?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setLong(1, activity.getUserId());
      stmt.setString(2, activity.getDescription());
      stmt.executeUpdate();

      // Получаем сгенерированный ID и дату активности
      try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          activity.setId(generatedKeys.getLong(1));
        }
      }
    }
  }

  @Override
  public void deleteActivity(Long id) throws SQLException {
    String sql = "DELETE FROM activities WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setLong(1, id);
      stmt.executeUpdate();
    }
  }

  @Override
  public List<Activity> getAllActivities() throws SQLException {
    List<Activity> activities = new ArrayList<>();

    String sql = "SELECT a.id, a.user_id, u.first_name, u.last_name, a.description, a.activity_date " +
        "FROM activities a " +
        "JOIN users u ON a.user_id = u.id " +
        "ORDER BY a.activity_date DESC";

    try (PreparedStatement stmt = connection.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        Activity activity = new Activity();
        activity.setId(rs.getLong("id"));
        activity.setUserId(rs.getLong("user_id"));
        activity.setFirstName(rs.getString("first_name"));
        activity.setLastName(rs.getString("last_name"));
        activity.setDescription(rs.getString("description"));
        activity.setActivityDate(rs.getTimestamp("activity_date"));
        activities.add(activity);
      }
    }
    return activities;
  }
}
