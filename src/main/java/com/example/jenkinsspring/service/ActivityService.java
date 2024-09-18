package com.example.jenkinsspring.service;

import com.example.jenkinsspring.model.Activity;
import com.example.jenkinsspring.exception.UserNotFoundException;
import java.sql.SQLException;
import java.util.List;

public interface ActivityService {
  void addActivity(Activity activity) throws SQLException, UserNotFoundException;
  void deleteActivity(Long id) throws SQLException;
  List<Activity> getAllActivities() throws SQLException;
}
