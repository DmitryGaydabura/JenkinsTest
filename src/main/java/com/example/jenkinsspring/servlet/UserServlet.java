package com.example.jenkinsspring.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserServlet extends HttpServlet {
  private Connection connection;

  @Override
  public void init() throws ServletException {
    try {
      Class.forName("org.postgresql.Driver");
      connection = DriverManager.getConnection(
          "jdbc:postgresql://192.168.64.5:5432/mydatabase", "myuser", "mypassword");
    } catch (ClassNotFoundException | SQLException e) {
      throw new ServletException("Cannot connect to database", e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String action = req.getParameter("action");

    try {
      switch (action) {
        case "add":
          addUser(req, resp);
          break;
        case "update":
          updateUser(req, resp);
          break;
        case "delete":
          deleteUser(req, resp);
          break;
        default:
          resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
      }
    } catch (SQLException e) {
      throw new ServletException(e);
    }
  }

  private void addUser(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
    String username = req.getParameter("username");
    String password = req.getParameter("password");
    String email = req.getParameter("email");

    String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, username);
      stmt.setString(2, password);
      stmt.setString(3, email);
      stmt.executeUpdate();
    }
    resp.getWriter().write("Пользователь добавлен");
  }

  private void updateUser(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
    String id = req.getParameter("id");
    String username = req.getParameter("username");
    String password = req.getParameter("password");
    String email = req.getParameter("email");

    String sql = "UPDATE users SET username = ?, password = ?, email = ? WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, username);
      stmt.setString(2, password);
      stmt.setString(3, email);
      stmt.setInt(4, Integer.parseInt(id));
      stmt.executeUpdate();
    }
    resp.getWriter().write("Пользователь обновлен");
  }

  private void deleteUser(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
    String id = req.getParameter("id");

    String sql = "DELETE FROM users WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, Integer.parseInt(id));
      stmt.executeUpdate();
    }
    resp.getWriter().write("Пользователь удален");
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
