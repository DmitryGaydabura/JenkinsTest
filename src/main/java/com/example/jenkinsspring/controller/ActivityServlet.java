package com.example.jenkinsspring.controller;

import com.example.jenkinsspring.dao.ActivityDAO;
import com.example.jenkinsspring.dao.ActivityDAOImpl;
import com.example.jenkinsspring.exception.UserNotFoundException;
import com.example.jenkinsspring.model.Activity;
import com.example.jenkinsspring.service.ActivityService;
import com.example.jenkinsspring.service.ActivityServiceImpl;
import com.example.jenkinsspring.util.EmailSender;
import com.example.jenkinsspring.util.ReportGenerator;
import com.example.jenkinsspring.util.TelegramSender;
import jakarta.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class ActivityServlet extends HttpServlet {
  private Connection connection;
  private ActivityService activityService;

  // Конфигурация Telegram
  private String telegramBotToken;
  private String telegramBotUsername;
  private String telegramChatId;

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

      // Получение конфигурации Telegram из переменных окружения
      telegramBotToken = System.getenv("6516869813:AAF_VFQgr500uGSx2bKxC_Ij6_xH5ToZSZ0");
      telegramBotUsername = System.getenv("testLab1011_bot");
      telegramChatId = System.getenv("387753803");

      if (telegramBotToken == null || telegramBotUsername == null || telegramChatId == null) {
        throw new ServletException("Telegram configuration is missing. Please set TELEGRAM_BOT_TOKEN, TELEGRAM_BOT_USERNAME, and TELEGRAM_CHAT_ID environment variables.");
      }

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
    } catch (SQLException | MessagingException | TelegramApiException e) {
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

  private void handleSendReport(HttpServletRequest req) throws SQLException, MessagingException, IOException, TelegramApiException {
    List<Activity> activities = activityService.getAllActivities();

    // Указываем путь для сохранения отчета
    String reportsDirPath = getServletContext().getRealPath("/WEB-INF/reports/");
    File reportsDir = new File(reportsDirPath);
    if (!reportsDir.exists()) {
      reportsDir.mkdirs();
    }

    String filePath = reportsDirPath + "activity_report.pdf";

    // Генерируем PDF отчет
    ReportGenerator.generateActivityReport(activities, filePath);

    // Отправляем отчет по электронной почте
    String toEmail = "gaydabura.d@icloud.com"; // Укажите ваш адрес электронной почты
    String subject = "User Activity Report";
    String body = "Hi,\n\nActivity report is attached below.\n\nBest regards";

    EmailSender.sendEmailWithAttachment(toEmail, subject, body, filePath);

    // Отправляем отчет в Telegram
    sendReportToTelegram(filePath);

    // Удаляем отчет после отправки
    File reportFile = new File(filePath);
    if (reportFile.exists()) {
      reportFile.delete();
    }

    req.getSession().setAttribute("message", "Great! Report was sent to your email and Telegram.");
  }

  private void sendReportToTelegram(String filePath) throws TelegramApiException {
    TelegramSender telegramSender = new TelegramSender(telegramBotToken, telegramBotUsername);

    // Регистрируем бота
    TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
    try {
      botsApi.registerBot(telegramSender);
    } catch (TelegramApiException e) {
      throw e; // Передаем дальше для обработки в вызывающем методе
    }

    File reportFile = new File(filePath);
    String caption = "Отчет о активности пользователей";

    telegramSender.sendDocument(telegramChatId, reportFile, caption);
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
