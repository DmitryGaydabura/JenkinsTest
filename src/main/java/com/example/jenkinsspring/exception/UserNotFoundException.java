package com.example.jenkinsspring.exception;

/**
 * Исключение, выбрасываемое, когда пользователь не найден.
 */
public class UserNotFoundException extends Exception {
  /**
   * Конструктор с сообщением об ошибке.
   *
   * @param message Сообщение об ошибке.
   */
  public UserNotFoundException(String message) {
    super(message);
  }

  /**
   * Конструктор с сообщением об ошибке и причиной.
   *
   * @param message Сообщение об ошибке.
   * @param cause   Причина ошибки.
   */
  public UserNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
