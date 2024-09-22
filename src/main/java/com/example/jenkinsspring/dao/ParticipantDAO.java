package com.example.jenkinsspring.dao;

import com.example.jenkinsspring.model.Participant;
import java.sql.SQLException;
import java.util.List;

public interface ParticipantDAO {
  void addParticipant(Participant participant) throws SQLException;
  List<Participant> getParticipantsByTeam(String team) throws SQLException;
}

