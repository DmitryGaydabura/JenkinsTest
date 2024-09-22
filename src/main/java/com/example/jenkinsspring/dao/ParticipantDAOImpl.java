package com.example.jenkinsspring.dao;

import com.example.jenkinsspring.model.Participant;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParticipantDAOImpl implements ParticipantDAO {
  private Connection connection;

  public ParticipantDAOImpl(Connection connection) {
    this.connection = connection;
  }

  @Override
  public void addParticipant(Participant participant) throws SQLException {
    String sql = "INSERT INTO participants (name, team) VALUES (?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, participant.getName());
      stmt.setString(2, participant.getTeam());
      int affectedRows = stmt.executeUpdate();

      if (affectedRows == 0) {
        throw new SQLException("Creating participant failed, no rows affected.");
      }

      try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          participant.setId(generatedKeys.getLong(1));
        } else {
          throw new SQLException("Creating participant failed, no ID obtained.");
        }
      }
    }
  }

  @Override
  public List<Participant> getParticipantsByTeam(String team) throws SQLException {
    List<Participant> participants = new ArrayList<>();
    String sql = "SELECT id, name, team FROM participants WHERE team = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, team);
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Participant participant = new Participant();
          participant.setId(rs.getLong("id"));
          participant.setName(rs.getString("name"));
          participant.setTeam(rs.getString("team"));
          participants.add(participant);
        }
      }
    }
    return participants;
  }

  public boolean deleteParticipantById(int id) throws SQLException {
    String query = "DELETE FROM participants WHERE id = ?";
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setInt(1, id);
      int rowsAffected = statement.executeUpdate();

      // Если строка была удалена, фиксируем транзакцию
      if (rowsAffected > 0) {
        connection.commit(); // Явный commit транзакции
      }

      return rowsAffected > 0;
    } catch (SQLException e) {
      connection.rollback(); // Откат в случае ошибки
      throw e;
    }
  }


}
