package com.roomreservation.controller;

import com.roomreservation.util.DBConnection;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BillServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");

        String reservationNo = req.getParameter("reservationNo");
        if (reservationNo == null || reservationNo.isBlank()) {
            resp.sendRedirect("bill.html");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            // 1) Get reservation
            String rSql = "SELECT reservation_no, guest_name, contact, room_type, check_in, check_out " +
                    "FROM reservations WHERE reservation_no=?";
            PreparedStatement rPst = con.prepareStatement(rSql);
            rPst.setString(1, reservationNo);
            ResultSet rRs = rPst.executeQuery();

            if (!rRs.next()) {
                resp.getWriter().println("<h2>❌ Reservation not found: " + escape(reservationNo) + "</h2>");
                resp.getWriter().println("<p><a href='bill.html'>Back</a></p>");
                return;
            }

            String guest = rRs.getString("guest_name");
            String contact = rRs.getString("contact");
            String roomType = rRs.getString("room_type");
            LocalDate checkIn = LocalDate.parse(rRs.getString("check_in"));
            LocalDate checkOut = LocalDate.parse(rRs.getString("check_out"));

            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
            if (nights <= 0) nights = 1;

            // 2) Get rate
            String rateSql = "SELECT rate_per_night FROM room_rates WHERE room_type=?";
            PreparedStatement ratePst = con.prepareStatement(rateSql);
            ratePst.setString(1, roomType);
            ResultSet rateRs = ratePst.executeQuery();

            double rate = 0;
            if (rateRs.next()) rate = rateRs.getDouble("rate_per_night");
            double total = rate * nights;

            // 3) Build HTML using StringBuilder (NO text blocks)
            StringBuilder html = new StringBuilder();
            html.append("<!doctype html>")
                    .append("<html><head><meta charset='UTF-8'><title>Bill</title>")
                    .append("<style>")
                    .append("body{font-family:Arial;margin:40px;}")
                    .append(".box{border:1px solid #ddd;border-radius:10px;padding:18px;max-width:650px;}")
                    .append("table{border-collapse:collapse;width:100%;}")
                    .append("td,th{border:1px solid #ddd;padding:10px;}")
                    .append("th{background:#f5f5f5;text-align:left;}")
                    .append("button{margin-top:12px;padding:10px 14px;cursor:pointer;}")
                    .append("</style></head><body>");

            html.append("<div class='box'>")
                    .append("<h2>Bill - Reservation ").append(escape(reservationNo)).append("</h2>")
                    .append("<table>")
                    .append("<tr><th>Guest</th><td>").append(escape(guest)).append("</td></tr>")
                    .append("<tr><th>Contact</th><td>").append(escape(contact)).append("</td></tr>")
                    .append("<tr><th>Room Type</th><td>").append(escape(roomType)).append("</td></tr>")
                    .append("<tr><th>Check-in</th><td>").append(checkIn).append("</td></tr>")
                    .append("<tr><th>Check-out</th><td>").append(checkOut).append("</td></tr>")
                    .append("<tr><th>Nights</th><td>").append(nights).append("</td></tr>")
                    .append("<tr><th>Rate per Night</th><td>").append(String.format("%.2f", rate)).append("</td></tr>")
                    .append("<tr><th>Total</th><td><b>").append(String.format("%.2f", total)).append("</b></td></tr>")
                    .append("</table>")
                    .append("<button onclick='window.print()'>Print</button>")
                    .append("<p><a href='bill.html'>Back</a> | <a href='dashboard.html'>Dashboard</a></p>")
                    .append("</div>");

            html.append("</body></html>");

            resp.getWriter().println(html.toString());

        } catch (Exception e) {
            resp.getWriter().println("<h2>❌ Bill Error</h2>");
            resp.getWriter().println("<pre>" + e + "</pre>");
        }
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