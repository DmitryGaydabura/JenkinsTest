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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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

  // Telegram Configuration
  private String telegramBotToken;
  private String telegramBotUsername;
  private String telegramChatId;

  // Task Scheduler
  private ScheduledExecutorService scheduler;

  @Override
  public void init() throws ServletException {
    try {
      // Initialize driver and database connection
      Class.forName("org.postgresql.Driver");
      connection = DriverManager.getConnection(
          "my-postgres-db.cn4kwmqcw0p8.eu-north-1.rds.amazonaws.com:5432/postgres", "postgres", "xAMP89zuA7TkEDVLYUn2");
      connection.setAutoCommit(false);
      connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

      // Initialize DAO and Service
      ActivityDAO activityDAO = new ActivityDAOImpl(connection);
      activityService = new ActivityServiceImpl(activityDAO);

      // Retrieve Telegram configuration from environment variables
      telegramBotToken = "6516869813:AAF_VFQgr500uGSx2bKxC_Ij6_xH5ToZSZ0";
      telegramBotUsername = "testLab1011_bot";
      telegramChatId = "387753803";

      if (telegramBotToken == null || telegramBotUsername == null || telegramChatId == null) {
        throw new ServletException(
            "Telegram configuration is missing. Please set TELEGRAM_BOT_TOKEN, TELEGRAM_BOT_USERNAME, and TELEGRAM_CHAT_ID environment variables.");
      }

      // Initialize Task Scheduler
      scheduler = Executors.newSingleThreadScheduledExecutor();
      scheduleDailyReport();

    } catch (ClassNotFoundException | SQLException e) {
      throw new ServletException("Failed to connect to the database", e);
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    try {
      // Retrieve messages from session
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
        throw new ServletException("Transaction rollback error", ex);
      }
      throw new ServletException("Error retrieving activities", e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
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
          resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
          return;
      }
      connection.commit();
    } catch (UserNotFoundException e) {
      try {
        connection.rollback();
      } catch (SQLException ex) {
        throw new ServletException("Transaction rollback error", ex);
      }
      req.getSession().setAttribute("errorMessage", e.getMessage());
    } catch (SQLException | MessagingException | TelegramApiException e) {
      try {
        connection.rollback();
      } catch (SQLException ex) {
        throw new ServletException("Transaction rollback error", ex);
      }
      throw new ServletException("Error processing action", e);
    }

    resp.sendRedirect("activity");
  }

  private void handleAddActivity(HttpServletRequest req)
      throws SQLException, UserNotFoundException {
    Long userId;
    try {
      userId = Long.parseLong(req.getParameter("userId"));
    } catch (NumberFormatException e) {
      throw new UserNotFoundException("Invalid user ID.");
    }
    String description = req.getParameter("description");

    Activity activity = new Activity();
    activity.setUserId(userId);
    activity.setDescription(description);

    activityService.addActivity(activity);
    req.getSession().setAttribute("message", "Activity successfully added.");
  }

  private void handleDeleteActivity(HttpServletRequest req) throws SQLException {
    Long id = Long.parseLong(req.getParameter("id"));
    activityService.deleteActivity(id);
    req.getSession().setAttribute("message", "Activity successfully deleted.");
  }

  private void handleSendReport(HttpServletRequest req)
      throws SQLException, MessagingException, IOException, TelegramApiException {
    handleSendReportInternal();
    req.getSession().setAttribute("message", "Great! Report was sent to your email and Telegram.");
  }

  private void handleSendReportInternal()
      throws SQLException, MessagingException, IOException, TelegramApiException {
    List<Activity> activities = activityService.getAllActivities();

    // Specify the path to save the report
    String reportsDirPath = getServletContext().getRealPath("/WEB-INF/reports/");
    File reportsDir = new File(reportsDirPath);
    if (!reportsDir.exists()) {
      reportsDir.mkdirs();
    }

    String filePath = reportsDirPath + "activity_report.pdf";

    // Generate PDF report
    ReportGenerator.generateActivityReport(activities, filePath);

    // Send report via email
    String toEmail = "gaydabura.d@icloud.com";
    String subject = "User Activity Report";
    String body = "Hi,\n\nActivity report is attached below.\n\nBest regards";

    EmailSender.sendEmailWithAttachment(toEmail, subject, body, filePath);

    // Send report via Telegram
    sendReportToTelegram(filePath);

    // Delete the report after sending
    File reportFile = new File(filePath);
    if (reportFile.exists()) {
      reportFile.delete();
    }
  }

  private void sendReportToTelegram(String filePath) throws TelegramApiException {
    TelegramSender telegramSender = new TelegramSender(telegramBotToken, telegramBotUsername);

    // Register the bot
    TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
    try {
      botsApi.registerBot(telegramSender);
    } catch (TelegramApiException e) {
      throw e;
    }

    File reportFile = new File(filePath);
    String caption = "User Activity Report";

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

    // Shutdown the scheduler
    if (scheduler != null && !scheduler.isShutdown()) {
      scheduler.shutdown();
      try {
        if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
          scheduler.shutdownNow();
        }
      } catch (InterruptedException e) {
        scheduler.shutdownNow();
        Thread.currentThread().interrupt();
      }
    }
  }

  /**
   * Schedules the daily report to be sent at 21:00
   */
  private void scheduleDailyReport() {
    // Current time
    LocalDateTime now = LocalDateTime.now();

    // Next run time at 21:00
    LocalDateTime nextRun = now.withHour(21).withMinute(0).withSecond(0).withNano(0);
    if (now.compareTo(nextRun) >= 0) {
      nextRun = nextRun.plusDays(1);
    }

    // Calculate initial delay in seconds until next run
    long initialDelay = Duration.between(now, nextRun).getSeconds();

    // Period of execution (1 day in seconds)
    long period = TimeUnit.DAYS.toSeconds(1);

    scheduler.scheduleAtFixedRate(() -> {
      try {
        System.out.println("Initiating automatic report sending: " + LocalDateTime.now());
        handleSendReportInternal();
        System.out.println("Report successfully sent automatically.");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }, initialDelay, period, TimeUnit.SECONDS);

    System.out.println("Report scheduler started. Next run at: " + nextRun);
  }
}
