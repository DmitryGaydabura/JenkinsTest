package com.example.jenkinsspring.servlet;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

@WebServlet("/activity")
public class ActivityServlet extends HttpServlet {
  private Connection connection;

  @Override
  public void init() throws ServletException {
    try {
      // Инициализация драйвера и соединения с базой данных
      Class.forName("org.postgresql.Driver");
      connection = DriverManager.getConnection(
          "jdbc:postgresql://192.168.64.5:5432/mydatabase", "myuser", "mypassword");
      connection.setAutoCommit(false); // Отключаем авто-коммит для управления транзакциями
    } catch (ClassNotFoundException | SQLException e) {
      throw new ServletException("Не удалось подключиться к базе данных", e);
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // Устанавливаем уровень изоляции для чтения данных
    try {
      connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
      List<Activity> activities = getActivities();
      connection.commit();

      // Передаем список активностей в JSP или возвращаем JSON
      req.setAttribute("activities", activities);
      req.getRequestDispatcher("/WEB-INF/views/activities.jsp").forward(req, resp);
    } catch (SQLException e) {
      try {
        connection.rollback();
      } catch (SQLException rollbackEx) {
        throw new ServletException("Ошибка отката транзакции", rollbackEx);
      }
      throw new ServletException("Ошибка при получении активности", e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // Получаем действие из параметров запроса
    String action = req.getParameter("action");

    try {
      switch (action) {
        case "add":
          connection.setAutoCommit(false);
          connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
          addActivity(req);
          connection.commit();
          break;
        case "delete":
          connection.setAutoCommit(false);
          connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
          deleteActivity(req);
          connection.commit();
          break;
        default:
          resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неизвестное действие");
          return;
      }
    } catch (SQLException e) {
      try {
        connection.rollback();
      } catch (SQLException rollbackEx) {
        throw new ServletException("Ошибка отката транзакции", rollbackEx);
      }
      throw new ServletException("Ошибка при обработке действия", e);
    } finally {
      try {
        connection.setAutoCommit(true);
      } catch (SQLException e) {
        throw new ServletException("Не удалось вернуть авто-коммит", e);
      }
    }

    // Перенаправляем пользователя обратно на страницу активности
    resp.sendRedirect("activity");
  }

  // Метод для добавления новой активности
  private void addActivity(HttpServletRequest req) throws SQLException {
    Long userId = Long.parseLong(req.getParameter("userId"));
    String description = req.getParameter("description");

    String sql = "INSERT INTO activities (user_id, description) VALUES (?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setLong(1, userId);
      stmt.setString(2, description);
      stmt.executeUpdate();
    }
  }

  // Метод для удаления активности
  private void deleteActivity(HttpServletRequest req) throws SQLException {
    Long id = Long.parseLong(req.getParameter("id"));

    String sql = "DELETE FROM activities WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setLong(1, id);
      stmt.executeUpdate();
    }
  }

  // Метод для получения списка активности
  private List<Activity> getActivities() throws SQLException {
    List<Activity> activities = new ArrayList<>();

    String sql = "SELECT id, user_id, description, activity_date FROM activities ORDER BY activity_date DESC";
    try (PreparedStatement stmt = connection.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        Activity activity = new Activity();
        activity.setId(rs.getLong("id"));
        activity.setUserId(rs.getLong("user_id"));
        activity.setDescription(rs.getString("description"));
        activity.setActivityDate(rs.getTimestamp("activity_date"));
        activities.add(activity);
      }
    }
    return activities;
  }

  @Override
  public void destroy() {
    // Закрываем соединение с базой данных при уничтожении сервлета
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
