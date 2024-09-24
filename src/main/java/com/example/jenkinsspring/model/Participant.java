package com.example.jenkinsspring.model;

/**
 * Модель участника.
 */
public class Participant {
  private int id;
  private String name;
  private String team;
  private boolean deleted; // Для механизма Soft Delete

  // Конструкторы
  public Participant() {}

  public Participant(int id, String name, String team) {
    this.id = id;
    this.name = name;
    this.team = team;
    this.deleted = false;
  }

  // Геттеры и сеттеры
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTeam() {
    return team;
  }

  public void setTeam(String team) {
    this.team = team;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }
}
