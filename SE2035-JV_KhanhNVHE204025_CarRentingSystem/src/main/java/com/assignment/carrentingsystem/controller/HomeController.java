package com.assignment.carrentingsystem.controller;

import com.assignment.carrentingsystem.entity.Account;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session) {
        Account user = (Account) session.getAttribute("currentUser");
        if (user != null) {
            if ("Admin".equalsIgnoreCase(user.getRole())) {
                return "redirect:/admin/cars";
            }
            if ("Customer".equalsIgnoreCase(user.getRole())) {
                return "redirect:/customer/cars";
            }
        }
        return "home";
    }
}
