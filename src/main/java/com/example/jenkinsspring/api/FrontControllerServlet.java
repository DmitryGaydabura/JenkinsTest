package com.example.jenkinsspring.api;

import com.example.jenkinsspring.exception.InsufficientParticipantsException;
import com.example.jenkinsspring.exception.PairGenerationException;
import com.example.jenkinsspring.exception.ParticipantAlreadyExistsException;
import com.example.jenkinsspring.exception.ParticipantNotFoundException;
import com.example.jenkinsspring.model.Activity;
import com.example.jenkinsspring.model.JournalScore;
import com.example.jenkinsspring.model.Pair;
import com.example.jenkinsspring.model.Participant;
import com.example.jenkinsspring.model.User;
import com.example.jenkinsspring.service.ActivityService;
import com.example.jenkinsspring.service.ActivityServiceImpl;
import com.example.jenkinsspring.service.JournalScoreService;
import com.example.jenkinsspring.service.JournalScoreServiceImpl;
import com.example.jenkinsspring.service.PairService;
import com.example.jenkinsspring.service.PairServiceImpl;
import com.example.jenkinsspring.service.ParticipantService;
import com.example.jenkinsspring.service.ParticipantServiceImpl;
import com.example.jenkinsspring.service.UserService;
import com.example.jenkinsspring.service.UserServiceImpl;
import com.example.jenkinsspring.util.EmailSender;
import com.example.jenkinsspring.util.TelegramSender;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Единственный сервлет для обработки всех входящих HTTP-запросов.
 * Использует HashMap для маршрутизации запросов к соответствующим обработчикам.
 */
