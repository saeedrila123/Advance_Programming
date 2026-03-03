package com.roomreservation.controller;

import com.roomreservation.util.DBConnection;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;

public class TestDBServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");

        try (Connection con = DBConnection.getConnection()) {
            resp.getWriter().println("<h2>✅ Database Connected Successfully!</h2>");
            resp.getWriter().println("<p>Connection Class: " + con.getClass().getName() + "</p>");
        } catch (Exception e) {
            resp.getWriter().println("<h2>❌ Database Connection Failed</h2>");
            resp.getWriter().println("<pre>" + e + "</pre>");
        }
    }
}