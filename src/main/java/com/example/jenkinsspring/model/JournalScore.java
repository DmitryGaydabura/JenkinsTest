package com.example.jenkinsspring.model;

import java.util.Date;

public class JournalScore {
  private int id;
  private int participantId;
  private int score;
  private Date date;

  // Getters and setters
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getParticipantId() {
    return participantId;
  }

  public void setParticipantId(int participantId) {
    this.participantId = participantId;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }
}
