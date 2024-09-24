package com.example.jenkinsspring.service;

import com.example.jenkinsspring.dao.ActivityDAO;
import com.example.jenkinsspring.dao.ActivityDAOImpl;
import com.example.jenkinsspring.exception.ActivityNotFoundException;
import com.example.jenkinsspring.model.Activity;
import com.example.jenkinsspring.util.DataSourceManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Реализация интерфейса ActivityService для управления активностями.
 */
public class ActivityServiceImpl implements ActivityService {
  private ActivityDAO activityDAO;

  /**
   * Конструктор, инициализирующий ActivityDAO.
   *
   * @throws SQLException если возникает ошибка при подключении к базе данных.
   */
  public ActivityServiceImpl() throws SQLException {
    try (Connection connection = DataSourceManager.getDataSource().getConnection()) {
      this.activityDAO = new ActivityDAOImpl(connection);
    }
  }

  @Override
  public void addActivity(Activity activity) throws SQLException {
    try (Connection connection = DataSourceManager.getDataSource().getConnection()) {
      connection.setAutoCommit(false);
      activityDAO = new ActivityDAOImpl(connection);
      activityDAO.addActivity(activity);
      connection.commit();
    } catch (SQLException e) {
      throw e;
    }
  }

  @Override
  public void updateActivity(Activity activity) throws SQLException, ActivityNotFoundException {
    try (Connection connection = DataSourceManager.getDataSource().getConnection()) {
      connection.setAutoCommit(false);
      activityDAO = new ActivityDAOImpl(connection);
      Activity existingActivity = activityDAO.getActivityById(activity.getId());
      if (existingActivity == null) {
        throw new ActivityNotFoundException("Активность с ID " + activity.getId() + " не найдена.");
      }
      activityDAO.updateActivity(activity);
      connection.commit();
    } catch (SQLException | ActivityNotFoundException e) {
      throw e;
    }
  }

  @Override
  public void deleteActivity(Long activityId) throws SQLException, ActivityNotFoundException {
    try (Connection connection = DataSourceManager.getDataSource().getConnection()) {
      connection.setAutoCommit(false);
      activityDAO = new ActivityDAOImpl(connection);
      Activity activity = activityDAO.getActivityById(activityId);
      if (activity == null) {
        throw new ActivityNotFoundException("Активность с ID " + activityId + " не найдена.");
      }
      activityDAO.deleteActivity(activityId);
      connection.commit();
    } catch (SQLException | ActivityNotFoundException e) {
      throw e;
    }
  }

  @Override
  public List<Activity> getAllActivities() throws SQLException {
    try (Connection connection = DataSourceManager.getDataSource().getConnection()) {
      activityDAO = new ActivityDAOImpl(connection);
      return activityDAO.getAllActivities();
    } catch (SQLException e) {
      throw e;
    }
  }

  @Override
  public Activity getActivityById(Long activityId) throws SQLException, ActivityNotFoundException {
    try (Connection connection = DataSourceManager.getDataSource().getConnection()) {
      activityDAO = new ActivityDAOImpl(connection);
      Activity activity = activityDAO.getActivityById(activityId);
      if (activity == null) {
        throw new ActivityNotFoundException("Активность с ID " + activityId + " не найдена.");
      }
      return activity;
    } catch (SQLException e) {
      throw e;
    }
  }
}
