package com.example.jenkinsspring.dao;

import com.example.jenkinsspring.model.Participant;

import java.sql.SQLException;
import java.util.List;

/**
 * Интерфейс для операций с участниками в базе данных.
 */
public interface ParticipantDAO {
  /**
   * Добавляет нового участника в базу данных.
   *
   * @param participant Участник для добавления.
   * @throws SQLException Если возникает ошибка при доступе к базе данных.
   */
  void addParticipant(Participant participant) throws SQLException;

  /**
   * Получает список участников по команде.
   *
   * @param team Название команды.
   * @return Список участников.
   * @throws SQLException Если возникает ошибка при доступе к базе данных.
   */
  List<Participant> getParticipantsByTeam(String team) throws SQLException;

  /**
   * Мягко удаляет участника по его ID.
   *
   * @param participantId ID участника для удаления.
   * @throws SQLException Если возникает ошибка при доступе к базе данных.
   */
  void softDeleteParticipant(int participantId) throws SQLException;

  /**
   * Получает участника по его ID.
   *
   * @param participantId ID участника.
   * @return Объект участника или null, если не найден.
   * @throws SQLException Если возникает ошибка при доступе к базе данных.
   */
  Participant getParticipantById(int participantId) throws SQLException;

  /**
   * Получает участника по его имени.
   *
   * @param name Имя участника.
   * @return Объект участника или null, если не найден.
   * @throws SQLException Если возникает ошибка при доступе к базе данных.
   */
  Participant getParticipantByName(String name) throws SQLException;

  /**
   * Восстанавливает мягко удаленного участника по его ID.
   *
   * @param participantId ID участника для восстановления.
   * @throws SQLException Если возникает ошибка при доступе к базе данных.
   */
  void restoreParticipant(int participantId) throws SQLException;
}
