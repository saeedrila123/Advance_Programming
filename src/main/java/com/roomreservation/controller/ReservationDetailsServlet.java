package com.roomreservation.controller;

import com.roomreservation.util.DBConnection;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReservationDetailsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");

        String reservationNo = req.getParameter("reservationNo");
        if (reservationNo == null || reservationNo.isBlank()) {
            resp.sendRedirect("search-reservation.html");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            String sql = "SELECT reservation_no, guest_name, address, contact, room_type, check_in, check_out " +
                    "FROM reservations WHERE reservation_no=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, reservationNo);
            ResultSet rs = pst.executeQuery();

            if (!rs.next()) {
                resp.getWriter().println("<h2>❌ Reservation Not Found: " + reservationNo + "</h2>");
                resp.getWriter().println("<p><a href='search-reservation.html'>Back</a></p>");
                return;
            }

            String guest = rs.getString("guest_name");
            String address = rs.getString("address");
            String contact = rs.getString("contact");
            String roomType = rs.getString("room_type");
            String checkIn = rs.getString("check_in");
            String checkOut = rs.getString("check_out");

            StringBuilder html = new StringBuilder();
            html.append("<!doctype html><html><head><meta charset='UTF-8'><title>Reservation Details</title>")
                    .append("<style>")
                    .append("body{font-family:Arial;margin:40px;}")
                    .append(".box{border:1px solid #ddd;border-radius:10px;padding:18px;max-width:650px;}")
                    .append("table{border-collapse:collapse;width:100%;}")
                    .append("td,th{border:1px solid #ddd;padding:10px;}")
                    .append("th{background:#f5f5f5;text-align:left;width:30%;}")
                    .append("</style></head><body>");

            html.append("<div class='box'>")
                    .append("<h2>Reservation Details - ").append(reservationNo).append("</h2>")
                    .append("<table>")
                    .append("<tr><th>Reservation No</th><td>").append(reservationNo).append("</td></tr>")
                    .append("<tr><th>Guest Name</th><td>").append(guest).append("</td></tr>")
                    .append("<tr><th>Address</th><td>").append(address).append("</td></tr>")
                    .append("<tr><th>Contact</th><td>").append(contact).append("</td></tr>")
                    .append("<tr><th>Room Type</th><td>").append(roomType).append("</td></tr>")
                    .append("<tr><th>Check-in</th><td>").append(checkIn).append("</td></tr>")
                    .append("<tr><th>Check-out</th><td>").append(checkOut).append("</td></tr>")
                    .append("</table>")
                    .append("<p style='margin-top:12px;'>")
                    .append("<a href='bill?reservationNo=").append(reservationNo).append("'>Generate Bill</a>")
                    .append(" | <a href='view-reservations'>View All</a>")
                    .append(" | <a href='dashboard.html'>Dashboard</a>")
                    .append("</p>")
                    .append("</div>");

            html.append("</body></html>");

            resp.getWriter().println(html.toString());

        } catch (Exception e) {
            resp.getWriter().println("<h2>❌ Error</h2>");
            resp.getWriter().println("<pre>" + e + "</pre>");
        }
    }
}