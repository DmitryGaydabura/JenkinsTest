package com.example.jenkinsspring.util;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

public class TelegramSender extends TelegramLongPollingBot {

  private final String botToken;
  private final String botUsername;

  public TelegramSender(String botToken, String botUsername) {
    this.botToken = botToken;
    this.botUsername = botUsername;
  }

  @Override
  public String getBotUsername() {
    return botUsername; // Имя вашего бота
  }

  @Override
  public String getBotToken() {
    return botToken; // Токен вашего бота
  }

  @Override
  public void onUpdateReceived(org.telegram.telegrambots.meta.api.objects.Update update) {
    // Этот бот предназначен только для отправки сообщений, поэтому мы не обрабатываем входящие обновления
  }

  /**
   * Метод для отправки документа в Telegram
   *
   * @param chatId   ID чата или пользователя
   * @param file     Файл для отправки
   * @param caption  Подпись к файлу
   * @throws TelegramApiException если происходит ошибка при отправке
   */
  public void sendDocument(String chatId, File file, String caption) throws TelegramApiException {
    SendDocument sendDocument = new SendDocument();
    sendDocument.setChatId(chatId);
    sendDocument.setDocument(new InputFile(file));
    sendDocument.setCaption(caption);

    execute(sendDocument);
  }
}
