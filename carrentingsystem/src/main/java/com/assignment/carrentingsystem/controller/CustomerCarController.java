package com.assignment.carrentingsystem.controller;

import com.assignment.carrentingsystem.dto.CarRentalDTO;
import com.assignment.carrentingsystem.dto.CustomerDTO;
import com.assignment.carrentingsystem.dto.ReviewDTO;
import com.assignment.carrentingsystem.entity.Account;
import com.assignment.carrentingsystem.entity.Customer;
import com.assignment.carrentingsystem.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerCarController {

    private final CarService carService;
    private final CarRentalService carRentalService;
    private final CustomerService customerService;
    private final AccountService accountService;
    private final ReviewService reviewService;

    private Customer getCurrentCustomer(Authentication authentication) {
        String email = authentication.getName();
        Account account = accountService.findByEmail(email);
        return customerService.findByAccountId(account.getAccountId());
    }

    @GetMapping("/cars")
    public String listCars(Model model) {
        model.addAttribute("cars", carService.findByStatus("Available"));
        return "customer/car-available";
    }

    @GetMapping("/rent")
    public String rentForm(Model model) {
        model.addAttribute("carRentalDTO", new CarRentalDTO());
        model.addAttribute("cars", carService.findByStatus("Available"));
        return "customer/rent-form";
    }

    @PostMapping("/rent")
    public String rent(@Valid @ModelAttribute("carRentalDTO") CarRentalDTO carRentalDTO, BindingResult bindingResult, Model model, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("cars", carService.findByStatus("Available"));
            return "customer/rent-form";
        }

        try {
            Customer customer = getCurrentCustomer(authentication);
            carRentalService.createCarRental(customer.getCustomerId(), carRentalDTO);
            return "redirect:/customer/history";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("cars", carService.findByStatus("Available"));
            return "customer/rent-form";
        }
    }

    @GetMapping("/history")
    public String history(Model model, Authentication authentication) {
        Customer customer = getCurrentCustomer(authentication);
        model.addAttribute("rentals", carRentalService.findByCustomerId(customer.getCustomerId()));
        model.addAttribute("reviewDTO", new ReviewDTO());
        return "customer/history";
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        model.addAttribute("customer", customerService.findByEmail(authentication.getName()));
        return "customer/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model, Authentication authentication) {
        Customer customer = getCurrentCustomer(authentication);
        model.addAttribute("customer", customer);
        return "customer/profile-form";
    }


    @PostMapping("/profile/edit")
    public String saveProfile(@ModelAttribute("customer") Customer customer,
                              Authentication authentication) {
        if (customer.getFullName() == null || customer.getFullName().isBlank()) {
            return "redirect:/customer/profile/edit?error=Tên không được để trống";
        }
        customerService.updateProfile(authentication.getName(), customer);
        return "redirect:/customer/profile";
    }

    @PostMapping("/review")
    public String review(@Valid @ModelAttribute("reviewDTO") ReviewDTO reviewDTO, BindingResult bindingResult, Model model, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            Customer customer = getCurrentCustomer(authentication);
            model.addAttribute("rentals", carRentalService.findByCustomerId(customer.getCustomerId()));
            return "customer/history";
        }

        try {
            reviewService.save(reviewDTO);
            return "redirect:/customer/history";
        } catch (Exception e) {
            Customer customer = getCurrentCustomer(authentication);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("rentals", carRentalService.findByCustomerId(customer.getCustomerId()));
            return "customer/history";

        }
    }




}
