package com.example.jenkinsspring.dao;

import com.example.jenkinsspring.model.JournalParticipant;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JournalParticipantDAOImpl {
  private Connection connection;

  public JournalParticipantDAOImpl(Connection connection) {
    this.connection = connection;
  }

  public List<JournalParticipant> getAllParticipants() throws SQLException {
    List<JournalParticipant> participants = new ArrayList<>();
    String query = "SELECT * FROM journal_participants";
    try (Statement statement = connection.createStatement()) {
      ResultSet rs = statement.executeQuery(query);
      while (rs.next()) {
        JournalParticipant participant = new JournalParticipant();
        participant.setId(rs.getInt("id"));
        participant.setFirstName(rs.getString("first_name"));
        participant.setLastName(rs.getString("last_name"));
        participants.add(participant);
      }
    }
    return participants;
  }

  public void addParticipant(JournalParticipant participant) throws SQLException {
    String query = "INSERT INTO journal_participants (first_name, last_name) VALUES (?, ?)";
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, participant.getFirstName());
      statement.setString(2, participant.getLastName());
      statement.executeUpdate();
    }
  }

  public void deleteParticipant(int id) throws SQLException {
    String query = "DELETE FROM journal_participants WHERE id = ?";
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setInt(1, id);
      statement.executeUpdate();
    }
  }
}
