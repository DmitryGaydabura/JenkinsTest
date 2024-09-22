package com.example.jenkinsspring.api;

import com.example.jenkinsspring.model.Activity;
import com.example.jenkinsspring.service.ActivityService;
import com.example.jenkinsspring.service.ActivityServiceImpl;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

@Path("/activities")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ActivityResource {

  private ActivityService activityService;

  public ActivityResource() {
    try {
      // Инициализация соединения
      Connection connection = DriverManager.getConnection(
          "jdbc:postgresql://my-postgres-db.cn4kwmqcw0p8.eu-north-1.rds.amazonaws.com:5432/postgres",
          "postgres",
          "xAMP89zuA7TkEDVLYUn2"
      );
      connection.setAutoCommit(false);
      connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

      activityService = new ActivityServiceImpl(new com.example.jenkinsspring.dao.ActivityDAOImpl(connection));
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to initialize ActivityService", e);
    }
  }

  @GET
  public Response getAllActivities() {
    try {
      List<Activity> activities = activityService.getAllActivities();
      return Response.ok(activities).build();
    } catch (SQLException e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Error retrieving activities")
          .build();
    }
  }

  @POST
  public Response addActivity(Activity activity) {
    try {
      activityService.addActivity(activity);
      return Response.status(Response.Status.CREATED).entity(activity).build();
    } catch (Exception e) { // Можно уточнить тип исключения
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Error adding activity")
          .build();
    }
  }

  @DELETE
  @Path("/{id}")
  public Response deleteActivity(@PathParam("id") Long id) {
    try {
      activityService.deleteActivity(id);
      return Response.noContent().build();
    } catch (SQLException e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Error deleting activity")
          .build();
    }
  }

  // Дополнительные методы по необходимости
}