public class FrontControllerServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private Gson gson = new Gson();

  // Карта маршрутов: ключ - комбинация HTTP метода и пути, значение - обработчик
  private Map<String, BiConsumer<HttpServletRequest, HttpServletResponse>> routes = new HashMap<>();

  // Сервисные компоненты
  private UserService userService;
  private ActivityService activityService;
  private ParticipantService participantService;
  private JournalScoreService journalScoreService;

  private PairService pairService;
  private TelegramSender telegramSender;
  private EmailSender emailSender;

  @Override
  public void init() throws ServletException {
    super.init();

    // Инициализация сервисных компонентов
    try {
      userService = new UserServiceImpl();
      activityService = new ActivityServiceImpl();
      participantService = new ParticipantServiceImpl();
      pairService = new PairServiceImpl(participantService);
      journalScoreService = new JournalScoreServiceImpl();
      telegramSender = new TelegramSender();
      emailSender = new EmailSender();
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServletException("Ошибка инициализации сервисов", e);
    }

    // Инициализация маршрутов
    initializeRoutes();
  }

  /**
   * Инициализирует маршруты и соответствующие обработчики.
   */
  private void initializeRoutes() {
    // Пример маршрутов
    routes.put("GET:/users", this::handleGetAllUsers);
    routes.put("POST:/users", this::handleAddUser);
    routes.put("PUT:/users", this::handleUpdateUser);
    routes.put("DELETE:/users", this::handleDeleteUser);

    routes.put("GET:/activities", this::handleGetAllActivities);
    routes.put("POST:/activities", this::handleAddActivity);
    routes.put("PUT:/activities", this::handleUpdateActivity);
    routes.put("DELETE:/activities", this::handleDeleteActivity);

    routes.put("GET:/participants", this::handleGetParticipantsByTeam);
    routes.put("POST:/participants", this::handleAddParticipant);
    routes.put("DELETE:/participants", this::handleDeleteParticipant);
    routes.put("POST:/participants/generate-pairs", this::handleGeneratePairs);

    routes.put("POST:/journal/scores", this::handleAddScore);
    routes.put("GET:/journal/scores", this::handleGetAllScores);
    routes.put("DELETE:/journal/scores/delete", this::handleDeleteScoresByDate);

    routes.put("POST:/email/send", this::handleSendEmail);
    routes.put("POST:/telegram/send", this::handleSendTelegramMessage);

    // Добавьте дополнительные маршруты по необходимости
  }

  private void handleGeneratePairs(HttpServletRequest req, HttpServletResponse resp) {
    try {
      // Вызов бизнес-логики из сервисного слоя
      List<Pair> newPairs = pairService.generatePairs();

      // Подготовка ответа
      List<Map<String, Object>> responsePairs = new ArrayList<>();
      for (Pair pair : newPairs) {
        Map<String, Object> pairMap = new HashMap<>();
        pairMap.put("blueParticipantId", pair.getBlueParticipantId());
        pairMap.put("yellowParticipantId", pair.getYellowParticipantId());
        responsePairs.add(pairMap);
      }

      sendJsonResponse(resp, responsePairs, HttpServletResponse.SC_OK);
    } catch (InsufficientParticipantsException | PairGenerationException e) {
      sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при генерации пар.");
    }
  }


  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String method = req.getMethod();
    String path = req.getPathInfo();

    // Составляем ключ для маршрутизации
    String routeKey = method + ":" + (path != null ? path : "");

    BiConsumer<HttpServletRequest, HttpServletResponse> handler = routes.get(routeKey);

    if (handler != null) {
      handler.accept(req, resp);
    } else {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Маршрут не найден");
    }
  }

  /**
   * Обработчик для получения всех пользователей.
   */
  private void handleGetAllUsers(HttpServletRequest req, HttpServletResponse resp) {
    try {
      List<User> users = userService.getAllUsers();
      sendJsonResponse(resp, users);
    } catch (Exception e) {
      sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при получении пользователей");
    }
  }

  /**
   * Обработчик для добавления нового пользователя.
   */
  private void handleAddUser(HttpServletRequest req, HttpServletResponse resp) {
    try {
      User user = parseRequestBody(req, User.class);
      userService.addUser(user);
      sendJsonResponse(resp, user, HttpServletResponse.SC_CREATED);
    } catch (JsonSyntaxException e) {
      sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Некорректный формат JSON");
    } catch (Exception e) {
      sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при добавлении пользователя");
    }
  }

  /**
   * Обработчик для обновления существующего пользователя.
   */
  private void handleUpdateUser(HttpServletRequest req, HttpServletResponse resp) {
    try {
      User user = parseRequestBody(req, User.class);
      userService.updateUser(user);
      sendJsonResponse(resp, user);
    } catch (JsonSyntaxException e) {
      sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Некорректный формат JSON");
    } catch (Exception e) {
      sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при обновлении пользователя");
    }
  }

  /**
   * Обработчик для мягкого удаления пользователя.
   */
  private void handleDeleteUser(HttpServletRequest req, HttpServletResponse resp) {
    try {
      String userIdStr = req.getParameter("id");
      if (userIdStr == null || userIdStr.isEmpty()) {
        sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Не указан ID пользователя");
        return;
      }
      Long userId = Long.parseLong(userIdStr);
      userService.softDeleteUser(userId);
      resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    } catch (NumberFormatException e) {
      sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Некорректный формат ID пользователя");
    } catch (Exception e) {
      sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при удалении пользователя");
    }
  }

  /**
   * Обработчик для получения всех активностей.
   */
  private void handleGetAllActivities(HttpServletRequest req, HttpServletResponse resp) {
    try {
      List<Activity> activities = activityService.getAllActivities();
      sendJsonResponse(resp, activities);
    } catch (Exception e) {
      sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при получении активностей");
    }
  }

  /**
   * Обработчик для добавления новой активности.
   */
  private void handleAddActivity(HttpServletRequest req, HttpServletResponse resp) {
    try {
      Activity activity = parseRequestBody(req, Activity.class);
      activityService.addActivity(activity);
      sendJsonResponse(resp, activity, HttpServletResponse.SC_CREATED);
    } catch (JsonSyntaxException e) {
      sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Некорректный формат JSON");
    } catch (Exception e) {
      sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при добавлении активности");
    }
  }

  /**
   * Обработчик для обновления активности.
   */
  private void handleUpdateActivity(HttpServletRequest req, HttpServletResponse resp) {
    try {
      Activity activity = parseRequestBody(req, Activity.class);
      activityService.updateActivity(activity);
      sendJsonResponse(resp, activity);
    } catch (JsonSyntaxException e) {
      sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Некорректный формат JSON");
    } catch (Exception e) {
      sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при обновлении активности");
    }
  }

  /**
   * Обработчик для удаления активности.
   */
  private void handleDeleteActivity(HttpServletRequest req, HttpServletResponse resp) {
    try {
      String activityIdStr = req.getParameter("id");
      if (activityIdStr == null || activityIdStr.isEmpty()) {
        sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Не указан ID активности");
        return;
      }
      Long activityId = Long.parseLong(activityIdStr);
      activityService.deleteActivity(activityId);
      resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    } catch (NumberFormatException e) {
      sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Некорректный формат ID активности");
    } catch (Exception e) {
      sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при удалении активности");
    }
  }

  /**
   * Обработчик для получения участников по команде.
   */
  private void handleGetParticipantsByTeam(HttpServletRequest req, HttpServletResponse resp) {
    try {
      String team = req.getParameter("team");
      if (team == null || team.isEmpty()) {
        sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Не указана команда");
        return;
      }
      List<Participant> participants = participantService.getParticipantsByTeam(team.toLowerCase());
      sendJsonResponse(resp, participants);
    } catch (Exception e) {
      sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при получении участников");
    }
  }

  /**
   * Обработчик для добавления нового участника.
   */
  private void handleAddParticipant(HttpServletRequest req, HttpServletResponse resp) {
    try {
      Participant participant = parseRequestBody(req, Participant.class);
      participantService.addParticipant(participant);
      sendJsonResponse(resp, participant, HttpServletResponse.SC_CREATED);
    } catch (ParticipantAlreadyExistsException e) {
      sendError(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
    } catch (JsonSyntaxException e) {
      sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Некорректный формат JSON");
    } catch (Exception e) {
      sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при добавлении участника");
    }
  }


  /**
   * Обработчик для удаления участника.
   */
  /**
   * Обработчик для удаления участника.
   */
  private void handleDeleteParticipant(HttpServletRequest req, HttpServletResponse resp) {
    try {
      String participantIdStr = req.getParameter("id");
      if (participantIdStr == null || participantIdStr.isEmpty()) {
        sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Не указан ID участника");
        return;
      }
      int participantId = Integer.parseInt(participantIdStr);

      participantService.softDeleteParticipant(participantId);

      sendJsonResponse(resp, new ApiResponse("Участник успешно удален"));
    } catch (NumberFormatException e) {
      sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Некорректный формат ID участника");
    } catch (ParticipantNotFoundException e) {
      sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Участник не найден");
    } catch (Exception e) {
      sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при удалении участника");
    }
  }



  /**
   * Обработчик для добавления или обновления оценки в журнале.
   */
  private void handleAddScore(HttpServletRequest req, HttpServletResponse resp) {
    try {
      JournalScore score = parseRequestBody(req, JournalScore.class);
      journalScoreService.addOrUpdateScore(score);
      sendJsonResponse(resp, new ApiResponse("Оценка успешно добавлена/обновлена"), HttpServletResponse.SC_CREATED);
    } catch (JsonSyntaxException e) {
      sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Некорректный формат JSON");
    } catch (Exception e) {
      sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при добавлении оценки");
    }
  }

  /**
   * Обработчик для получения всех оценок.
   */
  private void handleGetAllScores(HttpServletRequest req, HttpServletResponse resp) {
    try {
      List<JournalScore> scores = journalScoreService.getAllScores();
      sendJsonResponse(resp, scores);
    } catch (Exception e) {
      sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при получении оценок");
    }
  }

  /**
   * Обработчик для удаления оценок по дате.
   */
  private void handleDeleteScoresByDate(HttpServletRequest req, HttpServletResponse resp) {
    try {
      String dateStr = req.getParameter("date");
      if (dateStr == null || dateStr.isEmpty()) {
        sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Не указана дата");
        return;
      }
      java.sql.Date sqlDate = java.sql.Date.valueOf(dateStr);
      int rowsDeleted = journalScoreService.deleteScoresByDate(sqlDate);
      if (rowsDeleted > 0) {
        sendJsonResponse(resp, new ApiResponse("Оценки за указанную дату успешно удалены"));
      } else {
        sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Оценки за указанную дату не найдены");
      }
    } catch (IllegalArgumentException e) {
      sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Некорректный формат даты. Ожидается YYYY-MM-DD");
    } catch (Exception e) {
      sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при удалении оценок");
    }
  }

  /**
   * Обработчик для отправки отчета по электронной почте.
   */
  private void handleSendEmail(HttpServletRequest req, HttpServletResponse resp) {
    try {
      EmailReportRequest reportRequest = parseRequestBody(req, EmailReportRequest.class);
      emailSender.sendEmailWithAttachment(reportRequest.getEmail(), "Activity Report",
          "Please find attached the activity report.", "/tmp/activity_report.pdf");
      sendJsonResponse(resp, new ApiResponse("Отчет успешно отправлен на email"));
    } catch (JsonSyntaxException e) {
      sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Некорректный формат JSON");
    } catch (Exception e) {
      sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при отправке отчета по email");
    }
  }

  /**
   * Обработчик для отправки отчета в Telegram.
   */
  private void handleSendTelegramMessage(HttpServletRequest req, HttpServletResponse resp) {
    try {
      TelegramReportRequest reportRequest = parseRequestBody(req, TelegramReportRequest.class);
      telegramSender.sendDocument(String.valueOf(reportRequest.getChatId()), new File("/tmp/activity_report.pdf"), "Список активностей");
      sendJsonResponse(resp, new ApiResponse("Отчет успешно отправлен в Telegram"));
    } catch (JsonSyntaxException e) {
      sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Некорректный формат JSON");
    } catch (Exception e) {
      sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при отправке отчета в Telegram");
    }
  }

  /**
   * Парсит тело запроса в объект указанного класса.
   *
   * @param <T>   Тип объекта
   * @param req   HttpServletRequest
   * @param clazz Класс объекта
   * @return Объект типа T
   * @throws IOException
   */
  private <T> T parseRequestBody(HttpServletRequest req, Class<T> clazz) throws IOException {
    BufferedReader reader = req.getReader();
    return gson.fromJson(reader, clazz);
  }

  /**
   * Отправляет JSON-ответ клиенту.
   *
   * @param resp      HttpServletResponse
   * @param data      Данные для отправки
   * @param status    HTTP статус (по умолчанию 200)
   * @throws IOException
   */
  private void sendJsonResponse(HttpServletResponse resp, Object data, int... status) throws IOException {
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    if (status.length > 0) {
      resp.setStatus(status[0]);
    } else {
      resp.setStatus(HttpServletResponse.SC_OK);
    }
    PrintWriter out = resp.getWriter();
    out.print(gson.toJson(data));
    out.flush();
  }

  /**
   * Отправляет JSON-ответ с HTTP статусом 200.
   *
   * @param resp HttpServletResponse
   * @param data Данные для отправки
   * @throws IOException
   */
  private void sendJsonResponse(HttpServletResponse resp, Object data) throws IOException {
    sendJsonResponse(resp, data, HttpServletResponse.SC_OK);
  }

  /**
   * Отправляет ошибку клиенту.
   *
   * @param resp    HttpServletResponse
   * @param status  HTTP статус ошибки
   * @param message Сообщение об ошибке
   */
  private void sendError(HttpServletResponse resp, int status, String message) {
    try {
      resp.sendError(status, message);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Внутренний класс для запроса отправки email.
   */
  private class EmailReportRequest {
    private String email;

    public String getEmail() {
      return email;
    }
  }

  /**
   * Внутренний класс для запроса отправки в Telegram.
   */
  private class TelegramReportRequest {
    private Long chatId;

    public Long getChatId() {
      return chatId;
    }
  }

  /**
   * Внутренний класс для ответа API.
   */
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
