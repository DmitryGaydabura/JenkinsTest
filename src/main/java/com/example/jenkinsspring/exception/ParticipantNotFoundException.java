package com.example.jenkinsspring.exception;

/**
 * Исключение, выбрасываемое, когда участник не найден.
 */
public class ParticipantNotFoundException extends Exception {
  /**
   * Конструктор с сообщением об ошибке.
   *
   * @param message Сообщение об ошибке.
   */
  public ParticipantNotFoundException(String message) {
    super(message);
  }

  /**
   * Конструктор с сообщением об ошибке и причиной.
   *
   * @param message Сообщение об ошибке.
   * @param cause   Причина ошибки.
   */
  public ParticipantNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
