package com.example.jenkinsspring.dao;

import com.example.jenkinsspring.model.Activity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация интерфейса ActivityDAO для взаимодействия с базой данных.
 */
public class ActivityDAOImpl implements ActivityDAO {
  private final Connection connection;

  /**
   * Конструктор, принимающий соединение с базой данных.
   *
   * @param connection Соединение с базой данных.
   */
  public ActivityDAOImpl(Connection connection) {
    this.connection = connection;
  }

  @Override
  public void addActivity(Activity activity) throws SQLException {
    String sql = "INSERT INTO activities (user_id, description, activity_date) VALUES (?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setLong(1, activity.getUserId());
      stmt.setString(2, activity.getDescription());
      stmt.setTimestamp(3, new Timestamp(activity.getActivityDate().getTime()));
      stmt.executeUpdate();

      try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          activity.setId(generatedKeys.getLong(1));
        } else {
          throw new SQLException("Добавление активности не удалось, не получен ID.");
        }
      }
    }
  }

  @Override
  public void updateActivity(Activity activity) throws SQLException {
    String sql = "UPDATE activities SET user_id = ?, description = ?, activity_date = ? WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setLong(1, activity.getUserId());
      stmt.setString(2, activity.getDescription());
      stmt.setTimestamp(3, new Timestamp(activity.getActivityDate().getTime()));
      stmt.setLong(4, activity.getId());
      int affectedRows = stmt.executeUpdate();
      if (affectedRows == 0) {
        throw new SQLException("Обновление активности не удалось, активность не найдена.");
      }
    }
  }

  @Override
  public void deleteActivity(Long activityId) throws SQLException {
    String sql = "DELETE FROM activities WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setLong(1, activityId);
      int affectedRows = stmt.executeUpdate();
      if (affectedRows == 0) {
        throw new SQLException("Удаление активности не удалось, активность не найдена.");
      }
    }
  }

  @Override
  public List<Activity> getAllActivities() throws SQLException {
    String sql = "SELECT id, user_id, description, activity_date FROM activities";
    List<Activity> activities = new ArrayList<>();
    try (PreparedStatement stmt = connection.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        Activity activity = mapRowToActivity(rs);
        activities.add(activity);
      }
    }
    return activities;
  }

  @Override
  public Activity getActivityById(Long activityId) throws SQLException {
    String sql = "SELECT id, user_id, description, activity_date FROM activities WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setLong(1, activityId);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return mapRowToActivity(rs);
        }
      }
    }
    return null;
  }

  /**
   * Преобразует строку результата запроса в объект Activity.
   *
   * @param rs Результат запроса.
   * @return Объект Activity.
   * @throws SQLException Если возникает ошибка при доступе к данным.
   */
  private Activity mapRowToActivity(ResultSet rs) throws SQLException {
    Activity activity = new Activity();
    activity.setId(rs.getLong("id"));
    activity.setUserId(rs.getLong("user_id"));
    activity.setDescription(rs.getString("description"));
    activity.setActivityDate(rs.getTimestamp("activity_date"));
    return activity;
  }
}
