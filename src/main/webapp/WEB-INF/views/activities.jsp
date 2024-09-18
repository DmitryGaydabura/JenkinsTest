<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.example.jenkinsspring.model.Activity" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Activity Management</title>
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/styles.css">
</head>
<body>
<h1>Activity Management System</h1>

<!-- Сообщения -->
<c:if test="${not empty message}">
  <div class="alert success">${message}</div>
</c:if>
<c:if test="${not empty errorMessage}">
  <div class="alert error">${errorMessage}</div>
</c:if>

<!-- Форма добавления активности -->
<h2>Add Activity</h2>
<form action="activity" method="post">
  <input type="hidden" name="action" value="add">
  <label for="userId">User ID:</label>
  <input type="text" id="userId" name="userId" required>
  <label for="description">Description:</label>
  <textarea id="description" name="description" rows="4" required></textarea>
  <button type="submit">Add Activity</button>
</form>

<!-- Кнопка отправки отчета -->
<form action="activity" method="post">
  <input type="hidden" name="action" value="sendReport">
  <button type="submit">Send Report to Email</button>
</form>

<!-- Список активностей -->
<h2>Activities List</h2>
<table>
  <thead>
  <tr>
    <th>ID</th>
    <th>User ID</th>
    <th>Name</th>
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
    <td><%= activity.getFirstName() %> <%= activity.getLastName() %></td>
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
    <td colspan="6">No activities found.</td>
  </tr>
  <% } %>
  </tbody>
</table>
</body>
</html>
