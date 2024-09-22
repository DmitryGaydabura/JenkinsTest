package com.example.jenkinsspring.dao;

import com.example.jenkinsspring.model.Pair;
import java.sql.SQLException;
import java.util.List;

public interface PairDAO {
  void addPair(Pair pair) throws SQLException;
  List<Pair> getAllPairs() throws SQLException;
  boolean pairExists(Long user1Id, Long user2Id) throws SQLException;
}
