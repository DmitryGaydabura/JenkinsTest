package com.example.jenkinsspring.api;

import com.example.jenkinsspring.exception.UserNotFoundException;
import com.example.jenkinsspring.model.Activity;
import com.example.jenkinsspring.service.ActivityService;
import com.example.jenkinsspring.service.ActivityServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class ActivityApiServlet extends HttpServlet {

  private ActivityService activityService;
  private Gson gson = new Gson();

  @Override
  public void init() throws ServletException {
    try {
      // Инициализация соединения с базой данных
      Connection connection = DriverManager.getConnection(
          "jdbc:postgresql://my-postgres-db.cn4kwmqcw0p8.eu-north-1.rds.amazonaws.com:5432/postgres",
          "postgres",
          "xAMP89zuA7TkEDVLYUn2"
      );
      connection.setAutoCommit(false);
      connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

      activityService = new ActivityServiceImpl(new com.example.jenkinsspring.dao.ActivityDAOImpl(connection));
    } catch (SQLException e) {
      e.printStackTrace();
      throw new ServletException("Failed to initialize ActivityService", e);
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // Обработка запроса на получение всех активностей
    try {
      List<Activity> activities = activityService.getAllActivities();
      String json = gson.toJson(activities);

      resp.setContentType("application/json");
      resp.setCharacterEncoding("UTF-8");
      resp.getWriter().write(json);
    } catch (SQLException e) {
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving activities");
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // Обработка запроса на добавление новой активности
    try {
      BufferedReader reader = req.getReader();
      Activity activity = gson.fromJson(reader, Activity.class);

      if (activity.getUserId() == null || activity.getDescription() == null || activity.getDescription().isEmpty()) {
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required fields");
        return;
      }

      activityService.addActivity(activity);
      connectionCommit();

      String json = gson.toJson(activity);
      resp.setContentType("application/json");
      resp.setCharacterEncoding("UTF-8");
      resp.setStatus(HttpServletResponse.SC_CREATED);
      resp.getWriter().write(json);
    } catch (JsonSyntaxException e) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format");
    } catch (SQLException e) {
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error adding activity");
    } catch (UserNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // Обработка запроса на обновление активности
    String pathInfo = req.getPathInfo(); // Ожидается /{id}
    if (pathInfo == null || pathInfo.equals("/")) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Activity ID is missing");
      return;
    }

    String idStr = pathInfo.substring(1); // Удаляем первый символ '/'
    Long id;
    try {
      id = Long.parseLong(idStr);
    } catch (NumberFormatException e) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Activity ID");
      return;
    }

    try {
      BufferedReader reader = req.getReader();
      Activity activity = gson.fromJson(reader, Activity.class);
      activity.setId(id);

      activityService.updateActivity(activity);
      connectionCommit();

      String json = gson.toJson(activity);
      resp.setContentType("application/json");
      resp.setCharacterEncoding("UTF-8");
      resp.getWriter().write(json);
    } catch (JsonSyntaxException e) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format");
    } catch (SQLException e) {
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating activity");
    }
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // Обработка запроса на удаление активности
    String pathInfo = req.getPathInfo(); // Ожидается /{id}
    if (pathInfo == null || pathInfo.equals("/")) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Activity ID is missing");
      return;
    }

    String idStr = pathInfo.substring(1); // Удаляем первый символ '/'
    Long id;
    try {
      id = Long.parseLong(idStr);
    } catch (NumberFormatException e) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Activity ID");
      return;
    }

    try {
      activityService.deleteActivity(id);
      connectionCommit();
      resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    } catch (SQLException e) {
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting activity");
    }
  }

  @Override
  public void destroy() {
    // Закрытие соединения с БД при уничтожении сервлета
    try {
      if (activityService instanceof ActivityServiceImpl) {
        ((ActivityServiceImpl) activityService).closeConnection();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void connectionCommit() throws SQLException {
    if (activityService instanceof ActivityServiceImpl) {
      ((ActivityServiceImpl) activityService).commitConnection();
    }
  }
}
