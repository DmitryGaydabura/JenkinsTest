package com.example.jenkinsspring.util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.IOException;
import java.util.Properties;
import java.io.File;

public class EmailSender {

  public static void sendEmailWithAttachment(String toEmail, String subject, String body, String attachmentPath)
      throws MessagingException, IOException {
    // Настройки SMTP сервера
    Properties props = new Properties();
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587"); // Порт для TLS
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");

    // Учетные данные
    final String username = "your_email@gmail.com"; // Замените на ваш email
    final String password = "your_app_password"; // Замените на ваш пароль приложения

    // Создание сессии
    Session session = Session.getInstance(props, new Authenticator() {
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
    attachmentPart.attachFile(new File(attachmentPath));

    // Комбинируем части
    Multipart multipart = new MimeMultipart();
    multipart.addBodyPart(messageBodyPart);
    multipart.addBodyPart(attachmentPart);

    message.setContent(multipart);

    // Отправка сообщения
    Transport.send(message);
  }
}
