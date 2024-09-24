package com.example.jenkinsspring.service;

import com.example.jenkinsspring.dao.ParticipantDAO;
import com.example.jenkinsspring.dao.ParticipantDAOImpl;
import com.example.jenkinsspring.exception.ParticipantNotFoundException;
import com.example.jenkinsspring.model.Participant;
import com.example.jenkinsspring.util.DataSourceManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Реализация интерфейса ParticipantService для управления участниками.
 */
public class ParticipantServiceImpl implements ParticipantService {
  private ParticipantDAO participantDAO;

  /**
   * Конструктор, инициализирующий ParticipantDAO.
   *
   * @throws SQLException если возникает ошибка при подключении к базе данных.
   */
  public ParticipantServiceImpl() throws SQLException {
    try (Connection connection = DataSourceManager.getDataSource().getConnection()) {
      this.participantDAO = new ParticipantDAOImpl(connection);
    }
  }

  @Override
  public void addParticipant(Participant participant) throws SQLException {
    try (Connection connection = DataSourceManager.getDataSource().getConnection()) {
      connection.setAutoCommit(false);
      participantDAO = new ParticipantDAOImpl(connection);
      participantDAO.addParticipant(participant);
      connection.commit();
    } catch (SQLException e) {
      throw e;
    }
  }

  @Override
  public List<Participant> getParticipantsByTeam(String team) throws SQLException {
    try (Connection connection = DataSourceManager.getDataSource().getConnection()) {
      participantDAO = new ParticipantDAOImpl(connection);
      return participantDAO.getParticipantsByTeam(team);
    } catch (SQLException e) {
      throw e;
    }
  }

  @Override
  public void softDeleteParticipant(int participantId) throws SQLException, ParticipantNotFoundException {
    try (Connection connection = DataSourceManager.getDataSource().getConnection()) {
      connection.setAutoCommit(false);
      participantDAO = new ParticipantDAOImpl(connection);
      Participant participant = participantDAO.getParticipantById(participantId);
      if (participant == null) {
        throw new ParticipantNotFoundException("Участник с ID " + participantId + " не найден.");
      }
      participantDAO.softDeleteParticipant(participantId);
      connection.commit();
    } catch (SQLException | ParticipantNotFoundException e) {
      throw e;
    }
  }

  @Override
  public Participant restoreParticipantByName(String name) throws SQLException, ParticipantNotFoundException {
    try (Connection connection = DataSourceManager.getDataSource().getConnection()) {
      connection.setAutoCommit(false);
      participantDAO = new ParticipantDAOImpl(connection);
      Participant participant = participantDAO.getParticipantByName(name);
      if (participant == null || !participant.isDeleted()) {
        throw new ParticipantNotFoundException("Удаленный участник с именем " + name + " не найден.");
      }
      participantDAO.restoreParticipant(participant.getId());
      connection.commit();
      return participant;
    } catch (SQLException | ParticipantNotFoundException e) {
      throw e;
    }
  }
}
