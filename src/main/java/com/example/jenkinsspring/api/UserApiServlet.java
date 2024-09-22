package com.example.jenkinsspring.api;

import com.example.jenkinsspring.model.User;
import com.example.jenkinsspring.service.UserService;
import com.example.jenkinsspring.service.UserServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class UserApiServlet extends HttpServlet {

  private UserService userService;
  private final Gson gson = new Gson();

  @Override
  public void init() throws ServletException {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      throw new ServletException("PostgreSQL JDBC Driver not found", e);
    }

    try {
      // Инициализация соединения с базой данных
      Connection connection = DriverManager.getConnection(
          "jdbc:postgresql://my-postgres-db.cn4kwmqcw0p8.eu-north-1.rds.amazonaws.com:5432/postgres",
          "postgres",
          "xAMP89zuA7TkEDVLYUn2"
      );
      connection.setAutoCommit(false);
      connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

      userService = new UserServiceImpl(new com.example.jenkinsspring.dao.UserDAOImpl(connection));
    } catch (SQLException e) {
      e.printStackTrace();
      throw new ServletException("Failed to initialize UserService", e);
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // Обработка запроса на получение всех пользователей
    try {
      List<User> users = userService.getAllUsers();
      String json = gson.toJson(users);

      resp.setContentType("application/json");
      resp.setCharacterEncoding("UTF-8");
      resp.getWriter().write(json);
    } catch (SQLException e) {
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving users");
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // Обработка запроса на добавление нового пользователя
    try {
      BufferedReader reader = req.getReader();
      User user = gson.fromJson(reader, User.class);

      if (user.getFirstName() == null || user.getLastName() == null || user.getAge() <= 0) {
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or invalid required fields");
        return;
      }

      userService.addUser(user);
      connectionCommit();

      String json = gson.toJson(user);
      resp.setContentType("application/json");
      resp.setCharacterEncoding("UTF-8");
      resp.setStatus(HttpServletResponse.SC_CREATED);
      resp.getWriter().write(json);
    } catch (JsonSyntaxException e) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format");
    } catch (SQLException e) {
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error adding user");
    }
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // Обработка запроса на обновление пользователя
    String pathInfo = req.getPathInfo(); // Ожидается /{id}
    if (pathInfo == null || pathInfo.equals("/")) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "User ID is missing");
      return;
    }

    String idStr = pathInfo.substring(1); // Удаляем первый символ '/'
    Long id;
    try {
      id = Long.parseLong(idStr);
    } catch (NumberFormatException e) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid User ID");
      return;
    }

    try {
      BufferedReader reader = req.getReader();
      User user = gson.fromJson(reader, User.class);
      user.setId(id);

      userService.updateUser(user);
      connectionCommit();

      String json = gson.toJson(user);
      resp.setContentType("application/json");
      resp.setCharacterEncoding("UTF-8");
      resp.getWriter().write(json);
    } catch (JsonSyntaxException e) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format");
    } catch (SQLException e) {
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating user");
    }
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // Обработка запроса на удаление пользователя
    String pathInfo = req.getPathInfo(); // Ожидается /{id}
    if (pathInfo == null || pathInfo.equals("/")) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "User ID is missing");
      return;
    }

    String idStr = pathInfo.substring(1); // Удаляем первый символ '/'
    Long id;
    try {
      id = Long.parseLong(idStr);
    } catch (NumberFormatException e) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid User ID");
      return;
    }

    try {
      userService.deleteUser(id);
      connectionCommit();
      resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    } catch (SQLException e) {
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting user");
    }
  }

  @Override
  public void destroy() {
    // Закрытие соединения с БД при уничтожении сервлета
    try {
      if (userService instanceof UserServiceImpl) {
        ((UserServiceImpl) userService).closeConnection();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void connectionCommit() throws SQLException {
    if (userService instanceof UserServiceImpl) {
      ((UserServiceImpl) userService).commitConnection();
    }
  }
}
