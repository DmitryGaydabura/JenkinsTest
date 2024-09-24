package com.example.jenkinsspring.service;

import com.example.jenkinsspring.model.JournalScore;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * Интерфейс для сервисных операций, связанных с оценками в журнале.
 */
public interface JournalScoreService {
  /**
   * Добавляет или обновляет оценку в журнале.
   *
   * @param score Оценка для добавления или обновления.
   * @throws SQLException                 Если возникает ошибка при доступе к базе данных.
   * @throws IllegalArgumentException     Если оценка имеет недопустимое значение.
   */
  void addOrUpdateScore(JournalScore score) throws SQLException, IllegalArgumentException;

  /**
   * Получает список всех оценок в журнале.
   *
   * @return Список оценок.
   * @throws SQLException Если возникает ошибка при доступе к базе данных.
   */
  List<JournalScore> getAllScores() throws SQLException;

  /**
   * Удаляет оценки по дате.
   *
   * @param date Дата, по которой нужно удалить оценки.
   * @return Количество удаленных строк.
   * @throws SQLException Если возникает ошибка при доступе к базе данных.
   */
  int deleteScoresByDate(Date date) throws SQLException;
}
