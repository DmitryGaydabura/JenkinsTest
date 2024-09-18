package com.example.jenkinsspring.controller;

import com.example.jenkinsspring.model.User;
import com.example.jenkinsspring.service.UserService;
import com.example.jenkinsspring.service.UserServiceImpl;
import com.example.jenkinsspring.dao.UserDAO;
import com.example.jenkinsspring.dao.UserDAOImpl;

import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.List;

public class UserServlet extends HttpServlet {

  private Connection connection;
  private UserService userService;

  @Override
  public void init() throws ServletException {
    try {
      Class.forName("org.postgresql.Driver");
      connection = DriverManager.getConnection(
          "jdbc:postgresql://192.168.64.5:5432/mydatabase", "myuser", "mypassword");
      connection.setAutoCommit(false); // Отключаем авто-коммит
      connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

      // Инициализируем DAO и сервис
      UserDAO userDAO = new UserDAOImpl(connection);
      userService = new UserServiceImpl(userDAO);

    } catch (ClassNotFoundException | SQLException e) {
      throw new ServletException("Не удалось подключиться к базе данных", e);
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    try {
      List<User> userList = userService.getAllUsers();
      connection.commit();

      req.setAttribute("users", userList);
      RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/users.jsp");
      dispatcher.forward(req, resp);
    } catch (SQLException e) {
      try {
        connection.rollback();
      } catch (SQLException rollbackEx) {
        throw new ServletException("Ошибка отката транзакции", rollbackEx);
      }
      throw new ServletException("Ошибка при получении пользователей", e);
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
          resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неизвестное действие");
          return;
      }
      connection.commit();
    } catch (SQLException e) {
      try {
        connection.rollback();
      } catch (SQLException rollbackEx) {
        throw new ServletException("Ошибка отката транзакции", rollbackEx);
      }
      throw new ServletException("Ошибка при обработке действия", e);
    }

    resp.sendRedirect("user");
  }

  private void handleAddUser(HttpServletRequest req) throws SQLException {

    String firstName = req.getParameter("firstName");
    String lastName = req.getParameter("lastName");
    int age = Integer.parseInt(req.getParameter("age"));

    User user = new User();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setAge(age);

    userService.addUser(user);
    req.getSession().setAttribute("message", "Пользователь успешно добавлен.");
  }

  private void handleUpdateUser(HttpServletRequest req) throws SQLException {

    long id = Long.parseLong(req.getParameter("id"));
    String firstName = req.getParameter("firstName");
    String lastName = req.getParameter("lastName");
    int age = Integer.parseInt(req.getParameter("age"));

    User user = new User();
    user.setId(id);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setAge(age);

    userService.updateUser(user);
    req.getSession().setAttribute("message", "Пользователь успешно обновлен.");
  }

  private void handleDeleteUser(HttpServletRequest req) throws SQLException {

    long id = Long.parseLong(req.getParameter("id"));
    userService.deleteUser(id);
    req.getSession().setAttribute("message", "Пользователь успешно удален.");
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
