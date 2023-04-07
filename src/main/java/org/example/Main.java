package org.example;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
public class Main {
  final static String simpleDateFormat = "yyyy-MM-dd HH:mm.ss";

  public static void main(String[] args) {
    final String url = "jdbc:postgresql://localhost:5432/smart_thermostats";
    final String user = "postgres";
    final String password = "postgres";

    try (Connection conn = DriverManager.getConnection(url, user, password)) {
      while (true) {
        // Retrieve all records from the temperature table
        String sql = "SELECT * FROM thermostats";
        try (Statement stmt = conn.createStatement()) {
          try (ResultSet rs = stmt.executeQuery(sql)) {
            // Loop through each record and update the temperature field with a random value
            while (rs.next()) {
              Long id = rs.getLong("id");
              double randomTemperature = generateRandomTemperature();
              updateTemperature(conn, id, randomTemperature);
            }
          }
        } catch (SQLException e) {
          e.printStackTrace();
        }

        Thread.sleep(5000); // Wait for 5 seconds before executing the loop again
      }
    } catch (SQLException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static double generateRandomTemperature() {
    // Generate a random temperature value between 10 and 40
    Random rand = new Random();
    return 10 + rand.nextDouble() * 30;
  }

  private static void updateTemperature(Connection conn, Long id, double randomTemperature) throws SQLException {
    // Update the current temperature field in the specified record
    String sql = "UPDATE thermostats SET current_temperature = ? WHERE id = ?";
    try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
      preparedStatement.setDouble(1, randomTemperature);
      preparedStatement.setLong(2, id);
      preparedStatement.executeUpdate();
      System.out.println("UPDATED at " +  new SimpleDateFormat(simpleDateFormat).format(new Date()) +
          " ID: " + id + ", New temperature value: " + randomTemperature);
    }
  }
}
