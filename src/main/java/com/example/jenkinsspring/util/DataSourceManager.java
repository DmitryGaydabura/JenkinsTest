package com.example.jenkinsspring.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSourceManager {
  private static HikariDataSource dataSource;

  static {
    String dbUrl = System.getenv("DB_URL");
    String dbUsername = System.getenv("DB_USERNAME");
    String dbPassword = System.getenv("DB_PASSWORD");

    if (dbUrl == null || dbUsername == null || dbPassword == null) {
      throw new IllegalStateException("Не все переменные окружения установлены: DB_URL, DB_USERNAME, DB_PASSWORD");
    }

    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(dbUrl);
    config.setUsername(dbUsername);
    config.setPassword(dbPassword);
    config.setDriverClassName("org.postgresql.Driver");
    config.setMaximumPoolSize(10);
    config.setMinimumIdle(2);
    config.setIdleTimeout(30000);
    config.setMaxLifetime(1800000);
    config.setConnectionTimeout(30000);

    dataSource = new HikariDataSource(config);
  }

  public static HikariDataSource getDataSource() {
    return dataSource;
  }
}
