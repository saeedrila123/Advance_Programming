package com.roomreservation.controller;

import com.roomreservation.util.DBConnection;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class DeleteReservationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String reservationNo = req.getParameter("reservationNo");
        if (reservationNo == null || reservationNo.isBlank()) {
            resp.sendRedirect("view-reservations");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            String sql = "DELETE FROM reservations WHERE reservation_no=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, reservationNo);
            pst.executeUpdate();
        } catch (Exception e) {
            resp.setContentType("text/plain");
            resp.getWriter().println("Delete error: " + e);
            return;
        }

        resp.sendRedirect("view-reservations");
    }
}