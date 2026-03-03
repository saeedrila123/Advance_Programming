package com.roomreservation.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/room_reservation_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "SrK19456@"; // keep for now (later we’ll move to config)

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        try {
            // Force load driver (extra-safe)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // This happens ONLY if the driver JAR is not on classpath
            throw new SQLException("MySQL Driver not found. Add mysql-connector-j to Tomcat/lib or WEB-INF/lib.", e);
        }

        return DriverManager.getConnection(URL, USER, PASS);
    }
}