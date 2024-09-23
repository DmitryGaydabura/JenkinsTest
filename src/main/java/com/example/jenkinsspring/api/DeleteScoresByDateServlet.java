package com.example.jenkinsspring.api;

import com.example.jenkinsspring.dao.JournalScoreDAOImpl;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;

public class DeleteScoresByDateServlet extends HttpServlet {

  private JournalScoreDAOImpl scoreDAO;
  private Gson gson = new Gson();

  @Override
  public void init() throws ServletException {
    super.init();
    try {
      Connection connection = DriverManager.getConnection(
          "jdbc:postgresql://my-postgres-db.cn4kwmqcw0p8.eu-north-1.rds.amazonaws.com:5432/postgres",
          "postgres",
          "xAMP89zuA7TkEDVLYUn2"
      );
      scoreDAO = new JournalScoreDAOImpl(connection);
    } catch (SQLException e) {
      throw new ServletException("Unable to initialize database connection", e);
    }
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String dateString = req.getParameter("date");  // Получаем дату из параметра запроса
    resp.setContentType("application/json");
    PrintWriter out = resp.getWriter();

    try {
      // Преобразование строки в java.sql.Date
      java.sql.Date sqlDate = java.sql.Date.valueOf(dateString);

      // Удаляем данные из базы
      int rowsDeleted = scoreDAO.deleteScoresByDate(sqlDate);

      if (rowsDeleted > 0) {
        // Если были удалены строки, возвращаем успешный ответ
        out.print(gson.toJson("Scores for the date deleted successfully"));
        resp.setStatus(HttpServletResponse.SC_OK);
      } else {
        // Если не было удаленных данных, возвращаем 404
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        out.print(gson.toJson("No scores found for the specified date"));
      }
    } catch (SQLException e) {
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
      e.printStackTrace();
    } finally {
      out.flush();
    }
  }

}

