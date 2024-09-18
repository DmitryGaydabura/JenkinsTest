<%@ page import="com.example.jenkinsspring.model.Activity" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Activity Management</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      margin: 0;
      padding: 0;
      background-color: #f4f4f4;
      color: #333;
    }
    header {
      background-color: #007bff;
      color: #fff;
      padding: 1rem;
      text-align: center;
    }
    .container {
      width: 80%;
      margin: auto;
      overflow: hidden;
    }
    h1, h2 {
      margin-top: 0;
    }
    .form-container {
      background: #fff;
      padding: 2rem;
      margin-bottom: 1rem;
      border-radius: 8px;
      box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    }
    form {
      margin: 1rem 0;
    }
    label {
      display: block;
      margin: 0.5rem 0 0.2rem;
    }
    input[type="text"], input[type="number"] {
      width: 100%;
      padding: 0.5rem;
      margin-bottom: 1rem;
      border: 1px solid #ddd;
      border-radius: 4px;
    }
    button {
      background-color: #007bff;
      color: #fff;
      border: none;
      padding: 0.7rem 1.5rem;
      border-radius: 4px;
      cursor: pointer;
    }
    button:hover {
      background-color: #0056b3;
    }
    table {
      width: 100%;
      border-collapse: collapse;
      margin-top: 1rem;
    }
    table, th, td {
      border: 1px solid #ddd;
    }
    th, td {
      padding: 0.7rem;
      text-align: left;
    }
    th {
      background-color: #007bff;
      color: #fff;
    }
    .no-users {
      text-align: center;
      color: #888;
      padding: 1rem;
    }
  </style>
</head>
<body>
<!-- Отображение сообщения об ошибке -->
<% if (request.getAttribute("errorMessage") != null) { %>
<div class="alert error">
  <%= request.getAttribute("errorMessage") %>
</div>
<% } %>

<!-- Отображение сообщения об успехе -->
<% if (request.getAttribute("message") != null) { %>
<div class="alert success">
  <%= request.getAttribute("message") %>
</div>
<% } %>

<!-- Добавьте стили для сообщений -->
<style>
  .alert {
    padding: 15px;
    margin-bottom: 15px;
    color: white;
  }
  .alert.success {
    background-color: #4CAF50; /* Зеленый */
  }
  .alert.error {
    background-color: #f44336; /* Красный */
  }
</style>

<header>
  <h1>User Management System</h1>
  <!-- Добавляем навигационные кнопки -->
  <nav>
    <a href="${pageContext.request.contextPath}/activity">Activities</a>
    <a href="${pageContext.request.contextPath}/user">Users</a>
  </nav>
</header>
<div class="container">
  <!-- Add Activity -->
  <div class="form-container">
    <h2>Add Activity</h2>
    <form action="activity" method="post">
      <input type="hidden" name="action" value="add">
      <label for="userId">User ID:</label>
      <input type="text" id="userId" name="userId" required>
      <label for="description">Description:</label>
      <textarea id="description" name="description" rows="4" required></textarea>
      <button type="submit">Add Activity</button>
    </form>
  </div>

  <!-- Display Activities -->
  <div class="form-container">
    <h2>Activities List</h2>
    <!-- Добавляем кнопку для отправки отчета -->
    <form action="activity" method="post" style="text-align: right; margin-bottom: 10px;">
      <input type="hidden" name="action" value="sendReport">
      <button type="submit">Отправить отчет на почту</button>
    </form>
    <table>
      <thead>
      <tr>
        <th>ID</th>
        <th>User ID</th>
        <th>First Name</th>
        <th>Last Name</th>
        <th>Description</th>
        <th>Date</th>
        <th>Action</th>
      </tr>
      </thead>
      <tbody>
      <%
        List<Activity> activities = (List<Activity>) request.getAttribute("activities");
        if (activities != null && !activities.isEmpty()) {
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          for (Activity activity : activities) {
      %>
      <tr>
        <td><%= activity.getId() %></td>
        <td><%= activity.getUserId() %></td>
        <td><%= activity.getFirstName() %></td>
        <td><%= activity.getLastName() %></td>
        <td><%= activity.getDescription() %></td>
        <td><%= sdf.format(activity.getActivityDate()) %></td>
        <td>
          <form action="activity" method="post" style="display:inline;">
            <input type="hidden" name="action" value="delete">
            <input type="hidden" name="id" value="<%= activity.getId() %>">
            <button type="submit">Delete</button>
          </form>
        </td>
      </tr>
      <%
        }
      } else {
      %>
      <tr>
        <td colspan="7" class="no-activities">No activities found</td>
      </tr>
      <% } %>
      </tbody>
    </table>
  </div>
</div>
</body>
</html>
