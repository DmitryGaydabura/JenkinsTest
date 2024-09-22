package com.example.jenkinsspring.api;

import com.example.jenkinsspring.dao.ParticipantDAOImpl;
import com.example.jenkinsspring.model.Participant;
import com.example.jenkinsspring.service.ParticipantService;
import com.example.jenkinsspring.service.ParticipantServiceImpl;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class GetParticipantPairsServlet extends HttpServlet {

  private ParticipantService participantService;
  private List<Pair> previousPairs = new ArrayList<>();
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
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      // Получаем участников из обеих команд
      List<Participant> blueTeam = participantService.getParticipantsByTeam("blue");
      List<Participant> yellowTeam = participantService.getParticipantsByTeam("yellow");

      // Определяем минимальное количество участников, чтобы избежать проблем с неравным количеством участников
      int pairCount = Math.min(blueTeam.size(), yellowTeam.size());

      // Создаем список пар, избегая предыдущих
      List<Pair> pairs = generatePairs(blueTeam, yellowTeam, pairCount);

      // Сохраняем текущие пары для последующего исключения
      previousPairs = pairs;

      // Возвращаем результат в формате JSON
      resp.setContentType("application/json");
      resp.setCharacterEncoding("UTF-8");
      PrintWriter out = resp.getWriter();
      out.print(gson.toJson(pairs));
      out.flush();

    } catch (SQLException e) {
      e.printStackTrace();
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving participants");
    }
  }

  /**
   * Метод для генерации уникальных пар участников
   */
  private List<Pair> generatePairs(List<Participant> blueTeam, List<Participant> yellowTeam, int pairCount) {
    List<Pair> newPairs = new ArrayList<>();

    // Создаем доступные индексы участников обеих команд
    List<Participant> blueCandidates = new ArrayList<>(blueTeam);
    List<Participant> yellowCandidates = new ArrayList<>(yellowTeam);

    Random random = new Random();

    while (newPairs.size() < pairCount) {
      // Получаем случайных участников из обеих команд
      Participant blueParticipant = blueCandidates.remove(random.nextInt(blueCandidates.size()));
      Participant yellowParticipant = yellowCandidates.remove(random.nextInt(yellowCandidates.size()));

      Pair pair = new Pair(blueParticipant, yellowParticipant);

      // Проверяем, чтобы пара не повторялась с предыдущим запросом
      if (!previousPairs.contains(pair)) {
        newPairs.add(pair);
      } else {
        // Возвращаем участников обратно в список кандидатов, если пара была ранее
        blueCandidates.add(blueParticipant);
        yellowCandidates.add(yellowParticipant);
      }

      // Если все возможные комбинации были использованы, выходим
      if (blueCandidates.isEmpty() || yellowCandidates.isEmpty()) {
        break;
      }
    }

    return newPairs;
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

  /**
   * Класс для представления пары участников
   */
  private static class Pair {
    private Participant blueParticipant;
    private Participant yellowParticipant;

    public Pair(Participant blueParticipant, Participant yellowParticipant) {
      this.blueParticipant = blueParticipant;
      this.yellowParticipant = yellowParticipant;
    }

    public Participant getBlueParticipant() {
      return blueParticipant;
    }

    public Participant getYellowParticipant() {
      return yellowParticipant;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Pair pair = (Pair) o;
      return Objects.equals(blueParticipant, pair.blueParticipant) &&
          Objects.equals(yellowParticipant, pair.yellowParticipant);
    }

    @Override
    public int hashCode() {
      return Objects.hash(blueParticipant, yellowParticipant);
    }
  }
}
