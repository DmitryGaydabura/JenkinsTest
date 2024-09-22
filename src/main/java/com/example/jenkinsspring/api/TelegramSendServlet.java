package com.example.jenkinsspring.api;

import com.example.jenkinsspring.model.Activity;
import com.example.jenkinsspring.service.ActivityService;
import com.example.jenkinsspring.service.ActivityServiceImpl;
import com.example.jenkinsspring.util.ReportGenerator;
import com.example.jenkinsspring.util.TelegramSender;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramSendServlet extends HttpServlet {

  private TelegramSender telegramSender;
  private ActivityService activityService;
  private Gson gson = new Gson();

  // Путь для временного хранения PDF файла
  private static final String TEMP_PDF_PATH = "/tmp/activity_report.pdf";

  @Override
  public void init() throws ServletException {
    super.init();
    // Инициализация TelegramSender с токеном и именем бота
    String botToken = "6516869813:AAF_VFQgr500uGSx2bKxC_Ij6_xH5ToZSZ0"; // Ваш токен
    String botUsername = "testLab1011_bot"; // Имя вашего бота
    telegramSender = new TelegramSender(botToken, botUsername);
    try {
      telegramSender.initialize(); // Инициализация бота
    } catch (TelegramApiException e) {
      e.printStackTrace();
      throw new ServletException("Failed to initialize Telegram Sender", e);
    }

    try {
      // Создание соединения с базой данных
      Connection connection = DriverManager.getConnection(
          "jdbc:postgresql://my-postgres-db.cn4kwmqcw0p8.eu-north-1.rds.amazonaws.com:5432/postgres",
          "postgres",
          "xAMP89zuA7TkEDVLYUn2"
      );

      // Передача соединения в ActivityDAOImpl
      activityService = new ActivityServiceImpl(new com.example.jenkinsspring.dao.ActivityDAOImpl(connection));
    } catch (SQLException e) {
      e.printStackTrace();
      throw new ServletException("Failed to initialize ActivityService", e);
    }
  }



  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    try {
      BufferedReader reader = req.getReader();
      TelegramReportRequest reportRequest = gson.fromJson(reader, TelegramReportRequest.class);

      if (reportRequest.getChatId() == null) {
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required field: chatId");
        return;
      }

      // Получение списка активностей
      List<Activity> activities = activityService.getAllActivities();

      // Генерация PDF отчёта
      ReportGenerator.generateActivityReport(activities, TEMP_PDF_PATH);

      // Проверка, что PDF файл создан
      File pdfFile = new File(TEMP_PDF_PATH);
      if (!pdfFile.exists()) {
        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to generate PDF report");
        return;
      }

      // Отправка PDF отчёта в Telegram
      telegramSender.sendDocument(String.valueOf(reportRequest.getChatId()), pdfFile, "Список активностей");

      // Отправка успешного ответа
      resp.setContentType("application/json");
      resp.setCharacterEncoding("UTF-8");
      PrintWriter out = resp.getWriter();
      out.print(gson.toJson(new ApiResponse("Report sent successfully")));
      out.flush();
    } catch (JsonSyntaxException e) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format");
    } catch (SQLException e) {
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving activities");
    } catch (TelegramApiException e) {
      e.printStackTrace();
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to send report to Telegram");
    }
  }

  @Override
  public void destroy() {
    super.destroy();
    // Остановка Telegram бота
    try {
      telegramSender.stop();
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }

    // Закрытие соединения с БД при уничтожении сервлета
    try {
      if (activityService instanceof ActivityServiceImpl) {
        ((ActivityServiceImpl) activityService).closeConnection();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // Внутренний класс для запроса
  private class TelegramReportRequest {
    private Long chatId;

    public Long getChatId() {
      return chatId;
    }
  }

  // Внутренний класс для ответа
  private class ApiResponse {
    private String message;

    public ApiResponse(String message) {
      this.message = message;
    }

    public String getMessage() {
      return message;
    }
  }
}
