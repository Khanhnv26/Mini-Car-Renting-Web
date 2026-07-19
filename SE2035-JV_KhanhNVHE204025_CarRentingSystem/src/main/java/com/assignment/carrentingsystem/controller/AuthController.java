package com.assignment.carrentingsystem.controller;

import com.assignment.carrentingsystem.dto.RegisterForm;
import com.assignment.carrentingsystem.entity.Account;
import com.assignment.carrentingsystem.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    private void addDateBounds(Model model) {
        LocalDate today = LocalDate.now();
        model.addAttribute("today", today);
        model.addAttribute("maxBirthday", today.minusYears(18));
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("accountName") String accountName,
                        @RequestParam("password") String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        try {
            Account account = authService.login(accountName, password);
            session.setAttribute("currentUser", account);
            if ("Admin".equalsIgnoreCase(account.getRole())) {
                return "redirect:/admin/cars";
            }
            if ("Customer".equalsIgnoreCase(account.getRole())) {
                return "redirect:/customer/cars";
            }
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", "");
            return "redirect:/login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("toastMessage", "Đã đăng xuất!");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        addDateBounds(model);
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm registerForm,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addDateBounds(model);
            return "auth/register";
        }

        try {
            authService.register(registerForm);
            redirectAttributes.addFlashAttribute("toastMessage", "Đăng ký tài khoản thành công!");
            redirectAttributes.addFlashAttribute("toastType", "success");
            return "redirect:/login?registered";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            addDateBounds(model);
            return "auth/register";
        }
    }
}
