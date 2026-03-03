package com.roomreservation.controller;

import com.roomreservation.util.DBConnection;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddReservationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String reservationNo = req.getParameter("reservationNo");
        String guestName = req.getParameter("guestName");
        String address = req.getParameter("address");
        String contact = req.getParameter("contact");
        String roomType = req.getParameter("roomType");
        String checkIn = req.getParameter("checkIn");
        String checkOut = req.getParameter("checkOut");

        // Basic validation
        if (reservationNo == null || reservationNo.isBlank()
                || guestName == null || guestName.isBlank()
                || address == null || address.isBlank()
                || contact == null || contact.isBlank()
                || roomType == null || roomType.isBlank()
                || checkIn == null || checkOut == null) {
            resp.sendRedirect("add-reservation.html");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            String sql = "INSERT INTO reservations " +
                    "(reservation_no, guest_name, address, contact, room_type, check_in, check_out) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, reservationNo);
            pst.setString(2, guestName);
            pst.setString(3, address);
            pst.setString(4, contact);
            pst.setString(5, roomType);
            pst.setString(6, checkIn);
            pst.setString(7, checkOut);

            pst.executeUpdate();

            // Redirect to view list after save
            resp.sendRedirect("view-reservations");

        } catch (Exception e) {
            resp.setContentType("text/plain");
            resp.getWriter().println("DB Error: " + e.getMessage());
        }
    }
}