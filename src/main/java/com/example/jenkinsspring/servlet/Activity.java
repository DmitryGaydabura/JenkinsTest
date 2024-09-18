package com.example.jenkinsspring.servlet;

import java.sql.Timestamp;

public class Activity {
  private Long id;
  private Long userId;
  private String firstName;
  private String lastName;

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

  private String description;
  private Timestamp activityDate;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Timestamp getActivityDate() {
    return activityDate;
  }

  public void setActivityDate(Timestamp activityDate) {
    this.activityDate = activityDate;
  }
// Геттеры и сеттеры
}

