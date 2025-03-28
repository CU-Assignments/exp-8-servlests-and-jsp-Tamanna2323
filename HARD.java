//Create MySQL Database & Table

CREATE DATABASE IF NOT EXISTS student_portal;
USE student_portal;

CREATE TABLE IF NOT EXISTS attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_name VARCHAR(100) NOT NULL,
    roll_number VARCHAR(20) NOT NULL,
    date DATE NOT NULL,
    status ENUM('Present', 'Absent') NOT NULL
);


// attendance.jsp – Form to Enter Attendance

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Student Attendance Portal</title>
</head>
<body>
    <h2>Enter Student Attendance</h2>
    <form action="AttendanceServlet" method="post">
        Student Name: <input type="text" name="student_name" required><br><br>
        Roll Number: <input type="text" name="roll_number" required><br><br>
        Date: <input type="date" name="date" required><br><br>
        Status: 
        <select name="status">
            <option value="Present">Present</option>
            <option value="Absent">Absent</option>
        </select><br><br>
        <input type="submit" value="Submit Attendance">
    </form>
</body>
</html>

 // AttendanceServlet.java – Servlet to Save Data
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AttendanceServlet")
public class AttendanceServlet extends HttpServlet {
    private static final String URL = "jdbc:mysql://localhost:3306/student_portal";
    private static final String USER = "root";  // Replace with your MySQL username
    private static final String PASSWORD = "password";  // Replace with your MySQL password

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String studentName = request.getParameter("student_name");
        String rollNumber = request.getParameter("roll_number");
        String date = request.getParameter("date");
        String status = request.getParameter("status");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "INSERT INTO attendance (student_name, roll_number, date, status) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, studentName);
            stmt.setString(2, rollNumber);
            stmt.setString(3, date);
            stmt.setString(4, status);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                out.println("<h3>Attendance recorded successfully!</h3>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<h3>Error recording attendance.</h3>");
        }

        out.println("<br><a href='attendance.jsp'>Back to Form</a>");
    }
}

// viewAttendance.jsp – Display Attendance Records
<%@ page import="java.sql.*" %>
<!DOCTYPE html>
<html>
<head>
    <title>Attendance Records</title>
</head>
<body>
    <h2>Student Attendance Records</h2>
    <table border="1">
        <tr>
            <th>ID</th>
            <th>Student Name</th>
            <th>Roll Number</th>
            <th>Date</th>
            <th>Status</th>
        </tr>
        <%
            String url = "jdbc:mysql://localhost:3306/student_portal";
            String user = "root"; // Replace with your MySQL username
            String password = "password"; // Replace with your MySQL password

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM attendance");

                while (rs.next()) {
        %>
        <tr>
            <td><%= rs.getInt("id") %></td>
            <td><%= rs.getString("student_name") %></td>
            <td><%= rs.getString("roll_number") %></td>
            <td><%= rs.getDate("date") %></td>
            <td><%= rs.getString("status") %></td>
        </tr>
        <%
                }
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        %>
    </table>
</body>
</html>

// web.xml – (Optional) Servlet Configuration
<web-app xmlns="http://java.sun.com/xml/ns/javaee" version="3.0">
    <servlet>
        <servlet-name>AttendanceServlet</servlet-name>
        <servlet-class>AttendanceServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>AttendanceServlet</servlet-name>
        <url-pattern>/AttendanceServlet</url-pattern>
    </servlet-mapping>
</web-app>
