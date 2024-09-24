package com.example.jenkinsspring.util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.File;
import java.util.Properties;

/**
 * Утилита для отправки электронных писем.
 */
public class EmailSender {
  private final String username;
  private final String password;
  private final Properties props;

  /**
   * Конструктор, инициализирующий настройки почтового сервера.
   */
  public EmailSender() {
    this.username = System.getenv("EMAIL_USERNAME");
    this.password = System.getenv("EMAIL_PASSWORD");

    props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", System.getenv("EMAIL_SMTP_HOST"));
    props.put("mail.smtp.port", System.getenv("EMAIL_SMTP_PORT"));
  }

  /**
   * Отправляет письмо с вложением.
   *
   * @param to          Получатель.
   * @param subject     Тема письма.
   * @param text        Текст письма.
   * @param attachmentPath Путь к файлу вложения.
   * @throws MessagingException Если возникает ошибка при отправке письма.
   */
  public void sendEmailWithAttachment(String to, String subject, String text, String attachmentPath) throws MessagingException {
    Session session = Session.getInstance(props,
        new jakarta.mail.Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
          }
        });

    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress(username));
    message.setRecipients(
        Message.RecipientType.TO,
        InternetAddress.parse(to)
    );
    message.setSubject(subject);

    // Создаем тело письма
    MimeBodyPart mimeBodyPart = new MimeBodyPart();
    mimeBodyPart.setText(text);

    // Создаем вложение
    MimeBodyPart attachmentBodyPart = new MimeBodyPart();
    try {
      attachmentBodyPart.attachFile(new File(attachmentPath));
    } catch (Exception e) {
      throw new MessagingException("Не удалось прикрепить файл: " + attachmentPath, e);
    }

    // Объединяем тело и вложение
    Multipart multipart = new MimeMultipart();
    multipart.addBodyPart(mimeBodyPart);
    multipart.addBodyPart(attachmentBodyPart);

    message.setContent(multipart);

    Transport.send(message);
  }
}
