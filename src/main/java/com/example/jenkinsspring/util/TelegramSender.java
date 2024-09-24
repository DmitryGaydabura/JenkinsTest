package com.example.jenkinsspring.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;

/**
 * Утилита для отправки сообщений и документов в Telegram.
 */
public class TelegramSender {
  private final String botToken;

  /**
   * Конструктор, инициализирующий токен бота.
   */
  public TelegramSender() {
    this.botToken = System.getenv("TELEGRAM_BOT_TOKEN");
  }

  /**
   * Отправляет документ в чат Telegram.
   *
   * @param chatId    ID чата.
   * @param document  Файл документа.
   * @param caption   Подпись к документу.
   * @throws IOException Если возникает ошибка при отправке документа.
   */
  public void sendDocument(String chatId, File document, String caption) throws IOException {
    String url = "https://api.telegram.org/bot" + botToken + "/sendDocument";

    HttpPost post = new HttpPost(url);

    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.addTextBody("chat_id", chatId, ContentType.TEXT_PLAIN);
    builder.addTextBody("caption", caption, ContentType.TEXT_PLAIN);
    builder.addBinaryBody("document", document, ContentType.APPLICATION_OCTET_STREAM, document.getName());

    HttpEntity multipart = builder.build();
    post.setEntity(multipart);

    try (CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(post)) {
      // Можно добавить обработку ответа, если необходимо
    }
  }

  /**
   * Отправляет текстовое сообщение в чат Telegram.
   *
   * @param chatId ID чата.
   * @param message Текст сообщения.
   * @throws IOException Если возникает ошибка при отправке сообщения.
   */
  public void sendMessage(String chatId, String message) throws IOException {
    String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

    HttpPost post = new HttpPost(url);
    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.addTextBody("chat_id", chatId, ContentType.TEXT_PLAIN);
    builder.addTextBody("text", message, ContentType.TEXT_PLAIN);

    HttpEntity multipart = builder.build();
    post.setEntity(multipart);

    try (CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(post)) {
      // Можно добавить обработку ответа, если необходимо
    }
  }
}
