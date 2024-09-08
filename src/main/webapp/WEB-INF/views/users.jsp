<%@ page import="com.example.jenkinsspring.servlet.User" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management</title>
    <style>
      table {
        width: 100%;
        border-collapse: collapse;
      }
      table, th, td {
        border: 1px solid black;
      }
      th, td {
        padding: 8px;
        text-align: left;
      }
    </style>
</head>
<body>
<h1>User Management</h1>

<!-- Add User -->
<h2>Add User</h2>
<form action="user" method="post">
    <input type="hidden" name="action" value="add">
    <label for="add-firstname">First Name:</label>
    <input type="text" id="add-firstname" name="firstName" required><br>
    <label for="add-lastname">Last Name:</label>
    <input type="text" id="add-lastname" name="lastName" required><br>
    <label for="add-age">Age:</label>
    <input type="number" id="add-age" name="age" required><br>
    <button type="submit">Add User</button>
</form>

<!-- Update User -->
<h2>Update User</h2>
<form action="user" method="post">
    <input type="hidden" name="action" value="update">
    <label for="update-id">User ID:</label>
    <input type="text" id="update-id" name="id" required><br>
    <label for="update-firstname">New First Name:</label>
    <input type="text" id="update-firstname" name="firstName"><br>
    <label for="update-lastname">New Last Name:</label>
    <input type="text" id="update-lastname" name="lastName"><br>
    <label for="update-age">New Age:</label>
    <input type="number" id="update-age" name="age"><br>
    <button type="submit">Update User</button>
</form>

<!-- Delete User -->
<h2>Delete User</h2>
<form action="user" method="post">
    <input type="hidden" name="action" value="delete">
    <label for="delete-id">User ID:</label>
    <input type="text" id="delete-id" name="id" required><br>
    <button type="submit">Delete User</button>
</form>

<!-- Display Users -->
<h2>Users List</h2>
<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>First Name</th>
        <th>Last Name</th>
        <th>Age</th>
    </tr>
    </thead>
    <tbody>
    <%
        List<User> users = (List<User>) request.getAttribute("users");
        if (users != null && !users.isEmpty()) {
            for (User user : users) {
    %>
    <tr>
        <td><%= user.getId() %></td>
        <td><%= user.getFirstName() %></td>
        <td><%= user.getLastName() %></td>
        <td><%= user.getAge() %></td>
    </tr>
    <%
        }
    } else {
    %>
    <tr>
        <td colspan="4">No users found</td>
    </tr>
    <%
        }
    %>
    </tbody>
</table>
</body>
</html>
