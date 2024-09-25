package com.example.jenkinsspring.service;

import com.example.jenkinsspring.model.Pair;
import java.util.List;

public interface PairService {
  List<Pair> getAllPairs();
  void savePairs(List<Pair> pairs);
  boolean isPairExists(Integer blueId, Integer yellowId);
}
