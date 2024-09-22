package com.example.jenkinsspring.service;

import com.example.jenkinsspring.dao.ActivityDAO;
import com.example.jenkinsspring.model.Activity;
import com.example.jenkinsspring.exception.UserNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ActivityServiceImpl implements ActivityService {
  private ActivityDAO activityDAO;
  private Connection connection;

  public ActivityServiceImpl(ActivityDAO activityDAO) {
    this.activityDAO = activityDAO;
    if (activityDAO instanceof com.example.jenkinsspring.dao.ActivityDAOImpl) {
      this.connection = ((com.example.jenkinsspring.dao.ActivityDAOImpl) activityDAO).getConnection();
    }
  }

  @Override
  public void addActivity(Activity activity) throws SQLException, UserNotFoundException {
    activityDAO.addActivity(activity);
  }

  @Override
  public void updateActivity(Activity activity) throws SQLException {
    activityDAO.updateActivity(activity);
  }

  @Override
  public void deleteActivity(Long id) throws SQLException {
    activityDAO.deleteActivity(id);
  }

  @Override
  public List<Activity> getAllActivities() throws SQLException {
    return activityDAO.getAllActivities();
  }

  @Override
  public void commitConnection() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.commit();
    }
  }

  @Override
  public void closeConnection() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }
}
