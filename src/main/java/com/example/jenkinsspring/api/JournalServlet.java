package com.example.jenkinsspring.api;

import com.example.jenkinsspring.dao.JournalParticipantDAOImpl;
import com.example.jenkinsspring.model.JournalParticipant;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class JournalServlet extends HttpServlet {

  private JournalParticipantDAOImpl participantDAO;
  private Gson gson = new Gson();

  @Override
  public void init() throws ServletException {
    super.init();

    try {
      // Загрузка JDBC драйвера
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      throw new ServletException("PostgreSQL JDBC Driver not found", e);
    }
    try {
      Connection connection = DriverManager.getConnection(
          "jdbc:postgresql://my-postgres-db.cn4kwmqcw0p8.eu-north-1.rds.amazonaws.com:5432/postgres",
          "postgres",
          "xAMP89zuA7TkEDVLYUn2"
      );
      participantDAO = new JournalParticipantDAOImpl(connection);
    } catch (SQLException e) {
      throw new ServletException("Unable to initialize database connection", e);
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String pathInfo = req.getPathInfo();
    resp.setContentType("application/json");
    PrintWriter out = resp.getWriter();

    try {
      // Получить всех участников
      if (pathInfo == null || pathInfo.equals("/participants")) {
        List<JournalParticipant> participants = participantDAO.getAllParticipants();
        out.print(gson.toJson(participants));
      } else {
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
      }
    } catch (SQLException e) {
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
    } finally {
      out.flush();
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String pathInfo = req.getPathInfo();
    resp.setContentType("application/json");
    PrintWriter out = resp.getWriter();

    try {
      // Добавить нового участника
      if (pathInfo == null || pathInfo.equals("/participants")) {
        BufferedReader reader = req.getReader();
        JournalParticipant participant = gson.fromJson(reader, JournalParticipant.class);

        if (participant.getFirstName() == null || participant.getFirstName().isEmpty() ||
            participant.getLastName() == null || participant.getLastName().isEmpty()) {
          resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required fields: firstName and lastName");
          return;
        }

        participantDAO.addParticipant(participant);
        out.print(gson.toJson("Participant added successfully"));
      } else {
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
      }
    } catch (JsonSyntaxException e) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format");
      e.printStackTrace();
    } catch (SQLException e) {
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
      e.printStackTrace();
    } finally {
      out.flush();
    }
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String pathInfo = req.getPathInfo();
    resp.setContentType("application/json");
    PrintWriter out = resp.getWriter();

    try {
      if (pathInfo.startsWith("/participants/")) {
        int participantId = Integer.parseInt(pathInfo.split("/")[2]);
        participantDAO.deleteParticipant(participantId);
        out.print(gson.toJson("Participant deleted successfully"));
      } else {
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
      }
    } catch (SQLException e) {
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
    } finally {
      out.flush();
    }
  }
}
