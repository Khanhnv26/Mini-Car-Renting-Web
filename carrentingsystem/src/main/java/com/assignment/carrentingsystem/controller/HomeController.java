package com.assignment.carrentingsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (isLoggedIn(authentication)) {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_Admin"))) {
                return "redirect:/admin/cars";
            }
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_Customer"))) {
                return "redirect:/customer/cars";
            }
        }
        return "home";
    }

    private boolean isLoggedIn(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
