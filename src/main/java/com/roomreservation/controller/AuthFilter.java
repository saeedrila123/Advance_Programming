package com.roomreservation.controller;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String ctx = req.getContextPath();    // ex: /room_reservation_system_war_exploded
        String uri = req.getRequestURI();     // ex: /room_reservation_system_war_exploded/dashboard.html

        // Remove context path to get clean path
        String path = (ctx != null && !ctx.isEmpty()) ? uri.substring(ctx.length()) : uri;

        // ✅ Allow public pages + login/logout endpoints
        if (path.equals("/") ||
                path.equals("/index.html") ||
                path.equals("/login.html") ||
                path.equals("/login") ||
                path.equals("/logout") ||
                path.equals("/hello") ||
                path.equals("/db-test")) {

            chain.doFilter(request, response);
            return;
        }

        // ✅ Allow browser default request files
        if (path.equals("/favicon.ico") ||
                path.equals("/robots.txt")) {
            chain.doFilter(request, response);
            return;
        }

        // ✅ Allow static resources (if you add later)
        if (path.startsWith("/assets/") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/") ||
                path.endsWith(".css") ||
                path.endsWith(".js") ||
                path.endsWith(".png") ||
                path.endsWith(".jpg") ||
                path.endsWith(".jpeg") ||
                path.endsWith(".gif") ||
                path.endsWith(".ico")) {

            chain.doFilter(request, response);
            return;
        }

        // ✅ Check session for protected pages
        HttpSession session = req.getSession(false);
        boolean loggedIn = (session != null && session.getAttribute("loggedInUser") != null);

        if (!loggedIn) {
            resp.sendRedirect(ctx + "/login.html");
            return;
        }


        // ✅ Logged in -> allow request
        chain.doFilter(request, response);
    }
}