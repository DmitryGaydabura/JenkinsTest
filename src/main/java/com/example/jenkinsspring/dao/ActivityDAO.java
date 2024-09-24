package com.example.jenkinsspring.dao;

import com.example.jenkinsspring.model.Activity;

import java.sql.SQLException;
import java.util.List;

/**
 * Интерфейс для операций с активностями в базе данных.
 */
public interface ActivityDAO {
  /**
   * Добавляет новую активность в базу данных.
   *
   * @param activity Активность для добавления.
   * @throws SQLException Если возникает ошибка при доступе к базе данных.
   */
  void addActivity(Activity activity) throws SQLException;

  /**
   * Обновляет существующую активность в базе данных.
   *
   * @param activity Активность с обновленными данными.
   * @throws SQLException Если возникает ошибка при доступе к базе данных.
   */
  void updateActivity(Activity activity) throws SQLException;

  /**
   * Удаляет активность по её ID.
   *
   * @param activityId ID активности для удаления.
   * @throws SQLException Если возникает ошибка при доступе к базе данных.
   */
  void deleteActivity(Long activityId) throws SQLException;

  /**
   * Получает список всех активностей.
   *
   * @return Список активностей.
   * @throws SQLException Если возникает ошибка при доступе к базе данных.
   */
  List<Activity> getAllActivities() throws SQLException;

  /**
   * Получает активность по её ID.
   *
   * @param activityId ID активности.
   * @return Объект активности или null, если не найден.
   * @throws SQLException Если возникает ошибка при доступе к базе данных.
   */
  Activity getActivityById(Long activityId) throws SQLException;
}
