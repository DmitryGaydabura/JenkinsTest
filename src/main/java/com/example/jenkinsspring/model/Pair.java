package com.example.jenkinsspring.model;

public class Pair {
  private Long id;
  private Long user1Id;
  private Long user2Id;

  // Геттеры и сеттеры
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUser1Id() {
    return user1Id;
  }

  public void setUser1Id(Long user1Id) {
    this.user1Id = user1Id;
  }

  public Long getUser2Id() {
    return user2Id;
  }

  public void setUser2Id(Long user2Id) {
    this.user2Id = user2Id;
  }
}
