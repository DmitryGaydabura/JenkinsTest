package com.example.jenkinsspring.model;

/**
 * Модель пользователя.
 */
public class User {
  private Long id;
  private String firstName;
  private String lastName;
  private int age;
  private String team;
  private boolean deleted; // Для механизма Soft Delete

  // Конструкторы
  public User() {}

  public User(Long id, String firstName, String lastName, int age, String team) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.age = age;
    this.team = team;
    this.deleted = false;
  }

  // Геттеры и сеттеры
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    if(age <= 0){
      throw new IllegalArgumentException("Возраст должен быть положительным числом.");
    }
    this.age = age;
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
