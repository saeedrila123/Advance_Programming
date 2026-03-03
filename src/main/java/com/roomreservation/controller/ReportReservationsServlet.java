package com.roomreservation.controller;

import com.roomreservation.util.DBConnection;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReportReservationsServlet extends HttpServlet {

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
        html.append("<!doctype html><html><head><meta charset='UTF-8'><title>Reservations Report</title>")
                .append("<style>")
                .append("body{font-family:Arial;margin:40px;}")
                .append("table{border-collapse:collapse;width:100%;}")
                .append("th,td{border:1px solid #ddd;padding:10px;text-align:left;}")
                .append("th{background:#f5f5f5;}")
                .append("</style></head><body>");

        html.append("<h2>Reservations Report</h2>")
                .append("<p><b>From:</b> ").append(from).append(" <b>To:</b> ").append(to).append("</p>")
                .append("<table>")
                .append("<tr><th>Res No</th><th>Guest</th><th>Room</th><th>Check-in</th><th>Check-out</th></tr>");

        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT reservation_no, guest_name, room_type, check_in, check_out " +
                    "FROM reservations WHERE check_in BETWEEN ? AND ? ORDER BY check_in ASC";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, from);
            pst.setString(2, to);

            ResultSet rs = pst.executeQuery();

            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                html.append("<tr>")
                        .append("<td>").append(rs.getString("reservation_no")).append("</td>")
                        .append("<td>").append(rs.getString("guest_name")).append("</td>")
                        .append("<td>").append(rs.getString("room_type")).append("</td>")
                        .append("<td>").append(rs.getString("check_in")).append("</td>")
                        .append("<td>").append(rs.getString("check_out")).append("</td>")
                        .append("</tr>");
            }

            if (!hasRows) {
                html.append("<tr><td colspan='5'>No reservations found in this range.</td></tr>");
            }

        } catch (Exception e) {
            html.append("<tr><td colspan='5'>DB Error: ").append(e).append("</td></tr>");
        }

        html.append("</table>")
                .append("<p><a href='reports.html'>Back</a> | <a href='dashboard.html'>Dashboard</a></p>")
                .append("</body></html>");

        resp.getWriter().println(html.toString());
    }
}