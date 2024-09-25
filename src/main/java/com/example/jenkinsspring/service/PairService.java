package com.example.jenkinsspring.service;

import com.example.jenkinsspring.exception.InsufficientParticipantsException;
import com.example.jenkinsspring.exception.PairGenerationException;
import com.example.jenkinsspring.model.Pair;
import java.sql.SQLException;
import java.util.List;

public interface PairService {
  List<Pair> generatePairs()
      throws InsufficientParticipantsException, PairGenerationException, SQLException;
  List<Pair> getAllPairs();
  void savePairs(List<Pair> pairs);
  boolean isPairExists(Integer blueId, Integer yellowId);
  void resetPairs();
}
