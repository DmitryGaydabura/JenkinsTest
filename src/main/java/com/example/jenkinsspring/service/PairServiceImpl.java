package com.example.jenkinsspring.service;

import com.example.jenkinsspring.model.Pair;
import java.util.ArrayList;
import java.util.List;

public class PairServiceImpl implements PairService {
  private List<Pair> pairDatabase = new ArrayList<>();

  @Override
  public List<Pair> getAllPairs() {
    return new ArrayList<>(pairDatabase);
  }

  @Override
  public void savePairs(List<Pair> pairs) {
    pairDatabase.addAll(pairs);
  }

  @Override
  public boolean isPairExists(Integer blueId, Integer yellowId) {
    return pairDatabase.stream()
        .anyMatch(pair -> pair.getBlueParticipantId().equals(blueId) &&
            pair.getYellowParticipantId().equals(yellowId));
  }
}
