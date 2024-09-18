package com.example.jenkinsspring.util;

import com.example.jenkinsspring.model.Activity;
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

  public static void generateActivityReport(List<Activity> activities, String filePath) throws IOException {
    PdfWriter writer = new PdfWriter(filePath);
    PdfDocument pdf = new PdfDocument(writer);
    Document document = new Document(pdf);

    // Заголовок
    Paragraph header = new Paragraph("Activity Report")
        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
        .setFontSize(18)
        .setFontColor(ColorConstants.BLUE)
        .setTextAlignment(TextAlignment.CENTER)
        .setMarginBottom(20);
    document.add(header);

    // Таблица
    float[] columnWidths = {1, 2, 2, 4, 3};
    Table table = new Table(columnWidths);
    table.setWidth(UnitValue.createPercentValue(100));

    // Заголовки столбцов
    table.addHeaderCell(new Cell().add(new Paragraph("ID").setBold()));
    table.addHeaderCell(new Cell().add(new Paragraph("User ID").setBold()));
    table.addHeaderCell(new Cell().add(new Paragraph("Name").setBold()));
    table.addHeaderCell(new Cell().add(new Paragraph("Description").setBold()));
    table.addHeaderCell(new Cell().add(new Paragraph("Date").setBold()));

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Заполнение таблицы
    for (Activity activity : activities) {
      table.addCell(new Cell().add(new Paragraph(String.valueOf(activity.getId()))));
      table.addCell(new Cell().add(new Paragraph(String.valueOf(activity.getUserId()))));
      table.addCell(new Cell().add(new Paragraph(activity.getFirstName() + " " + activity.getLastName())));
      table.addCell(new Cell().add(new Paragraph(activity.getDescription())));
      table.addCell(new Cell().add(new Paragraph(sdf.format(activity.getActivityDate()))));
    }

    document.add(table);
    document.close();
  }
}
