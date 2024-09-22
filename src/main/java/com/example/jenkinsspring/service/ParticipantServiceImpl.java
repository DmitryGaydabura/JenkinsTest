package com.example.jenkinsspring.service;

import com.example.jenkinsspring.dao.ParticipantDAO;
import com.example.jenkinsspring.model.Participant;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ParticipantServiceImpl implements ParticipantService {
  private ParticipantDAO participantDAO;
  private Connection connection;

  public ParticipantServiceImpl(ParticipantDAO participantDAO, Connection connection) throws SQLException {
    this.participantDAO = participantDAO;
    this.connection = connection;
    this.connection.setAutoCommit(false);
  }

  @Override
  public void addParticipant(Participant participant) throws SQLException {
    try {
      participantDAO.addParticipant(participant);
      connection.commit();
    } catch (SQLException e) {
      connection.rollback();
      throw e;
    }
  }

  @Override
  public List<Participant> getParticipantsByTeam(String team) throws SQLException {
    try {
      List<Participant> participants = participantDAO.getParticipantsByTeam(team);
      connection.commit();
      return participants;
    } catch (SQLException e) {
      connection.rollback();
      throw e;
    }
  }

  @Override
  public boolean deleteParticipantById(int id) throws SQLException {
    return participantDAO.deleteParticipantById(id);
  }

  @Override
  public void closeConnection() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }
}
