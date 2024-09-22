package com.example.jenkinsspring.api;

import com.example.jenkinsspring.model.User;
import com.example.jenkinsspring.service.UserService;
import com.example.jenkinsspring.service.UserServiceImpl;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

  private UserService userService;

  public UserResource() {
    try {
      // Инициализация соединения
      Connection connection = DriverManager.getConnection(
          "jdbc:postgresql://my-postgres-db.cn4kwmqcw0p8.eu-north-1.rds.amazonaws.com:5432/postgres",
          "postgres",
          "xAMP89zuA7TkEDVLYUn2"
      );
      connection.setAutoCommit(false);
      connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

      userService = new UserServiceImpl(new com.example.jenkinsspring.dao.UserDAOImpl(connection));
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to initialize UserService", e);
    }
  }

  @GET
  public Response getAllUsers() {
    try {
      List<User> users = userService.getAllUsers();
      return Response.ok(users).build();
    } catch (SQLException e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Error retrieving users")
          .build();
    }
  }

  @POST
  public Response addUser(User user) {
    try {
      userService.addUser(user);
      return Response.status(Response.Status.CREATED).entity(user).build();
    } catch (SQLException e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Error adding user")
          .build();
    }
  }

  @PUT
  @Path("/{id}")
  public Response updateUser(@PathParam("id") Long id, User user) {
    try {
      user.setId(id);
      userService.updateUser(user);
      return Response.ok(user).build();
    } catch (SQLException e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Error updating user")
          .build();
    }
  }

  @DELETE
  @Path("/{id}")
  public Response deleteUser(@PathParam("id") Long id) {
    try {
      userService.deleteUser(id);
      return Response.noContent().build();
    } catch (SQLException e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Error deleting user")
          .build();
    }
  }
}

