package com.example.jenkinsspring.model;

import java.util.Date;

/**
 * Модель оценки в журнале.
 */
public class JournalScore {
  private int participantId;
  private Object score; // Может быть String "Н" или Double от 0 до 6 с шагом 0.5
  private Date date;

  // Конструкторы
  public JournalScore() {}

  public JournalScore(int participantId, Object score, Date date) {
    this.participantId = participantId;
    this.score = score;
    this.date = date;
  }

  // Геттеры и сеттеры
  public int getParticipantId() {
    return participantId;
  }

  public void setParticipantId(int participantId) {
    this.participantId = participantId;
  }

  public Object getScore() {
    return score;
  }

  public void setScore(Object score) {
    this.score = score;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  /**
   * Проверяет, является ли оценка допустимой.
   *
   * @return true, если оценка допустима, иначе false.
   */
  public boolean isValidScore() {
    if (score instanceof String) {
      return "Н".equalsIgnoreCase((String) score);
    } else if (score instanceof Double) {
      Double doubleScore = (Double) score;
      return doubleScore >= 0 && doubleScore <= 6 && (doubleScore * 2) % 1 == 0;
    }
    return false;
  }
}
