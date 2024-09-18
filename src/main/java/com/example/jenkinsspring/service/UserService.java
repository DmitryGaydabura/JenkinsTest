package com.example.jenkinsspring.service;

import com.example.jenkinsspring.model.User;
import java.sql.SQLException;
import java.util.List;

public interface UserService {
  void addUser(User user) throws SQLException;
  void updateUser(User user) throws SQLException;
  void deleteUser(Long id) throws SQLException;
  List<User> getAllUsers() throws SQLException;
}
