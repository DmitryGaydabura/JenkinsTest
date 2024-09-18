package com.example.jenkinsspring.servlet;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ReportGenerator {

  public static void generateDailyActivityReport(List<Activity> activities, String filePath)
      throws IOException {
    // Создаем PDF Writer
    PdfWriter writer = new PdfWriter(filePath);

    // Создаем PDF документ
    PdfDocument pdf = new PdfDocument(writer);

    // Создаем объект Document
    Document document = new Document(pdf);

    // Добавляем заголовок
    Paragraph header = new Paragraph("Daily Activity Report")
        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
        .setFontSize(18)
        .setFontColor(ColorConstants.BLUE)
        .setTextAlignment(TextAlignment.CENTER)
        .setMarginBottom(20);
    document.add(header);

    // Создаем таблицу с 7 столбцами
    float[] columnWidths = {1, 2, 3, 3, 5, 3};
    Table table = new Table(columnWidths);

    // Устанавливаем ширину таблицы в 100%
    table.setWidth(UnitValue.createPercentValue(100));

    // Добавляем заголовки столбцов
    table.addHeaderCell(new Cell().add(new Paragraph("ID").setBold()));
    table.addHeaderCell(new Cell().add(new Paragraph("User ID").setBold()));
    table.addHeaderCell(new Cell().add(new Paragraph("First Name").setBold()));
    table.addHeaderCell(new Cell().add(new Paragraph("Last Name").setBold()));
    table.addHeaderCell(new Cell().add(new Paragraph("Description").setBold()));
    table.addHeaderCell(new Cell().add(new Paragraph("Date").setBold()));

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Заполняем таблицу данными
    for (Activity activity : activities) {
      table.addCell(new Cell().add(new Paragraph(String.valueOf(activity.getId()))));
      table.addCell(new Cell().add(new Paragraph(String.valueOf(activity.getUserId()))));
      table.addCell(new Cell().add(new Paragraph(activity.getFirstName())));
      table.addCell(new Cell().add(new Paragraph(activity.getLastName())));
      table.addCell(new Cell().add(new Paragraph(activity.getDescription())));
      table.addCell(new Cell().add(new Paragraph(sdf.format(activity.getActivityDate()))));
    }

    // Добавляем таблицу в документ
    document.add(table);

    // Закрываем документ
    document.close();
  }
}
