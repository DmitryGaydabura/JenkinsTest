package com.example.jenkinsspring.service;

import com.example.jenkinsspring.model.Participant;
import com.example.jenkinsspring.exception.ParticipantNotFoundException;

import java.sql.SQLException;
import java.util.List;

/**
 * Интерфейс для сервисных операций, связанных с участниками.
 */
public interface ParticipantService {
  /**
   * Добавляет нового участника.
   *
   * @param participant Объект участника для добавления.
   * @throws SQLException если возникает ошибка при доступе к базе данных.
   */
  void addParticipant(Participant participant) throws SQLException;

  /**
   * Получает список участников по команде.
   *
   * @param team Название команды.
   * @return Список участников.
   * @throws SQLException если возникает ошибка при доступе к базе данных.
   */
  List<Participant> getParticipantsByTeam(String team) throws SQLException;

  /**
   * Мягко удаляет участника по его ID.
   *
   * @param participantId ID участника для удаления.
   * @throws SQLException                 если возникает ошибка при доступе к базе данных.
   * @throws ParticipantNotFoundException если участник с указанным ID не найден.
   */
  void softDeleteParticipant(int participantId) throws SQLException, ParticipantNotFoundException;

  /**
   * Восстанавливает мягко удаленного участника по его имени.
   *
   * @param name Имя участника.
   * @return Восстановленный объект участника.
   * @throws SQLException                 если возникает ошибка при доступе к базе данных.
   * @throws ParticipantNotFoundException если участник с указанным именем не найден.
   */
  Participant restoreParticipantByName(String name) throws SQLException, ParticipantNotFoundException;
}
