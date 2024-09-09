package com.example.jenkinsspring.servlet;

import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserServlet extends HttpServlet {

  private Connection connection;

  @Override
  public void init() throws ServletException {
    try {
      Class.forName("org.postgresql.Driver");
      connection = DriverManager.getConnection(
          "jdbc:postgresql://192.168.64.5:5432/mydatabase", "myuser", "mypassword");
      connection.setAutoCommit(false); // Disable auto-commit
    } catch (ClassNotFoundException | SQLException e) {
      throw new ServletException("Cannot connect to database", e);
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    try {
      connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); // Чтение пользователей
      displayUsers(req, resp); // Display the user list when GET request is made
      connection.commit();
    } catch (SQLException e) {
      try {
        connection.rollback();
      } catch (SQLException rollbackEx) {
        throw new ServletException("Rollback failed", rollbackEx);
      }
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String action = req.getParameter("action");

    try {
      switch (action) {
        case "add":
          connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); // Добавление пользователя
          addUser(req);
          break;
        case "update":
          connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ); // Изменение пользователя
          updateUser(req);
          break;
        case "delete":
          connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); // Удаление пользователя
          deleteUser(req);
          break;
        default:
          resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
      }
      connection.commit(); // Commit the transaction if no errors
    } catch (SQLException e) {
      try {
        connection.rollback(); // Rollback the transaction on error
      } catch (SQLException rollbackEx) {
        throw new ServletException("Rollback failed", rollbackEx);
      }
      throw new ServletException(e);
    }
    displayUsers(req, resp); // Redisplay users after action
  }

  private void addUser(HttpServletRequest req) throws SQLException {

    String firstName = req.getParameter("firstName");
    String lastName = req.getParameter("lastName");
    int age = Integer.parseInt(req.getParameter("age"));

    String sql = "INSERT INTO users (first_name, last_name, age) VALUES (?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, firstName);
      stmt.setString(2, lastName);
      stmt.setInt(3, age);
      stmt.executeUpdate();
    }
  }

  private void updateUser(HttpServletRequest req) throws SQLException {

    long id = Long.parseLong(req.getParameter("id"));
    String firstName = req.getParameter("firstName");
    String lastName = req.getParameter("lastName");
    int age = Integer.parseInt(req.getParameter("age"));

    String sql = "UPDATE users SET first_name = ?, last_name = ?, age = ? WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, firstName);
      stmt.setString(2, lastName);
      stmt.setInt(3, age);
      stmt.setLong(4, id);
      stmt.executeUpdate();
    }
  }

  private void deleteUser(HttpServletRequest req) throws SQLException {

    long id = Long.parseLong(req.getParameter("id"));

    String sql = "DELETE FROM users WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setLong(1, id);
      stmt.executeUpdate();
    }
  }

  private void displayUsers(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    List<User> userList = new ArrayList<>();

    String sql = "SELECT id, first_name, last_name, age FROM users";
    try (PreparedStatement stmt = connection.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setAge(rs.getInt("age"));
        userList.add(user);
      }
    } catch (SQLException e) {
      throw new ServletException(e);
    }
    req.setAttribute("users", userList);
    RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/users.jsp");
    dispatcher.forward(req, resp);
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
