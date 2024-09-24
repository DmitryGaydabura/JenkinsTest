package com.example.jenkinsspring.dao;

import com.example.jenkinsspring.model.JournalScore;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * Интерфейс для операций с оценками в журнале в базе данных.
 */
public interface JournalScoreDAO {
  /**
   * Добавляет или обновляет оценку в журнале.
   *
   * @param score Оценка для добавления или обновления.
   * @throws SQLException Если возникает ошибка при доступе к базе данных.
   */
  void addOrUpdateScore(JournalScore score) throws SQLException;

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
