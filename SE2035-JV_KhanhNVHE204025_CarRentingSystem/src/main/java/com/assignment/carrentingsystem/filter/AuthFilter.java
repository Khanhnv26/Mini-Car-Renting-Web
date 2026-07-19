package com.assignment.carrentingsystem.filter;

import com.assignment.carrentingsystem.entity.Account;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
@Order(1)
public class AuthFilter extends OncePerRequestFilter {

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/", "/cars", "/reviews", "/login", "/register", "/logout", "/access-denied"
    );

    private static final String[] PUBLIC_PREFIXES = {
            "/css/", "/js/", "/images/", "/webjars/", "/error"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String path = resolvePath(request);

        if (isPublic(path)) {
            chain.doFilter(request, response);
            return;
        }

        Account user = currentUser(request);

        if (path.startsWith("/admin/")) {
            requireRole(request, response, chain, user, "Admin");
            return;
        }
        if (path.startsWith("/customer/")) {
            requireRole(request, response, chain, user, "Customer");
            return;
        }
        if (user == null) {
            redirect(request, response, "/login");
            return;
        }
        chain.doFilter(request, response);
    }

    private void requireRole(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                             Account user, String role) throws IOException, ServletException {
        if (user == null) {
            redirect(request, response, "/login");
            return;
        }
        if (!role.equalsIgnoreCase(user.getRole())) {
            redirect(request, response, "/access-denied");
            return;
        }
        chain.doFilter(request, response);
    }

    private Account currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object attr = session.getAttribute("currentUser");
        return attr instanceof Account account ? account : null;
    }

    private String resolvePath(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return path.isEmpty() ? "/" : path;
    }

    private boolean isPublic(String path) {
        if (PUBLIC_PATHS.contains(path)) {
            return true;
        }
        for (String prefix : PUBLIC_PREFIXES) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private void redirect(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
        response.sendRedirect(request.getContextPath() + path);
    }
}
