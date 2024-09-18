package com.example.jenkinsspring.servlet;

import jakarta.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ActivityServlet extends HttpServlet {
  private Connection connection;

  @Override
  public void init() throws ServletException {
    try {
      // Инициализация драйвера и соединения с базой данных
      Class.forName("org.postgresql.Driver");
      connection = DriverManager.getConnection(
          "jdbc:postgresql://192.168.64.5:5432/mydatabase", "myuser", "mypassword");
      connection.setAutoCommit(false); // Отключаем авто-коммит

      // Устанавливаем уровень изоляции транзакции здесь
      connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

    } catch (ClassNotFoundException | SQLException e) {
      throw new ServletException("Не удалось подключиться к базе данных", e);
    }
  }


  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      // Извлекаем сообщение из сессии, если оно есть
      String message = (String) req.getSession().getAttribute("message");
      if (message != null) {
        req.setAttribute("message", message);
        req.getSession().removeAttribute("message");
      }

      // Извлекаем сообщение об ошибке из сессии, если оно есть
      String errorMessage = (String) req.getSession().getAttribute("errorMessage");
      if (errorMessage != null) {
        req.setAttribute("errorMessage", errorMessage);
        req.getSession().removeAttribute("errorMessage");
      }

      List<Activity> activities = getActivities();
      connection.commit();

      // Передаем список активностей в JSP
      req.setAttribute("activities", activities);
      RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/activities.jsp");
      dispatcher.forward(req, resp);
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
    String action = req.getParameter("action");

    try {
      switch (action) {
        case "add":
          // Удаляем вызов setTransactionIsolation
          // connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
          addActivity(req);
          connection.commit();
          req.getSession().setAttribute("message", "Активность успешно добавлена.");
          break;
        case "delete":
          // Удаляем вызов setTransactionIsolation
          // connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
          deleteActivity(req);
          connection.commit();
          req.getSession().setAttribute("message", "Активность успешно удалена.");
          break;
        case "sendReport":
          generateAndSendReport(req);
          req.getSession().setAttribute("message", "Отчет успешно отправлен на вашу почту.");
          break;
        default:
          resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неизвестное действие");
          return;
      }
    } catch (SQLException | MessagingException | UserNotFoundException e) {
      try {
        connection.rollback();
      } catch (SQLException rollbackEx) {
        throw new ServletException("Ошибка отката транзакции", rollbackEx);
      }
      throw new ServletException("Ошибка при обработке действия", e);
    }

    // Перенаправляем пользователя обратно на страницу активности
    resp.sendRedirect("activity");
  }



  // Метод для добавления новой активности
  private void addActivity(HttpServletRequest req) throws SQLException, UserNotFoundException {
    Long userId;
    try {
      userId = Long.parseLong(req.getParameter("userId"));
    } catch (NumberFormatException e) {
      throw new UserNotFoundException("Некорректный ID пользователя.");
    }
    String description = req.getParameter("description");

    // Проверяем, существует ли пользователь
    String checkUserSql = "SELECT COUNT(*) FROM users WHERE id = ?";
    try (PreparedStatement checkUserStmt = connection.prepareStatement(checkUserSql)) {
      checkUserStmt.setLong(1, userId);
      try (ResultSet rs = checkUserStmt.executeQuery()) {
        if (rs.next() && rs.getInt(1) == 0) {
          throw new UserNotFoundException("Пользователь с ID " + userId + " не существует.");
        }
      }
    }

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

  // Метод для получения списка всех активностей
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

  // Метод для генерации и отправки отчета
  private void generateAndSendReport(HttpServletRequest req) throws SQLException, MessagingException {
    try {
      // Получаем все активности
      List<Activity> activities = getActivities();

      // Указываем путь для сохранения отчета
      String reportsDirPath = getServletContext().getRealPath("/WEB-INF/reports/");
      File reportsDir = new File(reportsDirPath);
      if (!reportsDir.exists()) {
        reportsDir.mkdirs();
      }

      String filePath = reportsDirPath + "activity_report.pdf";

      // Генерируем PDF отчет
      ReportGenerator.generateDailyActivityReport(activities, filePath);

      // Отправляем отчет по электронной почте
      String toEmail = "gaydabura.d@icloud.com"; // Укажите ваш адрес электронной почты
      String subject = "Отчет о всех активностях";
      String body = "Здравствуйте,\n\nПрикреплен отчет о всех активностях.\n\nС уважением,\nВаша команда";

      EmailSender.sendEmailWithAttachment(toEmail, subject, body, filePath);

      // Удаляем отчет после отправки
      File reportFile = new File(filePath);
      if (reportFile.exists()) {
        reportFile.delete();
      }

    } catch (IOException e) {
      throw new SQLException("Ошибка при генерации отчета", e);
    }
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
