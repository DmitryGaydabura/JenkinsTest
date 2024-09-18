<%@ page import="com.example.jenkinsspring.model.User" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management</title>
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
<header>
    <h1>User Management System</h1>
</header>
<div class="container">
    <!-- Add User -->
    <div class="form-container">
        <h2>Add User</h2>
        <form action="user" method="post">
            <input type="hidden" name="action" value="add">
            <label for="add-firstname">First Name:</label>
            <input type="text" id="add-firstname" name="firstName" required>
            <label for="add-lastname">Last Name:</label>
            <input type="text" id="add-lastname" name="lastName" required>
            <label for="add-age">Age:</label>
            <input type="number" id="add-age" name="age" required>
            <button type="submit">Add User</button>
        </form>
    </div>

    <!-- Update User -->
    <div class="form-container">
        <h2>Update User</h2>
        <form action="user" method="post">
            <input type="hidden" name="action" value="update">
            <label for="update-id">User ID:</label>
            <input type="text" id="update-id" name="id" required>
            <label for="update-firstname">New First Name:</label>
            <input type="text" id="update-firstname" name="firstName">
            <label for="update-lastname">New Last Name:</label>
            <input type="text" id="update-lastname" name="lastName">
            <label for="update-age">New Age:</label>
            <input type="number" id="update-age" name="age">
            <button type="submit">Update User</button>
        </form>
    </div>

    <!-- Delete User -->
    <div class="form-container">
        <h2>Delete User</h2>
        <form action="user" method="post">
            <input type="hidden" name="action" value="delete">
            <label for="delete-id">User ID:</label>
            <input type="text" id="delete-id" name="id" required>
            <button type="submit">Delete User</button>
        </form>
    </div>

    <!-- Display Users -->
    <div class="form-container">
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
            <% List<User> users = (List<User>) request.getAttribute("users");
                if (users != null && !users.isEmpty()) {
                    for (User user : users) { %>
            <tr>
                <td><%= user.getId() %></td>
                <td><%= user.getFirstName() %></td>
                <td><%= user.getLastName() %></td>
                <td><%= user.getAge() %></td>
            </tr>
            <% }
            } else { %>
            <tr>
                <td colspan="4" class="no-users">No users found</td>
            </tr>
            <% } %>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
