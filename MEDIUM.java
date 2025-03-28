CREATE DATABASE IF NOT EXISTS companyDB;
USE companyDB;

CREATE TABLE IF NOT EXISTS employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(50) NOT NULL,
    salary DECIMAL(10,2) NOT NULL
);

INSERT INTO employees (name, department, salary) VALUES
('John Doe', 'HR', 50000),
('Jane Smith', 'IT', 70000),
('Alice Johnson', 'Finance', 65000),
('Bob Brown', 'Marketing', 60000);



import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/employees")
public class EmployeeServlet extends HttpServlet {
    private static final String URL = "jdbc:mysql://localhost:3306/companyDB";
    private static final String USER = "root";  // Replace with your MySQL username
    private static final String PASSWORD = "password";  // Replace with your MySQL password

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>Employee List</title></head><body>");
        out.println("<h2>Employee List</h2>");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM employees")) {

            out.println("<table border='1'><tr><th>ID</th><th>Name</th><th>Department</th><th>Salary</th></tr>");
            while (rs.next()) {
                out.println("<tr><td>" + rs.getInt("id") + "</td><td>" + rs.getString("name") +
                        "</td><td>" + rs.getString("department") + "</td><td>" + rs.getDouble("salary") + "</td></tr>");
            }
            out.println("</table>");

        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<p>Error retrieving employee data.</p>");
        }

     
        out.println("<h2>Search Employee by ID</h2>");
        out.println("<form action='employees' method='post'>");
        out.println("Employee ID: <input type='text' name='empId'/>");
        out.println("<input type='submit' value='Search'/>");
        out.println("</form>");

        out.println("</body></html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String empId = request.getParameter("empId");

        out.println("<html><head><title>Employee Details</title></head><body>");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM employees WHERE id = ?")) {

            stmt.setInt(1, Integer.parseInt(empId));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                out.println("<h2>Employee Details</h2>");
                out.println("<p>ID: " + rs.getInt("id") + "</p>");
                out.println("<p>Name: " + rs.getString("name") + "</p>");
                out.println("<p>Department: " + rs.getString("department") + "</p>");
                out.println("<p>Salary: $" + rs.getDouble("salary") + "</p>");
            } else {
                out.println("<p>No employee found with ID " + empId + ".</p>");
            }

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            out.println("<p>Error retrieving employee details. Please check the input.</p>");
        }

        out.println("<br><a href='employees'>Back to Employee List</a>");
        out.println("</body></html>");
    }
}
