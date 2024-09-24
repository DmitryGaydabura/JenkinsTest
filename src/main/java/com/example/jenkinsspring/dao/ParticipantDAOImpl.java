package com.example.jenkinsspring.dao;

import com.example.jenkinsspring.model.Participant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация интерфейса ParticipantDAO для взаимодействия с базой данных.
 */
public class ParticipantDAOImpl implements ParticipantDAO {
  private final Connection connection;

  /**
   * Конструктор, принимающий соединение с базой данных.
   *
   * @param connection Соединение с базой данных.
   */
  public ParticipantDAOImpl(Connection connection) {
    this.connection = connection;
  }

  @Override
  public void addParticipant(Participant participant) throws SQLException {
    String sql = "INSERT INTO participants (name, team, deleted) VALUES (?, ?, false)";
    try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, participant.getName());
      stmt.setString(2, participant.getTeam());
      stmt.executeUpdate();

      try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          participant.setId(generatedKeys.getInt(1));
        } else {
          throw new SQLException("Добавление участника не удалось, не получен ID.");
        }
      }
    }
  }

  @Override
  public List<Participant> getParticipantsByTeam(String team) throws SQLException {
    String sql = "SELECT id, name, team FROM participants WHERE team = ? AND deleted = false";
    List<Participant> participants = new ArrayList<>();
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, team);
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Participant participant = mapRowToParticipant(rs);
          participants.add(participant);
        }
      }
    }
    return participants;
  }

  @Override
  public void softDeleteParticipant(int participantId) throws SQLException {
    String sql = "UPDATE participants SET deleted = true WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, participantId);
      int affectedRows = stmt.executeUpdate();
      if (affectedRows == 0) {
        throw new SQLException("Мягкое удаление участника не удалось, участник не найден.");
      }
    }
  }

  @Override
  public Participant getParticipantById(int participantId) throws SQLException {
    String sql = "SELECT id, name, team FROM participants WHERE id = ? AND deleted = false";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, participantId);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return mapRowToParticipant(rs);
        }
      }
    }
    return null;
  }

  @Override
  public Participant getParticipantByName(String name) throws SQLException {
    String sql = "SELECT id, name, team, deleted FROM participants WHERE name = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, name);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          Participant participant = mapRowToParticipant(rs);
          participant.setDeleted(rs.getBoolean("deleted"));
          return participant;
        }
      }
    }
    return null;
  }

  @Override
  public void restoreParticipant(int participantId) throws SQLException {
    String sql = "UPDATE participants SET deleted = false WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, participantId);
      int affectedRows = stmt.executeUpdate();
      if (affectedRows == 0) {
        throw new SQLException("Восстановление участника не удалось, участник не найден.");
      }
    }
  }

  /**
   * Преобразует строку результата запроса в объект Participant.
   *
   * @param rs Результат запроса.
   * @return Объект Participant.
   * @throws SQLException Если возникает ошибка при доступе к данным.
   */
  private Participant mapRowToParticipant(ResultSet rs) throws SQLException {
    Participant participant = new Participant();
    participant.setId(rs.getInt("id"));
    participant.setName(rs.getString("name"));
    participant.setTeam(rs.getString("team"));
    return participant;
  }
}
