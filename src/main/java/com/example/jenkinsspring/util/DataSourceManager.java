package com.example.jenkinsspring.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * Класс для управления пулом соединений с базой данных с использованием HikariCP.
 */
public class DataSourceManager {
  private static HikariDataSource dataSource;

  static {
    try {
      HikariConfig config = new HikariConfig();
      // Получение конфигурационных параметров из переменных окружения
      config.setJdbcUrl(System.getenv("DB_URL")); // Пример: jdbc:postgresql://host:port/database
      config.setUsername(System.getenv("DB_USERNAME"));
      config.setPassword(System.getenv("DB_PASSWORD"));
      config.setDriverClassName(System.getenv("DB_DRIVER_CLASS")); // Например, org.postgresql.Driver

      // Настройки пула соединений
      config.setMaximumPoolSize(10);
      config.setMinimumIdle(2);
      config.setIdleTimeout(30000);
      config.setConnectionTimeout(30000);
      config.setMaxLifetime(1800000);

      dataSource = new HikariDataSource(config);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Ошибка инициализации пула соединений с БД", e);
    }
  }

  // Приватный конструктор для предотвращения создания экземпляров
  private DataSourceManager() {}

  /**
   * Возвращает DataSource для доступа к базе данных.
   *
   * @return DataSource
   */
  public static DataSource getDataSource() {
    return dataSource;
  }

  /**
   * Закрывает пул соединений при остановке приложения.
   */
  public static void closeDataSource() {
    if (dataSource != null && !dataSource.isClosed()) {
      dataSource.close();
    }
  }
}
