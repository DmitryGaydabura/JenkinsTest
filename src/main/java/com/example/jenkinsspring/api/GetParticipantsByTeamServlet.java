package com.example.jenkinsspring.api;


import com.example.jenkinsspring.dao.ParticipantDAOImpl;
import com.example.jenkinsspring.model.Participant;
import com.example.jenkinsspring.service.ParticipantService;
import com.example.jenkinsspring.service.ParticipantServiceImpl;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetParticipantsByTeamServlet extends HttpServlet {

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
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    // Получение параметра команды из URL
    String pathInfo = req.getPathInfo(); // Ожидается /blue или /yellow
    if (pathInfo == null || pathInfo.equals("/")) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Team name is required in the URL");
      return;
    }

    String team = pathInfo.substring(1).toLowerCase(); // Удаление '/' и преобразование в нижний регистр

    // Проверка, что команда является одной из допустимых
    if (!team.equals("blue") && !team.equals("yellow")) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid team. Must be 'blue' or 'yellow'");
      return;
    }

    try {
      // Получение списка участников команды через сервис
      List<Participant> participants = participantService.getParticipantsByTeam(team);

      // Отправка ответа в формате JSON
      resp.setContentType("application/json");
      resp.setCharacterEncoding("UTF-8");
      PrintWriter out = resp.getWriter();
      out.print(gson.toJson(participants));
      out.flush();
    } catch (SQLException e) {
      e.printStackTrace();
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving participants");
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
}

