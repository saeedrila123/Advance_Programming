package com.roomreservation.controller;

import com.roomreservation.util.DBConnection;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String ctx = req.getContextPath();
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            resp.sendRedirect(ctx + "/login.html?error=1");
            return;
        }

        boolean valid = false;

        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT id FROM users WHERE username=? AND password=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            valid = rs.next();
        } catch (Exception e) {
            resp.setContentType("text/plain");
            resp.getWriter().println("DB error: " + e);
            return;
        }

        if (valid) {
            HttpSession session = req.getSession(true);
            session.setAttribute("loggedInUser", username);

            // ✅ IMPORTANT: redirect using context path
            resp.sendRedirect(ctx + "/dashboard.html");
        } else {
            resp.sendRedirect(ctx + "/login.html?error=1");
        }
    }
}