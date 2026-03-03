package com.roomreservation.controller;

import com.roomreservation.util.DBConnection;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReportRevenueServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");

        String from = req.getParameter("from");
        String to = req.getParameter("to");

        if (from == null || to == null || from.isBlank() || to.isBlank()) {
            resp.sendRedirect("reports.html");
            return;
        }

        StringBuilder html = new StringBuilder();
        html.append("<!doctype html><html><head><meta charset='UTF-8'><title>Revenue Report</title>")
                .append("<style>")
                .append("body{font-family:Arial;margin:40px;}")
                .append("table{border-collapse:collapse;width:100%;}")
                .append("th,td{border:1px solid #ddd;padding:10px;text-align:left;}")
                .append("th{background:#f5f5f5;}")
                .append("</style></head><body>");

        html.append("<h2>Revenue Summary</h2>")
                .append("<p><b>From:</b> ").append(from).append(" <b>To:</b> ").append(to).append("</p>");

        double totalRevenue = 0;

        // Revenue = (nights * rate_per_night) summed for reservations in the date range
        String sql =
                "SELECT r.reservation_no, r.guest_name, r.room_type, r.check_in, r.check_out, rr.rate_per_night " +
                        "FROM reservations r " +
                        "JOIN room_rates rr ON r.room_type = rr.room_type " +
                        "WHERE r.check_in BETWEEN ? AND ? " +
                        "ORDER BY r.check_in ASC";

        html.append("<table>")
                .append("<tr><th>Res No</th><th>Guest</th><th>Room</th><th>Nights</th><th>Rate</th><th>Total</th></tr>");

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, from);
            pst.setString(2, to);
            ResultSet rs = pst.executeQuery();

            boolean hasRows = false;

            while (rs.next()) {
                hasRows = true;

                String resNo = rs.getString("reservation_no");
                String guest = rs.getString("guest_name");
                String room = rs.getString("room_type");
                String checkIn = rs.getString("check_in");
                String checkOut = rs.getString("check_out");
                double rate = rs.getDouble("rate_per_night");

                long nights = java.time.temporal.ChronoUnit.DAYS.between(
                        java.time.LocalDate.parse(checkIn),
                        java.time.LocalDate.parse(checkOut)
                );
                if (nights <= 0) nights = 1;

                double lineTotal = nights * rate;
                totalRevenue += lineTotal;

                html.append("<tr>")
                        .append("<td>").append(resNo).append("</td>")
                        .append("<td>").append(guest).append("</td>")
                        .append("<td>").append(room).append("</td>")
                        .append("<td>").append(nights).append("</td>")
                        .append("<td>").append(String.format("%.2f", rate)).append("</td>")
                        .append("<td>").append(String.format("%.2f", lineTotal)).append("</td>")
                        .append("</tr>");
            }

            if (!hasRows) {
                html.append("<tr><td colspan='6'>No records found in this range.</td></tr>");
            }

        } catch (Exception e) {
            html.append("<tr><td colspan='6'>DB Error: ").append(e).append("</td></tr>");
        }

        html.append("</table>");

        html.append("<h3>Total Revenue: ").append(String.format("%.2f", totalRevenue)).append("</h3>");
        html.append("<p><a href='reports.html'>Back</a> | <a href='dashboard.html'>Dashboard</a></p>");
        html.append("</body></html>");

        resp.getWriter().println(html.toString());
    }
}