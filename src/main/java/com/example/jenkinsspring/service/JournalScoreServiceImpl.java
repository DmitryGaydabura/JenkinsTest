package com.example.jenkinsspring.service;

import com.example.jenkinsspring.dao.JournalScoreDAO;
import com.example.jenkinsspring.dao.JournalScoreDAOImpl;
import com.example.jenkinsspring.model.JournalScore;
import com.example.jenkinsspring.util.DataSourceManager;

import java.sql.Date;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Реализация интерфейса JournalScoreService для управления оценками в журнале.
 */
public class JournalScoreServiceImpl implements JournalScoreService {
  private JournalScoreDAO journalScoreDAO;

  /**
   * Конструктор, инициализирующий JournalScoreDAO.
   *
   * @throws SQLException Если возникает ошибка при подключении к базе данных.
   */
  public JournalScoreServiceImpl() throws SQLException {
    try (Connection connection = DataSourceManager.getDataSource().getConnection()) {
      this.journalScoreDAO = new JournalScoreDAOImpl(connection);
    }
  }

  @Override
  public void addOrUpdateScore(JournalScore score) throws SQLException, IllegalArgumentException {
    // Валидация оценки
    if (score.getParticipantId() <= 0 || score.getDate() == null || score.getScore() == null) {
      throw new IllegalArgumentException("Отсутствуют обязательные поля оценки.");
    }

    if (!score.isValidScore()) {
      throw new IllegalArgumentException("Некорректное значение оценки. Допустимые: 'Н', 0-6 с шагом 0.5.");
    }

    try (Connection connection = DataSourceManager.getDataSource().getConnection()) {
      connection.setAutoCommit(false);
      journalScoreDAO = new JournalScoreDAOImpl(connection);
      journalScoreDAO.addOrUpdateScore(score);
      connection.commit();
    } catch (SQLException e) {
      throw e;
    }
  }

  @Override
  public List<JournalScore> getAllScores() throws SQLException {
    try (Connection connection = DataSourceManager.getDataSource().getConnection()) {
      journalScoreDAO = new JournalScoreDAOImpl(connection);
      return journalScoreDAO.getAllScores();
    } catch (SQLException e) {
      throw e;
    }
  }

  @Override
  public int deleteScoresByDate(Date date) throws SQLException {
    try (Connection connection = DataSourceManager.getDataSource().getConnection()) {
      journalScoreDAO = new JournalScoreDAOImpl(connection);
      return journalScoreDAO.deleteScoresByDate(date);
    } catch (SQLException e) {
      throw e;
    }
  }
}
