package com.example.jenkinsspring.controller;

import com.example.jenkinsspring.dao.UserDAO;
import com.example.jenkinsspring.dao.UserDAOImpl;
import com.example.jenkinsspring.exception.UserNotFoundException;
import com.example.jenkinsspring.model.User;
import com.example.jenkinsspring.service.UserService;
import com.example.jenkinsspring.service.UserServiceImpl;
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

public class UserServlet extends HttpServlet {

  private Connection connection;
  private UserService userService;

  @Override
  public void init() throws ServletException {
    try {
      // Initialize the PostgreSQL driver and establish a database connection
      Class.forName("org.postgresql.Driver");
      connection = DriverManager.getConnection(
          "jdbc:postgresql://my-postgres-db.cn4kwmqcw0p8.eu-north-1.rds.amazonaws.com:5432/postgres",
          "postgres",
          "xAMP89zuA7TkEDVLYUn2"
      );

      connection.setAutoCommit(false); // Disable auto-commit
      connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

      // Initialize DAO and Service
      UserDAO userDAO = new UserDAOImpl(connection);
      userService = new UserServiceImpl(userDAO);

    } catch (ClassNotFoundException | SQLException e) {
      throw new ServletException("Failed to connect to the database", e);
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    try {
      // Retrieve messages from the session
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

      List<User> userList = userService.getAllUsers();
      connection.commit();

      req.setAttribute("users", userList);
      RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/users.jsp");
      dispatcher.forward(req, resp);
    } catch (SQLException e) {
      try {
        connection.rollback();
      } catch (SQLException ex) {
        throw new ServletException("Transaction rollback error", ex);
      }
      throw new ServletException("Error retrieving users", e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String action = req.getParameter("action");

    try {
      switch (action) {
        case "add":
          handleAddUser(req);
          break;
        case "update":
          handleUpdateUser(req);
          break;
        case "delete":
          handleDeleteUser(req);
          break;
        default:
          resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
          return;
      }
      connection.commit();
    } catch (SQLException e) {
      try {
        connection.rollback();
      } catch (SQLException ex) {
        throw new ServletException("Transaction rollback error", ex);
      }
      throw new ServletException("Error processing action", e);
    } catch (UserNotFoundException e) {
      try {
        connection.rollback();
      } catch (SQLException ex) {
        throw new ServletException("Transaction rollback error", ex);
      }
      req.getSession().setAttribute("errorMessage", e.getMessage());
    }

    resp.sendRedirect("user");
  }

  private void handleAddUser(HttpServletRequest req) throws SQLException, UserNotFoundException {
    String firstName = req.getParameter("firstName");
    String lastName = req.getParameter("lastName");
    int age;
    try {
      age = Integer.parseInt(req.getParameter("age"));
    } catch (NumberFormatException e) {
      throw new UserNotFoundException("Invalid age provided.");
    }

    User user = new User();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setAge(age);

    userService.addUser(user);
    req.getSession().setAttribute("message", "User successfully added.");
  }

  private void handleUpdateUser(HttpServletRequest req) throws SQLException {
    long id;
    try {
      id = Long.parseLong(req.getParameter("id"));
    } catch (NumberFormatException e) {
      throw new SQLException("Invalid user ID.");
    }
    String firstName = req.getParameter("firstName");
    String lastName = req.getParameter("lastName");
    int age;
    try {
      age = Integer.parseInt(req.getParameter("age"));
    } catch (NumberFormatException e) {
      throw new SQLException("Invalid age provided.");
    }

    User user = new User();
    user.setId(id);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setAge(age);

    userService.updateUser(user);
    req.getSession().setAttribute("message", "User successfully updated.");
  }

  private void handleDeleteUser(HttpServletRequest req) throws SQLException {
    long id;
    try {
      id = Long.parseLong(req.getParameter("id"));
    } catch (NumberFormatException e) {
      throw new SQLException("Invalid user ID.");
    }
    userService.deleteUser(id);
    req.getSession().setAttribute("message", "User successfully deleted.");
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
