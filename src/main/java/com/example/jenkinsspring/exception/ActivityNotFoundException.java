package com.example.jenkinsspring.exception;

/**
 * Исключение, выбрасываемое, когда активность не найдена.
 */
public class ActivityNotFoundException extends Exception {
  /**
   * Конструктор с сообщением об ошибке.
   *
   * @param message Сообщение об ошибке.
   */
  public ActivityNotFoundException(String message) {
    super(message);
  }

  /**
   * Конструктор с сообщением об ошибке и причиной.
   *
   * @param message Сообщение об ошибке.
   * @param cause   Причина ошибки.
   */
  public ActivityNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
