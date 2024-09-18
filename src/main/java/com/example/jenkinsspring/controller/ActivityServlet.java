package com.example.jenkinsspring.controller;

import com.example.jenkinsspring.dao.ActivityDAO;
import com.example.jenkinsspring.dao.ActivityDAOImpl;
import com.example.jenkinsspring.exception.UserNotFoundException;
import com.example.jenkinsspring.model.Activity;
import com.example.jenkinsspring.service.ActivityService;
import com.example.jenkinsspring.service.ActivityServiceImpl;
import com.example.jenkinsspring.util.EmailSender;
import com.example.jenkinsspring.util.ReportGenerator;
import jakarta.mail.MessagingException;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.List;

public class ActivityServlet extends HttpServlet {
  private Connection connection;
  private ActivityService activityService;

  @Override
  public void init() throws ServletException {
    try {
      // Инициализация драйвера и соединения с базой данных
      Class.forName("org.postgresql.Driver");
      connection = DriverManager.getConnection(
          "jdbc:postgresql://192.168.64.5:5432/mydatabase", "myuser", "mypassword");
      connection.setAutoCommit(false);
      connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

      // Инициализация DAO и сервиса
      ActivityDAO activityDAO = new ActivityDAOImpl(connection);
      activityService = new ActivityServiceImpl(activityDAO);

    } catch (ClassNotFoundException | SQLException e) {
      throw new ServletException("Не удалось подключиться к базе данных", e);
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      // Извлекаем сообщения из сессии
      String message = (String) req.getSession().getAttribute("message");
      String errorMessage = (String) req.getSession().getAttribute("errorMessage");

      if (message != null) {
        req.setAttribute("message", message);
        req.getSession().removeAttribute("message");
      }

      if (errorMessage != null) {
        req.setAttribute("errorMessage", errorMessage);
        req.getSession().removeAttribute("errorMessage");
      }

      List<Activity> activities = activityService.getAllActivities();
      connection.commit();

      req.setAttribute("activities", activities);
      RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/activities.jsp");
      dispatcher.forward(req, resp);
    } catch (SQLException e) {
      try {
        connection.rollback();
      } catch (SQLException ex) {
        throw new ServletException("Ошибка отката транзакции", ex);
      }
      throw new ServletException("Ошибка при получении активностей", e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String action = req.getParameter("action");

    try {
      switch (action) {
        case "add":
          handleAddActivity(req);
          break;
        case "delete":
          handleDeleteActivity(req);
          break;
        case "sendReport":
          handleSendReport(req);
          break;
        default:
          resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неизвестное действие");
          return;
      }
      connection.commit();
    } catch (UserNotFoundException e) {
      try {
        connection.rollback();
      } catch (SQLException ex) {
        throw new ServletException("Ошибка отката транзакции", ex);
      }
      req.getSession().setAttribute("errorMessage", e.getMessage());
    } catch (SQLException | MessagingException e) {
      try {
        connection.rollback();
      } catch (SQLException ex) {
        throw new ServletException("Ошибка отката транзакции", ex);
      }
      throw new ServletException("Ошибка при обработке действия", e);
    }

    resp.sendRedirect("activity");
  }

  private void handleAddActivity(HttpServletRequest req) throws SQLException, UserNotFoundException {
    Long userId;
    try {
      userId = Long.parseLong(req.getParameter("userId"));
    } catch (NumberFormatException e) {
      throw new UserNotFoundException("Некорректный ID пользователя.");
    }
    String description = req.getParameter("description");

    Activity activity = new Activity();
    activity.setUserId(userId);
    activity.setDescription(description);

    activityService.addActivity(activity);
    req.getSession().setAttribute("message", "Активность успешно добавлена.");
  }

  private void handleDeleteActivity(HttpServletRequest req) throws SQLException {
    Long id = Long.parseLong(req.getParameter("id"));
    activityService.deleteActivity(id);
    req.getSession().setAttribute("message", "Активность успешно удалена.");
  }

  private void handleSendReport(HttpServletRequest req) throws SQLException, MessagingException, IOException {
    List<Activity> activities = activityService.getAllActivities();

    // Путь для сохранения отчета
    String reportsDirPath = getServletContext().getRealPath("/WEB-INF/reports/");
    File reportsDir = new File(reportsDirPath);
    if (!reportsDir.exists()) {
      reportsDir.mkdirs();
    }
    String filePath = reportsDirPath + "activity_report.pdf";

    // Генерация отчета
    ReportGenerator.generateActivityReport(activities, filePath);

    // Отправка отчета по почте
    String toEmail = "your_email@example.com"; // Замените на ваш email
    String subject = "Отчет о активности пользователей";
    String body = "Здравствуйте,\n\nОтчет о активности пользователей во вложении.\n\nС уважением,\nКоманда";

    EmailSender.sendEmailWithAttachment(toEmail, subject, body, filePath);

    // Удаление файла отчета
    File reportFile = new File(filePath);
    if (reportFile.exists()) {
      reportFile.delete();
    }

    req.getSession().setAttribute("message", "Отчет успешно отправлен на вашу почту.");
  }

  @Override
  public void destroy() {
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
