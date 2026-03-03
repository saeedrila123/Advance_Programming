package com.roomreservation.controller;

import com.roomreservation.util.DBConnection;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class UpdateReservationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String reservationNo = req.getParameter("reservationNo");
        String guestName = req.getParameter("guestName");
        String address = req.getParameter("address");
        String contact = req.getParameter("contact");
        String roomType = req.getParameter("roomType");
        String checkIn = req.getParameter("checkIn");
        String checkOut = req.getParameter("checkOut");

        if (reservationNo == null || reservationNo.isBlank()) {
            resp.sendRedirect("view-reservations");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            String sql = "UPDATE reservations SET guest_name=?, address=?, contact=?, room_type=?, check_in=?, check_out=? " +
                    "WHERE reservation_no=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, guestName);
            pst.setString(2, address);
            pst.setString(3, contact);
            pst.setString(4, roomType);
            pst.setString(5, checkIn);
            pst.setString(6, checkOut);
            pst.setString(7, reservationNo);

            pst.executeUpdate();

        } catch (Exception e) {
            resp.setContentType("text/plain");
            resp.getWriter().println("Update error: " + e);
            return;
        }

        resp.sendRedirect("reservation-details?reservationNo=" + reservationNo);
    }
}