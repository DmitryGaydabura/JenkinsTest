package com.example.jenkinsspring.util;

import java.io.File;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TelegramSender extends TelegramLongPollingBot {

  private final String botToken;
  private final String botUsername;
  private TelegramBotsApi botsApi;

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
   * Метод для инициализации TelegramBotsApi и регистрации бота
   *
   * @throws TelegramApiException если происходит ошибка при регистрации бота
   */
  public void initialize() throws TelegramApiException {
    botsApi = new TelegramBotsApi(DefaultBotSession.class);
    botsApi.registerBot(this);
  }

  /**
   * Метод для остановки бота
   *
   * @throws TelegramApiException если происходит ошибка при остановке бота
   */
  public void stop() throws TelegramApiException {

  }

  /**
   * Метод для отправки текстового сообщения в Telegram
   *
   * @param chatId ID чата или пользователя
   * @param text   Текст сообщения
   * @throws TelegramApiException если происходит ошибка при отправке
   */
  public void sendMessage(String chatId, String text) throws TelegramApiException {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(chatId);
    sendMessage.setText(text);

    execute(sendMessage);
  }

  /**
   * Метод для отправки документа в Telegram
   *
   * @param chatId  ID чата или пользователя
   * @param file    Файл для отправки
   * @param caption Подпись к файлу
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
