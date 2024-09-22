package com.example.jenkinsspring.dao;

import com.example.jenkinsspring.model.User;
import java.sql.SQLException;
import java.util.List;

public interface UserDAO {
  void addUser(User user) throws SQLException;
  void updateUser(User user) throws SQLException;
  void deleteUser(Long id) throws SQLException;
  List<User> getAllUsers() throws SQLException;
  User getUserById(Long id) throws SQLException; // Новый метод
  List<User> getUsersByTeam(String team) throws SQLException;

}
