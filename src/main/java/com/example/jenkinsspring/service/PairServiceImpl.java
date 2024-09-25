package com.example.jenkinsspring.service;

import com.example.jenkinsspring.exception.InsufficientParticipantsException;
import com.example.jenkinsspring.exception.PairGenerationException;
import com.example.jenkinsspring.model.Pair;
import com.example.jenkinsspring.model.Participant;
import java.sql.SQLException;
import java.util.*;

public class PairServiceImpl implements PairService {
  private Set<Pair> pairDatabase = new HashSet<>(); // Замените на реальное хранилище (БД)
  private ParticipantService participantService;

  /**
   * Конструктор с зависимостью от ParticipantService
   */
  public PairServiceImpl(ParticipantService participantService) {
    this.participantService = participantService;
  }

  @Override
  public List<Pair> generatePairs()
      throws InsufficientParticipantsException, PairGenerationException, SQLException {
    // Получение участников из команд "blue" и "yellow"
    List<Participant> blueParticipants = participantService.getParticipantsByTeam("blue");
    List<Participant> yellowParticipants = participantService.getParticipantsByTeam("yellow");

    if (blueParticipants.isEmpty() || yellowParticipants.isEmpty()) {
      throw new InsufficientParticipantsException("Недостаточно участников в одной из команд для формирования пар.");
    }

    // Создание всех возможных уникальных пар
    List<Pair> allPossiblePairs = new ArrayList<>();
    for (Participant blue : blueParticipants) {
      for (Participant yellow : yellowParticipants) {
        allPossiblePairs.add(new Pair(blue.getId(), yellow.getId()));
      }
    }

    // Фильтрация уже использованных пар
    List<Pair> availablePairs = new ArrayList<>();
    for (Pair pair : allPossiblePairs) {
      if (!isPairExists(pair.getBlueParticipantId(), pair.getYellowParticipantId())) {
        availablePairs.add(pair);
      }
    }

    // Если все пары уже использованы, сбрасываем историю и добавляем все возможные пары
    if (availablePairs.isEmpty()) {
      resetPairs();
      availablePairs.addAll(allPossiblePairs);
    }

    // Перемешиваем доступные пары для случайного выбора
    Collections.shuffle(availablePairs);

    // Определяем количество пар, которые можно создать в этом вызове
    int numberOfPairs = Math.min(blueParticipants.size(), yellowParticipants.size());

    List<Pair> newPairs = new ArrayList<>();
    Set<Integer> usedBlueIds = new HashSet<>();
    Set<Integer> usedYellowIds = new HashSet<>();

    for (Pair pair : availablePairs) {
      if (!usedBlueIds.contains(pair.getBlueParticipantId()) && !usedYellowIds.contains(pair.getYellowParticipantId())) {
        newPairs.add(pair);
        usedBlueIds.add(pair.getBlueParticipantId());
        usedYellowIds.add(pair.getYellowParticipantId());

        // Если достигли нужного количества пар, выходим
        if (newPairs.size() >= numberOfPairs) {
          break;
        }
      }
    }

    if (newPairs.isEmpty()) {
      throw new PairGenerationException("Не удалось сформировать новые пары.");
    }

    // Сохранение новых пар
    savePairs(newPairs);

    return newPairs;
  }

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

  @Override
  public void resetPairs() {
    pairDatabase.clear();
  }
}
