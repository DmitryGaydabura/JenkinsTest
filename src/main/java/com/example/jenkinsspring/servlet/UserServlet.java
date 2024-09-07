package com.example.jenkinsspring.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
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

@WebServlet("/users")
public class UserServlet extends HttpServlet {
  private static final String DB_URL = "jdbc:postgresql://192.168.64.5:5432/mydatabase";
  private static final String DB_USER = "myuser";
  private static final String DB_PASSWORD = "mypassword";

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    List<User> users = new ArrayList<>();
    try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
        ResultSet resultSet = statement.executeQuery()) {

      while (resultSet.next()) {
        User user = new User();
        user.setFirstName(resultSet.getString("name"));
        user.setAge(resultSet.getInt("age"));
        user.setLastName(resultSet.getString("surname"));
        users.add(user);
      }
    } catch (SQLException e) {
      throw new ServletException("Database access error", e);
    }
    request.setAttribute("users", users);
    request.getRequestDispatcher("/WEB-INF/views/users.jsp").forward(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String action = request.getParameter("action");

    if ("add".equals(action)) {
      String name = request.getParameter("name");
      int age = Integer.parseInt(request.getParameter("age"));
      String surname = request.getParameter("surname");

      try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
          PreparedStatement statement = connection.prepareStatement("INSERT INTO users (name, age, surname) VALUES (?, ?, ?)")) {

        statement.setString(1, name);
        statement.setInt(2, age);
        statement.setString(3, surname);
        statement.executeUpdate();
      } catch (SQLException e) {
        throw new ServletException("Database access error", e);
      }
    } else if ("delete".equals(action)) {
      String name = request.getParameter("name");

      try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
          PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE name = ?")) {

        statement.setString(1, name);
        statement.executeUpdate();
      } catch (SQLException e) {
        throw new ServletException("Database access error", e);
      }
    } else if ("update".equals(action)) {
      String name = request.getParameter("name");
      int age = Integer.parseInt(request.getParameter("age"));
      String surname = request.getParameter("surname");

      try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
          PreparedStatement statement = connection.prepareStatement("UPDATE users SET age = ?, surname = ? WHERE name = ?")) {

        statement.setInt(1, age);
        statement.setString(2, surname);
        statement.setString(3, name);
        statement.executeUpdate();
      } catch (SQLException e) {
        throw new ServletException("Database access error", e);
      }
    }
    response.sendRedirect("users");
  }
}
