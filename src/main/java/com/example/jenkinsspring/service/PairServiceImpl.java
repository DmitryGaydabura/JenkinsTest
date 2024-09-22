package com.example.jenkinsspring.service;

import com.example.jenkinsspring.dao.PairDAO;
import com.example.jenkinsspring.model.Pair;
import java.sql.SQLException;
import java.util.List;

public class PairServiceImpl implements PairService {
  private PairDAO pairDAO;

  public PairServiceImpl(PairDAO pairDAO) {
    this.pairDAO = pairDAO;
  }

  @Override
  public void addPair(Pair pair) throws SQLException {
    pairDAO.addPair(pair);
  }

  @Override
  public List<Pair> getAllPairs() throws SQLException {
    return pairDAO.getAllPairs();
  }

  @Override
  public boolean pairExists(Long user1Id, Long user2Id) throws SQLException {
    return pairDAO.pairExists(user1Id, user2Id);
  }
}

