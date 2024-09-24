package com.example.jenkinsspring.exception;

/**
 * Исключение, выбрасываемое, когда оценка в журнале не найдена.
 */
public class JournalScoreNotFoundException extends Exception {
  /**
   * Конструктор с сообщением об ошибке.
   *
   * @param message Сообщение об ошибке.
   */
  public JournalScoreNotFoundException(String message) {
    super(message);
  }

  /**
   * Конструктор с сообщением об ошибке и причиной.
   *
   * @param message Сообщение об ошибке.
   * @param cause   Причина ошибки.
   */
  public JournalScoreNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
