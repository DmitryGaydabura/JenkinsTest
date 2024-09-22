package com.example.jenkinsspring.api;

import com.example.jenkinsspring.dao.ParticipantDAOImpl;
import com.example.jenkinsspring.model.Participant;
import com.example.jenkinsspring.service.ParticipantService;
import com.example.jenkinsspring.service.ParticipantServiceImpl;
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

public class AddParticipantServlet extends HttpServlet {

  private ParticipantService participantService;
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
      // Создание соединения с базой данных
      Connection connection = DriverManager.getConnection(
          "jdbc:postgresql://my-postgres-db.cn4kwmqcw0p8.eu-north-1.rds.amazonaws.com:5432/postgres",
          "postgres",
          "xAMP89zuA7TkEDVLYUn2"
      );

      // Инициализация DAO и сервиса
      ParticipantDAOImpl participantDAO = new ParticipantDAOImpl(connection);
      participantService = new ParticipantServiceImpl(participantDAO, connection);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new ServletException("Failed to initialize ParticipantService", e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    try {
      BufferedReader reader = req.getReader();
      ParticipantRequest participantRequest = gson.fromJson(reader, ParticipantRequest.class);

      if (participantRequest.getName() == null || participantRequest.getName().isEmpty() ||
          participantRequest.getTeam() == null || participantRequest.getTeam().isEmpty()) {
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required fields: name and team");
        return;
      }

      // Проверка, что команда является одной из допустимых
      String team = participantRequest.getTeam().toLowerCase();
      if (!team.equals("blue") && !team.equals("yellow")) {
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid team. Must be 'blue' or 'yellow'");
        return;
      }

      Participant participant = new Participant();
      participant.setName(participantRequest.getName());
      participant.setTeam(team);

      // Добавление участника через сервис
      participantService.addParticipant(participant);


      // Отправка успешного ответа
      resp.setContentType("application/json");
      resp.setCharacterEncoding("UTF-8");
      PrintWriter out = resp.getWriter();
      out.print(gson.toJson(new ApiResponse("Participant added successfully")));
      out.flush();
    } catch (JsonSyntaxException e) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format");
    } catch (SQLException e) {
      e.printStackTrace();
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error adding participant");
    } /* catch (TelegramApiException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error sending Telegram notification");
        } */
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String participantId = req.getParameter("id");

    if (participantId == null || participantId.isEmpty()) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Participant ID is required");
      return;
    }

    try {
      int id = Integer.parseInt(participantId);

      boolean isDeleted = participantService.deleteParticipantById(id);

      if (isDeleted) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        out.print(gson.toJson(new ApiResponse("Participant deleted successfully")));
        out.flush();
      } else {
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Participant not found");
      }
    } catch (NumberFormatException e) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid participant ID");
    } catch (SQLException e) {
      e.printStackTrace();
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting participant");
    }
  }

  @Override
  public void destroy() {
    super.destroy();
    // Закрытие соединения с БД при уничтожении сервлета
    try {
      if (participantService instanceof ParticipantServiceImpl) {
        ((ParticipantServiceImpl) participantService).closeConnection();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // Внутренний класс для запроса
  private class ParticipantRequest {
    private String name;
    private String team;

    public String getName() {
      return name;
    }

    public String getTeam() {
      return team;
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

