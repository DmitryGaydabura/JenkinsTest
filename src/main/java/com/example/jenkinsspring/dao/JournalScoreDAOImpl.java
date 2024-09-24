package com.example.jenkinsspring.dao;

import com.example.jenkinsspring.model.JournalScore;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация интерфейса JournalScoreDAO для взаимодействия с базой данных.
 */
public class JournalScoreDAOImpl implements JournalScoreDAO {
  private final Connection connection;

  /**
   * Конструктор, принимающий соединение с базой данных.
   *
   * @param connection Соединение с базой данных.
   */
  public JournalScoreDAOImpl(Connection connection) {
    this.connection = connection;
  }

  @Override
  public void addOrUpdateScore(JournalScore score) throws SQLException {
    // Проверяем, существует ли уже оценка для данного участника и даты
    String checkSql = "SELECT COUNT(*) FROM journal_scores WHERE participant_id = ? AND date = ?";
    try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
      checkStmt.setInt(1, score.getParticipantId());
      checkStmt.setDate(2, new java.sql.Date(score.getDate().getTime()));
      try (ResultSet rs = checkStmt.executeQuery()) {
        if (rs.next() && rs.getInt(1) > 0) {
          // Обновляем существующую оценку
          String updateSql = "UPDATE journal_scores SET score = ? WHERE participant_id = ? AND date = ?";
          try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
            updateStmt.setString(1, String.valueOf(score.getScore()));
            updateStmt.setInt(2, score.getParticipantId());
            updateStmt.setDate(3, new java.sql.Date(score.getDate().getTime()));
            updateStmt.executeUpdate();
          }
        } else {
          // Добавляем новую оценку
          String insertSql = "INSERT INTO journal_scores (participant_id, score, date) VALUES (?, ?, ?)";
          try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
            insertStmt.setInt(1, score.getParticipantId());
            insertStmt.setString(2, String.valueOf(score.getScore()));
            insertStmt.setDate(3, new java.sql.Date(score.getDate().getTime()));
            insertStmt.executeUpdate();
          }
        }
      }
    }
  }

  @Override
  public List<JournalScore> getAllScores() throws SQLException {
    String sql = "SELECT participant_id, score, date FROM journal_scores";
    List<JournalScore> scores = new ArrayList<>();
    try (PreparedStatement stmt = connection.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        JournalScore score = mapRowToJournalScore(rs);
        scores.add(score);
      }
    }
    return scores;
  }

  @Override
  public int deleteScoresByDate(Date date) throws SQLException {
    String sql = "DELETE FROM journal_scores WHERE date = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setDate(1, date);
      return stmt.executeUpdate();
    }
  }

  /**
   * Преобразует строку результата запроса в объект JournalScore.
   *
   * @param rs Результат запроса.
   * @return Объект JournalScore.
   * @throws SQLException Если возникает ошибка при доступе к данным.
   */
  private JournalScore mapRowToJournalScore(ResultSet rs) throws SQLException {
    JournalScore score = new JournalScore();
    score.setParticipantId(rs.getInt("participant_id"));
    String scoreStr = rs.getString("score");
    if (scoreStr.equalsIgnoreCase("Н")) {
      score.setScore("Н");
    } else {
      score.setScore(Double.parseDouble(scoreStr));
    }
    score.setDate(rs.getDate("date"));
    return score;
  }
}
