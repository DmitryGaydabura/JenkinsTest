package com.example.jenkinsspring.api;

import com.example.jenkinsspring.dao.JournalScoreDAOImpl;
import com.example.jenkinsspring.model.JournalScore;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddScoreServlet extends HttpServlet {

  private JournalScoreDAOImpl scoreDAO;
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
      scoreDAO = new JournalScoreDAOImpl(connection);
    } catch (SQLException e) {
      throw new ServletException("Unable to initialize database connection", e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("application/json");
    PrintWriter out = resp.getWriter();
    String pathInfo = req.getPathInfo();

    try {
      // Извлечение participantId из URL
      if (pathInfo != null && pathInfo.startsWith("/participants/") && pathInfo.endsWith("/score")) {
        String[] parts = pathInfo.split("/");
        if (parts.length != 4) {
          resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL format");
          return;
        }

        int participantId;
        try {
          participantId = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
          resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid participant ID");
          return;
        }

        // Чтение тела запроса для получения данных оценки
        BufferedReader reader = req.getReader();
        JournalScore score = gson.fromJson(reader, JournalScore.class);

        if (score.getDate() == null) {
          resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required field: date");
          return;
        }

        score.setParticipantId(participantId);

        // Добавление или обновление оценки
        scoreDAO.addOrUpdateScore(score);
        out.print(gson.toJson("Score added/updated successfully"));
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
}

