package com.example.jenkinsspring.model;

import java.util.Date;

/**
 * Модель активности.
 */
public class Activity {
  private Long id;
  private Long userId;
  private String description;
  private Date activityDate;

  // Конструкторы
  public Activity() {}

  public Activity(Long id, Long userId, String description, Date activityDate) {
    this.id = id;
    this.userId = userId;
    this.description = description;
    this.activityDate = activityDate;
  }

  // Геттеры и сеттеры
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

  public Date getActivityDate() {
    return activityDate;
  }

  public void setActivityDate(Date activityDate) {
    this.activityDate = activityDate;
  }
}
