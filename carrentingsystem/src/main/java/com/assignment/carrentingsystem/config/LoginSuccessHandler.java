package com.assignment.carrentingsystem.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        boolean isAdmin = hasRole(authentication, "ROLE_Admin");
        boolean isCustomer = hasRole(authentication, "ROLE_Customer");

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            if (isAllowedForRole(targetUrl, isAdmin, isCustomer)) {
                requestCache.removeRequest(request, response);
                response.sendRedirect(targetUrl);
                return;
            }
            requestCache.removeRequest(request, response);
        }

        if (isAdmin) {
            response.sendRedirect(request.getContextPath() + "/admin/cars");
            return;
        }
        if (isCustomer) {
            response.sendRedirect(request.getContextPath() + "/customer/cars");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/");
    }

    private boolean isAllowedForRole(String targetUrl, boolean isAdmin, boolean isCustomer) {
        if (targetUrl == null) {
            return false;
        }
        if (targetUrl.contains("/admin/") && !isAdmin) {
            return false;
        }
        if (targetUrl.contains("/customer/") && !isCustomer) {
            return false;
        }
        return true;
    }

    private boolean hasRole(Authentication authentication, String role) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (role.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
