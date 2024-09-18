package com.example.jenkinsspring.servlet;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.IOException;
import java.util.Properties;
import java.io.File;

public class EmailSender {

  public static void sendEmailWithAttachment(String toEmail, String subject, String body, String attachmentPath) throws MessagingException {
    // Настройки SMTP сервера Gmail
    Properties props = new Properties();
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587"); // Используем порт 587 для TLS
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.ssl.protocols", "TLSv1.2"); // Обеспечиваем использование современного протокола

    // Учетные данные вашей учетной записи Gmail
    final String username = "gaydabura.d1@gmail.com"; // Ваш адрес Gmail
    final String password = "urme vzso taig veia"; // Пароль приложения, созданный ранее

    // Аутентификация
    Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    });

    // Создание сообщения
    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress(username));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
    message.setSubject(subject);

    // Тело сообщения
    MimeBodyPart messageBodyPart = new MimeBodyPart();
    messageBodyPart.setText(body);

    // Вложение
    MimeBodyPart attachmentPart = new MimeBodyPart();
    try {
      attachmentPart.attachFile(new File(attachmentPath));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    Multipart multipart = new MimeMultipart();
    multipart.addBodyPart(messageBodyPart);
    multipart.addBodyPart(attachmentPart);

    message.setContent(multipart);

    // Отправка сообщения
    Transport.send(message);
  }
}
