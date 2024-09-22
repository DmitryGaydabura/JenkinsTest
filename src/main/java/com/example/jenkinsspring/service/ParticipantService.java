package com.example.jenkinsspring.service;

import com.example.jenkinsspring.model.Participant;
import java.sql.SQLException;
import java.util.List;

public interface ParticipantService {
  void addParticipant(Participant participant) throws SQLException;
  List<Participant> getParticipantsByTeam(String team) throws SQLException;
  void closeConnection() throws SQLException;

  boolean deleteParticipantById(int id) throws SQLException;
}

