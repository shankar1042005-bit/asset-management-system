package com.virtusa.assetmanagement.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

/**
 * Session based auth filter - same "filter concept" as the original
 * AssetManagement.AuthFilter, just running as a Spring-managed bean
 * instead of a @WebFilter("/*"). Because it's a @Component implementing
 * Filter, Spring Boot auto-registers it for every request path.
 *
 * 1. A small list of PUBLIC paths pass through untouched (login,
 *    register, static assets).
 * 2. Everything else requires a session - no session -> bounced to
 *    login.html.
 * 3. ADMIN_ONLY / STUDENT_ONLY paths additionally check the role
 *    stored in the session.
 */
@Component
@Order(1)
public class AuthFilter implements Filter {

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/login.html",
            "/Login_servlet",
            "/register.html",
            "/RegisterServlet",
            "/index.html",
            "/"
    );

    private static final Set<String> ADMIN_ONLY = Set.of(
            "/admin_dashboard.html",
            "/asset_registration.html",
            "/asset_reg.jsp",
            "/asset_assignment.jsp",
            "/AssetRegistrationServlet",
            "/AssetDeleteServlet"
    );

    private static final Set<String> STUDENT_ONLY = Set.of(
            "/employee_dashboard.html"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getServletPath();

        if (isPublic(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("username") : null;

        if (username == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html?error=session");
            return;
        }

        String role = (String) session.getAttribute("role");

        if (ADMIN_ONLY.contains(path) && !"admin".equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/login.html?error=forbidden");
            return;
        }

        if (STUDENT_ONLY.contains(path) && !"student".equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/login.html?error=forbidden");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublic(String path) {

        if (path == null) {
            return true;
        }

        if (PUBLIC_PATHS.contains(path)) {
            return true;
        }

        if (path.startsWith("/images/") || path.startsWith("/css/") || path.startsWith("/js/")) {
            return true;
        }

        return path.equals("/favicon.ico");
    }
}
