<%@ page import="com.example.jenkinsspring.servlet.Activity" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Activity Management</title>
  <style>
    /* Добавьте здесь ваши стили, можете скопировать их из users.jsp */
  </style>
</head>
<body>
<header>
  <h1>Activity Management System</h1>
</header>
<div class="container">
  <!-- Форма для добавления активности -->
  <div class="form-container">
    <h2>Add Activity</h2>
    <form action="activity" method="post">
      <input type="hidden" name="action" value="add">
      <label for="userId">User ID:</label>
      <input type="text" id="userId" name="userId" required>
      <label for="description">Description:</label>
      <textarea id="description" name="description" required></textarea>
      <button type="submit">Add Activity</button>
    </form>
  </div>

  <!-- Список активностей -->
  <div class="form-container">
    <h2>Activities List</h2>
    <table>
      <thead>
      <tr>
        <th>ID</th>
        <th>User ID</th>
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
        <td colspan="5" class="no-activities">No activities found</td>
      </tr>
      <% } %>
      </tbody>
    </table>
  </div>
</div>
</body>
</html>
