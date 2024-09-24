package com.example.jenkinsspring.service;

import com.example.jenkinsspring.model.Activity;
import com.example.jenkinsspring.exception.ActivityNotFoundException;

import java.sql.SQLException;
import java.util.List;

/**
 * Интерфейс для сервисных операций, связанных с активностями.
 */
public interface ActivityService {
  /**
   * Добавляет новую активность.
   *
   * @param activity Объект активности для добавления.
   * @throws SQLException если возникает ошибка при доступе к базе данных.
   */
  void addActivity(Activity activity) throws SQLException;

  /**
   * Обновляет существующую активность.
   *
   * @param activity Объект активности с обновленными данными.
   * @throws SQLException если возникает ошибка при доступе к базе данных.
   */
  void updateActivity(Activity activity) throws SQLException, ActivityNotFoundException;

  /**
   * Удаляет активность по её ID.
   *
   * @param activityId ID активности для удаления.
   * @throws SQLException                если возникает ошибка при доступе к базе данных.
   * @throws ActivityNotFoundException   если активность с указанным ID не найдена.
   */
  void deleteActivity(Long activityId) throws SQLException, ActivityNotFoundException;

  /**
   * Получает список всех активностей.
   *
   * @return Список активностей.
   * @throws SQLException если возникает ошибка при доступе к базе данных.
   */
  List<Activity> getAllActivities() throws SQLException;

  /**
   * Получает активность по её ID.
   *
   * @param activityId ID активности.
   * @return Объект активности.
   * @throws SQLException                если возникает ошибка при доступе к базе данных.
   * @throws ActivityNotFoundException   если активность с указанным ID не найдена.
   */
  Activity getActivityById(Long activityId) throws SQLException, ActivityNotFoundException;
}
