package com.example.jenkinsspring.dao;

import com.example.jenkinsspring.model.JournalScore;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JournalScoreDAOImpl {

  private Connection connection;

  public JournalScoreDAOImpl(Connection connection) {
    this.connection = connection;
  }

  public List<JournalScore> getScoresByDate(Date date) throws SQLException {
    List<JournalScore> scores = new ArrayList<>();
    String query = "SELECT * FROM journal_scores WHERE date = ?";
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setDate(1, new java.sql.Date(date.getTime()));
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        JournalScore score = new JournalScore();
        score.setId(rs.getInt("id"));
        score.setParticipantId(rs.getInt("participant_id"));
        score.setScore(rs.getInt("score"));
        score.setDate(rs.getDate("date"));
        scores.add(score);
      }
    }
    return scores;
  }

  public void addOrUpdateScore(JournalScore score) throws SQLException {
    String query = "INSERT INTO journal_scores (participant_id, score, date) " +
        "VALUES (?, ?, ?) " +
        "ON CONFLICT (participant_id, date) DO UPDATE SET score = EXCLUDED.score";
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setInt(1, score.getParticipantId());
      statement.setInt(2, score.getScore());
      statement.setDate(3, new java.sql.Date(score.getDate().getTime()));
      statement.executeUpdate();
    }
  }
}
