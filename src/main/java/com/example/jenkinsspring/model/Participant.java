package com.example.jenkinsspring.model;


public class Participant {
  private Long id;
  private String name;
  private String team;

  public Participant() {}

  public Participant(Long id, String name, String team) {
    this.id = id;
    this.name = name;
    this.team = team;
  }

  // Геттеры и сеттеры

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getTeam() {
    return team;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setTeam(String team) {
    this.team = team;
  }
}

