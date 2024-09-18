package com.example.jenkinsspring.service;

import com.example.jenkinsspring.dao.ActivityDAO;
import com.example.jenkinsspring.model.Activity;
import com.example.jenkinsspring.exception.UserNotFoundException;
import java.sql.SQLException;
import java.util.List;

public class ActivityServiceImpl implements ActivityService {
  private ActivityDAO activityDAO;

  public ActivityServiceImpl(ActivityDAO activityDAO) {
    this.activityDAO = activityDAO;
  }

  @Override
  public void addActivity(Activity activity) throws SQLException, UserNotFoundException {
    activityDAO.addActivity(activity);
  }

  @Override
  public void deleteActivity(Long id) throws SQLException {
    activityDAO.deleteActivity(id);
  }

  @Override
  public List<Activity> getAllActivities() throws SQLException {
    return activityDAO.getAllActivities();
  }
}
