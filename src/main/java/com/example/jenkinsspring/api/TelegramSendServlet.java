package com.example.jenkinsspring.api;

import com.example.jenkinsspring.util.TelegramSender;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class TelegramSendServlet extends HttpServlet {

  private TelegramSender telegramSender;
  private Gson gson = new Gson();

  @Override
  public void init() throws ServletException {
    super.init();
    // Инициализация TelegramSender с токеном и именем бота
    String botToken = "6516869813:AAF_VFQgr500uGSx2bKxC_Ij6_xH5ToZSZ0\n"; // Замените на ваш токен
    String botUsername = "testLab1011_bot"; // Замените на имя вашего бота
    telegramSender = new TelegramSender(botToken, botUsername);
    try {
      telegramSender.initialize(); // Метод для инициализации бота, если требуется
    } catch (TelegramApiException e) {
      e.printStackTrace();
      throw new ServletException("Failed to initialize Telegram Sender", e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    // Обработка запроса на отправку сообщения в Telegram
    try {
      BufferedReader reader = req.getReader();
      TelegramMessageRequest messageRequest = gson.fromJson(reader, TelegramMessageRequest.class);

      if (messageRequest.getChatId() == null || messageRequest.getText() == null || messageRequest.getText().isEmpty()) {
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required fields: chatId and text");
        return;
      }

      // Отправка сообщения через TelegramSender
      telegramSender.sendMessage(String.valueOf(messageRequest.getChatId()), messageRequest.getText());

      // Отправка успешного ответа
      resp.setContentType("application/json");
      resp.setCharacterEncoding("UTF-8");
      PrintWriter out = resp.getWriter();
      out.print(gson.toJson(new ApiResponse("Message sent successfully")));
      out.flush();
    } catch (JsonSyntaxException e) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format");
    } catch (TelegramApiException e) {
      e.printStackTrace();
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to send message");
    }
  }

  @Override
  public void destroy() {
    super.destroy();
    // Остановка Telegram бота, если требуется

  }

  // Внутренний класс для запроса
  private class TelegramMessageRequest {
    private Long chatId;
    private String text;

    public Long getChatId() {
      return chatId;
    }

    public String getText() {
      return text;
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
