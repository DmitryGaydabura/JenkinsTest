package com.example.jenkinsspring.dao;

import com.example.jenkinsspring.model.Activity;
import java.sql.SQLException;
import java.util.List;

public interface ActivityDAO {
  void addActivity(Activity activity) throws SQLException;
  void updateActivity(Activity activity) throws SQLException;
  void deleteActivity(Long id) throws SQLException;
  List<Activity> getAllActivities() throws SQLException;
}
