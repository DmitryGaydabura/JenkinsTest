package com.example.jenkinsspring.dao;

import com.example.jenkinsspring.model.Pair;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PairDAOImpl implements PairDAO {
  private Connection connection;

  public PairDAOImpl(Connection connection) {
    this.connection = connection;
  }

  @Override
  public void addPair(Pair pair) throws SQLException {
    String sql = "INSERT INTO pairs (user1_id, user2_id) VALUES (?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setLong(1, pair.getUser1Id());
      stmt.setLong(2, pair.getUser2Id());
      stmt.executeUpdate();

      try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          pair.setId(generatedKeys.getLong(1));
        }
      }
    }
  }

  @Override
  public List<Pair> getAllPairs() throws SQLException {
    List<Pair> pairs = new ArrayList<>();
    String sql = "SELECT id, user1_id, user2_id FROM pairs";
    try (PreparedStatement stmt = connection.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        Pair pair = new Pair();
        pair.setId(rs.getLong("id"));
        pair.setUser1Id(rs.getLong("user1_id"));
        pair.setUser2Id(rs.getLong("user2_id"));
        pairs.add(pair);
      }
    }
    return pairs;
  }

  @Override
  public boolean pairExists(Long user1Id, Long user2Id) throws SQLException {
    String sql = "SELECT COUNT(*) FROM pairs WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setLong(1, user1Id);
      stmt.setLong(2, user2Id);
      stmt.setLong(3, user2Id);
      stmt.setLong(4, user1Id);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return rs.getInt(1) > 0;
        }
      }
    }
    return false;
  }
}
