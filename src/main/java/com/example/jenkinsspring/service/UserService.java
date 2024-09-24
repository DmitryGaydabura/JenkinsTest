package com.example.jenkinsspring.service;

import com.example.jenkinsspring.model.User;
import com.example.jenkinsspring.exception.UserNotFoundException;

import java.sql.SQLException;
import java.util.List;

/**
 * Интерфейс для сервисных операций, связанных с пользователями.
 */
public interface UserService {
  /**
   * Добавляет нового пользователя.
   *
   * @param user Объект пользователя для добавления.
   * @throws SQLException если возникает ошибка при доступе к базе данных.
   */
  void addUser(User user) throws SQLException;

  /**
   * Обновляет существующего пользователя.
   *
   * @param user Объект пользователя с обновленными данными.
   * @throws SQLException если возникает ошибка при доступе к базе данных.
   */
  void updateUser(User user) throws SQLException;

  /**
   * Мягко удаляет пользователя по его ID.
   *
   * @param userId ID пользователя для удаления.
   * @throws SQLException           если возникает ошибка при доступе к базе данных.
   * @throws UserNotFoundException  если пользователь с указанным ID не найден.
   */
  void softDeleteUser(Long userId) throws SQLException, UserNotFoundException;

  /**
   * Получает список всех активных пользователей.
   *
   * @return Список пользователей.
   * @throws SQLException если возникает ошибка при доступе к базе данных.
   */
  List<User> getAllUsers() throws SQLException;

  /**
   * Получает пользователя по его ID.
   *
   * @param userId ID пользователя.
   * @return Объект пользователя.
   * @throws SQLException           если возникает ошибка при доступе к базе данных.
   * @throws UserNotFoundException  если пользователь с указанным ID не найден.
   */
  User getUserById(Long userId) throws SQLException, UserNotFoundException;

  /**
   * Получает список пользователей по команде.
   *
   * @param team Название команды.
   * @return Список пользователей.
   * @throws SQLException если возникает ошибка при доступе к базе данных.
   */
  List<User> getUsersByTeam(String team) throws SQLException;
}
