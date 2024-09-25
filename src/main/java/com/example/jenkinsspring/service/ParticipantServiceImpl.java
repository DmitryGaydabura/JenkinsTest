package com.example.jenkinsspring.service;

import com.example.jenkinsspring.dao.ParticipantDAO;
import com.example.jenkinsspring.dao.ParticipantDAOImpl;
import com.example.jenkinsspring.exception.ParticipantAlreadyExistsException;
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
  public void addParticipant(Participant participant) throws SQLException, ParticipantAlreadyExistsException {
    try (Connection connection = DataSourceManager.getDataSource().getConnection()) {
      connection.setAutoCommit(false);
      participantDAO = new ParticipantDAOImpl(connection);

      // Проверяем, существует ли участник с таким же именем
      Participant existingParticipant = participantDAO.getParticipantByName(participant.getName());
      if (existingParticipant != null) {
        if (existingParticipant.isDeleted()) {
          // Участник помечен как удаленный, восстанавливаем его
          participantDAO.restoreParticipant(existingParticipant.getId());
          // Обновляем данные участника
          existingParticipant.setTeam(participant.getTeam());
          participantDAO.updateParticipant(existingParticipant);
          participant.setId(existingParticipant.getId());
        } else {
          // Участник уже существует и не помечен как удаленный
          throw new ParticipantAlreadyExistsException("Участник с именем " + participant.getName() + " уже существует.");
        }
      } else {
        // Участник не существует, добавляем нового
        participantDAO.addParticipant(participant);
      }

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
