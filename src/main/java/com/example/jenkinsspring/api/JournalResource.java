package com.example.jenkinsspring.api;

import com.example.jenkinsspring.model.Pair;
import com.example.jenkinsspring.model.User;
import com.example.jenkinsspring.service.PairService;
import com.example.jenkinsspring.service.PairServiceImpl;
import com.example.jenkinsspring.service.UserService;
import com.example.jenkinsspring.service.UserServiceImpl;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Path("/journal")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JournalResource {

  private UserService userService;
  private PairService pairService;

  public JournalResource() {
    try {
      // Инициализация соединения
      Connection connection = DriverManager.getConnection(
          "jdbc:postgresql://my-postgres-db.cn4kwmqcw0p8.eu-north-1.rds.amazonaws.com:5432/postgres",
          "postgres",
          "xAMP89zuA7TkEDVLYUn2"
      );
      connection.setAutoCommit(false);
      connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

      userService = new UserServiceImpl(new com.example.jenkinsspring.dao.UserDAOImpl(connection));
      pairService = new PairServiceImpl(new com.example.jenkinsspring.dao.PairDAOImpl(connection));
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to initialize JournalResource", e);
    }
  }

  /**
   * Добавить пользователя в команду
   *
   * @param userId ID пользователя
   * @param team    Название команды (например, "Team1" или "Team2")
   * @return Response
   */
  @POST
  @Path("/addToTeam")
  public Response addUserToTeam(@QueryParam("userId") Long userId, @QueryParam("team") String team) {
    try {
      User user = userService.getUserById(userId);
      if (user == null) {
        return Response.status(Response.Status.NOT_FOUND)
            .entity("User not found")
            .build();
      }

      // Предполагается, что у модели User есть поле team
      user.setTeam(team);
      userService.updateUser(user);

      return Response.ok(user).build();
    } catch (SQLException e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Error adding user to team")
          .build();
    }
  }

  /**
   * Получить список пользователей команды 1
   *
   * @return Список пользователей
   */
  @GET
  @Path("/team1")
  public Response getTeam1() {
    return getTeamByName("Team1");
  }

  /**
   * Получить список пользователей команды 2
   *
   * @return Список пользователей
   */
  @GET
  @Path("/team2")
  public Response getTeam2() {
    return getTeamByName("Team2");
  }

  private Response getTeamByName(String teamName) {
    try {
      List<User> users = userService.getUsersByTeam(teamName);
      return Response.ok(users).build();
    } catch (SQLException e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Error retrieving team users")
          .build();
    }
  }

  /**
   * Сгенерировать пары из разных команд, исключая предыдущие пары
   *
   * @return Список пар
   */
  @POST
  @Path("/generatePairs")
  public Response generatePairs() {
    try {
      List<User> team1 = userService.getUsersByTeam("Team1");
      List<User> team2 = userService.getUsersByTeam("Team2");

      if (team1.isEmpty() || team2.isEmpty()) {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity("Both teams must have at least one member")
            .build();
      }

      // Перемешиваем команды для случайного распределения
      Collections.shuffle(team1);
      Collections.shuffle(team2);

      List<Pair> newPairs = new ArrayList<>();

      // Минимальное количество пар
      int pairCount = Math.min(team1.size(), team2.size());

      for (int i = 0; i < pairCount; i++) {
        User user1 = team1.get(i);
        User user2 = team2.get(i);

        // Проверяем, были ли они ранее в паре
        if (pairService.pairExists(user1.getId(), user2.getId())) {
          // Ищем другой пользователь из другой команды
          boolean paired = false;
          for (int j = i + 1; j < team2.size(); j++) {
            User alternative = team2.get(j);
            if (!pairService.pairExists(user1.getId(), alternative.getId())) {
              // Меняем местами
              team2.set(j, user2);
              team2.set(i, alternative);
              user2 = alternative;
              paired = true;
              break;
            }
          }
          if (!paired) {
            // Если не удалось найти уникальную пару, продолжаем
            // Можно добавить логику для обработки этого случая
          }
        }

        Pair pair = new Pair();
        pair.setUser1Id(user1.getId());
        pair.setUser2Id(user2.getId());
        pairService.addPair(pair);
        newPairs.add(pair);
      }

      return Response.ok(newPairs).build();
    } catch (SQLException e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Error generating pairs")
          .build();
    }
  }
}
