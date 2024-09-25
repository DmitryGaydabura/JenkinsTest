package com.example.jenkinsspring.model;

public class Pair {
  private Integer blueParticipantId;
  private Integer yellowParticipantId;

  public Pair(Integer blueParticipantId, Integer yellowParticipantId) {
    this.blueParticipantId = blueParticipantId;
    this.yellowParticipantId = yellowParticipantId;
  }

  public Integer getBlueParticipantId() {
    return blueParticipantId;
  }

  public void setBlueParticipantId(Integer blueParticipantId) {
    this.blueParticipantId = blueParticipantId;
  }

  public Integer getYellowParticipantId() {
    return yellowParticipantId;
  }

  public void setYellowParticipantId(Integer yellowParticipantId) {
    this.yellowParticipantId = yellowParticipantId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Pair pair = (Pair) o;

    if (!blueParticipantId.equals(pair.blueParticipantId)) return false;
    return yellowParticipantId.equals(pair.yellowParticipantId);
  }

  @Override
  public int hashCode() {
    int result = blueParticipantId.hashCode();
    result = 31 * result + yellowParticipantId.hashCode();
    return result;
  }
}
