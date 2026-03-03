package com.roomreservation.controller;

import com.roomreservation.util.DBConnection;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewReservationsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setContentType("text/html; charset=UTF-8");

        StringBuilder html = new StringBuilder();

        // --- HTML Header ---
        html.append("<!doctype html>")
                .append("<html><head><meta charset='UTF-8'>")
                .append("<title>View Reservations</title>")
                .append("<style>")
                .append("body{font-family:Arial,sans-serif;margin:40px;}")
                .append("table{border-collapse:collapse;width:100%;}")
                .append("th,td{border:1px solid #ddd;padding:10px;text-align:left;}")
                .append("th{background:#f5f5f5;}")
                .append("a.btn{display:inline-block;margin:10px 10px 0 0;padding:8px 10px;border:1px solid #222;border-radius:8px;text-decoration:none;color:#222;}")
                .append("a.btn:hover{background:#f3f3f3;}")
                .append("</style>")
                .append("</head><body>");

        html.append("<h2>Reservations</h2>");

        // Buttons
        html.append("<a class='btn' href='add-reservation.html'>+ Add New Reservation</a>");
        html.append("<a class='btn' href='search-reservation.html'>Search Reservation</a>");
        html.append("<a class='btn' href='dashboard.html'>Back to Dashboard</a>");

        // Table
        html.append("<table style='margin-top:14px;'>")
                .append("<tr>")
                .append("<th>Res No</th>")
                .append("<th>Guest</th>")
                .append("<th>Contact</th>")
                .append("<th>Room</th>")
                .append("<th>Check-in</th>")
                .append("<th>Check-out</th>")
                .append("<th>Details</th>")
                .append("<th>Edit</th>")
                .append("<th>Delete</th>")
                .append("</tr>");

        try (Connection con = DBConnection.getConnection()) {

            String sql = "SELECT reservation_no, guest_name, address, contact, room_type, check_in, check_out " +
                    "FROM reservations ORDER BY created_at DESC";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            boolean hasRows = false;

            while (rs.next()) {
                hasRows = true;

                String resNo = rs.getString("reservation_no");
                String guest = rs.getString("guest_name");
                String address = rs.getString("address");
                String contact = rs.getString("contact");
                String roomType = rs.getString("room_type");
                String checkIn = rs.getString("check_in");
                String checkOut = rs.getString("check_out");

                // Encode values for URL (important when names have spaces)
                String editLink = "edit-reservation.html"
                        + "?reservationNo=" + enc(resNo)
                        + "&guestName=" + enc(guest)
                        + "&address=" + enc(address)
                        + "&contact=" + enc(contact)
                        + "&roomType=" + enc(roomType)
                        + "&checkIn=" + enc(checkIn)
                        + "&checkOut=" + enc(checkOut);

                String detailsLink = "reservation-details?reservationNo=" + enc(resNo);
                String deleteLink = "delete-reservation?reservationNo=" + enc(resNo);

                html.append("<tr>")
                        .append("<td>").append(escape(resNo)).append("</td>")
                        .append("<td>").append(escape(guest)).append("</td>")
                        .append("<td>").append(escape(contact)).append("</td>")
                        .append("<td>").append(escape(roomType)).append("</td>")
                        .append("<td>").append(escape(checkIn)).append("</td>")
                        .append("<td>").append(escape(checkOut)).append("</td>")
                        .append("<td><a href='").append(detailsLink).append("'>View</a></td>")
                        .append("<td><a href='").append(editLink).append("'>Edit</a></td>")
                        .append("<td><a href='").append(deleteLink)
                        .append("' onclick=\"return confirm('Delete this reservation?');\">Delete</a></td>")
                        .append("</tr>");
            }

            if (!hasRows) {
                html.append("<tr><td colspan='9'>No reservations found.</td></tr>");
            }

        } catch (Exception e) {
            html.append("<tr><td colspan='9'>DB Error: ").append(escape(String.valueOf(e))).append("</td></tr>");
        }

        html.append("</table>");
        html.append("</body></html>");

        resp.getWriter().println(html.toString());
    }

    private static String enc(String s) {
        if (s == null) return "";
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}