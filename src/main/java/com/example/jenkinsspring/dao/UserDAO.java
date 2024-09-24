package com.example.jenkinsspring.dao;

import com.example.jenkinsspring.model.User;

import java.sql.SQLException;
import java.util.List;

/**
 * Интерфейс для операций с пользователями в базе данных.
 */
public interface UserDAO {
  /**
   * Добавляет нового пользователя в базу данных.
   *
   * @param user Пользователь для добавления.
   * @throws SQLException Если возникает ошибка при доступе к базе данных.
   */
  void addUser(User user) throws SQLException;

  /**
   * Обновляет существующего пользователя в базе данных.
   *
   * @param user Пользователь с обновленными данными.
   * @throws SQLException Если возникает ошибка при доступе к базе данных.
   */
  void updateUser(User user) throws SQLException;

  /**
   * Мягко удаляет пользователя по его ID.
   *
   * @param userId ID пользователя для удаления.
   * @throws SQLException Если возникает ошибка при доступе к базе данных.
   */
  void softDeleteUser(Long userId) throws SQLException;

  /**
   * Получает список всех активных пользователей.
   *
   * @return Список пользователей.
   * @throws SQLException Если возникает ошибка при доступе к базе данных.
   */
  List<User> getAllUsers() throws SQLException;

  /**
   * Получает пользователя по его ID.
   *
   * @param userId ID пользователя.
   * @return Объект пользователя или null, если не найден.
   * @throws SQLException Если возникает ошибка при доступе к базе данных.
   */
  User getUserById(Long userId) throws SQLException;

  /**
   * Получает список пользователей по команде.
   *
   * @param team Название команды.
   * @return Список пользователей.
   * @throws SQLException Если возникает ошибка при доступе к базе данных.
   */
  List<User> getUsersByTeam(String team) throws SQLException;
}
